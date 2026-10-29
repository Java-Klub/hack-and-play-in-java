/// # Vyřešený úkol · Pig
/// Lekce: 11.1 Objekt si pamatuje změnu — 01-state.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Dva hráči závodí o uložení 50 bodů. Každý další hod může zvýšit odměnu současného tahu, ale jednička
/// ztratí neuložené body a předá kostku.
///
/// **Tvoje mise · Sestav riskantní tah:** Kostka padá, ale žádná branch (větev) neaktualizuje tah. Doplň
/// roll: jednička
/// vymaže turnTotal a přepne player; 2..6 přičtou die do turnTotal a hráče ponechají. Zachovej
/// připravený guard (ochrannou podmínku) neplatné kostky a ukončené hry.
///
/// Spusť program a zadávej d pro hod nebo b pro uložení tahu; sleduj, co se stane při jedničce
/// a šestce.

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

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Před hrou se domluvte na domácím pravidle: třeba jiná hodnota kostky zruší neuložený tah. Změň ji
/// záměrně a ověř ztrátovou i bodovací branch (větev). Ukládají hráči body dřív? Standardní kontrola
/// dál očekává jako ztrátovou hodnotu jedničku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
