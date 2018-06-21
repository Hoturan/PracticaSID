package rio;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
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
import jade.proto.ContractNetInitiator;
import jade.util.Logger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import ontology.Depuradora;
import ontology.MessageManager;


public class AgenteDepuradora extends Agent {
    private final Logger myLogger = Logger.getMyLogger(getClass().getName());

    private boolean debug = true;
    private Depuradora depuradora;
    private MessageManager msgManager;
    private boolean askedOnce = false;
    private int position = 3;
    
    DFAgentDescription template = new DFAgentDescription();
    ServiceDescription templateSd = new ServiceDescription();
    private MessageTemplate mt;
    
    String message="Have not River";

    private int nResponders = 0;
 
    private AID AIDrio;
    private ArrayList<AID> AIDsIndustrias = new ArrayList<AID>();

    
    private class MessageRecieverBehaviour extends SimpleBehaviour{
        private boolean finished = false;
        private int step = 1;
        private AID bestOffer;
        private int mostWaste = 0;
        private int replyNumber = 0;
        private int amountL = 0;
        @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if(msg != null){
                      switch(msg.getPerformative()){
                          case ACLMessage.REQUEST:
                                String content = msg.getContent();
                                String[] words = content.split("\\s+");
                                System.out.println("AgenteDepuradora has received the following message: " + content);
                                AID sender = msg.getSender();
                                System.out.println("The message was sent by: " + sender.getLocalName());
                                if(sender.getLocalName().contains("AgenteIndustria")){
                                    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                                    reply.addReceiver(sender);
                                    reply.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST); // no se si es necesario
                                    String send;
                                    System.out.println("Depuradora is replying to " + sender.getLocalName());
                                    int litrosRecibidos = msgManager.getLitros(words);
                                    //int indiceIndustria = msgManager.getIndice(words);
                                    if(depuradora.getTankCapacity() - (depuradora.getlWaste() + litrosRecibidos) > 0){
                                        // si la depuradora puede almacenar toda el agua recibida:
                                        depuradora.setlWaste(depuradora.getlWaste() + litrosRecibidos);
                                        send = msgManager.aguaAlmacenada(sender.getLocalName(), litrosRecibidos);
                                    }
                                    else{
                                        int volumeLeft = depuradora.getTankCapacity() - depuradora.getlWaste();
                                        send = msgManager.aguaAlmacenada(sender.getLocalName(), volumeLeft);
                                        depuradora.setlWaste(depuradora.getlWaste() + volumeLeft);
                                    }
                                    reply.setConversationId("others");
                                    reply.setContent(send);
                                    send(reply);
                                }
                                break;
                        case ACLMessage.INFORM:
                            if (msg.getSender().equals(bestOffer)){
                                // Purchase successful. We can terminate
                                System.out.println("lWaste successfully accuired from agent "+msg.getSender().getName());
                                System.out.println("Liters = "+ mostWaste);
                                depuradora.setlWaste(depuradora.getlWaste()+amountL);
                            }
                            else{
                                content = msg.getContent();
                                String[] word = content.split("\\s+");
                                System.out.println("AgenteDepuradora has received the following message: " + content);
                                int litrosDescargados = msgManager.getLitros(word);
                                depuradora.setlWater(depuradora.getlWater() - litrosDescargados);        
                            }
                            break;
                        case ACLMessage.REJECT_PROPOSAL:
                            System.out.println("Depuradora no puede descargar agua al rio");
                            break;
                        case ACLMessage.PROPOSE:
                            int waste = Integer.parseInt(msg.getContent());
                            System.out.println("Propose " + waste+ "L recieved from " + msg.getSender().getLocalName());
                            if (bestOffer == null || waste < mostWaste) {
                                // This is the best offer at present
                                mostWaste = waste;
                                bestOffer = msg.getSender();
                            }
                            replyNumber++;
                            if (replyNumber >= AIDsIndustrias.size()) {
                                // We received all replies
                                step = 2; 
                            }
                            if (step == 2){
                                ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                                order.addReceiver(bestOffer);
                                amountL = (depuradora.getTankCapacity() - depuradora.getlWaste() + mostWaste);
                                if (amountL < 0) amountL = mostWaste - amountL;
                                else amountL = mostWaste;
                                if (debug) System.out.println("Can handle " + amountL + "L");
                                order.setContent(String.valueOf(amountL));
                                order.setConversationId("cfp");
                                order.setReplyWith("order"+System.currentTimeMillis());
                                myAgent.send(order);
                                // Prepare the template to get the purchase order reply
                                mt = MessageTemplate.and(MessageTemplate.MatchConversationId("cfp"),
                                            MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                                depuradora.setlWaste(mostWaste);
                                step = 1;
                            }
                            break;
                        default:
                            System.out.println("MALFORMED MESSAGE");
                            break;
                        }  

                }
                else block();                  
            }
                   
