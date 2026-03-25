import java.util.List;

public class SimulationController {
    private final SimulationBoard board;

    public SimulationController(SimulationBoard board) {
        this.board = board;
    }

    // Advance one simulation step
    public void step() {
        List<Car> cars = board.getCars();
        for (Car c : cars) {
            continue;
        }
    }
}