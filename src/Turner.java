import java.util.List;
public class Turner {
    private static final Turner INSTANCE = new Turner();
    List<Intersection> intersections;
    private Turner() {
        // private to prevent external instantiation
    }

    public static Turner getInstance() {
        return INSTANCE;
    }

}
