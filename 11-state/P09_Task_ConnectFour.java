/// # Task · ConnectFour
/// Lesson: 11.2 Each robot has its own battery — 02-instances.html
///
/// Drop discs into columns and race a friend to connect four. A replay should give you a fresh board and
/// a fresh round.
///
/// **Your mission:** Make rematches alternate the opening player: the first round starts with X, the
/// next with O, then X again. This must not depend on who moved last or who won. Every rematch still
/// clears the board, winner and move count. Use the visible turn and Moves to investigate which facts
/// live for a round and which must survive across rounds.
///
/// Start a round, enter 4 and then r. Compare the next opening player with another restart made before
/// any disc is dropped. The current game always opens with X.

void reset() {
    for (char[] row : board) {
        Arrays.fill(row, '.');
    }
    player = 'X';
    winner = '.';
    moves = 0;
}

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
char[][] board = {
".......".toCharArray(),
".......".toCharArray(),
".......".toCharArray(),
".......".toCharArray(),
".......".toCharArray(),
".......".toCharArray()
};
char player = 'X';
char winner = '.';
int moves;

int count(int row, int column, int dr, int dc) {
    char mark = board[row][column];
    int total = 0;
    row += dr;
    column += dc;
    while (row >= 0 && row < 6 && column >= 0 && column < 7 && board[row][column] == mark) {
        total++;
        row += dr;
        column += dc;
    }
    return total;
}

boolean winsAt(int row, int column) {
    int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
    for (int[] direction : directions) {
        int dr = direction[0];
        int dc = direction[1];
        if (1 + count(row, column, dr, dc) + count(row, column, -dr, -dc) >= 4) {
            return true;
        }
    }
    return false;
}

boolean drop(int column) {
    if (winner != '.' || column < 1 || column > 7 || board[0][column - 1] != '.') {
        return false;
    }
    column--;
    int row = 5;
    while (board[row][column] != '.') {
        row--;
    }
    board[row][column] = player;
    moves++;
    if (winsAt(row, column)) {
        winner = player;
    } else {
        if (moves == 42) {
            winner = 'D';
        } else {
            player = player == 'X' ? 'O' : 'X';
        }
    }
    return true;
}

void display() {
    IO.println("1 2 3 4 5 6 7");
    IO.println("Moves: " + moves);
    for (char[] row : board) {
        String line = "";
        for (char mark : row) {
            line += mark + " ";
        }
        IO.println(line);
    }
    if (winner != '.') {
        IO.println(winner == 'D' ? "Draw!" : winner + " wins!");
    }
}

void main() {
    reset();
    IO.println("CONNECT FOUR: two players; four connected pieces win.");
    IO.println("Drop a disc into a column; it falls to the lowest empty cell. Connect four in any direction.");
    IO.println("Example: 4 drops into the middle column. r or /r restarts; q or /q quits.");
    display();
    while (true) {
        String input = IO.readln(winner == '.' ? player + ": column 1-7, r restart, q quit: " : "Enter: replay, r restart, q quit: ");
        if (input == null) {
            return;
        }
        input = input.strip();
        if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("/q")) {
            return;
        }
        if (input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r") || (winner != '.' && input.isEmpty())) {
            reset();
            display();
            continue;
        }
        if (winner != '.') {
            IO.println("That round is over. Press Enter to play again, or q to quit.");
            continue;
        }
        try {
            if (drop(Integer.parseInt(input))) {
                display();
            } else {
                IO.println("Illegal move. Choose a column with space during an unfinished round.");
            }
        } catch (NumberFormatException exception) {
            IO.println("Enter a column number, r or q.");
        }
    }
}
// </editor-fold>
