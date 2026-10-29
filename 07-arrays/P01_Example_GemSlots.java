/// # Hotový příklad · 7.1 Dej hodnotám očíslovaná místa
/// Lekce: 01-arrays.html

void main() {
    int[] gems = {3, 0, 5};
    int slot = Integer.parseInt(IO.readln("Slot: ").strip());
    if (slot < 0 || slot >= gems.length) {
        IO.println("Invalid slot");
    } else {
        IO.println("Gems: " + gems[slot]);
    }
    IO.println("Slots: " + gems.length);
}
