import java.util.Random;

public class LanesGenerator {
    SimulationBoard board;
    Random rand;


    public LanesGenerator(SimulationBoard board) {
        this.board = board;
        this.rand = new Random();
    }

    public Lane generateParallelLine(Lane lane, int distance, int direction, int side){
        Point start = lane.getStart();
        Point end = lane.getEnd();
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        double length = Math.sqrt(dx * dx + dy * dy);
        double unitX = dx / length;
        double unitY = dy / length;
        double offsetX = unitY * distance;
        double offsetY =  -unitX * distance;
        if (side == -1) {
            offsetX = -offsetX;
            offsetY = -offsetY;
        }

        if (direction >= 1) {
            Point newStart = new Point(start.getX() + offsetX, start.getY() + offsetY);
            Point newEnd = new Point(end.getX() + offsetX, end.getY() + offsetY);
            Lane parallelLane = new Lane(newStart, newEnd, lane.getWidth());
            return parallelLane;
        }
        else{
            Point newStart = new Point(end.getX() + offsetX, end.getY() + offsetY);
            Point newEnd = new Point(start.getX() + offsetX, start.getY() + offsetY);
            Lane parallelLane = new Lane(newStart, newEnd, lane.getWidth());
            return parallelLane;
        }

    }

    public void generateMainLanes(int amount, int boxSize){
        int i = 0;
        Lane[] tempLanes = new Lane[amount];
        for ( i = 0; i < amount; i++) {
            int x1 = rand.nextInt(boxSize);
            int y1 = rand.nextInt(boxSize);
            int x2 = rand.nextInt(boxSize);
            int y2 = rand.nextInt(boxSize);
            Lane lane = new Lane(new Point(x1, y1), new Point(x2, y2), 10);
            tempLanes[i] = lane;
        }
        Turner turner = new Turner();
        Lane[] lanes = new Lane[amount];
        for (int j = 0; j < tempLanes.length; j++) {
            float largestPoint =0;
            float smallestPoint = 1;
            for (int k = 0; k < tempLanes.length; k++) {
                float[] points = turner.calculateIntersectionPoints(tempLanes[j], tempLanes[k]);
                if (points != null){
                    if (points[0] > largestPoint) {
                        largestPoint = points[0];
                    }
                    if (points[0] < smallestPoint) {
                        smallestPoint = points[0];
                }
            }

        }
        lanes[j] = new Lane(tempLanes[j].getPointAtPosition(smallestPoint), tempLanes[j].getPointAtPosition(largestPoint), 10);
        }
    for (int j = 0; j < lanes.length; j++) {
        board.addLane(lanes[j]);
        i = j;
        while (i == j || lanes[j].getStart().equals(lanes[i].getStart())) {
            i = rand.nextInt(lanes.length);
        }
        Lane lane = new Lane(lanes[j].getEnd(), lanes[i].getStart(), 10);
        board.addLane(lane);
        }


    }
}
