public interface ActorStrategy {
    Turner turner = null;
    public Action nextMove(float pos, float speed, Lane lane);
    
}
