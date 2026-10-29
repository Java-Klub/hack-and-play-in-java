void main() {
    String command = IO.readln("Command: ").strip().toLowerCase(Locale.ROOT);
    String action = switch (command) {
        case "go", "g" -> "Move";
        case "look", "l" -> "Scan";
        case "rest", "r" -> "Recharge";
        default -> "Unknown command";
    };
    IO.println("Action: " + action);
}
