void main() {
    IO.println(signal(Mode.CALM));
    IO.println(signal(Mode.ALERT));
    IO.println(signal(Mode.ASLEEP));
}

String signal(Mode mode) {
    return switch (mode) {
        case CALM -> "Blue";
        case ALERT -> "Red";
        case ASLEEP -> "Dark";
    };
}

enum Mode {
    CALM, ALERT, ASLEEP
}
