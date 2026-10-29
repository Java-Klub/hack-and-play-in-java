/// # Solved task · EvilHangman
/// Lesson: 10.1 A bag need not know its size ahead of time — 01-lists.html
///
/// Original assignment (this version is complete):
///
/// Play hangman against a computer that has not committed to one secret word. After each guess it keeps
/// the largest group of words compatible with its next clue, so every revealed letter costs it options.
///
/// **Your mission:** The unfinished counter returns zero for every family, so the adversary cannot
/// compare its choices. Build a loop that adds one contribution per word and returns the accumulated
/// total. An empty family must remain zero.
///
/// Guess E to see the chosen clue, then run the checks to compare a
/// three-word family with a one-word family.

int familySize(List<String> family) {
    int total = 0;
    for (int i = 0; i < family.size(); i++) {
        total += 1;
    }
    return total;
}

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String[] dictionary = {"ALLY", "BETA", "COOL", "DEAL", "ELSE", "FLEW", "GOOD", "HOPE", "IBEX", "JAZZ", "KEPT", "LEND"};
List<String> pool;
String mask;
String tried;
String outcome;
int misses;

void reset() {
    pool = new ArrayList<>(List.of(dictionary));
    mask = "____";
    tried = "";
    misses = 0;
    outcome = "";
}

String pattern(String word, char letter) {
    String s = "";
    for (int i = 0; i < 4; i++) {
        s += word.charAt(i) == letter ? letter : mask.charAt(i);
    }
    return s;
}

void command(String input) {
    require(outcome.isEmpty());
    String letter = input.toUpperCase(Locale.ROOT);
    require(letter.matches("[A-Z]") && !tried.contains(letter));
    var families = new TreeMap<String, List<String>>();
    for (String word : pool) {
        families.computeIfAbsent(pattern(word, letter.charAt(0)), (k)->new ArrayList<>()).add(word);
    }
    String best = "";
    int count = -1;
    for (var entry : families.entrySet()) {
        if (familySize(entry.getValue()) > count) {
            best = entry.getKey();
            count = familySize(entry.getValue());
        }
    }
    pool = new ArrayList<>(families.get(best));
    if (best.equals(mask)) {
        misses++;
    }
    mask = best;
    tried += letter;
    if (!mask.contains("_")) {
        outcome = "You win: " + mask;
    } else {
        if (misses == 6) {
            outcome = "You lose. A consistent word: " + pool.getFirst();
        }
    }
}

void display() {
    IO.println(mask + "  Tried: " + tried + "  Misses: " + misses + "/6");
    IO.println("Largest family stays; ties use alphabetical mask order.");
}

void main() {
    reset();
    IO.println("Evil Hangman");
    IO.println("Guess one letter. The computer keeps a consistent largest word family. /r restarts; /q quits.");
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
            IO.println("This small game uses only these words: " + String.join(", ", dictionary));
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
            IO.println("Invalid move or command. Guess one letter. The computer keeps a consistent largest word family. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

// </editor-fold>
