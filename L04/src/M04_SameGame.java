/// # L04.04 · SameGame
///
/// Remove touching blocks of the same colour, then watch the others fall and empty columns close. Larger
/// groups score more, but removing a tempting group may strand the blocks you need later.
///
/// **Your mission:** The starter can announce that no groups remain even when matching blocks touch.
/// Repair the work condition in group so all reachable matching cells are collected, rather than stopping
/// after the first result.
///
/// **Today you learn:** While until no work remains: the pending queue and the collected result answer
/// different questions. Finding one cell does not mean there are no more cells waiting to be explored.
///
/// Use Run File and look for two same-colour blocks that share an edge despite the early “No groups
/// remain” message; after your repair, enter the row and column of a block in that group.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F10 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
List<int[]> group(int row, int col) {
    var result = new ArrayList<int[]>();
    if (row < 0 || row >= 5 || col < 0 || col >= 5 || board[row][col] == 0) {
        return result;
    }
    boolean[][] seen = new boolean[5][5];
    var queue = new ArrayDeque<int[]>();
    queue.add(new int[]{row, col});
    seen[row][col] = true;
    while (!queue.isEmpty()) {
        int[] p = queue.remove();
        result.add(p);
        for (int[] d : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int r = p[0] + d[0];
            int c = p[1] + d[1];
            if (r >= 0 && r < 5 && c >= 0 && c < 5 && !seen[r][c] && board[r][c] == board[row][col]) {
                seen[r][c] = true;
                queue.add(new int[]{r, c});
            }
        }
    }
    return result;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F10">
/// A `while` checks its condition before every pass, including the first. If the condition is false
/// immediately, its body runs zero times. Something must make progress towards stopping, or
/// deliberately wait for new input.
///
/// These example statements belong inside a called method.
///
/// ```java
/// int energy = 5;
/// while (energy > 0) {
///     IO.println(energy);
///     energy -= 2;
/// }
/// ```
///
/// Predict the printed numbers and the final stored value. The output is `5`, `3`, `1`; afterwards
/// energy is `-1`, so the next condition fails. The condition is not checked halfway through the
/// body. `energy -= 2` subtracts two and stores the result; without it, the loop repeats forever.
/// `return` ends the method; `break` ends the loop. Trace the condition and changing values
/// separately, especially when the data being searched can grow.
///
/// Now predict the trace if energy starts at four.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `while (!queue.isEmpty() && result.isEmpty())` — The ! means “not”; queue.isEmpty() asks whether
///   pending work is empty, while result.isEmpty() asks whether anything has been collected yet.
/// - `int[] p = queue.remove();` — Take the next waiting coordinate pair out of the queue. The pair
///   format is supplied.
/// - `result.add(p);` — Save this visited cell in the result that the game will later remove from the
///   board.
/// - `seen[r][c] = true;` — Mark a neighbour before queuing it, so another route cannot queue that cell
///   again.
/// - `queue.add(new int[]{r, c});` — Append one newly discovered coordinate pair as more work for a later
///   pass.
/// - `return result;` — After exploration ends, return every collected cell together.
///
/// ### What the prepared part already does
///
/// The initial guard rejects off-board and empty cells. The supplied neighbour scan checks four
/// directions, matching colours and seen marks. command removes groups of at least two, collapse supplies
/// gravity, and reset checks whether play can continue; that check also calls your group.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// Keep three prepared roles separate: queue holds discovered cells still waiting for a visit; result
/// holds cells already collected; seen remembers cells already scheduled so two routes cannot add
/// them twice. queue.remove() takes one waiting coordinate pair out, while result.add adds that cell
/// to the answer. An answer containing one cell does not imply that the waiting work is empty. The
/// initial “No groups remain” message comes from the game's own group search before any move; it is
/// an intended symptom, not a console-input problem. Repair the source and start a new run rather
/// than trying to restart away the fault.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// result becomes nonempty after the first processed cell.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// That does not mean the queue has finished.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// The while condition should be only !queue.isEmpty().
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[][] board;
int score;
String outcome;

void reset() {
    board = new int[5][5];
    var random = new Random();
    for (int[] row : board) {
        for (int c = 0; c < 5; c++) {
            row[c] = random.nextInt(1, 4);
        }
    }
    score = 0;
    outcome = "";
    assess();
}

boolean hasMove() {
    for (int r = 0; r < 5; r++) {
        for (int c = 0; c < 5; c++) {
            if (group(r, c).size() >= 2) {
                return true;
            }
        }
    }
    return false;
}

void assess() {
    boolean empty = true;
    for (int[] row : board) {
        for (int n : row) {
            empty &= n == 0;
        }
    }
    if (empty) {
        outcome = "Board clear: you win!";
    } else {
        if (!hasMove()) {
            outcome = "No groups remain. Final score: " + score;
        }
    }
}

void collapse() {
    int[][] next = new int[5][5];
    int writeColumn = 0;
    for (int c = 0; c < 5; c++) {
        int writeRow = 4;
        for (int r = 4; r >= 0; r--) {
            if (board[r][c] != 0) {
                next[writeRow--][writeColumn] = board[r][c];
            }
        }
        if (writeRow != 4) {
            writeColumn++;
        }
    }
    board = next;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 2);
    var cells = group(Integer.parseInt(p[0]) - 1, Integer.parseInt(p[1]) - 1);
    require(cells.size() >= 2);
    for (int[] cell : cells) {
        board[cell[0]][cell[1]] = 0;
    }
    score += cells.size() * (cells.size() - 1);
    collapse();
    assess();
}

void display() {
    IO.println("Colours1/2/3, 0 empty; columns1-5. Score " + score);
    for (int r = 0; r < 5; r++) {
        IO.println((r + 1) + " " + Arrays.toString(board[r]));
    }
}

void main() {
    reset();
    IO.println("Same Game");
    IO.println("Enter a row column (1-5) in a group of at least two equal colours. Blocks fall and columns close. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter a row column (1-5) in a group of at least two equal colours. Blocks fall and columns close. /r restarts; /q quits.");
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
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
/// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
///   direction. In List<String>, angle brackets describe the element type.
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
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>

