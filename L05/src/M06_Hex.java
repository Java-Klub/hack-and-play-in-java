/// # L05.06 · Hex
///
/// Build an unbroken road of your colour between opposite borders. Hexagons let a zigzag connect where
/// square-grid intuition says it cannot.
///
/// **Your mission:** Some genuine hex connections fail to win. Repair the neighbour directions inside
/// connected(int who), keeping the supplied search machinery.
///
/// **Today you learn:** Traversal and search: a path search depends on which neighbouring positions it is
/// allowed to visit. Here the candidate graph is a hex board.
///
/// Run File and take the upper-left hex by entering 1 1, then alternate row-and-column choices with a
/// friend.
/// After your repair: Save → Test File → play again.
boolean connected(int who) {
    boolean[][] seen = new boolean[5][5];
    var queue = new ArrayDeque<int[]>();
    for (int i = 0; i < 5; i++) {
        int r = who == 1 ? 0 : i;
        int c = who == 1 ? i : 0;
        if (board[r][c] == who) {
            seen[r][c] = true;
            queue.add(new int[]{r, c});
        }
    }
    int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    while (!queue.isEmpty()) {
        int[] cell = queue.remove();
        if (who == 1 && cell[0] == 4 || who == 2 && cell[1] == 4) {
            return true;
        }
        for (int[] d : directions) {
            int r = cell[0] + d[0];
            int c = cell[1] + d[1];
            if (r >= 0 && r < 5 && c >= 0 && c < 5 && !seen[r][c] && board[r][c] == who) {
                seen[r][c] = true;
                queue.add(new int[]{r, c});
            }
        }
    }
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int[][] directions` — Each small array is one row/column offset to a neighbouring hex.
/// - `queue.remove()` — Takes the next discovered cell whose neighbours must be explored.
/// - `!seen[r][c]` — Allows a cell to enter the search only before it has been visited.
/// - `cell[0] + d[0]` — Adds the chosen direction’s row offset to the current row.
///
/// ### What the prepared part already does
///
/// The queue holds discovered cells and seen prevents visiting them repeatedly. command() claims a tile,
/// calls connected(), and announces a win if the search reaches the far border.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. The printed rows are staggered, not square cells.
/// 2. Besides four straight offsets, cells touch at (+1,-1) and (-1,+1).
/// 3. Add those two directions; keep seen[][] and the queue logic unchanged.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[][] board;
int player;
String outcome;

/// reset(): Restores the starting Hex round, clearing the state that belongs to the old round.
void reset() {
    board = new int[5][5];
    player = 1;
    outcome = "";
}

/// command(): Claims one empty hex cell, tests the resulting connection and switches player if
/// unfinished.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 2);
    int r = Integer.parseInt(p[0]) - 1;
    int c = Integer.parseInt(p[1]) - 1;
    require(r >= 0 && r < 5 && c >= 0 && c < 5 && board[r][c] == 0);
    board[r][c] = player;
    if (connected(player)) {
        outcome = "Player " + player + " wins";
    } else {
        player = 3 - player;
    }
}

/// display(): Presents the current board and the legal input choices in the console.
void display() {
    IO.println("Player " + player + "; 1 joins top/bottom, 2 joins left/right. Columns 1-5.");
    for (int r = 0; r < 5; r++) {
        IO.println(" ".repeat(r) + (r + 1) + " " + Arrays.toString(board[r]));
    }
}

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
void main() {
    reset();
    IO.println("Hex");
    IO.println("Enter row column (1-5) on the staggered board. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter row column (1-5) on the staggered board. /r restarts; /q quits.");
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
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
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

