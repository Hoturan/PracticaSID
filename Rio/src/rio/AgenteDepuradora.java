/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rio;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
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

/**
 *
 * @author David
 */
public class AgenteDepuradora extends Agent {
    private final Logger myLogger = Logger.getMyLogger(getClass().getName());

    private boolean debug = true;
    
    DFAgentDescription template = new DFAgentDescription();
    ServiceDescription templateSd = new ServiceDescription();
    
    String message="Have not River";

    
    protected String performs;
    protected int evaluation;
 
    private AID AIDrio;
    private ArrayList<AID> AIDsDepuradoras = new ArrayList<AID>();
    private ArrayList<AID> AIDsIndustrias = new ArrayList<AID>();
    
    private class ContractNetResponderBehaviour extends ContractNetResponder
    {
                  
      private boolean performAction()
      {
            // Simulate action execution by generating a random number
            return (Math.random() > 0.2);
      }
 
        public ContractNetResponderBehaviour(Agent a, MessageTemplate mt)
        {
            super(a,mt);
        }
 
        protected ACLMessage prepareResponse(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
                System.out.println("Agent '"+getLocalName()+"' receives a CFP from Agent '"+cfp.getSender().getName()+"' to perform action '"+cfp.getContent() + "'");
                if (performs.equalsIgnoreCase("YES")) {
                    // We provide a proposal
                    System.out.println("Agent '"+getLocalName()+"' proposes  '"+evaluation + "'");
                    ACLMessage propose = cfp.createReply();
                    propose.setPerformative(ACLMessage.PROPOSE);
                    propose.setContent(String.valueOf(evaluation));
                    return propose;
                }
                else {
                    // We refuse to provide a proposal
                    System.out.println("Agent '"+getLocalName()+"' is not interested in the proposal");
                    throw new RefuseException("evaluation-failed");
                }
            }
 
            protected ACLMessage prepareResultNotification(ACLMessage cfp, ACLMessage propose,ACLMessage accept) throws FailureException {
                System.out.println("Agent '"+getLocalName()+"' accepts proposal and is about to perform an action");
                if (performAction()) {
                    System.out.println("Agent '"+getLocalName()+"' succesfully performs action");
                    ACLMessage inform = accept.createReply();
                    inform.setPerformative(ACLMessage.INFORM);
                    return inform;
                }
                else {
                    System.out.println("Agent '"+getLocalName()+"' failed to perform action");
                    throw new FailureException("unexpected-error");
                }
            }
 
            protected void handleRejectProposal(ACLMessage reject)
                        {
                System.out.println("Agent '"+getLocalName()+"' rejects proposal");
            }
    }
    
    public class SearchIndustriaAndRioOneShotBehaviour extends OneShotBehaviour
        {
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
          
          
        }
 
    }
    
    @Override
    protected void setup()
    {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();   
        sd.setType("AgenteDepuradora"); 
        sd.setName(getName());
        dfd.setName(getAID());
        dfd.addServices(sd);
        
        try {
            DFService.register(this,dfd);
        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent "+getLocalName()+" - Cannot register with DF", e);
            doDelete();
        }
        
        SearchIndustriaAndRioOneShotBehaviour b = new SearchIndustriaAndRioOneShotBehaviour();
        this.addBehaviour(b); 
        
        System.out.println("Agent '"+getLocalName()+"' is waiting for a CFP...");
        //Object[] args = getArguments();         
        //performs = args[0].toString();
        //evaluation = Integer.parseInt( args[1].toString());
 
        //Create a message template for the CFP
        MessageTemplate template = MessageTemplate.and(
        MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
        MessageTemplate.MatchPerformative(ACLMessage.CFP) );
 
        addBehaviour(new ContractNetResponderBehaviour(this, template));
    }
}
