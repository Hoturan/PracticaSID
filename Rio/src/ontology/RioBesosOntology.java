// file: RioBesosOntology.java generated by ontology bean generator.  DO NOT EDIT, UNLESS YOU ARE REALLY SURE WHAT YOU ARE DOING!
package ontology;

import jade.content.onto.*;
import jade.content.schema.*;

/** file: RioBesosOntology.java
 * @author OntologyBeanGenerator v4.1
 * @version 2018/06/18, 13:01:05
 */
public class RioBesosOntology extends jade.content.onto.Ontology  {

  private static final long serialVersionUID = 3688839762589084456L;

  //NAME
  public static final String ONTOLOGY_NAME = "rioBesos";
  // The singleton instance of this ontology
  private static Ontology theInstance = new RioBesosOntology();
  public static Ontology getInstance() {
     return theInstance;
  }


   // VOCABULARY
    public static final String PROCESADORDEAGUA="ProcesadorDeAgua";
    public static final String INDUSTRIA_LITERSPERPROCESS="LitersPerProcess";
    public static final String INDUSTRIA_EARNINGSPERPROCESS="EarningsPerProcess";
    public static final String INDUSTRIA_TANKCAPACITY="TankCapacity";
    public static final String INDUSTRIA="Industria";
    public static final String DEPURADORA_TANKCAPACITY="TankCapacity";
    public static final String DEPURADORA="Depuradora";
    public static final String RIO="Rio";
    public static final String METODOS_FUNCNAME="FuncName";
    public static final String METODOS="Metodos";
    public static final String AVANZARCURSORIO="AvanzarCursoRio";
    public static final String EXTRAERAGUA="ExtraerAgua";
    public static final String LIMPIARAGUA="LimpiarAgua";
    public static final String ENSUCIARAGUA="EnsuciarAgua";
    public static final String DESCARGARAGUA="DescargarAgua";
    public static final String VERTERAGUA="VerterAgua";
    public static final String CONTAMINADA="Contaminada";
    public static final String MASADEAGUA_VOLUMEN="Volumen";
    public static final String MASADEAGUA_INDICADORAGUA="IndicadorAgua";
    public static final String MASADEAGUA="MasaDeAgua";
    public static final String TRAMO_SIGUIENTETRAMO="SiguienteTramo";
    public static final String TRAMO_TRAMOID="TramoID";
    public static final String TRAMO="Tramo";
    public static final String LIMPIA="Limpia";

  /**
   * Constructor
  */
  private RioBesosOntology(){ 
    super(ONTOLOGY_NAME, BasicOntology.getInstance());
    try { 

    // adding Concept(s)
    ConceptSchema limpiaSchema = new ConceptSchema(LIMPIA);
    add(limpiaSchema, ontology.Limpia.class);
    ConceptSchema tramoSchema = new ConceptSchema(TRAMO);
    add(tramoSchema, ontology.Tramo.class);
    ConceptSchema masaDeAguaSchema = new ConceptSchema(MASADEAGUA);
    add(masaDeAguaSchema, ontology.MasaDeAgua.class);
    ConceptSchema contaminadaSchema = new ConceptSchema(CONTAMINADA);
    add(contaminadaSchema, ontology.Contaminada.class);

    // adding AgentAction(s)
    AgentActionSchema verterAguaSchema = new AgentActionSchema(VERTERAGUA);
    add(verterAguaSchema, ontology.VerterAgua.class);
    AgentActionSchema descargarAguaSchema = new AgentActionSchema(DESCARGARAGUA);
    add(descargarAguaSchema, ontology.DescargarAgua.class);
    AgentActionSchema ensuciarAguaSchema = new AgentActionSchema(ENSUCIARAGUA);
    add(ensuciarAguaSchema, ontology.EnsuciarAgua.class);
    AgentActionSchema limpiarAguaSchema = new AgentActionSchema(LIMPIARAGUA);
    add(limpiarAguaSchema, ontology.LimpiarAgua.class);
    AgentActionSchema extraerAguaSchema = new AgentActionSchema(EXTRAERAGUA);
    add(extraerAguaSchema, ontology.ExtraerAgua.class);
    AgentActionSchema avanzarCursoRioSchema = new AgentActionSchema(AVANZARCURSORIO);
    add(avanzarCursoRioSchema, ontology.AvanzarCursoRio.class);
    AgentActionSchema metodosSchema = new AgentActionSchema(METODOS);
    add(metodosSchema, ontology.Metodos.class);

    // adding AID(s)
    ConceptSchema rioSchema = new ConceptSchema(RIO);
    add(rioSchema, ontology.Rio.class);
    ConceptSchema depuradoraSchema = new ConceptSchema(DEPURADORA);
    add(depuradoraSchema, ontology.Depuradora.class);
    ConceptSchema industriaSchema = new ConceptSchema(INDUSTRIA);
    add(industriaSchema, ontology.Industria.class);
    ConceptSchema procesadorDeAguaSchema = new ConceptSchema(PROCESADORDEAGUA);
    add(procesadorDeAguaSchema, ontology.ProcesadorDeAgua.class);

    // adding Predicate(s)


    // adding fields
    tramoSchema.add(TRAMO_TRAMOID, (TermSchema)getSchema(BasicOntology.INTEGER), 0, ObjectSchema.UNLIMITED);
    tramoSchema.add(TRAMO_SIGUIENTETRAMO, tramoSchema, 0, ObjectSchema.UNLIMITED);
    masaDeAguaSchema.add(MASADEAGUA_INDICADORAGUA, (TermSchema)getSchema(BasicOntology.STRING), 0, ObjectSchema.UNLIMITED);
    masaDeAguaSchema.add(MASADEAGUA_VOLUMEN, (TermSchema)getSchema(BasicOntology.FLOAT), 0, ObjectSchema.UNLIMITED);
    metodosSchema.add(METODOS_FUNCNAME, (TermSchema)getSchema(BasicOntology.STRING), 0, ObjectSchema.UNLIMITED);
    depuradoraSchema.add(DEPURADORA_TANKCAPACITY, (TermSchema)getSchema(BasicOntology.INTEGER), 0, ObjectSchema.UNLIMITED);
    industriaSchema.add(INDUSTRIA_TANKCAPACITY, (TermSchema)getSchema(BasicOntology.INTEGER), 0, ObjectSchema.UNLIMITED);
    industriaSchema.add(INDUSTRIA_EARNINGSPERPROCESS, (TermSchema)getSchema(BasicOntology.INTEGER), 0, ObjectSchema.UNLIMITED);
    industriaSchema.add(INDUSTRIA_LITERSPERPROCESS, (TermSchema)getSchema(BasicOntology.INTEGER), 0, ObjectSchema.UNLIMITED);

    // adding name mappings

    // adding inheritance
    limpiaSchema.addSuperSchema(masaDeAguaSchema);
    contaminadaSchema.addSuperSchema(masaDeAguaSchema);
    verterAguaSchema.addSuperSchema(metodosSchema);
    descargarAguaSchema.addSuperSchema(metodosSchema);
    ensuciarAguaSchema.addSuperSchema(metodosSchema);
    limpiarAguaSchema.addSuperSchema(metodosSchema);
    extraerAguaSchema.addSuperSchema(metodosSchema);
    avanzarCursoRioSchema.addSuperSchema(metodosSchema);
    depuradoraSchema.addSuperSchema(procesadorDeAguaSchema);
    industriaSchema.addSuperSchema(procesadorDeAguaSchema);

   }catch (java.lang.Exception e) {e.printStackTrace();}
  }
}