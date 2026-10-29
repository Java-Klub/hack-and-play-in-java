void main() {
    List<String> bag = new ArrayList<>(List.of("Rope", "Torch", "Map"));
    String item = IO.readln("Add: ").strip();
    edit(bag, item);
    IO.println("Bag: " + bag);
    IO.println("Size: " + bag.size());
}

void edit(List<String> bag, String item) {
    bag.remove(1);
    if (!item.isEmpty()) {
        bag.add(item);
    }
}
