/// # Worked example · 14.3 Ask which kind of data arrived
/// Lesson: 03-patterns.html

sealed interface Message permits Text, Pause {}
record Text(String words) implements Message {}
record Pause(int seconds) implements Message {}

String describe(Message message) {
    return switch (message) {
        case null -> "No message";
        case Text text -> "Say " + text.words();
        case Pause(var seconds) -> "Wait " + seconds + "s";
    };
}

void main() {
    IO.println(describe(new Text("Hello")));
    IO.println(describe(new Pause(2)));
    IO.println(describe(null));
}
