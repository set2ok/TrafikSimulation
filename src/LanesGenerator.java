import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LanesGenerator {
    SimulationController controller;
    Random rand;
    LaneValidator validator = new LaneValidator();


    public LanesGenerator(SimulationController controller) {
        this.controller = controller;
        this.rand = new Random();
    }

    public Lane generateParallelLine(Lane lane, int distance, int direction, int side) {
        Point start = lane.getStart();
        Point end = lane.getEnd();
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        double length = Math.sqrt(dx * dx + dy * dy);
        double unitX = dx / length;
        double unitY = dy / length;
        double offsetX = unitY * distance;
        double offsetY = -unitX * distance;
        if (side == -1) {
            offsetX = -offsetX;
            offsetY = -offsetY;
        }

        if (direction >= 1) {
            Point newStart = new Point(start.getX() + offsetX, start.getY() + offsetY);
            Point newEnd = new Point(end.getX() + offsetX, end.getY() + offsetY);
            Lane parallelLane = new Lane(newStart, newEnd, lane.getWidth());
            return parallelLane;
        } else {
            Point newStart = new Point(end.getX() + offsetX, end.getY() + offsetY);
            Point newEnd = new Point(start.getX() + offsetX, start.getY() + offsetY);
            Lane parallelLane = new Lane(newStart, newEnd, lane.getWidth());
            return parallelLane;
        }

    }

    public void generateLanes(int amount, int boxSize) {
        List<Lane> allLanes = new ArrayList<>();
        Turner turner = new Turner();
        List<Lane> mainLanes = generateMainLanes(amount, boxSize, turner);

        // Create intersections for main lanes first
        turner.createIntersections(mainLanes);

        // Add main lanes
        for (Lane lane : mainLanes) {
            allLanes.add(lane);
        }

        turner.createIntersections(allLanes);
        // Create connectors that actually connect to intersections
        for (int j = 0; j < mainLanes.size(); j++) {
            List<Lane> newLanes = createValidConnectors(mainLanes.get(j), mainLanes);
            for (Lane lane : newLanes) {
                if (validator.isValidLane(lane, allLanes)) {
                    allLanes.add(lane);
                }
            }
        }

        for (Lane lane : allLanes) {
            Lane optimized = optimizeLaneOrientation(lane, mainLanes, turner);
            if (optimized != lane) {
                allLanes.remove(lane);
                allLanes.add(optimized);
            }
        }

        connectOrphanedLanes(allLanes);

        // Refactor all lanes to exist only between intersections
        List<Lane> refactoredLanes = refactorLanes(allLanes);
        System.out.println("Generated " + refactoredLanes.size() + " lanes");

        for (Lane lane : refactoredLanes) {
            controller.addLane(lane);
        }
    }

    private List<Lane> createValidConnectors(Lane mainLane, List<Lane> allMainLanes) {
        Turner turner = new Turner();
        turner.createIntersections(List.of(mainLane));
        List<Lane> newLanes = new ArrayList<>();

        // Check start
        if (!hasIntersectionAt(mainLane, 0, turner)) {
            Lane connector = findValidConnector(mainLane, allMainLanes, true);
            if (connector != null && validator.isValidLane(connector, allMainLanes)) {
                newLanes.add(connector);
            }
        }

        // Check end
        if (!hasIntersectionAt(mainLane, 1, turner)) {
            Lane connector = findValidConnector(mainLane, allMainLanes, false);
            if (connector != null && validator.isValidLane(connector, allMainLanes)) {
                newLanes.add(connector);
            }
        }
        return newLanes;
    }

    private Lane findValidConnector(Lane mainLane, List<Lane> allMainLanes, boolean fromStart) {
        Point targetPoint = fromStart ? mainLane.getStart() : mainLane.getEnd();
        int bestIndex = -1;
        float maxDistance = 0;

        for (int k = 0; k < allMainLanes.size(); k++) {
            if (allMainLanes.get(k) == null || allMainLanes.get(k) == mainLane) continue;

            // Try connecting to different parts of the target lane
            Point candidateStart = allMainLanes.get(k).getStart();
            Point candidateEnd = allMainLanes.get(k).getEnd();

            float distStart = targetPoint.distanceTo(candidateStart);
            float distEnd = targetPoint.distanceTo(candidateEnd);

            float minDist = Math.min(distStart, distEnd);
            if (minDist > maxDistance && minDist < 500) { // Reasonable max distance
                maxDistance = minDist;
                bestIndex = k;
            }
        }

        if (bestIndex == -1) return null;

        // Create connector to the closest point of the best lane
        Point targetLaneStart = allMainLanes.get(bestIndex).getStart();
        Point targetLaneEnd = allMainLanes.get(bestIndex).getEnd();

        if (targetPoint.distanceTo(targetLaneStart) < targetPoint.distanceTo(targetLaneEnd)) {
            return new Lane(targetPoint, targetLaneStart, mainLane.getWidth());
        } else {
            return new Lane(targetPoint, targetLaneEnd, mainLane.getWidth());
        }
    }

    private boolean hasIntersectionAt(Lane lane, float position, Turner turner) {
        for (Intersection i : turner.getIntersections()) {
            if (i.getStartLane() == lane && Math.abs(i.getStartLanePosition() - position) < 0.05f) {
                return true;
            }
        }
        return false;
    }

    public List<Lane> refactorLanes(List<Lane> lanes) {
        List<Lane> refactoredLanes = new ArrayList<>();
        Turner turner = new Turner();
        turner.createIntersections(lanes);
        List<Intersection> intersections = turner.getIntersections();

        for (Lane lane : lanes) {
            // Find EARLIEST entry (smallest position in endLanes)
            float entryPosition = 0;
            boolean hasEntry = false;
            for (Intersection intersection : intersections) {
                if (intersection.getEndLanes().contains(lane)) {
                    float pos = intersection.getEndLanePositions().get(intersection.getEndLanes().indexOf(lane));
                    if (!hasEntry || pos < entryPosition) {
                        entryPosition = pos;
                        hasEntry = true;
                    }
                }
            }

            // Find LATEST exit (largest position as startLane)
            float exitPosition = 1;
            boolean hasExit = false;
            for (Intersection intersection : intersections) {
                if (intersection.getStartLane() == lane) {
                    float pos = intersection.getStartLanePosition();
                    if (!hasExit || pos > exitPosition) {
                        exitPosition = pos;
                        hasExit = true;
                    }
                }
            }

            // Only keep lane if it has both entry AND exit
            if (hasEntry && hasExit && exitPosition > entryPosition) {
                Lane segment = new Lane(
                        lane.getPointAtPosition(entryPosition),
                        lane.getPointAtPosition(exitPosition),
                        lane.getWidth()
                );

                if (!laneAlreadyExists(segment, refactoredLanes)) {
                    refactoredLanes.add(segment);
                }
            }
        }

        return refactoredLanes;
    }

    private boolean laneAlreadyExists(Lane lane, List<Lane> lanes) {
        for (Lane existing : lanes) {
            if (Math.abs(existing.getStart().getX() - lane.getStart().getX()) < 1 &&
                    Math.abs(existing.getStart().getY() - lane.getStart().getY()) < 1 &&
                    Math.abs(existing.getEnd().getX() - lane.getEnd().getX()) < 1 &&
                    Math.abs(existing.getEnd().getY() - lane.getEnd().getY()) < 1) {
                return true;
            }
        }
        return false;
    }

    private Lane optimizeLaneOrientation(Lane lane, List<Lane> allMainLanes, Turner turner) {
        // Count intersections at start and end for current orientation
        int currentStartIntersections = countIntersectionsAt(lane, 0, turner);
        int currentEndIntersections = countIntersectionsAt(lane, 1, turner);

        // Reverse the lane and check again
        Lane reversedLane = new Lane(lane.getEnd(), lane.getStart(), lane.getWidth());
        int reversedStartIntersections = countIntersectionsAt(reversedLane, 0, turner);
        int reversedEndIntersections = countIntersectionsAt(reversedLane, 1, turner);

        // If reversed has more intersections, use it
        int currentTotal = currentStartIntersections + currentEndIntersections;
        int reversedTotal = reversedStartIntersections + reversedEndIntersections;

        if (reversedTotal > currentTotal) {
            return reversedLane;
        }

        // If equal, prefer the one with intersections at the start
        if (reversedTotal == currentTotal && reversedStartIntersections > currentStartIntersections) {
            return reversedLane;
        }

        return lane;
    }
    private int countIntersectionsAt(Lane lane, float position, Turner turner) {
        int count = 0;
        for (Intersection i : turner.getIntersections()) {
            if (i.getStartLane() == lane && Math.abs(i.getStartLanePosition() - position) < 0.05f) {
                count++;
            }
        }
        return count;
    }
    private void connectOrphanedLanes(List<Lane> lanes) {
        Turner turner = new Turner();
        turner.createIntersections(lanes);
        List<Intersection> intersections = turner.getIntersections();

        // Find lanes with no exit (end at position 1 with no intersection)
        List<Lane> lanesWithoutExit = new ArrayList<>();
        // Find lanes with no entry (start at position 0 with no intersection)
        List<Lane> lanesWithoutEntry = new ArrayList<>();

        for (Lane lane : lanes) {
            boolean hasExit = false;
            boolean hasEntry = false;

            for (Intersection i : intersections) {
                if (i.getStartLane() == lane) {
                    hasExit = true;
                }
                if (i.getEndLanes().contains(lane)) {
                    hasEntry = true;
                }
            }

            if (!hasExit) {
                lanesWithoutExit.add(lane);
            }
            if (!hasEntry) {
                lanesWithoutEntry.add(lane);
            }
        }

        // Match each lane without exit to closest lane without entry
        for (Lane endLane : lanesWithoutExit) {
            Point endPoint = endLane.getEnd();
            Lane bestMatch = null;
            float minDistance = Float.MAX_VALUE;

            for (Lane startLane : lanesWithoutEntry) {
                Point startPoint = startLane.getStart();
                float distance = endPoint.distanceTo(startPoint);

                if (distance < minDistance && distance < 20) { // Reasonable threshold
                    minDistance = distance;
                    bestMatch = startLane;
                }
            }

            // If found a match, create connector
            if (bestMatch != null) {
                Lane connector = new Lane(endLane.getEnd(), bestMatch.getStart(), endLane.getWidth());
                if (validator.isValidLane(connector, lanes)) {
                    lanes.add(connector);
                }
                lanesWithoutEntry.remove(bestMatch);
            }
        }
    }

    private List<Lane> generateMainLanes(int amount, int boxSize, Turner turner) {

        boolean allLanesValid = false;
        List<Lane> mainLanes = new ArrayList<Lane>();
        while (!allLanesValid) {
            mainLanes = new ArrayList<Lane>();
            allLanesValid = true;

            // Generate random lanes
            int i = 0;
            Lane[] tempLanes = new Lane[amount];
            for (i = 0; i < amount; i++) {
                int x1 = rand.nextInt(boxSize);
                int y1 = rand.nextInt(boxSize);
                int x2 = rand.nextInt(boxSize);
                int y2 = rand.nextInt(boxSize);
                Lane lane = new Lane(new Point(x1, y1), new Point(x2, y2), 10);
                tempLanes[i] = lane;
            }
            // For each lane, find the largest and smallest intersection points with other lanes, and create a new lane between those points
            for (int j = 0; j < tempLanes.length; j++) {
                float largestPoint = 0;
                float smallestPoint = 1;
                for (int k = 0; k < tempLanes.length; k++) {
                    float[] points = turner.calculateIntersectionPoints(tempLanes[j], tempLanes[k]);
                    if (points != null) {
                        if (points[0] > largestPoint) {
                            largestPoint = points[0];
                        }
                        if (points[0] < smallestPoint) {
                            smallestPoint = points[0];
                        }
                    }
                }

                Lane newLane = new Lane(tempLanes[j].getPointAtPosition(smallestPoint), tempLanes[j].getPointAtPosition(largestPoint), 10);
                if (validator.isValidLane(newLane, mainLanes)) {
                    mainLanes.add(newLane);
                } else {
                    allLanesValid = false;
                    break;
                }
            }
        }
        return mainLanes;
    }
}
