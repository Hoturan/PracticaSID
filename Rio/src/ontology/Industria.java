package ontology;


public class Industria{
    
    private int tankCapacity;
    private int position;
    private int litersPerProcess;
    private int earningsPerProcess;
    private int earnings;
    private int lWater;
    private int lWaste;
    
    public Industria(){
        earnings = lWaste = lWater = 0;
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
        return lWater;
    }

    public void setlWater(int lWater) {
        this.lWater = lWater;
    }

    public int getlWaste() {
        return lWaste;
    }

    public void setlWaste(int lWaste) {
        this.lWaste = lWaste;
    }

    public void generateEarnings(){
        this.earnings += earningsPerProcess;
    }
    
}
