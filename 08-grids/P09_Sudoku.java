// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
// </editor-fold>

/// # 08.09 · Sudoku
///
/// **Stop a forbidden number.** Select 1 and click the empty square immediately right of the
/// top-left 1. The starter wrongly accepts it. In this 4×4 Sudoku, each number 1–4 must appear
/// only once in each row, column and 2×2 block.
///
/// **Your task:** Add guards to `place` below. Reject rule violations, invalid coordinates or
/// values, changes to fixed clues, and changes after completion. The supplied `valid` already
/// checks a proposed choice; coordinates must be valid before you index the board.
///
/// **Result:** Another 1 is refused without changing the board; a correct 2 in that square
/// succeeds. Fixed clues stay protected even when `place` is called directly.

boolean place(int row, int col, int value) {
    if (complete()) return false;
    if (!valid(row, col, value)) return false;
    if (clues[row][col] != 0) return false;
    board[row][col] = value;
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F08">
/// A guard rejects a request before protected work happens. Read the rejected cases first, then ask
/// what remains allowed. Short-circuit evaluation matters: in `A || B`, Java skips B when A is
/// already true.
///
/// ```java
/// boolean useSlot(int slot) {
///     if (slot < 0 || slot >= 4) {
///         return false;
///     }
///     IO.println("Using slot " + slot);
///     return true;
/// }
/// ```
///
/// Predict what is printed and returned for `-1`, `3` and `4`. Only three reaches the print. A
/// rejection must happen before any state change that should not occur. A board access may itself
/// need a range guard before it is safe to evaluate. `return false` ends this method call; it does
/// not automatically undo work already performed.
///
/// `useSlot(3)` passes three into `slot`; `boolean` declares a true/false result. Now predict slot
/// zero.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Permission before the write
///
/// row and col are zero-based coordinates; value is the selected number, with zero used for erasing.
/// complete and valid are supplied questions. clues keeps the original fixed cells. board is the
/// editable board. Read clues only after valid has ruled out bad coordinates. The assignment and
/// return true happen only if no guard rejects the choice.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Follow the values · predict a case">
/// valid already checks coordinates, value range and number conflicts. It is safe to ask valid about
/// an outside square; it is not safe to index clues first. A printed clue has a nonzero clues entry,
/// even if the current board changes later. Keep all rejection paths before the single assignment to
/// board.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Solve the repaired puzzle, then erase selected numbers in the existing reset board to make a small
/// puzzle for a friend. Keep row, column and block consistency. Ask whether the puzzle has an obvious
/// first move; do not assume every edited board has a unique solution.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Three independent reasons can prevent the one write.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The supplied valid function must run before your clues lookup.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Keep complete(); reject !valid(row, col, value); then reject clues[row][col] != 0; leave the final
/// assignment after all guards.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int[][] clues = {{1, 0, 0, 4}, {0, 4, 1, 0}, {2, 0, 4, 0}, {0, 3, 0, 1}};
int[][] board = new int[4][4];
JButton[][] buttons = new JButton[4][4];
JLabel status = new JLabel();
JComboBox<Integer> number = new JComboBox<>(new Integer[]{0, 1, 2, 3, 4});

void reset() {
    for (int row = 0; row < 4; row++) board[row] = clues[row].clone();
}

boolean valid(int row, int col, int value) {
    if (row < 0 || row >= 4 || col < 0 || col >= 4 || value < 0 || value > 4) return false;
    if (value == 0) return true;
    for (int i = 0; i < 4; i++) {
        if (i != col && board[row][i] == value) return false;
        if (i != row && board[i][col] == value) return false;
    }
    int top = row / 2 * 2;
    int left = col / 2 * 2;
    for (int r = top; r < top + 2; r++) {
        for (int c = left; c < left + 2; c++) {
            if ((r != row || c != col) && board[r][c] == value) return false;
        }
    }
    return true;
}

boolean complete() {
    for (int row = 0; row < 4; row++) {
        for (int col = 0; col < 4; col++) {
            if (board[row][col] == 0 || !valid(row, col, board[row][col])) return false;
        }
    }
    return true;
}

void refresh() {
    boolean finished = complete();
    for (int row = 0; row < 4; row++) {
        for (int col = 0; col < 4; col++) {
            buttons[row][col].setText(board[row][col] == 0 ? "." : Integer.toString(board[row][col]));
            buttons[row][col].setEnabled(clues[row][col] == 0 && !finished);
        }
    }
    status.setText(finished ? "Solved! Restart for another round." : "Rows, columns and 2x2 boxes need 1, 2, 3, 4.");
}

void show() {
    var frame = new JFrame("Mini Sudoku");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var grid = new JPanel(new GridLayout(4, 4, 4, 4));
    for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
            int row = r;
            int col = c;
            var button = new JButton();
            buttons[r][c] = button;
            button.addActionListener(event -> {
                boolean accepted = place(row, col, (Integer) number.getSelectedItem());
                refresh();
                if (!accepted) status.setText("Move rejected. Check the row, column and box; the starter guard may also need repair.");
            });
            grid.add(button);
        }
    }
    var controls = new JPanel();
    var restart = new JButton("Restart");
    restart.addActionListener(event -> {
        reset();
        refresh();
    });
    number.setSelectedItem(1);
    controls.add(new JLabel("Number (0 clears):"));
    controls.add(number);
    controls.add(restart);
    frame.add(status, BorderLayout.NORTH);
    frame.add(grid, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    refresh();
    frame.setSize(480, 390);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

void main() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("This game needs a graphical display; model checks still work headlessly.");
        return;
    }
    reset();
    SwingUtilities.invokeLater(this::show);
}

/// Prepared entry points: main, reset, show.
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
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `final` — The named value cannot be assigned another value afterwards. A referenced mutable object
///   can still change.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `!=` — Checks that two values differ.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
/// - `/` — Divides numbers. Dividing two int values discards the fractional part.
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
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
