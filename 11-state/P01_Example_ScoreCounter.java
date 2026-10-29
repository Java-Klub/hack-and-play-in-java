/// # Worked example · 11.1 An object remembers a change
/// Lesson: 01-state.html

void main() {
    Counter score = new Counter();
    IO.println("Before: " + score.points);
    score.add(3);
    score.add(4);
    IO.println("After: " + score.points);
}

class Counter {
    int points = 0;

    void add(int amount) {
        points = points + amount;
    }
}
