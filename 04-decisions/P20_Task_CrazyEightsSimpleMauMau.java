/// # Solved task · CrazyEightsSimpleMauMau
/// Lesson: 4.5 Choose a value with switch — 05-switch.html
///
/// Original assignment (this version is complete):
///
/// **Lose your cards before the bot does.** The starter’s referee refuses every card. During
/// play, `p 1` chooses the first card in the displayed hand; this position is not the card’s
/// numeric code.
///
/// **Your task:** Complete `legal` below. An eight is always allowed; other cards must match the
/// top card’s rank or the active suit. Reject invalid card codes. This method receives the
/// internal code 0–51, not a position in the hand.
///
/// **Result:** You can play a legal card and the bot responds. For an eight, add a suit 0–3, for
/// example `p 1 2`; `d` draws only when you have no legal move.

boolean legal(int card) {
    if (card < 0 || card >= 52) return false;
    int value = rank(card);
    return switch (value) {
        case 8 -> true;
        default -> value == rank(top) || card / 13 == suit;
    };
}

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Invent a different wild rank and tell your opponent before dealing. Play two rounds: does saving
/// that rank create a different bluff?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
List<List<Integer>> hands;
ArrayDeque<Integer> stock;
int top;
int suit;
String outcome;
String message;

void reset() {
    var cards = new ArrayList<Integer>();
    for (int i = 0; i < 52; i++) {
        cards.add(i);
    }
    Collections.shuffle(cards);
    stock = new ArrayDeque<>(cards);
    hands = List.of(new ArrayList<>(), new ArrayList<>());
    for (int n = 0; n < 7; n++) {
        for (int who = 0; who < 2; who++) {
            hands.get(who).add(stock.remove());
        }
    }
    do {
        top = stock.remove();
        if (rank(top) == 8) {
            stock.add(top);
        }
    }
    while (rank(top) == 8);
    suit = top / 13;
    outcome = message = "";
}

int rank(int card) {
    return card % 13 + 1;
}

int firstLegal(int who) {
    for (int i = 0; i < hands.get(who).size(); i++) {
        if (legal(hands.get(who).get(i))) {
            return i;
        }
    }
    return -1;
}

void play(int who, int index, int chosenSuit) {
    require(index >= 0 && index < hands.get(who).size());
    int card = hands.get(who).get(index);
    require(legal(card) && chosenSuit >= 0 && chosenSuit < 4);
    hands.get(who).remove(index);
    top = card;
    suit = rank(card) == 8 ? chosenSuit : card / 13;
    if (hands.get(who).isEmpty()) {
        outcome = who == 0 ? "You win" : "Bot wins";
    }
}

void draw(int who) {
    require(firstLegal(who) < 0);
    if (!stock.isEmpty()) {
        hands.get(who).add(stock.remove());
    }
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.toLowerCase(Locale.ROOT).split("\\s+");
    if (input.equalsIgnoreCase("d")) {
        draw(0);
    } else {
        require((p.length == 2 || p.length == 3) && p[0].equals("p"));
        int index = Integer.parseInt(p[1]) - 1;
        require(index >= 0 && index < hands.get(0).size());
        int card = hands.get(0).get(index);
        require(rank(card) != 8 || p.length == 3);
        play(0, index, p.length == 3 ? Integer.parseInt(p[2]) : card / 13);
    }
    if (!outcome.isEmpty()) {
        return;
    }
    if (stock.isEmpty() && firstLegal(0) < 0 && firstLegal(1) < 0) {
        outcome = "Draw: both blocked";
        return;
    }
    int index = firstLegal(1);
    if (index < 0) {
        draw(1);
    } else {
        play(1, index, hands.get(1).get(index) / 13);
    }
    if (outcome.isEmpty() && stock.isEmpty() && firstLegal(0) < 0 && firstLegal(1) < 0) {
        outcome = "Draw: both blocked";
    }
}

String card(int n) {
    return rank(n) + "/" + n / 13;
}

void display() {
    IO.println("Suits 0=clubs 1=diamonds 2=hearts 3=spades. Ace=1, faces11-13.");
    IO.println("Top " + card(top) + "; active suit " + suit + "; bot cards " + hands.get(1).size() + "; stock " + stock.size());
    for (int i = 0; i < hands.get(0).size(); i++) {
        IO.println((i + 1) + ": " + card(hands.get(0).get(i)));
    }
}

void main() {
    reset();
    IO.println("Crazy Eights / Simple Mau-Mau");
    IO.println("Empty your hand against the bot. Match the top card's rank or active suit; an eight can change the suit.");
    IO.println("p index plays a card; an eight needs p index suit(0-3). d draws one only when blocked, then passes. /r restarts; /q quits.");
    IO.println("Example: p 1 plays your first card if legal; p 1 2 plays an eight and chooses hearts. d draws only when blocked.");
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
            IO.println("Invalid move. " + "p index plays a card; an eight needs p index suit(0-3). d draws one only when blocked, then passes. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
