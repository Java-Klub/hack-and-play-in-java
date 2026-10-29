/// # Task · AnagramRounds
/// Lesson: 3.3 Let the player shape the story — 03-stories.html
///
/// Unscramble five words and see how many you can solve within three guesses each. Several genuine
/// anagrams may solve the same round, so a surprising word can still be right.
///
/// **Your mission · Make the judge fair:** A valid answer such as silent is rejected because its letter
/// case differs from the
/// word list. Repair normalize without losing the existing removal of outside spaces.
///
/// Run the program and type silent in the first round, then try a mixed-case spelling after the
/// repair to see whether it is judged the same way.

String normalize(String input) {
    return input.strip();
}

// <editor-fold defaultstate="expanded" desc="For this game">
/// A chain uses each operation's returned value as the receiver of the next operation. For example,
/// `"  Hi  ".strip().length()` first produces `Hi`, then its length, 2. Now predict the same chain
/// for `"  Hello  "`. Your own calculation must still return text, not its length. The available
/// case-conversion operation is `toUpperCase(Locale.ROOT)`; `Locale.ROOT` is a supplied constant
/// passed as an argument, not another method call. It keeps this word-list comparison consistent
/// across regional settings. Preserve the existing edge cleanup when adding the case conversion.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Replace one existing curated word group with a new group of genuine anagrams a friend can solve.
/// Keep the same array structure and uppercase spelling. Avoid groups depending on obscure
/// vocabulary; test that the displayed scramble and accepted alternatives really share the same
/// letters.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String[][] answers = {{"LISTEN", "SILENT", "ENLIST", "TINSEL"}, {"STONE", "TONES", "NOTES"}, {"EARTH", "HEART", "HATER"}, {"ANGEL", "GLEAN", "ANGLE"}, {"NIGHT", "THING"}};
int round;
int tries;
int score;
String scrambled;
String outcome;
String message;
String[] clues = {"Use your ears", "A small rock", "Our planet", "A winged figure in stories", "The time after sunset"};

void scramble() {
    var letters = new ArrayList<Character>();
    for (char c : answers[round][0].toCharArray()) {
        letters.add(c);
    }
    Collections.shuffle(letters);
    scrambled = "";
    for (char c : letters) {
        scrambled += c;
    }
}

void reset() {
    round = tries = score = 0;
    outcome = "";
    message = "Rearrange every shown letter into an English word. Example: TAC becomes CAT.";
    scramble();
}

void advance() {
    round++;
    tries = 0;
    if (round == 5) {
        outcome = "Finished: " + score + "/5";
    } else {
        scramble();
    }
}

void command(String input) {
    require(outcome.isEmpty());
    String word = normalize(input);
    if (word.equals("SKIP")) {
        message = "Skipped. One accepted answer was " + answers[round][0] + ".";
        advance();
        return;
    }
    if (!word.matches("[A-Z]+")) {
        throw new IllegalArgumentException("Use letters A-Z after cleanup. If a lowercase English answer is rejected, that is the normalize mission's fault; uppercase input still lets you play.");
    }
    if (List.of(answers[round]).contains(word)) {
        message = "Accepted: " + word + ". One point!";
        score++;
        advance();
    } else {
        tries++;
        message = word.length() != answers[round][0].length()
            ? "Use all " + answers[round][0].length() + " letters exactly once; " + word + " has " + word.length() + "."
            : "That is not an accepted rearrangement. The word list is small; /words shows the accepted options.";
        message += " Guess used: " + tries + "/3.";
        if (tries == 3) {
            message += " One accepted answer was " + answers[round][0] + ".";
            advance();
        }
    }
}

void display() {
    IO.println(message);
    if (round < 5) {
        IO.println("Round " + (round + 1) + "/5: " + scrambled + "; guesses used " + tries + "/3; score " + score);
        IO.println("Clue for one possible answer: " + clues[round] + ". Other accepted anagrams can count too.");
    }
}

void main() {
    reset();
    IO.println("Anagram Rounds");
    IO.println("Use every shown letter once to make one English word. Example: TAC becomes CAT. Three guesses per word, five rounds. SKIP reveals an answer; /words lists accepted words without a guess; /r restarts; /q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
            showState = false;
        }
        String input = IO.readln(outcome.isEmpty() ? "Your word, SKIP or /words: " : "Enter or /r: new round; /q: quit: ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r") || (!outcome.isEmpty() && input.isBlank())) {
            reset();
            showState = true;
            continue;
        }
        if (input.strip().equalsIgnoreCase("/words") && outcome.isEmpty()) {
            IO.println("Accepted words: " + String.join(", ", answers[round]));
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        if (input.isBlank()) {
            IO.println("Type a word using the shown letters, SKIP for the next puzzle, or /words for choices. No guess spent.");
            continue;
        }
        try {
            command(input.strip());
            showState = true;
        } catch (IllegalArgumentException invalid) {
            IO.println(invalid.getMessage());
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

// </editor-fold>
