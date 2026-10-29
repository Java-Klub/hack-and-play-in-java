/// # Úkol · Ghost
/// Lekce: 7.4 Najdi místo, které vyhovuje — 04-search.html
///
/// Společně stavte začátek slova a v každém tahu přidejte jedno písmeno. Hráč prohraje, pokud dokončí
/// slovníkové slovo alespoň o čtyřech písmenech nebo nezanechá žádné možné pokračování.
///
/// **Tvoje mise:** R má být začátkem ROBOT, ROBIN či ROCK, ale nehotové hledání odmítá každý prefix
/// (začátek). Sestav hledání while přes words: při první shodě startsWith vrať true, jinak pokračuj a
/// false vrať až po konci slovníku.
///
/// Přečti vypsaný slovník a napiš R; starter (výchozí program) chybně oznámí
/// slepý začátek navzdory uvedeným slovům na R.

boolean possiblePrefix(String candidate) {
    int index = 0;
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String[] words = {"GHOST", "GHOUL", "GAME", "GAMER", "GARDEN", "ROBOT", "ROBIN", "ROCK", "STONE", "STORY", "START", "STARTER"};
String fragment;
String outcome;
int player;

void reset() {
    fragment = "";
    outcome = "";
    player = 1;
}

void command(String input) {
    require(outcome.isEmpty());
    String letter = input.toUpperCase(Locale.ROOT);
    require(letter.matches("[A-Z]"));
    fragment += letter;
    boolean possible = possiblePrefix(fragment);
    if (!possible || fragment.length() >= 4 && List.of(words).contains(fragment)) {
        outcome = "Player " + (3 - player) + " wins; player " + player + " completed a word or dead prefix.";
    } else {
        player = 3 - player;
    }
}

void display() {
    IO.println("Public dictionary: " + String.join(" ", words));
    IO.println("Player " + player + ", fragment: " + fragment);
}

void main() {
    reset();
    IO.println("Ghost");
    IO.println("Append one letter; completing a word of 4+ letters or a dead prefix loses. /r restarts; /q quits.");
    IO.println("The dictionary is deliberately small. /words lists its words without using a turn.");
    boolean showState = true;
    while (true) {
        if (showState) {
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
            showState = false;
        }
        String input = IO.readln(outcome.isEmpty() ? "> " : "Enter or /r: new round; /q: quit: ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r") || (!outcome.isEmpty() && input.isBlank())) {
            reset();
            showState = true;
            continue;
        }
        if (input.strip().equalsIgnoreCase("/words")) {
            IO.println("This small game uses only these words: " + String.join(", ", words));
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            command(input.strip());
            showState = true;
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move or command. Append one letter; completing a word of 4+ letters or a dead prefix loses. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

// </editor-fold>
