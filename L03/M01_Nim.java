/// # L03.01 · Nim
///
/// Take one, two or three stones from a shared pile and try to take the last one before the bot does.
/// Watch how leaving a particular pile size changes the bot’s next choice.
///
/// **Your mission:** The bot takes one even when another legal choice is useful. Repair the two paths in
/// botMove while keeping the empty-pile case safe.
///
/// **Today you learn:** If/else branching: one condition chooses which result is returned. Compare piles
/// of seven and eight stones: their remainders differ, so the selected branch should matter.
///
/// Run the program and take 2 stones; compare the bot’s next move from 19 stones, then use r to
/// try the same opening again.
int botMove() {
    if (stones == 0) {
        return 0;
    }
    int remainder = stones % 4;
    if (remainder == 0) {
        return 1;
    } else {
        return remainder;
    }
}

// <editor-fold defaultstate="expanded" desc="Learn · F07">
/// An `if` asks a boolean question. Its body runs only when that question is true. An `else if` asks
/// another question only after earlier ones failed; the final `else` covers everything remaining.
///
/// These example statements belong inside a called method.
///
/// ```java
/// int temperature = 10;
/// if (temperature < 0) {
///     IO.println("Frozen");
/// } else if (temperature < 20) {
///     IO.println("Cool");
/// } else {
///     IO.println("Warm");
/// }
/// ```
///
/// With ten, this prints `Cool`: the first comparison fails and the second succeeds. Predict the
/// messages for `-3` and `20`. Each run prints exactly one message. Order matters: a broad earlier
/// branch can prevent a later one from being reached. Inside a calculation, a branch may `return` a
/// result instead of printing. That immediately finishes this method call; it does not merely leave
/// the branch.
///
/// Now predict the message at exactly zero degrees.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int botMove()` — A no-input calculation returning one whole-number move; it reads the supplied
///   stones value.
/// - `if (stones == 0)` — Evaluate the numeric equality inside parentheses and take this branch only if
///   the pile is empty.
/// - `return 0;` — Immediately finish the calculation with no move; later statements in this call do not
///   run.
/// - `stones % 4` — The remainder after dividing the pile size by four; for example, seven leaves
///   remainder three.
/// - `if (remainder == 0)` — Select the fallback branch for a multiple of four.
/// - `else` — The alternative body runs when that if condition is false; its braces group that body.
///
/// ### What the prepared part already does
///
/// take checks and applies moves, changes turns and records the winner. main calls botMove on the bot
/// turn and supplies the pile display, typed moves, restart and quit. The bot calculation must return a
/// move, not remove stones itself.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="For this game">
/// The last stone wins; each turn removes one, two or three. Think backwards: a player facing four
/// stones cannot prevent the other player from taking the rest on the next turn. The same idea
/// repeats in groups of four: after an opponent takes some, a reply can make the pair of turns remove
/// four together. For a non-multiple of four, aim to leave the opponent a multiple of four. From an
/// exact positive multiple, no move guarantees a win against perfect replies; this bot uses the legal
/// fallback of one. An empty pile has no move. Translate that rule into the two existing branches
/// without removing stones inside botMove.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Trace seven stones: the remainder is three.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The else branch should differ from the zero-remainder branch.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// In else, return remainder instead of 1.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int stones = 21;
boolean humanTurn = true;
int winner;

void reset() {
    stones = 21;
    humanTurn = true;
    winner = 0;
}

boolean take(int amount) {
    if (winner != 0 || amount < 1 || amount > 3 || amount > stones) {
        return false;
    }
    stones -= amount;
    if (stones == 0) {
        winner = humanTurn ? 1 : 2;
    } else {
        humanTurn = !humanTurn;
    }
    return true;
}

void main() {
    IO.println("NIM: take 1-3 stones. Taking the last stone wins.");
    IO.println("You play first against the bot. r restarts; q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            if (winner == 0 && !humanTurn) {
                int amount = botMove();
                IO.println("Bot takes " + amount + ".");
                take(amount);
            }
            IO.println("Stones: " + stones + " " + "|".repeat(stones));
            if (winner != 0) {
                IO.println(winner == 1 ? "You win!" : "Bot wins!");
            }
            showState = false;
        }
        String input = IO.readln(winner == 0 ? "Take 1-3, r, q: " : "Enter or /r: new round; /q: quit: ");
        if (input == null || (input.trim().equalsIgnoreCase("q") || input.trim().equalsIgnoreCase("/q"))) {
            return;
        }
        input = input.trim();
        if ((input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r") || (winner != 0 && input.isBlank()))) {
            reset();
            showState = true;
            continue;
        }
        if (winner != 0) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            if (take(Integer.parseInt(input))) {
                showState = true;
            } else {
                IO.println("Illegal move. Take 1-3 available stones during your turn.");
            }
        } catch (NumberFormatException exception) {
            IO.println("Enter a number, r or q.");
        }
    }
}

/// Prepared entry points: main, reset.
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
/// - `%` — The remainder after division; useful for repeated positions or turns.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
///   direction. In List<String>, angle brackets describe the element type.
/// - `?` — In condition ? first : second, chooses the first value when the condition is true, otherwise
///   the second.
/// - `:` — In for (int value : values), reads each element in turn. It also separates the two
///   alternatives of ? : or follows a switch label.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `else` — The alternative when the preceding if condition is false.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
