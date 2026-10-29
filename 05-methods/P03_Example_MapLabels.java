/// # Worked example · 5.2 Send values to a method
/// Lesson: 02-parameters.html

void main() {
    mark("Portal", 2);
    mark("Dragon", 7);
}

void mark(String name, int position) {
    IO.println(name + " at " + position);
}
