/// # Vyřešený úkol · 9.5 rozhýbej kompas
/// Lekce: 05-enums.html

void main() {
    Point start = new Point(2, 3);
    for (Direction direction : Direction.values()) {
        Point finish = step(start, direction);
        IO.println(direction + ": " + finish.x() + "," + finish.y());
    }
}

Point step(Point position, Direction direction) {
    return switch (direction) {
        case NORTH -> new Point(position.x(), position.y() - 1);
        case EAST -> new Point(position.x() + 1, position.y());
        case SOUTH -> new Point(position.x(), position.y() + 1);
        case WEST -> new Point(position.x() - 1, position.y());
    };
}

record Point(int x, int y) {
}

enum Direction {
    NORTH, EAST, SOUTH, WEST
}
