import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Car extends Vehicle implements Actor {

    public Car(Lane lane, float speed , float positionOnLane, Turner turner) {
        super(lane, speed, positionOnLane, turner);
    }

    @Override
    public void move(List<Point> points) {
        ActorStrategy strategy = new AvoidCrash(turner, new RandomMove(turner), points);
        Action newAction = strategy.nextMove(positionOnLane, speed, currentLane);

        setPositionOnLane(newAction.getNewPos());
        setCurrentLane(newAction.getNewLane());
    }


}