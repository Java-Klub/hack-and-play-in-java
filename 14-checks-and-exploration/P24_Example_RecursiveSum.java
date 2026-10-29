/// # Worked example · 14.5 Explore smaller problems and choices
/// Lesson: 05-recursion.html

int sumTo(int number) {
    if (number == 0) return 0;
    return number + sumTo(number - 1);
}

void main() {
    String reply = IO.readln("Steps: ");
    if (reply == null) return;
    try {
        int number = Integer.parseInt(reply.strip());
        if (number < 0 || number > 12) {
            IO.println("Use 0..12.");
            return;
        }
        IO.println("Sum: " + sumTo(number));
    } catch (NumberFormatException failure) {
        IO.println("Use a whole number.");
    }
}
