/// # Hotový příklad · 10.1 Batoh nemusí znát délku předem
/// Lekce: 01-lists.html

void main() {
    List<String> starting = List.of("Rope", "Torch");
    List<String> bag = new ArrayList<>(starting);
    bag.add("Map");
    bag.set(1, "Lantern");
    IO.println("First: " + bag.get(0));
    IO.println("Size: " + bag.size());
    IO.println("Bag: " + bag);
    IO.println("Starting: " + starting);
}
