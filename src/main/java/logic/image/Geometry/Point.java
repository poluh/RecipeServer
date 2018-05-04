package logic.image.Geometry;

public class Point {

    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int distance(Point other) {
        return (int) Math.sqrt(Math.pow((other.x - this.x), 2) + Math.pow((other.y - this.y), 2));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        var objPoint = (Point) obj;
        return this.x == objPoint.x && this.y == objPoint.y;
    }

    @Override
    public String toString() {
        return String.format("x = %d, y = %d", x, y);
    }
}
