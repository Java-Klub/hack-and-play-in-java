/// # Task · MiniBattleshipHunt
/// Lesson: 8.1 Rows become a map — 01-grids.html
///
/// Hunt two hidden ships by calling grid coordinates. A hit becomes more useful when you can trust the
/// announced ship lengths.
///
/// **Your mission:** The fleet loader creates an empty sea but places no ships. Build the loop that
/// reads each coordinate pair from selected and writes it to board: the first two entries belong to
/// ship 1, the remaining three to ship 2. Use the selected data’s length.
///
/// Fire your first shot by typing 1 1 and pressing Enter, then try other
/// row-and-column pairs.

void layout(int n) {
    board = new int[6][6];
    int[][][] maps = {{{0, 0}, {0, 1}, {3, 2}, {3, 3}, {3, 4}}, {{1, 4}, {2, 4}, {5, 0}, {5, 1}, {5, 2}}, {{4, 0}, {4, 1}, {0, 5}, {1, 5}, {2, 5}}};
    int[][] selected = maps[n];
}

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Reading nested data
///
/// `maps` contains layouts; `maps[n]` selects one layout. That layout contains coordinate pairs, so
/// `maps[n][i]` selects pair i. The final `[0]` and `[1]` read its row and column. `maps.length`
/// counts layouts, whereas `maps[n].length` counts pairs in the selected layout. Predict what each
/// length measures before choosing the loop boundary. The pair values are internal coordinates
/// starting at zero.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[][] board;
boolean[][] shots;
int attempts;
String outcome;
String message;

void reset() {
    layout(new Random().nextInt(3));
    shots = new boolean[6][6];
    attempts = 0;
    outcome = message = "";
}

boolean sunk(int ship) {
    for (int r = 0; r < 6; r++) {
        for (int c = 0; c < 6; c++) {
            if (board[r][c] == ship && !shots[r][c]) {
                return false;
            }
        }
    }
    return true;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 2);
    int r = Integer.parseInt(p[0]) - 1;
    int c = Integer.parseInt(p[1]) - 1;
    require(r >= 0 && r < 6 && c >= 0 && c < 6 && !shots[r][c]);
    shots[r][c] = true;
    attempts++;
    int ship = board[r][c];
    message = ship == 0 ? "Miss" : sunk(ship) ? "Sunk ship " + ship : "Hit";
    if (sunk(1) && sunk(2)) {
        outcome = "Both ships sunk in " + attempts + " shots!";
    }
}

void display() {
    IO.println(message + "; shots " + attempts + "; two ships of lengths2 and3. Columns1-6.");
    for (int r = 0; r < 6; r++) {
        String s = (r + 1) + " ";
        for (int c = 0; c < 6; c++) {
            s += !shots[r][c] ? "? " : board[r][c] == 0 ? "~ " : "X ";
        }
        IO.println(s);
    }
}

void main() {
    reset();
    IO.println("Mini Battleship Hunt");
    IO.println("Sink both hidden ships: one has two cells, the other three. Only fresh shots count.");
    IO.println("Enter row column (1-6). Repeated shots do not count. Sink both ships. /r restarts; /q quits.");
    IO.println("Example: 1 1 fires at row 1, column 1. X is a hit; ~ is a miss.");
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
            IO.println("Invalid move. " + "Enter row column (1-6). Repeated shots do not count. Sink both ships. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
