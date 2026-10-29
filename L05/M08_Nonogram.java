// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
// </editor-fold>

/// # L05.08 · Nonogram
///
/// **Count the last black group.** The numbers beside a picture row give the lengths of its
/// uninterrupted black groups. Black, black, white, black needs the clue `[2, 1]`. The starter
/// loses the final 1.
///
/// **Your task:** Complete `runs` below so it also counts a black group reaching the end of its
/// input. Preserve earlier groups. An empty or all-white row must produce no clue numbers.
///
/// **Result:** `[1, 1, 0, 1]` returns lengths `[2, 1]`; `[0, 0]` returns an empty array. Then use
/// the correct clues to reveal the picture: left click paints black and right click white.

int[] runs(int[] values) {
    var result = new ArrayList<Integer>();
    int count = 0;
    for (int value : values) {
        if (value == 1) {
            count++;
        } else if (count > 0) {
            result.add(count);
            count = 0;
        }
    }
    return result.stream().mapToInt(Integer::intValue).toArray();
}

// <editor-fold defaultstate="collapsed" desc="Recall · F17">
/// ### Traversal: one question at a time
///
/// A traversal visits values; a search may stop at the first match, while a count must examine every
/// relevant value. In a for-each loop, the name before the colon receives one value from the sequence
/// after it. Keep a running count outside the repeated body so earlier matches survive.
///
/// ```java
/// int[] pulses = {2, 0, 4};
/// int strong = 0;
/// for (int pulse : pulses) {
///     if (pulse > 1) {
///         strong++;
///     }
/// }
/// IO.println(strong);
/// ```
///
/// The result is two because pulses two and four satisfy the condition. If the question were merely
/// whether any strong pulse exists, when could you already answer? What should the count be for an
/// empty array?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Read this code">
/// The supplied scan closes a run when it meets zero. At end of input there is no next cell to trigger
/// that branch.
///
/// | input consumed | count | completed result |
/// | --- | --- | --- |
/// | 1,1 | 2 | empty |
/// | 1,1,0 | 0 | 2 |
/// | 1,1,0,1 | 1 | 2 |
///
/// The final state still holds one useful count; the result needs both 2 and 1. `result.add(count)`
/// preserves that discovered length. Decide when this action belongs after the loop. Predict the final
/// states for all-white input and one black cell. The stream conversion is supplied; it just returns
/// the collected lengths as an int array.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Finishing a run
///
/// `result` is a supplied growable list. Its `add(count)` appends one completed run length; it does
/// not replace earlier lengths. The final stream conversion simply returns those recorded lengths as
/// int[]. Focus on when a run becomes complete. Compare a line ending in white with a line ending in
/// black: which event finishes the last run in each case? An all-white or empty line must not invent
/// a zero-length black run.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Compare the final count for a line ending black and one ending white.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The last group needs an append after the scan, guarded against zero.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// After the loop, add count only when it is positive; retain the supplied result conversion.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Make it yours">
/// Draw your own small black-and-white icon in the supplied picture data. Let runs generate its clues
/// rather than entering clue numbers by hand. Give only the clues to a partner, then compare their
/// reconstructed picture with your original.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 450, HEIGHT = 420;

int[][] picture = {{0, 0, 1, 0, 0}, {0, 1, 1, 1, 0}, {1, 1, 1, 1, 1}, {0, 1, 1, 1, 0}, {0, 0, 1, 0, 0}};
int[][] cells;
int[][] rowClues = new int[5][], colClues = new int[5][];
/// restart(): Restores the starting Nonogram model so this toy can be tried again.
void restart() {
    cells = new int[5][5];
    for (int i = 0; i < 5; i++) {
        rowClues[i] = runs(picture[i]);
        int[] col = new int[5];
        for (int y = 0; y < 5; y++) {
            col[y] = picture[y][i];
        }
        colClues[i] = runs(col);
    }
}

void mark(int x, int y, boolean white) {
    if (x >= 0 && y >= 0 && x < 5 && y < 5) {
        cells[y][x] = white ? (cells[y][x] == 2 ? 0 : 2) : (cells[y][x] == 1 ? 0 : 1);
    }
}

/// won(): Compares row and column run clues, requiring every cell to be decided.
boolean won() {
    for (int i = 0; i < 5; i++) {
        for (int c : cells[i]) {
            if (c == 0) {
                return false;
            }
        }
        int[] col = new int[5];
        for (int y = 0; y < 5; y++) {
            col[y] = cells[y][i];
        }
        if (!Arrays.equals(runs(cells[i]), rowClues[i]) || !Arrays.equals(runs(col), colClues[i])) {
            return false;
        }
    }
    return true;
}

/// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
void draw(Graphics2D g) {
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
    for (int i = 0; i < 5; i++) {
        g.setColor(Color.BLACK);
        g.drawString(Arrays.toString(rowClues[i]), 25, 110 + i * 50);
        g.drawString(colClues[i].length == 0 ? "0" : "" + colClues[i][0], 105 + i * 50, 65);
    }
    for (int y = 0; y < 5; y++) {
        for (int x = 0; x < 5; x++) {
            int xx = 85 + x * 50, yy = 80 + y * 50;
            g.setColor(cells[y][x] == 1 ? new Color(35, 50, 75) : cells[y][x] == 2 ? Color.WHITE : new Color(215, 220, 230));
            g.fillRect(xx, yy, 48, 48);
            if (cells[y][x] == 2) {
                g.setColor(Color.GRAY);
                g.drawLine(xx + 18, yy + 18, xx + 30, yy + 30);
                g.drawLine(xx + 30, yy + 18, xx + 18, yy + 30);
            }
        }
    }
    g.setColor(Color.BLACK);
    g.drawString(won() ? "All clues satisfied!" : "Grey = undecided; mark every square", 25, 380);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (x >= 85 && y >= 80 && x < 335 && y < 330) {
        mark((x - 85) / 50, (y - 80) / 50, button == MouseEvent.BUTTON3);
    }
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; disposing the
/// window closes this view.
void showWindow() {
    var frame = new JFrame("Nonogram");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            var g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            draw(g);
            g.dispose();
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    panel.setBackground(Color.WHITE);
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent e) {
            key(e.getKeyCode(), true);
            panel.repaint();
        }
    });
    var mouse = new MouseAdapter() {
        @Override public void mousePressed(MouseEvent e) {
            panel.requestFocusInWindow();
            press(e.getX(), e.getY(), e.getButton());
            panel.repaint();
        }
    };
    panel.addMouseListener(mouse);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Left click: black / undecided   Right click: white / undecided<br>R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

/// main(): Starts the model, then schedules the supplied window on Swing’s event thread.
void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
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
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `final` — The named value cannot be assigned another value afterwards. A referenced mutable object
///   can still change.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
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
/// - `else` — The alternative when the preceding if condition is false.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `public` — Makes this declaration accessible to other code.
/// - `protected` — A visibility keyword used by supplied library callbacks, such as paintComponent.
/// - `super` — In super.paintComponent(g), asks the supplied parent implementation to paint its standard
///   background first.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
