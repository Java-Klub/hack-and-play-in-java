/// # Vyřešený úkol · TicTacToe
/// Lekce: 8.2 Nejdřív zkontroluj okraj — 02-bounds.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Zahraj si s kamarádem piškvorky výběrem očíslovaných prázdných políček. Snaž se vytvořit řadu tří a
/// současně blokovat soupeřovu řadu.
///
/// **Tvoje mise · Sestav strážce tahů:** Všechna pole jsou zamčená. Doplň rozhodčího mayPlay: odmítni
/// ukončenou hru, čísla
/// mimo 1..9 a obsazená pole. Platné prázdné pole vrátí true. Připravené účinky play zachovej;
/// odmítnutý tah nesmí změnit desku ani hráče.
///
/// Spusť program, vyber políčko 9 a potom jej zkus znovu; porovnej přijaté prázdné políčko s
/// odmítnutým obsazeným.

boolean mayPlay(int cell) {
    if (winner != '.' || cell < 1 || cell > 9) {
        return false;
    }
    if (board[cell - 1] != '.') {
        return false;
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Provedení přijatého tahu · připravené">
boolean play(int cell) {
    if (!mayPlay(cell)) {
        return false;
    }
    board[cell - 1] = player;
    moves++;
    if (hasLine(player)) {
        winner = player;
    } else if (moves == 9) {
        winner = 'D';
    } else {
        player = player == 'X' ? 'O' : 'X';
    }
    return true;
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Nahraď X a O vlastními dvěma jednoznakovými značkami hráčů. Ponech '.' pro prázdné pole a 'D' pro
/// remízu. Slaď počátečního hráče, reset a přepínání hráčů a odehraj celé kolo s kamarádem. Znovu
/// zkus obsazené pole a vítěznou řadu: vlastní značky nesmějí obejít tvého rozhodčího.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
char[] board = ".........".toCharArray();
char player = 'X';
char winner = '.';
int moves;

void reset() {
    Arrays.fill(board, '.');
    player = 'X';
    winner = '.';
    moves = 0;
}

boolean hasLine(char mark) {
    for (int i = 0; i < 3; i++) {
        if (board[i * 3] == mark && board[i * 3 + 1] == mark && board[i * 3 + 2] == mark) {
            return true;
        }
        if (board[i] == mark && board[i + 3] == mark && board[i + 6] == mark) {
            return true;
        }
    }
    return board[0] == mark && board[4] == mark && board[8] == mark
        || board[2] == mark && board[4] == mark && board[6] == mark;
}

String cell(int index) {
    return board[index] == '.' ? Integer.toString(index + 1) : Character.toString(board[index]);
}

void main() {
    reset();
    IO.println("TIC-TAC-TOE: two players, three in a row wins.");
    boolean showState = true;
    while (true) {
        if (showState) {
            for (int row = 0; row < 3; row++) {
                IO.println(cell(row * 3) + " | " + cell(row * 3 + 1) + " | " + cell(row * 3 + 2));
            }
            if (winner != '.') {
                IO.println(winner == 'D' ? "Draw!" : winner + " wins!");
            }
            showState = false;
        }
        String input = IO.readln(winner == '.' ? player + ": cell 1-9, r restart, q quit: " : "Enter or /r: new round; /q: quit: ");
        if (input == null || (input.trim().equalsIgnoreCase("q") || input.trim().equalsIgnoreCase("/q"))) {
            return;
        }
        input = input.trim();
        if ((input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r") || (winner != '.' && input.isBlank()))) {
            reset();
            showState = true;
            continue;
        }
        if (winner != '.') {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            if (play(Integer.parseInt(input))) {
                showState = true;
            } else {
                IO.println("Illegal move. Choose an empty cell during an unfinished round.");
            }
        } catch (NumberFormatException exception) {
            IO.println("Enter a cell number, r or q.");
        }
    }
}

// </editor-fold>
