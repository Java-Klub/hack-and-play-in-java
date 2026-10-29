/// # Vyřešený úkol · 9.2 ponech za sebou kontrolní bod
/// Lekce: 02-objects.html

void main() {
    int x = Integer.parseInt(IO.readln("Start x: ").strip());
    int y = Integer.parseInt(IO.readln("Start y: ").strip());
    Point start = new Point(x, y);
    Point finish = new Point(start.x() + 2, start.y() - 1);
    IO.println("Start: " + start.x() + "," + start.y());
    IO.println("Finish: " + finish.x() + "," + finish.y());
    IO.println("Back at start: " + start.equals(finish));
}

record Point(int x, int y) {
}
