package rio;

import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
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
 
        @Override
        public void action() {
            MessageTemplate  mt=  AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST);  
            myAgent.addBehaviour( new  AchieveREResponder(myAgent,  mt)  {    
                @Override
                protected  ACLMessage  prepareResultNotification(ACLMessage  request,  ACLMessage  resp)  {   
                    System.out.println("Responder  has  received  the  following  message:" +request);    
                    ACLMessage  informDone  =  request.createReply();  
                    informDone.setPerformative(ACLMessage.INFORM);    
                    informDone.setContent("inform  done");
                    // River mass -10 or something like that
                    return  informDone;  
                }    
            });
        }
    } 
    
    protected void setup()
    {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();   
        sd.setType("AgenteRio"); 
        sd.setName(getName());
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
        
        RioTickerBehaviour b = new RioTickerBehaviour(this, 10000);
        this.addBehaviour(b);
        
        
    }
}