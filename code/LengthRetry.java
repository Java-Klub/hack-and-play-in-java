void main() {
    while (true) {
        String reply = IO.readln("Code length: ");
        if (reply == null || reply.strip().equalsIgnoreCase("q")) {
            IO.println("Stopped.");
            return;
        }
        try {
            int length = Integer.parseInt(reply.strip());
            if (length < 1 || length > 8) {
                IO.println("Use 1..8.");
                continue;
            }
            IO.println("Ready: " + length);
            return;
        } catch (NumberFormatException failure) {
            IO.println("Use a whole number.");
        }
    }
}
