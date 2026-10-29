/// # L09.01 · WordLadder
///
/// Turn COLD into WARM by changing one letter at a time. Your trail is part of the puzzle: can you reach
/// the target without forgetting the route?
///
/// **Your mission:** After a valid move, the trail loses its earlier words. Repair the history update
/// while leaving rejected moves and restart trustworthy.
///
/// **Today you learn:** A mutable List remembers items in order. Here one accepted word should extend an
/// existing journey; replacing the whole list changes what the player remembers.
///
/// Run the program, then type CORD and then CARD to climb from COLD; use /r to restart and /q
/// to quit. After winning, Enter starts a new trail.
void command(String input) {
    require(outcome.isEmpty());
    String word = input.toUpperCase(Locale.ROOT);
    require(List.of(words).contains(word) && adjacent(current, word));
    current = word;
    history = new ArrayList<>(List.of(word));
    if (word.equals("WARM")) {
        outcome = "Reached WARM in " + (history.size() - 1) + " moves!";
    }
}

// <editor-fold defaultstate="expanded" desc="Learn · F22">
/// ### List: a sequence you can change
///
/// A `List<String>` holds text in order. `new ArrayList<>()` makes a mutable list: its contents can
/// change without replacing the list itself.
///
/// ```java
/// List<String> snacks = new ArrayList<>();
/// snacks.add("apple");
/// snacks.add("nuts");
/// IO.println(snacks.get(0));
/// IO.println(snacks.size());
/// ```
///
/// The output is `apple`, then `2`. `add(value)` appends one item; `get(index)` reads without
/// removing it. Indexes start at zero. `isEmpty()` checks for no items. `removeLast()` removes and
/// returns the final item; call it only on a nonempty list. A list made directly by `List.of(...)`
/// cannot be changed, but `new ArrayList<>(List.of(...))` can.
///
/// Predict: after `snacks.removeLast()`, what are its size and first item?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `history =` — Assignment replaces the list referenced by history; it does not extend that list.
/// - `List.of(word)` — Creates a list containing just this word, with no earlier route.
/// - `&&` — Both dictionary membership and the one-letter rule must pass before current changes.
/// - `history.size() - 1` — Words include the starting position, so moves are one fewer.
///
/// ### What the prepared part already does
///
/// main reads a word and command validates it before changing current. adjacent counts changed letters,
/// display prints the same history, and reset creates the initial COLD route.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="For this game">
/// ### The history operation is available here
///
/// You do not need another optional word game to discover this API. For a mutable list,
/// `list.add(item)` appends one item while preserving earlier items. It changes that same list. `list
/// = new ArrayList<>(...)` instead changes which list the variable names. `List.of(...)` itself is
/// unmodifiable; the supplied history is an `ArrayList`.
///
/// Trace accepted and rejected commands separately: start at COLD, accept CORD, then attempt WARM,
/// which differs by more than one letter. The rejected command must leave both the current word and
/// every history item unchanged. Inspect where validation ends before deciding where the history
/// operation belongs.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Inspect what assigning a new ArrayList does to the earlier words.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Use the mutating operation that appends one word to the existing sequence.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Trace COLD → CORD → CARD, then reject WARM without appending it.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String[] words = {"COLD", "CORD", "CARD", "WARD", "WARM", "WORD", "WORM", "GOLD", "SOLD", "TOLD", "HOLD"};
String current;
String outcome;
List<String> history;

/// Restore the starting game state.
void reset() {
    current = "COLD";
    history = new ArrayList<>(List.of(current));
    outcome = "";
}

boolean adjacent(String a, String b) {
    if (a.length() != b.length()) {
        return false;
    }
    int n = 0;
    for (int i = 0; i < a.length(); i++) {
        if (a.charAt(i) != b.charAt(i)) {
            n++;
        }
    }
    return n == 1;
}

void display() {
    IO.println("Dictionary: " + String.join(" ", words));
    IO.println(String.join(" -> ", history) + "; target WARM");
}

void main() {
    reset();
    IO.println("Word Ladder: transform COLD to WARM using the listed words. /r restarts; /q quits.");
    display();
    while (true) {
        boolean finished = !outcome.isEmpty();
        String input = IO.readln(finished ? "Enter or /r: new round; /q: quit > " : "> ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) return;
        if (input.strip().equalsIgnoreCase("/r") || finished && input.isBlank()) {
            reset();
            display();
            continue;
        }
        if (finished) {
            IO.println("This round is finished. Enter or /r starts a fresh trail; /q quits.");
            continue;
        }
        try {
            command(input.strip());
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move. Change one letter to a listed word; the trail has not changed.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
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
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
