/// # Vyřešený úkol · WordLadder
/// Lekce: 12.1 Rozhodni, které hodnoty dávají smysl — 01-validation.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Žebřík po jednom slově funguje. Staň se plánovačem: pošli několik promyšlených zastávek najednou a
/// zajisti, aby tě chybný plán nenechal v polovině.
///
/// **Tvoje mise:** Přijmi trasu slov oddělených mezerami i samotné slovo. Každá zastávka musí být ve
/// slovníku a lišit se od předchozí právě jedním písmenem; okolní mezery a velikost písmen ignoruj.
/// Celou trasu ověř před změnou hry. Chyba pozdější zastávky nechá původní slovo i history (cestu) beze
/// změny. WARM smí být jen poslední zastávka; počítají se jednotlivé tahy, ne odeslání.
///
/// Použij vypsaný slovník k plánování. Výchozí hra přijme samostatně CORD a pak CARD. Nový plánovač
/// musí přijmout cord card jedním odesláním. Z CARD zkus WARD COLD: celý chybný plán se odmítne. /r
/// začíná znovu, /q končí.

// <editor-fold defaultstate="expanded" desc="Program · prozkoumej pravidla">
String[] words = {"COLD", "CORD", "CARD", "WARD", "WARM", "WORD", "WORM", "GOLD", "SOLD", "TOLD", "HOLD"};
String current;
String outcome;
List<String> history;

void reset() {
    current = "COLD";
    history = new ArrayList<>(List.of(current));
    outcome = "";
}

void command(String input) {
    require(outcome.isEmpty());
    String[] proposed = input.strip().toUpperCase(Locale.ROOT).split("\\s+");
    var route = new ArrayList<>(history);
    String previous = current;
    for (int i = 0; i < proposed.length; i++) {
        String word = proposed[i];
        require(List.of(words).contains(word) && adjacent(previous, word));
        require(!word.equals("WARM") || i == proposed.length - 1);
        route.add(word);
        previous = word;
    }
    history = route;
    current = previous;
    if (current.equals("WARM")) outcome = "Reached WARM in " + (history.size() - 1) + " moves!";
}

boolean adjacent(String a, String b) {
    if (a.length() != b.length()) {
        return false;
    }
    int n = 0;
    for (int i = 0; i < a.length(); i++) {
        if (a.charAt(i) != b.charAt(i)) {
            n++;
        }
    }
    return n == 1;
}

void display() {
    IO.println("Dictionary: " + String.join(" ", words));
    IO.println(String.join(" -> ", history) + "; target WARM");
}

void main() {
    reset();
    IO.println("Word Ladder: transform COLD to WARM using the listed words. /r restarts; /q quits.");
    display();
    while (true) {
        boolean finished = !outcome.isEmpty();
        String input = IO.readln(finished ? "Enter or /r: new round; /q: quit > " : "> ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) return;
        if (input.strip().equalsIgnoreCase("/r") || finished && input.isBlank()) {
            reset();
            display();
            continue;
        }
        if (finished) {
            IO.println("This round is finished. Enter or /r starts a fresh trail; /q quits.");
            continue;
        }
        try {
            command(input.strip());
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid route. Use listed words one letter apart; the trail has not changed.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
