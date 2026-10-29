import module java.base;
class M10_Slant {
    /// # L08.10 · Slant
    ///
    /// Place diagonal strokes to match the numbers at their corners without closing a loop. Undo makes it
    /// safe to try one uncertain slash.
    ///
    /// **Your mission:** Undo cannot recover the earlier board because saved rows change along with current
    /// rows. Repair snapshot(), including the case of undoing a finishing move.
    ///
    /// **Today you learn:** Copies and aliasing: a two-dimensional snapshot needs separate row arrays as well
    /// as a separate outer array. The saved characters must remain an earlier state.
    ///
    /// From the course folder containing README.md:
    /// - Play: `java L08/M10_Slant.java`
    /// - Check this mission: `java L08/Check.java M10_Slant.java`
    ///
    /// the play command, enter 1 1 / to place the first slash, then type undo to check whether that
    /// exact edit is restored. After your repair: save the file, run the check command above, then play
    /// again.
    ///
    /// Need an explanation? Recall · F20 explains the Java skill; Read this code explains these lines.
    /// Hints 1 and 2 open separately; Repair outline reveals the direction.
    char[][] snapshot() {
        return cells.clone();
    }

    // <editor-fold defaultstate="collapsed" desc="Recall · F20">
    /// ### References: another name or another object?
    ///
    /// An array variable stores a reference. Assignment can give two names access to one array; writing
    /// through either name affects that shared object. Calling `clone()` on a one-dimensional int array
    /// creates another array with copied values. The original and copy can then change independently.
    ///
    /// ```java
    /// int[] wave = {4, 1};
    /// int[] shared = wave;
    /// int[] saved = wave.clone();
    /// wave[1] = 7;
    /// IO.println(shared[1]);
    /// IO.println(saved[1]);
    /// ```
    ///
    /// The output is seven, then one: shared sees the write, saved retains its copied value. After
    /// `saved[0] = 8`, predict `wave[0]` and `saved[0]`. `clone()` copies one layer: for a grid, its
    /// elements are row references, so copying the outside still shares rows. Ask which objects remain
    /// shared, not merely whether a new outer object exists.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Read this code">
    /// ## Read these particular lines
    ///
    /// - `char[][]` — Rows contain individual slash, backslash, or empty-cell characters.
    /// - `snapshot()` — Returns board data for one-step undo; it does not draw a screenshot.
    /// - `cells.clone()` — Copies the outer array but initially shares all its row arrays.
    ///
    /// ### What the prepared part already does
    ///
    /// command() captures a snapshot before an edit and restores it for undo, reopening a finished round. The
    /// supplied acyclic() uses union-find to reject loops; solved() also checks corner counts.
    // </editor-fold>

    // <editor-fold defaultstate="expanded" desc="For this game">
    /// ### Copying rows as well as the outside
    ///
    /// A grid's outer array stores row references. An outer clone therefore copies references to the same
    /// rows. Here is a separate two-row example with independent rows:
    ///
    /// ```java
    /// int[][] original = {{1}, {2}};
    /// int[][] copied = {original[0].clone(), original[1].clone()};
    /// copied[0][0] = 9;
    /// IO.println(original[0][0]);
    /// ```
    ///
    /// The output stays one because the changed row belongs to copied. Predict the result if its first
    /// row were merely `original[0]`. Your board has more rows: use the actual row count to repeat the
    /// copying operation. Each row's clone preserves its current elements. Explain which layer would
    /// remain shared if only the outside were copied.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
    /// The outer clone gives new row references but still points to the old rows.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
    /// Changing a character then changes both boards.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
    /// Create a new outer char[][] and clone each cells[row].
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
    char[][] cells;
    int[][] clues;
    String outcome;

    /// reset(): Restores the starting Slant round, clearing the state that belongs to the old round.
    void reset() {
        cells = new char[3][3];
        previous = null;
        for (char[] row : cells) {
            Arrays.fill(row, '.');
        }
        clues = new int[][]{{0, 1, 1, 1}, {1, 2, 2, 1}, {1, 2, 2, 1}, {1, 1, 1, 0}};
        outcome = "";
    }

    int root(int[] parent, int n) {
        while (parent[n] != n) {
            n = parent[n];
        }
        return n;
    }

