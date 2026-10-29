/// # 14.03 · Hanoi
///
/// **Your mission · Find the shortest forbidden sequence:** The tower rules already work. Find a
/// shortest two-attempt demonstration: the first move is legal; the second tries to put a larger disc
/// on a smaller one and is rejected without changing the tower. Store those two commands as the
/// program’s demonstration. Changing the guard to permit the forbidden move is not the goal.
///
/// The starter works by its original rules. The check already expects your new goal.
///
/// Use A B or other peg pairs to investigate; r restarts. D replays your stored commands from a fresh
/// tower and prints moved/rejected. The initial demonstration contains only one legal move.

// <editor-fold defaultstate="collapsed" desc="Recall · F08">
/// A guard rejects a request before protected work happens. Read the rejected cases first, then ask
/// what remains allowed. Short-circuit evaluation matters: in `A || B`, Java skips B when A is
/// already true.
///
/// ```java
/// boolean useSlot(int slot) {
///     if (slot < 0 || slot >= 4) {
///         return false;
///     }
///     IO.println("Using slot " + slot);
///     return true;
/// }
/// ```
///
/// Predict what is printed and returned for `-1`, `3` and `4`. Only three reaches the print. A
/// rejection must happen before any state change that should not occur. A board access may itself
/// need a range guard before it is safe to evaluate. `return false` ends this method call; it does
/// not automatically undo work already performed.
///
/// `useSlot(3)` passes three into `slot`; `boolean` declares a true/false result. Now predict slot
/// zero.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Explore and experiment">
/// A useful negative example reaches the actual forbidden situation. A nonexistent peg is also
/// invalid, but does not prove the size rule. First play manually, then replay your evidence. Read
/// the guard to explain why one attempt passes and the other returns before writes. A String can
/// carry two commands using \n; the supplied runner translates command data into moves. No new loop
/// needs to be written.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · search area">
/// Can the first move be forbidden by size when both other pegs are empty? What changes after moving
/// the smallest disc?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a concrete route">
/// D reads the String returned by demonstration(). Each line is one ordinary command. Keep the guard
/// unchanged.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Possible change · reveals the direction">
/// One valid example is A B followed by A B. Encode the line break as \n inside the quoted String.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Find a different equally short demonstration, then a longer one exposing the same guard. Ask a
/// partner to remove redundant moves without losing the forbidden situation.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Program · explore as needed">
String demonstration() {
    return "A B\nA B";
}

boolean move(int from, int to) {
    if (won() || from < 0 || from > 2 || to < 0 || to > 2 || from == to || sizes[from] == 0) {
        return false;
    }
    int disc = pegs[from][sizes[from] - 1];
    if (sizes[to] > 0 && pegs[to][sizes[to] - 1] < disc) {
        return false;
    }
    sizes[from]--;
    pegs[from][sizes[from]] = 0;
    pegs[to][sizes[to]] = disc;
    sizes[to]++;
    moves++;
    return true;
}


int[][] pegs = {{3, 2, 1}, {0, 0, 0}, {0, 0, 0}};
int[] sizes = {3, 0, 0};
int moves;

void reset() {
    for (int[] peg : pegs) {
        Arrays.fill(peg, 0);
    }
    pegs[0][0] = 3;
    pegs[0][1] = 2;
    pegs[0][2] = 1;
    sizes[0] = 3;
    sizes[1] = 0;
    sizes[2] = 0;
    moves = 0;
}

boolean won() {
    return sizes[2] == 3;
}

void main() {
    IO.println("HANOI: move all three discs from A to C.");
    IO.println("Move only the top disc, never a larger disc onto a smaller one.");
    IO.println("The top disc is the rightmost number on each peg. D: replay your demonstration from a fresh tower.");
    boolean showState = true;
    while (true) {
        if (showState) {
            for (int i = 0; i < 3; i++) {
                IO.println((char) ('A' + i) + ": " + Arrays.toString(Arrays.copyOf(pegs[i], sizes[i])));
            }
            IO.println("Moves: " + moves);
            if (won()) {
                IO.println("Solved! The minimum for three discs is 7 moves.");
            }
            showState = false;
        }
        String input = IO.readln(won() ? "Enter or /r: new round; /q: quit: " : "From to, e.g. A C; r restart, q quit: ");
        if (input == null || (input.trim().equalsIgnoreCase("q") || input.trim().equalsIgnoreCase("/q"))) {
            return;
        }
        input = input.trim().toUpperCase(Locale.ROOT);
        if ((input.equals("R") || input.equals("/R") || (won() && input.isBlank()))) {
            reset();
            showState = true;
            continue;
        }
        if (input.equals("D") || input.equals("/D")) {
            reset();
            int attempt = 0;
            for (String line : demonstration().lines().toList()) {
                String[] pair = line.strip().toUpperCase(Locale.ROOT).split("\\s+");
                boolean accepted = pair.length == 2 && pair[0].length() == 1 && pair[1].length() == 1
                    && move(pair[0].charAt(0) - 'A', pair[1].charAt(0) - 'A');
                IO.println("Demo " + (++attempt) + ": " + line + " -> " + (accepted ? "moved" : "rejected"));
            }
            showState = true;
            continue;
        }
        if (won()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        String[] parts = input.split("\\s+");
        if (parts.length == 2 && parts[0].length() == 1 && parts[1].length() == 1
                && move(parts[0].charAt(0) - 'A', parts[1].charAt(0) - 'A')) {
            showState = true;
        } else {
            IO.println("Illegal move. Use two different pegs A-C with a legal top disc.");
        }
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
/// - `'` — Single quotes enclose one character, such as a board marker.
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `char` — A type for one UTF-16 code unit, often a simple board character such as X. Text uses
///   String.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `++` — Increases a numeric value by one.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
///   direction. In List<String>, angle brackets describe the element type.
/// - `?` — In condition ? first : second, chooses the first value when the condition is true, otherwise
///   the second.
/// - `:` — In for (int value : values), reads each element in turn. It also separates the two
///   alternatives of ? : or follows a switch label.
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
