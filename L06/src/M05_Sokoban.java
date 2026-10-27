/// # L06.05 · Sokoban
///
/// Push a crate onto its goal in a cramped warehouse. Planning matters because you can push a box but
/// cannot pull it back.
///
/// **Your mission:** The player can walk into wall squares because floor() checks only the map boundary.
/// Repair its yes/no answer without moving anything.
///
/// **Today you learn:** Pure contracts: a query promises an answer for its inputs without changing the
/// game. Define precisely which coordinates count as walkable floor.
///
/// Run File and type W, A, S, or D followed by Enter to move the player and push the crate toward its
/// goal.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Learn · F15 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
boolean floor(int px, int py) {
    return py >= 0 && py < 5 && px >= 0 && px < 7;
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

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `boolean floor(int px, int py)` — A query taking a proposed x/y position and returning yes or no.
/// - `&&` — Logical AND: every required condition must hold.
/// - `py >= 0` — The lower row boundary includes row zero.
/// - `px < 7` — The upper column boundary excludes seven because columns begin at zero.
///
/// ### What the prepared part already does
///
/// command() asks floor() about the player’s destination and, during a push, the crate’s destination. It
/// commits movement only after these queries succeed; map drawing and restart are supplied.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Reading one map character
///
/// A terrain row is a String. `row.charAt(index)` reads one char at a zero-based position; for
/// example, `"map".charAt(1)` reads the middle character. A char literal uses single quotes, unlike a
/// String literal. Here `#` marks a wall. Preserve the existing bounds before reading the selected
/// row and character. Predict the query's answer for an interior wall and explain why asking twice
/// must not move the player or crate.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The bounds are already checked safely before indexing terrain.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Inspect terrain[py].charAt(px).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Combine the bounds with the condition that this character is not #; do not assign any fields.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String[] terrain = {"#######", "#     #", "#    .#", "#     #", "#######"};
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
void main() {
    reset();
    IO.println("Sokoban");
    IO.println("W/A/S/D move. @ player, $ crate, . goal, * crate on goal. Push only. /r restarts; /q quits.");
    while (true) {
        display();
        if (!outcome.isEmpty()) {
            IO.println(outcome);
        }
        String input = IO.readln("> ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r")) {
            reset();
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. /r restarts; /q quits.");
            continue;
        }
        try {
            command(input.strip());
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move or command. W/A/S/D move. @ player, $ crate, . goal, * crate on goal. Push only. /r restarts; /q quits.");
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
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
/// - `/` — Divides numbers. Dividing two int values discards the fractional part.
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
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

