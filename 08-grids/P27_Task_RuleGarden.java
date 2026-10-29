/// # Vyřešený úkol · RuleGarden
/// Lekce: 8.5 Minulost čti, budoucnost zapisuj — 05-generations.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Vypěstuj stránku vzorů z jediného svítícího políčka a malého číslovaného pravidla. Změnou pravidla
/// objevuj trojúhelníky, pruhy nebo zdánlivý chaos.
///
/// **Tvoje mise:** Nová políčka okamžitě ovlivňují další políčka ve stejném řádku a křiví vzor. Oprav
/// nextRow(), aby celá generace četla tentýž starý řádek.
///
/// Program hned ukáže rule (pravidlo) 30 v terminálu. V další výzvě napiš 90 pro porovnání, stiskni
/// Enter pro opakování současného pravidla, nebo q pro konec.

boolean[] nextRow(boolean[] current, int rule) {
    boolean[] next = new boolean[current.length];
    for (int column = 0; column < current.length; column++) {
        int pattern = current[column] ? 2 : 0;
        if (column > 0 && current[column - 1]) {
            pattern += 4;
        }
        if (column + 1 < current.length && current[column + 1]) {
            pattern += 1;
        }
        next[column] = ((rule>>pattern)&1) != 0;
    }
    return next;
}

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// Připravený kód zapisuje left/centre/right (levou/střední/pravou cell) vahami 4/2/1: svítící kraje a
/// zhasnutý střed dávají pattern (vzor) 5. `(rule >> pattern) & 1` čte příslušný rule bit (bit
/// pravidla). Výpočet pravidla zůstává připravený; ty odděluješ old a next. Projdi tři sousední column
/// (sloupce) se stejným nezměněným current.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
List<String> grow(int rule, int width, int rows) {
    if (rule < 0 || rule > 255 || width < 3 || width > 121 || rows < 1 || rows > 60) {
        throw new IllegalArgumentException("Use rule 0..255, width 3..121 and rows 1..60.");
    }
    boolean[] cells = new boolean[width];
    cells[width / 2] = true;
    var result = new ArrayList<String>();
    for (int row = 0; row < rows; row++) {
        var line = new StringBuilder();
        for (boolean cell : cells) {
            line.append(cell ? '#' : '.');
        }
        result.add(line.toString());
        cells = nextRow(cells, rule);
    }
    return result;
}

void displayRule(int rule, int width, int rows) {
    IO.println("Rule " + rule + "; # is on, . is off; cells outside the width stay off.");
    for (String line : grow(rule, width, rows)) {
        IO.println(line);
    }
}

void main() {
    int rule = 30;
    IO.println("RULE GARDEN: watch one starting cell grow into a pattern.");
    IO.println("Each printed line is a new generation. Try 90 or 110 to compare with rule 30.");
    displayRule(rule, 61, 31);
    while (true) {
        String input = IO.readln("New rule 0..255; Enter or r: repeat; q: quit: ");
        if (input == null) {
            return;
        }
        input = input.strip();
        if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("/q")) {
            return;
        }
        try {
            int requested = input.isEmpty() || input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r")
                    ? rule : Integer.parseInt(input);
            if (requested < 0 || requested > 255) {
                throw new IllegalArgumentException("Rule: 0..255.");
            }
            rule = requested;
            displayRule(rule, 61, 31);
        } catch (IllegalArgumentException invalidInput) {
            IO.println("Enter a rule number from 0 to 255, or q to quit.");
        }
    }
}
// </editor-fold>
