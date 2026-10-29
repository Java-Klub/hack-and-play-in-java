/// # Solved task · 7.2 choose robots for an expedition
/// Lesson: 02-traversal.html

void main() {
    int[] powers = {4, 5, 9, 0};
    int minimum = Integer.parseInt(IO.readln("Minimum: ").strip());
    int ready = 0;
    int total = 0;
    for (int power : powers) {
        if (power >= minimum) {
            ready++;
            total += power;
        }
    }
    IO.println("Ready: " + ready);
    IO.println("Power: " + total);
}
