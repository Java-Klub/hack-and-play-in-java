/// # Solved task · Hexapawn
/// Lesson: 8.3 Ask the neighbouring cells — 03-neighbours.html
///
/// Original assignment (this version is complete):
///
/// Command three pawns on a tiny board and try to reach the far side or leave the opponent unable to
/// move. Pawns move forward and capture diagonally, so even a few squares allow traps.
///
/// **Your mission · Give both armies their orders:** Neither army has complete orders. Implement goalRow
/// and forwardStep for both
/// players: player 1 moves -1 towards row 0, player 2 moves +1 towards row 2. The prepared move rules
/// will ask your methods; return answers without changing the board.
///
/// Run the program and enter 3 1 2 1; 1 2 2 1; 3 3 2 3; 2 1 3 1 as four separate moves to bring
/// player 2 to the bottom.

int goalRow(int who) {
    if (who == 1) {
        return 0;
    } else {
        return 2;
    }
}

int forwardStep(int who) {
    if (who == 1) {
        return -1;
    }
    return 1;
}

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Create a tiny tactical position by changing the existing reset board, with both players still
/// present. Give a friend one move that wins and one tempting move that loses. Keep the normal
/// directions first; only then try reversing both armies and their goal rows together.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[][] board;
int player;
String outcome;

void reset() {
    board = new int[][]{{2, 2, 2}, {0, 0, 0}, {1, 1, 1}};
    player = 1;
    outcome = "";
}

boolean legal(int r, int c, int nr, int nc, int who) {
    if (r < 0 || r > 2 || c < 0 || c > 2 || nr < 0 || nr > 2 || nc < 0 || nc > 2) {
        return false;
    }
    int direction = forwardStep(who);
    return board[r][c] == who && nr == r + direction && (nc == c && board[nr][nc] == 0 || Math.abs(nc - c) == 1 && board[nr][nc] == 3 - who);
}

boolean canMove(int who) {
    for (int r = 0; r < 3; r++) {
        for (int c = 0; c < 3; c++) {
            for (int nc = 0; nc < 3; nc++) {
                if (legal(r, c, r + forwardStep(who), nc, who)) {
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
    require(legal(r, c, nr, nc, player));
    board[nr][nc] = player;
    board[r][c] = 0;
    if (nr == goalRow(player) || !canMove(3 - player)) {
        outcome = "Player " + player + " wins";
    } else {
        player = 3 - player;
    }
}

void display() {
    IO.println("Player " + player + "; columns 1-3, 1 moves up, 2 moves down");
    for (int r = 0; r < 3; r++) {
        IO.println((r + 1) + " " + Arrays.toString(board[r]));
    }
}

void main() {
    reset();
    IO.println("Hexapawn");
    IO.println("Enter fromRow fromColumn toRow toColumn (1-3). /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter fromRow fromColumn toRow toColumn (1-3). /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

// </editor-fold>
