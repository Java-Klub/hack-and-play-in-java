/// # Solved task · 5.2 the portals take passengers the wrong way
/// Lesson: 02-parameters.html

void main() {
    String start = IO.readln("Start: ");
    String finish = IO.readln("Finish: ");
    route(start, finish);
    route(finish, start);
}

void route(String from, String to) {
    IO.println(from + " -> " + to);
}
