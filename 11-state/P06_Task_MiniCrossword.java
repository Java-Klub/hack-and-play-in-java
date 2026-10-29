/// # Vyřešený úkol · MiniCrossword
/// Lekce: 11.1 Objekt si pamatuje změnu — 01-state.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Vyřeš malou křížovku postupně po nápovědách a klidně odpověď smaž, když ji chceš přehodnotit. Oslava
/// má počkat na všechny odpovědi.
///
/// **Tvoje mise:** Počítadlo přičítá staré výsledky k novým a může slavit už po třech odpovědích. Oprav
/// nové spočítání uvnitř command().
///
/// Jako první odpověď napiš 1 CAT a potvrď Enter, potom si přečti zbývající
/// očíslované nápovědy.

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.toUpperCase(Locale.ROOT).split("\\s+");
    require(p.length == 2);
    boolean erase = p[0].equals("ERASE");
    int n = Integer.parseInt(erase ? p[1] : p[0]) - 1;
    require(n >= 0 && n < 6);
    if (erase) {
        solved[n] = false;
    } else {
        require(p[1].equals(answers[n]));
        solved[n] = true;
    }
    int count = 0;
    for (boolean value : solved) {
        if (value) {
            count++;
        }
    }
    if (count == 6) {
        outcome = "All clues solved!";
    }
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String[] answers = {"CAT", "ARE", "TEN", "CAT", "ARE", "TEN"};
String[] clues = {"Across row1: pet that meows", "Across row2: plural of is", "Across row3: number after nine", "Down col1: pet that meows", "Down col2: plural of is", "Down col3: number after nine"};
boolean[] solved;
String outcome;

void reset() {
    solved = new boolean[6];
    outcome = "";
}

char[][] grid() {
    char[][] b = new char[3][3];
    for (char[] row : b) {
        Arrays.fill(row, '.');
    }
    for (int n = 0; n < 6; n++) {
        if (solved[n]) {
            for (int i = 0; i < 3; i++) {
                b[n < 3 ? n : i][n < 3 ? i : n - 3] = answers[n].charAt(i);
            }
        }
    }
    return b;
}

void display() {
    for (char[] row : grid()) {
        IO.println(new String(row));
    }
    for (int i = 0; i < 6; i++) {
        IO.println((i + 1) + " " + clues[i] + (solved[i] ? " [done]" : ""));
    }
}

void main() {
    reset();
    IO.println("Mini Crossword");
    IO.println("Solve six clues crossing in a tiny three-by-three crossword. A correct answer reveals its shared letters.");
    IO.println("Enter clueNumber answer, or erase clueNumber. A tiny authored 3x3 crossword. /r restarts; /q quits.");
    IO.println("Example: 1 CAT answers clue 1. erase 1 clears that answer so you can inspect the crossing again.");
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
            IO.println("Invalid move. " + "Enter clueNumber answer, or erase clueNumber. A tiny authored 3x3 crossword. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
