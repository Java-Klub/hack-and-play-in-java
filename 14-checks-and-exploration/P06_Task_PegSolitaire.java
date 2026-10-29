/// # Solved task · PegSolitaire
/// Lesson: 14.1 Choose examples that catch mistakes — 01-checks.html
///
/// Original assignment (this version is complete):
///
/// **Investigate the peg-game referee.** The game already works. This time you create examples
/// that expose fake jumping rules rather than repair the game.
///
/// **Your task:** Extend `learnerChecks` below. For each proposed jump, write its start,
/// destination and expected answer: `true` for legal or `false` for illegal. Each example is
/// judged independently on the same initial board. Count rows and columns from zero in these
/// data; ordinary play numbers them from one.
///
/// **Result:** Your examples accept the real rule but expose jumping over an empty middle,
/// jumping diagonally, and accepting every proposal. Merely finishing the puzzle does not
/// complete this mission.

record JumpExample(int row, int column, int toRow, int toColumn, boolean expected) {
}

JumpExample[] learnerChecks() {
    return new JumpExample[]{
    new JumpExample(0, 1, 2, 1, true), new JumpExample(2, 0, 2, 2, false),
    new JumpExample(0, 1, 1, 2, false), new JumpExample(-1, 1, 1, 1, false)};
}

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Permission is a result too
///
/// Every JumpExample asks about the same fixed starting board, rather than continuing the previous
/// example's move. Read `expected` as permission, not success at solving the puzzle. A legal move
/// needs a peg at the start, a jumped peg between the endpoints, and an empty destination in a
/// straight line. Predict both an accepted and a refused request from that opening. Which single part
/// of the contract would a rule accepting every request ignore?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[][] board;
String outcome;

void reset() {
    board = new int[5][5];
    outcome = "";
    for (int r = 0; r < 5; r++) {
        for (int c = 0; c < 5; c++) {
            board[r][c] = (r > 0 && r < 4 || c > 0 && c < 4) ? 0 : -1;
        }
    }
    board[2][0] = board[0][1] = board[1][1] = 1;
}

boolean legal(int r, int c, int nr, int nc) {
    return r >= 0 && r < 5 && c >= 0 && c < 5 && nr >= 0 && nr < 5 && nc >= 0 && nc < 5 && Math.abs(nr - r) + Math.abs(nc - c) == 2 && (r == nr || c == nc) && board[r][c] == 1 && board[nr][nc] == 0 && board[(r + nr) / 2][(c + nc) / 2] == 1;
}

int pegs() {
    int n = 0;
    for (int[] row : board) {
        for (int v : row) {
            if (v == 1) {
                n++;
            }
        }
    }
    return n;
}

boolean canJump() {
    for (int r = 0; r < 5; r++) {
        for (int c = 0; c < 5; c++) {
            for (int[] d : new int[][]{{2, 0}, {-2, 0}, {0, 2}, {0, -2}}) {
                if (legal(r, c, r + d[0], c + d[1])) {
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
    require(legal(r, c, nr, nc));
    board[r][c] = board[(r + nr) / 2][(c + nc) / 2] = 0;
    board[nr][nc] = 1;
    if (pegs() == 1) {
        outcome = "One peg: you win!";
    } else {
        if (!canJump()) {
            outcome = "No jumps left: you lose.";
        }
    }
}

void display() {
    IO.println("Columns 1-5; # peg, . empty, space outside. Pegs: " + pegs());
    for (int r = 0; r < 5; r++) {
        String s = (r + 1) + " ";
        for (int v : board[r]) {
            s += v < 0 ? "  " : v == 1 ? "# " : ". ";
        }
        IO.println(s);
    }
}

void main() {
    reset();
    verifyExamples();
    IO.println("Peg Solitaire");
    IO.println("Jump a peg over one adjacent peg into an empty cell. The jumped peg disappears; finish with one peg.");
    IO.println("Enter fromRow fromCol toRow toCol (1-5). A short authored two-jump cross puzzle. /r restarts; /q quits.");
    IO.println("Example: 1 2 3 2 jumps from row 1, column 2 to row 3, column 2.");
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
            IO.println("Invalid move. " + "Enter fromRow fromCol toRow toCol (1-5). A short authored two-jump cross puzzle. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

void verifyExamples() {
    for (var e : learnerChecks()) {
        if (legal(e.row(), e.column(), e.toRow(), e.toColumn()) != e.expected()) {
            throw new IllegalStateException("Jump (" + e.row() + "," + e.column() + ")->("
                    + e.toRow() + "," + e.toColumn() + "): expected legal=" + e.expected()
                    + ", actual=" + legal(e.row(), e.column(), e.toRow(), e.toColumn()));
        }
    }
}
// </editor-fold>
