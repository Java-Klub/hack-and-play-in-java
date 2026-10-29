/// # Task · SameGame
/// Lesson: 10.1 A bag need not know its size ahead of time — 01-lists.html
///
/// Remove touching blocks of the same colour, then watch the others fall and empty columns close. Larger
/// groups score more, but removing a tempting group may strand the blocks you need later.
///
/// **Your mission:** The starter can announce that no groups remain even when matching blocks touch.
/// Repair the work condition in group so all reachable matching cells are collected, rather than stopping
/// after the first result.
///
/// Look for two same-colour blocks that share an edge despite the early “No
/// groups remain” message; after your repair, enter the row and column of a block in that
/// group.

List<int[]> group(int row, int col) {
    var result = new ArrayList<int[]>();
    if (row < 0 || row >= 5 || col < 0 || col >= 5 || board[row][col] == 0) {
        return result;
    }
    boolean[][] seen = new boolean[5][5];
    var queue = new ArrayDeque<int[]>();
    queue.add(new int[]{row, col});
    seen[row][col] = true;
    while (!queue.isEmpty() && result.isEmpty()) {
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

// <editor-fold defaultstate="collapsed" desc="For this game">
/// Keep three prepared roles separate: queue holds discovered cells still waiting for a visit; result
/// holds cells already collected; seen remembers cells already scheduled so two routes cannot add
/// them twice. queue.remove() takes one waiting coordinate pair out, while result.add adds that cell
/// to the answer. An answer containing one cell does not imply that the waiting work is empty. The
/// initial “No groups remain” message comes from the game's own group search before any move; it is
/// an intended symptom, not a console-input problem. Repair the source and start a new run rather
/// than trying to restart away the fault.
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

// </editor-fold>
