/// # Hotový příklad · 5.2 Pošli metodě hodnoty
/// Lekce: 02-parameters.html

void main() {
    mark("Portal", 2);
    mark("Dragon", 7);
}

void mark(String name, int position) {
    IO.println(name + " at " + position);
}
