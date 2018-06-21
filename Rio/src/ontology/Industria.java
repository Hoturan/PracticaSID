package ontology;


public class Industria{
    
    private int tankCapacity;
    private int position;
    private int litersPerProcess;
    private int earningsPerProcess;
    private int earnings;
    private MasaDeAgua aguaLimpia, aguaSucia;
    
    public Industria(){
        earnings = 0;
        aguaLimpia = new MasaDeAgua();
        aguaSucia = new MasaDeAgua();
        aguaLimpia.setVolumen(0);
        aguaSucia.setVolumen(0);
    }
    
    public int getLitersPerProcess() {
        return litersPerProcess;
    }

    public void setLitersPerProcess(int litersPerProcess) {
        this.litersPerProcess = litersPerProcess;
    }

    public int getEarningsPerProcess() {
        return earningsPerProcess;
    }

    public void setEarningsPerProcess(int earningsPerProcess) {
        this.earningsPerProcess = earningsPerProcess;
    }
      
    public int getPosition(){
        return position;
    }
    
    public void setPosition(int position){
        this.position = position;
    }

    public int getTankCapacity() {
        return tankCapacity;
    }

    public void setTankCapacity(int tankCapacity) {
        this.tankCapacity = tankCapacity;
    }

    public int getEarnings() {
        return earnings;
    }

    public void setEarnings(int earnings) {
        this.earnings = earnings;
    }

    public int getlWater() {
        return aguaLimpia.getVolumen();
    }

    public void setlWater(int lWater) {
        this.aguaLimpia.setVolumen(lWater);
    }

    public int getlWaste() {
        return aguaSucia.getVolumen();
    }

    public void setlWaste(int lWaste) {
        this.aguaSucia.setVolumen(lWaste);
    }

    public void generateEarnings(){
        this.earnings += earningsPerProcess;
    }
    
    public void processWater(){
        aguaLimpia.setVolumen(aguaLimpia.getVolumen() - litersPerProcess);
        aguaSucia.setVolumen(aguaSucia.getVolumen() + litersPerProcess);
        aguaSucia.contaminaAgua();
    }
    
    public int getGradoContaminacion(){
        return aguaSucia.getGradoContaminacion();
    }
    
    public void addCleanWater(int litros){
        aguaLimpia.setVolumen(aguaLimpia.getVolumen() + litros);
    }
    
    public int reduceFilthyWater(int litrosEnviados){
        int litersLeft = aguaSucia.getVolumen() - litrosEnviados;
        aguaSucia = new MasaDeAgua();
        aguaSucia.setVolumen(0);
        return litersLeft;

    }
}
