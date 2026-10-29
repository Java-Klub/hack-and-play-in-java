/// # Úkol · LightUp
/// Lekce: 8.4 Vyrob vzor podle souřadnic — 04-patterns.html
///
/// Rozmísti žárovky tak, aby svítila všechna volná políčka, ale žádné dvě žárovky na sebe neviděly. Zdi
/// mohou jednu chodbu rozdělit na několik samostatných světelných úloh.
///
/// **Tvoje mise:** Pravidlo vidí jen bezprostředně sousední žárovky. Oprav visible(), aby se rovná volná
/// chodba prověřovala až ke zdi nebo okraji.
///
/// Spusť program a zadáním 1 1 přepni žárovku vlevo nahoře, potom zkus osvětlit ostatní volná
/// políčka bez střetů žárovek.

int visible(int r, int c) {
    int n = 0;
    for (int[] d : dirs) {
        int y = r + d[0];
        int x = c + d[1];
        if (open(y, x)) {
            if (bulbs[y][x]) {
                n++;
            }
            y += d[0];
            x += d[1];
        }
    }
    return n;
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String[] walls = {"...", ".0.", "..."};
boolean[][] bulbs;
String outcome;
int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

void reset() {
    bulbs = new boolean[3][3];
    outcome = "";
}

boolean open(int r, int c) {
    return r >= 0 && r < 3 && c >= 0 && c < 3 && walls[r].charAt(c) == '.';
}

boolean solved() {
    for (int r = 0; r < 3; r++) {
        for (int c = 0; c < 3; c++) {
            if (open(r, c)) {
                if (bulbs[r][c] && visible(r, c) > 0 || !bulbs[r][c] && visible(r, c) == 0) {
                    return false;
                }
            } else {
                int count = 0;
                for (int[] d : dirs) {
                    int y = r + d[0];
                    int x = c + d[1];
                    if (open(y, x) && bulbs[y][x]) {
                        count++;
                    }
                }
                if (count != walls[r].charAt(c) - '0') {
                    return false;
                }
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
    require(open(r, c));
    bulbs[r][c] = !bulbs[r][c];
    if (solved()) {
        outcome = "All cells lit, no bulb conflicts, wall numbers satisfied!";
    }
}

void display() {
    IO.println("Columns1-3. B bulb, + lit, . dark, digit wall with adjacent-bulb count.");
    for (int r = 0; r < 3; r++) {
        String s = (r + 1) + " ";
        for (int c = 0; c < 3; c++) {
            s += !open(r, c) ? walls[r].charAt(c) : bulbs[r][c] ? 'B' : visible(r, c) > 0 ? '+' : '.';
        }
        IO.println(s);
    }
}

void main() {
    reset();
    IO.println("Light Up");
    IO.println("Light every open cell without two bulbs seeing each other. A wall digit counts bulbs touching its four sides.");
    IO.println("Enter row column (1-3) to toggle a bulb. Light every open cell; bulbs cannot see each other. /r restarts; /q quits.");
    IO.println("Example: 1 1 toggles a bulb at the upper-left. B is a bulb; + is lit; a dot is still dark.");
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
            IO.println("Invalid move. " + "Enter row column (1-3) to toggle a bulb. Light every open cell; bulbs cannot see each other. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
