/// # Vyřešený úkol · 5.5 sestav rozhodčího arény
/// Lekce: 05-composition.html

void main() {
    String move = IO.readln("Move: ");
    int energy = Integer.parseInt(IO.readln("Energy: ").strip());
    IO.println("Verdict: " + verdict(move, energy));
}

String clean(String move) {
    return move.strip().toUpperCase(Locale.ROOT);
}

int cost(String move) {
    return switch (move) {
        case "JUMP" -> 3;
        case "DUCK" -> 1;
        default -> 0;
    };
}

String verdict(String move, int energy) {
    String action = clean(move);
    int needed = cost(action);
    if (needed == 0) {
        return "UNKNOWN";
    }
    if (energy < needed) {
        return "TIRED";
    }
    return "GO";
}
