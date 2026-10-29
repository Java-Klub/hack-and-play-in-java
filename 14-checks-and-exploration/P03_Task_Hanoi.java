/// # Solved task · Hanoi
/// Lesson: 14.1 Choose examples that catch mistakes — 01-checks.html
///
/// Original assignment (this version is complete):
///
/// **Your mission · Find the shortest forbidden sequence:** The tower rules already work. Find a
/// shortest two-attempt demonstration: the first move is legal; the second tries to put a larger disc
/// on a smaller one and is rejected without changing the tower. Store those two commands as the
/// program’s demonstration. Changing the guard to permit the forbidden move is not the goal.
///
/// The starter works by its original rules. The check already expects your new goal.
///
/// Use A B or other peg pairs to investigate; r restarts. D replays your stored commands from a fresh
/// tower and prints moved/rejected. The initial demonstration contains only one legal move.

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Find a different equally short demonstration, then a longer one exposing the same guard. Ask a
/// partner to remove redundant moves without losing the forbidden situation.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Program · explore as needed">
String demonstration() {
    return "A B\nA B";
}

boolean move(int from, int to) {
    if (won() || from < 0 || from > 2 || to < 0 || to > 2 || from == to || sizes[from] == 0) {
        return false;
    }
    int disc = pegs[from][sizes[from] - 1];
    if (sizes[to] > 0 && pegs[to][sizes[to] - 1] < disc) {
        return false;
    }
    sizes[from]--;
    pegs[from][sizes[from]] = 0;
    pegs[to][sizes[to]] = disc;
    sizes[to]++;
    moves++;
    return true;
}

int[][] pegs = {{3, 2, 1}, {0, 0, 0}, {0, 0, 0}};
int[] sizes = {3, 0, 0};
int moves;

void reset() {
    for (int[] peg : pegs) {
        Arrays.fill(peg, 0);
    }
    pegs[0][0] = 3;
    pegs[0][1] = 2;
    pegs[0][2] = 1;
    sizes[0] = 3;
    sizes[1] = 0;
    sizes[2] = 0;
    moves = 0;
}

boolean won() {
    return sizes[2] == 3;
}

void main() {
    IO.println("HANOI: move all three discs from A to C.");
    IO.println("Move only the top disc, never a larger disc onto a smaller one.");
    IO.println("The top disc is the rightmost number on each peg. D: replay your demonstration from a fresh tower.");
    boolean showState = true;
    while (true) {
        if (showState) {
            for (int i = 0; i < 3; i++) {
                IO.println((char) ('A' + i) + ": " + Arrays.toString(Arrays.copyOf(pegs[i], sizes[i])));
            }
            IO.println("Moves: " + moves);
            if (won()) {
                IO.println("Solved! The minimum for three discs is 7 moves.");
            }
            showState = false;
        }
        String input = IO.readln(won() ? "Enter or /r: new round; /q: quit: " : "From to, e.g. A C; r restart, q quit: ");
        if (input == null || (input.trim().equalsIgnoreCase("q") || input.trim().equalsIgnoreCase("/q"))) {
            return;
        }
        input = input.trim().toUpperCase(Locale.ROOT);
        if ((input.equals("R") || input.equals("/R") || (won() && input.isBlank()))) {
            reset();
            showState = true;
            continue;
        }
        if (input.equals("D") || input.equals("/D")) {
            reset();
            int attempt = 0;
            for (String line : demonstration().lines().toList()) {
                String[] pair = line.strip().toUpperCase(Locale.ROOT).split("\\s+");
                boolean accepted = pair.length == 2 && pair[0].length() == 1 && pair[1].length() == 1
                    && move(pair[0].charAt(0) - 'A', pair[1].charAt(0) - 'A');
                IO.println("Demo " + (++attempt) + ": " + line + " -> " + (accepted ? "moved" : "rejected"));
            }
            showState = true;
            continue;
        }
        if (won()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        String[] parts = input.split("\\s+");
        if (parts.length == 2 && parts[0].length() == 1 && parts[1].length() == 1
                && move(parts[0].charAt(0) - 'A', parts[1].charAt(0) - 'A')) {
            showState = true;
        } else {
            IO.println("Illegal move. Use two different pegs A-C with a legal top disc.");
        }
    }
}
// </editor-fold>
