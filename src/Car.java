public class Car implements Actor {
    private Lane currentLane;
    private float positionOnLane; // Position of the car on the lane
    private int speed; // Speed of the car (units per time step)

    public Car(Lane lane, int speed) {
        this.currentLane = lane;
        this.speed = speed;
        this.positionOnLane = 0; // Start at the beginning of the lane
    }

    public void move() {
        // Move the car forward based on its speed
        float newPosition = positionOnLane + (float) (speed / currentLane.getLength());
        setPositionOnLane(newPosition);
    }

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
}
