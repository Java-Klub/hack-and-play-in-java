/// # Úkol · ConnectFour
/// Lekce: 11.2 Každý robot má vlastní baterii — 02-instances.html
///
/// Pouštěj kameny do sloupců a spoj čtyři dřív než kamarád. Odveta má začít s čistou deskou i novým
/// kolem.
///
/// **Tvoje mise:** Nech odvety střídat začínajícího hráče: první kolo začne X, další O, potom znovu X.
/// Nezáleží na posledním tahu ani vítězi. Každá odveta dál vyčistí desku, vítěze a počet tahů. Podle
/// zobrazeného hráče a Moves vyšetři, které údaje patří kolu a které musí přežít několik kol.
///
/// Začni kolo, zadej 4 a potom r. Porovnej dalšího začínajícího hráče s restartem ještě před prvním
/// kamenem. Současná hra vždy začne X.

void reset() {
    for (char[] row : board) {
        Arrays.fill(row, '.');
    }
    player = 'X';
    winner = '.';
    moves = 0;
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
char[][] board = {
".......".toCharArray(),
".......".toCharArray(),
".......".toCharArray(),
".......".toCharArray(),
".......".toCharArray(),
".......".toCharArray()
};
char player = 'X';
char winner = '.';
int moves;

int count(int row, int column, int dr, int dc) {
    char mark = board[row][column];
    int total = 0;
    row += dr;
    column += dc;
    while (row >= 0 && row < 6 && column >= 0 && column < 7 && board[row][column] == mark) {
        total++;
        row += dr;
        column += dc;
    }
    return total;
}

boolean winsAt(int row, int column) {
    int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
    for (int[] direction : directions) {
        int dr = direction[0];
        int dc = direction[1];
        if (1 + count(row, column, dr, dc) + count(row, column, -dr, -dc) >= 4) {
            return true;
        }
    }
    return false;
}

boolean drop(int column) {
    if (winner != '.' || column < 1 || column > 7 || board[0][column - 1] != '.') {
        return false;
    }
    column--;
    int row = 5;
    while (board[row][column] != '.') {
        row--;
    }
    board[row][column] = player;
    moves++;
    if (winsAt(row, column)) {
        winner = player;
    } else {
        if (moves == 42) {
            winner = 'D';
        } else {
            player = player == 'X' ? 'O' : 'X';
        }
    }
    return true;
}

void display() {
    IO.println("1 2 3 4 5 6 7");
    IO.println("Moves: " + moves);
    for (char[] row : board) {
        String line = "";
        for (char mark : row) {
            line += mark + " ";
        }
        IO.println(line);
    }
    if (winner != '.') {
        IO.println(winner == 'D' ? "Draw!" : winner + " wins!");
    }
}

void main() {
    reset();
    IO.println("CONNECT FOUR: two players; four connected pieces win.");
    IO.println("Drop a disc into a column; it falls to the lowest empty cell. Connect four in any direction.");
    IO.println("Example: 4 drops into the middle column. r or /r restarts; q or /q quits.");
    display();
    while (true) {
        String input = IO.readln(winner == '.' ? player + ": column 1-7, r restart, q quit: " : "Enter: replay, r restart, q quit: ");
        if (input == null) {
            return;
        }
        input = input.strip();
        if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("/q")) {
            return;
        }
        if (input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r") || (winner != '.' && input.isEmpty())) {
            reset();
            display();
            continue;
        }
        if (winner != '.') {
            IO.println("That round is over. Press Enter to play again, or q to quit.");
            continue;
        }
        try {
            if (drop(Integer.parseInt(input))) {
                display();
            } else {
                IO.println("Illegal move. Choose a column with space during an unfinished round.");
            }
        } catch (NumberFormatException exception) {
            IO.println("Enter a column number, r or q.");
        }
    }
}
// </editor-fold>
