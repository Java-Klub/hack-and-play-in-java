void main() {
    int altitude = 0;
    while (true) {
        String command = IO.readln("Move: ");
        if (command == null || command.equals("q")) {
            break;
        }
        if (command.equals("up") || command.equals("down")) {
            IO.println("Altitude: " + altitude);
        } else {
            IO.println("Unknown");
        }
    }
    IO.println("Landed");
}
