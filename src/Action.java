public class Action {
    private float newPos;
    private Lane newLane;

    public Action(float newPos, Lane newLane){
        this.newPos = newPos;
        this.newLane = newLane;
    }

    public Lane getNewLane() {
        return newLane;
    }

    public float getNewPos(){
        return newPos;
    }
}
