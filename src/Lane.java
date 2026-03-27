

public class Lane {
    private int width;
    private Point start;
    private Point end;
    private double length;

    public Lane(Point start, Point end, int width) {
        this.start = start;
        this.end = end;
        this.width = width;
        calculateLength();
    }

    private void calculateLength() {
        float xDiff = end.getX() - start.getX();
        float yDiff = end.getY() - start.getY();
        this.length = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    public int getWidth() {
        return width;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    public double getLength() {
        return length;
    }
}
