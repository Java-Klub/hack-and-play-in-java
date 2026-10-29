/// # Úkol · 9.4 zanech stopu pozic
/// Lekce: 04-data-methods.html

void main() {
    int x = Integer.parseInt(IO.readln("X: ").strip());
    int y = Integer.parseInt(IO.readln("Y: ").strip());
    Vector start = new Vector(x, y);
    Vector finish = start.moved(2, -1);
    IO.println("Start: " + start.x() + "," + start.y());
    IO.println("Finish: " + finish.x() + "," + finish.y());
    IO.println("Squared distance: " + finish.distanceSquared());
}

record Vector(int x, int y) {
    Vector moved(int dx, int dy) {
        return this;
    }

    int distanceSquared() {
        return 0;
    }
}
