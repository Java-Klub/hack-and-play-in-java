/// # Solved task · 6.5 a small drone controller
/// Lesson: 05-game-loop.html

void main() {
    int altitude = 0;
    while (true) {
        String command = IO.readln("Move: ");
        if (command == null || command.equals("q")) {
            break;
        }
        if (command.equals("up") && altitude < 3) {
            altitude++;
            IO.println("Altitude: " + altitude);
        } else if (command.equals("down") && altitude > 0) {
            altitude--;
            IO.println("Altitude: " + altitude);
        } else if (command.equals("up") || command.equals("down")) {
            IO.println("Blocked");
        } else {
            IO.println("Unknown");
        }
    }
    IO.println("Landed");
}
