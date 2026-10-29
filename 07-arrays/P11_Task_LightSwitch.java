/// # Vyřešený úkol · 7.3 přepínač světla
/// Lekce: 03-updates.html

void main() {
    boolean[] lights = {false, false, false, false};
    int slot = Integer.parseInt(IO.readln("Slot: ").strip());
    if (slot >= 0 && slot < lights.length) {
        lights[slot] = !lights[slot];
    } else {
        IO.println("Invalid slot");
    }
    IO.println(Arrays.toString(lights));
}
