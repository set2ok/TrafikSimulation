import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class LanesGenerator {
    SimulationBoard board;
    Random rand;

    public LanesGenerator(SimulationBoard board) {
        this.board = board;
        this.rand = new Random();
    }

    public void generateMainLanes(int amount){
        int i = 0;
        while (i < amount) {
            int x1 = rand.nextInt(1000);
            int y1 = rand.nextInt(1000);
            int x2 = rand.nextInt(1000);
            int y2 = rand.nextInt(1000);
            Lane lane = new Lane(new Point(x1, y1), new Point(x2, y2), 10);
            if (lane.getLength() > 500) {

                board.addLane(lane);
                i++;
            }
        }

    }
}
