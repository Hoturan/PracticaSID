/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rio;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import jade.util.Logger;
import java.util.ArrayList;
import ontology.Industria;
import ontology.MessageManager;
/**
 *
 * @author David
 */
public class AgenteIndustria extends Agent{
    private Logger myLogger = Logger.getMyLogger(getClass().getName());

    private boolean debug = true;
    //-gui AgenteRio:rio.AgenteRio;AgenteIndustria:rio.AgenteIndustria(500000,4,100000);AgenteDepuradora:rio.AgenteDepuradora
    //-gui AgenteRio:rio.AgenteRio;AgenteIndustria:rio.AgenteIndustria(1000000,4,250000);AgenteDepuradora:rio.AgenteDepuradora
    private int secondsPerTick = 5;
    private Industria industria;
    private MessageManager msgManager;
    private int identificador = 9156;
    
    String message="Have not found one of the two basic Agents";
    DFAgentDescription template = new DFAgentDescription();
    ServiceDescription templateSd = new ServiceDescription();
    
    private MessageTemplate mt;     
    
    protected String performs;
    protected int evaluation;
    
    private AID AIDrio;
    private AID AIDDepuradora;
    private ArrayList<AID> AIDsIndustrias = new ArrayList<AID>();
       
    private int litersPerProcess = 250000;
    private int posicion = 1;
    private int tankCapacity = 1000000;
    
    private class MessageRecieverBehaviour extends SimpleBehaviour{
        private boolean finish = false;
        @Override
            public void action() {
                mt = MessageTemplate.MatchConversationId("others");
                ACLMessage msg = myAgent.receive(mt);
                //System.out.println("Industria espera contestacion de rio");
                if(msg != null){
                    switch(msg.getPerformative()){
                        case ACLMessage.INFORM:
                            String content = msg.getContent();
                            String[] words = content.split("\\s+");
                            System.out.println(myAgent.getLocalName() + " has received the following message: " + content);
                            int litros = msgManager.getLitros(words);

                            switch(msg.getSender().getLocalName()){
                                case "AgenteRio":
                                    industria.addCleanWater(litros);
                                    System.out.println("Industria " + myAgent.getLocalName() + " tiene " + industria.getlWater() + " litros de agua");
                                    break;
                                case "AgenteDepuradora":
                                    int gradoContaminacion = industria.getGradoContaminacion();
                                    int litersLeft = industria.reduceFilthyWater(litros);
                                    if(litersLeft > 0){
                                        System.out.println("A la industria " + myAgent.getLocalName() + " aun le quedan " + litersLeft + " por expulsar, se ve obligada e verterlos al rio");
                                        String msgToRio = msgManager.verterAgua(myAgent.getLocalName(), industria.getPosition(), litersLeft, gradoContaminacion);
                                        ACLMessage msgToRiver = new ACLMessage(ACLMessage.INFORM);
                                        msgToRiver.addReceiver(AIDrio);
                                        msgToRiver.setContent(msgToRio);
                                        send(msgToRiver);
                                    }
                                    else{
                                        System.out.println("Industria " + myAgent.getLocalName() + " ha podido enviar a la depuradora el agua sucia");
                                    }  
                                    break;
                                default:
                                    System.out.println("Oops, algo ha ido mal!");
                                    break;
                            }

                                break;
                            case ACLMessage.REJECT_PROPOSAL:
                                System.out.println(myAgent.getLocalName() + " no puede realizar la accion deseada");
                                break;                            
                            default:
                                System.out.println("Oops, algo ha ido mal! - Mensaje malformado");
                                break;
                        }
                }
                    
                block();                  
            }     

        @Override
        public boolean done() {
            return finish;
        }

                
    }
    
    private class OfferRequestsServer extends CyclicBehaviour {
		public void action() { 
			MessageTemplate mt = MessageTemplate.MatchConversationId("cfp");   
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null && ACLMessage.CFP == msg.getPerformative() && industria != null) {
				// CFP Message received. Process it
                                System.out.println(myAgent.getAID().getLocalName() + " has recieved a petiton for waste");
				String title = msg.getContent();
				ACLMessage reply = new  ACLMessage(ACLMessage.PROPOSE);
                                reply.addReceiver(AIDDepuradora);

				int waste = industria.getlWaste();

                                // The requested book is available for sale. Reply with the price
                                reply.setContent(String.valueOf(waste) + " " + String.valueOf(industria.getGradoContaminacion()));
                                if (debug) System.out.println("Offering " + waste + "L");
                                reply.setConversationId("cfp");
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}  

