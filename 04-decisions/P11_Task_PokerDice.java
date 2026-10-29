/// # Task · PokerDice
/// Lesson: 4.3 Combine rules — 03-logic.html
///
/// Roll five dice, keep promising values and reroll selected positions before banking the hand. Five
/// rounds let you compare safe small rewards with a chase for stronger combinations.
///
/// **Your mission · Build a fair referee:** The full-house referee has no working rule. Complete the
/// triple and pair
/// decisions, then combine them so only a triple together with a pair wins this category. A lone
/// triple keeps its smaller ordinary reward.
///
/// Run the program: dice are numbered and clicking selects them for reroll. In the opening
/// hand, keep the three twos, select dice 4 and 5 and press Reroll selected. Bank points keeps
/// the score and prepares the next round; Scoring help explains the hands.

boolean fullHouse(int largestGroup, int pairs) {
    boolean triple = false;
    boolean pair = false;
    return triple || pair;
}

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// After the standard full-house rule works, design a deliberately different house rule and explain
/// it before playing with a friend. For example, choose a different reward in points rather than
/// accidentally calling every triple a full house. Compare how the rule changes reroll choices.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[] dice;
int rolls;
int round;
int total;
String outcome;
Random random = new Random();

void deal() {
    dice = new int[5];
    for (int i = 0; i < 5; i++) {
        dice[i] = random.nextInt(1, 7);
    }
    rolls = 1;
}

void reset() {
    round = total = 0;
    outcome = "";
    deal();
    dice = new int[]{2, 2, 2, 3, 4};
}

int points() {
    int[] n = new int[7];
    for (int d : dice) {
        n[d]++;
    }
    int max = Arrays.stream(n).max().orElse(0);
    int pairs = 0;
    for (int v : n) {
        if (v == 2) {
            pairs++;
        }
    }
    boolean straight = true;
    for (int i = 2; i <= 5; i++) {
        straight &= n[i] == 1;
    }
    straight &= n[1] == 1 || n[6] == 1;
    return max == 5 ? 50 : max == 4 ? 40 : fullHouse(max, pairs) ? 35 : straight ? 30 : max == 3 ? 20 : pairs == 2 ? 10 : pairs == 1 ? 5 : 0;
}

void reroll(String[] p) {
    require(rolls < 3 && p.length > 1);
    var ids = new HashSet<Integer>();
    for (int i = 1; i < p.length; i++) {
        int n = Integer.parseInt(p[i]) - 1;
        require(n >= 0 && n < 5 && ids.add(n));
    }
    for (int n : ids) {
        dice[n] = random.nextInt(1, 7);
    }
    rolls++;
}

void command(String input) {
    require(outcome.isEmpty());
    if (input.equalsIgnoreCase("bank")) {
        total += points();
        round++;
        if (round == 5) {
            outcome = "Five rounds finished. Total: " + total;
        } else {
            deal();
        }
    } else {
        String[] p = input.split("\\s+");
        require(p[0].equalsIgnoreCase("roll"));
        reroll(p);
    }
}

void main() {
    reset();
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("Poker Dice needs a graphical display; the model remains headless-testable.");
        return;
    }
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame("Poker Dice");
        var dicePanel = new javax.swing.JPanel(new java.awt.GridLayout(1, 5, 8, 8));
        var picks = new javax.swing.JToggleButton[5];
        for (int i = 0; i < picks.length; i++) {
            picks[i] = new javax.swing.JToggleButton();
            picks[i].setPreferredSize(new java.awt.Dimension(95, 100));
            dicePanel.add(picks[i]);
        }
        var status = new javax.swing.JLabel();
        var feedback = new javax.swing.JLabel("Opening: keep the three 2s; try selecting dice 4 and 5, then Reroll selected.");
        var reroll = new javax.swing.JButton("Reroll selected");
        var bank = new javax.swing.JButton("Bank points / next round");
        var restart = new javax.swing.JButton("New game");
        var rules = new javax.swing.JButton("Scoring help");
        Runnable refresh = () -> {
            boolean selected = false;
            for (int i = 0; i < picks.length; i++) {
                picks[i].setText("<html><center>Die " + (i + 1) + "<br><span style='font-size:30px'>" + dice[i] + "</span><br>" + (picks[i].isSelected() ? "REROLL" : "keep") + "</center></html>");
                picks[i].setEnabled(outcome.isEmpty() && rolls < 3);
                selected |= picks[i].isSelected();
            }
            status.setText(outcome.isEmpty() ? "Round " + (round + 1) + "/5. Rerolls left: " + (3 - rolls) + ". This hand: " + points() + " points. Banked total: " + total : outcome);
            reroll.setEnabled(outcome.isEmpty() && rolls < 3 && selected);
            bank.setEnabled(outcome.isEmpty());
        };
        for (var pick : picks) pick.addActionListener(event -> refresh.run());
        reroll.addActionListener(event -> {
            String request = "roll";
            for (int i = 0; i < picks.length; i++) if (picks[i].isSelected()) request += " " + (i + 1);
            command(request);
            for (var pick : picks) pick.setSelected(false);
            feedback.setText(rolls < 3 ? "Select dice to change, or bank the current points." : "No rerolls left. Bank this hand to continue.");
            refresh.run();
        });
        bank.addActionListener(event -> {
            int earned = points();
            command("bank");
            for (var pick : picks) pick.setSelected(false);
            feedback.setText(outcome.isEmpty() ? "Banked " + earned + " points. A new hand is ready." : "Finished. Use New game to play again.");
            refresh.run();
        });
        restart.addActionListener(event -> { reset(); for (var pick : picks) pick.setSelected(false); feedback.setText("Select the dice you want to change; unselected dice stay."); refresh.run(); });
        rules.addActionListener(event -> javax.swing.JOptionPane.showMessageDialog(frame,
            "Only the highest matching hand scores.\nFive equal dice: 50. Four equal: 40.\nFull house: a triple AND a pair, 35.\nStraight 1-2-3-4-5 or 2-3-4-5-6: 30.\nThree equal: 20. Two pairs: 10. One pair: 5. Otherwise: 0.\nBank keeps those points and advances one of five rounds.", "Scoring help", javax.swing.JOptionPane.INFORMATION_MESSAGE));
        var buttons = new javax.swing.JPanel();
        buttons.add(reroll); buttons.add(bank); buttons.add(restart); buttons.add(rules);
        var content = new javax.swing.JPanel(new java.awt.BorderLayout(8, 8));
        content.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        content.add(new javax.swing.JLabel("Click a die to select it for reroll. You may reroll twice, or bank at any time."), java.awt.BorderLayout.NORTH);
        content.add(dicePanel);
        var bottom = new javax.swing.JPanel(new java.awt.GridLayout(3, 1));
        bottom.add(status); bottom.add(feedback); bottom.add(buttons);
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
