/// # Vyřešený úkol · LoopBuilder
/// Lekce: 8.1 Z řad vznikne mapa — 01-grids.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// **Spoj čáry se správnými rohy.** Rozhodčí zatím neví, které rohy každá čára spojuje. Kresbu
/// čar lze měnit, ale uzavřený okruh nedokáže správně posoudit.
///
/// **Úkol:** Doplň `vertices` níže: pro číslo hrany vrať čísla jejích dvou koncových rohů.
/// Šestnáct rohů tvoří mřížku 4×4 a číslují se po řádcích zleva doprava od 0 do 15. Hrany 0–11
/// jsou vodorovné, po třech v každém řádku. Hrany 12–23 jsou svislé, po čtyřech mezi sousedními
/// řádky. Číslo mimo 0–23 odmítni.
///
/// **Výsledek:** Hrana 0 má konce `[0, 1]`, hrana 12 má `[0, 4]`; Check ověří všech 24 hran. Při
/// hraní se čísla hran zadávají od 1 do 24, tedy o jednu větší než jejich čísla v kódu.

int[] vertices(int edge) {
    if (edge < 0 || edge >= 24) throw new IllegalArgumentException("Edge: 0..23.");
    if (edge < 12) {
        int row = edge / 3;
        int col = edge % 3;
        return new int[]{row * 4 + col, row * 4 + col + 1};
    }
    int vertical = edge - 12;
    int row = vertical / 4;
    int col = vertical % 4;
    return new int[]{row * 4 + col, (row + 1) * 4 + col};
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
boolean[] edges;
int[] clues = {2, 1, 2, 1, 0, 1, 2, 1, 2};
String outcome;

void reset() {
    edges = new boolean[24];
    outcome = "";
}

boolean oneLoop() {
    int[] degree = new int[16];
    var graph = new ArrayList<List<Integer>>();
    for (int i = 0; i < 16; i++) {
        graph.add(new ArrayList<>());
    }
    for (int i = 0; i < 24; i++) {
        if (edges[i]) {
            int[] v = vertices(i);
            degree[v[0]]++;
            degree[v[1]]++;
            graph.get(v[0]).add(v[1]);
            graph.get(v[1]).add(v[0]);
        }
    }
    int start = -1;
    int total = 0;
    for (int i = 0; i < 16; i++) {
        if (degree[i] != 0 && degree[i] != 2) {
            return false;
        }
        if (degree[i] == 2) {
            start = i;
            total++;
        }
    }
    if (start < 0) {
        return false;
    }
    var seen = new HashSet<Integer>();
    var queue = new ArrayDeque<Integer>();
    queue.add(start);
    seen.add(start);
    while (!queue.isEmpty()) {
        for (int v : graph.get(queue.remove())) {
            if (seen.add(v)) {
                queue.add(v);
            }
        }
    }
    return seen.size() == total;
}

boolean solved() {
    for (int r = 0; r < 3; r++) {
        for (int c = 0; c < 3; c++) {
            int n = 0;
            for (int e : new int[]{r * 3 + c, (r + 1) * 3 + c, 12 + r * 4 + c, 13 + r * 4 + c}) {
                if (edges[e]) {
                    n++;
                }
            }
            if (n != clues[r * 3 + c]) {
                return false;
            }
        }
    }
    return oneLoop();
}

void command(String input) {
    require(outcome.isEmpty());
    int edge = Integer.parseInt(input) - 1;
    require(edge >= 0 && edge < 24);
    edges[edge] = !edges[edge];
    if (solved()) {
        outcome = "One complete loop satisfies every clue!";
    }
}

String label(int edge) {
    return edges[edge] ? "##" : String.format("%02d", edge + 1);
}

void display() {
    for (int r = 0; r < 4; r++) {
        String line = "+";
        for (int c = 0; c < 3; c++) {
            line += "--" + label(r * 3 + c) + "--+";
        }
        IO.println(line);
        if (r < 3) {
            line = label(12 + r * 4);
            for (int c = 0; c < 3; c++) {
                line += "  " + clues[r * 3 + c] + "   " + label(13 + r * 4 + c);
            }
            IO.println(line);
        }
    }
    IO.println("## marks chosen edge; cell digits count surrounding edges.");
}

void main() {
    reset();
    IO.println("Loop Builder");
    IO.println("Choose edges to make exactly one closed loop. Each cell's number counts the chosen edges around it.");
    IO.println("Enter edge1-24 to toggle it. One closed loop must match all numbered cells. /r restarts; /q quits.");
    IO.println("Example: enter 1 to toggle edge 01. ## means chosen; enter the same edge again to remove it.");
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
            IO.println("Invalid move. " + "Enter edge1-24 to toggle it. One closed loop must match all numbered cells. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
