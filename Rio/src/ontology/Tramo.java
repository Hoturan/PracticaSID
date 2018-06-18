package ontology;


import jade.util.leap.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#Tramo
* @author OntologyBeanGenerator v4.1
* @version 2018/06/18, 13:01:05
*/
public interface Tramo extends jade.content.Concept {

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#TramoID
   */
   public void addTramoID(int elem);
   public boolean removeTramoID(int elem);
   public void clearAllTramoID();
   public Iterator getAllTramoID();
   public List getTramoID();
   public void setTramoID(List l);

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#SiguienteTramo
   */
   public void addSiguienteTramo(Tramo elem);
   public boolean removeSiguienteTramo(Tramo elem);
   public void clearAllSiguienteTramo();
   public Iterator getAllSiguienteTramo();
   public List getSiguienteTramo();
   public void setSiguienteTramo(List l);

}
