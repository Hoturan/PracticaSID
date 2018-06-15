package rio;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
 
 
public class AgenteRio extends Agent
{

    private Logger myLogger = Logger.getMyLogger(getClass().getName());

    private class RioTickerBehaviour extends TickerBehaviour    {
        String message;
        int count_chocula;

        public RioTickerBehaviour(Agent a, long period) {
            super(a, period);
        }

 
        public void onStart()
        {
            this.message = "Agent " + myAgent +" with RioTickerBehaviour in action!!" + count_chocula;
            count_chocula = 0;
        }
 
        public int onEnd()
        {
            System.out.println("I have done " + count_chocula + " iterations");
            return count_chocula;
        }
        
        public void onTick(){
            
            avanzaCursoAgua();
        }
        
        public void avanzaCursoAgua() {
            System.out.println("Agua avanza curso (No implementado)");
        
        }      
 
    }

    private class WaitMessageAndReplyBehaviour extends CyclicBehaviour {
 
        public WaitMessageAndReplyBehaviour(Agent a) {
            super(a);
        }
 
        public void action() {
            ACLMessage  msg = myAgent.receive();
            if(msg != null){
                ACLMessage reply = msg.createReply();
 
                if(msg.getPerformative()== ACLMessage.REQUEST){
                    String content = msg.getContent();
                    
                    if ((content != null) && (content.indexOf("ping") != -1)){
                        myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Received PING Request from "+msg.getSender().getLocalName());
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent("pong");
                    }
                    else{
                        myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Unexpected request ["+content+"] received from "+msg.getSender().getLocalName());
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent("( UnexpectedContent ("+content+"))");
                    }
 
                }
                else {
                    myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Unexpected message ["+ACLMessage.getPerformative(msg.getPerformative())+"] received from "+msg.getSender().getLocalName());
                    reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                    reply.setContent("( (Unexpected-act "+ACLMessage.getPerformative(msg.getPerformative())+") )");   
                }
                send(reply);
            }
            else {
                block();
            }
        }
    } 
    
    protected void setup()
    {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();   
        sd.setType("RioAgent"); 
        sd.setName(getName());
        sd.setOwnership("TILAB");
        dfd.setName(getAID());
        dfd.addServices(sd);

        try {
            DFService.register(this,dfd);
            WaitMessageAndReplyBehaviour RioMessageBehaviour = new  WaitMessageAndReplyBehaviour(this);
            this.addBehaviour(RioMessageBehaviour);
        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent "+getLocalName()+" - Cannot register with DF", e);
            doDelete();
        }
        
        RioTickerBehaviour b = new RioTickerBehaviour(this, 3000);
        this.addBehaviour(b);
    }
}