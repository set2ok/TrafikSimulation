import java.util.List;
import java.util.ArrayList;

public class SimulationBoard {
    private List<Lane> lanes;
    private List<Car> cars;
    private static final SimulationBoard instance = new SimulationBoard();

    private SimulationBoard() {
    this.lanes = new ArrayList<>();
    this.cars = new ArrayList<>();
    }

    public static SimulationBoard getInstance() {
        return instance;
    }

    public List<Point> getCarPoints(){
        List<Point> carPoints = new ArrayList<>();
        for (Car car : cars){
            carPoints.add(car.getCurrentLane().getPointAtPosition(car.getPositionOnLane()));
        }
    return carPoints;
    }

    public void addCar(Car car) {
        this.cars.add(car);
    }

    public void addLane(Lane lane) {
        this.lanes.add(lane);
    }

    public List<Car> getCars() {
        return cars;
    }

    public List<Lane> getLanes() {
        return lanes;
    }
}
