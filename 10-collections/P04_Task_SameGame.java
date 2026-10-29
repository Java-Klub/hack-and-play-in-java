/// # Vyřešený úkol · SameGame
/// Lekce: 10.1 Batoh nemusí znát délku předem — 01-lists.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Odstraňuj dotýkající se bloky stejné barvy a sleduj, jak ostatní spadnou a prázdné sloupce se zavřou.
/// Větší skupiny dávají více bodů, ale lákavé odstranění může izolovat bloky potřebné později.
///
/// **Tvoje mise:** Starter (výchozí program) může oznámit, že nezbývají skupiny, i když se stejné bloky
/// dotýkají. Oprav podmínku práce v group tak, aby posbírala všechna dosažitelná shodná políčka místo
/// zastavení po prvním výsledku.
///
/// Navzdory předčasné zprávě „No groups remain“ najdi dvě políčka stejné barvy,
/// která sdílejí hranu; po opravě zadej řádek a sloupec některého políčka z této skupiny.

List<int[]> group(int row, int col) {
    var result = new ArrayList<int[]>();
    if (row < 0 || row >= 5 || col < 0 || col >= 5 || board[row][col] == 0) {
        return result;
    }
    boolean[][] seen = new boolean[5][5];
    var queue = new ArrayDeque<int[]>();
    queue.add(new int[]{row, col});
    seen[row][col] = true;
    while (!queue.isEmpty()) {
        int[] p = queue.remove();
        result.add(p);
        for (int[] d : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int r = p[0] + d[0];
            int c = p[1] + d[1];
            if (r >= 0 && r < 5 && c >= 0 && c < 5 && !seen[r][c] && board[r][c] == board[row][col]) {
                seen[r][c] = true;
                queue.add(new int[]{r, c});
            }
        }
    }
    return result;
}

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// Odděl tři připravené role: queue uchovává objevená políčka čekající na návštěvu; result již
/// nasbíraná políčka; seen pamatuje už zařazená políčka, aby je dvě cesty nepřidaly dvakrát.
/// queue.remove() vyjme čekající dvojici souřadnic, zatímco result.add přidá toto políčko do
/// odpovědi. Odpověď obsahující jedno políčko neznamená, že čekající práce je prázdná. Počáteční
/// zpráva „No groups remain“ pochází z herního hledání skupin před jakýmkoli tahem; je to záměrný
/// projev chyby, nikoli problém console input (vstupu do konzole). Oprav source (zdrojový text) a
/// spusť jej znovu místo pokusu odstranit chybu pouhým restartem kola.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[][] board;
int score;
String outcome;

void reset() {
    board = new int[5][5];
    var random = new Random();
    for (int[] row : board) {
        for (int c = 0; c < 5; c++) {
            row[c] = random.nextInt(1, 4);
        }
    }
    score = 0;
    outcome = "";
    assess();
}

boolean hasMove() {
    for (int r = 0; r < 5; r++) {
        for (int c = 0; c < 5; c++) {
            if (group(r, c).size() >= 2) {
                return true;
            }
        }
    }
    return false;
}

void assess() {
    boolean empty = true;
    for (int[] row : board) {
        for (int n : row) {
            empty &= n == 0;
        }
    }
    if (empty) {
        outcome = "Board clear: you win!";
    } else {
        if (!hasMove()) {
            outcome = "No groups remain. Final score: " + score;
        }
    }
}

void collapse() {
    int[][] next = new int[5][5];
    int writeColumn = 0;
    for (int c = 0; c < 5; c++) {
        int writeRow = 4;
        for (int r = 4; r >= 0; r--) {
            if (board[r][c] != 0) {
                next[writeRow--][writeColumn] = board[r][c];
            }
        }
        if (writeRow != 4) {
            writeColumn++;
        }
    }
    board = next;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 2);
    var cells = group(Integer.parseInt(p[0]) - 1, Integer.parseInt(p[1]) - 1);
    require(cells.size() >= 2);
    for (int[] cell : cells) {
        board[cell[0]][cell[1]] = 0;
    }
    score += cells.size() * (cells.size() - 1);
    collapse();
    assess();
}

void display() {
    IO.println("Colours1/2/3, 0 empty; columns1-5. Score " + score);
    for (int r = 0; r < 5; r++) {
        IO.println((r + 1) + " " + Arrays.toString(board[r]));
    }
}

void main() {
    reset();
    IO.println("Same Game");
    IO.println("Enter a row column (1-5) in a group of at least two equal colours. Blocks fall and columns close. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter a row column (1-5) in a group of at least two equal colours. Blocks fall and columns close. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

// </editor-fold>
