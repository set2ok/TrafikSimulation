

public class Lane {
    private int width;
    private Point start;
    private Point end;
    private int length;

    public Lane(Point start, Point end) {
        this.start = start;
        this.end = end;
        calculateLength();
    }

    private void calculateLength() {
        int xDiff = end.getX() - start.getX();
        int yDiff = end.getY() - start.getY();
        this.length = (int) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
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

    public int getLength() {
        return length;
    }
}
