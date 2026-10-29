/// # 03.11 · AnagramRounds
///
/// Unscramble five words and see how many you can solve within three guesses each. Several genuine
/// anagrams may solve the same round, so a surprising word can still be right.
///
/// **Your mission · Make the judge fair:** A valid answer such as silent is rejected because its letter
/// case differs from the
/// word list. Repair normalize without losing the existing removal of outside spaces.
///
/// **Today you learn:** String operations: a text operation produces a result that you must use. A line
/// can be cleaned at its edges and then normalized to the letter case expected by another part of the
/// program.
///
/// Run the program and type silent in the first round, then try a mixed-case spelling after the
/// repair to see whether it is judged the same way.
String normalize(String input) {
    return input.strip().toUpperCase(Locale.ROOT);
}

// <editor-fold defaultstate="collapsed" desc="Recall · F05">
/// `IO.readln` receives a whole line, including internal spaces. String operations return text rather
/// than changing the original String. Keep the returned result when you need it.
///
/// These example statements belong inside a called method.
///
/// ```java
/// String raw = "  Quiet Room  ";
/// String cleaned = raw.strip();
/// String loud = cleaned.toUpperCase(Locale.ROOT);
/// ```
///
/// Predict all three values. `raw` still has its outside spaces, `cleaned` is `Quiet Room`, and
/// `loud` is `QUIET ROOM`. `strip()` removes edge whitespace, not the space between words. The dot
/// selects an operation on the value to its left; parentheses pass its arguments. `Locale.ROOT` asks
/// for language-neutral case conversion rather than the computer’s regional default. The prepared
/// input code handles `null` at end of input; an empty line is a different value.
///
/// Now predict cleaned and loud for raw text `"  Soft Sky  "`.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `String normalize(String input)` — Receive one whole input line and return its normalized text.
/// - `input` — The text received for this particular call, including any typed spaces and mixed case.
/// - `.strip()` — Ask the text for a copy without leading and trailing whitespace; the empty parentheses
///   supply no extra input.
/// - `return input.strip();` — Return the result of the operation, rather than merely calling it and
///   discarding the new text.
///
/// ### What the prepared part already does
///
/// command uses normalize before checking the curated answers. scramble shuffles the displayed letters;
/// advance manages the five rounds and guess limits. The input loop already reads complete lines and
/// handles replay.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="For this game">
/// A chain uses each operation's returned value as the receiver of the next operation. For example,
/// `"  Hi  ".strip().length()` first produces `Hi`, then its length, 2. Now predict the same chain
/// for `"  Hello  "`. Your own calculation must still return text, not its length. The available
/// case-conversion operation is `toUpperCase(Locale.ROOT)`; `Locale.ROOT` is a supplied constant
/// passed as an argument, not another method call. It keeps this word-list comparison consistent
/// across regional settings. Preserve the existing edge cleanup when adding the case conversion.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Follow the values · predict a case">
/// Trace raw text `"  Moon  "`: strip returns `"Moon"`; converting that returned text to uppercase
/// gives `"MOON"`. Calling strip without keeping its return would leave your original reference
/// unchanged. Predict the normalized text for a mixed-case answer with one inner space. The game
/// decides accepted words separately from this cleanup.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Replace one existing curated word group with a new group of genuine anagrams a friend can solve.
/// Keep the same array structure and uppercase spelling. Avoid groups depending on obscure
/// vocabulary; test that the displayed scramble and accepted alternatives really share the same
/// letters.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// strip already removes the outer spaces.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// A String operation returns a new String; its result must be used.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Return input.strip().toUpperCase(Locale.ROOT).
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

/// Prepared entry points: main, reset, command.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Small Java map · optional">
/// ## An unfamiliar piece of Java?
///
/// This small map is optional. Look up just the symbol or word you need; no need to learn it all first.
///
/// - `///` — Words for the reader. Java skips these comment lines when running.
/// - `;` — Ends one statement, like a full stop. A semicolon inside a for header separates its three
///   parts.
/// - `()` — After a name: call an action, or list its parameters in its declaration. Empty parentheses
///   mean no supplied arguments.
/// - `{}` — Braces group a block of code. The opening and closing brace belong together.
/// - `.` — Accesses something belonging to the value or type on its left, such as IO.println.
/// - `void` — This method performs an action and returns no result value.
/// - `main` — Java starts the program here. Other method declarations do not run just because they appear
///   earlier in the file.
/// - `int` — A type for whole numbers within a fixed range.
/// - `boolean` — A value that is either true or false.
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `char` — A type for one UTF-16 code unit, often a simple board character such as X. Text uses
///   String.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
///   direction. In List<String>, angle brackets describe the element type.
/// - `:` — In for (int value : values), reads each element in turn. It also separates the two
///   alternatives of ? : or follows a switch label.
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `else` — The alternative when the preceding if condition is false.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
