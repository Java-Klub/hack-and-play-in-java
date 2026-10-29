/// # Solved task · FourColourMap
/// Lesson: 8.3 Ask the neighbouring cells — 03-neighbours.html
///
/// Original assignment (this version is complete):
///
/// **Redraw the map and catch the judge.** Find the `map` data and replace its rows with the
/// single row `"AABCCDEE"`. Run the game and enter `B 2`, `C 1`, `D 1`, `E 2`. Only neighbouring
/// C–D now share a colour, but the original judge reports three conflicts.
///
/// **Your task:** Make neighbour detection read the actual A–E map rows. Redrawing the map should
/// no longer require updating a separate border list. Sharing a side counts; touching at a corner
/// does not.
///
/// **Result:** The test map with these colours has one conflict. Even a long shared border counts
/// as one conflicting pair. Inspecting neighbours must preserve the player’s colours and work for
/// other rectangular maps too.

boolean touches(int first, int second) {
    if (first == second) {
        return false;
    }
    char a = (char) ('A' + first);
    char b = (char) ('A' + second);
    for (int row = 0; row < map.length; row++) {
        for (int column = 0; column < map[row].length(); column++) {
            char here = map[row].charAt(column);
            if (column + 1 < map[row].length()) {
                char right = map[row].charAt(column + 1);
                if ((here == a && right == b) || (here == b && right == a)) {
                    return true;
                }
            }
            if (row + 1 < map.length) {
                char below = map[row + 1].charAt(column);
                if ((here == a && below == b) || (here == b && below == a)) {
                    return true;
                }
            }
        }
    }
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
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
