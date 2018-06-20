package ontology;

public class MasaDeAgua{
	
    private int volumen;					// Millon/es de litros
    private float solidosEnSuspension;                          // mg/l
    private float demandaBiologicaDeOxigeno;                    // mg/l
    private float demandaQuimicaDeOxigeno;                      // mg/l
    private float totalNitratos;				// mg/l
    private float totalSulfatos;				// mg/l
    private int posicion;

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

    public MasaDeAgua(){
            solidosEnSuspension = demandaQuimicaDeOxigeno = demandaBiologicaDeOxigeno = 
                    totalSulfatos = totalNitratos = 0.0f;
            volumen = 1;
            posicion = 0;
    }

    public void contaminaAgua(){
            this.solidosEnSuspension += 50.0f;
            this.demandaBiologicaDeOxigeno += 50.0f;
            this.demandaQuimicaDeOxigeno += 50.0f;
            this.totalNitratos += 50.0f;
            this.totalSulfatos += 50.0f;
    }

    public void descontaminaAgua(){
            this.solidosEnSuspension -= 50.0f;
            this.demandaBiologicaDeOxigeno -= 50.0f;
            this.demandaQuimicaDeOxigeno -= 50.0f;
            this.totalNitratos -= 50.0f;
            this.totalSulfatos -= 50.0f;
    }

    public void mezclaMasasDeAgua(MasaDeAgua m){
            this.volumen += m.volumen;
            this.solidosEnSuspension += m.solidosEnSuspension;
            this.demandaQuimicaDeOxigeno += m.demandaBiologicaDeOxigeno;
            this.demandaQuimicaDeOxigeno += m.demandaQuimicaDeOxigeno;
            this.totalNitratos += m.totalNitratos;
            this.totalSulfatos += m.totalSulfatos;
    }

    public boolean aguaLimpia(){
            return (solidosEnSuspension == 0.0f && 
                    demandaQuimicaDeOxigeno == 0.0f &&
                    demandaBiologicaDeOxigeno == 0.0f &&
                    totalNitratos == 0.0f &&
                    totalSulfatos == 0.0f);
    }

    public void aumentarPosicion(){
            posicion++;
        }
}