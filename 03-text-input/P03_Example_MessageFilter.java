/// # Hotový příklad · 3.2 Vyčisti přijatý signál
/// Lekce: 02-text.html

void main() {
    String raw = IO.readln("Signal: ");
    String clean = raw.strip();
    String loud = clean.toUpperCase(Locale.ROOT);
    IO.println("Signal [" + loud + "]");
    IO.println("Original [" + raw + "]");
}
