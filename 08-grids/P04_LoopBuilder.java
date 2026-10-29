/// # 08.04 · LoopBuilder
///
/// **Connect lines to the correct corners.** The referee does not yet know which corners each
/// line connects. You can toggle drawn lines, but it cannot judge a closed loop correctly.
///
/// **Your task:** Complete `vertices` below: given an edge number, return its two corner numbers.
/// Sixteen corners form a 4×4 grid, numbered left to right by rows from 0 to 15. Edges 0–11 are
/// horizontal, three per row. Edges 12–23 are vertical, four between each pair of rows. Reject
/// numbers outside 0–23.
///
/// **Result:** Edge 0 has ends `[0, 1]`; edge 12 has `[0, 4]`. Check tests all 24 edges. Ordinary
/// play numbers the edges from 1 to 24, one larger than their numbers in code.

int[] vertices(int edge) {
    if (edge < 0 || edge >= 24) throw new IllegalArgumentException("Edge: 0..23.");
    if (edge < 12) {
        int row = edge / 3;
        int col = edge % 3;
        return new int[]{row * 4 + col, row * 4 + col + 1};
    }
    int vertical = edge - 12;
    int row = vertical / 4;
    int col = vertical % 4;
    return new int[]{row * 4 + col, (row + 1) * 4 + col};
}

// <editor-fold defaultstate="collapsed" desc="Recall · F18">
/// ### Coordinates describe neighbours
///
/// A grid stores rows first: `board[y][x]`. Increasing x goes right; increasing y goes down on our
/// screens. A neighbour is the current coordinate plus a small offset.
///
/// ```text
///           (0,-1)
/// (-1,0)    HERE    (1,0)
///           (0,1)
/// ```
///
/// ```java
/// int[][] board = {{2, 5, 8}, {1, 4, 7}};
/// int x = 1, y = 0;
/// int nx = x + 0, ny = y + 1;
/// if (ny >= 0 && ny < board.length && nx >= 0 && nx < board[ny].length) {
///     IO.println(board[ny][nx]);
/// }
/// ```
///
/// The move goes from 5 to 4; the printed value is 4. Bounds are checked before indexing, because an
/// array has no cell outside its edges. A diagonal changes both coordinates. A corridor repeats the
/// same offset; a neighbourhood tries several offsets. These are different algorithms using the same
/// coordinate model.
///
/// Predict the result of offset `(-1,0)` from the same start. Which offset leaves this board? Mark the
/// start and destination on paper before combining directions in a loop.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read this part
///
/// In a flat grid, a vertex at (row,col) has index row*4+col. `/` finds a row; `%` finds the remainder,
/// hence a column. Horizontal edge 0 joins 0→1; vertical edge 12 joins 0→4. Those examples use the same
/// vertices but different edge numbering. The prepared loop checker asks your method for connectivity;
/// it does not infer endpoints from the drawing.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Number the sixteen vertices on paper before numbering edges.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Separate the first twelve edges from the last twelve; their row widths differ.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Translate the edge to row/column, flatten its starting vertex with width 4, then add 1 horizontally
/// or 4 vertically.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Draw your own closed loop, derive the surrounding cell clues, and challenge a partner. You need a
/// playable puzzle, not a solver or a promise of uniqueness.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
boolean[] edges;
int[] clues = {2, 1, 2, 1, 0, 1, 2, 1, 2};
String outcome;

/// reset(): Restores the starting Loop Builder round, clearing the state that belongs to the old
/// round.
void reset() {
    edges = new boolean[24];
    outcome = "";
}

/// oneLoop(): Supplied degree-two and connectivity checks distinguish one loop from branches or
/// separate loops.
boolean oneLoop() {
    int[] degree = new int[16];
    var graph = new ArrayList<List<Integer>>();
    for (int i = 0; i < 16; i++) {
        graph.add(new ArrayList<>());
    }
    for (int i = 0; i < 24; i++) {
        if (edges[i]) {
            int[] v = vertices(i);
            degree[v[0]]++;
            degree[v[1]]++;
            graph.get(v[0]).add(v[1]);
            graph.get(v[1]).add(v[0]);
        }
    }
    int start = -1;
    int total = 0;
    for (int i = 0; i < 16; i++) {
        if (degree[i] != 0 && degree[i] != 2) {
            return false;
        }
        if (degree[i] == 2) {
            start = i;
            total++;
        }
    }
    if (start < 0) {
        return false;
    }
    var seen = new HashSet<Integer>();
    var queue = new ArrayDeque<Integer>();
    queue.add(start);
    seen.add(start);
    while (!queue.isEmpty()) {
        for (int v : graph.get(queue.remove())) {
            if (seen.add(v)) {
                queue.add(v);
            }
        }
    }
    return seen.size() == total;
}

boolean solved() {
    for (int r = 0; r < 3; r++) {
        for (int c = 0; c < 3; c++) {
            int n = 0;
            for (int e : new int[]{r * 3 + c, (r + 1) * 3 + c, 12 + r * 4 + c, 13 + r * 4 + c}) {
                if (edges[e]) {
                    n++;
                }
            }
            if (n != clues[r * 3 + c]) {
                return false;
            }
        }
    }
    return oneLoop();
}

/// command(): Validates one actual player command before applying its changes to this game.
void command(String input) {
    require(outcome.isEmpty());
    int edge = Integer.parseInt(input) - 1;
    require(edge >= 0 && edge < 24);
    edges[edge] = !edges[edge];
    if (solved()) {
        outcome = "One complete loop satisfies every clue!";
    }
}

String label(int edge) {
    return edges[edge] ? "##" : String.format("%02d", edge + 1);
}

/// display(): Presents the current board and the legal input choices in the console.
void display() {
    for (int r = 0; r < 4; r++) {
        String line = "+";
        for (int c = 0; c < 3; c++) {
            line += "--" + label(r * 3 + c) + "--+";
        }
        IO.println(line);
        if (r < 3) {
            line = label(12 + r * 4);
            for (int c = 0; c < 3; c++) {
                line += "  " + clues[r * 3 + c] + "   " + label(13 + r * 4 + c);
            }
            IO.println(line);
        }
    }
    IO.println("## marks chosen edge; cell digits count surrounding edges.");
}

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
/// Prepared console flow: type a command and press Enter. A valid command redraws the board.
/// Rejected input leaves the display alone; after a round, Enter or /r starts again and /q quits.
void main() {
    reset();
    IO.println("Loop Builder");
    IO.println("Choose edges to make exactly one closed loop. Each cell's number counts the chosen edges around it.");
    IO.println("Enter edge1-24 to toggle it. One closed loop must match all numbered cells. /r restarts; /q quits.");
    IO.println("Example: enter 1 to toggle edge 01. ## means chosen; enter the same edge again to remove it.");
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
            IO.println("Invalid move. " + "Enter edge1-24 to toggle it. One closed loop must match all numbered cells. /r restarts; /q quits.");
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
/// - `%` — The remainder after division; useful for repeated positions or turns.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
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
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
