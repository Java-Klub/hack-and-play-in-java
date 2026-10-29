/// # L04.10 · WordleStyleWordHunt
///
/// Guess a five-letter word using clues for exact positions, misplaced letters and absent letters.
/// Repeated letters make the clues especially useful: one available copy cannot explain two different
/// guesses.
///
/// **Your mission:** The training word LEVEL contains two L letters, but one yellow match disappears for
/// ALLEY. Repair leftoverLetters so every unmatched secret letter contributes to its supplied counter
/// without erasing earlier occurrences.
///
/// **Today you learn:** Several accumulators: the supplied counts has one counter per letter. Two
/// unmatched L discoveries must both contribute to the same counter; exact-position matches have already
/// been set aside.
///
/// From the course folder containing README.md:
/// - Play: `java L04/M10_WordleStyleWordHunt.java`
/// - Check this mission: `java L04/Check.java M10_WordleStyleWordHunt.java`
///
/// Run the play command above and enter ALLEY against the training word LEVEL; compare the printed
/// clue with the two L letters that can both match elsewhere. After your repair: save the file, run
/// the check command above, then play again.
///
/// Need an explanation? Recall · F12 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
int[] leftoverLetters(String guess, char[] result) {
    int[] counts = new int[26];
    for (int i = 0; i < 5; i++) {
        if (secret.charAt(i) == guess.charAt(i)) {
            result[i] = 'G';
        } else {
            int letter = secret.charAt(i) - 'A';
            counts[letter] += 1;
        }
    }
    return counts;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F12">
/// An accumulator remembers a growing result across passes. Its starting value belongs before the
/// loop; putting it inside would start the result again for every item. The remembered result can be
/// text, not only a number.
///
/// These example statements belong inside a called method.
///
/// ```java
/// String receipt = "Items:";
/// for (String item : List.of("tea", "cake")) {
///     receipt += " " + item;
/// }
/// ```
///
/// Predict receipt after each pass: `Items: tea`, then `Items: tea cake`. `+=` combines the old value
/// with the new contribution and stores the result. Plain `=` replaces the stored value.
/// `List.of(...)` supplies the fixed sequence of listed values; the colon visits one item per pass.
/// Trace the current item and accumulator in two columns to discover whether earlier work survives.
///
/// Now predict receipt when the supplied list contains only `"rice"`.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int[] counts = new int[26];` — The supplied storage starts twenty-six counters at zero, one for
///   each capital letter.
/// - `for (int i = 0; i < 5; i++)` — Inspect the five positions once each, using indices zero through
///   four.
/// - `result[i] = 'G';` — The prepared branch marks an exact-position match with the single character G.
/// - `int letter = secret.charAt(i) - 'A';` — This supplied conversion chooses the letter’s counter: A is
///   zero, B is one, and so on.
/// - `counts[letter] = 1;` — The square brackets select one counter; this assignment currently replaces
///   its previous count.
/// - `return counts;` — Pass all remaining letter counts to the second clue-making pass.
///
/// ### What the prepared part already does
///
/// grade calls leftoverLetters, then spends those remaining counts on yellow matches so no secret letter
/// is used twice. command checks the printed allowed-word list and stores up to six guesses; display
/// explains G, Y and the dot. The first round uses LEVEL, while /r chooses a random word.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// For LEVEL versus ALLEY, both L letters remain unmatched initially.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// counts[letter] is one prepared counter for that letter.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Use counts[letter] += 1 instead of = 1.
// </editor-fold>

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
/// - `'` — Single quotes enclose one character, such as a board marker.
/// - `char` — A type for one UTF-16 code unit, often a simple board character such as X. Text uses
///   String.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `!=` — Checks that two values differ.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
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
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>

