import java.util.List;

public class SimulationController {
    private Turner turner = new Turner();
    private SimulationBoard simBoard;

    public SimulationController(SimulationBoard simBoard){
        this.simBoard = simBoard;
    }

    public List<Point> getPointsForIntersection() {
        List<Intersection> intersections = turner.getIntersections();
        List<Point> points = new java.util.ArrayList<>();
        for (Intersection i : intersections) {
            Point p = i.getIntersectionPoint();
            points.add(p);
        }
        return points;
    }

    public void createIntersections() {
        turner.createIntersections(simBoard.getLanes());
    }
    public void printIntersections() {
        turner.printIntersections();

    }

    public void addCar(Lane lane, float position, float speed) {
        Car car = new Car(lane, speed, position, turner);
        simBoard.addCar(car);
    }

    // Advance one simulation step
    public void step() {
        List<Car> cars = simBoard.getCars();
        for (Car c : cars) {
            c.move();
        }
    }
}