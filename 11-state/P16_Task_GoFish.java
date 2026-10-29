/// # Task · GoFish
/// Lesson: 11.3 Rejection must preserve state — 03-invariants.html
///
/// Ask for ranks, draw when you miss, and collect four-card books. A completed book should remove its
/// cards from circulation.
///
/// **Your mission:** A book leaves a spare card in the hand. Repair collect() so turning a complete rank
/// into a book preserves the total number of cards.
///
/// Run the program and request a rank shown in your hand by typing its number from 2 to 14 and
/// pressing Enter.

void collect(int who) {
    for (int rank = 2; rank <= 14; rank++) {
        if (Collections.frequency(hands.get(who), rank) == 4) {
            for (int n = 0; n < 3; n++) {
                hands.get(who).remove(Integer.valueOf(rank));
            }
            books[who]++;
        }
    }
}

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Count what a book represents
///
/// A book is one score entry but represents four removed cards. Compare physical cards in both hands
/// and the deck with the cards represented by books before and after collection. For a neutral
/// example, replacing six loose tokens by two sealed packs of three still represents six tokens. Pack
/// counts need the weight of their contents. What quantity does the game need to preserve? The
/// supplied `remove(Integer.valueOf(rank))` removes a card value, whereas removing an int index would
/// select a position.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Prepare an opening deal with two nearly complete books, accounting for all remaining cards in the
/// deck. Challenge a partner to predict which book appears first.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
List<List<Integer>> hands;
ArrayDeque<Integer> stock;
int[] books;
int player;
String outcome;
String message;
Random random = new Random();

void refill(int who) {
    if (hands.get(who).isEmpty() && !stock.isEmpty()) {
        hands.get(who).add(stock.remove());
    }
    collect(who);
}

void finish() {
    if (books[0] + books[1] == 13) {
        outcome = books[0] == books[1] ? "Draw" : books[0] > books[1] ? "You win" : "Bot wins";
    }
}

void reset() {
    var cards = new ArrayList<Integer>();
    for (int rank = 2; rank <= 14; rank++) {
        for (int i = 0; i < 4; i++) {
            cards.add(rank);
        }
    }
    Collections.shuffle(cards);
    stock = new ArrayDeque<>(cards);
    hands = List.of(new ArrayList<>(), new ArrayList<>());
    books = new int[2];
    player = 0;
    outcome = message = "";
    for (int n = 0; n < 7; n++) {
        for (int who = 0; who < 2; who++) {
            hands.get(who).add(stock.remove());
        }
    }
    collect(0);
    collect(1);
    refill(0);
}

void ask(int who, int rank) {
    require(hands.get(who).contains(rank));
    int received = 0;
    while (hands.get(1 - who).remove(Integer.valueOf(rank))) {
        hands.get(who).add(rank);
        received++;
    }
    boolean again = received > 0;
    message = (who == 0 ? "You" : "Bot") + " asked for " + rank + " and received " + received + ".";
    if (!again && !stock.isEmpty()) {
        int card = stock.remove();
        hands.get(who).add(card);
        again = card == rank;
    }
    collect(who);
    finish();
    player = again ? who : 1 - who;
    refill(player);
    if (hands.get(player).isEmpty()) {
        player = 1 - player;
    }
}

void command(String input) {
    require(outcome.isEmpty());
    require(player == 0);
    ask(0, Integer.parseInt(input));
    while (outcome.isEmpty() && player == 1) {
        refill(1);
        if (hands.get(1).isEmpty()) {
            player = 0;
            break;
        }
        var hand = hands.get(1);
        ask(1, hand.get(random.nextInt(hand.size())));
    }
    refill(0);
    finish();
}

void display() {
    IO.println(message);
    IO.println("Your ranks: " + hands.get(0) + " (2-14, Ace14)");
    IO.println("Books: you " + books[0] + ", bot " + books[1] + "; stock " + stock.size() + ", bot cards " + hands.get(1).size());
}

void main() {
    reset();
    IO.println("Go Fish");
    IO.println("Ask the other player for a rank you already hold. Four equal ranks form a book; most books wins.");
    IO.println("Ask for a rank you hold: 2-14 (Ace14). Four equal ranks make a book; thirteen books ends play. /r restarts; /q quits.");
    IO.println("Example: enter 7 only if your displayed hand contains a seven. A failed request draws one card if available.");
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
            IO.println("Invalid move. " + "Ask for a rank you hold: 2-14 (Ace14). Four equal ranks make a book; thirteen books ends play. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
