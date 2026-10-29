/// # L03.05 · Chomp
///
/// Two players take bites from one chocolate bar. Each bite removes a rectangle towards the lower right,
/// but eating the poisoned top-left piece loses the game.
///
/// **Your mission:** The visible fourth row cannot be bitten. Repair the range guard in bite while
/// continuing to reject chocolate that is outside the bar or already eaten.
///
/// **Today you learn:** Bounds before access: a guard must accept the boundary that belongs to the game
/// and reject the next value. It also protects the later row lookup from using a nonexistent row.
///
/// From the course folder containing README.md:
/// - Play: `java L03/M05_Chomp.java`
/// - Check this mission: `java L03/Check.java M05_Chomp.java`
///
/// Run the play command above and enter 4 5 to bite the visible bottom-right piece, then try the
/// same coordinates again after it is gone. After your repair: save the file, run the check command
/// above, then play again.
///
/// Need an explanation? Recall · F08 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
boolean bite(int row, int column) {
    if (winner != 0 || row < 1 || row >= 4 || column < 1 || column > widths[row - 1]) {
        return false;
    }
    for (int i = row - 1; i < widths.length; i++) {
        widths[i] = Math.min(widths[i], column - 1);
    }
    if (row == 1 && column == 1) {
        winner = 3 - player;
    } else {
        player = 3 - player;
    }
    return true;
}

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

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `row >= 4` — The current upper-row check includes the last real row in its rejection.
/// - `||` — Any bad condition rejects the bite; earlier bounds can stop evaluation before a row is
///   accessed.
/// - `widths[row - 1]` — The prepared row-width table tells how many pieces remain in that row;
///   subtracting one converts its human number.
/// - `column > widths[row - 1]` — Reject a column beyond the remaining chocolate in this particular row.
/// - `return false;` — Leave before removing chocolate or changing the player.
/// - `return true;` — Report an accepted bite after the supplied removal and poison rules have run.
///
/// ### What the prepared part already does
///
/// The prepared loop shrinks all affected rows after the guard passes. The remaining branch handles
/// poison and turn changes, while main draws the uneven bar and reads row/column pairs. You only repair
/// the entry boundary.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Row four belongs to the bar.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Do not remove the bounds guard: it also protects widths[row - 1].
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// The row rejection is row < 1 || row > 4.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[] widths = {5, 5, 5, 5};
int player = 1;
int winner;

void reset() {
    Arrays.fill(widths, 5);
    player = 1;
    winner = 0;
}

void main() {
    IO.println("CHOMP: two players share a chocolate bar.");
    IO.println("A bite removes its cell and the rectangle below and right.");
    IO.println("P at row 1, column 1 is poisoned. Eating P loses.");
    boolean showState = true;
    while (true) {
        if (showState) {
            IO.println("    1 2 3 4 5");
            for (int row = 0; row < 4; row++) {
                String line = (row + 1) + " : ";
                for (int column = 0; column < 5; column++) {
                    line += column >= widths[row] ? ". " : row == 0 && column == 0 ? "P " : "# ";
                }
                IO.println(line);
            }
            if (winner != 0) {
                IO.println("Player " + winner + " wins!");
            }
            showState = false;
        }
        String input = IO.readln(winner == 0 ? "Player " + player + ": row column, r restart, q quit: " : "Enter or /r: new round; /q: quit: ");
        if (input == null || (input.trim().equalsIgnoreCase("q") || input.trim().equalsIgnoreCase("/q"))) {
            return;
        }
        input = input.trim();
        if ((input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r") || (winner != 0 && input.isBlank()))) {
            reset();
            showState = true;
            continue;
        }
        if (winner != 0) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            String[] parts = input.split("\\s+");
            if (parts.length == 2 && bite(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]))) {
                showState = true;
            } else {
                IO.println("Illegal bite. Enter row and column of remaining chocolate.");
            }
        } catch (NumberFormatException exception) {
            IO.println("Enter two numbers, r or q.");
        }
    }
}

/// Prepared entry points: main, reset.
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
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `!=` — Checks that two values differ.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+=` — Adds to the old value and stores the result back under the same name.
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
/// - `else` — The alternative when the preceding if condition is false.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>

