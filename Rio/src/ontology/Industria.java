package ontology;


import jade.util.leap.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#Industria
* @author OntologyBeanGenerator v4.1
* @version 2018/06/18, 13:01:05
*/
public interface Industria extends ProcesadorDeAgua {

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#TankCapacity
   */
   public void addTankCapacity(int elem);
   public boolean removeTankCapacity(int elem);
   public void clearAllTankCapacity();
   public Iterator getAllTankCapacity();
   public List getTankCapacity();
   public void setTankCapacity(List l);

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#EarningsPerProcess
   */
   public void addEarningsPerProcess(int elem);
   public boolean removeEarningsPerProcess(int elem);
   public void clearAllEarningsPerProcess();
   public Iterator getAllEarningsPerProcess();
   public List getEarningsPerProcess();
   public void setEarningsPerProcess(List l);

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#LitersPerProcess
   */
   public void addLitersPerProcess(int elem);
   public boolean removeLitersPerProcess(int elem);
   public void clearAllLitersPerProcess();
   public Iterator getAllLitersPerProcess();
   public List getLitersPerProcess();
   public void setLitersPerProcess(List l);

}
