/// # Vyřešený úkol · SetStyleTripleHunt
/// Lekce: 10.1 Batoh nemusí znát délku předem — 01-lists.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// **Najdi přehlédnutou trojici.** Odevzdání správné trojice už funguje. Chybný je jiný úsudek:
/// zda na stole ještě nějaká platná trojice zbývá. Tento výsledek používá příkaz `more` a
/// rozhodování o konci hry.
///
/// **Úkol:** Oprav prohledávání v `hasSet` níže. Musí posoudit všechny kombinace tří různých
/// karet, včetně kombinací s poslední kartou. Připravená `triple` už umí vybranou trojici ověřit.
///
/// **Výsledek:** `more` přidá karty jen tehdy, když na stole žádná trojice není. Check ověří také
/// stůl, kde jediná platná trojice potřebuje poslední kartu; samotné odevzdání běžné trojice tuto
/// opravu neprokáže.

boolean hasSet() {
    for (int a = 0; a < table.size(); a++) {
        for (int b = a + 1; b < table.size(); b++) {
            for (int c = b + 1; c < table.size(); c++) {
                if (triple(table.get(a), table.get(b), table.get(c))) {
                    return true;
                }
            }
        }
    }
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Reading API (rozhraní čtení) stolu
///
/// `table` je připravený List (seznam): `size()` dává aktuální počet karet a `get(index)` jednu
/// přečte bez odebrání. Pozice začínají nulou jako u array (pole). Tři loop index (pozice cyklů)
/// záměrně rostou: a předchází b a b předchází c, takže stejnou kartu nevybereš dvakrát. Předpověz,
/// jak se poslední pozice dostane do trojice; připravený query (dotaz) `triple` dál posuzuje
/// vlastnosti.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
ArrayDeque<Integer> deck;
List<Integer> table;
int score;
String outcome;

void reset() {
    var cards = new ArrayList<Integer>();
    for (int i = 0; i < 81; i++) {
        cards.add(i);
    }
    Collections.shuffle(cards);
    deck = new ArrayDeque<>(cards);
    table = new ArrayList<>();
    score = 0;
    outcome = "";
    deal(12);
}

void deal(int count) {
    while (count-- > 0 && !deck.isEmpty()) {
        table.add(deck.remove());
    }
}

boolean triple(int a, int b, int c) {
    for (int i = 0; i < 4; i++) {
        if ((a % 3 + b % 3 + c % 3) % 3 != 0) {
            return false;
        }
        a /= 3;
        b /= 3;
        c /= 3;
    }
    return true;
}

void command(String input) {
    require(outcome.isEmpty());
    if (input.equalsIgnoreCase("more")) {
        require(!hasSet() && !deck.isEmpty());
        deal(3);
    } else {
        String[] p = input.split("\\s+");
        require(p.length == 3);
        int[] ids = new int[3];
        for (int i = 0; i < 3; i++) {
            ids[i] = Integer.parseInt(p[i]) - 1;
            require(ids[i] >= 0 && ids[i] < table.size());
        }
        Arrays.sort(ids);
        require(ids[0] != ids[1] && ids[1] != ids[2] && triple(table.get(ids[0]), table.get(ids[1]), table.get(ids[2])));
        for (int i = 2; i >= 0; i--) {
            table.remove(ids[i]);
        }
        score++;
        deal(Math.max(0, 12 - table.size()));
    }
    if (deck.isEmpty() && !hasSet()) {
        outcome = "Finished: " + score + " triples found.";
    }
}

String code(int card) {
    String s = "";
    for (int i = 0; i < 4; i++) {
        s = (card % 3) + s;
        card /= 3;
    }
    return s;
}

void display() {
    IO.println("Four attributes, values 0/1/2: each attribute must be all equal or all different.");
    for (int i = 0; i < table.size(); i++) {
        IO.println((i + 1) + ": " + code(table.get(i)));
    }
    IO.println("Sets " + score + "; deck " + deck.size() + "; " + (hasSet() ? "A set exists" : "No set: use more if deck remains"));
}

void main() {
    reset();
    IO.println("Set-style Triple Hunt");
    IO.println("Find three cards whose four attributes are each all equal or all different.");
    IO.println("Enter three card numbers, or more when no set exists. /r restarts; /q quits.");
    IO.println("Example format: 1 2 3 selects three displayed card numbers. Use more only when no triple exists.");
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
            IO.println("Invalid move. " + "Enter three card numbers, or more when no set exists. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
