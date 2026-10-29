/// # L07.07 · GoFish
///
/// Ask for ranks, draw when you miss, and collect four-card books. A completed book should remove its
/// cards from circulation.
///
/// **Your mission:** A book leaves a spare card in the hand. Repair collect() so turning a complete rank
/// into a book preserves the total number of cards.
///
/// **Today you learn:** Invariants: some quantities must remain consistent after every move. Cards in
/// hands and deck plus the cards represented by books must still account for the original deck.
///
/// Run the program and request a rank shown in your hand by typing its number from 2 to 14 and
/// pressing Enter.
void collect(int who) {
    for (int rank = 2; rank <= 14; rank++) {
        if (Collections.frequency(hands.get(who), rank) == 4) {
            for (int n = 0; n < 4; n++) {
                hands.get(who).remove(Integer.valueOf(rank));
            }
            books[who]++;
        }
    }
}

// <editor-fold defaultstate="expanded" desc="Learn · F25">
/// ### An invariant survives each action
///
/// An invariant is a promise about valid state. It is useful before and after a move, not merely when
/// the game ends. Think of moving glow stones between two jars:
///
/// ```java
/// int violet = 6;
/// int gold = 2;
/// int amount = 3;
/// if (amount >= 0 && amount <= violet) {
///     violet -= amount;
///     gold += amount;
/// }
/// IO.println(violet + gold);
/// ```
///
/// Trace: `(6, 2)` becomes `(3, 5)`, and the printed total is still 8. The two assignments belong to
/// one accepted action. Changing only one jar would silently create or destroy stones. A request for 9
/// fails the guard and changes neither jar.
///
/// Write the promise before writing the repair: a total stays constant, a resource stays non-negative,
/// or a shape stays outside a wall. Then list every field that one action must change together. Predict
/// the two jar values for amount 0 and amount 7. An invariant describes the intended world; it does not
/// forbid inventing a different, clearly stated rule in your own variant.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `Collections.frequency(hands.get(who), rank)` — Counts copies of one rank in the selected player’s
///   hand.
/// - `== 4` — A book is available only when all four cards of that rank are present.
/// - `remove(Integer.valueOf(rank))` — Removes a card by its value, rather than interpreting rank as a
///   list position.
/// - `books[who]++` — Records one new book, which now represents the removed cards.
///
/// ### What the prepared part already does
///
/// ask() handles transfers, drawing, and whether a turn continues, then calls collect(). The supplied
/// deck and hand logic are complete; your change concerns the conversion into books.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Count what a book represents
///
/// A book is one score entry but represents four removed cards. Compare physical cards in both hands
/// and the deck with the cards represented by books before and after collection. For a neutral
/// example, replacing six loose tokens by two sealed packs of three still represents six tokens. Pack
/// counts need the weight of their contents. What quantity does the game need to preserve? The
/// supplied `remove(Integer.valueOf(rank))` removes a card value, whereas removing an int index would
/// select a position.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Count hands + stock + four times all books before and after collection.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The loop currently removes only three matching cards.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Remove four copies, then increment books once; leave other ranks untouched.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Prepare an opening deal with two nearly complete books, accounting for all remaining cards in the
/// deck. Challenge a partner to predict which book appears first.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
List<List<Integer>> hands;
ArrayDeque<Integer> stock;
int[] books;
int player;
String outcome;
String message;
Random random = new Random();

void refill(int who) {
    if (hands.get(who).isEmpty() && !stock.isEmpty()) {
        hands.get(who).add(stock.remove());
    }
    collect(who);
}

void finish() {
    if (books[0] + books[1] == 13) {
        outcome = books[0] == books[1] ? "Draw" : books[0] > books[1] ? "You win" : "Bot wins";
    }
}

/// reset(): Restores the starting Go Fish round, clearing the state that belongs to the old round.
void reset() {
    var cards = new ArrayList<Integer>();
    for (int rank = 2; rank <= 14; rank++) {
        for (int i = 0; i < 4; i++) {
            cards.add(rank);
        }
    }
    Collections.shuffle(cards);
    stock = new ArrayDeque<>(cards);
    hands = List.of(new ArrayList<>(), new ArrayList<>());
    books = new int[2];
    player = 0;
    outcome = message = "";
    for (int n = 0; n < 7; n++) {
        for (int who = 0; who < 2; who++) {
            hands.get(who).add(stock.remove());
        }
    }
    collect(0);
    collect(1);
    refill(0);
}

/// ask(): Transfers matching ranks or draws one card, then applies collection and turn
/// continuation.
void ask(int who, int rank) {
    require(hands.get(who).contains(rank));
    int received = 0;
    while (hands.get(1 - who).remove(Integer.valueOf(rank))) {
        hands.get(who).add(rank);
        received++;
    }
    boolean again = received > 0;
    message = (who == 0 ? "You" : "Bot") + " asked for " + rank + " and received " + received + ".";
    if (!again && !stock.isEmpty()) {
        int card = stock.remove();
        hands.get(who).add(card);
        again = card == rank;
    }
    collect(who);
    finish();
    player = again ? who : 1 - who;
    refill(player);
    if (hands.get(player).isEmpty()) {
        player = 1 - player;
    }
}

/// command(): Validates one actual player command before applying its changes to this game.
void command(String input) {
    require(outcome.isEmpty());
    require(player == 0);
    ask(0, Integer.parseInt(input));
    while (outcome.isEmpty() && player == 1) {
        refill(1);
        if (hands.get(1).isEmpty()) {
            player = 0;
            break;
        }
        var hand = hands.get(1);
        ask(1, hand.get(random.nextInt(hand.size())));
    }
    refill(0);
    finish();
}

/// display(): Presents the current board and the legal input choices in the console.
void display() {
    IO.println(message);
    IO.println("Your ranks: " + hands.get(0) + " (2-14, Ace14)");
    IO.println("Books: you " + books[0] + ", bot " + books[1] + "; stock " + stock.size() + ", bot cards " + hands.get(1).size());
}

/// main(): Starts the real game loop: display, read a command, validate, replay or quit.
/// Prepared console flow: type a command and press Enter. A valid command redraws the board.
/// Rejected input leaves the display alone; after a round, Enter or /r starts again and /q quits.
void main() {
    reset();
    IO.println("Go Fish");
    IO.println("Ask the other player for a rank you already hold. Four equal ranks form a book; most books wins.");
    IO.println("Ask for a rank you hold: 2-14 (Ace14). Four equal ranks make a book; thirteen books ends play. /r restarts; /q quits.");
    IO.println("Example: enter 7 only if your displayed hand contains a seven. A failed request draws one card if available.");
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
            IO.println("Invalid move. " + "Ask for a rank you hold: 2-14 (Ace14). Four equal ranks make a book; thirteen books ends play. /r restarts; /q quits.");
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
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
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
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
