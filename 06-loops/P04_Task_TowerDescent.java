/// # Solved task · 6.2 descend from the tower
/// Lesson: 02-for.html

void main() {
    int height = Integer.parseInt(IO.readln("Height: ").strip());
    for (int floor = height; floor > 0; floor--) {
        IO.println("Floor " + floor);
    }
    IO.println("Ground");
}
