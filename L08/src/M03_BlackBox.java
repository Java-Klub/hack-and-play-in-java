/// # L08.03 · BlackBox
///
/// Send rays through an invisible box and infer where its hidden balls sit. Hits, reflections, and bent
/// exits are the clues to your theory.
///
/// **Your mission:** The ray checks the same side twice, so some clues are wrong. Repair the
/// side-neighbour calculation in trace() without changing the supplied ray rules.
///
/// **Today you learn:** Grid neighbours: left and right must be relative to the ray’s current direction,
/// not fixed screen directions. Follow the perpendicular offsets as the ray turns.
///
/// Run File and enter ray N1 to probe from the top edge, then compare that observation with rays from
/// other labelled ports.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F18 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
String trace(boolean[][] grid, String entry) {
    require(entry.matches("[NSEW][1-4]"));
    int n = entry.charAt(1) - '1';
    char side = entry.charAt(0);
    int x = side == 'W' ? -1 : side == 'E' ? 4 : n;
    int y = side == 'N' ? -1 : side == 'S' ? 4 : n;
    int dx = side == 'W' ? 1 : side == 'E' ? -1 : 0;
    int dy = side == 'N' ? 1 : side == 'S' ? -1 : 0;
    boolean entering = true;
    var seen = new HashSet<String>();
    while (true) {
        if (!seen.add(x + "," + y + "," + dx + "," + dy)) {
            return "LOOP";
        }
        int nx = x + dx;
        int ny = y + dy;
        if (ball(grid, nx, ny)) {
            return "HIT";
        }
        boolean left = ball(grid, nx - dy, ny + dx);
        boolean right = ball(grid, nx + dy, ny - dx);
        if (entering && (left || right)) {
            return "REFLECT";
        }
        if (left && right) {
            dx = -dx;
            dy = -dy;
        } else {
            if (left) {
                int old = dx;
                dx = dy;
                dy = -old;
            } else {
                if (right) {
                    int old = dx;
                    dx = -dy;
                    dy = old;
                } else {
                    x = nx;
                    y = ny;
                    if (x < 0 || x >= 4 || y < 0 || y >= 4) {
                        String exit = port(x, y);
                        return exit.equals(entry) ? "REFLECT" : exit;
                    }
                }
            }
        }
        entering = false;
    }
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
/// - `int nx = x + dx` — Finds the next forward cell before deciding whether the ray may enter it.
/// - `nx - dy, ny + dx` — A perpendicular offset locates one side neighbour relative to the forward cell.
/// - `left && right` — The supplied rule treats balls on both sides differently from a single-side ball.
/// - `!seen.add(` — If the full position-and-direction state was already recorded, this detects a loop.
///
/// ### What the prepared part already does
///
/// ball() safely answers occupancy queries, and port() labels border exits. The remaining trace() handles
/// hits, reflections, and loop prevention; equivalent() compares all ray observations when you check a
/// guess.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Perpendicular means relative to travel
///
/// A direction is a pair of changes, not an absolute screen position. For a ray moving right with
/// direction `(1,0)`, its two side offsets are `(0,1)` and `(0,-1)`. They are opposites. The next
/// forward position supplies the common origin for both side checks. Sketch the same construction for
/// an upward ray. Keep the supplied turning and reflection rules; only the two distinct side
/// positions need to agree with travel.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The ray’s forward vector is (dx,dy), and next cell is (nx,ny).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Perpendicular offsets are (-dy,+dx) and (+dy,-dx).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Keep left at (nx-dy,ny+dx); right must use (nx+dy,ny-dx).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
boolean[][] hidden;
boolean[][] marked;
List<String> clues;
String outcome;
String message;

/// reset(): Restores the starting Black Box round, clearing the state that belongs to the old
/// round.
void reset() {
    hidden = new boolean[4][4];
    hidden[1][1] = hidden[2][3] = true;
    marked = new boolean[4][4];
    clues = new ArrayList<>();
    outcome = message = "";
}

boolean ball(boolean[][] grid, int x, int y) {
    return x >= 0 && x < 4 && y >= 0 && y < 4 && grid[y][x];
}

String port(int x, int y) {
    return y < 0 ? "N" + (x + 1) : y >= 4 ? "S" + (x + 1) : x < 0 ? "W" + (y + 1) : "E" + (y + 1);
}

/// equivalent(): Compares every border-ray observation, accepting any observationally equivalent
/// hidden layout.
boolean equivalent() {
    for (char side : new char[]{'N', 'S', 'E', 'W'}) {
        for (int n = 1; n <= 4; n++) {
            if (!trace(hidden, "" + side + n).equals(trace(marked, "" + side + n))) {
                return false;
            }
        }
    }
    return true;
}

/// command(): Validates one actual player command before applying its changes to this game.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.toUpperCase(Locale.ROOT).split("\\s+");
    if (p.length == 2 && p[0].equals("RAY")) {
        clues.add(p[1] + " -> " + trace(hidden, p[1]));
        message = "Probe recorded";
    } else {
        if (p.length == 3 && p[0].equals("MARK")) {
            int r = Integer.parseInt(p[1]) - 1;
            int c = Integer.parseInt(p[2]) - 1;
            require(r >= 0 && r < 4 && c >= 0 && c < 4);
            marked[r][c] = !marked[r][c];
            message = marked[r][c] ? "Guess marked" : "Guess removed";
        } else {
            require(p.length == 1 && p[0].equals("CHECK"));
            int count = 0;
            for (boolean[] row : marked) {
                for (boolean b : row) {
                    if (b) {
                        count++;
                    }
                }
            }
            require(count == 2);
            if (equivalent()) {
                outcome = "All possible ray observations agree: solved!";
                message = "";
            } else {
                message = "Some observations disagree. Revise the marks.";
            }
        }
    }
}

/// display(): Presents the current board and the legal input choices in the console.
void display() {
    IO.println("Exactly two balls. Borders N/S use columns1-4; E/W use rows1-4.");
    for (int r = 0; r < 4; r++) {
        String s = (r + 1) + " ";
        for (boolean b : marked[r]) {
            s += b ? "O " : ". ";
        }
        IO.println(s);
    }
    for (String clue : clues) {
        IO.println(clue);
    }
    IO.println(message);
}

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
/// Prepared console flow: type a command and press Enter. A valid command redraws the board.
/// Rejected input leaves the display alone; after a round, Enter or /r starts again and /q quits.
void main() {
    reset();
    IO.println("Black Box");
    IO.println("Find two hidden balls by probing rays, then mark two guesses and check their observations.");
    IO.println("ray N1/S1/E1/W1 probes; mark row col toggles a guess; check tests all ray observations. /r restarts; /q quits.");
    IO.println("Example: ray N1 enters from the north at column 1; mark 2 2 toggles a guess. HIT is absorption; REFLECT returns to entry.");
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
            IO.println("Invalid move. " + "ray N1/S1/E1/W1 probes; mark row col toggles a guess; check tests all ray observations. /r restarts; /q quits.");
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
/// - `char` — A type for one UTF-16 code unit, often a simple board character such as X. Text uses
///   String.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
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

