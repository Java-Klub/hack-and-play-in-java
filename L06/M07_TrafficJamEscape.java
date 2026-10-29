/// # L06.07 · TrafficJamEscape
///
/// Slide cars around a traffic jam until car A can leave through the right edge. Trying a blocked move
/// should be a harmless experiment.
///
/// **Your mission:** A rejected position can still secretly move a car. Repair fits() so testing a
/// possible position never commits that position.
///
/// **Today you learn:** Pure contracts: separate asking whether a move fits from actually making the
/// move. Repeated or unsuccessful probes must leave the board unchanged.
///
/// Request a car move by entering B 2, then use a car letter and signed
/// step count to clear A’s exit.
boolean fits(int car, int x, int y) {
    for (int i = 0; i < lengths[car]; i++) {
        int px = x + (vertical[car] ? 0 : i);
        int py = y + (vertical[car] ? i : 0);
        if (py < 0 || py >= 6 || px < 0 || px >= 6 && car != 0) {
            return false;
        }
        for (int j = 0; j < 2; j++) {
            if (j != car) {
                for (int k = 0; k < lengths[j]; k++) {
                    if (px == xs[j] + (vertical[j] ? 0 : k) && py == ys[j] + (vertical[j] ? k : 0)) {
                        return false;
                    }
                }
            }
        }
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F15">
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
/// - `xs[car] = x` — Assignment changes the saved x position of the actual car.
/// - `vertical[car] ? 0 : i` — Chooses whether this car’s next occupied cell advances horizontally.
/// - `j != car` — Avoids treating the car being tested as a different blocking car.
/// - `return false` — Rejects a proposed position; rejection must not leave a partial move behind.
///
/// ### What the prepared part already does
///
/// command() checks every intermediate position of a multi-cell move before updating the car fields.
/// fits() supplies those checks; lengths, orientations, collision traversal, and exit handling are
/// already present.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// x and y are proposed arguments, not new field values yet.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// command() checks every intermediate square before assigning xs and ys.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Delete the assignments to xs[car] and ys[car] from fits(); keep collision checks.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Make it yours">
/// Add a hint command that reports whether one chosen car can move to two candidate positions. Call
/// fits for both probes before committing any move. Give a partner a blocked and an open candidate and
/// show that asking the hint leaves every car exactly where it was.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[] xs;
int[] ys;
int[] lengths = {2, 3};
boolean[] vertical = {false, true};
String outcome;

/// reset(): Restores the starting Traffic Jam Escape round, clearing the state that belongs to the
/// old round.
void reset() {
    xs = new int[]{0, 2};
    ys = new int[]{2, 1};
    outcome = "";
}

/// command(): Checks every intermediate position before committing a signed multi-cell move.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.toUpperCase(Locale.ROOT).split("\\s+");
    require(p.length == 2 && p[0].matches("[AB]"));
    int car = p[0].charAt(0) - 'A';
    int amount = Integer.parseInt(p[1]);
    require(amount != 0 && Math.abs((long)amount) <= 6);
    int x = xs[car];
    int y = ys[car];
    int sign = Integer.signum(amount);
    for (int i = 0; i < Math.abs(amount); i++) {
        x += vertical[car] ? 0 : sign;
        y += vertical[car] ? sign : 0;
        require(fits(car, x, y));
    }
    xs[car] = x;
    ys[car] = y;
    if (xs[0] >= 6) {
        outcome = "A escaped: you win!";
    }
}

/// display(): Presents the current board and the legal input choices in the console.
void display() {
    char[][] grid = new char[6][6];
    for (char[] row : grid) {
        Arrays.fill(row, '.');
    }
    for (int car = 0; car < 2; car++) {
        for (int i = 0; i < lengths[car]; i++) {
            int x = xs[car] + (vertical[car] ? 0 : i);
            int y = ys[car] + (vertical[car] ? i : 0);
            if (x < 6) {
                grid[y][x] = (char)('A' + car);
            }
        }
    }
    for (int r = 0; r < 6; r++) {
        IO.println(new String(grid[r]) + (r == 2 ? " -> EXIT" : ""));
    }
}

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
/// Prepared console flow: type a command and press Enter. A valid command redraws the board.
/// Rejected input leaves the display alone; after a round, Enter or /r starts again and /q quits.
void main() {
    reset();
    IO.println("Traffic Jam Escape");
    IO.println("Move the red car A fully through the right-hand exit. Other cars slide only along their own orientation.");
    IO.println("Enter car and signed steps, e.g. B 2. Positive means right/down. Move A fully out right. /r restarts; /q quits.");
    IO.println("Example: B 2 moves B two cells down, clearing A's row. A 6 then tries to drive A fully out.");
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
            IO.println("Invalid move. " + "Enter car and signed steps, e.g. B 2. Positive means right/down. Move A fully out right. /r restarts; /q quits.");
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
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `char` — A type for one UTF-16 code unit, often a simple board character such as X. Text uses
///   String.
/// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `!=` — Checks that two values differ.
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
