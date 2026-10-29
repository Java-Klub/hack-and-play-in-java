/// # Vyřešený úkol · 5.2 portály vozí cestující špatně
/// Lekce: 02-parameters.html

void main() {
    String start = IO.readln("Start: ");
    String finish = IO.readln("Finish: ");
    route(start, finish);
    route(finish, start);
}

void route(String from, String to) {
    IO.println(from + " -> " + to);
}
