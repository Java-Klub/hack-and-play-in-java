/// # Vyřešený úkol · 5.1 ukliď poplach, neměň jeho zvuk
/// Lekce: 01-actions.html

void main() {
    alarm();
    IO.println("Door A locked");
    alarm();
    IO.println("Door B locked");
}

void alarm() {
    IO.println("Intruder!");
    IO.println("Lights red");
}
