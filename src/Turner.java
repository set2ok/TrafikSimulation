import java.util.ArrayList;
import java.util.List;
public class Turner {
    List<Intersection> intersections;

    public Turner() {
    // SINGLETON??
    }
    public void createIntersections(List<Lane> lanes) {
        List<Intersection> allIntersections = new ArrayList<>();

        // Check for intersections between lanes
        for (int i = 0; i < lanes.size(); i++) {
            for (int j = 0; j < lanes.size(); j++) {
                if (i != j) {
                    // Logic to check if lane i and lane j intersect
                    float[] intersectionPoints = getIntersectionsForLanes(lanes.get(i), lanes.get(j));
                    if (intersectionPoints != null) {

                        allIntersections.add(new Intersection(lanes.get(i), List.of(lanes.get(j)), intersectionPoints[0], List.of(intersectionPoints[1])));
                    }
                }

            }
        }
        // remove duplicates by merging intersections that occur at the same point on the same lane
        List<Intersection> toRemove = new ArrayList<>();
        for (Intersection i : allIntersections) {
            if (!toRemove.contains(i)) {
                for (Intersection j : allIntersections) {
                    if (i != j && i.getStartLane() == j.getStartLane() &&
                            Math.abs(i.getStartLanePosition() - j.getStartLanePosition()) < 0.05f) {
                        i.mergeWith(j);
                        toRemove.add(j);
                    }
                }
            }
        }
        allIntersections.removeAll(toRemove);

        this.intersections = allIntersections;
    }

    private float[] getIntersectionsForLanes(Lane lane1, Lane lane2) {

        float[] intersectionPoints = calculateIntersectionPoints(lane1, lane2);

        if (intersectionPoints == null) {
            return null; // Lines are parallel, no intersection
        }

        if (intersectionPoints[0] >= -0.01 && intersectionPoints[0] <= 1 && intersectionPoints[1] >= 0 && intersectionPoints[1] <= 1.01) {
            return intersectionPoints;
        }

        return null; // Intersection point is outside the line segments
    }

    public float[] calculateIntersectionPoints(Lane lane1, Lane lane2) {
        // Extract coordinates for readability
        double x1 = lane1.getStart().getX(), y1 = lane1.getStart().getY();
        double x2 = lane1.getEnd().getX(), y2 = lane1.getEnd().getY();
        double x3 = lane2.getStart().getX(), y3 = lane2.getStart().getY();
        double x4 = lane2.getEnd().getX(), y4 = lane2.getEnd().getY();

        // Calculate the denominator
        double D = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

        // Check for parallel lines using a small epsilon
        if (Math.abs(D) < 0.000001f) {
            return null;
        }

        // represents the point's position along lane1 (0 to 1)
        float lane1Point = (float) (((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / D);
        // represents the point's position along lane2 (0 to 1)
        float lane2Point = (float) (((x1 - x3) * (y1 - y2) - (y1 - y3) * (x1 - x2)) / D);
    return new float[]{lane1Point, lane2Point};
    }

    public List<Intersection> getTurns(float pos1, float pos2, Lane lane) {
        // Return all intersections that occur on the given lane between pos1 and pos2
        List<Intersection> activeIntersections = new ArrayList<>();
        for (Intersection i : intersections) {
            if (i.getStartLane() == lane && i.getStartLanePosition() >= pos1 && i.getStartLanePosition() <= pos2) {
            activeIntersections.add(i);
            }
        }
        return activeIntersections;
    }

    public boolean isLastIntersection(Intersection interesection) {
        for (Intersection i : intersections) {
            if (i.getStartLane() == interesection.getStartLane() && i.getStartLanePosition() > interesection.getStartLanePosition()) {
                return false;
            }
        }
        return true;
    }





    public List<Intersection> getIntersections() {
        return intersections;
    }
    public void printIntersections() {
        System.out.println("Intersections:");
        for (Intersection i : intersections) {
            System.out.println("Intersection at point " + i.getIntersectionPoint());
        }
    }
}
