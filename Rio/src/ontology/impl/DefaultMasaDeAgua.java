package ontology.impl;


import jade.util.leap.*;
import ontology.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#MasaDeAgua
* @author OntologyBeanGenerator v4.1
* @version 2018/06/18, 13:01:05
*/
public class DefaultMasaDeAgua /*implements MasaDeAgua */{

  private static final long serialVersionUID = 3688839762589084456L;

  private String _internalInstanceName = null;

  public DefaultMasaDeAgua() {
    this._internalInstanceName = "";
  }

  public DefaultMasaDeAgua(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#IndicadorAgua
   */
   private List indicadorAgua = new ArrayList();
   public void addIndicadorAgua(String elem) { 
     indicadorAgua.add(elem);
   }
   public boolean removeIndicadorAgua(String elem) {
     boolean result = indicadorAgua.remove(elem);
     return result;
   }
   public void clearAllIndicadorAgua() {
     indicadorAgua.clear();
   }
   public Iterator getAllIndicadorAgua() {return indicadorAgua.iterator(); }
   public List getIndicadorAgua() {return indicadorAgua; }
   public void setIndicadorAgua(List l) {indicadorAgua = l; }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#Volumen
   */
   private List volumen = new ArrayList();
   public void addVolumen(Float elem) { 
     volumen.add(elem);
   }
   public boolean removeVolumen(Float elem) {
     boolean result = volumen.remove(elem);
     return result;
   }
   public void clearAllVolumen() {
     volumen.clear();
   }
   public Iterator getAllVolumen() {return volumen.iterator(); }
   public List getVolumen() {return volumen; }
   public void setVolumen(List l) {volumen = l; }

}
