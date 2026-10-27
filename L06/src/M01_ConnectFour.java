/// # L06.01 · ConnectFour
///
/// Drop discs into columns and race a friend to connect four. A replay should give you a fresh board and
/// a fresh round.
///
/// **Your mission:** Restart clears the discs but keeps the old move count, which can spoil a later draw.
/// Repair the counter reset inside reset().
///
/// **Today you learn:** Scope and state lifetime: a new local variable can hide a longer-lived field with
/// the same name. Identify which moves belongs to the whole round.
///
/// Run File, type 4 and press Enter to drop a disc in the middle column, then alternate column choices
/// with a friend.
/// After your repair: Save → Test File → play again.
void reset() {
    for (char[] row : board) {
        Arrays.fill(row, '.');
    }
    player = 'X';
    winner = '.';
    int moves = 0;
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `void reset()` — A method that changes existing game state and returns no value.
/// - `Arrays.fill(row, '.')` — The supplied call clears every cell in this board row.
/// - `int moves = 0` — Declares a local variable; the declaration affects which moves name is being
///   reset.
/// - `player = 'X'` — Assignment to the existing player field, without declaring another variable.
///
/// ### What the prepared part already does
///
/// drop() places a disc in the lowest free cell and updates the shared move count. The input loop handles
/// r and calls reset(); board clearing and win detection are already present.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. Find the field declared outside reset().
/// 2. int moves inside a method creates a different local variable.
/// 3. Assign moves = 0 without redeclaring its type.
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

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
void main() {
    reset();
    IO.println("CONNECT FOUR: two players; four connected pieces win.");
    while (true) {
        IO.println("1 2 3 4 5 6 7");
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
        String input = IO.readln(winner == '.' ? player + ": column 1-7, r restart, q quit: " : "r replay, q quit: ");
        if (input == null || input.trim().equalsIgnoreCase("q")) {
            return;
        }
        input = input.trim();
        if (input.equalsIgnoreCase("r")) {
            reset();
            continue;
        }
        try {
            if (!drop(Integer.parseInt(input))) {
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
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

