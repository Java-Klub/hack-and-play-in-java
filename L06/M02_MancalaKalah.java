/// # L06.02 · MancalaKalah
///
/// Sow stones around the pits, catch an extra turn, and try to collect more than your opponent. Then
/// trade tactics in a fresh round.
///
/// **Your mission:** Restart can leave the previous player on turn. Repair reset() so the newly dealt
/// pits and active player belong to the same fresh round.
///
/// **Today you learn:** Scope and state lifetime: a local player disappears when reset() returns, while
/// the game’s player field remains. Reset the state actually read by commands.
///
/// Run the program and enter one of your pit numbers from 1 to 6 to sow its stones around the
/// board.
void reset() {
    pits = new int[14];
    Arrays.fill(pits, 4);
    pits[6] = pits[13] = 0;
    player = 0;
    outcome = "";
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
/// - `new int[14]` — Creates fourteen zero-filled pit/store slots.
/// - `Arrays.fill(pits, 4)` — Places four stones in each slot before the stores are cleared.
/// - `pits[6] = pits[13] = 0` — The same zero value is assigned to both store positions.
/// - `int player = 0` — This declaration introduces a local player instead of automatically updating the
///   field.
///
/// ### What the prepared part already does
///
/// command() already sows stones, skips the opponent’s store, awards captures, and settles the final
/// stones. side() counts a player’s ordinary pits; neither mechanism needs changing for a fair restart.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The field survives between calls to command().
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// A local int player disappears when reset() returns.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Assign to the existing player; leave pit initialization unchanged.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[] pits;
int player;
String outcome;

/// side(): Sums the six ordinary pits belonging to one player.
int side(int who) {
    int n = 0;
    for (int i = who * 7; i < who * 7 + 6; i++) {
        n += pits[i];
    }
    return n;
}

/// command(): Sows stones, skips the enemy store, captures opposite stones and settles an empty
/// side.
void command(String input) {
    require(outcome.isEmpty());
    int chosen = Integer.parseInt(input) - 1;
    require(chosen >= 0 && chosen < 6);
    int at = player * 7 + chosen;
    require(pits[at] > 0);
    int stones = pits[at];
    pits[at] = 0;
    while (stones > 0) {
        at = (at + 1) % 14;
        if (at == (player == 0 ? 13 : 6)) {
            continue;
        }
        pits[at]++;
        stones--;
    }
    int store = player == 0 ? 6 : 13;
    if (at >= player * 7 && at < player * 7 + 6 && pits[at] == 1 && pits[12 - at] > 0) {
        pits[store] += pits[12 - at] + 1;
        pits[12 - at] = pits[at] = 0;
    }
    if (side(0) == 0 || side(1) == 0) {
        for (int who = 0; who < 2; who++) {
            for (int i = who * 7; i < who * 7 + 6; i++) {
                pits[who == 0 ? 6 : 13] += pits[i];
                pits[i] = 0;
            }
        }
        outcome = pits[6] == pits[13] ? "Draw" : "Player " + (pits[6] > pits[13] ? 1 : 2) + " wins";
    } else {
        if (at != store) {
            player = 1 - player;
        }
    }
}

/// display(): Presents the current board and the legal input choices in the console.
void display() {
    IO.println("Player " + (player + 1) + " chooses own pit 1-6 (left-to-right in its list).");
    IO.println("P1 " + Arrays.toString(Arrays.copyOfRange(pits, 0, 6)) + " store " + pits[6]);
    IO.println("P2 " + Arrays.toString(Arrays.copyOfRange(pits, 7, 13)) + " store " + pits[13]);
    IO.println("Opposite pairs: P1 pit1/P2 pit6 ... P1 pit6/P2 pit1.");
}

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
/// Prepared console flow: type a command and press Enter. A valid command redraws the board.
/// Rejected input leaves the display alone; after a round, Enter or /r starts again and /q quits.
void main() {
    reset();
    IO.println("Mancala / Kalah");
    IO.println("Collect more stones in your store. Sow one by one, skip the enemy store, and play again when you finish in your store.");
    IO.println("Kalah: enter own pit 1-6. Sow forward; skip enemy store; finish in own store plays again. /r restarts; /q quits.");
    IO.println("Example: 3 selects your third pit. The two printed pit lists each run from their own 1 to 6.");
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
            IO.println("Invalid move. " + "Kalah: enter own pit 1-6. Sow forward; skip enemy store; finish in own store plays again. /r restarts; /q quits.");
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
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `!=` — Checks that two values differ.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `%` — The remainder after division; useful for repeated positions or turns.
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
