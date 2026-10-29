/// # Task · HigherOrLower
/// Lesson: 2.2 Change a stored value — 02-change.html
///
/// **Shorten the card challenge.** Choose Higher to predict a higher next card, or Lower for a
/// lower one. The game currently requires ten correct predictions; a wrong prediction ends the
/// round.
///
/// **Your task:** Search the supplied program for the one shared setting that determines how many
/// correct predictions win. Change it to `3`. It must affect both the actual win and the goal
/// shown in the window; keep the current score starting at zero.
///
/// **Result:** The window will show a goal of 3 and the third correct prediction will win. New
/// round will clear the score and keep the goal of 3.

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Make a sprint and a survival version for two players, then compare how often a round ends. Explain
/// why changing the score itself would not be a new goal.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Program · explore as needed">
int target = 10;

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
