import java.util.List;

public class Main {
    public static void main(String[] args) {
        SimulationBoard simBoard = SimulationBoard.getInstance();
        SimulationController controller = new SimulationController(simBoard);
        LanesGenerator generator = new LanesGenerator(simBoard);
        generator.generateMainLanes(4, 100);
        controller.createIntersections();
        controller.printIntersections();
        SimulationGui gui = new SimulationGui(simBoard, controller);
    }

}