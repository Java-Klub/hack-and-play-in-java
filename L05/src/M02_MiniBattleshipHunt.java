/// # L05.02 · MiniBattleshipHunt
///
/// Hunt two hidden ships by calling grid coordinates. A hit becomes more useful when you can trust the
/// announced ship lengths.
///
/// **Your mission:** One ship loses its final square during setup. Repair layout(int n) so the selected
/// coordinate list is copied completely.
///
/// **Today you learn:** Arrays: nested arrays can hold several layouts and their coordinate pairs. Use
/// the selected data’s size to control a traversal.
///
/// Run File and fire your first shot by typing 1 1 and pressing Enter, then try other row-and-column
/// pairs.
/// After your repair: Save → Test File → play again.
void layout(int n) {
    board = new int[6][6];
    int[][][] maps = {{{0, 0}, {0, 1}, {3, 2}, {3, 3}, {3, 4}}, {{1, 4}, {2, 4}, {5, 0}, {5, 1}, {5, 2}}, {{4, 0}, {4, 1}, {0, 5}, {1, 5}, {2, 5}}};
    for (int i = 0; i < 4; i++) {
        board[maps[n][i][0]][maps[n][i][1]] = i < 2 ? 1 : 2;
    }
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int[][][] maps` — Three levels select a layout, then a coordinate pair, then one coordinate.
/// - `maps[n][i][0]` — Reads the row coordinate of position i in layout n.
/// - `i++` — Advances to the next coordinate pair after each iteration.
/// - `i < 2 ? 1 : 2` — Labels early coordinate pairs as ship 1 and the others as ship 2.
///
/// ### What the prepared part already does
///
/// reset() chooses an authored layout and clears shots. command() records a shot, while sunk() searches
/// the resulting board for any surviving part of that ship.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. maps[n] is the selected list of positions.
/// 2. Each maps[n][i] holds row and column; i is not a board coordinate.
/// 3. Iterate while i < maps[n].length; keep the supplied ship-number assignment.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[][] board;
boolean[][] shots;
int attempts;
String outcome;
String message;

/// reset(): Restores the starting Mini Battleship Hunt round, clearing the state that belongs to
/// the old round.
void reset() {
    layout(new Random().nextInt(3));
    shots = new boolean[6][6];
    attempts = 0;
    outcome = message = "";
}

/// sunk(): Searches for any unshot cell belonging to the requested ship.
boolean sunk(int ship) {
    for (int r = 0; r < 6; r++) {
        for (int c = 0; c < 6; c++) {
            if (board[r][c] == ship && !shots[r][c]) {
                return false;
            }
        }
    }
    return true;
}

/// command(): Validates one actual player command before applying its changes to this game.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 2);
    int r = Integer.parseInt(p[0]) - 1;
    int c = Integer.parseInt(p[1]) - 1;
    require(r >= 0 && r < 6 && c >= 0 && c < 6 && !shots[r][c]);
    shots[r][c] = true;
    attempts++;
    int ship = board[r][c];
    message = ship == 0 ? "Miss" : sunk(ship) ? "Sunk ship " + ship : "Hit";
    if (sunk(1) && sunk(2)) {
        outcome = "Both ships sunk in " + attempts + " shots!";
    }
}

/// display(): Presents the current board and the legal input choices in the console.
void display() {
    IO.println(message + "; shots " + attempts + "; two ships of lengths2 and3. Columns1-6.");
    for (int r = 0; r < 6; r++) {
        String s = (r + 1) + " ";
        for (int c = 0; c < 6; c++) {
            s += !shots[r][c] ? "? " : board[r][c] == 0 ? "~ " : "X ";
        }
        IO.println(s);
    }
}

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
void main() {
    reset();
    IO.println("Mini Battleship Hunt");
    IO.println("Enter row column (1-6). Repeated shots do not count. Sink both ships. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter row column (1-6). Repeated shots do not count. Sink both ships. /r restarts; /q quits.");
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
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
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
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

