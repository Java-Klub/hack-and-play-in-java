String cleanName(String name) {
    return name.strip().toUpperCase(Locale.ROOT);
}

String welcome(String name) {
    return "Welcome, " + cleanName(name) + "!";
}

String farewell(String name) {
    return cleanName(name) + " logged out.";
}

void main() {
    IO.println(welcome("  Nova "));
    IO.println(farewell("  Nova "));
}
