/// # 08.07 · TicTacToe
///
/// Play tic-tac-toe with a friend by choosing numbered empty squares. Try to make a line of three while
/// blocking the other player’s line.
///
/// **Your mission · Build a move gatekeeper:** All squares are locked. Implement mayPlay as the referee:
/// reject a finished
/// game, cell numbers outside 1..9 and occupied squares. A legal empty square returns true. Keep the
/// prepared play effects; a rejected move must change neither board nor turn.
///
/// **Today you learn:** Guard clauses: return early when an action is forbidden, before any state
/// changes. Test the last legal cell, a repeated cell and a number just outside the range.
///
/// Run the program, choose cell 9 and then try it again; compare an accepted empty square with
/// a rejected occupied square.
boolean mayPlay(int cell) {
    if (winner != '.' || cell < 1 || cell > 9) {
        return false;
    }
    if (board[cell - 1] != '.') {
        return false;
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Applying an accepted move · prepared">
boolean play(int cell) {
    if (!mayPlay(cell)) {
        return false;
    }
    board[cell - 1] = player;
    moves++;
    if (hasLine(player)) {
        winner = player;
    } else if (moves == 9) {
        winner = 'D';
    } else {
        player = player == 'X' ? 'O' : 'X';
    }
    return true;
}
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Learn · F08">
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
/// ## Permission before effects
///
/// mayPlay returns a boolean and does not place a mark. winner is '.' while the round continues;
/// board stores '.' for an empty square. Player-facing cells 1..9 become array indices 0..8 through
/// cell - 1. play already uses your decision, then places the mark, counts it, checks a line and
/// switches player. Today you author the permission rule, not those later effects.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Follow the values · predict a case">
/// Guard in stages. First reject a finished game or a number outside 1..9; only then may cell - 1
/// index the array. Next reject an occupied square. True is permission, not the move itself: play
/// performs the supplied effects only after mayPlay returns true. Predict whether cell 10 ever
/// reaches the board lookup in a correct version.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Replace X and O with your own two single-character player marks. Keep '.' for empty cells and 'D'
/// for a draw. Change the initial player, reset and the player-switch rule consistently, then play a
/// full round with a friend. Try an occupied square and a winning line again: custom marks must not
/// bypass your referee.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Separate whether a square exists from whether it is empty.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Return false before indexing when cell is outside 1..9.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Reject winner != '.' or an invalid range first; then reject board[cell - 1] != '.'; otherwise
/// return true.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
char[] board = ".........".toCharArray();
char player = 'X';
char winner = '.';
int moves;

void reset() {
    Arrays.fill(board, '.');
    player = 'X';
    winner = '.';
    moves = 0;
}

boolean hasLine(char mark) {
    for (int i = 0; i < 3; i++) {
        if (board[i * 3] == mark && board[i * 3 + 1] == mark && board[i * 3 + 2] == mark) {
            return true;
        }
        if (board[i] == mark && board[i + 3] == mark && board[i + 6] == mark) {
            return true;
        }
    }
    return board[0] == mark && board[4] == mark && board[8] == mark
        || board[2] == mark && board[4] == mark && board[6] == mark;
}

String cell(int index) {
    return board[index] == '.' ? Integer.toString(index + 1) : Character.toString(board[index]);
}

void main() {
    reset();
    IO.println("TIC-TAC-TOE: two players, three in a row wins.");
    boolean showState = true;
    while (true) {
        if (showState) {
            for (int row = 0; row < 3; row++) {
                IO.println(cell(row * 3) + " | " + cell(row * 3 + 1) + " | " + cell(row * 3 + 2));
            }
            if (winner != '.') {
                IO.println(winner == 'D' ? "Draw!" : winner + " wins!");
            }
            showState = false;
        }
        String input = IO.readln(winner == '.' ? player + ": cell 1-9, r restart, q quit: " : "Enter or /r: new round; /q: quit: ");
        if (input == null || (input.trim().equalsIgnoreCase("q") || input.trim().equalsIgnoreCase("/q"))) {
            return;
        }
        input = input.trim();
        if ((input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r") || (winner != '.' && input.isBlank()))) {
            reset();
            showState = true;
            continue;
        }
        if (winner != '.') {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            if (play(Integer.parseInt(input))) {
                showState = true;
            } else {
                IO.println("Illegal move. Choose an empty cell during an unfinished round.");
            }
        } catch (NumberFormatException exception) {
            IO.println("Enter a cell number, r or q.");
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
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
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
