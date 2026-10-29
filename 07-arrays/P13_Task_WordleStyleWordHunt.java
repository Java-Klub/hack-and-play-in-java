/// # Task · WordleStyleWordHunt
/// Lesson: 7.3 Change the actual place — 03-updates.html
///
/// Guess a five-letter word using clues for exact positions, misplaced letters and absent letters.
/// Repeated letters make the clues especially useful: one available copy cannot explain two different
/// guesses.
///
/// **Your mission:** The training word LEVEL contains two L letters, but one yellow match disappears for
/// ALLEY. Repair leftoverLetters so every unmatched secret letter contributes to its supplied counter
/// without erasing earlier occurrences.
///
/// Enter ALLEY against the training word LEVEL; compare the printed clue
/// with the two L letters that can both match elsewhere.

int[] leftoverLetters(String guess, char[] result) {
    int[] counts = new int[26];
    for (int i = 0; i < 5; i++) {
        if (secret.charAt(i) == guess.charAt(i)) {
            result[i] = 'G';
        } else {
            int letter = secret.charAt(i) - 'A';
            counts[letter] = 1;
        }
    }
    return counts;
}

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String[] words = {"LEVEL", "LEVER", "RIVER", "ROBOT", "APPLE", "ALLEY", "EAGER", "EARTH", "STONE", "MONEY"};
String secret;
String outcome;
List<String> history;

void reset() {
    secret = words[new Random().nextInt(words.length)];
    history = new ArrayList<>();
    outcome = "";
}

String grade(String guess) {
    char[] result = ".....".toCharArray();
    int[] counts = leftoverLetters(guess, result);
    for (int i = 0; i < 5; i++) {
        if (result[i] != 'G' && counts[guess.charAt(i) - 'A'] > 0) {
            result[i] = 'Y';
            counts[guess.charAt(i) - 'A']--;
        }
    }
    return new String(result);
}

void command(String input) {
    require(outcome.isEmpty());
    String word = input.toUpperCase(Locale.ROOT);
    require(List.of(words).contains(word));
    history.add(word + " " + grade(word));
    if (word.equals(secret)) {
        outcome = "You win!";
    } else {
        if (history.size() == 6) {
            outcome = "Out of tries. Word: " + secret;
        }
    }
}

void display() {
    IO.println("Allowed: " + String.join(" ", words));
    IO.println("G exact, Y elsewhere, . absent; attempts " + history.size() + "/6");
    for (String row : history) {
        IO.println(row);
    }
}

void main() {
    reset();
    secret = "LEVEL";
    IO.println("Training word: LEVEL. /r chooses a random word.");
    IO.println("Wordle-style Word Hunt");
    IO.println("Guess one allowed five-letter word in six tries. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Guess one allowed five-letter word in six tries. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

// </editor-fold>
