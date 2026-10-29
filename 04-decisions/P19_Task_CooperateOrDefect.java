import javax.swing.*;
import java.awt.BorderLayout;

/// # Vyřešený úkol · CooperateOrDefect
/// Lekce: 4.5 Vyber hodnotu pomocí switch — 05-switch.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Tajně slib spolupráci, nebo zvol zradu, a potom oba rozhodnutí odhalte. Pět krátkých kol je dobrá
/// záminka k debatě o důvěře.
///
/// **Tvoje mise:** Vytvoř reward() pro čtyři dvojice voleb: oboustranná spolupráce dává 3, spolupráce
/// proti zradě 0, zrada proti spolupráci 5 a oboustranná zrada 1. Zakóduj dvojici a vyber payoff
/// (odměnu) pomocí switch; prázdné pravidlo nyní všem dává nulu.
///
/// Spusť program, klikni na Private choices for next round a před odhalením obou voleb si mezi
/// soukromými dialogy předejte počítač.

int reward(boolean cooperate, boolean otherCooperates) {
    int choice = (cooperate ? 2 : 0) + (otherCooperates ? 1 : 0);
    return switch (choice) {
        case 3 -> 3;
        case 2 -> 0;
        case 1 -> 5;
        default -> 1;
    };
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[] scores;
int round;
String outcome;
String message;
JFrame frame;
JLabel status;

void reset() {
    scores = new int[2];
    round = 0;
    outcome = "";
    message = "Pass privately for each choice; reveal only after both choose.";
}

void play(boolean a, boolean b) {
    require(outcome.isEmpty());
    scores[0] += reward(a, b);
    scores[1] += reward(b, a);
    round++;
    message = "P1 " + (a ? "cooperated" : "defected") + ", P2 " + (b ? "cooperated" : "defected");
    if (round == 5) {
        outcome = scores[0] == scores[1] ? "Draw" : "Player " + (scores[0] > scores[1] ? 1 : 2) + " wins";
    }
}

int privateChoice(int who) {
    int handover = JOptionPane.showConfirmDialog(frame, "Pass to Player " + who + ". Other player looks away. Ready?", "Private handover", JOptionPane.OK_CANCEL_OPTION);
    if (handover != JOptionPane.OK_OPTION) {
        return -1;
    }
    return JOptionPane.showOptionDialog(frame, "Player " + who + ": choose privately", "Private choice", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Cooperate", "Defect"}, "Cooperate");
}

void refresh() {
    status.setText("<html>Round " + round + "/5; P1 " + scores[0] + ", P2 " + scores[1] + "<br>" + message + "<br>" + outcome + "<br>CC=3/3, DD=1/1, lone defector=5, other=0.</html>");
}

void show() {
    frame = new JFrame("Cooperate or Defect");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    status = new JLabel();
    var play = new JButton("Private choices for next round");
    play.addActionListener((e) -> {
        if (!outcome.isEmpty()) {
            return;
        }
        int a = privateChoice(1);
        if (a < 0) {
            return;
        }
        int b = privateChoice(2);
        if (b < 0) {
            return;
        }
        play(a == 0, b == 0);
        refresh();
        play.setEnabled(outcome.isEmpty());
    });
    var restart = new JButton("Restart");
    restart.addActionListener((e) -> {
        reset();
        play.setEnabled(true);
        refresh();
    });
    frame.add(status, BorderLayout.CENTER);
    frame.add(play, BorderLayout.NORTH);
    frame.add(restart, BorderLayout.SOUTH);
    frame.setSize(540, 220);
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
