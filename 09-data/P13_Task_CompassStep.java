/// # Task · 9.5 make the compass move
/// Lesson: 05-enums.html

void main() {
    Point start = new Point(2, 3);
    for (Direction direction : Direction.values()) {
        Point finish = step(start, direction);
        IO.println(direction + ": " + finish.x() + "," + finish.y());
    }
}

Point step(Point position, Direction direction) {
    return position;
}

record Point(int x, int y) {
}

enum Direction {
    NORTH, EAST, SOUTH, WEST
}
