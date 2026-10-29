/// # Hotový příklad · 14.4 Vyčleň jednu společnou myšlenku
/// Lekce: 04-refactoring.html

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
