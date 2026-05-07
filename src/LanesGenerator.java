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

    public void generateMainLanes(int amount, int boxSize) {
        List<Lane> allLanes = new ArrayList<Lane>();

        boolean allLanesValid = false;
        Turner turner = new Turner();
        Lane[] mainLanes = new Lane[amount];
        while (!allLanesValid) {
            mainLanes = new Lane[amount];
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
                    mainLanes[j] = newLane;
                } else {
                    allLanesValid = false;
                    break;
                }
            }
        }
        // TODO: BUG - ther exist dead ends
        // Create intersections for the main lanes and add lanes between the main lanes and the furthest lane from their start and end points if there is no intersection at those points
        turner.createIntersections(List.of(mainLanes));
        for (int j = 0; j < mainLanes.length; j++) {
            allLanes.add(mainLanes[j]);

            List<Intersection> intersections = turner.getIntersections();
            boolean hasIntersectionAtEnd = false;
            boolean hasIntersectionAtStart = false;
            for (Intersection intersection : intersections) {
                if (intersection.getStartLane() == mainLanes[j] && intersection.getStartLanePosition() == 1) {
                    hasIntersectionAtEnd = true;

                }
                if (intersection.getStartLane() == mainLanes[j] && intersection.getStartLanePosition() == 0) {
                    hasIntersectionAtStart = true;
                }
            }

            if (!hasIntersectionAtEnd) {

            // find the lane that is furthest away from the start of the current lane, and create a new lane between the end of the current lane and the start of the furthest lane
                int targetIndex = j;
                float maxDistanceSum = 0;
                Point startPoint = mainLanes[j].getStart();

                for (int k = 0; k < mainLanes.length; k++) {
                    if (k == j) continue; // Skip comparing the lane to itself

                    Point startK = mainLanes[k].getStart();
                    Point endK = mainLanes[k].getEnd();

                    // Skip if they share the exact same starting coordinate
                    if (startPoint.getX() == startK.getX() && startPoint.getY() == startK.getY()) {
                        continue;
                    }

                    float distanceSum = startPoint.distanceTo(startK) + startPoint.distanceTo(endK);

                    if (distanceSum > maxDistanceSum) {
                        maxDistanceSum = distanceSum;
                        targetIndex = k;
                    }
                }

            Lane lane = new Lane(mainLanes[j].getEnd(), mainLanes[targetIndex].getStart(), 10);
            allLanes.add(lane);
            }
            // find the lane that is furthest away from the end of the current lane, and create a new lane between the start of the current lane and the end of the furthest lane
            if (!hasIntersectionAtStart) {
                int targetIndex = j;
                float maxDistanceSum = 0;
                Point endJ = mainLanes[j].getEnd();

                for (int k = 0; k < mainLanes.length; k++) {
                    if (k == j) continue; // Skip comparing the lane to itself

                    Point startK = mainLanes[k].getStart();
                    Point endK = mainLanes[k].getEnd();

                    // Skip if they share the exact same ending coordinate
                    if (endJ.getX() == endK.getX() && endJ.getY() == endK.getY()) {
                        continue;
                    }

                    // Beautiful, readable math
                    float distanceSum = endJ.distanceTo(startK) + endJ.distanceTo(endK);

                    if (distanceSum > maxDistanceSum) {
                        maxDistanceSum = distanceSum;
                        targetIndex = k;
                    }
                }

                Lane lane = new Lane(mainLanes[j].getStart(), mainLanes[targetIndex].getEnd(), 10);
                allLanes.add(lane);
            }



        }
        // refactor lanes to be between intersections
        List<Lane> refactoredLanes = refactorLanes(allLanes);
        System.out.println("Generated " + refactoredLanes.size() + " lanes");
        for (Lane lane : refactoredLanes) {
            controller.addLane(lane);
        }
    }


        public List<Lane> refactorLanes(List<Lane> lanes) {
            List<Lane> refactoredLanes = new ArrayList<>();
            Turner turner = new Turner();
            turner.createIntersections(lanes);
            List<Intersection> intersections = turner.getIntersections();

            for (Lane lane : lanes) {
                boolean duplicate = false;
                // Check if the lane already exists in the refactoredLanes list
                for (Lane refactoredLane : refactoredLanes) {
                    if (lane.getStart().getX() == refactoredLane.getStart().getX() && lane.getStart().getY() == refactoredLane.getStart().getY() && lane.getEnd().getX() == refactoredLane.getEnd().getX() && lane.getEnd().getY() == refactoredLane.getEnd().getY()) {
                        duplicate = true;
                        break;
                    }
                }
                if (duplicate) {
                    continue;
                }
                // Find the latest and earliest intersection points on the lane
                Intersection latestIntersection = null;
                Intersection earliestIntersection = null;
                for (Intersection intersection : intersections) {
                    if (intersection.getStartLane() == lane) {
                        if (latestIntersection == null || intersection.getStartLanePosition() > latestIntersection.getStartLanePosition()) {
                            latestIntersection = intersection;
                        }
                        if (earliestIntersection == null || intersection.getStartLanePosition() < earliestIntersection.getStartLanePosition()) {
                            earliestIntersection = intersection;
                        }

                    }

                }
                // If there are intersection points, create a new lane between the earliest and latest intersection points
                if (latestIntersection != null && earliestIntersection != null) {
                    Lane newLane = new Lane(lane.getPointAtPosition(earliestIntersection.getStartLanePosition()), lane.getPointAtPosition(latestIntersection.getStartLanePosition()), lane.getWidth());

                    refactoredLanes.add(newLane);
                }
                else {
                    refactoredLanes.add(lane);
                }


            }
            return refactoredLanes;
        }
}