                @Override
                public boolean done() {
                    return finished;
                }

                
    }
    
    private class RequestPerformer extends Behaviour {
		private int step = 1;
                private AID bestOffer;
                private int mostWaste = 0;
                private int replyNumber = 0;
                

                @Override
		public void action() {
			switch (step) {
                            case 1:
                                MessageTemplate mess = MessageTemplate.MatchConversationId("cfp");
                                ACLMessage msg = myAgent.receive(mess);
                                if (msg != null) {
                                        System.out.println("GOT A MESSAGE!");
                                        if (msg.getPerformative() == ACLMessage.PROPOSE){
                                            // This is an offer 
                                            int waste = Integer.parseInt(msg.getContent());
                                            System.out.print("Propose " + waste+ "L recieved from " + msg.getSender().getLocalName());
;                                           if (bestOffer == null || waste < mostWaste) {
                                                // This is the best offer at present
                                                mostWaste = waste;
                                                bestOffer = msg.getSender();
                                            }
                                        }
                                        replyNumber++;
                                        if (replyNumber >= AIDsIndustrias.size()) {
                                        // We received all replies
                                        step = 2; 
                                        }      
                                }
                                else block();
                                break;

                            case 2:
                                ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                                order.addReceiver(bestOffer);
                                order.setContent("SYMBOLIC");
                                order.setConversationId("cfp");
                                order.setReplyWith("order"+System.currentTimeMillis());
                                myAgent.send(order);
                                // Prepare the template to get the purchase order reply
                                mt = MessageTemplate.and(MessageTemplate.MatchConversationId("cfp"),
                                            MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                                depuradora.setlWaste(mostWaste);
                                step = 3;
                                break;
                            case 3:                             
                                msg = myAgent.receive(mt);
                                if (msg.getPerformative() == ACLMessage.INFORM) {
                                        // Purchase successful. We can terminate
                                        System.out.println("lWaste successfully accuired from agent "+msg.getSender().getName());
                                        System.out.println("Liters = "+ mostWaste);

                                }
                                else {
                                        System.out.println("Attempt failed");
                                }

                                step = 1;
                            }
                        }        

                @Override
		public boolean done() {
			if (step == 2 && bestOffer == null) {
				System.out.println("Attempt failed: No waste avaliable");
			}
			return ((step == 2 && bestOffer == null) || step == 4);
		}
	}  // End of inner class RequestPerformer
      
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
            msgManager = new MessageManager();
            depuradora = new Depuradora();
            depuradora.setPosition(6);              ////  { POR PARAMETROS }
            depuradora.setTicksPerProcess(5);       ////  { POR PARAMETROS }
            depuradora.setTankCapacity(1000000);    ////  { POR PARAMETROS }
            
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
            request.setConversationId("others");
            String content = msgManager.descargarAgua(depuradora.getPosition(), depuradora.getlWater());
            request.setContent(content);
            send(request);
            
            pourSuccessful = true;
        }
        
        private void depurarAgua(){
            if (depuradora.getTicksLeft() == 0){
                if (depuradora.getlWaste() == 0){
                    System.out.println("Depuradora has no waste to clean!!!");
                    if (!askedOnce){
                        askedOnce = true;
                        askForWaste();
                    }
                }
                else if (depuradora.getlWaste() > 0){   
                    depuradora.setlWater(depuradora.getlWaste());
                    depuradora.setlWaste(0);
                    ///// DESCARGAR EL AGUA LIMPIA AL RIO
                    pourCleanWater(); 
                    askedOnce = false;
                    if (debug){
                        System.out.println("Depuradora Process Done");    
                        System.out.println("    Waste Tank at: " + depuradora.getlWaste() + "\n");
                    }
                }

                if (pourSuccessful){  
                    askedOnce = false;
                    pourSuccessful = false;
                    depuradora.restartTicksLeft();
                
                    double percentage = (double) depuradora.getlWaste() / (double) depuradora.getTankCapacity();
                    if (percentage  <= 50.0) askForWaste();
                }
                
            }
            else depuradora.setTicksLeft(depuradora.getTicksLeft() - 1);
     
        }
        
        private void askForWaste(){
            nResponders = AIDsIndustrias.size();
            System.out.println("Asking for more waste to one of " + nResponders + " Industria responders.");

            // Fill the CFP message
            ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
            for (int i = 0; i < AIDsIndustrias.size(); ++i) {
                    cfp.addReceiver(AIDsIndustrias.get(i));
            } 
            cfp.setContent("Need Waste");
            cfp.setConversationId("cfp");
            cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
            mt = MessageTemplate.MatchConversationId("cpf");
            myAgent.send(cfp);
            
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
            
            
            AID[] aux = searchDF("Industria");
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
            
            MessageRecieverBehaviour mR = new MessageRecieverBehaviour();
            this.addBehaviour(mR); 
        
            DepuradoraTickerBehaviour dT = new DepuradoraTickerBehaviour(this, 3000);
            this.addBehaviour(dT);
            
            DFService.register(this,dfd);
        } 
        catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent " + getLocalName()+ " - Cannot register with DF", e);
            doDelete();
        }
            
    }
}
