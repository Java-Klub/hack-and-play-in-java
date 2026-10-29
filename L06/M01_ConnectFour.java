/// # L06.01 · ConnectFour
///
/// Drop discs into columns and race a friend to connect four. A replay should give you a fresh board and
/// a fresh round.
///
///
/// **Your mission:** Make rematches alternate the opening player: the first round starts with X, the
/// next with O, then X again. This must not depend on who moved last or who won. Every rematch still
/// clears the board, winner and move count. Use the visible turn and Moves to investigate which facts
/// live for a round and which must survive across rounds.
///
/// **Today you learn:** State lifetime: a match fact must survive a round reset, while temporary
/// calculations and old round facts must not.
///
/// Start a round, enter 4 and then r. Compare the next opening player with another restart made before
/// any disc is dropped. The current game always opens with X.
void reset() {
    for (char[] row : board) {
        Arrays.fill(row, '.');
    }
    openingPlayer = openingPlayer == 'X' ? 'O' : 'X';
    player = openingPlayer;
    winner = '.';
    moves = 0;
}

// <editor-fold defaultstate="expanded" desc="Learn · F14">
/// ### Scope: which storage does this name mean?
///
/// A field belongs to the current object and can survive between method calls. A local variable
/// belongs to one call or block. A declaration includes a type; assignment to an existing name does
/// not declare another variable. A local name can shadow a field with the same spelling.
///
/// ```java
/// int volume = 7;
///
/// void whisper() {
///     int volume = 2;
///     IO.println(volume);
/// }
/// ```
///
/// `whisper()` prints two while the field stays seven: the local declaration created separate
/// storage. Predict a later field read; `this.volume` explicitly names the field when needed. Which
/// value should survive several calls, and which value belongs to only one temporary calculation?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Read this code">
/// An active turn and a round opener answer different questions. One changes after ordinary moves; the
/// other changes only when a new round begins.
///
/// For a separate quiz alternating team A/B, a local variable declared during restart forgets its
/// earlier value when the call ends. A field can remember the previous opener, but clearing that field
/// on every restart defeats the memory. Predict the opener sequence after rounds containing zero, one
/// and four moves; the lengths must not matter. Trace where a new round is requested and where a normal
/// move changes the turn.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// List what must reset and what must remember the previous round. Do not infer the opener from the
/// last disc.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// reset clears the round and chooses player. drop changes player during ordinary play, so that field
/// alone cannot remember who opened.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Keep a separate remembered opener, initially arranged so the first reset selects X. Alternate it
/// once per reset, copy it to player, and keep clearing board, winner and moves.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Make it yours">
/// Create a best-of-three match whose score survives rematches. Decide whether a separate new-match
/// action should also reset the opening-player schedule.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
char[][] board = {
".......".toCharArray(),
".......".toCharArray(),
".......".toCharArray(),
".......".toCharArray(),
".......".toCharArray(),
".......".toCharArray()
};
char openingPlayer = 'O';
char player = 'X';
char winner = '.';
int moves;

int count(int row, int column, int dr, int dc) {
    char mark = board[row][column];
    int total = 0;
    row += dr;
    column += dc;
    while (row >= 0 && row < 6 && column >= 0 && column < 7 && board[row][column] == mark) {
        total++;
        row += dr;
        column += dc;
    }
    return total;
}

boolean winsAt(int row, int column) {
    int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
    for (int[] direction : directions) {
        int dr = direction[0];
        int dc = direction[1];
        if (1 + count(row, column, dr, dc) + count(row, column, -dr, -dc) >= 4) {
            return true;
        }
    }
    return false;
}

/// drop(): Finds the lowest free cell in a chosen column and checks win or draw.
boolean drop(int column) {
    if (winner != '.' || column < 1 || column > 7 || board[0][column - 1] != '.') {
        return false;
    }
    column--;
    int row = 5;
    while (board[row][column] != '.') {
        row--;
    }
    board[row][column] = player;
    moves++;
    if (winsAt(row, column)) {
        winner = player;
    } else {
        if (moves == 42) {
            winner = 'D';
        } else {
            player = player == 'X' ? 'O' : 'X';
        }
    }
    return true;
}

/// display(): Prints the current board and a completed round's result.
void display() {
    IO.println("1 2 3 4 5 6 7");
    IO.println("Moves: " + moves);
    for (char[] row : board) {
        String line = "";
        for (char mark : row) {
            line += mark + " ";
        }
        IO.println(line);
    }
    if (winner != '.') {
        IO.println(winner == 'D' ? "Draw!" : winner + " wins!");
    }
}

void main() {
    reset();
    IO.println("CONNECT FOUR: two players; four connected pieces win.");
    IO.println("Drop a disc into a column; it falls to the lowest empty cell. Connect four in any direction.");
    IO.println("Example: 4 drops into the middle column. r or /r restarts; q or /q quits.");
    display();
    while (true) {
        String input = IO.readln(winner == '.' ? player + ": column 1-7, r restart, q quit: " : "Enter: replay, r restart, q quit: ");
        if (input == null) {
            return;
        }
        input = input.strip();
        if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("/q")) {
            return;
        }
        if (input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r") || (winner != '.' && input.isEmpty())) {
            reset();
            display();
            continue;
        }
        if (winner != '.') {
            IO.println("That round is over. Press Enter to play again, or q to quit.");
            continue;
        }
        try {
            if (drop(Integer.parseInt(input))) {
                display();
            } else {
                IO.println("Illegal move. Choose a column with space during an unfinished round.");
            }
        } catch (NumberFormatException exception) {
            IO.println("Enter a column number, r or q.");
        }
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
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `else` — The alternative when the preceding if condition is false.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
