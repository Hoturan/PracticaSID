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
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import jade.proto.ContractNetInitiator;
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

    //leaters of clean water 
    private int lWater = 10;
    //leaters of filthy water 
    private int lWaste = 0;
    
    //Money made, trying to maximize it
    private int earnings = 0;
    
    private int leatersUsedPerProcess = 2;
    private int earningsPerProcess = 500;
    
    String message="Have not found one of the two basic Agents";
    DFAgentDescription template = new DFAgentDescription();
    ServiceDescription templateSd = new ServiceDescription();
    
    private int nResponders;
    
    AID AIDrio;
    ArrayList<AID> AIDsDepuradoras = new ArrayList<AID>();
    ArrayList<AID> AIDsIndustrias = new ArrayList<AID>();
    
    private class IndustriaTickerBehaviour extends TickerBehaviour    {
        String message;
        int count_chocula;

        boolean extractingWater = false;
        
        public IndustriaTickerBehaviour(Agent a, long period) {
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
            
            processaAgua();
        }
        
        private void extractCleanWater(){
            if (debug) System.out.println("No clean water left in Industria " + this.getAgent().getName());
            if (!extractingWater){
                if (debug) System.out.println("Going to extract more");
                ACLMessage  request  =  new  ACLMessage(ACLMessage.REQUEST); 
                request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
                request.addReceiver(AIDrio);
                extractingWater = true;
                myAgent.addBehaviour( new  AchieveREInitiator(myAgent,  request)  {      
                    @Override
                    protected  void  handleInform(ACLMessage  inform)  {   
                        System.out.println("Protocol  finished. Received  the  following  message:  "+inform); 
                        lWater += 10;
                        extractingWater = false;
                    }

                    @Override
                    protected void handleRefuse(ACLMessage reject){
                        System.out.println("Protocol  finished. No more water left in this section of the river");
                        extractingWater = false;
                    }
                });
            }
                      
        }
        
        public void processaAgua() {
            if (lWater > 2){
                lWater -= 2;
                lWaste += 2;
                earnings += earningsPerProcess;
                
                if(debug){
                    System.out.println("Industria Process Done: ");
                    System.out.println("    Clean water Tank at: " + lWater + "L");
                    System.out.println("    Waste Tank at: " + lWaste + "L");
                    System.out.println("    Earnings at: " + earnings + " euros");
                }
                                     
            }
            else{
                extractCleanWater();
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
                        System.out.println("Depuradora added with AID: " + aux[i]);
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
                    System.out.println("Rio added with AID: " + aux[0]);
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
            
            /*if (AIDsDepuradoras.size() > 0) {
                nResponders = AIDsDepuradoras.size();
                System.out.println("Trying to delegate the action Perform AIA exam to "+nResponders+" responders. Press any key to go on...");
                        BufferedReader br = new BufferedReader(new InputStreamReader(System.in) );
                    try {
                        String sample = br.readLine();
                    } catch (IOException ex) {
                        Logger.getLogger(AgenteIndustria.class.getName()).log(Level.SEVERE, null, ex);
                    }

                // Create the CFP message
                ACLMessage msg = new ACLMessage(ACLMessage.CFP);
                for (int i = 0; i < AIDsDepuradoras.size(); ++i) {
                    msg.addReceiver(new AID((String) AIDsDepuradoras.get(i).getName(), AID.ISLOCALNAME)); //Not sure
                }
                    msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
                    // We want to receive a reply in 10 secs
                    msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
                    msg.setContent("Permisso para verter agua");
                                ContractNetInitiatorBehaviour cib = new ContractNetInitiatorBehaviour(myAgent,msg);
                    addBehaviour(cib);
            }
            else {
                System.out.println("No responder specified.");
            }*/
        }
 
    }
     
     private class ContractNetInitiatorBehaviour extends ContractNetInitiator
         {
             public ContractNetInitiatorBehaviour(Agent a, ACLMessage mt)
            {
                super(a,mt);
            }
 
             protected void handlePropose(ACLMessage propose, Vector v)
             {
                System.out.println("Agent '"+propose.getSender().getName()+"' proposed '"+propose.getContent() + "'");
             }
 
            protected void handleRefuse(ACLMessage refuse)
            {
                    System.out.println("Agent '"+refuse.getSender().getName()+"' refused");
            }
 
            protected void handleFailure(ACLMessage failure)
            {
                    if (failure.getSender().equals(myAgent.getAMS())) {
                            // FAILURE notification from the JADE runtime: the receiver
                            // does not exist
                            System.out.println("Responder does not exist");
                    }
                    else {
                            System.out.println("Agent '"+failure.getSender().getName()+"' failed");
                    }
                    // Immediate failure --> we will not receive a response from this agent
                    nResponders--;
            }
 
            protected void handleAllResponses(Vector responses, Vector acceptances)
            {
                    if (responses.size() < nResponders) {
                            // Some responder didn't reply within the specified timeout
                            System.out.println("Timeout expired: missing "+(nResponders - responses.size())+" responses");
                    }
                    // Evaluate proposals.
                    int bestProposal = -1;
                    AID bestProposer = null;
                    ACLMessage accept = null;
                    Enumeration e = responses.elements();
                    while (e.hasMoreElements()) {
                            ACLMessage msg = (ACLMessage) e.nextElement();
                            if (msg.getPerformative() == ACLMessage.PROPOSE) {
                                    ACLMessage reply = msg.createReply();
                                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                                    acceptances.addElement(reply);
                                    int proposal = Integer.parseInt(msg.getContent());
                                    if (proposal > bestProposal) {
                                            bestProposal = proposal;
                                            bestProposer = msg.getSender();
                                            accept = reply;
                                    }
                            }
                    }
                    // Accept the proposal of the best proposer
                    if (accept != null) {
                            System.out.println("Accepting proposal '"+bestProposal+"' from responder '"+bestProposer.getName() + "'");
                            accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    }
            }
 
            protected void handleInform(ACLMessage inform)
            {
                    System.out.println("Agent '"+inform.getSender().getName()+"' successfully performed the requested action");
            }
 
              
         } 
    
    protected void setup()
    {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();   
        sd.setType("AgenteIndustria"); 
        sd.setName(getName());
        dfd.setName(getAID());
        dfd.addServices(sd);
        
        try {
            DFService.register(this,dfd);
        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent "+getLocalName()+" - Cannot register with DF", e);
            doDelete();
        }
        
        SearchDepuradoraAndRioOneShotBehaviour b = new SearchDepuradoraAndRioOneShotBehaviour();
        this.addBehaviour(b); 
        
        IndustriaTickerBehaviour It = new IndustriaTickerBehaviour(this, 1000);
        this.addBehaviour(It);
        
    }
    
}
