/// # Úkol · SOS
/// Lekce: 5.4 Kam až dosáhne jméno — 04-local-values.html
///
/// **Tvoje mise · Vystopuj a změň odměnu:** Zaveď combo odměnu: jedna nová SOS má jeden bod, dvě nebo
/// více v jediném tahu mají dvojnásobek počtu nových řad. Staré řady se znovu neodměňují. Vystopuj
/// cestu od vloženého písmena ke skóre; hledání řad a bonusové tahy zachovej.
///
/// Výchozí program funguje podle původních pravidel. Kontrola už očekává tvůj nový cíl.
///
/// Hraj číslem pole a S nebo O. /r restartuje a /q ukončí hru. Standardní začátek dává jeden bod za
/// novou řadu. Zkus jedním tahem vytvořit dvě řady přes střed a uvidíš nové pravidlo.

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Připrav pozici, ve které střed dokončí dvě řady, a potom navrhni jinou combo tabulku. Kamarád má
/// mít možnost odvodit pravidlo ze tří bodovaných příkladů.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Program · prozkoumej podle potřeby">
int gainedPoints(int before, int after) {
    return after - before;
}

char[] board;
int player;
int moves;
int[] scores;
String outcome;

void reset() {
    board = ".........".toCharArray();
    player = 1;
    moves = 0;
    scores = new int[3];
    outcome = "";
}

int triples() {
    int total = 0;
    for (int r = 0; r < 3; r++) {
        for (int c = 0; c < 3; c++) {
            for (int[] d : new int[][]{{0, 1}, {1, 0}, {1, 1}, {1, -1}}) {
                int er = r + 2 * d[0];
                int ec = c + 2 * d[1];
                if (er < 0 || er >= 3 || ec < 0 || ec >= 3) {
                    continue;
                }
                if (board[r * 3 + c] == 'S' && board[(r + d[0]) * 3 + c + d[1]] == 'O' && board[er * 3 + ec] == 'S') {
                    total++;
                }
            }
        }
    }
    return total;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.toUpperCase(Locale.ROOT).split("\\s+");
    require(p.length == 2 && p[1].matches("[SO]"));
    int cell = Integer.parseInt(p[0]) - 1;
    require(cell >= 0 && cell < 9 && board[cell] == '.');
    int before = triples();
    board[cell] = p[1].charAt(0);
    moves++;
    int gained = gainedPoints(before, triples());
    scores[player] += gained;
    if (gained == 0) {
        player = 3 - player;
    }
    if (moves == 9) {
        outcome = scores[1] == scores[2] ? "Draw" : "Player " + (scores[1] > scores[2] ? 1 : 2) + " wins";
    }
}

void display() {
    IO.println("Player " + player + "; score " + scores[1] + ":" + scores[2]);
    for (int r = 0; r < 3; r++) {
        String line = "";
        for (int c = 0; c < 3; c++) {
            line += (board[r * 3 + c] == '.' ? "" + (r * 3 + c + 1) : "" + board[r * 3 + c]) + " ";
        }
        IO.println(line);
    }
}

void main() {
    reset();
    IO.println("SOS");
    IO.println("Enter cell 1-9 and S or O, e.g. 5 O. Each new SOS scores. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter cell 1-9 and S or O, e.g. 5 O. Each new SOS scores. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
