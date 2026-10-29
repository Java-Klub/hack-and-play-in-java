/// # Úkol · 9.1 neztrať údaje o nákladu
/// Lekce: 01-records.html

void main() {
    String name = IO.readln("Cargo: ");
    int count = Integer.parseInt(IO.readln("Count: ").strip());
    int unitWeight = Integer.parseInt(IO.readln("Unit weight: ").strip());
    Cargo cargo = new Cargo("Unknown", 0, 0);
    IO.println(cargo.name() + ": " + cargo.count() + " pieces");
    IO.println("Total weight: " + cargo.unitWeight());
}

record Cargo(String name, int count, int unitWeight) {
}
