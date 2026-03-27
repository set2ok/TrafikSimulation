import java.util.ArrayList;
import java.util.List;
public class Turner {
    List<Intersection> intersections;
    public Turner() {
        // private to prevent external instantiation
    }
    public void createIntersections(List<Lane> lanes) {
        intersections = new ArrayList<>();

        // generate function for lane
        List<float[]> laneFunctions = new ArrayList<>();
        for (int i = 0; i < lanes.size(); i++) {
            laneFunctions.add(generateLaneFunction(lanes.get(i)));
        }
        // Check for intersections between lanes
        for (int i = 0; i < lanes.size(); i++) {
            for (int j = 0; j < lanes.size(); j++) {
                if (i != j) {
                    // Logic to check if lane i and lane j intersect
                    Point intersectionPoint = calculateIntersectionPoint(laneFunctions.get(i), laneFunctions.get(j));
                    if (intersectionPoint != null && pointOnLane(lanes.get(i), intersectionPoint) && pointOnLane(lanes.get(j), intersectionPoint)) {
                        List<Lane> endLanes = new ArrayList<>();
                        endLanes.add(lanes.get(j));
                        intersections.add(new Intersection(lanes.get(i),endLanes, intersectionPoint));

                        endLanes.remove(lanes.get(j));
                        endLanes.add(lanes.get(i));
                        intersections.add(new Intersection(lanes.get(j),endLanes, intersectionPoint));
                    }

                }
            }
        }

    }

    private Boolean pointOnLane(Lane lane, Point point){
        if (point.getX() < Math.min(lane.getStart().getX(), lane.getEnd().getX()) &&
            point.getX() > Math.max(lane.getStart().getX(), lane.getEnd().getX()) &&
            point.getY() < Math.min(lane.getStart().getY(), lane.getEnd().getY()) &&
            point.getY() > Math.max(lane.getStart().getY(), lane.getEnd().getY())) {
            return false; // Point is outside the bounding box of the lane
        }
        else return true; // Point is within the bounding box of the lane
    }

    private Point calculateIntersectionPoint(float[] func1, float[] func2) {
        // Logic to calculate the intersection point of two lines given their functions
        float k1 = func1[0];
        float b1 = func1[1];
        float k2 = func2[0];
        float b2 = func2[1];

        if (k1 == k2) {
            return null; // Lines are parallel, no intersection
        }

        float x = (b2 - b1) / (k1 - k2); // Calculate x coordinate of intersection
        float y = (k1 * x + b1); // Calculate y coordinate of intersection using one of the line equations

        return new Point(x, y); // Return the intersection point
    }

    private float[] generateLaneFunction(Lane lane) {
        // Logic to generate a function representing the lane's path
        float dx = lane.getEnd().getX() - lane.getStart().getX();
        float dy = lane.getEnd().getY() - lane.getStart().getY();
        if (dx == 0) {
            return new float[]{Float.MAX_VALUE, lane.getStart().getX()}; // Vertical line case
        }
        if (dy == 0) {
            return new float[]{0, lane.getStart().getY()}; // Horizontal line case
        }
        float k = (dy / dx); // Slope of the lane
        float b = (lane.getStart().getY() - k * lane.getStart().getX()); // Y-intercept of the lane
        return new float[]{k, b}; // Return the function parameters as an array
    }

    public void printIntersections() {
        for (Intersection i : intersections) {
            System.out.println("Intersection at: " + i.getIntersectionPoint().getX() + ", " + i.getIntersectionPoint().getY());
        }
    }

    public List<Intersection> getIntersections() {
        return intersections;
    }
}
