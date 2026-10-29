/// # Worked example · 6.5 Read the next move
/// Lesson: 05-game-loop.html

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
