/// # Solved task · Pig
/// Lesson: 11.1 An object remembers a change — 01-state.html
///
/// Original assignment (this version is complete):
///
/// Two players race to bank 50 points. Each extra roll can increase the current turn’s reward, but
/// rolling one loses those unbanked points and passes the die.
///
/// **Your mission · Rebuild a risky turn:** The die is rolling but neither branch updates the turn.
/// Complete roll: a 1
/// clears turnTotal and switches player; 2..6 add die to turnTotal and keep the player. Preserve the
/// supplied invalid-die and finished-game guard.
///
/// Run the program and enter d to roll or b to bank your turn; watch what happens when a 1 or a
/// 6 appears.

boolean roll(int die) {
    if (winner != -1 || die < 1 || die > 6) {
        return false;
    }
    if (die == 1) {
        turnTotal = 0;
        player = 1 - player;
    } else {
        turnTotal += die;
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Agree on a house rule before playing: perhaps a different die face loses the unbanked turn. Change
/// that rule deliberately and test both its losing and scoring branch. Compare whether players bank
/// earlier. The standard check continues to expect that one is the losing face.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[] scores = {0, 0};
int player;
int turnTotal;
int winner = -1;

void reset() {
    scores[0] = 0;
    scores[1] = 0;
    player = 0;
    turnTotal = 0;
    winner = -1;
}

boolean bank() {
    if (winner != -1 || turnTotal == 0) {
        return false;
    }
    scores[player] += turnTotal;
    turnTotal = 0;
    if (scores[player] >= 50) {
        winner = player;
    } else {
        player = 1 - player;
    }
    return true;
}

void main() {
    reset();
    var random = new Random();
    IO.println("PIG: two players race to 50 banked points.");
    IO.println("d rolls, b banks. A 1 should lose this turn's points; banking keeps them and passes play. /r restarts; /q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            IO.println("Scores: P1 " + scores[0] + ", P2 " + scores[1] + "; turn total " + turnTotal);
            if (winner != -1) IO.println("Player " + (winner + 1) + " wins!");
            showState = false;
        }
        String input = IO.readln(winner == -1 ? "Player " + (player + 1) + ": d roll, b bank, /r, /q: " : "Enter or /r: new round; /q: quit: ");
        if (input == null) return;
        input = input.strip().toLowerCase(Locale.ROOT);
        if (input.equals("q") || input.equals("/q")) return;
        if (input.equals("r") || input.equals("/r") || (winner != -1 && input.isEmpty())) {
            reset(); showState = true; continue;
        }
        if (winner != -1) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        if (input.equals("d")) {
            int before = player;
            int die = random.nextInt(1, 7);
            roll(die);
            IO.println("Player " + (before + 1) + " rolled " + die + ". " + (player != before ? "Turn passed; the turn total is now " + turnTotal + "." : "The same player continues."));
            showState = true;
        } else if (input.equals("b") && bank()) {
            IO.println("Turn points banked.");
            showState = true;
        } else {
            IO.println("Use d to roll, b to bank a positive turn total, /r or /q.");
        }
    }
}

// </editor-fold>
