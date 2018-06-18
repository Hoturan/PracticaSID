package ontology.impl;


import jade.util.leap.*;
import ontology.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#Depuradora
* @author OntologyBeanGenerator v4.1
* @version 2018/06/18, 13:01:05
*/
public class DefaultDepuradora implements Depuradora {

  private static final long serialVersionUID = 3688839762589084456L;

  private String _internalInstanceName = null;

  public DefaultDepuradora() {
    this._internalInstanceName = "";
  }

  public DefaultDepuradora(String instance_name) {
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
     return tankCapacity.remove(elem) != null;
     
   }
   public void clearAllTankCapacity() {
     tankCapacity.clear();
   }
   public Iterator getAllTankCapacity() {return tankCapacity.iterator(); }
   public List getTankCapacity() {return tankCapacity; }
   public void setTankCapacity(List l) {tankCapacity = l; }

}
