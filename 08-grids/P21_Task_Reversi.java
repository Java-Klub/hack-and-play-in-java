/// # Solved task · Reversi
/// Lesson: 8.4 Make a pattern from coordinates — 04-patterns.html
///
/// Original assignment (this version is complete):
///
/// Trap your opponent’s discs between your own to flip them. A player may pass only when there is truly
/// nowhere to play.
///
/// **Your mission:** Create corner-prize Reversi: each occupied corner is worth three points and every
/// other occupied square one. Empty squares earn nothing. Show both scores during play and use those
/// same scores to decide the result after two passes. Moves, captures and legal passing stay as they
/// are. Construct one tiny board where disc count and prize score name different leaders.
///
/// Play an opening with 2 3. Read the displayed scores, then trace what happens after both players have
/// no legal move and pass.

int score(int who) {
    int total = 0;
    for (int row = 0; row < board.length; row++) {
        for (int column = 0; column < board[row].length; column++) {
            if (board[row][column] == who) {
                boolean corner = (row == 0 || row == board.length - 1)
                        && (column == 0 || column == board[row].length - 1);
                total += corner ? 3 : 1;
            }
        }
    }
    return total;
}

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
boolean hasMove(int who) {
    for (int r = 0; r < 6; r++) {
        for (int c = 0; c < 6; c++) {
            if (!captures(r, c, who).isEmpty()) {
                return true;
            }
        }
    }
    return false;
}

int[][] board = new int[6][6];
int player;
int passes;
String outcome;

void reset() {
    board = new int[6][6];
    board[2][2] = board[3][3] = 2;
    board[2][3] = board[3][2] = 1;
    player = 1;
    passes = 0;
    outcome = "";
}

List<int[]> captures(int row, int col, int who) {
    var result = new ArrayList<int[]>();
    if (row < 0 || row >= 6 || col < 0 || col >= 6 || board[row][col] != 0) {
        return result;
    }
    for (int dr = -1; dr <= 1; dr++) {
        for (int dc = -1; dc <= 1; dc++) {
            if (dr == 0 && dc == 0) {
                continue;
            }
            var line = new ArrayList<int[]>();
            int r = row + dr;
            int c = col + dc;
            while (r >= 0 && r < 6 && c >= 0 && c < 6 && board[r][c] == 3 - who) {
                line.add(new int[]{r, c});
                r += dr;
                c += dc;
            }
            if (r >= 0 && r < 6 && c >= 0 && c < 6 && board[r][c] == who) {
                result.addAll(line);
            }
        }
    }
    return result;
}

void command(String input) {
    require(outcome.isEmpty());
    if (input.equalsIgnoreCase("pass")) {
        require(!hasMove(player));
        passes++;
    } else {
        String[] p = input.split("\\s+");
        require(p.length == 2);
        int r = Integer.parseInt(p[0]) - 1;
        int c = Integer.parseInt(p[1]) - 1;
        var flips = captures(r, c, player);
        require(!flips.isEmpty());
        board[r][c] = player;
        for (int[] cell : flips) {
            board[cell[0]][cell[1]] = player;
        }
        passes = 0;
    }
    player = 3 - player;
    if (passes == 2) {
        int firstScore = score(1);
        int secondScore = score(2);
        outcome = firstScore == secondScore ? "Draw" : "Player " + (firstScore > secondScore ? 1 : 2) + " wins";
    }
}

void display() {
    IO.println("Score P1: " + score(1) + "  P2: " + score(2));
    IO.println("Columns 1-6; player " + player + ". 1=X, 2=O, 0=empty");
    for (int r = 0; r < 6; r++) {
        IO.println((r + 1) + " " + Arrays.toString(board[r]));
    }
}

void main() {
    reset();
    IO.println("Reversi");
    IO.println("Place a disc to trap an enemy line between two of yours; trapped discs flip. Higher displayed score wins after both players pass.");
    IO.println("Enter row column (1-6), or pass when blocked. /r restarts; /q quits.");
    IO.println("Example: 2 3 is an opening move for Player 1. Enter pass only when you have no legal move.");
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
            IO.println("Invalid move. " + "Enter row column (1-6), or pass when blocked. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
