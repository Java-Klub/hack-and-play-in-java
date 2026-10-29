/// # Vyřešený úkol · 7.1 kam vede vybraný portál?
/// Lekce: 01-arrays.html

void main() {
    String[] portals = {"SUN", "MOON", "STAR"};
    int slot = Integer.parseInt(IO.readln("Slot: ").strip());
    if (slot < 0 || slot >= portals.length) {
        IO.println("No portal");
    } else {
        String destination = portals[slot];
        IO.println("Portal: " + destination);
    }
}
