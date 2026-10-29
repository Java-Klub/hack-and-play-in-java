import javax.swing.*;
import java.awt.BorderLayout;

/// # Úkol · CarrotInABox
/// Lekce: 9.5 Pojmenuj povolené možnosti — 05-enums.html
///
/// Jedna krabice skrývá mrkev a jeden hráč smí tajně nahlédnout. Blafuj, vyjednávej a rozhodni, zda stojí
/// za to krabice vyměnit.
///
/// **Tvoje mise:** Konečný majitel ceny ignoruje rozhodnutí o výměně. Oprav holderAfterSwap(), aby
/// odhalení odpovídalo ponechání či výměně krabic.
///
/// Spusť program, klikni na Look, bluff, then choose, nech prvního hráče tajně nahlédnout a
/// druhého rozhodnout o ponechání či výměně.

enum Holder {
    PLAYER_ONE, PLAYER_TWO
}

Holder holderAfterSwap(boolean swap) {
    return prizeA ? Holder.PLAYER_ONE : Holder.PLAYER_TWO;
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
boolean prizeA;
String outcome;
JFrame frame;
JLabel status;

void reset() {
    prizeA = new Random().nextBoolean();
    outcome = "";
}

void choose(boolean swap) {
    require(outcome.isEmpty());
    Holder holder = holderAfterSwap(swap);
    outcome = "Player " + (holder == Holder.PLAYER_ONE ? 1 : 2) + " wins the carrot!";
}

void refresh() {
    status.setText("<html>One box holds a carrot. A looks privately and may bluff aloud.<br>B chooses keep or swap. " + outcome + "</html>");
}

void show() {
    frame = new JFrame("Carrot in a Box");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    status = new JLabel();
    var play = new JButton("Look, bluff, then choose");
    play.addActionListener((e) -> {
        if (!outcome.isEmpty()) {
            return;
        }
        if (JOptionPane.showConfirmDialog(frame, "Player B looks away. Pass to A.", "Handover", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }
        JOptionPane.showMessageDialog(frame, "Player A: your box " + (prizeA ? "HAS" : "does NOT have") + " the carrot.");
        JOptionPane.showMessageDialog(frame, "A may now bluff aloud. Then pass to B; A keeps their box closed.");
        int answer = JOptionPane.showOptionDialog(frame, "Player B: keep your box or swap with A?", "B chooses", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Keep", "Swap"}, "Keep");
        if (answer >= 0) {
            choose(answer == 1);
            refresh();
            play.setEnabled(false);
        }
    });
    var restart = new JButton("Restart");
    restart.addActionListener((e) -> {
        reset();
        play.setEnabled(true);
        refresh();
    });
    frame.add(play, BorderLayout.NORTH);
    frame.add(status, BorderLayout.CENTER);
    frame.add(restart, BorderLayout.SOUTH);
    frame.setSize(580, 180);
    refresh();
    frame.setVisible(true);
}

void main() {
    reset();
    SwingUtilities.invokeLater(this::show);
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
