/// # L05.04 · Quarto
///
/// Your opponent chooses the piece you must place. Four pieces sharing any property can win, so an
/// innocent-looking diagonal may be a trap.
///
/// **Your mission:** Rows can win, but a winning column or diagonal is ignored. Extend won() to inspect
/// every kind of four-cell line.
///
/// **Today you learn:** Arrays: a flat array can represent a square board. Follow how row and column
/// positions become indices before asking the same line rule about them.
///
/// From the course folder containing README.md:
/// - Play: `java L05/M04_Quarto.java`
/// - Check this mission: `java L05/Check.java M04_Quarto.java`
///
/// the play command, choose an unused piece number from 0 to 15, then let the other player enter
/// its destination cell from 1 to 16. After your repair: save the file, run the check command
/// above, then play again.
///
/// Need an explanation? Recall · F16 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
boolean won() {
    for (int row = 0; row < 4; row++) {
        if (line(row * 4, row * 4 + 1, row * 4 + 2, row * 4 + 3)) {
            return true;
        }
    }
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F16">
/// ### Arrays: the position matters
///
/// An array keeps a fixed number of values of one element type. Square brackets select a position:
/// the first index is zero, and `length` is the count. Reading one element does not alter the others.
/// An index must exist before it is used; an empty array has no first element.
///
/// ```java
/// String[] tools = {"pen", "tape", "chalk"};
/// int chosen = 1;
/// IO.println(tools[chosen]);
/// IO.println(tools.length);
/// ```
///
/// These statements belong inside a method. Index one selects `tape`; the count is three. What
/// changes if `chosen` becomes two? Which index would be the first invalid one?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `for (int row = 0; row < 4; row++)` — Visits four row numbers: 0, 1, 2, and 3.
/// - `row * 4` — Finds the first position of a row in the flat sixteen-cell array.
/// - `line(` — Calls the supplied query with four board indices.
/// - `return true` — Stops the search as soon as one winning line is found.
///
/// ### What the prepared part already does
///
/// line() already decides whether four occupied positions share a property using bit operations.
/// command() handles choosing and placing pieces, then asks won() whether the round has ended.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### A flat board has an invisible row width
///
/// A flat array can hold rows consecutively. In an unrelated three-column picture, row one starts
/// after the first three values; its column two is at index five. Moving down one row keeps the
/// column but skips the row width. Draw this game's indices as a square. `line(a,b,c,d)` already
/// checks a proposed group, so your job is selecting positions, not decoding piece properties.
/// Predict one column and each diagonal before extending the search.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Draw the array indices as a 4 by 4 board.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// A column has indices c, c+4, c+8, c+12.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// The two diagonals are 0,5,10,15 and 3,6,9,12.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[] board;
boolean[] used;
int player;
int selected;
int moves;
String outcome;

/// reset(): Restores the starting Quarto round, clearing the state that belongs to the old round.
void reset() {
    board = new int[16];
    Arrays.fill(board, -1);
    used = new boolean[16];
    player = 1;
    selected = -1;
    moves = 0;
    outcome = "";
}

/// line(): Supplied bit masks test whether four pieces share at least one binary property.
boolean line(int a, int b, int c, int d) {
    if (board[a] < 0 || board[b] < 0 || board[c] < 0 || board[d] < 0) {
        return false;
    }
    int both = board[a] & board[b] & board[c] & board[d];
    int either = board[a] | board[b] | board[c] | board[d];
    return both != 0 || ((~either) & 15) != 0;
}

/// command(): Validates one actual player command before applying its changes to this game.
void command(String input) {
    require(outcome.isEmpty());
    int n = Integer.parseInt(input);
    if (selected < 0) {
        require(n >= 0 && n < 16 && !used[n]);
        selected = n;
        used[n] = true;
        player = 3 - player;
    } else {
        require(n >= 1 && n <= 16 && board[n - 1] < 0);
        board[n - 1] = selected;
        selected = -1;
        moves++;
        if (won()) {
            outcome = "Player " + player + " wins";
        } else {
            if (moves == 16) {
                outcome = "Draw";
            }
        }
    }
}

String piece(int n) {
    return String.format("%4s", Integer.toBinaryString(n)).replace(' ', '0');
}

/// display(): Presents the current board and the legal input choices in the console.
void display() {
    IO.println("Player " + player + (selected < 0 ? ": choose a piece for your opponent" : ": place piece " + selected + " (" + piece(selected) + ")"));
    IO.println("Attributes: bits = tall, dark, square, hollow (1=yes, 0=no).");
    for (int r = 0; r < 4; r++) {
        IO.println(Arrays.toString(Arrays.copyOfRange(board, r * 4, r * 4 + 4)));
    }
    String available = "Unused pieces: ";
    for (int i = 0; i < 16; i++) {
        if (!used[i]) {
            available += i + "=" + piece(i) + " ";
        }
    }
    IO.println(available);
}

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
/// Prepared console flow: type a command and press Enter. A valid command redraws the board.
/// Rejected input leaves the display alone; after a round, Enter or /r starts again and /q quits.
void main() {
    reset();
    IO.println("Quarto");
    IO.println("Give a piece to your opponent, then place the piece they gave you. Four in a line sharing any attribute wins.");
    IO.println("Choose unused piece 0-15, then its receiver places in cell 1-16 (row order). -1 is empty. /r restarts; /q quits.");
    IO.println("Example: choose piece 0; the next player can enter 1 to place it in the first cell. Read the current phase above.");
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
            IO.println("Invalid move. " + "Choose unused piece 0-15, then its receiver places in cell 1-16 (row order). -1 is empty. /r restarts; /q quits.");
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
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
