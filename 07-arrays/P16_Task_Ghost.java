/// # Task · Ghost
/// Lesson: 7.4 Find a place that fits — 04-search.html
///
/// Build a word fragment together, adding one letter each turn. Completing a dictionary word of at least
/// four letters, or leaving no possible continuation, makes the current player lose.
///
/// **Your mission:** R should begin ROBOT, ROBIN or ROCK, but the unfinished search rejects every
/// prefix. Build a while search over words: return true at the first startsWith match, otherwise
/// advance and return false only after the dictionary ends.
///
/// Read the printed dictionary and type R; the starter wrongly declares a dead
/// prefix despite the listed R words.

boolean possiblePrefix(String candidate) {
    int index = 0;
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
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
