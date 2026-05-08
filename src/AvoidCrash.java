import java.util.List;


public class AvoidCrash implements ActorStrategy{
    Turner turner = null;
    ActorStrategy basicMove = null;
    List<Point> points;

    public AvoidCrash(Turner turner, ActorStrategy basicMove,List<Point> points ){
        this.turner = turner;
        this.basicMove = basicMove;
        this.points = points;
    }

    public void setPoints(List<Point> points){
        this.points = points;
    }

    @Override
    public Action nextMove(float pos, float speed, Lane lane) {
        Action basicAction = basicMove.nextMove(pos,speed,lane);

        if (collision(pos,lane, basicAction.getNewPos(), basicAction.getNewLane())){
            return new Action(pos,lane);

        }

        return basicAction;
    }
    private Boolean collision(float pos, Lane lane, float newPos, Lane newLane){
        Point coord = lane.getPointAtPosition(pos);
        for (Point point : points){
            if (coord.distanceTo(point) < 10 && coord.distanceTo(point) > 1
                && !movingAway(point, coord, newLane.getPointAtPosition(newPos)) && lane == newLane ){
                return true;
            }

        }

    return false;
    }
    private Boolean movingAway(Point point,Point curentPoint, Point newPoint){
        if (point.distanceTo(curentPoint) <= point.distanceTo(newPoint)){
            return true;
        }
        double dx = point.getX() - curentPoint.getX();
        double dy = point.getY() - curentPoint.getY();

        double tx = (newPoint.getX() - point.getX())/dx;
        double ty = (newPoint.getY() - point.getY())/dy;
        if (Math.abs(tx-ty) > 0.01){
            return true;
        }

        return false;
    }
}
