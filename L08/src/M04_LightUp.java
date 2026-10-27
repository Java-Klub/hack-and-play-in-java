/// # L08.04 · LightUp
///
/// Place bulbs so every open square is lit, but no two bulbs shine at one another. Walls can turn one
/// corridor into several separate lighting puzzles.
///
/// **Your mission:** Only immediately adjacent bulbs are visible to the rule. Repair visible() so a
/// straight open corridor is checked all the way to a wall or border.
///
/// **Today you learn:** Grid neighbours: reuse a direction offset repeatedly to explore a ray of cells.
/// One neighbour test and a corridor traversal answer different questions.
///
/// Run File and enter 1 1 to toggle a bulb in the upper-left cell, then try lighting the other open cells
/// without bulb conflicts.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F18 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
int visible(int r, int c) {
    int n = 0;
    for (int[] d : dirs) {
        int y = r + d[0];
        int x = c + d[1];
        if (open(y, x)) {
            if (bulbs[y][x]) {
                n++;
            }
            y += d[0];
            x += d[1];
        }
    }
    return n;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F18">
/// ### Grids: choose the row before the column
///
/// In an array of rows, the first index selects a row and the second selects a position within it.
/// Rows can have different lengths. A neighbour is a displacement from a cell, not another absolute
/// position. Define the permitted directions before checking whether a candidate exists.
///
/// ```java
/// int[][] mosaic = {{3, 6, 9}, {2, 4, 8}};
/// int row = 0;
/// int column = 1;
/// IO.println(mosaic[row][column + 1]);
/// ```
///
/// The output is nine: row zero and column two select that element. Moving down changes row; moving
/// right changes column. From the top-right cell, which of those two moves stays inside? For a
/// diagonal both coordinates change; bounds must be checked before accessing the candidate.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `for (int[] d : dirs)` — Runs the same visibility search in each of four directions.
/// - `if (open(y, x))` — Currently tests just the next position once; consider the corridor’s length.
/// - `bulbs[y][x]` — Reads whether the currently visited corridor cell contains a bulb.
/// - `y += d[0]` — Advances the row by the same direction offset for another step.
///
/// ### What the prepared part already does
///
/// open() rejects walls and positions outside the board. solved() uses visible() for illumination and
/// bulb conflicts, then checks numbered walls; toggling bulbs and console display are ready.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// dirs supplies the four one-cell offsets.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Advance y and x repeatedly while open(y,x) stays true.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Use a while loop instead of one if; keep the coordinate increments inside it.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String[] walls = {"...", ".0.", "..."};
boolean[][] bulbs;
String outcome;
int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

/// reset(): Restores the starting Light Up round, clearing the state that belongs to the old round.
void reset() {
    bulbs = new boolean[3][3];
    outcome = "";
}

boolean open(int r, int c) {
    return r >= 0 && r < 3 && c >= 0 && c < 3 && walls[r].charAt(c) == '.';
}

/// solved(): Requires illumination, no visible bulb pairs and correct adjacent numbered-wall
/// counts.
boolean solved() {
    for (int r = 0; r < 3; r++) {
        for (int c = 0; c < 3; c++) {
            if (open(r, c)) {
                if (bulbs[r][c] && visible(r, c) > 0 || !bulbs[r][c] && visible(r, c) == 0) {
                    return false;
                }
            } else {
                int count = 0;
                for (int[] d : dirs) {
                    int y = r + d[0];
                    int x = c + d[1];
                    if (open(y, x) && bulbs[y][x]) {
                        count++;
                    }
                }
                if (count != walls[r].charAt(c) - '0') {
                    return false;
                }
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
    require(open(r, c));
    bulbs[r][c] = !bulbs[r][c];
    if (solved()) {
        outcome = "All cells lit, no bulb conflicts, wall numbers satisfied!";
    }
}

/// display(): Presents the current board and the legal input choices in the console.
void display() {
    IO.println("Columns1-3. B bulb, + lit, . dark, digit wall with adjacent-bulb count.");
    for (int r = 0; r < 3; r++) {
        String s = (r + 1) + " ";
        for (int c = 0; c < 3; c++) {
            s += !open(r, c) ? walls[r].charAt(c) : bulbs[r][c] ? 'B' : visible(r, c) > 0 ? '+' : '.';
        }
        IO.println(s);
    }
}

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
void main() {
    reset();
    IO.println("Light Up");
    IO.println("Enter row column (1-3) to toggle a bulb. Light every open cell; bulbs cannot see each other. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter row column (1-3) to toggle a bulb. Light every open cell; bulbs cannot see each other. /r restarts; /q quits.");
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
/// - `'` — Single quotes enclose one character, such as a board marker.
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
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

