/// # Task · 5.5 build the arena referee
/// Lesson: 05-composition.html

void main() {
    String move = IO.readln("Move: ");
    int energy = Integer.parseInt(IO.readln("Energy: ").strip());
    IO.println("Verdict: " + verdict(move, energy));
}

String clean(String move) {
    return move;
}

int cost(String move) {
    return 0;
}

String verdict(String move, int energy) {
    return "GO";
}
