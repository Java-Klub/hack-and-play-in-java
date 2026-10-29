/// # Úkol · Nim
/// Lekce: 4.4 Zastav neplatný tah včas — 04-guards.html
///
/// **Tvoje mise · Napiš nástroj k vítězství:** Pozoruj fungujícího bota, odvoď, jakou hromádku chce
/// zanechat, a proměň tuto myšlenku v rádce člověku. Otazník má doporučit 3 ze sedmi a 1 z osmi.
/// Check se rádce ptá také na prázdnou hromádku: vrať 0, protože tam žádný tah neexistuje. Dotaz jen
/// radí: nesmí odebrat kameny ani předat tah. Bota zachovej.
///
/// Výchozí program funguje podle původních pravidel. Kontrola už očekává tvůj nový cíl.
///
/// Hraj pomocí 1–3, otazník žádá radu, r restartuje a q ukončí hru. Výchozí program už dovoluje hrát
/// proti botovi; nedokončený rádce říká 0.

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Dej kamarádovi záměrně slabého rádce a zachovej silného bota. Vysvětli hromádku, kde slabá rada
/// promarní výhodu. Vrať silného rádce a porovnej stejnou pozici.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Program · prozkoumej podle potřeby">
int advice(int pile) {
    return 0;
}

int botMove() {
    if (stones == 0) {
        return 0;
    }
    int remainder = stones % 4;
    if (remainder == 0) {
        return 1;
    } else {
        return remainder;
    }
}

int stones = 21;
boolean humanTurn = true;
int winner;

void reset() {
    stones = 21;
    humanTurn = true;
    winner = 0;
}

boolean take(int amount) {
    if (winner != 0 || amount < 1 || amount > 3 || amount > stones) {
        return false;
    }
    stones -= amount;
    if (stones == 0) {
        winner = humanTurn ? 1 : 2;
    } else {
        humanTurn = !humanTurn;
    }
    return true;
}

void main() {
    IO.println("NIM: take 1-3 stones. Taking the last stone wins.");
    IO.println("You play first against the bot. ?: coach; r restarts; q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            if (winner == 0 && !humanTurn) {
                int amount = botMove();
                IO.println("Bot takes " + amount + ".");
                take(amount);
            }
            IO.println("Stones: " + stones + " " + "|".repeat(stones));
            if (winner != 0) {
                IO.println(winner == 1 ? "You win!" : "Bot wins!");
            }
            showState = false;
        }
        String input = IO.readln(winner == 0 ? "Take 1-3, r, q: " : "Enter or /r: new round; /q: quit: ");
        if (input == null || (input.trim().equalsIgnoreCase("q") || input.trim().equalsIgnoreCase("/q"))) {
            return;
        }
        input = input.trim();
        if ((input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r") || (winner != 0 && input.isBlank()))) {
            reset();
            showState = true;
            continue;
        }
        if (winner != 0) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        if (input.equals("?")) {
            IO.println("Coach suggests: " + advice(stones));
            continue;
        }
        try {
            if (take(Integer.parseInt(input))) {
                showState = true;
            } else {
                IO.println("Illegal move. Take 1-3 available stones during your turn.");
            }
        } catch (NumberFormatException exception) {
            IO.println("Enter a number, r or q.");
        }
    }
}
// </editor-fold>
