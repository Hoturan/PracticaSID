package rio;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.core.AID;


import ontology.Rio;
import ontology.MessageManager;
 
public class AgenteRio extends Agent
{
    
    private boolean rioIniciado = false;
    private Rio rioBesos;
    private Logger myLogger = Logger.getMyLogger(getClass().getName());
    private MessageManager msgManager;
    private int numberTramos = 10;
    private final boolean debug = false;
    private class RioTickerBehaviour extends TickerBehaviour {
        String message;
        int count_chocula;

        public RioTickerBehaviour(Agent a, long period) {
            super(a, period);
        }

 
        public void onStart()
        {
            msgManager = new MessageManager();
            this.message = "Agent " + myAgent +" with RioTickerBehaviour in action!!" + count_chocula;
            count_chocula = 0;
            System.out.println("--------------------- INICIAMOS RIO ----------------------");
            Object[] args = getArguments();
            if (args != null && args.length == 1){
                numberTramos = Integer.valueOf(args[0].toString());       

            }
            else System.out.println("No tramo amount specified for Rio, assuming 10");
            
            rioBesos = new Rio(numberTramos); // rio de 10 tramos  POR PARAMETRO?
            rioIniciado = true;
        }
 
        public int onEnd()
        {
            System.out.println("I have done " + count_chocula + " iterations");
            return count_chocula;
        }
        
        public void onTick(){
            String waterToSea = rioBesos.avanzarCurso();
            System.out.println(msgManager.finalRio(waterToSea.split("\\s+")) + "\n");
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
                            String[] words = content.split("\\s+");
                            if (debug) System.out.println("AgenteRio has received the following message: " + content);
                            AID sender = msg.getSender();
                            if (debug) System.out.println("The message was sent by: " + sender.getLocalName());
                            if(sender.getLocalName().contains("AgenteIndustria")){
                                
                                if (debug) System.out.println("Rio is sending reply to AgenteIndustria");
                                //hola
                                if(rioIniciado){
                                    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                                    reply.addReceiver(sender);
                                    int tramoIndustria = msgManager.getTramo(words);
                                    //int indiceIndustria = msgManager.getIndice(words);
                                    int litrosExtraidos = rioBesos.extraerAgua(tramoIndustria, 1000000);
                                    String replyMsg = msgManager.extraerAguaReply(sender.getLocalName(), litrosExtraidos);
                                    reply.setContent(replyMsg);
                                    reply.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
                                    reply.setConversationId("others");
                                    send(reply);
                                }
                                else {
                                    System.out.println("EL RIO AUN NO ESTA INICIADO!");
                                    ACLMessage reply = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                                    reply.setContent("No se ha podido extraer agua del rio");
                                    reply.addReceiver(sender);
                                    reply.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST); /// no se si es necesario
                                    reply.setConversationId("others");
                                    send(reply);
                                }
                            }
                            else if(sender.getLocalName().equals("AgenteDepuradora")){
                                if (debug) System.out.println("Rio is sending reply to AgenteDepuradora");
                                if(rioIniciado){
                                    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                                    reply.addReceiver(sender);
                                    int tramoDepuradora = msgManager.getTramo(words);
                                    int litrosDescargados = msgManager.getLitros(words);
                                    rioBesos.descargarAgua(tramoDepuradora, litrosDescargados);
                                    String replyMsg = msgManager.descargarAguaReply(tramoDepuradora, litrosDescargados);
                                    reply.setContent(replyMsg);
                                    reply.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
                                    reply.setConversationId("others");
                                    send(reply);
                                }
                                else {
                                    System.out.println("EL RIO AUN NO ESTA INICIADO!");
                                    ACLMessage reply = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                                    reply.setContent("No se ha podido descargar agua al rio");
                                    reply.addReceiver(sender);
                                    reply.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST); /// no se si es necesario
                                    reply.setConversationId("others");
                                    send(reply);
                                }
                            }
                            break;
                        
                        case ACLMessage.INFORM:
                            String content2 = msg.getContent();
                            String[] words2 = content2.split("\\s+");
                            if (debug) System.out.println("AgenteRio has received the following message: " + content2);
                            AID sender2 = msg.getSender();
                            if (debug) System.out.println("The message was sent by: " + sender2.getLocalName());
                            if(sender2.getLocalName().contains("AgenteIndustria")){
                                int tramo = msgManager.getTramo(words2);
                                int litrosDescargados = msgManager.getLitros(words2);
                                int gradoContaminacion = msgManager.getGradoContaminacion(words2);
                                rioBesos.verterAgua(tramo,litrosDescargados,gradoContaminacion);
                            }
                            break;
                           
                        default:
                            System.out.println("MALFORMED MESSAGE");
                            break;
                    }
                }
                else block();
                
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