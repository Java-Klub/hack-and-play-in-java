/// # Worked example · 5.5 Combine small referees
/// Lesson: 05-composition.html

void main() {
    String input = IO.readln("Spell: ");
    String spell = clean(input);
    IO.println("Effect: " + effect(spell));
}

String clean(String input) {
    return input.strip().toLowerCase(Locale.ROOT);
}

boolean isKnown(String spell) {
    return spell.equals("red") || spell.equals("blue");
}

String effect(String spell) {
    if (!isKnown(spell)) {
        return "Fizzle";
    }
    return switch (spell) {
        case "red" -> "Fire";
        default -> "Ice";
    };
}