    /// acyclic(): Supplied union-find joins endpoint components and rejects an edge whose endpoints are
    /// already joined.
    boolean acyclic() {
        int[] parent = new int[16];
        for (int i = 0; i < 16; i++) {
            parent[i] = i;
        }
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (cells[r][c] != '.') {
                    int a = r * 4 + c + (cells[r][c] == '/' ? 1 : 0);
                    int b = (r + 1) * 4 + c + (cells[r][c] == '/' ? 0 : 1);
                    int x = root(parent, a);
                    int y = root(parent, b);
                    if (x == y) {
                        return false;
                    }
                    parent[x] = y;
                }
            }
        }
        return true;
    }

    boolean solved() {
        int[][] counts = new int[4][4];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (cells[r][c] == '.') {
                    return false;
                }
                if (cells[r][c] == '/') {
                    counts[r][c + 1]++;
                    counts[r + 1][c]++;
                } else {
                    counts[r][c]++;
                    counts[r + 1][c + 1]++;
                }
            }
        }
        return Arrays.deepEquals(counts, clues) && acyclic();
    }

    /// command(): Validates one actual player command before applying its changes to this game.
    void command(String input) {
        if (input.equalsIgnoreCase("undo")) {
            require(previous != null);
            cells = previous;
            previous = null;
            outcome = "";
            return;
        }
        require(outcome.isEmpty());
        String[] p = input.split("\\s+");
        require(p.length == 3 && (p[2].equals("/") || p[2].equals("\\") || p[2].equals(".")));
        int r = Integer.parseInt(p[0]) - 1;
        int c = Integer.parseInt(p[1]) - 1;
        require(r >= 0 && r < 3 && c >= 0 && c < 3);
        previous = snapshot();
        cells[r][c] = p[2].charAt(0);
        if (solved()) {
            outcome = "Counts satisfied with no closed loops!";
        }
    }

    /// display(): Presents the current board and the legal input choices in the console.
    void display() {
        IO.println("undo restores the previous diagonal change.");
        IO.println("Vertex counts (4x4):");
        for (int[] row : clues) {
            IO.println(Arrays.toString(row));
        }
        IO.println("Cell diagonals (3x3), columns1-3:");
        for (int r = 0; r < 3; r++) {
            IO.println((r + 1) + " " + Arrays.toString(cells[r]));
        }
        IO.println(acyclic() ? "No closed loop" : "Closed loop: revise a diagonal");
    }

    /// main(): Starts the real game loop: display, read a command, validate, replay or quit.
    /// Prepared console flow: type a command and press Enter. A valid command redraws the board.
    /// Rejected input leaves the display alone; after a round, Enter or /r starts again and /q quits.
    void main() {
        reset();
        IO.println("Slant");
        IO.println("Fill the cells with diagonals matching every corner count, without making a closed loop.");
        IO.println("Enter row col / or row col backslash; . clears a cell. Match vertex counts with no loops. /r restarts; /q quits.");
        IO.println("Example: 1 1 / changes the upper-left cell. undo reverses one change, even after completing the puzzle.");
        display();
        while (true) {
            String input = IO.readln(outcome.isEmpty() ? "> " : "Enter: new round; undo: previous change; /r: restart; /q: quit: ");
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
            if (!outcome.isEmpty() && !input.equalsIgnoreCase("undo")) {
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
                IO.println("Invalid move. " + "Enter row col / or row col backslash; . clears a cell. Match vertex counts with no loops. /r restarts; /q quits.");
            }
        }
    }

    /// require(): Rejects an illegal command with an exception handled by the supplied input loop.
    void require(boolean valid) {
        if (!valid) {
            throw new IllegalArgumentException("Invalid move or command.");
        }
    }

    char[][] previous;
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
    /// - `=` — Stores the value on the right under the name on the left. It does not compare them.
    /// - `==` — Compares primitive values. For text contents, use equals instead.
    /// - `!=` — Checks that two values differ.
    /// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
    /// - `||` — At least one condition must be true; if the first is true, Java skips the second.
    /// - `!` — In front of a boolean expression, reverses true and false.
    /// - `++` — Increases a numeric value by one.
    /// - `+` — Adds numbers. When a String is involved, joins text instead.
    /// - `-` — Subtracts numbers; in front of one number, changes its sign.
    /// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
    ///   available.
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

}
