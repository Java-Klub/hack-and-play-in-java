/// # Vyřešený úkol · 6.1 hlas odpočítává jen jednou
/// Lekce: 01-while.html

void main() {
    int remaining = Integer.parseInt(IO.readln("Start: ").strip());
    while (remaining > 0) {
        IO.println(remaining);
        remaining = remaining - 1;
    }
    IO.println("Launch");
}
