/// # Solved task · Slant
/// Lesson: 8.5 Read the past; write the future — 05-generations.html
///
/// Original assignment (this version is complete):
///
/// Place diagonal strokes to match the numbers at their corners without closing a loop. Undo makes it
/// safe to try one uncertain slash.
///
/// **Your mission:** Undo cannot recover the earlier board because saved rows change along with current
/// rows. Repair snapshot(), including the case of undoing a finishing move.
///
/// Run the program and enter 1 1 / to place the first slash, then type undo to check whether
/// that exact edit is restored.

char[][] snapshot() {
    char[][] copy = new char[cells.length][];
    for (int row = 0; row < cells.length; row++) {
        copy[row] = cells[row].clone();
    }
    return copy;
}

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

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Sketch a slash solution first and count its corner clues. Hide some clues for a partner, keeping at
/// least one loop trap they can reason about.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
char[][] cells;
int[][] clues;
String outcome;

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

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

char[][] previous;
// </editor-fold>
