/// # L09.01 · WordLadder
///
/// The one-word ladder works. Become a route planner: send several carefully chosen stops at once, and
/// make the machine refuse a bad plan without stranding you halfway.
///
/// **Your mission:** Accept a space-separated route as well as one word. Every stop must be a listed
/// word one letter from the previous stop; strip outer whitespace and ignore letter case. Validate the
/// whole route before changing play. A bad later stop leaves the original word and history untouched.
/// Reaching WARM is allowed only as the final stop and counts individual moves, not submissions.
///
/// **Today you learn:** Build and validate a candidate List before publishing it. Order matters twice:
/// between planned words and between the saved journey’s entries.
///
/// Use the printed dictionary to plan a route. The starter accepts CORD, then CARD separately. Your new
/// planner must also accept cord card in one submission. Try WARD COLD from CARD: the entire bad plan
/// must be refused. /r restarts; /q quits.

// <editor-fold defaultstate="expanded" desc="Program · explore the rules">
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

void command(String input) {
    require(outcome.isEmpty());
    String word = input.strip().toUpperCase(Locale.ROOT);
    require(List.of(words).contains(word) && adjacent(current, word));
    current = word;
    history.add(word);
    if (word.equals("WARM")) outcome = "Reached WARM in " + (history.size() - 1) + " moves!";
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
            IO.println("Invalid route. Use listed words one letter apart; the trail has not changed.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>


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
/// ### Observe, then trace the flow
///
/// A plan and an executed journey are different Lists. You may extend a temporary copy while checking
/// each proposed stop; only publish it when all checks pass. The previous word for the next planned
/// step is the most recent candidate, not always the real current word. String.split("\\s+") produces
/// words separated by one or more whitespace characters; the doubled backslash is Java’s way to pass a
/// backslash to that separator pattern.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Experiment and replay">
/// Submit CORD CARD, record the whole trail, then try WARD COLD. The refusal must leave CARD at the
/// end. Submit WARD WARM to finish in four moves. Restart and test a single word too; the planning tool
/// must preserve ordinary play.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Plan a valid first step followed by an invalid second one. Which real values must still match the
/// before picture?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Follow the console submission into the word-transition action. Work on a copy of its ordered history
/// while checking proposed words in order.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Find command. Normalise and split the input, copy history into a mutable route, and validate each
/// candidate against the previous candidate. After all checks, replace history/current once and compute
/// any final WARM outcome.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Design a new small dictionary and write two plans: a shortest known route and a longer scenic route.
/// Let a partner predict which submitted plan is rejected.
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
