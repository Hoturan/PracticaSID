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
import java.util.Vector;
import java.util.logging.Level;

/**
 *
 * @author David
 */
public class AgenteDepuradora extends Agent {
    private final Logger myLogger = Logger.getMyLogger(getClass().getName());

    private boolean debug = true;
    private int position = 3;
    
    DFAgentDescription template = new DFAgentDescription();
    ServiceDescription templateSd = new ServiceDescription();
    
    String message="Have not River";

   private int nResponders;
 
    private AID AIDrio;
    private ArrayList<AID> AIDsDepuradoras = new ArrayList<AID>();
    private ArrayList<AID> AIDsIndustrias = new ArrayList<AID>();
    
     //Number of ticks until waste is clean
    int count_until_depurado = 3;
    //liters of filthy water 
    private int tankCapacity = 2500000; //Liters
    private int lWaste = 0;
    
   
    
    private class DepuradoraTickerBehaviour extends TickerBehaviour    {
        String message;
        int count_chocula;

        boolean pouringWater = false;
        boolean pourSuccessful = false;
       
                        
        public DepuradoraTickerBehaviour(Agent a, long period) {
            super(a, period);
        }

 
        public void onStart()
        {
            this.message = "Agent " + myAgent +" with DepuradoraTickerBehaviour in action!!" + count_chocula;
            count_chocula = 0;
        }
 
        public int onEnd()
        {
            System.out.println("I have done " + count_chocula + " iterations");
            return count_chocula;
        }
        
        public void onTick(){       
            depurarAgua();
            block();
        }
        
        private void pourCleanWater(){
            if (debug) System.out.println("Pouring clean water to " + AIDrio.getLocalName());
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST); 
            request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            request.addReceiver(AIDrio);
            request.setContent("DESCARGAR AGUA " + String.valueOf(position) + " " + String.valueOf(lWaste));
            send(request);
            
