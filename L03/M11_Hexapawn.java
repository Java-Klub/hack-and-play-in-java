/// # L03.11 · Hexapawn
///
/// Command three pawns on a tiny board and try to reach the far side or leave the opponent unable to
/// move. Pawns move forward and capture diagonally, so even a few squares allow traps.
///
/// **Your mission · Give both armies their orders:** Neither army has complete orders. Implement goalRow
/// and forwardStep for both
/// players: player 1 moves -1 towards row 0, player 2 moves +1 towards row 2. The prepared move rules
/// will ask your methods; return answers without changing the board.
///
/// **Today you learn:** Methods, parameters and results: one named calculation can answer different
/// calls. goalRow(1) and goalRow(2) supply different who values to the same body; the returned row goes
/// back to the move rule.
///
/// Run the program and enter 3 1 2 1; 1 2 2 1; 3 3 2 3; 2 1 3 1 as four separate moves to bring
/// player 2 to the bottom.
int goalRow(int who) {
    if (who == 1) {
        return 0;
    } else {
        return 2;
    }
}

int forwardStep(int who) {
    if (who == 1) {
        return -1;
    }
    return 1;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F13">
/// A method packages an operation under one name. Parameters are local names for the information
/// received on each call; arguments are the actual supplied values. `return` gives one result back to
/// the caller.
///
/// ```java
/// int remaining(int capacity, int used) {
///     return capacity - used;
/// }
///
/// void main() {
///     IO.println(remaining(12, 5));
///     IO.println(remaining(9, 4));
/// }
/// ```
///
/// Predict both printed values: seven and five. The comma separates the parameters or arguments, and
/// their order decides which value each name receives. The names describe roles; they do not fetch
/// information by magic. This method calculates without printing or changing the supplied program
/// state. When repairing a calculation, try a second pair of arguments so an accidental fixed answer
/// cannot hide.
///
/// Now predict remaining(10, 6).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Two reusable questions
///
/// goalRow and forwardStep each receive a player number and return an int. legal uses forwardStep to
/// check direction; canMove uses the same question while looking for any legal move; command uses
/// goalRow to recognise promotion. Reusing a named rule keeps those callers in agreement. The
/// supplied board uses zero-based rows and columns.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Follow the values · predict a case">
/// Two questions describe each army: which way is forward and which row wins? Player 1 starts at
/// internal row 2, travels by -1 and aims for 0. Player 2 starts at 0, travels by +1 and aims for 2.
/// The same who argument must produce consistent answers in both methods. Neither method moves a
/// pawn.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Create a tiny tactical position by changing the existing reset board, with both players still
/// present. Give a friend one move that wins and one tempting move that loses. Keep the normal
/// directions first; only then try reversing both armies and their goal rows together.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Write down the direction and goal for each who before coding.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Each method uses the same player parameter but answers a different question.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// For who == 1 return goal 0 and step -1; for player 2 return goal 2 and step 1.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[][] board;
int player;
String outcome;

void reset() {
    board = new int[][]{{2, 2, 2}, {0, 0, 0}, {1, 1, 1}};
    player = 1;
    outcome = "";
}

boolean legal(int r, int c, int nr, int nc, int who) {
    if (r < 0 || r > 2 || c < 0 || c > 2 || nr < 0 || nr > 2 || nc < 0 || nc > 2) {
        return false;
    }
    int direction = forwardStep(who);
    return board[r][c] == who && nr == r + direction && (nc == c && board[nr][nc] == 0 || Math.abs(nc - c) == 1 && board[nr][nc] == 3 - who);
}

boolean canMove(int who) {
    for (int r = 0; r < 3; r++) {
        for (int c = 0; c < 3; c++) {
            for (int nc = 0; nc < 3; nc++) {
                if (legal(r, c, r + forwardStep(who), nc, who)) {
                    return true;
                }
            }
        }
    }
    return false;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 4);
    int r = Integer.parseInt(p[0]) - 1;
    int c = Integer.parseInt(p[1]) - 1;
    int nr = Integer.parseInt(p[2]) - 1;
    int nc = Integer.parseInt(p[3]) - 1;
    require(legal(r, c, nr, nc, player));
    board[nr][nc] = player;
    board[r][c] = 0;
    if (nr == goalRow(player) || !canMove(3 - player)) {
        outcome = "Player " + player + " wins";
    } else {
        player = 3 - player;
    }
}

void display() {
    IO.println("Player " + player + "; columns 1-3, 1 moves up, 2 moves down");
    for (int r = 0; r < 3; r++) {
        IO.println((r + 1) + " " + Arrays.toString(board[r]));
    }
}

void main() {
    reset();
    IO.println("Hexapawn");
    IO.println("Enter fromRow fromColumn toRow toColumn (1-3). /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter fromRow fromColumn toRow toColumn (1-3). /r restarts; /q quits.");
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
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
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
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
