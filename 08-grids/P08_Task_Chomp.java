/// # Solved task · Chomp
/// Lesson: 8.2 Check the boundary first — 02-bounds.html
///
/// Original assignment (this version is complete):
///
/// Two players take bites from one chocolate bar. Each bite removes a rectangle towards the lower right,
/// but eating the poisoned top-left piece loses the game.
///
/// **Your mission · Protect the last chocolate row:** The visible fourth row cannot be bitten. Repair the
/// range guard in bite while
/// continuing to reject chocolate that is outside the bar or already eaten.
///
/// Run the program and enter 4 5 to bite the visible bottom-right piece, then try the same
/// coordinates again after it is gone.

boolean bite(int row, int column) {
    if (winner != 0 || row < 1 || row > 4 || column < 1 || column > widths[row - 1]) {
        return false;
    }
    for (int i = row - 1; i < widths.length; i++) {
        widths[i] = Math.min(widths[i], column - 1);
    }
    if (row == 1 && column == 1) {
        winner = 3 - player;
    } else {
        player = 3 - player;
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Change the initial row widths to another small chocolate shape using the same four-row layout.
/// Give a friend a position where biting a tempting corner leaves poison for the other player. Keep
/// the poisoned top-left rule and test a bite just beyond each row’s remaining width.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[] widths = {5, 5, 5, 5};
int player = 1;
int winner;

void reset() {
    Arrays.fill(widths, 5);
    player = 1;
    winner = 0;
}

void main() {
    IO.println("CHOMP: two players share a chocolate bar.");
    IO.println("A bite removes its cell and the rectangle below and right.");
    IO.println("P at row 1, column 1 is poisoned. Eating P loses.");
    boolean showState = true;
    while (true) {
        if (showState) {
            IO.println("    1 2 3 4 5");
            for (int row = 0; row < 4; row++) {
                String line = (row + 1) + " : ";
                for (int column = 0; column < 5; column++) {
                    line += column >= widths[row] ? ". " : row == 0 && column == 0 ? "P " : "# ";
                }
                IO.println(line);
            }
            if (winner != 0) {
                IO.println("Player " + winner + " wins!");
            }
            showState = false;
        }
        String input = IO.readln(winner == 0 ? "Player " + player + ": row column, r restart, q quit: " : "Enter or /r: new round; /q: quit: ");
        if (input == null || (input.trim().equalsIgnoreCase("q") || input.trim().equalsIgnoreCase("/q"))) {
            return;
        }
        input = input.trim();
        if ((input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r") || (winner != 0 && input.isBlank()))) {
            reset();
            showState = true;
            continue;
        }
        if (winner != 0) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            String[] parts = input.split("\\s+");
            if (parts.length == 2 && bite(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]))) {
                showState = true;
            } else {
                IO.println("Illegal bite. Enter row and column of remaining chocolate.");
            }
        } catch (NumberFormatException exception) {
            IO.println("Enter two numbers, r or q.");
        }
    }
}

// </editor-fold>
