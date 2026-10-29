/// # Task · 4.2 the alarm must not speak twice
/// Lesson: 02-branches.html

void main() {
    int distance = Integer.parseInt(IO.readln("Distance: ").strip());
    if (distance <= 5) {
        IO.println("Near");
    }
    if (distance <= 2) {
        IO.println("Danger");
    }
    if (distance > 5) {
        IO.println("Clear");
    }
}
