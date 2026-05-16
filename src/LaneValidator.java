import java.util.List;

public class LaneValidator {
    private static final double MIN_DISTANCE = 25.0; // Minimum distance between parallel lanes

    public static boolean isValidLaneDistance(Lane lane1, Lane lane2) {
        // Check if lanes are roughly parallel
        if (!areLanesParallel(lane1, lane2)) {
            return true; // Not parallel, so distance check doesn't apply
        }

        // Calculate perpendicular distance between parallel lanes
        double distance = calculatePerpendicularDistance(lane1, lane2);
        return distance >= MIN_DISTANCE;
    }

    public static boolean isValidLane(Lane lane, List<Lane> existingLanes) {
        for (int i = 0; i < existingLanes.size(); i++) {
            if (existingLanes.get(i) != null) {

                if (!isValidLaneDistance(lane, existingLanes.get(i))) {
                    return false; // Lane is too close to an existing lane
                }
            }
        }

        return true; // Lane is valid
    }

    private static boolean areLanesParallel(Lane lane1, Lane lane2) {
        double dx1 = lane1.getEnd().getX() - lane1.getStart().getX();
        double dy1 = lane1.getEnd().getY() - lane1.getStart().getY();
        double dx2 = lane2.getEnd().getX() - lane2.getStart().getX();
        double dy2 = lane2.getEnd().getY() - lane2.getStart().getY();

        double dotProduct = dx1 * dx2 + dy1 * dy2;
        double mag1 = Math.sqrt(dx1 * dx1 + dy1 * dy1);
        double mag2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);

        double cosAngle = Math.abs(dotProduct) / (mag1 * mag2);
        return cosAngle > 0.95; // Nearly parallel (within ~18 degrees)
    }

    private static double calculatePerpendicularDistance(Lane lane1, Lane lane2) {
        Point p1 = lane1.getStart();
        Point p2 = lane2.getStart();
        double dx = lane1.getEnd().getX() - lane1.getStart().getX();
        double dy = lane1.getEnd().getY() - lane1.getStart().getY();
        double length = Math.sqrt(dx * dx + dy * dy);

        double distance = Math.abs((p2.getX() - p1.getX()) * (-dy) + (p2.getY() - p1.getY()) * dx) / length;
        return distance;
    }
}