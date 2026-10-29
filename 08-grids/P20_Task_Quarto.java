/// # Task · Quarto
/// Lesson: 8.4 Make a pattern from coordinates — 04-patterns.html
///
/// Your opponent chooses the piece you must place. Four pieces sharing any property can win, so an
/// innocent-looking diagonal may be a trap.
///
/// **Your mission:** Rows can win, but a winning column or diagonal is ignored. Extend won() to inspect
/// every kind of four-cell line.
///
/// Choose an unused piece number from 0 to 15, then let the other player enter
/// its destination cell from 1 to 16.

boolean won() {
    for (int row = 0; row < 4; row++) {
        if (line(row * 4, row * 4 + 1, row * 4 + 2, row * 4 + 3)) {
            return true;
        }
    }
    return false;
}

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### A flat board has an invisible row width
///
/// A flat array can hold rows consecutively. In an unrelated three-column picture, row one starts
/// after the first three values; its column two is at index five. Moving down one row keeps the
/// column but skips the row width. Draw this game's indices as a square. `line(a,b,c,d)` already
/// checks a proposed group, so your job is selecting positions, not decoding piece properties.
/// Predict one column and each diagonal before extending the search.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[] board;
boolean[] used;
int player;
int selected;
int moves;
String outcome;

void reset() {
    board = new int[16];
    Arrays.fill(board, -1);
    used = new boolean[16];
    player = 1;
    selected = -1;
    moves = 0;
    outcome = "";
}

boolean line(int a, int b, int c, int d) {
    if (board[a] < 0 || board[b] < 0 || board[c] < 0 || board[d] < 0) {
        return false;
    }
    int both = board[a] & board[b] & board[c] & board[d];
    int either = board[a] | board[b] | board[c] | board[d];
    return both != 0 || ((~either) & 15) != 0;
}

void command(String input) {
    require(outcome.isEmpty());
    int n = Integer.parseInt(input);
    if (selected < 0) {
        require(n >= 0 && n < 16 && !used[n]);
        selected = n;
        used[n] = true;
        player = 3 - player;
    } else {
        require(n >= 1 && n <= 16 && board[n - 1] < 0);
        board[n - 1] = selected;
        selected = -1;
        moves++;
        if (won()) {
            outcome = "Player " + player + " wins";
        } else {
            if (moves == 16) {
                outcome = "Draw";
            }
        }
    }
}

String piece(int n) {
    return String.format("%4s", Integer.toBinaryString(n)).replace(' ', '0');
}

void display() {
    IO.println("Player " + player + (selected < 0 ? ": choose a piece for your opponent" : ": place piece " + selected + " (" + piece(selected) + ")"));
    IO.println("Attributes: bits = tall, dark, square, hollow (1=yes, 0=no).");
    for (int r = 0; r < 4; r++) {
        IO.println(Arrays.toString(Arrays.copyOfRange(board, r * 4, r * 4 + 4)));
    }
    String available = "Unused pieces: ";
    for (int i = 0; i < 16; i++) {
        if (!used[i]) {
            available += i + "=" + piece(i) + " ";
        }
    }
    IO.println(available);
}

void main() {
    reset();
    IO.println("Quarto");
    IO.println("Give a piece to your opponent, then place the piece they gave you. Four in a line sharing any attribute wins.");
    IO.println("Choose unused piece 0-15, then its receiver places in cell 1-16 (row order). -1 is empty. /r restarts; /q quits.");
    IO.println("Example: choose piece 0; the next player can enter 1 to place it in the first cell. Read the current phase above.");
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
            IO.println("Invalid move. " + "Choose unused piece 0-15, then its receiver places in cell 1-16 (row order). -1 is empty. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
