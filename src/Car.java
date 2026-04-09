public class Car implements Actor {
    private Lane currentLane;
    private int positionOnLane; // Position of the car on the lane
    private int speed; // Speed of the car (units per time step)

    public Car(Lane lane, int speed) {
        this.currentLane = lane;
        this.speed = speed;
        this.positionOnLane = 0; // Start at the beginning of the lane
    }

    public void move() {
        // Move the car forward based on its speed
        int newPosition = positionOnLane + speed;
        setPositionOnLane(newPosition);
    }

    public Lane getCurrentLane() {
        return currentLane;
    }

    public int getPositionOnLane() {
        return positionOnLane;
    }

    public void setPositionOnLane(int positionOnLane) {
        this.positionOnLane = positionOnLane;
    }

    public void setCurrentLane(Lane currentLane) {
        this.currentLane = currentLane;
    }
}
