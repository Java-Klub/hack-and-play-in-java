import module java.base;
class M06_HigherOrLower {
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
    /// From the course folder containing README.md:
    /// - Play: `java L01/M06_HigherOrLower.java`
    /// - Check this mission: `java L01/Check.java M06_HigherOrLower.java`
    ///
    /// Run the play command above, read Current card and click Higher or Lower to predict the next
    /// rank. The result and target stay visible; New round restarts play.
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
    /// reset builds and shuffles the deck. command applies a prediction and reads target when deciding
    /// a win; The prepared window reads the same target for the score label. main supplies the buttons
    /// and restart.
    // </editor-fold>

    // <editor-fold defaultstate="expanded" desc="For this game">
    /// The default main provides cards and buttons directly in Java. Jack is 11, Queen 12, King 13 and
    /// Ace 14. An equal card does not count a correct guess or lose. Both the target display and win
    /// rule read the same target. After changing target, save, close the old window and run the play
    /// command again; New round does not reload edited Java.
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

}
