package ontology;


import jade.util.leap.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#Depuradora
* @author OntologyBeanGenerator v4.1
* @version 2018/06/18, 13:01:05
*/
public interface Depuradora extends ProcesadorDeAgua {

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#TankCapacity
   */
   public void addTankCapacity(int elem);
   public boolean removeTankCapacity(int elem);
   public void clearAllTankCapacity();
   public Iterator getAllTankCapacity();
   public List getTankCapacity();
   public void setTankCapacity(List l);

}
