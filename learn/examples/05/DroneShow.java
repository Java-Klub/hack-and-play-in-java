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
