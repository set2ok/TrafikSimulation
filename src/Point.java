public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public float distanceTo(Point other) {
        float dx = (float) (this.getX() - other.getX());
        float dy = (float) (this.getY() - other.getY());
        // dx * dx is significantly faster to compute than Math.pow(dx, 2)
        return (float) Math.sqrt(dx * dx + dy * dy);
    }


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    public void printer() {
        System.out.println("(" + x + ", " + y + ")");
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
