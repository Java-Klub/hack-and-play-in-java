/// # L03.06 · Chopsticks
///
/// Tap an opponent’s hand with one of your living hands and add your fingers to it. Five or more fingers
/// knock a hand out; eliminate both opposing hands to win.
///
/// **Your mission:** A knocked-out hand can still participate in a strike. Repair handsAlive so invalid
/// hand numbers and either zero-finger hand are rejected before the game changes.
///
/// **Today you learn:** Several guards in order: first establish that a place exists, then inspect
/// whether it is usable. A rejected strike must not move fingers or pass the turn.
///
/// Use Run File and enter 1 1 three times to knock out a hand, then try a fourth 1 1 and watch whether an
/// invalid strike changes the player.
/// After your repair: Save → Test File → play again.
boolean handsAlive(int own, int opposing) {
    if (own < 0 || own >= 2 || opposing < 0 || opposing >= 2) {
        return false;
    }
    if (hands[player][own] < 0 || hands[1 - player][opposing] < 0) {
        return false;
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `own` — The selected attacking-hand index, already converted to zero or one.
/// - `opposing` — The selected defending-hand index for the other player.
/// - `own < 0 || own >= 2` — These alternatives reject a number outside the two available hand slots.
/// - `hands[player][own]` — Read fingers from the supplied two-level table: first choose the player, then
///   their hand.
/// - `< 0` — This asks whether a value is negative; a knocked-out hand actually has zero fingers.
/// - `return false;` — End the check as rejected as soon as one guard finds a problem.
/// - `return true;` — Only a call passing both guards reaches the final accepted result.
///
/// ### What the prepared part already does
///
/// command converts the typed hand numbers and asks handsAlive before applying a strike. The supplied
/// body adds fingers, eliminates hands, changes turns and detects repeated positions; main displays both
/// players’ hands.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. Zero, not a negative number, represents a dead hand.
/// 2. Keep the index guard before reading either hand.
/// 3. Reject when either stored hand value == 0.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[][] hands;
int player;
Set<String> seen;
String outcome;

void reset() {
    hands = new int[][]{{1, 1}, {1, 1}};
    player = 0;
    seen = new HashSet<>();
    outcome = "";
    seen.add(key());
}

String key() {
    return Arrays.deepToString(hands) + player;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 2);
    int a = Integer.parseInt(p[0]) - 1;
    int b = Integer.parseInt(p[1]) - 1;
    require(handsAlive(a, b));
    hands[1 - player][b] += hands[player][a];
    if (hands[1 - player][b] >= 5) {
        hands[1 - player][b] = 0;
    }
    if (hands[1 - player][0] + hands[1 - player][1] == 0) {
        outcome = "Player " + (player + 1) + " wins";
    }
    player = 1 - player;
    if (outcome.isEmpty() && !seen.add(key())) {
        outcome = "Draw: repeated state";
    }
}

void display() {
    IO.println("Player " + (player + 1) + "; P1 " + Arrays.toString(hands[0]) + " P2 " + Arrays.toString(hands[1]));
}

void main() {
    reset();
    IO.println("Chopsticks");
    IO.println("Enter own hand 1-2 and opposing hand 1-2. Five or more kills a hand; no splitting. /r restarts; /q quits.");
    while (true) {
        display();
        if (!outcome.isEmpty()) {
            IO.println(outcome);
        }
        String input = IO.readln("> ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r")) {
            reset();
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. /r restarts; /q quits.");
            continue;
        }
        try {
            command(input.strip());
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move or command. Enter own hand 1-2 and opposing hand 1-2. Five or more kills a hand; no splitting. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

/// Prepared entry points: main, reset, command.
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
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `/` — Divides numbers. Dividing two int values discards the fractional part.
/// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
///   direction. In List<String>, angle brackets describe the element type.
/// - `:` — In for (int value : values), reads each element in turn. It also separates the two
///   alternatives of ? : or follows a switch label.
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Set` — Stores distinct values; the same value is present at most once.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

