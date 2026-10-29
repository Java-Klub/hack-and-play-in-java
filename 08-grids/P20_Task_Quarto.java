/// # Vyřešený úkol · Quarto
/// Lekce: 8.4 Vyrob vzor podle souřadnic — 04-patterns.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Soupeř ti vybírá figurku, kterou musíš položit. Vyhraje čtveřice se společnou vlastností, takže i
/// nenápadná diagonála může být past.
///
/// **Tvoje mise:** Řada vyhraje, ale vítězný sloupec či diagonála zůstane bez povšimnutí. Rozšiř won(),
/// aby prohlédlo všechny druhy čtyřpolíčkových linií.
///
/// Vyber číslo nepoužité figurky od 0 do 15 a nech druhého hráče zadat její
/// cílové políčko od 1 do 16.

boolean won() {
    for (int i = 0; i < 4; i++) {
        if (line(i * 4, i * 4 + 1, i * 4 + 2, i * 4 + 3) || line(i, i + 4, i + 8, i + 12)) {
            return true;
        }
    }
    return line(0, 5, 10, 15) || line(3, 6, 9, 12);
}

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Plochá deska má skrytou šířku řádku
///
/// Flat array (ploché pole) může uchovávat řádky za sebou. V jiném obrázku se třemi sloupci začíná
/// řádek jedna za prvními třemi hodnotami; jeho sloupec dva leží na index (pozici) pět. Pohyb o řádek
/// dolů zachová sloupec a přeskočí šířku řádku. Nakresli index tohoto čtverce. `line(a,b,c,d)` už
/// ověří navrženou skupinu: vybíráš pozice, nerozebíráš vlastnosti figurek. Předpověz sloupec i obě
/// úhlopříčky před rozšířením search (hledání).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[] board;
boolean[] used;
int player;
int selected;
int moves;
String outcome;

void reset() {
    board = new int[16];
    Arrays.fill(board, -1);
    used = new boolean[16];
    player = 1;
    selected = -1;
    moves = 0;
    outcome = "";
}

boolean line(int a, int b, int c, int d) {
    if (board[a] < 0 || board[b] < 0 || board[c] < 0 || board[d] < 0) {
        return false;
    }
    int both = board[a] & board[b] & board[c] & board[d];
    int either = board[a] | board[b] | board[c] | board[d];
    return both != 0 || ((~either) & 15) != 0;
}

void command(String input) {
    require(outcome.isEmpty());
    int n = Integer.parseInt(input);
    if (selected < 0) {
        require(n >= 0 && n < 16 && !used[n]);
        selected = n;
        used[n] = true;
        player = 3 - player;
    } else {
        require(n >= 1 && n <= 16 && board[n - 1] < 0);
        board[n - 1] = selected;
        selected = -1;
        moves++;
        if (won()) {
            outcome = "Player " + player + " wins";
        } else {
            if (moves == 16) {
                outcome = "Draw";
            }
        }
    }
}

String piece(int n) {
    return String.format("%4s", Integer.toBinaryString(n)).replace(' ', '0');
}

void display() {
    IO.println("Player " + player + (selected < 0 ? ": choose a piece for your opponent" : ": place piece " + selected + " (" + piece(selected) + ")"));
    IO.println("Attributes: bits = tall, dark, square, hollow (1=yes, 0=no).");
    for (int r = 0; r < 4; r++) {
        IO.println(Arrays.toString(Arrays.copyOfRange(board, r * 4, r * 4 + 4)));
    }
    String available = "Unused pieces: ";
    for (int i = 0; i < 16; i++) {
        if (!used[i]) {
            available += i + "=" + piece(i) + " ";
        }
    }
    IO.println(available);
}

void main() {
    reset();
    IO.println("Quarto");
    IO.println("Give a piece to your opponent, then place the piece they gave you. Four in a line sharing any attribute wins.");
    IO.println("Choose unused piece 0-15, then its receiver places in cell 1-16 (row order). -1 is empty. /r restarts; /q quits.");
    IO.println("Example: choose piece 0; the next player can enter 1 to place it in the first cell. Read the current phase above.");
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
            IO.println("Invalid move. " + "Choose unused piece 0-15, then its receiver places in cell 1-16 (row order). -1 is empty. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
