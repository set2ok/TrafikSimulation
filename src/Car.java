import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Car extends Vehicle implements Actor {

    public Car(Lane lane, float speed , float positionOnLane, Turner turner) {
        super(lane, speed, positionOnLane, turner);
    }

    @Override
    public void move() {
        float newPositionOnLane = newPosition(positionOnLane);
        List<Intersection> intersections = turner.getTurns(positionOnLane, newPositionOnLane, currentLane);
        if (!intersections.isEmpty()) {
            Random rand = new Random();
            Intersection intersection = intersections.get(rand.nextInt(intersections.size())); // Assuming we take the first intersection for simplicity
            int index = 0;
            int dif = 1;
            if (intersection.getStartLanePosition() >=1 ){
                index = rand.nextInt(intersection.getEndLanes().size());
                dif = 0;
            }
            else{
                index = rand.nextInt(intersection.getEndLanes().size() + 1);

            }

            if (index <= intersection.getEndLanes().size()-dif) {
                Lane newLane = intersection.getEndLanes().get(index);
                float newPosition = intersection.getEndLanePositions().get(index);
                setCurrentLane(newLane);
                newPositionOnLane = newPosition; // Move the car to the new lane at the
            }

        }

        setPositionOnLane(newPositionOnLane);
    }
    private float newPosition(float pos){
        float newPosition = pos + (float) (speed / currentLane.getLength());
        if (newPosition > 1) {
            newPosition = 1; // Cap the position at the end of the lane
        }
        if (newPosition < 0) {
            newPosition = 0; // Ensure the position does not go below the start of the lane
        }
        return newPosition;
    }

}