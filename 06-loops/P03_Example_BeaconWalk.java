/// # Hotový příklad · 6.2 Dej počítadlu vlastní místo
/// Lekce: 02-for.html

void main() {
    int count = Integer.parseInt(IO.readln("Count: ").strip());
    for (int index = 0; index < count; index++) {
        IO.println("Beacon " + index);
    }
    IO.println("Done");
}
