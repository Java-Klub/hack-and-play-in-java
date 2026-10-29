/// # Úkol · Hex
/// Lekce: 10.1 Batoh nemusí znát délku předem — 01-lists.html
///
/// Postav souvislou cestu své barvy mezi protilehlými okraji. Šestiúhelníky propojí i cikcak, které by na
/// čtvercové síti nefungovalo.
///
/// **Tvoje mise:** Některá skutečná spojení šestiúhelníků nevyhrávají. Oprav sousední směry uvnitř
/// connected(int who) a zachovej připravené prohledávání.
///
/// Zaber levý horní šestiúhelník zadáním 1 1, potom se s kamarádem střídejte ve
/// volbě řádku a sloupce.

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

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Hex adjacency (sousedství hexů) a připravená queue (fronta)
///
/// Vypsané řádky se sklánějí doprava. Buňka má šest dotýkajících se sousedů: nakresli dva posunuté
/// řádky a sleduj společné strany místo úhlopříček čtvercové mřížky. Dvojice směru obsahuje posun
/// řádku a potom sloupce. Připravená ArrayDeque je work queue (fronta práce): `add` naplánuje buňku,
/// `remove` vezme další a `seen` brání opakování. Měníš jen definici sousedství. Předpověz spojení
/// přecházející mezi posunutými řádky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
