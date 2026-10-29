// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import module java.base;
import javax.swing.*;
import java.awt.BorderLayout;
// </editor-fold>

class M04_CarrotInABox {
    /// # L07.04 · CarrotInABox
    ///
    /// One box hides a carrot, and one player gets a private peek. Bluff, bargain, and decide whether
    /// swapping boxes is worth the risk.
    ///
    /// **Your mission:** The final prize holder ignores the swap decision. Repair holderAfterSwap() so the
    /// reveal agrees with keeping or exchanging the boxes.
    ///
    /// **Today you learn:** Enum states: use a small set of meaningful named outcomes instead of unexplained
    /// numbers. holderAfterSwap() must return the correct Holder value for the final situation.
    ///
    /// From the course folder containing README.md:
    /// - Play: `java L07/M04_CarrotInABox.java`
    /// - Check this mission: `java L07/Check.java M04_CarrotInABox.java`
    ///
    /// the play command, click Look, bluff, then choose, let the first player peek privately, and let
    /// the other decide whether to keep or swap. After your repair: save the file, run the check
    /// command above, then play again.
    ///
    /// Need an explanation? Learn · F21 explains the Java skill; Read this code explains these lines.
    /// Hints 1 and 2 open separately; Repair outline reveals the direction.
    enum Holder {
        PLAYER_ONE, PLAYER_TWO
    }

    Holder holderAfterSwap(boolean swap) {
        return prizeA != swap ? Holder.PLAYER_ONE : Holder.PLAYER_TWO;
    }

    // <editor-fold defaultstate="expanded" desc="Learn · F21">
    /// ### Enum: one of the named choices
    ///
    /// An enum defines a finite set of values of one type. The type name and dot qualify a particular
    /// value. Unlike arbitrary text, a misspelled enum value is a compiler error. A variable holds one
    /// choice at a time; comparing enum values with `==` is appropriate.
    ///
    /// ```java
    /// enum Voice {
    ///     WHISPER, SING, SHOUT
    /// }
    ///
    /// Voice voice = Voice.SING;
    /// IO.println(voice == Voice.SHOUT);
    /// ```
    ///
    /// The declaration belongs beside methods; the statements can be read inside a method. The comparison
    /// prints false because SING and SHOUT are distinct values. Predict it after assigning SHOUT. Can
    /// this single variable simultaneously claim SING and SHOUT? Explain what contradictory combinations
    /// separate booleans might allow.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Read this code">
    /// ## Read these particular lines
    ///
    /// - `enum Holder` — Declares the complete set of possible named prize holders.
    /// - `PLAYER_ONE, PLAYER_TWO` — These are alternatives of the same Holder type, not arbitrary strings.
    /// - `boolean swap` — The caller passes whether the boxes were exchanged.
    /// - `prizeA ? Holder.PLAYER_ONE : Holder.PLAYER_TWO` — Chooses a named holder from the initial carrot
    ///   location; consider what the final choice changes.
    ///
    /// ### What the prepared part already does
    ///
    /// The dialogs handle private peeking and the final choice. choose() calls holderAfterSwap() and builds
    /// the public reveal; prizeA already records where the carrot started.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
    /// prizeA says where the carrot started; swap says whether boxes changed hands.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
    /// Keeping preserves the owner; swapping reverses it.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
    /// Player one holds the prize exactly when prizeA != swap.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
    boolean prizeA;
    String outcome;
    JFrame frame;
    JLabel status;

    /// reset(): Restores the starting Carrot in a Box round, clearing the state that belongs to the old
    /// round.
    void reset() {
        prizeA = new Random().nextBoolean();
        outcome = "";
    }

    /// choose(): Commits the final reveal using the Holder value.
    void choose(boolean swap) {
        require(outcome.isEmpty());
        Holder holder = holderAfterSwap(swap);
        outcome = "Player " + (holder == Holder.PLAYER_ONE ? 1 : 2) + " wins the carrot!";
    }

    /// refresh(): Copies the current model values into button labels, enabled states and status text.
    void refresh() {
        status.setText("<html>One box holds a carrot. A looks privately and may bluff aloud.<br>B chooses keep or swap. " + outcome + "</html>");
    }

    /// show(): Supplied later topic: Swing button callbacks invoke the model and refresh its visible
    /// state.
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
    /// - `+` — Adds numbers. When a String is involved, joins text instead.
    /// - `-` — Subtracts numbers; in front of one number, changes its sign.
    /// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
    ///   available.
    /// - `?` — In condition ? first : second, chooses the first value when the condition is true, otherwise
    ///   the second.
    /// - `:` — In for (int value : values), reads each element in turn. It also separates the two
    ///   alternatives of ? : or follows a switch label.
    /// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
    ///   count.
    /// - `new` — Creates a fresh value or object, such as a new array or window.
    /// - `return` — Ends this method call, optionally handing a result back to its caller.
    /// - `if` — Runs its body only when the condition in parentheses is true.
    /// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
    ///   later.
    /// - `::` — A method reference: supplies an action without calling it yet.
    /// - `this` — The current game object: this particular run and its stored state.
    /// - `enum` — A type whose values are a fixed named set, such as READY, PLAYING and FINISHED.
    /// - `null` — No object is present.
    /// - `throw` — Reports a failure by creating or passing an exception.
    /// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
    /// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
    /// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
    ///   later.
    // </editor-fold>

}
