import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Car extends Vehicle implements Actor {

    public Car(Lane lane, float speed , float positionOnLane, Turner turner, SimulationBoard simBoard) {
        super(lane, speed, positionOnLane, turner, simBoard);
    }

    @Override
    public void move() {
        ActorStrategy strategy = new AvoidCrash(turner, new RandomMove(turner), simBoard.getCarPoints());
        Action newAction = strategy.nextMove(positionOnLane, speed, currentLane);

        setPositionOnLane(newAction.getNewPos());
        setCurrentLane(newAction.getNewLane());
    }


}