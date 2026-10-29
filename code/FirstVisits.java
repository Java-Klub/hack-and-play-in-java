void main() {
    Set<String> seen = new HashSet<>();
    while (true) {
        String name = IO.readln("Place (blank ends): ").strip();
        if (name.isEmpty()) {
            break;
        }
        boolean first = seen.add(name);
        if (first) {
            IO.println("Discovered: " + name);
        }
    }
    IO.println("Unique places: " + seen.size());
}
