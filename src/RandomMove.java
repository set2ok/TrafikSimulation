import java.util.List;
import java.util.Random;

public class RandomMove implements ActorStrategy{
    Turner turner = null;

    public RandomMove(Turner turner){
        this.turner = turner;
    }

    @Override
    public Action nextMove(float pos, float speed, Lane lane) {
        float newPositionOnLane = newPosition(pos, speed, lane);
        List<Intersection> intersections = turner.getTurns(pos, newPositionOnLane, lane);
        if (!intersections.isEmpty()) {
            Random rand = new Random();
            Intersection intersection = intersections.get(rand.nextInt(intersections.size()));
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
                lane = intersection.getEndLanes().get(index);
                float newPosition = intersection.getEndLanePositions().get(index);
                newPositionOnLane = newPosition; // Move the car to the new lane at the
            }

        }

        return new Action(newPositionOnLane, lane);
    }

    private float newPosition(float pos, float speed, Lane lane){
        // TODO: ADD DRIVING BEHAVIOR - if there is a car in front of this car, slow down or stop
        float newPosition = pos + (float) (speed / lane.getLength());
        if (newPosition > 1f) {
            newPosition = 1f; // Cap the position at the end of the lane
        }
        if (newPosition < 0f) {
            newPosition = 0f; // Ensure the position does not go below the start of the lane
        }
        return newPosition;
    }
}
