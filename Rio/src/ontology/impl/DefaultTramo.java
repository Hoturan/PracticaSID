package ontology.impl;


import jade.util.leap.*;
import ontology.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#Tramo
* @author OntologyBeanGenerator v4.1
* @version 2018/06/18, 13:01:05
*/
public class DefaultTramo implements Tramo {

  private static final long serialVersionUID = 3688839762589084456L;

  private String _internalInstanceName = null;

  public DefaultTramo() {
    this._internalInstanceName = "";
  }

  public DefaultTramo(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#TramoID
   */
   private List tramoID = new ArrayList();
   public void addTramoID(int elem) { 
     tramoID.add(elem);
   }
   public boolean removeTramoID(int elem) {
     return tramoID.remove(elem) != null;
   }
   public void clearAllTramoID() {
     tramoID.clear();
   }
   public Iterator getAllTramoID() {return tramoID.iterator(); }
   public List getTramoID() {return tramoID; }
   public void setTramoID(List l) {tramoID = l; }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#SiguienteTramo
   */
   private List siguienteTramo = new ArrayList();
   public void addSiguienteTramo(Tramo elem) { 
     siguienteTramo.add(elem);
   }
   public boolean removeSiguienteTramo(Tramo elem) {
      return siguienteTramo.remove(elem);
   }
   public void clearAllSiguienteTramo() {
     siguienteTramo.clear();
   }
   public Iterator getAllSiguienteTramo() {return siguienteTramo.iterator(); }
   public List getSiguienteTramo() {return siguienteTramo; }
   public void setSiguienteTramo(List l) {siguienteTramo = l; }

}
