/// # L06.05 · Sokoban
///
/// Push a crate onto its goal in a cramped warehouse. Planning matters because you can push a box but
/// cannot pull it back.
///
///
/// **Your mission:** Make ? list exactly the legal immediate W/A/S/D moves without moving the player or
/// crate. A push is legal only if the crate has free floor beyond it; a wall, map boundary, diagonal or
/// finished round is never a legal move. Asking repeatedly must leave everything unchanged. This is a
/// one-move radar, not a promise that the puzzle remains solvable.
///
/// **Today you learn:** A query and a command can agree about legality while only the command changes
/// state. Check both accepted and rejected candidates for side effects.
///
/// Enter ? twice, then d twice, then ? again. Compare the advice with an attempted third d and inspect
/// whether asking alone changes the printed warehouse.
boolean canMove(int dx, int dy) {
    return floor(x + dx, y + dy);
}

// <editor-fold defaultstate="expanded" desc="Learn · F15">
/// ### Contracts: ask without changing the world
///
/// A contract describes accepted inputs and the meaning of a result. A pure calculation uses its
/// inputs without changing other state. A query may read the current model, but it should still leave
/// that model untouched. Reading an answer and committing an action are separate responsibilities.
///
/// ```java
/// int contrast(int light, int dark) {
///     return Math.abs(light - dark);
/// }
/// ```
///
/// `Math.abs` gives the nonnegative magnitude, removing a negative sign. This method can be called
/// repeatedly without consuming anything. Arguments `(9, 4)` give five: the difference is five.
/// Predict `(4, 9)`. Which stored values would you compare before and after a query to show that it
/// caused no side effect? A rejected candidate deserves the same protection.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Read this code">
/// The next player square and the next crate square are different candidates. Inspect terrain before
/// changing either position.
///
/// In a separate corridor @ $ #, stepping into the crate square is not enough: the square beyond is a
/// wall, so the push is illegal. With @ $ . it is legal. A planning query must produce the same answer
/// twice without first performing and undoing a move. Snapshot the player, crate and outcome before
/// asking. Predict a query aimed away from the crate and one at the map edge.
// </editor-fold>


// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Compare the radar path with the actual move path. Find a candidate they judge differently.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The ? branch calls canMove for the four directions. canMove currently checks only floor at the next
/// player square; command also checks where a pushed crate would go.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Reject non-cardinal steps and a finished round, reject missing floor, then inspect the square beyond
/// a crate only when the next square contains it. Return an answer without assigning positions.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Make it yours">
/// Add a second advisor that warns about a crate pushed into a non-goal corner. Keep that warning
/// separate from immediate legality and author one legal but losing push to demonstrate the
/// distinction.
// </editor-fold>

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

/// reset(): Restores the starting Sokoban round, clearing the state that belongs to the old round.
void reset() {
    x = 2;
    y = 2;
    bx = 3;
    by = 2;
    outcome = "";
}

/// command(): Checks a player step and any pushed crate destination before committing both
/// positions.
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

/// display(): Presents the current board and the legal input choices in the console.
void display() {
    for (int r = 0; r < 5; r++) {
        String s = "";
        for (int c = 0; c < 7; c++) {
            s += x == c && y == r ? '@' : bx == c && by == r ? (terrain[r].charAt(c) == '.' ? '*' : '$') : terrain[r].charAt(c);
        }
        IO.println(s);
    }
}

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
/// Prepared console flow: type a command and press Enter. A valid command redraws the board.
/// Rejected input leaves the display alone; after a round, Enter or /r starts again and /q quits.
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

/// require(): Rejects an illegal command with an exception handled by the supplied input loop.
void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Small Java map · optional">
/// ## An unfamiliar piece of Java?
///
/// This small map is optional. Look up just the symbol or word you need; no need to learn it all first.
///
/// - `///` — Words for the reader. Java skips these comment lines when running.
/// - `;` — Ends one statement, like a full stop. A semicolon inside a for header separates its three
///   parts.
/// - `()` — After a name: call an action, or list its parameters in its declaration. Empty parentheses
///   mean no supplied arguments.
/// - `{}` — Braces group a block of code. The opening and closing brace belong together.
/// - `.` — Accesses something belonging to the value or type on its left, such as IO.println.
/// - `void` — This method performs an action and returns no result value.
/// - `main` — Java starts the program here. Other method declarations do not run just because they appear
///   earlier in the file.
/// - `int` — A type for whole numbers within a fixed range.
/// - `boolean` — A value that is either true or false.
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `'` — Single quotes enclose one character, such as a board marker.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
///   direction. In List<String>, angle brackets describe the element type.
/// - `?` — In condition ? first : second, chooses the first value when the condition is true, otherwise
///   the second.
/// - `:` — In for (int value : values), reads each element in turn. It also separates the two
///   alternatives of ? : or follows a switch label.
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
