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
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import jade.proto.ContractNetInitiator;
import jade.proto.ContractNetResponder;
import jade.util.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

/**
 *
 * @author David
 */
public class AgenteIndustria extends Agent{
    private Logger myLogger = Logger.getMyLogger(getClass().getName());

    private boolean debug = true;
    private int position = 2;  // TRAMO DEL RIO EN EL QUE ESTA SITUADA LA INDUSTRIA

    //liters of clean water 
    private int lWater = 0;
    //liters of filthy water 
    private int lWaste = 0;
    
    private int tankCapacity = 2500000; // 2.5M Liters

    //Money made, trying to maximize it
    private int earnings = 0;
    
    private int earningsPerProcess = 500;
    
    String message="Have not found one of the two basic Agents";
    DFAgentDescription template = new DFAgentDescription();
    ServiceDescription templateSd = new ServiceDescription();
        
    protected String performs;
    protected int evaluation;
    
    private AID AIDrio;
    private ArrayList<AID> AIDsDepuradoras = new ArrayList<AID>();
    private ArrayList<AID> AIDsIndustrias = new ArrayList<AID>();
    
        
    private class IndustriaTickerBehaviour extends TickerBehaviour {
        String message;
        int count_chocula;
        
        boolean pouringWater = false;
        
        public IndustriaTickerBehaviour(Agent a, long period) {
            super(a, period);
        }

 
        public void onStart()
        {
            this.message = "Agent " + myAgent +" with IndustriaTickerBehaviour in action!!" + count_chocula;
            count_chocula = 0;
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
            if (debug) {
                System.out.println("Not enough clean water in Industria " + this.getAgent().getName());
                System.out.println("Going to extract more");
            }
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST); 
            request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            request.addReceiver(AIDrio);
            request.setContent("EXTRAER AGUA " + String.valueOf(position));
            send(request);

            myAgent.addBehaviour(new SimpleBehaviour(){

                private boolean finished = false;

                @Override
                public void action() {
                    ACLMessage reply = receive();
                    System.out.println("Industria espera contestacion de rio");
                    if(reply != null){
                        switch(reply.getPerformative()){
                            case ACLMessage.INFORM:
                                String content = reply.getContent();
                                System.out.println("AgenteIndustria has received the following message: " + content);
                                int litrosExtraidos = Integer.parseInt(content.substring(content.length()-1));
                                lWater += litrosExtraidos * 1000000; // * 1000.000
                                System.out.println("Industria tiene " + lWater + " litros de agua");
                                break;
                            case ACLMessage.REJECT_PROPOSAL:
                                System.out.println("AgenteIndustria aun no puede obtener agua del rio");
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
                    return finished;
                }        
            });
                
        }        
              
        
        public void procesaAgua() {
            if (lWater > 250000 && lWaste < (tankCapacity-250000)){
                lWater -= 250000;
                lWaste += 250000;
                earnings += earningsPerProcess;
                
                if(debug){
                    System.out.println("Industria Process Done: ");
                    System.out.println("    Clean water Tank at: " + lWater + "L");
                    System.out.println("    Waste Tank at: " + lWaste + "L");
                    System.out.println("    Earnings at: " + earnings + " euros");
                }
                                     
            }
            else if (lWaste > (tankCapacity-250000)){
                System.out.println("Stopping production, no more capacity for Waste");
            }
            else{
                extractCleanWater();
            }
            
            if (lWaste/tankCapacity > 0.75){
                if (debug) System.out.println("Waste Tank at more than 75% capacity, proceding to search for Depuradora");
                if (!pouringWater){
                   for (int i = 0; i < AIDsDepuradoras.size() && lWaste > tankCapacity - 250000; ++i){
                        ACLMessage  request  =  new  ACLMessage(ACLMessage.REQUEST); 
                        request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
                        request.setContent(String.valueOf(lWaste));
                        request.addReceiver(AIDsDepuradoras.get(i));
                        pouringWater = true;
                        final String depuradoraName =  AIDsDepuradoras.get(i).getLocalName();
                        myAgent.addBehaviour( new  AchieveREInitiator(myAgent,  request)  {      
                            @Override
                            protected  void  handleInform(ACLMessage  inform)  {   
                                System.out.println("Protocol  finished. Depuradora" + depuradoraName +  " accepted the following waste liters:  "+ inform); 
                                int l = Integer.valueOf(inform.getContent());
                                lWaste -= l;
                                pouringWater = false; 
                            }

                            @Override
                            protected void handleRefuse(ACLMessage reject){
                                System.out.println("Depuradora " + depuradoraName + " rejects because" + reject.getContent());
                            }
                        }); 
                } 
                }
                
                
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
            // Don't set type to obtain all Agents
            //templateSd.setType("AgenteRio");
            
            // To control that we have at least found 1 of each
            boolean minADepuradora = false;
            boolean minARio = false;
            
            AID[] aux = searchDF("AgenteDepuradora");
            if (aux != null){
                for (int i = 0; i < aux.length; ++i){
                    if (debug){
                        System.out.println(myAgent.getAID().getName() + " is adding Depuradora with AID: " + aux[i]);
                    }
                    minADepuradora = true;
                    AIDsDepuradoras.add(aux[i]);
                }
            }
            else {
                myLogger.log(Logger.SEVERE, "No AgenteDepuradora Found! - Cannot continue");
                doDelete();
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
            block();
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
            IndustriaTickerBehaviour Ib = new IndustriaTickerBehaviour(this, 5000);
            this.addBehaviour(Ib);
        
            SearchDepuradoraAndRioOneShotBehaviour sD = new SearchDepuradoraAndRioOneShotBehaviour();
            this.addBehaviour(sD);
            
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
            return lWaste;
	}

	private boolean performAction() {
		// Verter agua al acantarillado
                lWaste = 0;
                return true;
	}
        
        
 }
