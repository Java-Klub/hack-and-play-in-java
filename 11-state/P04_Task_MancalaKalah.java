/// # Úkol · MancalaKalah
/// Lekce: 11.1 Objekt si pamatuje změnu — 01-state.html
///
/// **Oprav první tah nového kola.** Zahraj jamku příkazem `1` a potom zadej `r`. Jamky se obnoví,
/// ale výchozí program chybně ponechá na tahu druhého hráče.
///
/// **Úkol:** Oprav `reset` níže tak, aby vždy začínal první hráč. Zachovej obnovení kamenů i
/// vymazání starého výsledku. Měnit se má údaj o hráči, který používá skutečná hra.
///
/// **Výsledek:** Po tomto pokusu i po libovolně rozehraném kole ukáže restart Player 1. Nová
/// deska má opět čtyři kameny v každé malé jamce a prázdné zásobní jamky.

void reset() {
    pits = new int[14];
    Arrays.fill(pits, 4);
    pits[6] = pits[13] = 0;
    int player = 0;
    outcome = "";
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[] pits;
int player;
String outcome;

int side(int who) {
    int n = 0;
    for (int i = who * 7; i < who * 7 + 6; i++) {
        n += pits[i];
    }
    return n;
}

void command(String input) {
    require(outcome.isEmpty());
    int chosen = Integer.parseInt(input) - 1;
    require(chosen >= 0 && chosen < 6);
    int at = player * 7 + chosen;
    require(pits[at] > 0);
    int stones = pits[at];
    pits[at] = 0;
    while (stones > 0) {
        at = (at + 1) % 14;
        if (at == (player == 0 ? 13 : 6)) {
            continue;
        }
        pits[at]++;
        stones--;
    }
    int store = player == 0 ? 6 : 13;
    if (at >= player * 7 && at < player * 7 + 6 && pits[at] == 1 && pits[12 - at] > 0) {
        pits[store] += pits[12 - at] + 1;
        pits[12 - at] = pits[at] = 0;
    }
    if (side(0) == 0 || side(1) == 0) {
        for (int who = 0; who < 2; who++) {
            for (int i = who * 7; i < who * 7 + 6; i++) {
                pits[who == 0 ? 6 : 13] += pits[i];
                pits[i] = 0;
            }
        }
        outcome = pits[6] == pits[13] ? "Draw" : "Player " + (pits[6] > pits[13] ? 1 : 2) + " wins";
    } else {
        if (at != store) {
            player = 1 - player;
        }
    }
}

void display() {
    IO.println("Player " + (player + 1) + " chooses own pit 1-6 (left-to-right in its list).");
    IO.println("P1 " + Arrays.toString(Arrays.copyOfRange(pits, 0, 6)) + " store " + pits[6]);
    IO.println("P2 " + Arrays.toString(Arrays.copyOfRange(pits, 7, 13)) + " store " + pits[13]);
    IO.println("Opposite pairs: P1 pit1/P2 pit6 ... P1 pit6/P2 pit1.");
}

void main() {
    reset();
    IO.println("Mancala / Kalah");
    IO.println("Collect more stones in your store. Sow one by one, skip the enemy store, and play again when you finish in your store.");
    IO.println("Kalah: enter own pit 1-6. Sow forward; skip enemy store; finish in own store plays again. /r restarts; /q quits.");
    IO.println("Example: 3 selects your third pit. The two printed pit lists each run from their own 1 to 6.");
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
            IO.println("Invalid move. " + "Kalah: enter own pit 1-6. Sow forward; skip enemy store; finish in own store plays again. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
