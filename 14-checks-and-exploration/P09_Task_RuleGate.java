/// # Solved task · 14.2 assemble two word rules
/// Lesson: 02-interfaces.html

interface Rule {
    boolean allows(String word);
}

record Minimum(int length) implements Rule {
    public boolean allows(String word) {
        return word.length() >= length;
    }
}

record NotWord(String forbidden) implements Rule {
    public boolean allows(String word) {
        return !word.equalsIgnoreCase(forbidden);
    }
}

record Both(Rule first, Rule second) implements Rule {
    public boolean allows(String word) {
        return first.allows(word) && second.allows(word);
    }
}

Rule gateRule() {
    return new Both(new Minimum(4), new NotWord("robot"));
}

void main() {
    String reply = IO.readln("Word: ");
    if (reply == null) return;
    IO.println("Allowed: " + gateRule().allows(reply.strip()));
}
