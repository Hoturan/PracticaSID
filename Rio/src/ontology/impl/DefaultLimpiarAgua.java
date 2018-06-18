package ontology.impl;


import jade.util.leap.*;
import ontology.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#LimpiarAgua
* @author OntologyBeanGenerator v4.1
* @version 2018/06/18, 13:01:05
*/
public class DefaultLimpiarAgua implements LimpiarAgua {

  private static final long serialVersionUID = 3688839762589084456L;

  private String _internalInstanceName = null;

  public DefaultLimpiarAgua() {
    this._internalInstanceName = "";
  }

  public DefaultLimpiarAgua(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1529152759.owl#FuncName
   */
   private List funcName = new ArrayList();
   public void addFuncName(String elem) { 
     funcName.add(elem);
   }
   public boolean removeFuncName(String elem) {
     boolean result = funcName.remove(elem);
     return result;
   }
   public void clearAllFuncName() {
     funcName.clear();
   }
   public Iterator getAllFuncName() {return funcName.iterator(); }
   public List getFuncName() {return funcName; }
   public void setFuncName(List l) {funcName = l; }

}
