/// # L02.09 · SonarTreasureHunt
///
/// Search an 8×8 sea for three hidden treasures with only eight probes. Each miss reports how far the
/// nearest remaining treasure was, so compare clues before spending another probe.
///
/// **Your mission:** The program accepts a probe one row or column beyond the map. Repair insideSea so
/// only indices 0 through 7 belong to the internal sea.
///
/// **Today you learn:** Boolean ranges: several comparisons can describe one rectangular area. Both the
/// row and the column must stay within their own lower and upper bounds; eight positions starting at zero
/// end at seven.
///
/// Use Run File, enter 9 1 to investigate the off-map probe and watch the Probes count; /r restores the
/// round before a real treasure search.
/// After your repair: Save → Test File → play again.
boolean insideSea(int row, int column) {
    return row >= 0 && row <= 8 && column >= 0 && column <= 8;
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `insideSea(int row, int column)` — Receive internal row and column indices, already converted from
///   the player’s 1–8 coordinates.
/// - `row >= 0` — The row is at least zero; >= includes equality.
/// - `row <= 8` — The current upper comparison includes eight itself, which is outside an eight-position
///   zero-based range.
/// - `column <= 8` — The column has its own upper bound; fixing only the row would leave another escape.
/// - `&&` — Logical AND: every connected comparison must be true for the complete expression to be true.
/// - `return` — Return the combined decision; this rule does not spend a probe by itself.
///
/// ### What the prepared part already does
///
/// command reads player coordinates, subtracts one from them and calls insideSea before recording a
/// probe. distance computes a clue, and the supplied history keeps each old clue tied to its original
/// moment.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. The printed 1–8 coordinates are converted to 0–7 before this rule.
/// 2. All four bounds must hold at the same time.
/// 3. Use row < 8 and column < 8; keep both >= 0 tests.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
Set<Integer> treasures;
Set<Integer> probed;
List<String> history;
int probes;
String outcome;

void reset() {
    var cells = new ArrayList<Integer>();
    for (int i = 0; i < 64; i++) {
        cells.add(i);
    }
    Collections.shuffle(cells);
    treasures = new HashSet<>(cells.subList(0, 3));
    probed = new HashSet<>();
    history = new ArrayList<>();
    probes = 0;
    outcome = "";
}

int distance(int cell) {
    int best = 99;
    for (int t : treasures) {
        best = Math.min(best, Math.abs(t / 8 - cell / 8) + Math.abs(t % 8 - cell % 8));
    }
    return best;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 2);
    int r = Integer.parseInt(p[0]) - 1;
    int c = Integer.parseInt(p[1]) - 1;
    require(insideSea(r, c) && !probed.contains(r * 8 + c));
    int cell = r * 8 + c;
    probed.add(cell);
    probes++;
    boolean hit = treasures.remove(cell);
    history.add("Probe " + probes + " at " + (r + 1) + "," + (c + 1) + ": " + (hit ? "treasure found" : "distance " + distance(cell)));
    if (treasures.isEmpty()) {
        outcome = "All treasures found!";
    } else {
        if (probes == 8) {
            outcome = "No probes left. Remaining treasures: " + treasures.stream().map((t)->(t / 8 + 1) + "," + (t % 8 + 1)).toList();
        }
    }
}

void display() {
    IO.println("Probes " + probes + "/8. Manhattan distance to nearest unfound treasure at that time.");
    IO.println("Old timestamped clues never change after a find.");
    for (String s : history) {
        IO.println(s);
    }
}

void main() {
    reset();
    IO.println("Sonar Treasure Hunt");
    IO.println("Enter probe row column (1-8). Eight probes to find three treasures. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter probe row column (1-8). Eight probes to find three treasures. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

/// Prepared entry points: main, reset, command.
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
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `%` — The remainder after division; useful for repeated positions or turns.
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
/// - `else` — The alternative when the preceding if condition is false.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `Set` — Stores distinct values; the same value is present at most once.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

