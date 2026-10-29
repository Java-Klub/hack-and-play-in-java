void main() {
    String raw = IO.readln("Signal: ");
    String clean = raw.strip();
    String loud = clean.toUpperCase(Locale.ROOT);
    IO.println("Signal [" + loud + "]");
    IO.println("Original [" + raw + "]");
}
