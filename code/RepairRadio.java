void main() {
    String command = IO.readln("Command: ").strip().toLowerCase(Locale.ROOT);
    String action = switch (command) {
        case "go" -> "Unknown command";
        default -> "Move";
    };
    IO.println("Action: " + action);
}