            myAgent.addBehaviour(new SimpleBehaviour(){

                private boolean finished = false;

                @Override
                public void action() {
                    ACLMessage reply = receive();
                    System.out.println("Depuradora espera contestacion de rio");
                    if(reply != null){
                        switch(reply.getPerformative()){
                            case ACLMessage.INFORM:
                                String content = reply.getContent();
                                String[] words = content.split("\\s+");
                                System.out.println("AgenteDepuradora has received the following message: " + content);
                                int litrosDescargados = Integer.parseInt(words[words.length-1]);
                                lWaste -= litrosDescargados;
                                break;
                            case ACLMessage.REJECT_PROPOSAL:
                                System.out.println("Depuradora no puede descargar agua al rio");
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
            pourSuccessful = true;
        }
        
        private void depurarAgua(){
            if (count_until_depurado == 0){
                if (lWaste == 0){
                    System.out.println("Depuradora has no waste to clean!!!");
                }
                else if (lWaste > 0){                    
                    ///// DESCARGAR EL AGUA LIMPIA AL RIO
                    pourCleanWater(); 
                    if (debug){
                        System.out.println("Depuradora Process Done");    
                        System.out.println("    Waste Tank at: " + lWaste);
                    }
                }

                if (pourSuccessful){                   
                    pourSuccessful = false;
                    count_until_depurado = 3;
                
                    double percentage = (double) lWaste / (double) tankCapacity;
                    if (percentage  <= 50.0) askForWaste();
                }
                
            }
            else count_until_depurado -= 1;
            
            
        }
        
        private void askForWaste(){
            nResponders = AIDsIndustrias.size();
            System.out.println("Asking for more waste to one of " + nResponders + " Industria responders.");

            // Fill the CFP message
            ACLMessage msg = new ACLMessage(ACLMessage.CFP);
            for (int i = 0; i < AIDsIndustrias.size(); ++i) {
                    msg.addReceiver(AIDsIndustrias.get(i));
            }
            msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
            // We want to receive a reply in 10 secs
            msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
            msg.setContent("dummy-action");
            
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
 
             @Override
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
  
    
    public class SearchIndustriaAndRioOneShotBehaviour extends OneShotBehaviour{
        public SearchIndustriaAndRioOneShotBehaviour()
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
            boolean minARio = false;
            
            AID[] aux = searchDF("AgenteDepuradora");
            if (aux != null){
                for (int i = 0; i < aux.length; ++i){
                    if (!aux[i].equals(myAgent.getAID())){
                        if (debug){
                        System.out.println(myAgent.getAID().getName() + " is adding Depuradora with AID: " + aux[i]);
                    }
                    AIDsDepuradoras.add(aux[i]);
                    }                 
                }
            }
            
            aux = searchDF("AgenteIndustria");
            if (aux != null){
                for (int i = 0; i < aux.length; ++i){
                    if (debug){
                      System.out.println(myAgent.getAID().getName() + " is adding Industria with AID: " + aux[i]);
                    
                    AIDsIndustrias.add(aux[i]);
                    }                 
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
            
          
            
            if (!minARio) {
                myLogger.log(Logger.SEVERE, "No AgenteRio Found! - Cannot continue");
                doDelete();
            }
            block();   
        }
        
 
    }
    
    private class WaitMessageAndReplyBehaviour extends SimpleBehaviour {       

        public WaitMessageAndReplyBehaviour(Agent a) {
            super(a);
        }
        
        private boolean finished = false;
        
        @Override
        public void action() {
            
            ACLMessage request = receive();
            if(request != null){
                switch(request.getPerformative()){
                    case ACLMessage.REQUEST:
                        String content = request.getContent();
                        String[] words = content.split("\\s+");
                        System.out.println("AgenteDepuradora has received the following message: " + content);
                        AID sender = request.getSender();
                        System.out.println("The message was sent by: " + sender.getLocalName());
                        if(sender.getLocalName().equals("AgenteIndustria")){
                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                            reply.addReceiver(sender);
                            reply.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST); // no se si es necesario
                            String msg = "Se han podido almacenar: ";
                            System.out.println("Depuradora is replying to AgenteIndustria");
                            int litrosRecibidos = Integer.parseInt(words[words.length-1]);
                            if(tankCapacity - (lWaste + litrosRecibidos) > 0){
                                // si la depuradora puede almacenar toda el agua recibida:
                                lWaste += litrosRecibidos;
                                msg += String.valueOf(litrosRecibidos);
                            }
                            else{
                                int volumeLeft = tankCapacity - lWaste;
                                msg += String.valueOf(volumeLeft);
                                lWaste += volumeLeft;
                            }
                            reply.setContent(msg);
                            send(reply);
                        }
                        break;
                    default:
                        System.out.println("MALFORMED MESSAGE");
                        break;
                }
                finished = true;
            }
            block();
        }

        @Override
        public boolean done() {
            return finished;
        }
    } 
    
    @Override
    protected void setup()
    {
        this.setQueueSize(5);
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();   
        sd.setType("AgenteDepuradora"); 
        sd.setName(getName());
        dfd.setName(getAID());
        dfd.addServices(sd);
        
        try {
            SearchIndustriaAndRioOneShotBehaviour b = new SearchIndustriaAndRioOneShotBehaviour();
            this.addBehaviour(b); 
        
            DepuradoraTickerBehaviour dT = new DepuradoraTickerBehaviour(this, 3000);
            this.addBehaviour(dT);
        
            AgenteDepuradora.WaitMessageAndReplyBehaviour RioMessageBehaviour = new AgenteDepuradora.WaitMessageAndReplyBehaviour(this);
            this.addBehaviour(RioMessageBehaviour);
            
            DFService.register(this,dfd);
        } 
        catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent " + getLocalName()+ " - Cannot register with DF", e);
            doDelete();
        }
        
        
        //Object[] args = getArguments();
  	//if (args != null && args.length > 0) {
            //nResponders = args.length;
            //nResponders = AIDsIndustrias.size();
            //System.out.println("Trying to delegate dummy-action to one out of "+nResponders+" responders.");

            // Fill the CFP message
            
        //}
			
            
    }
}
