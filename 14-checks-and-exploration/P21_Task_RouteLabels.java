/// # Solved task · 14.4 keep both route labels unchanged
/// Lesson: 04-refactoring.html

String placeName(String text) {
    return text.strip().toUpperCase(Locale.ROOT);
}

String outbound(String place) {
    return "To " + placeName(place) + ".";
}

String inbound(String place) {
    return "From " + placeName(place) + ".";
}

void main() {
    String place = IO.readln("Place: ");
    if (place == null) return;
    IO.println(outbound(place));
    IO.println(inbound(place));
}
