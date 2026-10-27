/// # L04.12 · WordChain
///
/// Take turns extending a chain of words: each new word must begin with the last letter of the previous
/// one, and no word may be reused. Choose a continuation that leaves your opponent few options.
///
/// **Your mission:** The opening menu keeps only one word even though many are legal. Repair choices so
/// it retains every acceptable word discovered during the loop, rather than losing earlier discoveries.
///
/// **Today you learn:** Accumulating a collection: the running result can be a list rather than a number.
/// Create the empty list once, then keep earlier entries while later legal choices are discovered.
///
/// Use Run File and compare the full Dictionary with the single next choice; after repair, enter APPLE
/// and inspect the offered E continuations.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F12 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
List<String> choices() {
    var list = new ArrayList<String>();
    for (String w : words) {
        if (!used.contains(w) && (last.isEmpty() || w.charAt(0) == last.charAt(last.length() - 1))) {
            list.add(w);
        }
    }
    return list;
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
/// - `var list = new ArrayList<String>();` — The supplied line creates an empty list for text entries;
///   var lets the compiler infer its fixed type from this initial value.
/// - `for (String w : words)` — Consider each supplied dictionary word once, naming the current word w.
/// - `!used.contains(w)` — This prepared filter requires a word that has not already been played.
/// - `list.clear();` — Erase every entry collected so far; consider what that means after several
///   successful passes.
/// - `list.add(w);` — Append the current accepted word to the running result.
/// - `return list;` — Return the collected legal choices together for both display and move validation.
///
/// ### What the prepared part already does
///
/// The rest of the supplied filter already checks the required first letter, with no such restriction on
/// the opening word. command uses choices to validate a move and records used words; display uses the
/// same result for its menu, so your repair affects actual legal play.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// list.clear erases earlier accepted words.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The empty list already exists before the loop starts.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Remove list.clear(); keep list.add(w).
// </editor-fold>

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
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
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
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `Set` — Stores distinct values; the same value is present at most once.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>

