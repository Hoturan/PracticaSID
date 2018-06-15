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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author David
 */
public class AgenteIndustria extends Agent{
    String message="Have not found one of the two basic Agents";
    DFAgentDescription template = new DFAgentDescription();
    ServiceDescription templateSd = new ServiceDescription();
    
    AID AIDrio;
    List AIDDepuradoras = new ArrayList();
    List AIDIndustrias = new ArrayList();

     public class SearchDepuradoraAndRioOneShotBehaviour extends OneShotBehaviour
    {
        public SearchDepuradoraAndRioOneShotBehaviour()
        {
             
        }
 
        public void onStart()
        {
             
        }
 
        public int onEnd()
        {
             
            return 1;
        }
 
        public void action()
        {
            templateSd.setType("AgenteRio");
            template.addServices(templateSd);
            SearchConstraints sc = new SearchConstraints();
            DFAgentDescription[] results = null;
            
            try {
                results = DFService.search(myAgent, template, sc );
            } catch (FIPAException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (results.length > 0) {
                DFAgentDescription dfd = results[0];
                AID provider = dfd.getName();
                message = "AgenteRio is "+provider;
                AIDrio = provider;
            }
            System.out.println(message);
        }
 
    }
    
    protected void setup()
    {
         SearchDepuradoraAndRioOneShotBehaviour b = new SearchDepuradoraAndRioOneShotBehaviour();
         this.addBehaviour(b); 
    }
    
}
