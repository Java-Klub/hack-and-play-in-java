/// # L03.01 · Pig
///
/// Two players race to bank 50 points. Each extra roll can increase the current turn’s reward, but
/// rolling one loses those unbanked points and passes the die.
///
/// **Your mission · Rebuild a risky turn:** The die is rolling but neither branch updates the turn.
/// Complete roll: a 1
/// clears turnTotal and switches player; 2..6 add die to turnTotal and keep the player. Preserve the
/// supplied invalid-die and finished-game guard.
///
/// **Today you learn:** Branch effects: an if/else can choose between changing several values and
/// changing just one. Trace the turn total and player separately for a losing roll and a scoring roll.
///
/// Run the program and enter d to roll or b to bank your turn; watch what happens when a 1 or a
/// 6 appears.
boolean roll(int die) {
    if (winner != -1 || die < 1 || die > 6) {
        return false;
    }
    if (die == 1) {
        turnTotal = 0;
        player = 1 - player;
    } else {
        turnTotal += die;
    }
    return true;
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
/// ## One choice, several effects
///
/// The first guard is supplied: it rejects impossible dice and an already finished game. Your if
/// chooses between losing and scoring. Assignment changes stored game values; return true reports
/// that the roll was accepted. The dice generator and bank action remain prepared. winner uses -1 for
/// an unfinished game; do not replace that convention.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Follow the values · predict a case">
/// Suppose player is 0 and turnTotal is 9. Losing the turn needs two changes: total becomes zero and
/// player becomes 1. A scoring roll of four makes the total 13 and leaves player at 0. The expression
/// 1 - player switches 0 and 1. A branch may contain several assignments; running only half its
/// effects leaves inconsistent game state.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Agree on a house rule before playing: perhaps a different die face loses the unbanked turn. Change
/// that rule deliberately and test both its losing and scoring branch. Compare whether players bank
/// earlier. The standard check continues to expect that one is the losing face.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// List the values that change for a losing turn and for a scoring turn.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Use 1 - player only in the losing branch.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// For die == 1 assign turnTotal = 0 and player = 1 - player; otherwise add die to turnTotal.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[] scores = {0, 0};
int player;
int turnTotal;
int winner = -1;

void reset() {
    scores[0] = 0;
    scores[1] = 0;
    player = 0;
    turnTotal = 0;
    winner = -1;
}

boolean bank() {
    if (winner != -1 || turnTotal == 0) {
        return false;
    }
    scores[player] += turnTotal;
    turnTotal = 0;
    if (scores[player] >= 50) {
        winner = player;
    } else {
        player = 1 - player;
    }
    return true;
}

void main() {
    reset();
    var random = new Random();
    IO.println("PIG: two players race to 50 banked points.");
    IO.println("d rolls, b banks. A 1 should lose this turn's points; banking keeps them and passes play. /r restarts; /q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            IO.println("Scores: P1 " + scores[0] + ", P2 " + scores[1] + "; turn total " + turnTotal);
            if (winner != -1) IO.println("Player " + (winner + 1) + " wins!");
            showState = false;
        }
        String input = IO.readln(winner == -1 ? "Player " + (player + 1) + ": d roll, b bank, /r, /q: " : "Enter or /r: new round; /q: quit: ");
        if (input == null) return;
        input = input.strip().toLowerCase(Locale.ROOT);
        if (input.equals("q") || input.equals("/q")) return;
        if (input.equals("r") || input.equals("/r") || (winner != -1 && input.isEmpty())) {
            reset(); showState = true; continue;
        }
        if (winner != -1) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        if (input.equals("d")) {
            int before = player;
            int die = random.nextInt(1, 7);
            roll(die);
            IO.println("Player " + (before + 1) + " rolled " + die + ". " + (player != before ? "Turn passed; the turn total is now " + turnTotal + "." : "The same player continues."));
            showState = true;
        } else if (input.equals("b") && bank()) {
            IO.println("Turn points banked.");
            showState = true;
        } else {
            IO.println("Use d to roll, b to bank a positive turn total, /r or /q.");
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
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `else` — The alternative when the preceding if condition is false.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
