/// # Solved task · 5.1 tidy up the alarm without changing its sound
/// Lesson: 01-actions.html

void main() {
    alarm();
    IO.println("Door A locked");
    alarm();
    IO.println("Door B locked");
}

void alarm() {
    IO.println("Intruder!");
    IO.println("Lights red");
}
