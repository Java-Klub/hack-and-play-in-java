/// # L03.02 · Pig
///
/// Two players race to bank 50 points. Each extra roll can increase the current turn’s reward, but
/// rolling one loses those unbanked points and passes the die.
///
/// **Your mission:** The starter swaps the effects of one and six. Repair which branch roll selects
/// without changing the prepared effects of the two branches.
///
/// **Today you learn:** Branch effects: an if/else can choose between changing several values and
/// changing just one. Trace the turn total and player separately for a losing roll and a scoring roll.
///
/// Use Run File and enter d to roll or b to bank your turn; watch what happens when a 1 or a 6 appears.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F07 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
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

// <editor-fold defaultstate="collapsed" desc="Recall · F07">
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
/// - `boolean roll(int die)` — Receive one die value and return whether the roll was accepted.
/// - `winner != -1 || die < 1 || die > 6` — A supplied guard rejects an already finished round or an
///   impossible die value; != means not equal and || joins alternatives.
/// - `if (die == 6)` — This currently decides which valid die face selects the turn-loss branch.
/// - `turnTotal = 0;` — Replace the unbanked total with zero.
/// - `player = 1 - player;` — Switch between player indices zero and one.
/// - `turnTotal += die;` — Add the die to the previous total instead of replacing that total.
/// - `return true;` — Report that the valid roll has been processed.
///
/// ### What the prepared part already does
///
/// bank stores the current turn’s points and checks the race to 50. main rolls a random die and calls
/// roll, then prints the state and handles the next command. The unfinished turn’s points and the banked
/// scores remain separate.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Trace die = 1 through the current condition.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The two branch bodies are ready; their selecting condition is wrong.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// The if comparison must be die == 1.
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
    Random random = new Random();
    IO.println("PIG: two players race to 50 banked points.");
    IO.println("Roll 1: lose this turn's points. Bank: keep them and pass the die.");
    while (true) {
        IO.println("Scores: P1 " + scores[0] + ", P2 " + scores[1] + "; turn total " + turnTotal);
        if (winner != -1) {
            IO.println("Player " + (winner + 1) + " wins!");
        }
        String input = IO.readln(winner == -1 ? "Player " + (player + 1) + ": d roll, b bank, r restart, q quit: " : "r replay, q quit: ");
        if (input == null || input.trim().equalsIgnoreCase("q")) {
            return;
        }
        input = input.trim().toLowerCase(Locale.ROOT);
        if (input.equals("r")) {
            reset();
        } else if (input.equals("d") && winner == -1) {
            int die = random.nextInt(1, 7);
            roll(die);
            IO.println(die == 1 ? "Rolled 1. Turn lost!" : "Rolled " + die + ".");
        } else if (input.equals("b") && bank()) {
            IO.println("Points banked.");
        } else {
            IO.println("Use d to roll, b to bank a positive turn total, r or q.");
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
/// - `'` — Single quotes enclose one character, such as a board marker.
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
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

