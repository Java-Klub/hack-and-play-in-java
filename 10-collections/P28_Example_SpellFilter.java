/// # Worked example · 10.5 Send data through a filter
/// Lesson: 05-streams.html

void main() {
    List<String> words = List.of("sun", "moon", "star", "a");
    List<String> loud = words.stream()
        .filter(word -> word.length() >= 4)
        .map(word -> word.toUpperCase(Locale.ROOT))
        .toList();
    IO.println("Selected: " + loud);
    IO.println("Original: " + words);
}
