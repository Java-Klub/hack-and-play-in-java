/// # L04.05 · WordSearch
///
/// Hunt for CAT, DOG and TIGER in a letter grid by selecting the two ends of a word. A selection can run
/// horizontally, vertically or diagonally, so its direction is part of the puzzle.
///
/// **Your mission:** The selected path currently returns no letters. Reconstruct the for loop that
/// appends every selected cell to word, including both endpoints. Use the supplied dr, dc and gap count
/// n; preserve validation.
///
/// **Today you learn:** For loops and endpoints: a row of three cells has only two gaps. Here n counts
/// the gaps; trace the successive i values to decide how many letters must be read.
///
/// Select the first row with 1 1 1 3. The unfinished reader returns empty text; the full path must read
/// CAT. Reversing the endpoints must read TAC.
String read(int r, int c, int nr, int nc) {
    require(r >= 0 && r < 5 && c >= 0 && c < 5 && nr >= 0 && nr < 5 && nc >= 0 && nc < 5);
    int dr = Integer.signum(nr - r);
    int dc = Integer.signum(nc - c);
    int n = Math.max(Math.abs(nr - r), Math.abs(nc - c));
    require(n > 0 && (r == nr || c == nc || Math.abs(nr - r) == Math.abs(nc - c)));
    String word = "";
    return word;
}

// <editor-fold defaultstate="expanded" desc="Learn · F11">
/// A counted `for` puts initialization, a continue condition and progress in one header.
/// Initialization happens once. Then Java checks the condition, runs the body if true, performs
/// progress, and checks again.
///
/// These example statements belong inside a called method.
///
/// ```java
/// for (int floor = 2; floor <= 4; floor++) {
///     IO.println(floor);
/// }
/// ```
///
/// Predict the visited values: `2`, `3`, `4`. There are three passes, even though the last printed
/// number is four. The two semicolons separate the header’s three parts; `floor++` adds one after a
/// pass. The braces group the repeated statements. If the starting value already fails the condition,
/// no pass occurs. Count positions and gaps separately: three positions have two gaps. Write down a
/// tiny trace before changing a bound.
///
/// Now predict the output if floor starts at five.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Read this code">
/// The supplied calculations give a start (r,c), one-step direction (dr,dc) and n gaps. They already
/// reject bent and off-board paths.
///
/// | i along CAT | row | column | collected text |
/// | --- | --- | --- | --- |
/// | 0 | 0 | 0 | C |
/// | 1 | 0 | 1 | CA |
/// | 2 | 0 | 2 | CAT |
///
/// Three positions need two gaps. At step i, read `grid[r + i * dr].charAt(c + i * dc)` and append the
/// returned character to word. Build a for header whose visited values include the last position.
/// Predict coordinates for the same selection backwards. command uses the returned word to record a
/// real find.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// List the i values needed for a three-letter selection.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Start from zero; each pass appends one character before i advances.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Visit i from zero through n inclusive and append the supplied grid lookup to word.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Make it yours">
/// Hide a new word in your grid in a reverse or diagonal direction. Give a partner only its length and
/// endpoint coordinates in the opposite order. Predict both strings before trying the selection; which
/// order spells the intended word?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String[] grid = {"CATER", "ADOGX", "TIGER", "XXXXX", "XXXXX"};
Set<String> found;
String outcome;

void reset() {
    found = new LinkedHashSet<>();
    outcome = "";
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 4);
    String word = read(Integer.parseInt(p[0]) - 1, Integer.parseInt(p[1]) - 1, Integer.parseInt(p[2]) - 1, Integer.parseInt(p[3]) - 1);
    require(Set.of("CAT", "DOG", "TIGER").contains(word) && !found.contains(word));
    found.add(word);
    if (found.size() == 3) {
        outcome = "All words found!";
    }
}

void display() {
    IO.println("Columns 1-5. Targets CAT DOG TIGER; found " + found);
    for (int r = 0; r < 5; r++) {
        IO.println((r + 1) + " " + grid[r]);
    }
}

void main() {
    reset();
    IO.println("Word Search");
    IO.println("Enter startRow startCol endRow endCol (1-5) along a straight word. /r restarts; /q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
            showState = false;
        }
        String input = IO.readln(outcome.isEmpty() ? "> " : "Enter or /r: new round; /q: quit: ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r") || (!outcome.isEmpty() && input.isBlank())) {
            reset();
            showState = true;
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            command(input.strip());
            showState = true;
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move or command. Enter startRow startCol endRow endCol (1-5) along a straight word. /r restarts; /q quits.");
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
/// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
///   direction. In List<String>, angle brackets describe the element type.
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
/// - `Set` — Stores distinct values; the same value is present at most once.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
