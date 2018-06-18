package ontology;


import jade.util.leap.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#MasaDeAgua
* @author OntologyBeanGenerator v4.1
* @version 2018/06/18, 13:01:05
*/
public interface MasaDeAgua extends jade.content.Concept {

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#IndicadorAgua
   */
   public void addIndicadorAgua(String elem);
   public boolean removeIndicadorAgua(String elem);
   public void clearAllIndicadorAgua();
   public Iterator getAllIndicadorAgua();
   public List getIndicadorAgua();
   public void setIndicadorAgua(List l);

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#Volumen
   */
   public void addVolumen(Float elem);
   public boolean removeVolumen(Float elem);
   public void clearAllVolumen();
   public Iterator getAllVolumen();
   public List getVolumen();
   public void setVolumen(List l);

}
