/// # L06.12 · Merge2048
///
/// Slide numbered tiles into bigger pairs. Design tiny rows that can expose a fake 2048 engine before a
/// full game hides its mistakes.
///
/// **Your mission:** The empty-row check lets broken merge rules pass. Expand learnerChecks() to
/// challenge compression, merging, independent pairs, and awarded points.
///
/// **Today you learn:** Writing functional checks: a result can have more than one observable part. State
/// both the transformed row and its score, then choose cases that separate plausible mistakes.
///
/// Run File and type A followed by Enter to slide left, then try other directions and use Test File to
/// challenge your row examples.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F33 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
record MergeExample(int[] input, int[] expected, int score) {
}

MergeExample[] learnerChecks() {
    return new MergeExample[]{
    new MergeExample(new int[]{0, 0, 0, 0}, new int[]{0, 0, 0, 0}, 0),
    new MergeExample(new int[]{2, 0, 2, 0}, new int[]{4, 0, 0, 0}, 4),
    new MergeExample(new int[]{2, 2, 4, 0}, new int[]{4, 4, 0, 0}, 4),
    new MergeExample(new int[]{4, 4, 4, 4}, new int[]{8, 8, 0, 0}, 16)};
}

// <editor-fold defaultstate="collapsed" desc="Recall · F33">
/// ### Checks: choose an example that could disagree
///
/// A functional check needs an input and an independently predicted result. A passing example is weak
/// if a plausible wrong rule gives the same answer. Read this separate calculation and its example;
/// the equality produces a boolean that reports agreement.
///
/// ```java
/// int twice(int value) {
///     return value + value;
/// }
///
/// void main() {
///     boolean agrees = twice(0) == 0;
///     IO.println(agrees);
/// }
/// ```
///
/// This prints true: twice zero is zero. An implementation always returning zero also passes. Choose
/// a different input and derive its expected result yourself. What mistake does your new example
/// expose? Keep each check's starting data independent so one example cannot accidentally prepare
/// another.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `record MergeExample(int[] input, int[] expected, int score)` — An example carries an input row, an
///   expected row, and expected awarded points.
/// - `MergeExample[]` — The checker consumes an array of independent row scenarios.
/// - `new int[]{0, 0, 0, 0}` — An explicit four-cell row; zero represents an empty slot.
/// - `new MergeExample(` — Constructs one complete expectation for the real rule and its wrong variants.
///
/// ### What the prepared part already does
///
/// merge() supplies the real row rule used by movement. Startup checks your examples against it; Test
/// File tries five wrong variants, including one that merges only the first available pair.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Check the whole observable result
///
/// A MergeExample contains an input row, an expected row and awarded points. The supplied checker
/// compares array contents. For orientation, sliding `[0,8,0,2]` left compresses it to `[8,2,0,0]`
/// without a merge or points. A newly merged tile cannot merge again in the same move. Draw a
/// different row to test that rule, then choose another that separates moving tiles from awarding
/// points. Predict both parts before constructing the record.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Every example is one four-cell row sliding left; score is the sum of created tile values.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// [2,0,2,0] becomes [4,0,0,0], but [2,2,4,0] becomes [4,4,0,0].
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Also test [4,4,4,4] -> [8,8,0,0] with score 16; justify each expectation.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[][] board;
int score;
String outcome;
Random random = new Random();

/// reset(): Restores the starting 2048 round, clearing the state that belongs to the old round.
void reset() {
    board = new int[4][4];
    score = 0;
    outcome = "";
    spawn();
    spawn();
}

void spawn() {
    var free = new ArrayList<int[]>();
    for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
            if (board[r][c] == 0) {
                free.add(new int[]{r, c});
            }
        }
    }
    if (free.isEmpty()) {
        return;
    }
    int[] p = free.get(random.nextInt(free.size()));
    board[p[0]][p[1]] = random.nextInt(10) == 0 ? 4 : 2;
}

/// merge(): Compresses zeros, combines equal neighbours once and awards created tile values.
int[] merge(int[] row) {
    int[] values = Arrays.stream(row).filter((n) -> n != 0).toArray();
    int[] result = new int[4];
    int out = 0;
    for (int i = 0; i < values.length; i++) {
        int n = values[i];
        if (i + 1 < values.length && n == values[i + 1]) {
            n *= 2;
            score += n;
            i++;
        }
        result[out++] = n;
    }
    return result;
}

boolean canMove() {
    for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
            if (board[r][c] == 0 || r < 3 && board[r][c] == board[r + 1][c] || c < 3 && board[r][c] == board[r][c + 1]) {
                return true;
            }
        }
    }
    return false;
}

/// command(): Validates one actual player command before applying its changes to this game.
void command(String input) {
    require(outcome.isEmpty());
    require(input.matches("(?i)[wasd]"));
    char d = input.toLowerCase(Locale.ROOT).charAt(0);
    boolean changed = false;
    for (int line = 0; line < 4; line++) {
        int[] values = new int[4];
        for (int i = 0; i < 4; i++) {
            int k = d == 'd' || d == 's' ? 3 - i : i;
            values[i] = d == 'a' || d == 'd' ? board[line][k] : board[k][line];
        }
        int[] result = merge(values);
        changed |= !Arrays.equals(values, result);
        for (int i = 0; i < 4; i++) {
            int k = d == 'd' || d == 's' ? 3 - i : i;
            if (d == 'a' || d == 'd') {
                board[line][k] = result[i];
            } else {
                board[k][line] = result[i];
            }
        }
    }
    if (changed) {
        spawn();
    }
    for (int[] row : board) {
        for (int n : row) {
            if (n >= 2048) {
                outcome = "2048: you win!";
            }
        }
    }
    if (outcome.isEmpty() && !canMove()) {
        outcome = "No legal move: game over.";
    }
}

/// display(): Presents the current board and the legal input choices in the console.
void display() {
    for (int[] row : board) {
        IO.println(Arrays.toString(row));
    }
    IO.println("Score: " + score);
}

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
/// Prepared console flow: type a command and press Enter. A valid command redraws the board.
/// Rejected input leaves the display alone; after a round, Enter or /r starts again and /q quits.
void main() {
    reset();
    verifyExamples();
    IO.println("2048");
    IO.println("Slide all tiles with W/A/S/D. Equal neighbours merge once per move; aim for 2048 before the board locks.");
    IO.println("W/A/S/D slides up/left/down/right. Equal values merge once. /r restarts; /q quits.");
    IO.println("Example: a slides left. A slide that changes nothing does not add another tile.");
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
            IO.println("Invalid move. " + "W/A/S/D slides up/left/down/right. Equal values merge once. /r restarts; /q quits.");
        }
    }
}

/// require(): Rejects an illegal command with an exception handled by the supplied input loop.
void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

/// verifyExamples(): Startup runs your examples against the real game rule; supplied save/restore
/// keeps the round unchanged.
void verifyExamples() {
    int saved = score;
    try {
        for (var e : learnerChecks()) {
            score = 0;
            if (!Arrays.equals(merge(e.input().clone()), e.expected()) || score != e.score()) {
                throw new IllegalStateException("Merge example has wrong expected result");
            }
        }
    } finally {
        score = saved;
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
/// - `char` — A type for one UTF-16 code unit, often a simple board character such as X. Text uses
///   String.
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
/// - `else` — The alternative when the preceding if condition is false.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `record` — A small named bundle of values. Its component accessors use the component names with ().
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>

