import java.util.List;

public class Intersection {
    private Lane StartLane;
    private List<Lane> EndLanes;
    private Point intersectionPoint;

        public Intersection(Lane StartLane, List<Lane> EndLanes, Point intersectionPoint) {
            this.StartLane = StartLane;
            this.EndLanes = EndLanes;
            this.intersectionPoint = intersectionPoint;
        }

    public Lane getStartLane() {
        return StartLane;
    }

    public List<Lane> getEndLane() {
        return EndLanes;
    }

    public Point getIntersectionPoint() {
        return intersectionPoint;
    }
}
