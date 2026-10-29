import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/// # Solved task · MemoryPairs
/// Lesson: 11.3 Rejection must preserve state — 03-invariants.html
///
/// Original assignment (this version is complete):
///
/// Flip two cards and remember where their partners hide. An X-ray can help a beginner, but deciding
/// exactly what it may reveal changes the challenge.
///
/// **Your mission:** Turn the all-seeing X-ray into a partner finder. With one card selected, X-ray
/// should reveal only cards with that same face, alongside normally visible cards. With no selection it
/// reveals nothing extra. Toggle it off or play another valid card to hide the extra information. It
/// must never mark a pair, spend an attempt or alter the deck.
///
/// Select one card and toggle X-ray. Notice which private faces leak. Turn it off, then try it before
/// selecting any card in a fresh round.

int visible(int index) {
    if (matched[index] || index == first || index == second) {
        return cards[index];
    }
    if (xray && first >= 0 && cards[index] == cards[first]) {
        return cards[index];
    }
    return 0;
}

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[] cards = new int[12];
boolean[] matched = new boolean[12];
int first = -1;
int second = -1;
boolean xray;
int attempts;
int pairs;
JButton[] buttons = new JButton[12];
JLabel status = new JLabel();

void reset(long seed) {
    var values = new ArrayList<Integer>();
    for (int value = 1; value <= 6; value++) {
        values.add(value);
        values.add(value);
    }
    Collections.shuffle(values, new Random(seed));
    for (int i = 0; i < cards.length; i++) {
        cards[i] = values.get(i);
    }
    matched = new boolean[12];
    first = -1;
    second = -1;
    xray = false;
    attempts = 0;
    pairs = 0;
}

boolean play(int index) {
    if (index < 0 || index >= cards.length || matched[index] || index == first || index == second || pairs == 6) {
        return false;
    }
    xray = false;
    if (second != -1) {
        first = -1;
        second = -1;
    }
    if (first == -1) {
        first = index;
    } else {
        second = index;
        attempts++;
        if (cards[first] == cards[second]) {
            matched[first] = true;
            matched[second] = true;
            pairs++;
            first = -1;
            second = -1;
        }
    }
    return true;
}

void refresh() {
    for (int i = 0; i < cards.length; i++) {
        int value = visible(i);
        buttons[i].setText(value == 0 ? "?" : Integer.toString(value));
        buttons[i].setEnabled(!matched[i] && pairs < 6);
    }
    String guidance = pairs == 6 ? "All pairs found!" : second != -1
            ? "Not a match: click another covered card." : first != -1
            ? "Choose a second card." : "Find matching pairs.";
    status.setText("<html>" + guidance + "<br>X-ray: " + (xray ? "on" : "off") + ". Pairs: " + pairs + "/6. Attempts: " + attempts + "</html>");
}

void show() {
    var frame = new JFrame("Memory Pairs");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var grid = new JPanel(new GridLayout(3, 4, 4, 4));
    for (int i = 0; i < cards.length; i++) {
        int index = i;
        var button = new JButton();
        buttons[i] = button;
        button.addActionListener(event -> {
            play(index);
            refresh();
        });
        grid.add(button);
    }
    var restart = new JButton("New cards");
    restart.addActionListener(event -> {
        reset(System.nanoTime());
        refresh();
    });
    frame.add(status, BorderLayout.NORTH);
    frame.add(grid, BorderLayout.CENTER);
    var peek = new JButton("X-ray on / off");
    peek.addActionListener(event -> {
        xray = !xray;
        refresh();
    });
    var actions = new JPanel();
    actions.add(peek);
    actions.add(restart);
    frame.add(actions, BorderLayout.SOUTH);
    refresh();
    frame.setSize(470, 350);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

void main() {
    reset(System.nanoTime());
    SwingUtilities.invokeLater(this::show);
}
// </editor-fold>
