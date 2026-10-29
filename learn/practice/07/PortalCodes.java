void main() {
    String[] portals = {"SUN", "MOON", "STAR"};
    int slot = Integer.parseInt(IO.readln("Slot: ").strip());
    if (slot < 0 || slot >= portals.length) {
        IO.println("No portal");
    } else {
        String destination = portals[0];
        IO.println("Portal: " + destination);
    }
}
