package ontology;

public class MasaDeAgua{
	
    private int volumen;					// Millon/es de litros
    private float solidosEnSuspension;                          // mg/l
    private float demandaBiologicaDeOxigeno;                    // mg/l
    private float demandaQuimicaDeOxigeno;                      // mg/l
    private float totalNitratos;				// mg/l
    private float totalSulfatos;				// mg/l
    private int posicion;
    private int gradoContaminacion;

    public MasaDeAgua(){
        solidosEnSuspension = demandaQuimicaDeOxigeno = demandaBiologicaDeOxigeno = 
        totalSulfatos = totalNitratos = 0.0f;
        volumen = 1000000;
        posicion = gradoContaminacion = 0;
    }
    
    public int getVolumen() {
        return volumen;
    }

    public void setVolumen(int volumen) {
        this.volumen = volumen;
    }

    public float getSolidosEnSuspension() {
        return solidosEnSuspension;
    }

    public void setSolidosEnSuspension(float solidosEnSuspension) {
        this.solidosEnSuspension = solidosEnSuspension;
    }

    public float getDemandaBiologicaDeOxigeno() {
        return demandaBiologicaDeOxigeno;
    }

    public void setDemandaBiologicaDeOxigeno(float demandaBiologicaDeOxigeno) {
        this.demandaBiologicaDeOxigeno = demandaBiologicaDeOxigeno;
    }

    public float getDemandaQuimicaDeOxigeno() {
        return demandaQuimicaDeOxigeno;
    }

    public void setDemandaQuimicaDeOxigeno(float demandaQuimicaDeOxigeno) {
        this.demandaQuimicaDeOxigeno = demandaQuimicaDeOxigeno;
    }

    public float getTotalNitratos() {
        return totalNitratos;
    }

    public void setTotalNitratos(float totalNitratos) {
        this.totalNitratos = totalNitratos;
    }

    public float getTotalSulfatos() {
        return totalSulfatos;
    }

    public void setTotalSulfatos(float totalSulfatos) {
        this.totalSulfatos = totalSulfatos;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public void contaminaAgua(){
        this.solidosEnSuspension += 50.0f;
        this.demandaBiologicaDeOxigeno += 50.0f;
        this.demandaQuimicaDeOxigeno += 50.0f;
        this.totalNitratos += 50.0f;
        this.totalSulfatos += 50.0f;
        this.gradoContaminacion++; 
    }

    public void descontaminaAgua(){
        this.solidosEnSuspension -= 50.0f;
        this.demandaBiologicaDeOxigeno -= 50.0f;
        this.demandaQuimicaDeOxigeno -= 50.0f;
        this.totalNitratos -= 50.0f;
        this.totalSulfatos -= 50.0f;
        this.gradoContaminacion--;
        
    }

    public void mezclaMasasDeAgua(MasaDeAgua m){
        this.volumen += m.volumen;
        this.solidosEnSuspension += m.solidosEnSuspension;
        this.demandaQuimicaDeOxigeno += m.demandaBiologicaDeOxigeno;
        this.demandaQuimicaDeOxigeno += m.demandaQuimicaDeOxigeno;
        this.totalNitratos += m.totalNitratos;
        this.totalSulfatos += m.totalSulfatos;
        this.gradoContaminacion += m.gradoContaminacion;
    }

    public boolean aguaLimpia(){
        return gradoContaminacion == 0;
    }

    public void aumentarPosicion(){
        posicion++;
    }
     
    public void contaminaAgua(int gradoContaminacion){
        this.solidosEnSuspension = 50.0f * gradoContaminacion;
        this.demandaBiologicaDeOxigeno = 50.0f * gradoContaminacion;
        this.demandaQuimicaDeOxigeno = 50.0f * gradoContaminacion;
        this.totalNitratos = 50.0f * gradoContaminacion;
        this.totalSulfatos = 50.0f * gradoContaminacion;
        this.gradoContaminacion = gradoContaminacion;
    }

    int getGradoContaminacion() {
        return this.gradoContaminacion;
    }
}