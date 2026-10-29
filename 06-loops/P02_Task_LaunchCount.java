/// # Solved task · 6.1 the countdown voice speaks only once
/// Lesson: 01-while.html

void main() {
    int remaining = Integer.parseInt(IO.readln("Start: ").strip());
    while (remaining > 0) {
        IO.println(remaining);
        remaining = remaining - 1;
    }
    IO.println("Launch");
}
