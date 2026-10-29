/// # Task · Hex
/// Lesson: 10.1 A bag need not know its size ahead of time — 01-lists.html
///
/// Build an unbroken road of your colour between opposite borders. Hexagons let a zigzag connect where
/// square-grid intuition says it cannot.
///
/// **Your mission:** Some genuine hex connections fail to win. Repair the neighbour directions inside
/// connected(int who), keeping the supplied search machinery.
///
/// Take the upper-left hex by entering 1 1, then alternate row-and-column
/// choices with a friend.

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

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Hex adjacency and the supplied work queue
///
/// The printed rows slant right. A cell has six touching neighbours: sketch two neighbouring
/// staggered rows and follow shared sides rather than square-grid diagonals. A direction pair is a
/// row offset followed by a column offset. The supplied `ArrayDeque` is a work queue: `add` schedules
/// a cell, `remove` takes the next one, and `seen` prevents repeated scheduling. You only change the
/// neighbourhood definition. Predict a connection that crosses between staggered rows.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[][] board;
int player;
String outcome;

void reset() {
    board = new int[5][5];
    player = 1;
    outcome = "";
}

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

void display() {
    IO.println("Player " + player + "; 1 joins top/bottom, 2 joins left/right. Columns 1-5.");
    for (int r = 0; r < 5; r++) {
        IO.println(" ".repeat(r) + (r + 1) + " " + Arrays.toString(board[r]));
    }
}

void main() {
    reset();
    IO.println("Hex");
    IO.println("Connect your two opposite borders: Player 1 joins top to bottom; Player 2 joins left to right.");
    IO.println("Enter row column (1-5) on the staggered board. /r restarts; /q quits.");
    IO.println("Example: 1 1 claims the upper-left cell. Players alternate and occupied cells cannot be claimed again.");
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
            IO.println("Invalid move. " + "Enter row column (1-5) on the staggered board. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
