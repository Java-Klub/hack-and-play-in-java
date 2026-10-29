/// # L04.11 · Jotto
///
/// Find a secret five-letter word from clues that reveal only how many letters you share. No positions
/// are revealed, so a new guess can test a different combination rather than merely rearranging the same
/// letters.
///
/// **Your mission:** The clue never rises above one even when several letters match. Repair matches so it
/// keeps a running count of every shared letter rather than forgetting earlier matches.
///
/// **Today you learn:** Accumulating conditionally: visit every guess letter, but add a contribution only
/// when the supplied match condition is true. The result is a count of discoveries, not simply whether
/// any discovery happened.
///
/// Run the program and enter STONE against the training word PLANT; T and N are shared, but the
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
/// - `int n = 0;` — Initialize the match count once, before reading any guess letters.
/// - `for (char c : word.toCharArray())` — For each character of the guessed word, run the body once with
///   that character named c.
/// - `secret.indexOf(c) >= 0` — The supplied search returns a nonnegative position when c occurs in the
///   secret; zero is a valid match too.
/// - `n = 1;` — This assignment resets n to one instead of retaining the work from earlier matching
///   letters.
/// - `return n;` — Give the final count to the history display after all five letters have been examined.
///
/// ### What the prepared part already does
///
/// The supplied dictionary uses words with five different letters, so this task need not decide how
/// repeated letters count. command validates a listed guess, calls matches and stores the clue; main
/// supplies ten turns, /r and /q. The first secret is PLANT.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Setting n to one loses previous matches.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The dictionary already supplies words with distinct letters.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Replace n = 1 with n++ or n += 1.
// </editor-fold>

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
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
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
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
