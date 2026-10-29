/// # Task · DotsAndBoxes
/// Lesson: 7.1 Give values numbered places — 01-arrays.html
///
/// Take turns claiming lines between dots. Close a box to steal its point and take another turn.
///
/// **Your mission:** Every box currently consults the same four edges. Repair box(int n) so scoring
/// follows the box actually closed.
///
/// Then take turns typing an unclaimed edge number from 1 to 12 and pressing
/// Enter.

int[] box(int n) {
    return new int[]{0, 1, 6, 7};
}

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Edges are values too
///
/// The returned int array lists four edge indices; it does not list the four corners. Draw the
/// displayed board and label its edges with internal indices, one less than the printed numbers. For
/// a chosen box, find its top, bottom, left and right edges separately. A `new int[]{...}` groups
/// those four numbers into a result. Predict one box away from the top-left before writing a general
/// expression for n.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
boolean[] edges;
int[] owners;
int[] scores;
int player;
int moves;
String outcome;

void reset() {
    edges = new boolean[12];
    owners = new int[4];
    scores = new int[3];
    player = 1;
    moves = 0;
    outcome = "";
}

void command(String input) {
    require(outcome.isEmpty());
    int edge = Integer.parseInt(input) - 1;
    require(edge >= 0 && edge < 12 && !edges[edge]);
    edges[edge] = true;
    moves++;
    int gained = 0;
    for (int b = 0; b < 4; b++) {
        if (owners[b] != 0) {
            continue;
        }
        boolean complete = true;
        for (int e : box(b)) {
            complete &= edges[e];
        }
        if (complete) {
            owners[b] = player;
            scores[player]++;
            gained++;
        }
    }
    if (moves == 12) {
        outcome = scores[1] == scores[2] ? "Draw" : "Player " + (scores[1] > scores[2] ? 1 : 2) + " wins";
    }
    if (gained == 0) {
        player = 3 - player;
    }
}

String edge(int n) {
    return edges[n] ? "##" : String.format("%02d", n + 1);
}

void display() {
    IO.println("Player " + player + "; scores " + scores[1] + ":" + scores[2]);
    for (int r = 0; r < 3; r++) {
        IO.println("+--" + edge(r * 2) + "--+--" + edge(r * 2 + 1) + "--+");
        if (r < 2) {
            IO.println(edge(6 + r * 3) + "  " + owners[r * 2] + "   " + edge(7 + r * 3) + "  " + owners[r * 2 + 1] + "   " + edge(8 + r * 3));
        }
    }
}

void main() {
    reset();
    IO.println("Dots and Boxes");
    IO.println("Take turns claiming numbered edges. Close a box to score and play again; most boxes wins.");
    IO.println("Enter an unclaimed edge number 1-12. Completing a box grants another turn. /r restarts; /q quits.");
    IO.println("Example: enter 1 to claim edge 01. ## means an edge is already taken.");
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
            IO.println("Invalid move. " + "Enter an unclaimed edge number 1-12. Completing a box grants another turn. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
