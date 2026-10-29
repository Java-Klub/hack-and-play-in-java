void main() {
    String spell = IO.readln("Spell: ").strip().toLowerCase(Locale.ROOT);
    String effect = switch (spell) {
        case "red", "r" -> "Fire";
        case "blue", "b" -> "Ice";
        case "green", "g" -> "Vines";
        default -> "Fizzle";
    };
    IO.println("Effect: " + effect);
}
