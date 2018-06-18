package ontology.impl;


import jade.util.leap.*;
import ontology.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#Industria
* @author OntologyBeanGenerator v4.1
* @version 2018/06/18, 13:01:05
*/
public class DefaultIndustria implements Industria {

  private static final long serialVersionUID = 3688839762589084456L;

  private String _internalInstanceName = null;

  public DefaultIndustria() {
    this._internalInstanceName = "";
  }

  public DefaultIndustria(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#TankCapacity
   */
   private List tankCapacity = new ArrayList();
   public void addTankCapacity(int elem) { 
     tankCapacity.add(elem);
   }
   public boolean removeTankCapacity(int elem) {
      return tankCapacity.remove(elem)!= null;
   }
   public void clearAllTankCapacity() {
     tankCapacity.clear();
   }
   public Iterator getAllTankCapacity() {return tankCapacity.iterator(); }
   public List getTankCapacity() {return tankCapacity; }
   public void setTankCapacity(List l) {tankCapacity = l; }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#EarningsPerProcess
   */
   private List earningsPerProcess = new ArrayList();
   public void addEarningsPerProcess(int elem) { 
     earningsPerProcess.add(elem);
   }
   public boolean removeEarningsPerProcess(int elem) {
     return earningsPerProcess.remove(elem) != null;  
   }
   public void clearAllEarningsPerProcess() {
     earningsPerProcess.clear();
   }
   public Iterator getAllEarningsPerProcess() {return earningsPerProcess.iterator(); }
   public List getEarningsPerProcess() {return earningsPerProcess; }
   public void setEarningsPerProcess(List l) {earningsPerProcess = l; }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#LitersPerProcess
   */
   private List litersPerProcess = new ArrayList();
   public void addLitersPerProcess(int elem) { 
     litersPerProcess.add(elem);
   }
   public boolean removeLitersPerProcess(int elem) {
     return litersPerProcess.remove(elem)!= null;
   }
   public void clearAllLitersPerProcess() {
     litersPerProcess.clear();
   }
   public Iterator getAllLitersPerProcess() {return litersPerProcess.iterator(); }
   public List getLitersPerProcess() {return litersPerProcess; }
   public void setLitersPerProcess(List l) {litersPerProcess = l; }

}
