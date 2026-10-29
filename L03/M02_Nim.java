/// # L03.02 · Nim
///
/// **Your mission · Build a tool for winning:** Observe the working bot, infer which pile it wants to
/// leave, and turn that idea into a coach for the human player. Typing ? must recommend 3 from seven
/// and 1 from eight. Check also asks the coach about an empty pile: return 0, since no move exists
/// there. A request only advises: it must not take stones or pass the turn. The bot stays unchanged.
///
/// The starter works by its original rules. The check already expects your new goal.
///
/// Play with 1–3, ask ? for advice, r restarts and q quits. The starter already lets you play against
/// the bot; its unfinished coach says 0.

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

// <editor-fold defaultstate="expanded" desc="Explore and experiment">
/// Before reading the strategy, observe two different bot choices. Then find the calculation that
/// chooses a move and distinguish choosing from applying it. Your coach receives the pile to analyse
/// as a parameter, so it can advise about another position without changing the actual game. Neutral
/// example: a weather helper can predict for a supplied temperature without changing today’s stored
/// temperature. Reuse the idea, not a read of an unrelated global value.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · search area">
/// Search the input branch for ?. Which returned value does it print? Compare that helper with the
/// bot’s decision.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a concrete route">
/// advice(pile) must analyse pile; botMove reads stones. Their branching idea can be shared without
/// moving anything.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Possible change · reveals the direction">
/// Use pile % 4: return 0 for zero, 1 for a positive multiple of four, and the remainder otherwise.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Give a friend a deliberately weak coach while keeping the bot strong. Explain a pile where the
/// weak advice loses an advantage. Restore the strong coach and compare the same position.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Program · explore as needed">
int advice(int pile) {
    if (pile == 0) return 0;
    int remainder = pile % 4;
    if (remainder == 0) return 1;
    return remainder;
}

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
    IO.println("You play first against the bot. ?: coach; r restarts; q quits.");
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
        if (input.equals("?")) {
            IO.println("Coach suggests: " + advice(stones));
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
