/// # Vyřešený úkol · Chopsticks
/// Lekce: 7.3 Změň skutečné místo — 03-updates.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Ťukni jednou ze svých živých rukou do soupeřovy ruky a přičti jí své prsty. Pět a více prstů ruku
/// vyřadí; vyřazením obou soupeřových rukou vyhraješ.
///
/// **Tvoje mise · Zavři podvodnou mezeru:** Rozhodčí nyní povolí každý úder. Sestav handsAlive: oba index
/// (indexy) musí být 0
/// nebo 1 a obě vybrané ruce musí mít více než nula prstů. Odmítni tah před neplatným lookup
/// (čtením). Method (metoda) pouze povoluje; nesmí měnit prsty ani hráče.
///
/// Spusť program a třikrát zadej 1 1 pro vyřazení ruky, potom zkus čtvrté 1 1 a sleduj, zda
/// neplatný zásah změní hráče.

boolean handsAlive(int own, int opposing) {
    if (own < 0 || own >= 2 || opposing < 0 || opposing >= 2) {
        return false;
    }
    if (hands[player][own] == 0 || hands[1 - player][opposing] == 0) {
        return false;
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Připrav kamarádovi krátkou výzvu: posloupnost vyřazující ruku a potom pokus o neplatný úder.
/// Vystřídejte se jako hráč a rozhodčí. Později jako samostatný domácí experiment změň význam součtu
/// pěti a vysvětli změnu hry.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[][] hands;
int player;
Set<String> seen;
String outcome;

void reset() {
    hands = new int[][]{{1, 1}, {1, 1}};
    player = 0;
    seen = new HashSet<>();
    outcome = "";
    seen.add(key());
}

String key() {
    return Arrays.deepToString(hands) + player;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 2);
    int a = Integer.parseInt(p[0]) - 1;
    int b = Integer.parseInt(p[1]) - 1;
    require(handsAlive(a, b));
    hands[1 - player][b] += hands[player][a];
    if (hands[1 - player][b] >= 5) {
        hands[1 - player][b] = 0;
    }
    if (hands[1 - player][0] + hands[1 - player][1] == 0) {
        outcome = "Player " + (player + 1) + " wins";
    }
    player = 1 - player;
    if (outcome.isEmpty() && !seen.add(key())) {
        outcome = "Draw: repeated state";
    }
}

void display() {
    IO.println("Player " + (player + 1) + "; P1 " + Arrays.toString(hands[0]) + " P2 " + Arrays.toString(hands[1]));
}

void main() {
    reset();
    IO.println("Chopsticks");
    IO.println("Enter own hand 1-2 and opposing hand 1-2. Five or more kills a hand; each turn is one attack. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter own hand 1-2 and opposing hand 1-2. Five or more kills a hand; each turn is one attack. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

// </editor-fold>
