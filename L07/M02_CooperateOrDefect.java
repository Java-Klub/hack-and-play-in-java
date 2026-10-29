// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import javax.swing.*;
import java.awt.BorderLayout;
// </editor-fold>

/// # L07.02 · CooperateOrDefect
///
/// Privately promise cooperation or choose betrayal, then reveal both decisions. Five short rounds make a
/// good excuse to argue about trust.
///
/// **Your mission:** Build reward() for all four paired choices: mutual cooperation gives 3,
/// cooperating against betrayal gives 0, betraying cooperation gives 5, and mutual betrayal gives 1.
/// Encode the two choices and select a payoff with switch; the empty payoff rule currently gives
/// everyone zero.
///
/// **Today you learn:** Build a value selector from stated cases; trace its selector and returned value
/// separately from the supplied game state changes.
///
/// Run the program, click Private choices for next round, and pass the computer between the two
/// private choice dialogs before revealing both decisions.
int reward(boolean cooperate, boolean otherCooperates) {
    int choice = (cooperate ? 2 : 0) + (otherCooperates ? 1 : 0);
    return switch (choice) {
        case 3 -> 3;
        case 2 -> 0;
        case 1 -> 5;
        default -> 1;
    };
}

// <editor-fold defaultstate="collapsed" desc="Recall · F09">
/// ### A switch chooses a value
///
/// An expression has a value. A switch expression reads one selector and chooses the value after the
/// matching arrow. `return` then hands that value to the caller. Read this small sound selector before
/// touching the game:
///
/// ```java
/// String sound(int taps) {
///     return switch (taps) {
///         case 0 -> "silence";
///         case 1, 2 -> "tap";
///         default -> "drum";
///     };
/// }
/// ```
///
/// Trace `sound(2)`: the selector is 2, the grouped case includes 2, so the result is `"tap"`. No other
/// case runs. For 7, no listed case matches, so `default` provides `"drum"`. The semicolon closes the
/// whole switch expression. The returned values must fit the method's declared return type.
///
/// A switch can select a boolean, number, or record just as it selects a String here. It does not
/// automatically change a field: returning a proposed direction and storing that direction are separate
/// actions. Predict `sound(0)` and `sound(-1)`, and point to the case each uses.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read this part
///
/// The first boolean describes the player whose reward is being computed. The second describes the
/// opponent. Two booleans have four combinations. A ternary expression can contribute 2 for the first
/// choice and 1 for the second, giving distinct selectors 0–3. The prepared reveal calls reward once
/// from each player’s viewpoint.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Draw the four pairs before assigning any numbers.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Give the two choices different place values so no pair collides with another.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Encode true/false choices with weights 2 and 1, then map each resulting case to the advertised
/// reward.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Create a second payoff table in which mutual cooperation is more valuable than a lone betrayal.
/// Agree on the table first and compare five rounds of behaviour.
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
