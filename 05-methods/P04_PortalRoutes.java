void main() {
    String start = IO.readln("Start: ");
    String finish = IO.readln("Finish: ");
    route(start, finish);
    route(finish, start);
}

void route(String from, String to) {
    IO.println(from + " -> " + to);
}
