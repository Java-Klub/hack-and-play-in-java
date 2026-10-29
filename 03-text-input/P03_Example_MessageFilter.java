/// # Worked example · 3.2 Clean up an incoming signal
/// Lesson: 02-text.html

void main() {
    String raw = IO.readln("Signal: ");
    String clean = raw.strip();
    String loud = clean.toUpperCase(Locale.ROOT);
    IO.println("Signal [" + loud + "]");
    IO.println("Original [" + raw + "]");
}
