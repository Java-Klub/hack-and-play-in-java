/// # L01.06 · HigherOrLower
///
/// **Your mission · Find the shared setting:** Turn the ten-point survival round into a three-correct
/// sprint. Find the setting that controls both the displayed goal and winning. Restart must keep your
/// new goal; do not replace the current score with a fixed number.
///
/// The starter works by its original rules. The check already expects your new goal.
///
/// Read the goal in the Java window. Choose Higher/Lower; New round starts again. After editing,
/// save, close and relaunch so both places use the new source.

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

// <editor-fold defaultstate="expanded" desc="Explore and experiment">
/// Separate a chosen goal from a changing score. A variable can be read by several actions. First
/// find where the window describes success, then where play decides it. Which stored fact do both
/// read? Keep the deck and predictions unchanged. Neutral example: two signs reading `int limit = 4;`
/// will both see a later chosen limit; editing only one printed sign cannot change a rule.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · search area">
/// Look among the stored game values before the window-building code.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a concrete route">
/// Search for the comparison that ends a successful round and follow its right-hand name.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Possible change · reveals the direction">
/// The shared field is target. Set its initial value to 3; leave correct and reset alone.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Make a sprint and a survival version for two players, then compare how often a round ends. Explain
/// why changing the score itself would not be a new goal.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Program · explore as needed">
int target = 3;


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
    message = "Next card: " + cardName(next);
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

// Prepared display names; ranks remain numeric in the game rules.
String cardName(int rank) {
    return switch (rank) {
        case 11 -> "Jack";
        case 12 -> "Queen";
        case 13 -> "King";
        case 14 -> "Ace";
        default -> Integer.toString(rank);
    };
}

void main() {
    reset();
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("Higher or Lower needs a graphical display; the model remains headless-testable.");
        return;
    }
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame("Higher or Lower");
        var card = new javax.swing.JLabel("", javax.swing.SwingConstants.CENTER);
        card.setOpaque(true); card.setBackground(java.awt.Color.WHITE);
        card.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.DARK_GRAY, 2));
        card.setPreferredSize(new java.awt.Dimension(190, 170));
        var score = new javax.swing.JLabel();
        var feedback = new javax.swing.JLabel("Choose whether the next card will be higher or lower.");
        var higher = new javax.swing.JButton("Higher");
        var lower = new javax.swing.JButton("Lower");
        var restart = new javax.swing.JButton("New round");
        Runnable refresh = () -> {
            card.setText("<html><center>Current card<br><span style='font-size:32px'>" + cardName(current) + "</span></center></html>");
            score.setText("Correct guesses: " + correct + " / " + target + "    Cards left: " + deck.size());
            feedback.setText(outcome.isEmpty() ? (message.isEmpty() ? "Choose Higher or Lower. Equal cards give another chance." : message) : outcome + " Use New round to play again.");
            higher.setEnabled(outcome.isEmpty()); lower.setEnabled(outcome.isEmpty());
        };
        higher.addActionListener(event -> { command("h"); refresh.run(); });
        lower.addActionListener(event -> { command("l"); refresh.run(); });
        restart.addActionListener(event -> { reset(); refresh.run(); });
        var buttons = new javax.swing.JPanel();
        buttons.add(higher); buttons.add(lower); buttons.add(restart);
        var content = new javax.swing.JPanel(new java.awt.BorderLayout(10, 10));
        content.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        content.add(new javax.swing.JLabel("Guess the next rank. Ace is highest; an equal rank does not count or lose."), java.awt.BorderLayout.NORTH);
        content.add(card);
        var bottom = new javax.swing.JPanel(new java.awt.GridLayout(3, 1));
        bottom.add(score); bottom.add(feedback); bottom.add(buttons);
        content.add(bottom, java.awt.BorderLayout.SOUTH);
        frame.add(content); frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        refresh.run(); frame.pack(); frame.setLocationByPlatform(true); frame.setVisible(true);
    });
}

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
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
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
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `IO` — Console input and output. println writes a line.
// </editor-fold>
