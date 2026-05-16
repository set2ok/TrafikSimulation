import java.util.List;

public class Main {
    public static void main(String[] args) {
        SimulationBoard simBoard = SimulationBoard.getInstance();
        SimulationController controller = new SimulationController(simBoard);
        LanesGenerator generator = new LanesGenerator(controller);
        generator.generateLanes(4, 500);
        controller.createIntersections();
        controller.printIntersections();
        SimulationGui gui = new SimulationGui(simBoard, controller);
    }

}