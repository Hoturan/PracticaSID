/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rio;

import jade.core.AID;
import jade.core.Agent;
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
 
    private int secondsPerTick = 5;
    private Industria industria;
    private MessageManager msgManager;
    private int identificador = 9156;
    
    String message="Have not found one of the two basic Agents";
    DFAgentDescription template = new DFAgentDescription();
    ServiceDescription templateSd = new ServiceDescription();
        
    protected String performs;
    protected int evaluation;
    
    private AID AIDrio;
    private AID AIDDepuradora;
    private ArrayList<AID> AIDsIndustrias = new ArrayList<AID>();    
    
    private class MessageRecieverBehaviour extends SimpleBehaviour{
        private boolean finish = false;
        @Override
            public void action() {
                ACLMessage reply = receive();
                //System.out.println("Industria espera contestacion de rio");
                if(reply != null){
                    switch(reply.getPerformative()){
                        case ACLMessage.INFORM:
                            String content = reply.getContent();
                            String[] words = content.split("\\s+");
                            System.out.println("AgenteIndustria has received the following message: " + content);
                            int litros = msgManager.getLitros(words);
                            int indiceIndustria = msgManager.getIndice(words);
                            switch(reply.getSender().getLocalName()){
                                case "AgenteRio":
                                    industria.setlWater(industria.getlWater() + litros);
                                    System.out.println("Industria " + indiceIndustria + " tiene " + industria.getlWater() + " litros de agua");
                                    break;
                                case "AgenteDepuradora":
                                    industria.setlWaste(industria.getlWaste() - litros);
                                    System.out.println("Industria " + indiceIndustria + " tiene " + industria.getlWaste() + " litros de agua sucia");
                                    break;
                                default:
                                    System.out.println("Oops, algo ha ido mal!");
                                    break;
                            }

                            break;
                        case ACLMessage.REJECT_PROPOSAL:
                            System.out.println("AgenteIndustria no puede realizar la accion deseada");
                            break;
                        default:
                            System.out.println("MALFORMED MESSAGE");
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
        
    private class IndustriaTickerBehaviour extends TickerBehaviour {
        String message;
        int count_chocula;
        
        boolean pouringWater = false;
        
        public void onStart(){
            msgManager = new MessageManager();
            Object[] args = getArguments();
            int tankCapacity = Integer.valueOf(args[0].toString());       
            int posicion = Integer.valueOf(args[1].toString());             
            int litersPerProcess = Integer.valueOf(args[2].toString());   
            
            if (debug) {
                System.out.println("Parametros de " + myAgent.getAID().getLocalName());
                System.out.println("    Capacidad del tanque ---> " + tankCapacity + "L");
                System.out.println("    Tramo de la Indunstria ---> " + posicion);
                System.out.println("    Litros usados por processo ---> " + litersPerProcess + "L");
                System.out.println("-------------------------------------------------------");
            }
            
            industria = new Industria();
            industria.setTankCapacity(tankCapacity);
            industria.setLitersPerProcess(litersPerProcess);
            industria.setPosition(posicion);
            industria.setEarningsPerProcess(500);
          
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

        private void extractCleanWater(int indiceIndustria){
            if (debug) {
                System.out.println("Not enough clean water in Industria " + this.getAgent().getName());
                System.out.println("Going to extract more");
            }
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST); 
            request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            request.addReceiver(AIDrio);
            String content = msgManager.extraerAgua(indiceIndustria, industria.getPosition(), 1000000);
            request.setContent(content);
            send(request);      
       
        }        
              
        
        public void procesaAgua() {

                

                int litersPerProcess = industria.getLitersPerProcess();
                
                if (industria.getlWater() >= litersPerProcess && industria.getlWaste() <= (industria.getTankCapacity() - litersPerProcess)){
                    industria.setlWater(industria.getlWater() - litersPerProcess);
                    industria.setlWaste(industria.getlWaste() + litersPerProcess);
                    industria.generateEarnings();

                    if(debug){
                        System.out.println("Industria " + identificador + " Process Done: ");
                        System.out.println("    Clean water Tank at: " + industria.getlWater() + "L");
                        System.out.println("    Waste Tank at: " + industria.getlWaste() + "L");
                        System.out.println("    Earnings at: " + industria.getEarnings() + " euros\n");
                    }

                }
                else if (industria.getlWaste() > (industria.getTankCapacity() - litersPerProcess)){
                    System.out.println("Stopping production, no more capacity for Waste");
                }
                else if (industria.getlWater() <= (industria.getTankCapacity() - 1000000)){
                    extractCleanWater(identificador);
                }

                double wasteWaterLoad = (double) industria.getlWaste() / (double) industria.getTankCapacity();

                if (wasteWaterLoad > 0.75){
                    if (debug) System.out.println("Waste Tank at more than 75% capacity, proceding to search for Depuradora");                 
                    ACLMessage  request  =  new  ACLMessage(ACLMessage.REQUEST); 
                    request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
                    String content = msgManager.enviaAgua(identificador, industria.getlWaste());
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
    
    
    protected void setup()
    {
        this.setQueueSize(5);
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();   
        sd.setType("AgenteIndustria"); 
        sd.setName(getName());
        dfd.setName(getAID());
        dfd.addServices(sd);
        
        try {     
            SearchDepuradoraAndRioOneShotBehaviour sD = new SearchDepuradoraAndRioOneShotBehaviour();
            this.addBehaviour(sD);        
            
            MessageRecieverBehaviour mR = new MessageRecieverBehaviour();
            this.addBehaviour(mR); 
            
            IndustriaTickerBehaviour Ib = new IndustriaTickerBehaviour(this, secondsPerTick*1000);
            this.addBehaviour(Ib);
            
            DFService.register(this,dfd);
        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent "+getLocalName()+" - Cannot register with DF", e);
            doDelete();
        }
        
        
                
        System.out.println("Agent "+getLocalName()+" waiting for CFP...");
        MessageTemplate template = MessageTemplate.and(
                        MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
                        MessageTemplate.MatchPerformative(ACLMessage.CFP) );

        this.addBehaviour(new ContractNetResponder(this, template) {
                @Override
                protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
                        System.out.println("Agent "+getLocalName()+": CFP received from "+cfp.getSender().getName()+". Action is "+cfp.getContent());
                        int proposal = evaluateAction();
                        if (proposal > 2) {
                                // We provide a proposal
                                System.out.println("Agent "+getLocalName()+": Proposing "+proposal);
                                ACLMessage propose = cfp.createReply();
                                propose.setPerformative(ACLMessage.PROPOSE);
                                propose.setContent(String.valueOf(proposal));
                                return propose;
                        }
                        else {
                                // We refuse to provide a proposal
                                System.out.println("Agent "+getLocalName()+": Refuse");
                                throw new RefuseException("evaluation-failed");
                        }
                }

                @Override
                protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose,ACLMessage accept) throws FailureException {
                        System.out.println("Agent "+getLocalName()+": Proposal accepted");
                        if (performAction()) {
                                System.out.println("Agent "+getLocalName()+": Pour Waste action successfully performed");
                                ACLMessage inform = accept.createReply();
                                inform.setPerformative(ACLMessage.INFORM);
                                return inform;
                        }
                        else {
                                System.out.println("Agent "+getLocalName()+": Pour Waste action execution failed");
                                throw new FailureException("unexpected-error");
                        }	
                }

                protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
                        System.out.println("Agent "+getLocalName()+": Proposal rejected");
                }
        } );
	}

	private int evaluateAction() {
         //   return lWaste;
            return 5;
	}

	private boolean performAction() {
		// Verter agua al acantarillado
                //lWaste = 0;
                return true;
	}
        
        
 }
