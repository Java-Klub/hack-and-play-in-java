/// # Úkol · Hexapawn
/// Lekce: 8.3 Ptej se okolních buněk — 03-neighbours.html
///
/// Veď tři pěšce na malé desce a snaž se dojít na protější stranu nebo soupeři znemožnit tah. Pěšci
/// postupují dopředu a berou šikmo, takže i několik políček umožní pasti.
///
/// **Tvoje mise · Dej oběma armádám rozkazy:** Ani jedna armáda nemá úplné rozkazy. Doplň goalRow a
/// forwardStep pro oba hráče:
/// hráč 1 jde o -1 k řádku 0, hráč 2 o +1 k řádku 2. Připravená pravidla se tvých method (metod)
/// zeptají; vrať odpovědi bez změny desky.
///
/// Spusť program a jako čtyři samostatné tahy zadej 3 1 2 1; 1 2 2 1; 3 3 2 3; 2 1 3 1, čímž
/// hráč 2 dojde dolů.

int goalRow(int who) {
    return 0;
}

int forwardStep(int who) {
    return 0;
}

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Změnou existující desky reset vytvoř malou taktickou pozici s oběma hráči. Kamarádovi připrav
/// jeden vítězný a jeden lákavý prohrávající tah. Nejprve zachovej normální směry; potom zkus obrátit
/// současně obě armády i jejich cílové řádky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
