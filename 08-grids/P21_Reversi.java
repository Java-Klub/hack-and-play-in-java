/// # 08.21 · Reversi
///
/// Trap your opponent’s discs between your own to flip them. A player may pass only when there is truly
/// nowhere to play.
///
///
/// **Your mission:** Create corner-prize Reversi: each occupied corner is worth three points and every
/// other occupied square one. Empty squares earn nothing. Show both scores during play and use those
/// same scores to decide the result after two passes. Moves, captures and legal passing stay as they
/// are. Construct one tiny board where disc count and prize score name different leaders.
///
/// **Today you learn:** Traversal can aggregate a weighted score. A calculation used only for display
/// cannot change a game result unless the result actually calls it.
///
/// Play an opening with 2 3. Read the displayed scores, then trace what happens after both players have
/// no legal move and pass.
int score(int who) {
    int total = 0;
    for (int row = 0; row < board.length; row++) {
        for (int column = 0; column < board[row].length; column++) {
            if (board[row][column] == who) {
                boolean corner = (row == 0 || row == board.length - 1)
                        && (column == 0 || column == board[row].length - 1);
                total += corner ? 3 : 1;
            }
        }
    }
    return total;
}

// <editor-fold defaultstate="expanded" desc="Learn · F17">
/// ### Traversal: one question at a time
///
/// A traversal visits values; a search may stop at the first match, while a count must examine every
/// relevant value. In a for-each loop, the name before the colon receives one value from the sequence
/// after it. Keep a running count outside the repeated body so earlier matches survive.
///
/// ```java
/// int[] pulses = {2, 0, 4};
/// int strong = 0;
/// for (int pulse : pulses) {
///     if (pulse > 1) {
///         strong++;
///     }
/// }
/// IO.println(strong);
/// ```
///
/// The result is two because pulses two and four satisfy the condition. If the question were merely
/// whether any strong pulse exists, when could you already answer? What should the count be for an
/// empty array?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Read this code">
/// Keep the rule for earning a square separate from the rule for valuing it. The same board may produce
/// different leaders under different scores.
///
/// In a separate three-cell row with values [1, 2, 1] and positional weights [2, 1, 2], player 1 earns
/// four and player 2 one. Predict the scores if the middle value becomes 1. A square contributes to
/// only its owner. Inspect both the live display and the final outcome so they cannot silently use
/// different accounting.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Follow the points from an occupied cell all the way to the winner announcement.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// score(who) currently counts every owned disc as one. Both display and the two-pass result call it;
/// hasMove and captures already work.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Visit every row and column, add only for the requested owner, and give a square the corner weight
/// only when both its row and column are boundary indices.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Make it yours">
/// Invent your own positional prizes. Exchange only the prize map with a partner and challenge them to
/// construct the smallest disagreement with ordinary disc count.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
boolean hasMove(int who) {
    for (int r = 0; r < 6; r++) {
        for (int c = 0; c < 6; c++) {
            if (!captures(r, c, who).isEmpty()) {
                return true;
            }
        }
    }
    return false;
}

int[][] board = new int[6][6];
int player;
int passes;
String outcome;

/// reset(): Restores the starting Reversi round, clearing the state that belongs to the old round.
void reset() {
    board = new int[6][6];
    board[2][2] = board[3][3] = 2;
    board[2][3] = board[3][2] = 1;
    player = 1;
    passes = 0;
    outcome = "";
}

/// captures(): Walks eight directions and returns opponent discs bracketed by a friendly disc.
List<int[]> captures(int row, int col, int who) {
    var result = new ArrayList<int[]>();
    if (row < 0 || row >= 6 || col < 0 || col >= 6 || board[row][col] != 0) {
        return result;
    }
    for (int dr = -1; dr <= 1; dr++) {
        for (int dc = -1; dc <= 1; dc++) {
            if (dr == 0 && dc == 0) {
                continue;
            }
            var line = new ArrayList<int[]>();
            int r = row + dr;
            int c = col + dc;
            while (r >= 0 && r < 6 && c >= 0 && c < 6 && board[r][c] == 3 - who) {
                line.add(new int[]{r, c});
                r += dr;
                c += dc;
            }
            if (r >= 0 && r < 6 && c >= 0 && c < 6 && board[r][c] == who) {
                result.addAll(line);
            }
        }
    }
    return result;
}

/// command(): Validates one actual player command before applying its changes to this game.
void command(String input) {
    require(outcome.isEmpty());
    if (input.equalsIgnoreCase("pass")) {
        require(!hasMove(player));
        passes++;
    } else {
        String[] p = input.split("\\s+");
        require(p.length == 2);
        int r = Integer.parseInt(p[0]) - 1;
        int c = Integer.parseInt(p[1]) - 1;
        var flips = captures(r, c, player);
        require(!flips.isEmpty());
        board[r][c] = player;
        for (int[] cell : flips) {
            board[cell[0]][cell[1]] = player;
        }
        passes = 0;
    }
    player = 3 - player;
    if (passes == 2) {
        int firstScore = score(1);
        int secondScore = score(2);
        outcome = firstScore == secondScore ? "Draw" : "Player " + (firstScore > secondScore ? 1 : 2) + " wins";
    }
}

/// display(): Presents the current board and the legal input choices in the console.
void display() {
    IO.println("Score P1: " + score(1) + "  P2: " + score(2));
    IO.println("Columns 1-6; player " + player + ". 1=X, 2=O, 0=empty");
    for (int r = 0; r < 6; r++) {
        IO.println((r + 1) + " " + Arrays.toString(board[r]));
    }
}

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
/// Prepared console flow: type a command and press Enter. A valid command redraws the board.
/// Rejected input leaves the display alone; after a round, Enter or /r starts again and /q quits.
void main() {
    reset();
    IO.println("Reversi");
    IO.println("Place a disc to trap an enemy line between two of yours; trapped discs flip. Higher displayed score wins after both players pass.");
    IO.println("Enter row column (1-6), or pass when blocked. /r restarts; /q quits.");
    IO.println("Example: 2 3 is an opening move for Player 1. Enter pass only when you have no legal move.");
    display();
    while (true) {
        String input = IO.readln(outcome.isEmpty() ? "> " : "Enter or /r: new round; /q: quit: ");
        if (input == null) {
            return;
        }
        input = input.strip();
        if (input.equalsIgnoreCase("/q") || input.equalsIgnoreCase("q")) {
            return;
        }
        if (input.equalsIgnoreCase("/r") || input.equalsIgnoreCase("r")
                || (!outcome.isEmpty() && input.isEmpty())) {
            reset();
            display();
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("That round is over. Press Enter to play again, or /q to quit.");
            continue;
        }
        try {
            command(input);
            display();
            if (!outcome.isEmpty()) {
                IO.println(outcome);
            }
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move. " + "Enter row column (1-6), or pass when blocked. /r restarts; /q quits.");
        }
    }
}

/// require(): Rejects an illegal command with an exception handled by the supplied input loop.
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
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
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
