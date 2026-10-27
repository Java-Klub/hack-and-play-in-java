/// # L01.06 · HigherOrLower
///
/// Guess whether the next card will be higher or lower and try to build a streak before one wrong
/// prediction ends it. Equal cards give you another chance without a penalty.
///
/// **Your mission:** The cabinet celebrates after just one correct guess. Repair the shared target so
/// both the score display and the winning rule describe a ten-correct round.
///
/// **Today you learn:** Named values: target stores one fact that several parts can read. Changing one
/// initialization should keep the displayed goal and the actual goal in agreement.
///
/// Use Run File, enter h for higher or l for lower, and watch how many correct guesses the displayed
/// round requires.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Learn · F03 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
int target = 10;

// <editor-fold defaultstate="expanded" desc="Learn · F03">
/// A variable gives a stored value a name. Its type describes the values it can hold: `int` holds
/// whole numbers and `double` can hold a fractional part. Initialization supplies the first value;
/// later assignment replaces it.
///
/// These example statements belong inside a called method.
///
/// ```java
/// int charge = 4;
/// charge = charge + 3;
/// IO.println(charge);
/// ```
///
/// Predict the output. First `charge` stores four. On the second line, Java reads the old value to
/// calculate the right-hand expression, then stores seven under the same name. `=` is assignment, not
/// a mathematical claim that both sides remain equal forever. Other statements reading the name now
/// obtain seven. A name does not update itself: some executed assignment must change its value. A
/// `final` variable cannot be assigned again.
///
/// Now predict the final charge if its initialization uses six instead of four.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int` — The stored value is a whole number, suitable for counting correct guesses.
/// - `target` — The English name identifies the shared goal; it is different from correct, the score that
///   changes during play.
/// - `=` — Store the value on the right under the name on the left.
/// - `1` — The current numeric literal is one, which explains the too-short round.
/// - `;` — End the declaration and its initialization.
///
/// ### What the prepared part already does
///
/// reset builds and shuffles the deck. command applies a prediction and reads target when deciding a win;
/// display reads the same target for the score label. main supplies the input and restart loop.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="For this game">
/// This is your first game that waits for typed console input. After Run File, click in **Output at
/// the current prompt**, type `h` or `l`, and press **Enter** to send the whole line. Type into
/// Output, not into the Java editor. The printed `/r` restarts a round and `/q` ends the program.
/// Save your source change and start a new run to load it; restarting the already running round does
/// not reload edited Java. Random cards can end a round before ten correct guesses, so use Test File
/// as well as playing to check the shared target.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Find the one initialization of target near the top.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The supplied display and win comparison already read target.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Change int target = 1; to int target = 10;
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
ArrayDeque<Integer> deck;
int current;
int correct;
String outcome;
String message;

void reset() {
    var cards = new ArrayList<Integer>();
    for (int suit = 0; suit < 4; suit++) {
        for (int rank = 2; rank <= 14; rank++) {
            cards.add(rank);
        }
    }
    Collections.shuffle(cards);
    deck = new ArrayDeque<>(cards);
    current = deck.remove();
    correct = 0;
    outcome = message = "";
}

void command(String input) {
    require(outcome.isEmpty());
    require(input.matches("(?i)[hl]"));
    require(!deck.isEmpty());
    int next = deck.remove();
    message = "Next card: " + next;
    if (next == current) {
        message += "; tie, no penalty.";
    } else {
        if (input.equalsIgnoreCase("h") == (next > current)) {
            correct++;
            message += "; correct!";
        } else {
            outcome = "Wrong prediction. Final score: " + correct;
        }
    }
    current = next;
    if (correct == target) {
        outcome = target + " correct: you win!";
    }
    if (deck.isEmpty() && outcome.isEmpty()) {
        outcome = "Deck exhausted. Final score: " + correct;
    }
}

void display() {
    IO.println(message);
    IO.println("Current card " + current + " (Jack11 Queen12 King13 Ace14). Correct " + correct + "/" + target + "; deck " + deck.size());
}

void main() {
    reset();
    IO.println("Higher or Lower");
    IO.println("h predicts higher, l lower. Ace is high; equal rank is a free tie. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. h predicts higher, l lower. Ace is high; equal rank is a free tie. /r restarts; /q quits.");
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
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `/` — Divides numbers. Dividing two int values discards the fractional part.
/// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
///   direction. In List<String>, angle brackets describe the element type.
/// - `?` — In condition ? first : second, chooses the first value when the condition is true, otherwise
///   the second.
/// - `:` — In for (int value : values), reads each element in turn. It also separates the two
///   alternatives of ? : or follows a switch label.
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
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

