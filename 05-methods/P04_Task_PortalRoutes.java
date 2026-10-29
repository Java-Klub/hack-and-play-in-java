/// # Task · 5.2 the portals take passengers the wrong way
/// Lesson: 02-parameters.html

void main() {
    String start = IO.readln("Start: ");
    String finish = IO.readln("Finish: ");
    route(finish, start);
    route(finish, start);
}

void route(String from, String to) {
    IO.println(from + " -> " + from);
}
