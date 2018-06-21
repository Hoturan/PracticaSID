package ontology;

public class Depuradora{
    
    private int ticksPerProcess;
    private int position;
    private int tankCapacity;
    private int ticksLeft;
    private MasaDeAgua aguaDepuradora;
    
    public Depuradora(){
        aguaDepuradora = new MasaDeAgua();
        aguaDepuradora.setVolumen(0);
    }

    public int getTicksPerProcess() {
        return ticksPerProcess;
    }

    public void setTicksPerProcess(int ticksPerProcess) {
        this.ticksLeft = ticksPerProcess;
        this.ticksPerProcess = ticksPerProcess;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTankCapacity() {
        return tankCapacity;
    }

    public void setTankCapacity(int tankCapacity) {
        this.tankCapacity = tankCapacity;
    }
  
    public int getWaterAmount(){
        return aguaDepuradora.getVolumen();
    }

    public int getTicksLeft() {
        return ticksLeft;
    }

    public void setTicksLeft(int ticksLeft) {
        this.ticksLeft = ticksLeft;
    }
    
    public void restartTicksLeft(){
        ticksLeft = ticksPerProcess;
    }
    
    public void addFilthyWater(MasaDeAgua newWater){
        aguaDepuradora.mezclaMasasDeAgua(newWater);
    }
    
    public void descargaAgua(int liters){
        if(liters == aguaDepuradora.getVolumen()){
            aguaDepuradora = new MasaDeAgua();
            aguaDepuradora.setVolumen(0);
        }
        else aguaDepuradora.setVolumen(aguaDepuradora.getVolumen() - liters);
    }
    
    public boolean aguaDepurada(){
        return aguaDepuradora.aguaLimpia();
    }
    
    public void limpiaAgua(){
        aguaDepuradora.descontaminaAgua();
    }
    
    public int getGradoContaminacion(){
        return aguaDepuradora.getGradoContaminacion();
    }
}