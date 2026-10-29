/// # L06.04 · MiniCrossword
///
/// Solve a tiny crossword one clue at a time, and erase an answer if you want to rethink it. The
/// celebration should wait until every clue is filled.
///
/// **Your mission:** The counter keeps adding yesterday’s count to today’s count and can celebrate after
/// only three answers. Repair the recount inside command().
///
/// **Today you learn:** Scope and state lifetime: a count describing one scan needs a fresh lifetime for
/// that scan. Distinguish this temporary total from the solved clues stored between commands.
///
/// From the course folder containing README.md:
/// - Play: `java L06/M04_MiniCrossword.java`
/// - Check this mission: `java L06/Check.java M04_MiniCrossword.java`
///
/// the play command, enter 1 CAT and press Enter for your first answer, then read the remaining
/// numbered clues. After your repair: save the file, run the check command above, then play again.
///
/// Need an explanation? Recall · F14 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.toUpperCase(Locale.ROOT).split("\\s+");
    require(p.length == 2);
    boolean erase = p[0].equals("ERASE");
    int n = Integer.parseInt(erase ? p[1] : p[0]) - 1;
    require(n >= 0 && n < 6);
    if (erase) {
        solved[n] = false;
    } else {
        require(p[1].equals(answers[n]));
        solved[n] = true;
    }
    int count = 0;
    for (boolean value : solved) {
        if (value) {
            count++;
        }
    }
    if (count == 6) {
        outcome = "All clues solved!";
    }
}

// <editor-fold defaultstate="collapsed" desc="Recall · F14">
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

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `boolean erase` — A temporary yes/no value describing only this command.
/// - `solved[n] = true` — Stores that clue’s solved state so later commands can still see it.
/// - `for (boolean value : solved)` — Scans the currently saved states rather than the history of
///   commands.
/// - `count++` — Adds one solved clue to the total for this scan.
///
/// ### What the prepared part already does
///
/// The front of command() already parses an answer or erase request and updates solved. grid() projects
/// those saved answers into the visible crossword; your recount decides when outcome announces
/// completion.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// solved[] is persistent state; count is only a temporary summary.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// With one, then two, then three answers, an old accumulator reaches 1+2+3.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Declare int count = 0 inside command() just before its traversal; remove the field.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String[] answers = {"CAT", "ARE", "TEN", "CAT", "ARE", "TEN"};
String[] clues = {"Across row1: pet that meows", "Across row2: plural of is", "Across row3: number after nine", "Down col1: pet that meows", "Down col2: plural of is", "Down col3: number after nine"};
boolean[] solved;
String outcome;

/// reset(): Restores the starting Mini Crossword round, clearing the state that belongs to the old
/// round.
void reset() {
    solved = new boolean[6];
    outcome = "";
}

/// grid(): Projects solved across/down words into a fresh three-by-three display buffer.
char[][] grid() {
    char[][] b = new char[3][3];
    for (char[] row : b) {
        Arrays.fill(row, '.');
    }
    for (int n = 0; n < 6; n++) {
        if (solved[n]) {
            for (int i = 0; i < 3; i++) {
                b[n < 3 ? n : i][n < 3 ? i : n - 3] = answers[n].charAt(i);
            }
        }
    }
    return b;
}

/// display(): Presents the current board and the legal input choices in the console.
void display() {
    for (char[] row : grid()) {
        IO.println(new String(row));
    }
    for (int i = 0; i < 6; i++) {
        IO.println((i + 1) + " " + clues[i] + (solved[i] ? " [done]" : ""));
    }
}

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
/// Prepared console flow: type a command and press Enter. A valid command redraws the board.
/// Rejected input leaves the display alone; after a round, Enter or /r starts again and /q quits.
void main() {
    reset();
    IO.println("Mini Crossword");
    IO.println("Solve six clues crossing in a tiny three-by-three crossword. A correct answer reveals its shared letters.");
    IO.println("Enter clueNumber answer, or erase clueNumber. A tiny authored 3x3 crossword. /r restarts; /q quits.");
    IO.println("Example: 1 CAT answers clue 1. erase 1 clears that answer so you can inspect the crossing again.");
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
            IO.println("Invalid move. " + "Enter clueNumber answer, or erase clueNumber. A tiny authored 3x3 crossword. /r restarts; /q quits.");
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
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
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
/// - `else` — The alternative when the preceding if condition is false.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
