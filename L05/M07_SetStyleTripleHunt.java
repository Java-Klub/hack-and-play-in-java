/// # L05.07 · SetStyleTripleHunt
///
/// **Find the overlooked triple.** Submitting a correct triple already works. A different
/// decision is faulty: whether any valid triple remains on the table. The `more` command and the
/// end-of-game decision use that answer.
///
/// **Your task:** Fix the search in `hasSet` below. Examine every combination of three distinct
/// cards, including combinations using the last card. The supplied `triple` already checks a
/// chosen triple.
///
/// **Result:** `more` adds cards only when no valid triple remains. Check also tries a table
/// whose only valid triple needs its last card; submitting an ordinary triple alone does not
/// demonstrate this repair.

boolean hasSet() {
    for (int a = 0; a < table.size(); a++) {
        for (int b = a + 1; b < table.size(); b++) {
            for (int c = b + 1; c < table.size() - 1; c++) {
                if (triple(table.get(a), table.get(b), table.get(c))) {
                    return true;
                }
            }
        }
    }
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F17">
/// ### Traversal: one question at a time
///
/// A traversal visits values; a search may stop at the first match, while a count must examine every
/// relevant value. In a for-each loop, the name before the colon receives one value from the sequence
/// after it. Keep a running count outside the repeated body so earlier matches survive.
///
/// ```java
/// int[] pulses = {2, 0, 4};
/// int strong = 0;
/// for (int pulse : pulses) {
///     if (pulse > 1) {
///         strong++;
///     }
/// }
/// IO.println(strong);
/// ```
///
/// The result is two because pulses two and four satisfy the condition. If the question were merely
/// whether any strong pulse exists, when could you already answer? What should the count be for an
/// empty array?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int b = a + 1` — Starts the second card after the first, so a card is not reused.
/// - `int c = b + 1` — Starts the third card after the second, giving each combination one ordering.
/// - `table.size()` — The number of cards currently available, not the final valid index.
/// - `table.get(c)` — Reads the card at candidate index c for the supplied triple query.
///
/// ### What the prepared part already does
///
/// triple() already compares the cards’ encoded attributes. hasSet() chooses candidates for it; the
/// supplied game uses this result when a player claims that no set remains.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### The table's reading API
///
/// `table` is a supplied List: `size()` gives its current number of cards, and `get(index)` reads one
/// card without removing it. Its positions start at zero like an array. The three loop indices
/// deliberately increase: a precedes b and b precedes c, so one card is never selected twice. Predict
/// how the last table position could enter a triple; keep the supplied `triple` query responsible for
/// judging properties.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Use a < b < c so a card is never reused.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The largest allowed c is table.size()-1.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// The c loop condition should compare c with table.size(), not table.size()-1.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Make it yours">
/// Design a stricter set variant: an accepted triple must have at least two attributes that differ
/// across all three cards. Use a counter for those attributes. Find one original set your rule rejects
/// and one it retains, then explain the new visual clue.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
ArrayDeque<Integer> deck;
List<Integer> table;
int score;
String outcome;

/// reset(): Restores the starting Set-style Triple Hunt round, clearing the state that belongs to
/// the old round.
void reset() {
    var cards = new ArrayList<Integer>();
    for (int i = 0; i < 81; i++) {
        cards.add(i);
    }
    Collections.shuffle(cards);
    deck = new ArrayDeque<>(cards);
    table = new ArrayList<>();
    score = 0;
    outcome = "";
    deal(12);
}

void deal(int count) {
    while (count-- > 0 && !deck.isEmpty()) {
        table.add(deck.remove());
    }
}

/// triple(): Supplied base-three digits check that each card attribute is all-equal or
/// all-different.
boolean triple(int a, int b, int c) {
    for (int i = 0; i < 4; i++) {
        if ((a % 3 + b % 3 + c % 3) % 3 != 0) {
            return false;
        }
        a /= 3;
        b /= 3;
        c /= 3;
    }
    return true;
}

/// command(): Validates one actual player command before applying its changes to this game.
void command(String input) {
    require(outcome.isEmpty());
    if (input.equalsIgnoreCase("more")) {
        require(!hasSet() && !deck.isEmpty());
        deal(3);
    } else {
        String[] p = input.split("\\s+");
        require(p.length == 3);
        int[] ids = new int[3];
        for (int i = 0; i < 3; i++) {
            ids[i] = Integer.parseInt(p[i]) - 1;
            require(ids[i] >= 0 && ids[i] < table.size());
        }
        Arrays.sort(ids);
        require(ids[0] != ids[1] && ids[1] != ids[2] && triple(table.get(ids[0]), table.get(ids[1]), table.get(ids[2])));
        for (int i = 2; i >= 0; i--) {
            table.remove(ids[i]);
        }
        score++;
        deal(Math.max(0, 12 - table.size()));
    }
    if (deck.isEmpty() && !hasSet()) {
        outcome = "Finished: " + score + " triples found.";
    }
}

String code(int card) {
    String s = "";
    for (int i = 0; i < 4; i++) {
        s = (card % 3) + s;
        card /= 3;
    }
    return s;
}

/// display(): Presents the current board and the legal input choices in the console.
void display() {
    IO.println("Four attributes, values 0/1/2: each attribute must be all equal or all different.");
    for (int i = 0; i < table.size(); i++) {
        IO.println((i + 1) + ": " + code(table.get(i)));
    }
    IO.println("Sets " + score + "; deck " + deck.size() + "; " + (hasSet() ? "A set exists" : "No set: use more if deck remains"));
}

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
/// Prepared console flow: type a command and press Enter. A valid command redraws the board.
/// Rejected input leaves the display alone; after a round, Enter or /r starts again and /q quits.
void main() {
    reset();
    IO.println("Set-style Triple Hunt");
    IO.println("Find three cards whose four attributes are each all equal or all different.");
    IO.println("Enter three card numbers, or more when no set exists. /r restarts; /q quits.");
    IO.println("Example format: 1 2 3 selects three displayed card numbers. Use more only when no triple exists.");
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
            IO.println("Invalid move. " + "Enter three card numbers, or more when no set exists. /r restarts; /q quits.");
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
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `!=` — Checks that two values differ.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `%` — The remainder after division; useful for repeated positions or turns.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `/` — Divides numbers. Dividing two int values discards the fractional part.
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
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
