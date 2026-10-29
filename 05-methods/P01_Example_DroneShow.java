/// # Hotový příklad · 5.1 Pojmenuj vlastní krok
/// Lekce: 01-actions.html

void main() {
    wake();
    flash();
    flash();
    IO.println("Show finished");
}

void wake() {
    IO.println("Drone awake");
    IO.println("Lights ready");
}

void flash() {
    IO.println("* FLASH *");
}
