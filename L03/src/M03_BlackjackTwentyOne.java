/// # L03.03 · BlackjackTwentyOne
///
/// Try to finish closer to 21 than the dealer without going over. You can take another card or stand, and
/// aces may count as one or eleven.
///
/// **Your mission:** The known 20-versus-19 opening incorrectly awards the dealer a win. Complete
/// compareTotals so bust, player win, draw and dealer win are separated in the right priority.
///
/// **Today you learn:** Ordered branches: a first matching branch can settle the result before later
/// possibilities are considered. A player total above 21 must be handled before deciding which total is
/// larger.
///
/// Use Run File and enter s to stand on the training total 20, then compare the reported result against
/// the dealer’s 19.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F07 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
String compareTotals(int human, int house) {
    if (human > 21) {
        return "You bust";
    } else {
        return "Dealer wins";
    }
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
/// - `String compareTotals(int human, int house)` — Receive two whole-number totals and return one text
///   result; the comma keeps the two inputs separate.
/// - `human > 21` — A numeric condition for the player exceeding the limit; > does not include equality.
/// - `if` — Start a conditional choice; the following parentheses contain the question.
/// - `return "You bust";` — Return this literal result and finish the current call.
/// - `else` — The current alternative covers everything that did not satisfy the first condition.
/// - `house` — The other total is available as an input even though the incomplete body does not use it
///   yet.
///
/// ### What the prepared part already does
///
/// value computes the supplied ace-aware hand totals. finish lets the dealer draw and then asks
/// compareTotals for the result. reset prepares a known first comparison and a real remaining deck; input
/// and display are ready.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="For this game">
/// Return one of these exact English result strings; the prepared caller prints it:
///
/// - Player above 21: `You bust`, including when both totals exceed 21.
/// - Otherwise, dealer above 21 or player above dealer: `You win`.
/// - Otherwise, equal totals: `Draw`.
/// - Otherwise: `Dealer wins`.
///
/// These are priorities, not four independent announcements. For example, player 22 versus dealer 25
/// is still `You bust`; comparing the larger number first would get this wrong. Predict results for
/// 20 versus 22, 18 versus 18, and 18 versus 20 before writing branches. Keep the original
/// capitalization and spaces: a returned message is part of this method's contract. Do not move card
/// drawing or printing into compareTotals.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Check player bust before comparing which number is larger.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The win condition is house > 21 OR human > house.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Use else if for the win and draw cases before the final else.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
ArrayDeque<Integer> deck;
List<Integer> hand;
List<Integer> dealer;
String outcome;

void reset() {
    var cards = new ArrayList<Integer>();
    for (int suit = 0; suit < 4; suit++) {
        for (int rank = 1; rank <= 13; rank++) {
            cards.add(rank);
        }
    }
    cards.remove(Integer.valueOf(10));
    cards.remove(Integer.valueOf(10));
    cards.remove(Integer.valueOf(10));
    cards.remove(Integer.valueOf(9));
    Collections.shuffle(cards);
    deck = new ArrayDeque<>(cards);
    hand = new ArrayList<>(List.of(10, 10));
    dealer = new ArrayList<>(List.of(10, 9));
    outcome = "";
}

int value(List<Integer> cards) {
    int sum = 0;
    int aces = 0;
    for (int rank : cards) {
        sum += rank == 1 ? 11 : Math.min(rank, 10);
        if (rank == 1) {
            aces++;
        }
    }
    while (sum > 21 && aces > 0) {
        sum -= 10;
        aces--;
    }
    return sum;
}

void finish() {
    while (value(dealer) < 17) {
        dealer.add(deck.remove());
    }
    int h = value(hand);
    int d = value(dealer);
    outcome = compareTotals(h, d);
}

void command(String input) {
    require(outcome.isEmpty());
    if (input.equalsIgnoreCase("h")) {
        hand.add(deck.remove());
        if (value(hand) > 21) {
            outcome = "You bust";
        }
    } else {
        require(input.equalsIgnoreCase("s"));
        finish();
    }
}

void display() {
    IO.println("Ranks: 1=Ace (1/11), 11-13=faces (10). Dealer stands on soft 17.");
    IO.println("Your hand " + hand + " = " + value(hand));
    IO.println(outcome.isEmpty() ? "Dealer: " + dealer.getFirst() + " + hidden" : "Dealer: " + dealer + " = " + value(dealer));
}

void main() {
    reset();
    IO.println("Blackjack / Twenty-One");
    IO.println("h hits, s stands. One hand, no bets or splitting. /r deals again; /q quits.");
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
            IO.println("Invalid move or command. h hits, s stands. One hand, no bets or splitting. /r deals again; /q quits.");
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
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
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
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

