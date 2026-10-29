/// # Task · Jotto
/// Lesson: 7.2 Visit values without picking each one — 02-traversal.html
///
/// Find a secret five-letter word from clues that reveal only how many letters you share. No positions
/// are revealed, so a new guess can test a different combination rather than merely rearranging the same
/// letters.
///
/// **Your mission:** The clue never rises above one even when several letters match. Repair matches so it
/// keeps a running count of every shared letter rather than forgetting earlier matches.
///
/// Enter STONE against the training word PLANT; T and N are shared, but the
/// starter reports only one.

int matches(String word) {
    int n = 0;
    for (char c : word.toCharArray()) {
        if (secret.indexOf(c) >= 0) {
            n = 1;
        }
    }
    return n;
}

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String[] words = {"PLANT", "TIGER", "BRICK", "GHOST", "MONEY", "WATER", "CHAIR", "CLOUD", "SPEAR", "STONE"};
String secret;
String outcome;
int attempts;
List<String> history;

void reset() {
    secret = words[new Random().nextInt(words.length)];
    attempts = 0;
    history = new ArrayList<>();
    outcome = "";
}

void command(String input) {
    require(outcome.isEmpty());
    String word = input.toUpperCase(Locale.ROOT);
    require(List.of(words).contains(word));
    attempts++;
    history.add(word + " -> " + matches(word) + " shared letters");
    if (word.equals(secret)) {
        outcome = "You win";
    } else {
        if (attempts == 10) {
            outcome = "Out of tries. Word: " + secret;
        }
    }
}

void display() {
    IO.println("Allowed words: " + String.join(" ", words));
    IO.println("Attempts " + attempts + "/10. No positions are revealed.");
    for (String row : history) {
        IO.println(row);
    }
}

void main() {
    reset();
    secret = "PLANT";
    IO.println("Training word: PLANT. /r chooses a random word.");
    IO.println("Jotto");
    IO.println("Guess a listed five-letter word with distinct letters. Ten turns. /r restarts; /q quits.");
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
        if (!outcome.isEmpty()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            command(input.strip());
            showState = true;
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move or command. Guess a listed five-letter word with distinct letters. Ten turns. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

// </editor-fold>
