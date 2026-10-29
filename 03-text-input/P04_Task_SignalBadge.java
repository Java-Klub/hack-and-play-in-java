/// # Solved task · 3.2 fix the badge printer
/// Lesson: 02-text.html

void main() {
    String raw = IO.readln("Badge: ");
    String clean = raw.strip();
    clean = clean.toUpperCase(Locale.ROOT);
    IO.println("Badge [" + clean + "]");
    IO.println("Original [" + raw + "]");
}
