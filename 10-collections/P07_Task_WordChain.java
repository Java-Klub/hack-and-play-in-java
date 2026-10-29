/// # Task · WordChain
/// Lesson: 10.1 A bag need not know its size ahead of time — 01-lists.html
///
/// Take turns extending a chain of words: each new word must begin with the last letter of the previous
/// one, and no word may be reused. Choose a continuation that leaves your opponent few options.
///
/// **Your mission:** The opening menu keeps only one word even though many are legal. Repair choices so
/// it retains every acceptable word discovered during the loop, rather than losing earlier discoveries.
///
/// Compare the full Dictionary with the single next choice; after repair,
/// enter APPLE and inspect the offered E continuations.

List<String> choices() {
    var list = new ArrayList<String>();
    for (String w : words) {
        if (!used.contains(w) && (last.isEmpty() || w.charAt(0) == last.charAt(last.length() - 1))) {
            list.clear();
            list.add(w);
        }
    }
    return list;
}

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String[] words = {"APPLE", "EARTH", "HOUSE", "EAGLE", "ELEPHANT", "TIGER", "ROBOT", "TREE", "ECHO", "ORBIT", "TRAIN", "NIGHT", "TABLE", "ENGINE", "RAIN", "NORTH", "HERO", "OCEAN", "NEST", "TOWER"};
Set<String> used;
String last;
String outcome;
int player;

void reset() {
    used = new LinkedHashSet<>();
    last = "";
    outcome = "";
    player = 1;
}

void command(String input) {
    require(outcome.isEmpty());
    String word = input.toUpperCase(Locale.ROOT);
    require(choices().contains(word));
    used.add(word);
    last = word;
    int previous = player;
    player = 3 - player;
    if (choices().isEmpty()) {
        outcome = "Player " + previous + " wins: no unused continuation.";
    }
}

void display() {
    IO.println("Dictionary: " + String.join(" ", words));
    IO.println("Player " + player + ", used: " + used + "; next choices: " + choices());
}

void main() {
    reset();
    IO.println("Word Chain");
    IO.println("Enter an unused listed word beginning with the last letter. A player with no continuation loses. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter an unused listed word beginning with the last letter. A player with no continuation loses. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

// </editor-fold>
