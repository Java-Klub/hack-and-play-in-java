/// # Solved task · Dominosa
/// Lesson: 9.1 Give related data a shape — 01-records.html
///
/// Original assignment (this version is complete):
///
/// Cover the number board with dominoes, using each number pair only once. Turning a domino around should
/// not create a second kind of piece.
///
/// **Your mission:** A reversed duplicate slips through the uniqueness check. Repair pairKey(int a, int
/// b) so equal domino values are represented consistently.
///
/// Type 1 1 1 2 to pair the first two adjacent cells, then continue with
/// other neighbouring cells.

record Domino(int low, int high) {
}

Domino pairKey(int a, int b) {
    int x = numbers[a / 10][a % 10], y = numbers[b / 10][b % 10];
    return new Domino(Math.min(x, y), Math.max(x, y));
}

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Values and canonical representation
///
/// `Math.min(a,b)` selects the smaller number and `Math.max(a,b)` the larger. These queries do not
/// change either input. A Domino compares the ordered components low and high, not the physical way a
/// player turned it. First read the two domino values from `numbers`; the selected cell indices are
/// not those values. Predict a consistent component pair for values three and eight presented in
/// either order, and also for two equal values.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[][] numbers = {{0, 0, 0, 1, 0, 2, 0, 3, 1, 1}, {1, 2, 1, 3, 2, 2, 2, 3, 3, 3}};
int[] partner;

Set<Domino> used;
String outcome;

void reset() {
    partner = new int[20];
    Arrays.fill(partner, -1);
    used = new HashSet<>();
    outcome = "";
}

int cell(String row, String col) {
    int r = Integer.parseInt(row) - 1;
    int c = Integer.parseInt(col) - 1;
    require(r >= 0 && r < 2 && c >= 0 && c < 10);
    return r * 10 + c;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    if (p.length == 3 && p[0].equalsIgnoreCase("erase")) {
        int a = cell(p[1], p[2]);
        require(partner[a] >= 0);
        int b = partner[a];
        used.remove(pairKey(a, b));
        partner[a] = partner[b] = -1;
    } else {
        require(p.length == 4);
        int a = cell(p[0], p[1]);
        int b = cell(p[2], p[3]);
        require(Math.abs(a / 10 - b / 10) + Math.abs(a % 10 - b % 10) == 1 && partner[a] < 0 && partner[b] < 0 && !used.contains(pairKey(a, b)));
        used.add(pairKey(a, b));
        partner[a] = b;
        partner[b] = a;
        if (used.size() == 10) {
            outcome = "All ten distinct dominoes cover the board!";
        }
    }
}

void display() {
    IO.println("Authored 0-3 set. Rows1-2; columns1-10. Match adjacent cells once per unordered pair.");
    for (int r = 0; r < 2; r++) {
        IO.println((r + 1) + " " + Arrays.toString(numbers[r]));
    }
    IO.println("Current partners (cell numbers1-20):");
    for (int i = 0; i < 20; i++) {
        if (partner[i] > i) {
            IO.println((i + 1) + "--" + (partner[i] + 1));
        }
    }
}

void main() {
    reset();
    IO.println("Dominosa");
    IO.println("Cover every cell with an adjacent pair. Use each unordered number pair from 0 to 3 exactly once.");
    IO.println("Pair with row col row col; erase row col removes a domino. Use every unordered pair0-3 once. /r restarts; /q quits.");
    IO.println("Example: 1 1 1 2 pairs the first two cells of the first row. erase 1 1 removes that pair.");
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
            IO.println("Invalid move. " + "Pair with row col row col; erase row col removes a domino. Use every unordered pair0-3 once. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
