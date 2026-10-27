/// # L05.11 · Dominosa
///
/// Cover the number board with dominoes, using each number pair only once. Turning a domino around should
/// not create a second kind of piece.
///
/// **Your mission:** A reversed duplicate slips through the uniqueness check. Repair pairKey(int a, int
/// b) so equal domino values are represented consistently.
///
/// **Today you learn:** Records: equal component values give equal record values. Choose a consistent
/// representation before using a Domino as a uniqueness key.
///
/// Run File and type 1 1 1 2 to pair the first two adjacent cells, then continue with other neighbouring
/// cells.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F19 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
record Domino(int low, int high) {
}

Domino pairKey(int a, int b) {
    int x = numbers[a / 10][a % 10], y = numbers[b / 10][b % 10];
    return new Domino(Math.min(x, y), Math.max(x, y));
}

// <editor-fold defaultstate="collapsed" desc="Recall · F19">
/// ### Records: one named value
///
/// A record groups related components. Its constructor receives them in declaration order; accessors
/// such as `ink()` read them. Inside the record, its own methods can use the component names
/// directly. Making a new value leaves the original components unchanged. Generated `equals` compares
/// the component values.
///
/// ```java
/// record Frame(String ink, int dots) {
///     Frame recoloured(String nextInk) {
///         return new Frame(nextInk, dots);
///     }
/// }
/// ```
///
/// Calling `recoloured("gold")` on `new Frame("violet", 6)` returns gold and six; the original stays
/// violet and six. Would another recolouring back to violet compare equal to the original? The same
/// names now explain what two otherwise anonymous values mean.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `record Domino(int low, int high)` — A two-number value with generated component access and value
///   equality.
/// - `a / 10` — Decodes the row from the game’s packed cell number.
/// - `a % 10` — Decodes the column as the remainder after division by ten.
/// - `new Domino(x, y)` — Constructs the key that the duplicate checker will compare with existing
///   pieces.
///
/// ### What the prepared part already does
///
/// command() checks adjacency and unused cells, then stores Domino keys in a Set. That supplied
/// collection prevents repeats only when pairKey() gives equivalent pieces equal values.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Values and canonical representation
///
/// `Math.min(a,b)` selects the smaller number and `Math.max(a,b)` the larger. These queries do not
/// change either input. A Domino compares the ordered components low and high, not the physical way a
/// player turned it. First read the two domino values from `numbers`; the selected cell indices are
/// not those values. Predict a consistent component pair for values three and eight presented in
/// either order, and also for two equal values.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Record equality compares both named components by value.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Domino(0,1) and Domino(1,0) are different records unless you normalize input.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Construct Domino(min(x,y), max(x,y)); the supplied Set then rejects repeats.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[][] numbers = {{0, 0, 0, 1, 0, 2, 0, 3, 1, 1}, {1, 2, 1, 3, 2, 2, 2, 3, 3, 3}};
int[] partner;

Set<Domino> used;
String outcome;

/// reset(): Restores the starting Dominosa round, clearing the state that belongs to the old round.
void reset() {
    partner = new int[20];
    Arrays.fill(partner, -1);
    used = new HashSet<>();
    outcome = "";
}

int cell(String row, String col) {
    int r = Integer.parseInt(row) - 1;
    int c = Integer.parseInt(col) - 1;
    require(r >= 0 && r < 2 && c >= 0 && c < 10);
    return r * 10 + c;
}

/// command(): Places adjacent unused cells or erases their pair; the Set prevents duplicate domino
/// values.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    if (p.length == 3 && p[0].equalsIgnoreCase("erase")) {
        int a = cell(p[1], p[2]);
        require(partner[a] >= 0);
        int b = partner[a];
        used.remove(pairKey(a, b));
        partner[a] = partner[b] = -1;
    } else {
        require(p.length == 4);
        int a = cell(p[0], p[1]);
        int b = cell(p[2], p[3]);
        require(Math.abs(a / 10 - b / 10) + Math.abs(a % 10 - b % 10) == 1 && partner[a] < 0 && partner[b] < 0 && !used.contains(pairKey(a, b)));
        used.add(pairKey(a, b));
        partner[a] = b;
        partner[b] = a;
        if (used.size() == 10) {
            outcome = "All ten distinct dominoes cover the board!";
        }
    }
}

/// display(): Presents the current board and the legal input choices in the console.
void display() {
    IO.println("Authored 0-3 set. Rows1-2; columns1-10. Match adjacent cells once per unordered pair.");
    for (int r = 0; r < 2; r++) {
        IO.println((r + 1) + " " + Arrays.toString(numbers[r]));
    }
    IO.println("Current partners (cell numbers1-20):");
    for (int i = 0; i < 20; i++) {
        if (partner[i] > i) {
            IO.println((i + 1) + "--" + (partner[i] + 1));
        }
    }
}

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
void main() {
    reset();
    IO.println("Dominosa");
    IO.println("Pair with row col row col; erase row col removes a domino. Use every unordered pair0-3 once. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Pair with row col row col; erase row col removes a domino. Use every unordered pair0-3 once. /r restarts; /q quits.");
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
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
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
/// - `record` — A small named bundle of values. Its component accessors use the component names with ().
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Set` — Stores distinct values; the same value is present at most once.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

