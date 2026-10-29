/// # Hotový příklad · 7.3 Změň skutečné místo
/// Lekce: 03-updates.html

void main() {
    int[] charges = {2, 5, 1};
    int slot = Integer.parseInt(IO.readln("Slot: ").strip());
    if (slot >= 0 && slot < charges.length) {
        charges[slot] = charges[slot] + 2;
    } else {
        IO.println("Invalid slot");
    }
    IO.println(Arrays.toString(charges));
}
