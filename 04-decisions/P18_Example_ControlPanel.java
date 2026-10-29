/// # Hotový příklad · 4.5 Vyber hodnotu pomocí switch
/// Lekce: 05-switch.html

void main() {
    boolean lights = true;
    boolean music = false;
    int mode = 0;
    if (lights) {
        mode = mode + 1;
    }
    if (music) {
        mode = mode + 2;
    }
    IO.println("Mode: " + mode);
}
