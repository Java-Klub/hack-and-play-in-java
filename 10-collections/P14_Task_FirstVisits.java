/// # Task · 10.2 announce only real discoveries
/// Lesson: 02-sets.html

void main() {
    Set<String> seen = new HashSet<>();
    while (true) {
        String name = IO.readln("Place (blank ends): ").strip();
        if (name.isEmpty()) {
            break;
        }
        boolean first = true;
        if (first) {
            IO.println("Discovered: " + name);
        }
    }
    IO.println("Unique places: " + seen.size());
}
