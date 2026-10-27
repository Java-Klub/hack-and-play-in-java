/// # L06.11 · PegSolitaire
///
/// Jump pegs over one another to empty a short solitaire board. Now inspect the referee: does it reject
/// jumps that should be impossible?
///
/// **Your mission:** A single successful jump does not expose cheating rules. Add examples in
/// learnerChecks() that test both permission and refusal on the fixed starting board.
///
/// **Today you learn:** Writing functional checks: a useful suite includes counterexamples, not just
/// happy paths. Each expected boolean states whether that exact move should be legal.
///
/// Run File and enter 1 2 3 2 to make the first jump in the short peg puzzle, then use Test File to
/// challenge your examples.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F33 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
record JumpExample(int row, int column, int toRow, int toColumn, boolean expected) {
}

JumpExample[] learnerChecks() {
    return new JumpExample[]{
    new JumpExample(0, 1, 2, 1, true), new JumpExample(2, 0, 2, 2, false),
    new JumpExample(0, 1, 1, 2, false), new JumpExample(-1, 1, 1, 1, false)};
}

// <editor-fold defaultstate="collapsed" desc="Recall · F33">
/// ### Checks: choose an example that could disagree
///
/// A functional check needs an input and an independently predicted result. A passing example is weak
/// if a plausible wrong rule gives the same answer. Read this separate calculation and its example;
/// the equality produces a boolean that reports agreement.
///
/// ```java
/// int twice(int value) {
///     return value + value;
/// }
///
/// void main() {
///     boolean agrees = twice(0) == 0;
///     IO.println(agrees);
/// }
/// ```
///
/// This prints true: twice zero is zero. An implementation always returning zero also passes. Choose
/// a different input and derive its expected result yourself. What mistake does your new example
/// expose? Keep each check's starting data independent so one example cannot accidentally prepare
/// another.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `record JumpExample` — A named value stores one proposed jump together with its expected legality.
/// - `int toRow, int toColumn` — These components describe the destination, separately from the starting
///   coordinates.
/// - `boolean expected` — true means this example must be accepted; false means it must be rejected.
/// - `new JumpExample(0, 1, 2, 1, true)` — The supplied example is one accepted jump on the initial
///   board; coordinates here begin at zero.
///
/// ### What the prepared part already does
///
/// legal() already checks bounds, straight distance, the source peg, the middle peg, and an empty
/// destination. Test File runs your examples on it and on deliberately wrong jump rules.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Permission is a result too
///
/// Every JumpExample asks about the same fixed starting board, rather than continuing the previous
/// example's move. Read `expected` as permission, not success at solving the puzzle. A legal move
/// needs a peg at the start, a jumped peg between the endpoints, and an empty destination in a
/// straight line. Predict both an accepted and a refused request from that opening. Which single part
/// of the contract would a rule accepting every request ignore?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// One valid jump proves that true is possible, not that illegal moves are rejected.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// On the start board, (2,0)->(2,2) lacks a middle peg; (0,1)->(1,2) is diagonal.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Add those false cases and an outside coordinate; keep the valid (0,1)->(2,1) case.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[][] board;
String outcome;

/// reset(): Restores the starting Peg Solitaire round, clearing the state that belongs to the old
/// round.
void reset() {
    board = new int[5][5];
    outcome = "";
    for (int r = 0; r < 5; r++) {
        for (int c = 0; c < 5; c++) {
            board[r][c] = (r > 0 && r < 4 || c > 0 && c < 4) ? 0 : -1;
        }
    }
    board[2][0] = board[0][1] = board[1][1] = 1;
}

/// legal(): Checks bounds, axial distance two, source peg, empty destination and middle peg.
boolean legal(int r, int c, int nr, int nc) {
    return r >= 0 && r < 5 && c >= 0 && c < 5 && nr >= 0 && nr < 5 && nc >= 0 && nc < 5 && Math.abs(nr - r) + Math.abs(nc - c) == 2 && (r == nr || c == nc) && board[r][c] == 1 && board[nr][nc] == 0 && board[(r + nr) / 2][(c + nc) / 2] == 1;
}

int pegs() {
    int n = 0;
    for (int[] row : board) {
        for (int v : row) {
            if (v == 1) {
                n++;
            }
        }
    }
    return n;
}

boolean canJump() {
    for (int r = 0; r < 5; r++) {
        for (int c = 0; c < 5; c++) {
            for (int[] d : new int[][]{{2, 0}, {-2, 0}, {0, 2}, {0, -2}}) {
                if (legal(r, c, r + d[0], c + d[1])) {
                    return true;
                }
            }
        }
    }
    return false;
}

/// command(): Validates one actual player command before applying its changes to this game.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 4);
    int r = Integer.parseInt(p[0]) - 1;
    int c = Integer.parseInt(p[1]) - 1;
    int nr = Integer.parseInt(p[2]) - 1;
    int nc = Integer.parseInt(p[3]) - 1;
    require(legal(r, c, nr, nc));
    board[r][c] = board[(r + nr) / 2][(c + nc) / 2] = 0;
    board[nr][nc] = 1;
    if (pegs() == 1) {
        outcome = "One peg: you win!";
    } else {
        if (!canJump()) {
            outcome = "No jumps left: you lose.";
        }
    }
}

/// display(): Presents the current board and the legal input choices in the console.
void display() {
    IO.println("Columns 1-5; # peg, . empty, space outside. Pegs: " + pegs());
    for (int r = 0; r < 5; r++) {
        String s = (r + 1) + " ";
        for (int v : board[r]) {
            s += v < 0 ? "  " : v == 1 ? "# " : ". ";
        }
        IO.println(s);
    }
}

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
void main() {
    reset();
    verifyExamples();
    IO.println("Peg Solitaire");
    IO.println("Enter fromRow fromCol toRow toCol (1-5). A short authored two-jump cross puzzle. /r restarts; /q quits.");
    while (true) {
        display();
        if (!outcome.isEmpty()) {
            IO.println(outcome);
        }
        String input = IO.readln("> ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r")) {
            reset();
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. /r restarts; /q quits.");
            continue;
        }
        try {
            command(input.strip());
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move or command. Enter fromRow fromCol toRow toCol (1-5). A short authored two-jump cross puzzle. /r restarts; /q quits.");
        }
    }
}

/// require(): Rejects an illegal command with an exception handled by the supplied input loop.
void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

/// verifyExamples(): Startup checks your examples against real non-mutating queries; no game state
/// is changed.
void verifyExamples() {
    for (var e : learnerChecks()) {
        if (legal(e.row(), e.column(), e.toRow(), e.toColumn()) != e.expected()) {
            throw new IllegalStateException("Jump example has wrong expected result");
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
/// - `/` — Divides numbers. Dividing two int values discards the fractional part.
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
/// - `record` — A small named bundle of values. Its component accessors use the component names with ().
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

