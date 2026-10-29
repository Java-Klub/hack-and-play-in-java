/// # Solved task · Sokoban
/// Lesson: 11.3 Rejection must preserve state — 03-invariants.html
///
/// Original assignment (this version is complete):
///
/// Push a crate onto its goal in a cramped warehouse. Planning matters because you can push a box but
/// cannot pull it back.
///
/// **Your mission:** Make ? list exactly the legal immediate W/A/S/D moves without moving the player or
/// crate. A push is legal only if the crate has free floor beyond it; a wall, map boundary, diagonal or
/// finished round is never a legal move. Asking repeatedly must leave everything unchanged. This is a
/// one-move radar, not a promise that the puzzle remains solvable.
///
/// Enter ? twice, then d twice, then ? again. Compare the advice with an attempted third d and inspect
/// whether asking alone changes the printed warehouse.

boolean canMove(int dx, int dy) {
    if (!outcome.isEmpty() || Math.abs(dx) + Math.abs(dy) != 1) {
        return false;
    }
    int nx = x + dx;
    int ny = y + dy;
    if (!floor(nx, ny)) {
        return false;
    }
    if (nx == bx && ny == by) {
        return floor(bx + dx, by + dy);
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
boolean floor(int px, int py) {
    if (py < 0 || py >= terrain.length || px < 0 || px >= terrain[py].length()) {
        return false;
    }
    return terrain[py].charAt(px) != '#';
}

String[] terrain = {"#######", "#    .#", "#     #", "#     #", "#######"};
int x;
int y;
int bx;
int by;
String outcome;

void reset() {
    x = 2;
    y = 2;
    bx = 3;
    by = 2;
    outcome = "";
}

void command(String input) {
    require(outcome.isEmpty());
    require(input.matches("(?i)[wasd]"));
    int dx = input.equalsIgnoreCase("a") ? -1 : input.equalsIgnoreCase("d") ? 1 : 0;
    int dy = input.equalsIgnoreCase("w") ? -1 : input.equalsIgnoreCase("s") ? 1 : 0;
    int nx = x + dx;
    int ny = y + dy;
    require(floor(nx, ny));
    if (nx == bx && ny == by) {
        require(floor(bx + dx, by + dy));
        bx += dx;
        by += dy;
    }
    x = nx;
    y = ny;
    if (terrain[by].charAt(bx) == '.') {
        outcome = "Crate on goal: you win!";
    }
}

void display() {
    for (int r = 0; r < 5; r++) {
        String s = "";
        for (int c = 0; c < 7; c++) {
            s += x == c && y == r ? '@' : bx == c && by == r ? (terrain[r].charAt(c) == '.' ? '*' : '$') : terrain[r].charAt(c);
        }
        IO.println(s);
    }
}

void main() {
    reset();
    IO.println("Sokoban");
    IO.println("Push the crate $ onto the goal dot. You can push, but cannot pull; restart if you trap the crate.");
    IO.println("W/A/S/D move; ? asks the move radar. @ player, $ crate, . goal, * crate on goal. Push only. /r restarts; /q quits.");
    IO.println("Example: d moves right; pressing Enter submits the command. Use /r to reset a stuck attempt.");
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
        if (input.equals("?")) {
            String advice = "";
            if (canMove(0, -1)) advice += "W ";
            if (canMove(-1, 0)) advice += "A ";
            if (canMove(0, 1)) advice += "S ";
            if (canMove(1, 0)) advice += "D ";
            IO.println("Immediate moves: " + (advice.isEmpty() ? "none" : advice.strip()));
            continue;
        }
        try {
            command(input);
            display();
            if (!outcome.isEmpty()) {
                IO.println(outcome);
            }
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move. " + "W/A/S/D move; ? asks the move radar. @ player, $ crate, . goal, * crate on goal. Push only. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
