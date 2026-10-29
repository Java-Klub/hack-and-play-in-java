/// # Úkol · 10.1 udělej v batohu místo
/// Lekce: 01-lists.html

void main() {
    List<String> bag = new ArrayList<>(List.of("Rope", "Torch", "Map"));
    String item = IO.readln("Add: ").strip();
    edit(bag, item);
    IO.println("Bag: " + bag);
    IO.println("Size: " + bag.size());
}

void edit(List<String> bag, String item) {
}
