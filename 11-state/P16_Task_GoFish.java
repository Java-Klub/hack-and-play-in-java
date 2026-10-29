/// # Vyřešený úkol · GoFish
/// Lekce: 11.3 Odmítnutí nesmí poškodit stav — 03-invariants.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Žádej soupeře o hodnoty, při neúspěchu lízej a sbírej čtyřkaretní sady. Dokončená sada má své karty
/// stáhnout ze hry.
///
/// **Tvoje mise:** Po vytvoření sady zůstane v ruce přebytečná karta. Oprav collect(), aby převod úplné
/// hodnoty na sadu zachoval celkový počet karet.
///
/// Spusť program a požádej o hodnotu, kterou máš v ruce, zadáním jejího čísla od 2 do 14
/// potvrzeného Enter.

void collect(int who) {
    for (int rank = 2; rank <= 14; rank++) {
        if (Collections.frequency(hands.get(who), rank) == 4) {
            for (int n = 0; n < 4; n++) {
                hands.get(who).remove(Integer.valueOf(rank));
            }
            books[who]++;
        }
    }
}

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Počítej, co sada zastupuje
///
/// Book (hotová sada) je jedna položka skóre, ale zastupuje čtyři odebrané karty. Porovnej fyzické
/// karty v rukou a balíčku spolu s kartami zastoupenými sadami před sesbíráním a po něm. Neutrální
/// příklad: náhrada šesti žetonů dvěma balíčky po třech stále zastupuje šest žetonů. Počet balíčků
/// potřebuje váhu jejich obsahu. Kterou veličinu musí hra zachovat? Připravené
/// `remove(Integer.valueOf(rank))` odebírá hodnotu karty, zatímco odebrání int index (pozice) by
/// vybíralo místo.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
