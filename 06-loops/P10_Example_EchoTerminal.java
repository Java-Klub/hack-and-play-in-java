/// # Hotový příklad · 6.5 Čti další tah
/// Lekce: 05-game-loop.html

void main() {
    while (true) {
        String command = IO.readln("Command: ");
        if (command == null || command.equals("q")) {
            break;
        }
        if (command.equals("ping")) {
            IO.println("pong");
        } else {
            IO.println("Unknown");
        }
    }
    IO.println("Closed");
}
