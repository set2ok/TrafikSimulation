import java.util.List;

public abstract class Vehicle implements Actor {
    protected Lane currentLane;
    protected float positionOnLane;
    protected float speed;
    protected Turner turner;

    public Vehicle(Lane lane, float speed, float positionOnLane, Turner turner) {
        this.currentLane = lane;
        this.speed = speed;
        this.positionOnLane = positionOnLane;
        this.turner = turner;
    }

    public abstract void move(List<Point> points);

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