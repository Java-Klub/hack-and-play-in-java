void main() {
    int x = 2;
    Point first = new Point(x, 3);
    x = 8;
    Point second = new Point(x, 3);
    Point copy = new Point(2, 3);
    IO.println("First: " + first.x() + "," + first.y());
    IO.println("Second: " + second.x() + "," + second.y());
    IO.println("Same values: " + first.equals(copy));
}

record Point(int x, int y) {
}
