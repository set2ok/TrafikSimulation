import java.util.ArrayList;
import java.util.List;

public class Intersection {
    private Lane startLane;
    private List<Lane> endLanes;
    private Float startLanePosition; // position on the start lane where the intersection occurs, as a percentage of the lane length
    private List<Float> endLanePositions; // position on the end lane where the intersection occurs, as a percentage of the lane length

        public Intersection(Lane StartLane, List<Lane> EndLanes, Float StartLanePosition, List<Float> EndLanePosition) {
            this.startLane = StartLane;
            this.endLanes = new ArrayList<>(EndLanes);
            this.startLanePosition = StartLanePosition;
            this.endLanePositions = new ArrayList<>(EndLanePosition);
        }


    public Point getIntersectionPoint() {
        return startLane.getPointAtPosition(startLanePosition);
    }

    public Lane getStartLane() {
        return startLane;
    }

    public List<Lane> getEndLanes() {
        return endLanes;
    }

    public Float getStartLanePosition() {
        return startLanePosition;
    }

    public List<Float> getEndLanePositions() {
        return endLanePositions;
    }

    public void addEndLanes(List<Lane> lanes, List<Float> positions) {
        if (lanes.size() != positions.size()) {
            throw new IllegalArgumentException("lanes and positions must be the same size");
        }
        this.endLanes.addAll(lanes);
        this.endLanePositions.addAll(positions);
    }

    public void mergeWith(Intersection other) {
        addEndLanes(other.getEndLanes(), other.getEndLanePositions());
    }


}
