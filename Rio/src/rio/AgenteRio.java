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
import jade.core.AID;


import ontology.Rio;
 
 
public class AgenteRio extends Agent
{
    
    private boolean rioIniciado = false;
    private Rio rioBesos;
    private Logger myLogger = Logger.getMyLogger(getClass().getName());

    private class RioTickerBehaviour extends TickerBehaviour {
        String message;
        int count_chocula;

        public RioTickerBehaviour(Agent a, long period) {
            super(a, period);
        }

 
        public void onStart()
        {
            this.message = "Agent " + myAgent +" with RioTickerBehaviour in action!!" + count_chocula;
            count_chocula = 0;
            System.out.println("--------------------- INICIAMOS RIO ----------------------");
            rioBesos = new Rio(10); // rio de 10 tramos
            rioIniciado = true;
        }
 
        public int onEnd()
        {
            System.out.println("I have done " + count_chocula + " iterations");
            return count_chocula;
        }
        
        public void onTick(){
            rioBesos.avanzarCurso();
        }
    
    }

    private class WaitMessageAndReplyBehaviour extends SimpleBehaviour{

        private boolean finished = false;
        
        public WaitMessageAndReplyBehaviour(Agent a) {
            super(a);
        }
        
        @Override
        public void action() {
            try{ 
                ACLMessage msg = receive();
                if(msg != null){
                    switch(msg.getPerformative()){
                        case ACLMessage.REQUEST:
                            String content = msg.getContent();
                            System.out.println("AgenteRio has received the following message: " + content);
                            AID sender = msg.getSender();
                            System.out.println("The message was sent by: " + sender.getLocalName());
                            if(sender.getLocalName().equals("AgenteIndustria")){
                                System.out.println("Rio is sending reply to AgenteIndustria");
                                
                                if(rioIniciado){
                                    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                                    reply.addReceiver(sender);
                                    int msgLength = content.length();
                                    int posicionIndustria = Integer.parseInt(content.substring(msgLength-1));
                                    int litrosExtraidos = rioBesos.extraerAgua(posicionIndustria, 1);
                                    reply.setContent("Se han podido extraer (Millones de litros): " + String.valueOf(litrosExtraidos));
                                    reply.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
                                    send(reply);
                                }
                                else {
                                    System.out.println("EL RIO AUN NO ESTA INICIADO!");
                                    ACLMessage reply = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                                    reply.setContent("No se ha podido extraer agua del rio");
                                    reply.addReceiver(sender);
                                    reply.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST); /// no se si es necesario
                                    send(reply);
                                }
                            }
                            break;
                        default:
                            System.out.println("MALFORMED MESSAGE");
                            break;
                    }
                }
                
            }
            catch (Exception e){
                e.printStackTrace();
            }
           // block();
        }

        @Override
        public boolean done() {
            return finished;
        }
        
    }
    
    protected void setup()
    {   
        this.setQueueSize(5);
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();   
        sd.setType("AgenteRio"); 
        sd.setName(getName());
        dfd.setName(getAID());
        dfd.addServices(sd);
       

        try {
            DFService.register(this,dfd);
            RioTickerBehaviour b = new RioTickerBehaviour(this, 10000);
            this.addBehaviour(b);
            WaitMessageAndReplyBehaviour RioMessageBehaviour = new  WaitMessageAndReplyBehaviour(this);
            this.addBehaviour(RioMessageBehaviour);
        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent "+getLocalName()+" - Cannot register with DF", e);
            doDelete();
        }      
        
    }
}