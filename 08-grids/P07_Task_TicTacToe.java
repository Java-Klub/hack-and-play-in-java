/// # Task · TicTacToe
/// Lesson: 8.2 Check the boundary first — 02-bounds.html
///
/// Play tic-tac-toe with a friend by choosing numbered empty squares. Try to make a line of three while
/// blocking the other player’s line.
///
/// **Your mission · Build a move gatekeeper:** All squares are locked. Implement mayPlay as the referee:
/// reject a finished
/// game, cell numbers outside 1..9 and occupied squares. A legal empty square returns true. Keep the
/// prepared play effects; a rejected move must change neither board nor turn.
///
/// Run the program, choose cell 9 and then try it again; compare an accepted empty square with
/// a rejected occupied square.

boolean mayPlay(int cell) {
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Applying an accepted move · prepared">
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

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Replace X and O with your own two single-character player marks. Keep '.' for empty cells and 'D'
/// for a draw. Change the initial player, reset and the player-switch rule consistently, then play a
/// full round with a friend. Try an occupied square and a winning line again: custom marks must not
/// bypass your referee.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
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
