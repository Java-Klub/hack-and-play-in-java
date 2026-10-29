/// # Hotový příklad · 12.1 Rozhodni, které hodnoty dávají smysl
/// Lekce: 01-validation.html

boolean allowed(int players) {
    return players >= 1 && players <= 4;
}

void main() {
    String reply = IO.readln("Players: ");
    if (reply == null) return;
    int players = Integer.parseInt(reply.strip());
    if (allowed(players)) {
        IO.println("Ready for " + players + " players.");
    } else {
        IO.println("Use 1..4 players.");
    }
}
