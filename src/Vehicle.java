public abstract class Vehicle {
    protected Lane currentLane;
    protected float positionOnLane;
    protected float speed;
    protected Turner turner;
    protected SimulationBoard simBoard;

    public Vehicle(Lane lane, float speed, float positionOnLane, Turner turner, SimulationBoard simBoard) {
        this.currentLane = lane;
        this.speed = speed;
        this.positionOnLane = positionOnLane;
        this.turner = turner;
        this.simBoard = simBoard;
    }

    public abstract void move();

    public Lane getCurrentLane() {
        return currentLane;
    }

    public float getPositionOnLane() {
        return positionOnLane;
    }

    public void setPositionOnLane(float positionOnLane) {
        this.positionOnLane = positionOnLane;
    }

    public void setCurrentLane(Lane currentLane) {
        this.currentLane = currentLane;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}