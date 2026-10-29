// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import javax.swing.*;
import java.awt.BorderLayout;
// </editor-fold>

/// # L07.02 · CooperateOrDefect
///
/// Privately promise cooperation or choose betrayal, then reveal both decisions. Five short rounds make a
/// good excuse to argue about trust.
///
/// **Your mission:** Two cooperating players receive the wrong reward. Repair reward() without disturbing
/// the outcomes of the other paired choices.
///
/// **Today you learn:** Switch expressions: encode two yes/no choices into one small case number, then
/// return the corresponding payoff. Read each case from one player’s viewpoint.
///
/// Run the program, click Private choices for next round, and pass the computer between the two
/// private choice dialogs before revealing both decisions.
int reward(boolean cooperate, boolean otherCooperates) {
    int choice = (cooperate ? 2 : 0) + (otherCooperates ? 1 : 0);
    return switch (choice) {
        case 3 -> 0;
        case 2 -> 0;
        case 1 -> 5;
        default -> 1;
    };
}

// <editor-fold defaultstate="collapsed" desc="Recall · F09">
/// ### Switch: choose one value
///
/// A switch expression selects a result from its selector. Each arrow case handles a matching value;
/// it does not continue into the next case. `default` handles remaining values. The closing semicolon
/// belongs to the surrounding assignment or return statement.
///
/// ```java
/// String weather = "mist";
/// String colour = switch (weather) {
///     case "mist" -> "silver";
///     case "sun" -> "gold";
///     default -> "indigo";
/// };
/// IO.println(colour);
/// ```
///
/// The mist case selects `silver`. Predict the output for `"sun"`, then for `"snow"`. The selector is
/// compared with case labels; the text after the arrow is the result. Changing one special case
/// should leave the fallback's meaning intact.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `cooperate ? 2 : 0` — Encodes this player’s choice as one contribution to the case number.
/// - `otherCooperates ? 1 : 0` — Uses a different weight for the other player, keeping the four pairs
///   distinct.
/// - `case 3 -> 0` — A case maps one encoded pair to this player’s reward; this result needs
///   investigation.
/// - `default -> 1` — The remaining encoded pair has its own fallback reward.
///
/// ### What the prepared part already does
///
/// The private dialogs collect choices separately. play() calls reward() from both viewpoints, updates
/// totals, and reveals the round summary; you only repair the shared scoring table.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### The selector is not the reward
///
/// The two boolean choices contribute separate amounts to a case number. Write the four input pairs
/// and calculate their selector numbers before assigning any reward. The game's displayed contract
/// gives each cooperator three points when both cooperate, one each when both defect, and five versus
/// zero when only one defects. `reward` returns the viewpoint of its first argument; the caller also
/// asks from the other viewpoint. Predict both calls for one mixed-choice round.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// choice uses two bits: your cooperation contributes 2, the other player contributes 1.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// List the four possible sums 0,1,2,3 before editing.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// The branch for 3 means both cooperated and must return 3.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[] scores;
int round;
String outcome;
String message;
JFrame frame;
JLabel status;

/// reset(): Restores the starting Cooperate or Defect round, clearing the state that belongs to the
/// old round.
void reset() {
    scores = new int[2];
    round = 0;
    outcome = "";
    message = "Pass privately for each choice; reveal only after both choose.";
}

/// play(): Scores both viewpoints, advances one of five rounds and builds the revealed summary.
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

/// refresh(): Copies the current model values into button labels, enabled states and status text.
void refresh() {
    status.setText("<html>Round " + round + "/5; P1 " + scores[0] + ", P2 " + scores[1] + "<br>" + message + "<br>" + outcome + "<br>CC=3/3, DD=1/1, lone defector=5, other=0.</html>");
}

/// show(): Supplied later topic: Swing button callbacks invoke the model and refresh its visible
/// state.
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

/// main(): Starts the model, then schedules the supplied window on Swing’s event thread.
void main() {
    reset();
    SwingUtilities.invokeLater(this::show);
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
/// - `import` — Makes library names available in this file; it does not run the library.
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
/// - `!=` — Checks that two values differ.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
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
/// - `switch` — Selects one case from a set of alternatives. It can also produce a result.
/// - `case` — One alternative of a switch; the arrow points to what it does or returns.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `null` — No object is present.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
