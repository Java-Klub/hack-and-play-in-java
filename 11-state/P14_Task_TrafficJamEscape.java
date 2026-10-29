/// # Úkol · TrafficJamEscape
/// Lekce: 11.3 Odmítnutí nesmí poškodit stav — 03-invariants.html
///
/// Posouvej auta v zácpě, dokud auto A nevyjede pravým okrajem. Vyzkoušení zablokovaného tahu má být
/// neškodný pokus.
///
/// **Tvoje mise:** Odmítnutá poloha přesto může auto tajně přesunout. Oprav fits(), aby zkoušení možné
/// polohy tuto polohu rovnou nezapsalo.
///
/// Zkus posun auta zadáním B 2, potom pomocí písmene auta a počtu kroků se
/// znaménkem uvolni výjezd auta A.

boolean fits(int car, int x, int y) {
    xs[car] = x;
    ys[car] = y;
    for (int i = 0; i < lengths[car]; i++) {
        int px = x + (vertical[car] ? 0 : i);
        int py = y + (vertical[car] ? i : 0);
        if (py < 0 || py >= 6 || px < 0 || px >= 6 && car != 0) {
            return false;
        }
        for (int j = 0; j < 2; j++) {
            if (j != car) {
                for (int k = 0; k < lengths[j]; k++) {
                    if (px == xs[j] + (vertical[j] ? 0 : k) && py == ys[j] + (vertical[j] ? k : 0)) {
                        return false;
                    }
                }
            }
        }
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[] xs;
int[] ys;
int[] lengths = {2, 3};
boolean[] vertical = {false, true};
String outcome;

void reset() {
    xs = new int[]{0, 2};
    ys = new int[]{2, 1};
    outcome = "";
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.toUpperCase(Locale.ROOT).split("\\s+");
    require(p.length == 2 && p[0].matches("[AB]"));
    int car = p[0].charAt(0) - 'A';
    int amount = Integer.parseInt(p[1]);
    require(amount != 0 && Math.abs((long)amount) <= 6);
    int x = xs[car];
    int y = ys[car];
    int sign = Integer.signum(amount);
    for (int i = 0; i < Math.abs(amount); i++) {
        x += vertical[car] ? 0 : sign;
        y += vertical[car] ? sign : 0;
        require(fits(car, x, y));
    }
    xs[car] = x;
    ys[car] = y;
    if (xs[0] >= 6) {
        outcome = "A escaped: you win!";
    }
}

void display() {
    char[][] grid = new char[6][6];
    for (char[] row : grid) {
        Arrays.fill(row, '.');
    }
    for (int car = 0; car < 2; car++) {
        for (int i = 0; i < lengths[car]; i++) {
            int x = xs[car] + (vertical[car] ? 0 : i);
            int y = ys[car] + (vertical[car] ? i : 0);
            if (x < 6) {
                grid[y][x] = (char)('A' + car);
            }
        }
    }
    for (int r = 0; r < 6; r++) {
        IO.println(new String(grid[r]) + (r == 2 ? " -> EXIT" : ""));
    }
}

void main() {
    reset();
    IO.println("Traffic Jam Escape");
    IO.println("Move the red car A fully through the right-hand exit. Other cars slide only along their own orientation.");
    IO.println("Enter car and signed steps, e.g. B 2. Positive means right/down. Move A fully out right. /r restarts; /q quits.");
    IO.println("Example: B 2 moves B two cells down, clearing A's row. A 6 then tries to drive A fully out.");
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
            IO.println("Invalid move. " + "Enter car and signed steps, e.g. B 2. Positive means right/down. Move A fully out right. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
