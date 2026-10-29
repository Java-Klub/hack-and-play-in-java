/// # Úkol · MiniBattleshipHunt
/// Lekce: 8.1 Z řad vznikne mapa — 01-grids.html
///
/// Lov dvě skryté lodě zadáváním souřadnic. Zásah ti pomůže víc, když se můžeš spolehnout na oznámené
/// délky lodí.
///
/// **Tvoje mise:** Načtení flotily vytvoří prázdné moře bez lodí. Sestav smyčku, která přečte každý
/// souřadnicový pár ze selected a zapíše jej do board: první dvě položky patří lodi 1, zbývající tři
/// lodi 2. Použij délku vybraných dat.
///
/// První střelu zadej jako 1 1 a potvrď Enter, potom zkoušej další dvojice řádku
/// a sloupce.

void layout(int n) {
    board = new int[6][6];
    int[][][] maps = {{{0, 0}, {0, 1}, {3, 2}, {3, 3}, {3, 4}}, {{1, 4}, {2, 4}, {5, 0}, {5, 1}, {5, 2}}, {{4, 0}, {4, 1}, {0, 5}, {1, 5}, {2, 5}}};
    int[][] selected = maps[n];
}

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Čtení nested data (vnořených dat)
///
/// `maps` obsahuje rozmístění; `maps[n]` vybere jedno. To obsahuje dvojice souřadnic, takže
/// `maps[n][i]` vybere dvojici i. Poslední `[0]` a `[1]` čtou řádek a sloupec. `maps.length` počítá
/// rozmístění, zatímco `maps[n].length` počítá dvojice ve vybraném rozmístění. Před volbou meze
/// předpověz význam každé délky. Hodnoty dvojice jsou vnitřní coordinate (souřadnice) od nuly.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
