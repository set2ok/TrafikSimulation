

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

    public Point getPointAtPosition(float position) {
        if (position < 0 || position > 1) {
            throw new IllegalArgumentException("position must be between 0 and 1");
        }
        float x = (float) (start.getX() + position * (end.getX() - start.getX()));
        float y = (float) (start.getY() + position * (end.getY() - start.getY()));
        return new Point(x, y);
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
