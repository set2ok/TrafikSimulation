import java.util.List;

public class SimulationBoard {
    private List<Lane> Lanes;
    private List<Car> cars;


    public SimulationBoard(List<Lane> lanes, List<Car> cars) {
        this.Lanes = lanes;
        this.cars = cars;
    }
    public void addCar(Car car) {
        this.cars.add(car);
    }
    public void addLane(Lane lane) {
        this.Lanes.add(lane);
    }

    public List<Car> getCars() {
        return cars;
    }

    public List<Lane> getLanes() {
        return Lanes;
    }
}
