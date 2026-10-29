/// # Task · 9.2 leave a checkpoint behind
/// Lesson: 02-objects.html

void main() {
    int x = Integer.parseInt(IO.readln("Start x: ").strip());
    int y = Integer.parseInt(IO.readln("Start y: ").strip());
    Point start = new Point(x, y);
    Point finish = start;
    IO.println("Start: " + start.x() + "," + start.y());
    IO.println("Finish: " + finish.x() + "," + finish.y());
    IO.println("Back at start: " + start.equals(finish));
}

record Point(int x, int y) {
}
