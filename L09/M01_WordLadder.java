/// # L09.01 · WordLadder
///
/// Turn COLD into WARM by changing one letter at a time. Your trail is part of the puzzle: can you reach
/// the target without forgetting the route?
///
/// **Your mission:** Build command(input): strip whitespace, uppercase with Locale.ROOT, accept only a
/// dictionary word differing in exactly one letter, update current and append the accepted word to
/// history. Reaching WARM sets the outcome with history.size()-1 moves. Reject invalid moves or a
/// finished round before changing either current or history.
///
/// **Today you learn:** Build an ordered, mutable collection operation and make its accepted and empty
/// cases precise.
///
/// Run the program, then type CORD and then CARD to climb from COLD; use /r to restart and /q
/// to quit. After winning, Enter starts a new trail.
void command(String input) {
    require(outcome.isEmpty());
}

// <editor-fold defaultstate="expanded" desc="Learn · F22">
/// ### A List gives changing things an order
///
/// An array has a fixed number of slots. A mutable List can grow or shrink while keeping an order.
/// `List<T>` describes access to elements of type T; `ArrayList<T>` is a JDK implementation you can
/// change.
///
/// ```java
/// List<String> trail = new ArrayList<>();
/// trail.add("north");
/// trail.add("east");
/// String latest = trail.removeLast();
/// IO.println(trail + " / " + latest);
/// ```
///
/// The result is `[north] / east`. Add appends at the end; removeLast both removes and returns the last
/// element. Check isEmpty before removing from an empty list. A queue removes at the front and appends
/// at the back; an undo stack removes the latest item instead. The intended order determines which end
/// to use.
///
/// `List.of("north", "east")` supplies an unmodifiable list: reading works, adding does not. `new
/// ArrayList<>(existing)` creates a mutable list with the existing membership. Predict the list after
/// adding `"south"` to trail. If two changes are undone, which should disappear first?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read this part
///
/// `words` is the visible finite dictionary; /words lists it in play. `adjacent(a,b)` already checks a
/// one-letter difference. `require(false)` rejects an action by throwing IllegalArgumentException. The
/// initial word is already in history, so words visited and moves taken differ by one. This command
/// builds one accepted transition; the console loop supplies restart and quit.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Validate a proposed word completely before changing the journey.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Use the supplied dictionary and adjacent check; accepted words extend history rather than replacing
/// it.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// After both guards, store the normalized word, append it, and set the WARM outcome using moves, not
/// the number of visited words.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Create a new four-letter dictionary, start and target, with at least one known route. Print its
/// allowed words so the challenge stays discoverable offline.
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
