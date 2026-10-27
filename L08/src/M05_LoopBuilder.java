/// # L08.05 · LoopBuilder
///
/// Toggle lines to form one closed loop around numbered cells. The numbers constrain the loop, but its
/// corners must also really connect.
///
/// **Your mission:** Vertical lines report the wrong endpoint, so a visible loop is judged as broken.
/// Repair the vertical mapping inside vertices(int edge).
///
/// **Today you learn:** Grid neighbours: displayed edges connect vertices on a separate grid. Translate
/// row/column neighbours into flat vertex indices using the vertex grid’s width.
///
/// Run File and enter 1 to toggle the first numbered edge, then add other edges to build a single loop.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F18 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
int[] vertices(int edge) {
    if (edge < 12) {
        int r = edge / 3;
        int c = edge % 3;
        return new int[]{r * 4 + c, r * 4 + c + 1};
    }
    int n = edge - 12;
    int r = n / 4;
    int c = n % 4;
    return new int[]{r * 4 + c, (r + 1) * 4 + c};
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
/// - `if (edge < 12)` — Separates the horizontal edge numbers from the vertical ones.
/// - `edge - 12` — Converts a vertical edge number into a local zero-based number.
/// - `r * 4 + c` — Maps a vertex row and column to one index in the four-wide vertex grid.
/// - `new int[]` — Returns the two endpoint indices together as one small array.
///
/// ### What the prepared part already does
///
/// display() numbers horizontal and vertical edges. oneLoop() uses the endpoint pairs from vertices() to
/// check degrees and connectivity; the numbered-cell counts are checked separately.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Cells and their corners use different grids
///
/// A row of two adjacent cells has three corner vertices across it. Similarly, a cell grid needs one
/// extra vertex row and column. In an unrelated vertex grid of width three, index one lies above
/// index four: moving down preserves the column and skips three positions. A horizontal edge changes
/// column; a vertical edge changes row. Draw the vertex indices separately from the cell clues before
/// predicting each endpoint pair.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Cell rows are width 3, but vertex rows are width 4.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Moving down one vertex row adds 4 to its linear index.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Return r*4+c and (r+1)*4+c for a vertical edge.
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
void main() {
    reset();
    IO.println("Loop Builder");
    IO.println("Enter edge1-24 to toggle it. One closed loop must match all numbered cells. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter edge1-24 to toggle it. One closed loop must match all numbered cells. /r restarts; /q quits.");
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
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

