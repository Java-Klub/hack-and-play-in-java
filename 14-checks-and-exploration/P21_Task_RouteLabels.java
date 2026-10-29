/// # Task · 14.4 keep both route labels unchanged
/// Lesson: 04-refactoring.html

String placeName(String text) {
    return text;
}

String outbound(String place) {
    return "To " + place.strip().toUpperCase(Locale.ROOT) + ".";
}

String inbound(String place) {
    return "From " + place.strip().toUpperCase(Locale.ROOT) + ".";
}

void main() {
    String place = IO.readln("Place: ");
    if (place == null) return;
    IO.println(outbound(place));
    IO.println(inbound(place));
}
