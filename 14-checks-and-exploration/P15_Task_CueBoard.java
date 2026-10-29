/// # Solved task · 14.3 repair a cue description
/// Lesson: 03-patterns.html

sealed interface Cue permits Light, Rest {}
record Light(String colour, int seconds) implements Cue {}
record Rest(int seconds) implements Cue {}

String timed(String label, int seconds) {
    return label + " for " + seconds + "s";
}

String describe(Cue cue) {
    return switch (cue) {
        case null -> "No cue";
        case Light(var colour, var seconds) -> timed("Light " + colour, seconds);
        case Rest rest -> timed("Rest", rest.seconds());
    };
}

void main() {
    IO.println(describe(new Light("blue", 3)));
    IO.println(describe(new Rest(2)));
    IO.println(describe(null));
}
