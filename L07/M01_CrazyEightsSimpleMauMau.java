/// # L07.01 · CrazyEightsSimpleMauMau
///
/// Keep an eight for the moment no ordinary card can save you. Play against another person and choose the
/// suit after your wild escape.
///
/// **Your mission:** The wrong rank acts as the wild card. Repair legal(int card) so special and ordinary
/// cards follow the advertised rules.
///
/// **Today you learn:** Switch expressions: select one returned value from named cases and a fallback.
/// The special case must not replace the ordinary rank-or-suit condition.
///
/// From the course folder containing README.md:
/// - Play: `java L07/M01_CrazyEightsSimpleMauMau.java`
/// - Check this mission: `java L07/Check.java M01_CrazyEightsSimpleMauMau.java`
///
/// the play command, read your numbered hand, and enter p followed by a card index to play it,
/// adding a suit number when playing an eight. After your repair: save the file, run the check
/// command above, then play again.
///
/// Need an explanation? Learn · F09 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
boolean legal(int card) {
    return switch (rank(card)) {
        case 1 -> true;
        default -> rank(card) == rank(top) || card / 13 == suit;
    };
}

// <editor-fold defaultstate="expanded" desc="Learn · F09">
/// ### Switch: choose one value
///
/// A switch expression selects a result from its selector. Each arrow case handles a matching value;
/// it does not continue into the next case. `default` handles remaining values. The closing semicolon
/// belongs to the surrounding assignment or return statement.
///
/// ```java
/// String weather = "mist";
/// String colour = switch (weather) {
///     case "mist" -> "silver";
///     case "sun" -> "gold";
///     default -> "indigo";
/// };
/// IO.println(colour);
/// ```
///
/// The mist case selects `silver`. Predict the output for `"sun"`, then for `"snow"`. The selector is
/// compared with case labels; the text after the arrow is the result. Changing one special case
/// should leave the fallback's meaning intact.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `return switch (rank(card))` — The whole switch produces the boolean returned by legal().
/// - `case 1 -> true` — An arrow case supplies one result for one rank; check which rank deserves it.
/// - `default ->` — Handles all ranks without their own case.
/// - `||` — Either matching rank or matching suit is enough on the ordinary branch.
///
/// ### What the prepared part already does
///
/// rank() decodes a card’s rank. play() asks legal(), removes an accepted card, updates the chosen suit,
/// and checks for an empty hand; dealing and console input are already supplied.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// rank(card) converts the encoded card to a value 1-13.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// A switch expression returns one boolean value.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Use case 8 -> true; let default evaluate the supplied rank/suit condition.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
List<List<Integer>> hands;
ArrayDeque<Integer> stock;
int top;
int suit;
String outcome;
String message;

/// reset(): Restores the starting Crazy Eights / Simple Mau-Mau round, clearing the state that
/// belongs to the old round.
void reset() {
    var cards = new ArrayList<Integer>();
    for (int i = 0; i < 52; i++) {
        cards.add(i);
    }
    Collections.shuffle(cards);
    stock = new ArrayDeque<>(cards);
    hands = List.of(new ArrayList<>(), new ArrayList<>());
    for (int n = 0; n < 7; n++) {
        for (int who = 0; who < 2; who++) {
            hands.get(who).add(stock.remove());
        }
    }
    do {
        top = stock.remove();
        if (rank(top) == 8) {
            stock.add(top);
        }
    }
    while (rank(top) == 8);
    suit = top / 13;
    outcome = message = "";
}

int rank(int card) {
    return card % 13 + 1;
}

int firstLegal(int who) {
    for (int i = 0; i < hands.get(who).size(); i++) {
        if (legal(hands.get(who).get(i))) {
            return i;
        }
    }
    return -1;
}

/// play(): Removes a legal card, updates the top and chosen suit, then detects an empty hand.
void play(int who, int index, int chosenSuit) {
    require(index >= 0 && index < hands.get(who).size());
    int card = hands.get(who).get(index);
    require(legal(card) && chosenSuit >= 0 && chosenSuit < 4);
    hands.get(who).remove(index);
    top = card;
    suit = rank(card) == 8 ? chosenSuit : card / 13;
    if (hands.get(who).isEmpty()) {
        outcome = who == 0 ? "You win" : "Bot wins";
    }
}

/// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
void draw(int who) {
    require(firstLegal(who) < 0);
    if (!stock.isEmpty()) {
        hands.get(who).add(stock.remove());
    }
}

/// command(): Validates one actual player command before applying its changes to this game.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.toLowerCase(Locale.ROOT).split("\\s+");
    if (input.equalsIgnoreCase("d")) {
        draw(0);
    } else {
        require((p.length == 2 || p.length == 3) && p[0].equals("p"));
        int index = Integer.parseInt(p[1]) - 1;
        require(index >= 0 && index < hands.get(0).size());
        int card = hands.get(0).get(index);
        require(rank(card) != 8 || p.length == 3);
        play(0, index, p.length == 3 ? Integer.parseInt(p[2]) : card / 13);
    }
    if (!outcome.isEmpty()) {
        return;
    }
    if (stock.isEmpty() && firstLegal(0) < 0 && firstLegal(1) < 0) {
        outcome = "Draw: both blocked";
        return;
    }
    int index = firstLegal(1);
    if (index < 0) {
        draw(1);
    } else {
        play(1, index, hands.get(1).get(index) / 13);
    }
    if (outcome.isEmpty() && stock.isEmpty() && firstLegal(0) < 0 && firstLegal(1) < 0) {
        outcome = "Draw: both blocked";
    }
}

String card(int n) {
    return rank(n) + "/" + n / 13;
}

/// display(): Presents the current board and the legal input choices in the console.
void display() {
    IO.println("Suits 0=clubs 1=diamonds 2=hearts 3=spades. Ace=1, faces11-13.");
    IO.println("Top " + card(top) + "; active suit " + suit + "; bot cards " + hands.get(1).size() + "; stock " + stock.size());
    for (int i = 0; i < hands.get(0).size(); i++) {
        IO.println((i + 1) + ": " + card(hands.get(0).get(i)));
    }
}

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
/// Prepared console flow: type a command and press Enter. A valid command redraws the board.
/// Rejected input leaves the display alone; after a round, Enter or /r starts again and /q quits.
void main() {
    reset();
    IO.println("Crazy Eights / Simple Mau-Mau");
    IO.println("Empty your hand against the bot. Match the top card's rank or active suit; an eight can change the suit.");
    IO.println("p index plays a card; an eight needs p index suit(0-3). d draws one only when blocked, then passes. /r restarts; /q quits.");
    IO.println("Example: p 1 plays your first card if legal; p 1 2 plays an eight and chooses hearts. d draws only when blocked.");
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
            IO.println("Invalid move. " + "p index plays a card; an eight needs p index suit(0-3). d draws one only when blocked, then passes. /r restarts; /q quits.");
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
/// - `switch` — Selects one case from a set of alternatives. It can also produce a result.
/// - `case` — One alternative of a switch; the arrow points to what it does or returns.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
