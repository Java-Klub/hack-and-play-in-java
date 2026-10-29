/// # L03.09 · SOS
///
/// **Your mission · Trace and change the reward:** Install a combo reward: one newly made SOS scores
/// one, but two or more in the same move score double their new-line count. Existing lines score
/// nothing again. Find the route from a placed letter to the score; leave line detection and bonus
/// turns intact.
///
/// The starter works by its original rules. The check already expects your new goal.
///
/// Play cell number plus S or O. /r restarts and /q quits. The standard starter scores one per new
/// line. Try making two lines through the centre in one move to see your new rule.

// <editor-fold defaultstate="expanded" desc="Learn · F13">
/// A method packages an operation under one name. Parameters are local names for the information
/// received on each call; arguments are the actual supplied values. `return` gives one result back to
/// the caller.
///
/// ```java
/// int remaining(int capacity, int used) {
///     return capacity - used;
/// }
///
/// void main() {
///     IO.println(remaining(12, 5));
///     IO.println(remaining(9, 4));
/// }
/// ```
///
/// Predict both printed values: seven and five. The comma separates the parameters or arguments, and
/// their order decides which value each name receives. The names describe roles; they do not fetch
/// information by magic. This method calculates without printing or changing the supplied program
/// state. When repairing a calculation, try a second pair of arguments so an accidental fixed answer
/// cannot hide.
///
/// Now predict remaining(10, 6).
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Explore and experiment">
/// Separate a count of all existing lines from a reward for one move. Follow where the old count is
/// saved, where the board changes and where the new count returns. Your changed calculation should
/// receive those two counts and return a reward; it should not need to know the board or whose turn
/// it is. Neutral example: two inventory counts can describe an increase without revealing the whole
/// inventory.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · search area">
/// Search for the place that adds to a player’s score, then follow the value being added.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a concrete route">
/// command obtains that value from gainedPoints(before, triples()). Keep triples unchanged.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Possible change · reveals the direction">
/// Calculate after - before; double it when it is at least two, otherwise return it unchanged.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Build a position where the centre finishes two lines, then propose another combo table. A friend
/// should be able to infer your rule from three scored examples.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Program · explore as needed">
int gainedPoints(int before, int after) {
    return after - before;
}


char[] board;
int player;
int moves;
int[] scores;
String outcome;

void reset() {
    board = ".........".toCharArray();
    player = 1;
    moves = 0;
    scores = new int[3];
    outcome = "";
}

int triples() {
    int total = 0;
    for (int r = 0; r < 3; r++) {
        for (int c = 0; c < 3; c++) {
            for (int[] d : new int[][]{{0, 1}, {1, 0}, {1, 1}, {1, -1}}) {
                int er = r + 2 * d[0];
                int ec = c + 2 * d[1];
                if (er < 0 || er >= 3 || ec < 0 || ec >= 3) {
                    continue;
                }
                if (board[r * 3 + c] == 'S' && board[(r + d[0]) * 3 + c + d[1]] == 'O' && board[er * 3 + ec] == 'S') {
                    total++;
                }
            }
        }
    }
    return total;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.toUpperCase(Locale.ROOT).split("\\s+");
    require(p.length == 2 && p[1].matches("[SO]"));
    int cell = Integer.parseInt(p[0]) - 1;
    require(cell >= 0 && cell < 9 && board[cell] == '.');
    int before = triples();
    board[cell] = p[1].charAt(0);
    moves++;
    int gained = gainedPoints(before, triples());
    scores[player] += gained;
    if (gained == 0) {
        player = 3 - player;
    }
    if (moves == 9) {
        outcome = scores[1] == scores[2] ? "Draw" : "Player " + (scores[1] > scores[2] ? 1 : 2) + " wins";
    }
}

void display() {
    IO.println("Player " + player + "; score " + scores[1] + ":" + scores[2]);
    for (int r = 0; r < 3; r++) {
        String line = "";
        for (int c = 0; c < 3; c++) {
            line += (board[r * 3 + c] == '.' ? "" + (r * 3 + c + 1) : "" + board[r * 3 + c]) + " ";
        }
        IO.println(line);
    }
}

void main() {
    reset();
    IO.println("SOS");
    IO.println("Enter cell 1-9 and S or O, e.g. 5 O. Each new SOS scores. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter cell 1-9 and S or O, e.g. 5 O. Each new SOS scores. /r restarts; /q quits.");
        }
    }
}

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
