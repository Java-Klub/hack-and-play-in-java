import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/// # Vyřešený úkol · MemoryPairs
/// Lekce: 11.3 Odmítnutí nesmí poškodit stav — 03-invariants.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Otáčej dvojice a pamatuj si, kde se skrývají jejich partneři. X-ray (rentgen) může pomoci
/// začátečníkovi; rozhodnutí, co přesně odhalí, ale mění obtížnost hry.
///
/// **Tvoje mise:** Z vševědoucího X-ray (rentgenu) udělej hledání partnera. Při vybrané první kartě
/// odhalí jen karty se stejnou hodnotou vedle běžně viditelných karet. Bez výběru neodhalí nic navíc.
/// Vypnutí nebo další platně zahraná karta pomoc skryje. Nesmí samo označit pár, přidat pokus ani
/// změnit balíček.
///
/// Vyber jednu kartu a zapni X-ray. Všimni si úniku skrytých hodnot. Vypni ho a v novém kole jej zkus
/// ještě před výběrem karty.

int visible(int index) {
    if (matched[index] || index == first || index == second) {
        return cards[index];
    }
    if (xray && first >= 0 && cards[index] == cards[first]) {
        return cards[index];
    }
    return 0;
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
