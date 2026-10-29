/// # Worked example · 6.2 Give the counter its own place
/// Lesson: 02-for.html

void main() {
    int count = Integer.parseInt(IO.readln("Count: ").strip());
    for (int index = 0; index < count; index++) {
        IO.println("Beacon " + index);
    }
    IO.println("Done");
}
