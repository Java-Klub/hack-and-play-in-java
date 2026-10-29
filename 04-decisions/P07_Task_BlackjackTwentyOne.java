/// # Úkol · BlackjackTwentyOne
/// Lekce: 4.2 Vyber, co se stane — 02-branches.html
///
/// Zkus skončit blíž k 21 než krupiér bez překročení. Můžeš si vzít další kartu nebo zůstat a esa mohou
/// mít hodnotu jedna či jedenáct.
///
/// **Tvoje mise · Napiš konečný rozsudek:** Známý začátek 20 proti 19 chybně přisoudí výhru krupiérovi.
/// Doplň compareTotals tak,
/// aby ve správném pořadí oddělilo překročení, výhru hráče, remízu a výhru krupiéra.
///
/// Spusť program a zadáním s zůstaň na cvičném součtu 20, potom porovnej oznámený výsledek s
/// krupiérovou 19.

String compareTotals(int human, int house) {
    if (human > 21) {
        return "You bust";
    } else {
        return "Dealer wins";
    }
}

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// Vrať jeden z těchto přesných anglických String (řetězců); připravená volající část jej vypíše:
///
/// - Hráč nad 21: `You bust`, i když 21 překročily oba součty.
/// - Jinak krupiér nad 21 nebo hráč výš než krupiér: `You win`.
/// - Jinak stejné součty: `Draw`.
/// - Jinak: `Dealer wins`.
///
/// Jsou to priority, nikoli čtyři nezávislá oznámení. Například hráč 22 proti krupiérovým 25 stále
/// znamená `You bust`; přednostní porovnání většího čísla by bylo chybné. Před zapsáním branch
/// (větví) předpověz výsledky pro 20 proti 22, 18 proti 18 a 18 proti 20. Zachovej původní velikost
/// písmen i mezery: vrácená zpráva je součástí contract (smlouvy) této method (metody). Dobírání
/// karet ani vypisování do compareTotals nepřesouvej.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Navrhni krátké jiné pravidlo remízy a vysvětli je kamarádovi před hrou. Změň jen výsledek shodných
/// součtů a zachovej přednost překročení 21. Zapiš dva součty, na kterých se pravidla liší, a jeden
/// případ, kde se shodnou.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
