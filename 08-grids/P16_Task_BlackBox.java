/// # Vyřešený úkol · BlackBox
/// Lekce: 8.3 Ptej se okolních buněk — 03-neighbours.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Posílej paprsky neviditelnou krabicí a odvozuj, kde jsou skryté kuličky. Zásahy, odrazy i zahnuté
/// výstupy prověří tvou teorii.
///
/// **Tvoje mise:** Paprsek prověřuje tutéž stranu dvakrát, takže některé stopy lžou. Oprav výpočet
/// bočních sousedů v trace() a zachovej připravená pravidla paprsku.
///
/// Spusť program a zadáním ray N1 prozkoumej krabici shora, potom pozorování porovnej s paprsky
/// z dalších označených vstupů.

String trace(boolean[][] grid, String entry) {
    require(entry.matches("[NSEW][1-4]"));
    int n = entry.charAt(1) - '1';
    char side = entry.charAt(0);
    int x = side == 'W' ? -1 : side == 'E' ? 4 : n;
    int y = side == 'N' ? -1 : side == 'S' ? 4 : n;
    int dx = side == 'W' ? 1 : side == 'E' ? -1 : 0;
    int dy = side == 'N' ? 1 : side == 'S' ? -1 : 0;
    boolean entering = true;
    var seen = new HashSet<String>();
    while (true) {
        if (!seen.add(x + "," + y + "," + dx + "," + dy)) {
            return "LOOP";
        }
        int nx = x + dx;
        int ny = y + dy;
        if (ball(grid, nx, ny)) {
            return "HIT";
        }
        boolean left = ball(grid, nx - dy, ny + dx);
        boolean right = ball(grid, nx + dy, ny - dx);
        if (entering && (left || right)) {
            return "REFLECT";
        }
        if (left && right) {
            dx = -dx;
            dy = -dy;
        } else {
            if (left) {
                int old = dx;
                dx = dy;
                dy = -old;
            } else {
                if (right) {
                    int old = dx;
                    dx = -dy;
                    dy = old;
                } else {
                    x = nx;
                    y = ny;
                    if (x < 0 || x >= 4 || y < 0 || y >= 4) {
                        String exit = port(x, y);
                        return exit.equals(entry) ? "REFLECT" : exit;
                    }
                }
            }
        }
        entering = false;
    }
}

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Kolmý směr je vztažený k pohybu
///
/// Direction (směr) je dvojice změn, ne absolutní pozice obrazovky. Paprsek mířící doprava ve směru
/// `(1,0)` má dva boční posuny `(0,1)` a `(0,-1)`. Jsou opačné. Společným počátkem obou bočních
/// dotazů je další pozice vpřed. Nakresli stejnou konstrukci pro paprsek mířící vzhůru. Zachovej
/// připravená pravidla otočení a odrazu; s pohybem mají souhlasit dvě odlišné boční pozice.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
boolean[][] hidden;
boolean[][] marked;
List<String> clues;
String outcome;
String message;

void reset() {
    hidden = new boolean[4][4];
    hidden[1][1] = hidden[2][3] = true;
    marked = new boolean[4][4];
    clues = new ArrayList<>();
    outcome = message = "";
}

boolean ball(boolean[][] grid, int x, int y) {
    return x >= 0 && x < 4 && y >= 0 && y < 4 && grid[y][x];
}

String port(int x, int y) {
    return y < 0 ? "N" + (x + 1) : y >= 4 ? "S" + (x + 1) : x < 0 ? "W" + (y + 1) : "E" + (y + 1);
}

boolean equivalent() {
    for (char side : new char[]{'N', 'S', 'E', 'W'}) {
        for (int n = 1; n <= 4; n++) {
            if (!trace(hidden, "" + side + n).equals(trace(marked, "" + side + n))) {
                return false;
            }
        }
    }
    return true;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.toUpperCase(Locale.ROOT).split("\\s+");
    if (p.length == 2 && p[0].equals("RAY")) {
        clues.add(p[1] + " -> " + trace(hidden, p[1]));
        message = "Probe recorded";
    } else {
        if (p.length == 3 && p[0].equals("MARK")) {
            int r = Integer.parseInt(p[1]) - 1;
            int c = Integer.parseInt(p[2]) - 1;
            require(r >= 0 && r < 4 && c >= 0 && c < 4);
            marked[r][c] = !marked[r][c];
            message = marked[r][c] ? "Guess marked" : "Guess removed";
        } else {
            require(p.length == 1 && p[0].equals("CHECK"));
            int count = 0;
            for (boolean[] row : marked) {
                for (boolean b : row) {
                    if (b) {
                        count++;
                    }
                }
            }
            require(count == 2);
            if (equivalent()) {
                outcome = "All possible ray observations agree: solved!";
                message = "";
            } else {
                message = "Some observations disagree. Revise the marks.";
            }
        }
    }
}

void display() {
    IO.println("Exactly two balls. Borders N/S use columns1-4; E/W use rows1-4.");
    for (int r = 0; r < 4; r++) {
        String s = (r + 1) + " ";
        for (boolean b : marked[r]) {
            s += b ? "O " : ". ";
        }
        IO.println(s);
    }
    for (String clue : clues) {
        IO.println(clue);
    }
    IO.println(message);
}

void main() {
    reset();
    IO.println("Black Box");
    IO.println("Find two hidden balls by probing rays, then mark two guesses and check their observations.");
    IO.println("ray N1/S1/E1/W1 probes; mark row col toggles a guess; check tests all ray observations. /r restarts; /q quits.");
    IO.println("Example: ray N1 enters from the north at column 1; mark 2 2 toggles a guess. HIT is absorption; REFLECT returns to entry.");
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
            IO.println("Invalid move. " + "ray N1/S1/E1/W1 probes; mark row col toggles a guess; check tests all ray observations. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
