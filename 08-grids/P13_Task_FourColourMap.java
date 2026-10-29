/// # Úkol · FourColourMap
/// Lekce: 8.3 Ptej se okolních buněk — 03-neighbours.html
///
/// **Překresli mapu a nachytej rozhodčího.** Vyhledej data `map` a nahraď jejich řádky jediným
/// řádkem `"AABCCDEE"`. Spusť hru a zadej postupně `B 2`, `C 1`, `D 1`, `E 2`. Stejnou barvu teď
/// sdílejí jen sousedé C–D, ale původní rozhodčí hlásí tři konflikty.
///
/// **Úkol:** Uprav zjišťování sousedství, aby vycházelo ze skutečných řádků mapy A–E. Po změně
/// kresby už nemá být nutné opravovat další seznam hranic. Za sousedství považuj společnou
/// stranu, nikoli pouhý dotyk rohem.
///
/// **Výsledek:** Zkušební mapa s těmito barvami má jediný konflikt. I dlouhá společná hranice se
/// počítá jako jedna konfliktní dvojice. Zkoumání sousedů zachová hráčovy barvy a musí fungovat
/// také pro jiné obdélníkové mapy.

boolean touches(int first, int second) {
    int[][] borders = {{0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 4}, {2, 3}, {2, 4}, {3, 4}};
    for (int[] border : borders) {
        if ((border[0] == first && border[1] == second)
                || (border[0] == second && border[1] == first)) {
            return true;
        }
    }
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int conflicts() {
    int total = 0;
    for (int first = 0; first < colours.length; first++) {
        for (int second = first + 1; second < colours.length; second++) {
            if (colours[first] != 0 && colours[first] == colours[second] && touches(first, second)) {
                total++;
            }
        }
    }
    return total;
}

String[] map = {"AABB", "ACCB", "DDEE"};
int[] colours;
String outcome;

void reset() {
    colours = new int[]{1, 0, 0, 0, 0};
    outcome = "";
}

boolean solved() {
    for (int c : colours) {
        if (c == 0) {
            return false;
        }
    }
    return conflicts() == 0;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.toUpperCase(Locale.ROOT).split("\\s+");
    require(p.length == 2 && p[0].matches("[B-E]"));
    int region = p[0].charAt(0) - 'A';
    int colour = Integer.parseInt(p[1]);
    require(colour >= 0 && colour <= 4);
    colours[region] = colour;
    if (solved()) {
        outcome = "Map coloured correctly!";
    }
}

void display() {
    for (String row : map) {
        IO.println(row);
    }
    IO.println("A is fixed1. Colours1-4, 0 unfilled; corner-only touch is not adjacent.");
    for (int i = 0; i < 5; i++) {
        IO.println((char)('A' + i) + " = " + colours[i]);
    }
    IO.println("Equal-colour shared-edge conflicts: " + conflicts());
}

void main() {
    reset();
    IO.println("Four-Colour Map");
    IO.println("Colour every region so regions sharing an edge differ. Region A stays colour 1; corner contact does not count.");
    IO.println("Enter region B-E and colour1-4; 0 clears. A=1 is fixed. Shared-edge neighbours must differ. /r restarts; /q quits.");
    IO.println("Example: B 2 colours region B with colour 2. B 0 clears it.");
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
            IO.println("Invalid move. " + "Enter region B-E and colour1-4; 0 clears. A=1 is fixed. Shared-edge neighbours must differ. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
