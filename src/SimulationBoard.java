import java.util.List;

public class SimulationBoard {
    private List<Lane> lanes;
    private List<Car> cars;
    private static final SimulationBoard instance = new SimulationBoard();

    private SimulationBoard() {
    this.lanes = new java.util.ArrayList<>();
    this.cars = new java.util.ArrayList<>();
    }

    public static SimulationBoard getInstance() {
        return instance;
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
