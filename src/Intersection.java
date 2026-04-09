import java.util.List;

public class Intersection {
    private Lane StartLane;
    private List<Lane> EndLanes;
    private Float StartLanePosition; // position on the start lane where the intersection occurs, as a percentage of the lane length
    private Float EndLanePosition; // position on the end lane where the intersection occurs, as a percentage of the lane length

        public Intersection(Lane StartLane, List<Lane> EndLanes, Float StartLanePosition, Float EndLanePosition) {
            this.StartLane = StartLane;
            this.EndLanes = EndLanes;
            this.StartLanePosition = StartLanePosition;
            this.EndLanePosition = EndLanePosition;
        }

    public Lane getStartLane() {
        return StartLane;
    }

    public List<Lane> getEndLanes() {
        return EndLanes;
    }

}
