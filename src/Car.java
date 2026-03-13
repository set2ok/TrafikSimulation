public class Car implements Actor{
    Lane currentLane;
    int positionOnLane; // Position of the car on the lane

    public Car(Lane lane) {
        this.currentLane = lane;
        this.positionOnLane = 0; // Start at the beginning of the lane
    }

    public void move() {
        // Implement the logic for moving the car
    }
}