	private class WasteOrdersServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchConversationId("cfp");
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null && ACLMessage.ACCEPT_PROPOSAL == msg.getPerformative()) {
                            // ACCEPT_PROPOSAL Message received. Process it
                            String title = msg.getContent();
                            ACLMessage reply = msg.createReply();

                            industria.setlWaste(0);
                            reply.setPerformative(ACLMessage.INFORM);
                            if (debug) System.out.println(title+" given to agent "+msg.getSender().getName());
                            reply.setConversationId("cfp");
                            myAgent.send(reply);
			}
			else {
                            block();
			}
		}
        }
        
    private class IndustriaTickerBehaviour extends TickerBehaviour {
        String message;
        int count_chocula;
        
        boolean pouringWater = false;
        
        public void onStart(){
            msgManager = new MessageManager();
            Object[] args = getArguments();
            if (args != null && (args.length == 3)){
                tankCapacity = Integer.valueOf(args[0].toString());       
                posicion = Integer.valueOf(args[1].toString());             
                litersPerProcess = Integer.valueOf(args[2].toString()); 
                System.out.println("Parametros de " + myAgent.getAID().getLocalName());
            }
            else System.out.println("No parameters specified for " + myAgent.getLocalName() + " assuming");
             
            System.out.println("    Capacidad del tanque ---> " + tankCapacity + "L");
            System.out.println("    Tramo de la Indunstria ---> " + posicion);
            System.out.println("    Litros usados por processo ---> " + litersPerProcess + "L");
            System.out.println("-------------------------------------------------------");
            
            industria = new Industria();
            industria.setTankCapacity(tankCapacity);
            industria.setLitersPerProcess(litersPerProcess);
            industria.setPosition(posicion);
            industria.setEarningsPerProcess(500);
            
            ///// INICIAMOS LA INDUSTRIA CON AGUA:  --- ESTO NO SE DEBE HACER ---
            ///// industria.addCleanWater(1000000);
            /////////////////////////////////////////////////////////////////////
        }
        
        public IndustriaTickerBehaviour(Agent a, long period) {
            super(a, period);
        }
 
        public int onEnd()
        {
            System.out.println("I have done " + count_chocula + " iterations");
            return count_chocula;
        }
        
        public void onTick(){      
            procesaAgua();
        }

        private void extractCleanWater(){
            System.out.println("Not enough clean water in Industria " + this.getAgent().getName());
            
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST); 
            request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            request.addReceiver(AIDrio);
            String content = msgManager.extraerAgua(myAgent.getLocalName(), industria.getPosition(), 1000000);
            request.setContent(content);
            request.setConversationId("others");
            send(request);      
       
        }        
              
        
        public void procesaAgua() {

            int litersPerProcess = industria.getLitersPerProcess();

            if (industria.getlWater() >= litersPerProcess && industria.getlWaste() <= (industria.getTankCapacity() - litersPerProcess)){
                industria.processWater();
                industria.generateEarnings();

                System.out.println("Industria " + myAgent.getAID().getLocalName() + " Process Done: ");
                System.out.println("    Clean water Tank at: " + industria.getlWater() + "L");
                System.out.println("    Waste Tank at: " + industria.getlWaste() + "L");
                System.out.println("    Earnings at: " + industria.getEarnings() + " euros\n");
                
            }
            else if (industria.getlWaste() > (industria.getTankCapacity() - litersPerProcess)){
                System.out.println("Stopping production, no more capacity for Waste");
            }
            else if (industria.getlWater() <= (industria.getTankCapacity() - 1000000)){
                extractCleanWater();
            }

            double wasteWaterLoad = (double) industria.getlWaste() / (double) industria.getTankCapacity();

            if (wasteWaterLoad > 0.75){
                System.out.println("Waste Tank at more than 75% capacity, proceding to search for Depuradora");                 
                ACLMessage  request  =  new  ACLMessage(ACLMessage.REQUEST); 
                request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
                String content = msgManager.enviaAgua(myAgent.getLocalName(), industria.getlWaste(), industria.getGradoContaminacion());
                request.setContent(content);
                request.addReceiver(AIDDepuradora);
                send(request);
            }
        }
    }
    
    public class SearchDepuradoraAndRioOneShotBehaviour extends OneShotBehaviour
        {
        public SearchDepuradoraAndRioOneShotBehaviour()
        {
             
        }
 
        @Override
        public void onStart()
        {
             
        }
 
        @Override
        public int onEnd()
        {
             
            return 1;
        }
 
        private AID [] searchDF( String service )
        {
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType( service );
            dfd.addServices(sd);

            SearchConstraints ALL = new SearchConstraints();
            ALL.setMaxResults(new Long(-1));

            try
            {
                DFAgentDescription[] result = DFService.search(myAgent, dfd, ALL);
                AID[] agents = new AID[result.length];
                for (int i=0; i<result.length; i++){
                    agents[i] = result[i].getName();
                }
                return agents;

            }
            catch (FIPAException fe) { fe.printStackTrace(); }
            
            return null;
        }
        
        @Override
        public void action()
        {

            boolean minADepuradora = false;
            boolean minARio = false;
            
            AID[] aux = searchDF("AgenteDepuradora");
            if (aux.length == 1){
                if (debug){
                    System.out.println(myAgent.getAID().getName() + " is adding Depuradora with AID: " + aux[0]);
                }
                minADepuradora = true;
                AIDDepuradora = aux[0];
            }
            else {
                if (aux == null){
                    myLogger.log(Logger.SEVERE, "No AgenteDepuradora Found! - Cannot continue");
                    doDelete();
                }
                else {
                    myLogger.log(Logger.SEVERE, "There should NOT be 2 AgenteDepuradora! - Taking First as valid");
                    if (debug){
                        System.out.println("Depuradora added with AID: " + aux[0]);
                    }
                    minADepuradora = true;
                    AIDDepuradora = aux[0];
                }
                  
            }
            
            aux = searchDF("AgenteRio");
            if (aux.length == 1){
                if (debug){
                    System.out.println(myAgent.getAID().getName() + " is adding Rio with AID: " + aux[0]);
                }
                minARio = true;
                AIDrio = aux[0];
            }
            else {
                if (aux == null){
                    myLogger.log(Logger.SEVERE, "No AgenteRio Found! - Cannot continue");
                    doDelete();
                }
                else {
                    myLogger.log(Logger.SEVERE, "There should NOT be 2 AgenteRio! - Taking First as valid");
                    if (debug){
                        System.out.println("Rio added with AID: " + aux[0]);
                    }
                    minARio = true;
                    AIDrio = aux[0];
                }
                  
            }
            
            if (!minADepuradora || !minARio) System.out.println(message);
        }
    }
    
   
    
    @Override
    protected void setup()
    {
        this.setQueueSize(5);
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();   
        sd.setType("Industria"); 
        sd.setName(this.getName());
        dfd.setName(this.getAID());
        dfd.addServices(sd);
                
        try {     
            SearchDepuradoraAndRioOneShotBehaviour sD = new SearchDepuradoraAndRioOneShotBehaviour();
            this.addBehaviour(sD);        
            
            MessageRecieverBehaviour mR = new MessageRecieverBehaviour();
            this.addBehaviour(mR); 
            
            OfferRequestsServer oR = new OfferRequestsServer();
            this.addBehaviour(oR);
            
            WasteOrdersServer wO = new WasteOrdersServer();
            this.addBehaviour(wO);
            
            IndustriaTickerBehaviour Ib = new IndustriaTickerBehaviour(this, secondsPerTick*1000);
            this.addBehaviour(Ib);
      
            
            DFService.register(this,dfd);
            } catch (FIPAException e) {
                myLogger.log(Logger.SEVERE, "Agent "+getLocalName()+" - Cannot register with DF", e);
                doDelete();
            } 
        
	}

	private int evaluateAction() {
//            return lWaste;
                return 0;
	}

	private boolean performAction() {
		// Verter agua al acantarillado
                //lWaste = 0;
                return true;
	}
        
        
 }
