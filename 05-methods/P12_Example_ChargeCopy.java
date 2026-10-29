/// # Worked example · 5.4 How far does a name reach?
/// Lesson: 04-local-values.html

void main() {
    int energy = 3;
    drain(energy);
    IO.println("In main: " + energy);
}

void drain(int energy) {
    energy = energy - 1;
    IO.println("In drain: " + energy);
}
