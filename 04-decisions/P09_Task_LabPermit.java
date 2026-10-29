/// # Solved task · 4.3 find the false permission
/// Lesson: 03-logic.html

void main() {
    String badge = IO.readln("Badge: ").strip();
    int energy = Integer.parseInt(IO.readln("Energy: ").strip());
    boolean accepted = badge.equals("BLUE") || badge.equals("GOLD");
    boolean enough = energy >= 3;
    boolean locked = false;
    boolean enter = accepted && enough && !locked;
    IO.println("Access: " + enter);
}
