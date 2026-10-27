/// # L08.12 · RuleGarden
///
/// Grow a whole page of patterns from one lit cell and a tiny numbered rule. Change the rule to discover
/// triangles, stripes, or apparent chaos.
///
/// **Your mission:** New cells immediately influence later cells in the same row, distorting the pattern.
/// Repair nextRow() so an entire generation reads the same old row.
///
/// **Today you learn:** Simultaneous updates: separate the snapshot being read from the generation being
/// written. The result should not depend on whether columns are visited left-to-right or right-to-left.
///
/// Run File, type 90 and press Enter to grow a pattern, then run again with another rule number from 0 to
/// 255.
/// After your repair: Save → Test File → play again.
boolean[] nextRow(boolean[] current, int rule) {
    boolean[] next = current;
    for (int column = 0; column < current.length; column++) {
        int pattern = current[column] ? 2 : 0;
        if (column > 0 && current[column - 1]) {
            pattern += 4;
        }
        if (column + 1 < current.length && current[column + 1]) {
            pattern += 1;
        }
        next[column] = ((rule>>pattern)&1) != 0;
    }
    return next;
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `boolean[] next = current` — Both names currently refer to the same mutable row, so a write changes
///   later reads.
/// - `current.length` — The number of cells in the input row, including small test rows.
/// - `column - 1` — The left neighbour is read only when its index is inside the row.
/// - `((rule>>pattern)&1) != 0` — The supplied bit expression reads the rule’s yes/no output for this
///   three-cell pattern.
///
/// ### What the prepared part already does
///
/// The supplied bit decoder turns three neighbouring cell values into a rule-table choice. grow() prints
/// each finished generation and then uses nextRow() for the next one; outside cells stay off.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. next=current makes two names for one array; an early write changes a later cell’s input.
/// 2. A shallow clone would separate one-dimensional boolean values, but a new output array makes
///   read/write roles clearest.
/// 3. Allocate new boolean[current.length]; keep every pattern read on current and return next after the
///   loop.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
List<String> grow(int rule, int width, int rows) {
    if (rule < 0 || rule > 255 || width < 3 || width > 121 || rows < 1 || rows > 60) {
        throw new IllegalArgumentException("Use rule 0..255, width 3..121 and rows 1..60.");
    }
    boolean[] cells = new boolean[width];
    cells[width / 2] = true;
    var result = new ArrayList<String>();
    for (int row = 0; row < rows; row++) {
        var line = new StringBuilder();
        for (boolean cell : cells) {
            line.append(cell ? '#' : '.');
        }
        result.add(line.toString());
        cells = nextRow(cells, rule);
    }
    return result;
}

/// main(): Reads one rule or optional arguments, prints a finite pattern, then returns.
void main(String[] args) {
    String text = args.length > 0 ? args[0] : IO.readln("Rule 0..255 (q to quit): ");
    if (text == null || text.equalsIgnoreCase("q")) {
        return;
    }
    try {
        int rule = Integer.parseInt(text.strip());
        int width = args.length > 1 ? Integer.parseInt(args[1]) : 61;
        int rows = args.length > 2 ? Integer.parseInt(args[2]) : 31;
        IO.println("Rule " + rule + "; cells outside the printed width stay off.");
        for (String line : grow(rule, width, rows)) {
            IO.println(line);
        }
    } catch (IllegalArgumentException invalidInput) {
        IO.println("Use rule 0..255, width 3..121 and rows 1..60.");
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
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
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
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

