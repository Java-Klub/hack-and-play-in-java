/// # Vyřešený úkol · WordSearch
/// Lekce: 10.2 Pamatuj si první návštěvu — 02-sets.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Hledej CAT, DOG a TIGER v mřížce písmen výběrem obou konců slova. Výběr může vést vodorovně, svisle
/// nebo úhlopříčně, takže směr je součástí hádanky.
///
/// **Tvoje mise:** Dovol vybrat každé cílové slovo od kteréhokoli konce. CAT i TAC zapíšou stejný cíl
/// CAT, takže obrácený výběr už nalezeného slova nepřidá další nález. Zachovej ověření přímé cesty a
/// odmítej cesty bez cílového slova. Nejdřív hrou zjisti, kde dnes záleží na směru.
///
/// V čerstvých kolech porovnej 1 1 1 3 a 1 3 1 1. Oba výběry navštíví stejné buňky, hra ale zatím
/// přijme jen jeden směr.

String targetName(String selected) {
    String reversed = "";
    for (int index = selected.length() - 1; index >= 0; index--) {
        reversed += selected.charAt(index);
    }
    if (Set.of("CAT", "DOG", "TIGER").contains(reversed)) {
        return reversed;
    }
    return selected;
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String read(int r, int c, int nr, int nc) {
    require(r >= 0 && r < 5 && c >= 0 && c < 5 && nr >= 0 && nr < 5 && nc >= 0 && nc < 5);
    int dr = Integer.signum(nr - r);
    int dc = Integer.signum(nc - c);
    int n = Math.max(Math.abs(nr - r), Math.abs(nc - c));
    require(n > 0 && (r == nr || c == nc || Math.abs(nr - r) == Math.abs(nc - c)));
    String word = "";
    for (int i = 0; i <= n; i++) {
        word += grid[r + i * dr].charAt(c + i * dc);
    }
    return word;
}

String[] grid = {"CATER", "ADOGX", "TIGER", "XXXXX", "XXXXX"};
Set<String> found;
String outcome;

void reset() {
    found = new LinkedHashSet<>();
    outcome = "";
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 4);
    String word = targetName(read(Integer.parseInt(p[0]) - 1, Integer.parseInt(p[1]) - 1, Integer.parseInt(p[2]) - 1, Integer.parseInt(p[3]) - 1));
    require(Set.of("CAT", "DOG", "TIGER").contains(word) && !found.contains(word));
    found.add(word);
    if (found.size() == 3) {
        outcome = "All words found!";
    }
}

void display() {
    IO.println("Columns 1-5. Targets CAT DOG TIGER; found " + found);
    for (int r = 0; r < 5; r++) {
        IO.println((r + 1) + " " + grid[r]);
    }
}

void main() {
    reset();
    IO.println("Word Search");
    IO.println("Enter startRow startCol endRow endCol (1-5) along a straight word. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter startRow startCol endRow endCol (1-5) along a straight word. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

// </editor-fold>
