package ontology;

public class Depuradora{
    
    private int ticksPerProcess;
    private int position;
    private int tankCapacity;
    private int lWater;
    private int lWaste;
    private int ticksLeft;
    
    public Depuradora(){
        lWater = lWaste = 0;
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

    public int getTicksLeft() {
        return ticksLeft;
    }

    public void setTicksLeft(int ticksLeft) {
        this.ticksLeft = ticksLeft;
    }
    
    public void restartTicksLeft(){
        ticksLeft = ticksPerProcess;
    }
}