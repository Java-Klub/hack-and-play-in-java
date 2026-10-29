/// # Solved task · BlackjackTwentyOne
/// Lesson: 4.2 Choose what happens — 02-branches.html
///
/// Original assignment (this version is complete):
///
/// Try to finish closer to 21 than the dealer without going over. You can take another card or stand, and
/// aces may count as one or eleven.
///
/// **Your mission · Write the final verdict:** The known 20-versus-19 opening incorrectly awards the
/// dealer a win. Complete
/// compareTotals so bust, player win, draw and dealer win are separated in the right priority.
///
/// Run the program and enter s to stand on the training total 20, then compare the reported
/// result against the dealer’s 19.

String compareTotals(int human, int house) {
    if (human > 21) {
        return "You bust";
    } else if (house > 21 || human > house) {
        return "You win";
    } else if (human == house) {
        return "Draw";
    } else {
        return "Dealer wins";
    }
}

// <editor-fold defaultstate="expanded" desc="For this game">
/// Return one of these exact English result strings; the prepared caller prints it:
///
/// - Player above 21: `You bust`, including when both totals exceed 21.
/// - Otherwise, dealer above 21 or player above dealer: `You win`.
/// - Otherwise, equal totals: `Draw`.
/// - Otherwise: `Dealer wins`.
///
/// These are priorities, not four independent announcements. For example, player 22 versus dealer 25
/// is still `You bust`; comparing the larger number first would get this wrong. Predict results for
/// 20 versus 22, 18 versus 18, and 18 versus 20 before writing branches. Keep the original
/// capitalization and spaces: a returned message is part of this method's contract. Do not move card
/// drawing or printing into compareTotals.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Design a short alternative tie rule and tell a friend before playing. Change only the equal-total
/// result, keeping bust priority intact. Write down two totals on which the new rule differs and one
/// on which both versions must agree.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
ArrayDeque<Integer> deck;
List<Integer> hand;
List<Integer> dealer;
String outcome;

void reset() {
    var cards = new ArrayList<Integer>();
    for (int suit = 0; suit < 4; suit++) {
        for (int rank = 1; rank <= 13; rank++) {
            cards.add(rank);
        }
    }
    cards.remove(Integer.valueOf(10));
    cards.remove(Integer.valueOf(10));
    cards.remove(Integer.valueOf(10));
    cards.remove(Integer.valueOf(9));
    Collections.shuffle(cards);
    deck = new ArrayDeque<>(cards);
    hand = new ArrayList<>(List.of(10, 10));
    dealer = new ArrayList<>(List.of(10, 9));
    outcome = "";
}

int value(List<Integer> cards) {
    int sum = 0;
    int aces = 0;
    for (int rank : cards) {
        sum += rank == 1 ? 11 : Math.min(rank, 10);
        if (rank == 1) {
            aces++;
        }
    }
    while (sum > 21 && aces > 0) {
        sum -= 10;
        aces--;
    }
    return sum;
}

void finish() {
    while (value(dealer) < 17) {
        dealer.add(deck.remove());
    }
    int h = value(hand);
    int d = value(dealer);
    outcome = compareTotals(h, d);
}

void command(String input) {
    require(outcome.isEmpty());
    if (input.equalsIgnoreCase("h")) {
        hand.add(deck.remove());
        if (value(hand) > 21) {
            outcome = "You bust";
        }
    } else {
        require(input.equalsIgnoreCase("s"));
        finish();
    }
}

void display() {
    IO.println("Ranks: 1=Ace (1/11), 11-13=faces (10). Dealer stands on soft 17.");
    IO.println("Your hand " + hand + " = " + value(hand));
    IO.println(outcome.isEmpty() ? "Dealer: " + dealer.getFirst() + " + hidden" : "Dealer: " + dealer + " = " + value(dealer));
}

void main() {
    reset();
    IO.println("Blackjack / Twenty-One");
    IO.println("h hits, s stands. One hand at a time. /r deals again; /q quits.");
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
            IO.println("Invalid move or command. h hits, s stands. One hand at a time. /r deals again; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

// </editor-fold>
