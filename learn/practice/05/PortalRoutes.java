void main() {
    String start = IO.readln("Start: ");
    String finish = IO.readln("Finish: ");
    route(finish, start);
    route(finish, start);
}

void route(String from, String to) {
    IO.println(from + " -> " + from);
}
