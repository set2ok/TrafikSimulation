import java.util.List;

public class Main {
    public static void main(String[] args) {
        SimulationBoard simBoard = SimulationBoard.getInstance();
        SimulationController controller = new SimulationController(simBoard);
        simBoard.addLane(new Lane(new Point(0,0), new Point(100, 100), 10));
        simBoard.addLane(new Lane(new Point(0,50), new Point(80, 50), 10));
        simBoard.addLane(new Lane(new Point(50,0), new Point(50, 80), 10));
        controller.createIntersections();
        controller.printIntersections();
        SimulationGui gui = new SimulationGui(simBoard, controller);
    }

}