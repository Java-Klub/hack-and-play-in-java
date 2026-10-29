/// # Task · 4.5 fix the broken radio
/// Lesson: 05-switch.html

void main() {
    String command = IO.readln("Command: ").strip().toLowerCase(Locale.ROOT);
    String action = switch (command) {
        case "go" -> "Unknown command";
        default -> "Move";
    };
    IO.println("Action: " + action);
}
