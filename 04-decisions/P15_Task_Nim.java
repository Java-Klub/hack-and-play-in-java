/// # Task · Nim
/// Lesson: 4.4 Stop an illegal move in time — 04-guards.html
///
/// **Your mission · Build a tool for winning:** Observe the working bot, infer which pile it wants to
/// leave, and turn that idea into a coach for the human player. Typing ? must recommend 3 from seven
/// and 1 from eight. Check also asks the coach about an empty pile: return 0, since no move exists
/// there. A request only advises: it must not take stones or pass the turn. The bot stays unchanged.
///
/// The starter works by its original rules. The check already expects your new goal.
///
/// Play with 1–3, ask ? for advice, r restarts and q quits. The starter already lets you play against
/// the bot; its unfinished coach says 0.

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Give a friend a deliberately weak coach while keeping the bot strong. Explain a pile where the
/// weak advice loses an advantage. Restore the strong coach and compare the same position.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Program · explore as needed">
int advice(int pile) {
    return 0;
}

int botMove() {
    if (stones == 0) {
        return 0;
    }
    int remainder = stones % 4;
    if (remainder == 0) {
        return 1;
    } else {
        return remainder;
    }
}

int stones = 21;
boolean humanTurn = true;
int winner;

void reset() {
    stones = 21;
    humanTurn = true;
    winner = 0;
}

boolean take(int amount) {
    if (winner != 0 || amount < 1 || amount > 3 || amount > stones) {
        return false;
    }
    stones -= amount;
    if (stones == 0) {
        winner = humanTurn ? 1 : 2;
    } else {
        humanTurn = !humanTurn;
    }
    return true;
}

void main() {
    IO.println("NIM: take 1-3 stones. Taking the last stone wins.");
    IO.println("You play first against the bot. ?: coach; r restarts; q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            if (winner == 0 && !humanTurn) {
                int amount = botMove();
                IO.println("Bot takes " + amount + ".");
                take(amount);
            }
            IO.println("Stones: " + stones + " " + "|".repeat(stones));
            if (winner != 0) {
                IO.println(winner == 1 ? "You win!" : "Bot wins!");
            }
            showState = false;
        }
        String input = IO.readln(winner == 0 ? "Take 1-3, r, q: " : "Enter or /r: new round; /q: quit: ");
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
        if (input.equals("?")) {
            IO.println("Coach suggests: " + advice(stones));
            continue;
        }
        try {
            if (take(Integer.parseInt(input))) {
                showState = true;
            } else {
                IO.println("Illegal move. Take 1-3 available stones during your turn.");
            }
        } catch (NumberFormatException exception) {
            IO.println("Enter a number, r or q.");
        }
    }
}
// </editor-fold>
