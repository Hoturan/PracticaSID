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
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author David
 */
public class AgenteIndustria extends Agent{
    private Logger myLogger = Logger.getMyLogger(getClass().getName());

    private boolean debug = true;

    
    String message="Have not found one of the two basic Agents";
    DFAgentDescription template = new DFAgentDescription();
    ServiceDescription templateSd = new ServiceDescription();
    
    AID AIDrio;
    ArrayList<AID> AIDsDepuradoras = new ArrayList<AID>();
    ArrayList<AID> AIDsIndustrias = new ArrayList<AID>();

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
            if (aux != null){
                if (debug){
                    System.out.println("Rio added with AID: " + aux[0]);
                }
                minARio = true;
                AIDrio = aux[0];
            }
            else {
                myLogger.log(Logger.SEVERE, "No AgenteRio Found! - Cannot continue");
                doDelete();
            }
            
            /*template.addServices(templateSd);
            SearchConstraints sc = new SearchConstraints();
            DFAgentDescription[] results = null;
            
            try {
                results = DFService.search(myAgent, template, sc);
            } catch (FIPAException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("Search returns: " + results.length + " elements" );
            if (results.length > 0) {
                for (int i = 0; i < results.length; ++i){
                    DFAgentDescription dfd = results[0];
                    AID provider = dfd.getName();
                    if (provider.getLocalName().equals("AgenteRio")){
                        System.out.println("HERE1: " + provider.getName());
                        minARio = true;
                        AIDrio = provider;
                    }
                    else if (provider.getLocalName().equals("AgenteDepuradora")){
                        System.out.println("HERE2");
                        minADepuradora = true;
                        AIDsDepuradoras.add(provider);
                    }
                    else if (!provider.equals(myAgent.getAID()) && provider.getLocalName().equals("AgenteIndustria")){
                        AIDsIndustrias.add(provider);
                    }
                }
            }*/

            if (!minADepuradora || !minARio) System.out.println(message);
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
    }
    
}
