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

        // Check for intersections between lanes
        for (int i = 0; i < lanes.size(); i++) {
            for (int j = 0; j < lanes.size(); j++) {
                if (i != j) {
                    // Logic to check if lane i and lane j intersect
                    float[] intersectionPoints = calculateIntersectionPoints(lanes.get(i), lanes.get(j));
                    if (intersectionPoints != null) {

                        intersections.add(new Intersection(lanes.get(i), List.of(lanes.get(j)), intersectionPoints[0], intersectionPoints[1]));
                    }
                }

            }
        }
    }

    private float[] calculateIntersectionPoints(Lane lane1, Lane lane2) {
        // calculate the denominator
        float D = (lane1.getStart().getX() - lane1.getEnd().getX()) * (lane2.getStart().getY() - lane2.getEnd().getY()) - (lane1.getStart().getY() - lane1.getEnd().getY()) * (lane2.getStart().getX() - lane2.getEnd().getX());
        if (D == 0) {
            return null; // Lines are parallel
        }
        // calculate the intersection point as a procent for lane1 and lane2
        float lane1Point = ((lane1.getStart().getX() - lane2.getStart().getX()) * (lane2.getStart().getY() - lane2.getEnd().getY())
                - (lane1.getStart().getY() - lane2.getStart().getY()) * (lane2.getStart().getX() - lane2.getEnd().getX())) / D;
        float lane2Point = ((lane1.getStart().getX() - lane1.getEnd().getX())*(lane1.getStart().getY() - lane2.getStart().getY())
                - (lane1.getStart().getY() - lane1.getEnd().getY())*(lane1.getStart().getX() - lane2.getStart().getX())) / D;
        if (lane1Point < 0 || lane1Point > 1 || lane2Point < 0 || lane2Point > 1) {
            return null; // Intersection point is outside the line segments
        }
        return new float[]{lane1Point, lane2Point};
    }



    public List<Intersection> getIntersections() {
        return intersections;
    }
}
