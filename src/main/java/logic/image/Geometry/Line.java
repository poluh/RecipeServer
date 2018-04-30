package logic.image.Geometry;

public class Line {

    private Point start;
    private Point end;

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Point intersectionOfLines(Line other) {
        return new Point(other.start.x, this.start.y);
    }

    @Override
    public String toString() {
        return this.start.toString() + " " + this.end.toString();
    }
}
