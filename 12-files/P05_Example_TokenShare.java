/// # Hotový příklad · 12.2 Oznam neúspěšnou operaci
/// Lekce: 02-exceptions.html

int share(int tokens, int players) {
    if (players < 1) {
        throw new IllegalArgumentException("Players must be positive.");
    }
    return tokens / players;
}

void main() {
    String reply = IO.readln("Players: ");
    if (reply == null) return;
    try {
        int players = Integer.parseInt(reply.strip());
        IO.println("Each gets " + share(12, players) + ".");
    } catch (NumberFormatException failure) {
        IO.println("Use a whole number.");
    } catch (IllegalArgumentException failure) {
        IO.println(failure.getMessage());
    }
}
