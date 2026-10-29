// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
// </editor-fold>

/// # L08.08 · MagnetDominoes
///
/// Choose the polarity of magnetic dominoes to satisfy the row and column clues. A quick undo lets you
/// test a different arrangement safely.
///
/// **Your mission:** Undo keeps the last changed polarity instead of restoring the previous one. Repair
/// copyState() so the saved domino states are independent.
///
/// **Today you learn:** Copies and aliasing: two names can share one mutable array. Copying the
/// one-dimensional values is enough here because each domino state is an int.
///
/// From the course folder containing README.md:
/// - Play: `java L08/M08_MagnetDominoes.java`
/// - Check this mission: `java L08/Check.java M08_MagnetDominoes.java`
///
/// the play command, click a domino to change its polarity, and press U to try restoring the
/// previous selection. After your repair: save the file, run the check command above, then play
/// again.
///
/// Need an explanation? Recall · F20 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
int[] copyState() {
    return state;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F20">
/// ### References: another name or another object?
///
/// An array variable stores a reference. Assignment can give two names access to one array; writing
/// through either name affects that shared object. Calling `clone()` on a one-dimensional int array
/// creates another array with copied values. The original and copy can then change independently.
///
/// ```java
/// int[] wave = {4, 1};
/// int[] shared = wave;
/// int[] saved = wave.clone();
/// wave[1] = 7;
/// IO.println(shared[1]);
/// IO.println(saved[1]);
/// ```
///
/// The output is seven, then one: shared sees the write, saved retains its copied value. After
/// `saved[0] = 8`, predict `wave[0]` and `saved[0]`. `clone()` copies one layer: for a grid, its
/// elements are row references, so copying the outside still shares rows. Ask which objects remain
/// shared, not merely whether a new outer object exists.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int[]` — One entry stores one domino’s selected state.
/// - `copyState()` — Names the boundary where a preserved pre-edit state is requested.
/// - `return state` — Passes back the same mutable object unless a real copy is made.
///
/// ### What the prepared part already does
///
/// cycle() saves copyState() before changing a domino, and undo() restores that saved array. pole()
/// interprets orientation at a cell; won() handles counts and forbidden equal neighbours.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Copy values, not a later screen image
///
/// Each domino polarity is an int in one array. Its clone copies those integers into separate
/// storage. No row objects or nested lists need copying here. Predict the saved polarity after
/// changing the live domino twice; it should still describe the moment the snapshot was taken. The
/// supplied undo replaces the working state from that earlier data. Explain why repainting cannot
/// repair shared storage.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// previousState must survive a later write to state[domino].
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// A reference alias sees that write too.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Return state.clone() in copyState(); keep polarity rules unchanged.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 520, HEIGHT = 400;

int[] state;
int[] rowPlus = {1, 2, 1, 2}, rowMinus = {1, 2, 1, 2}, colPlus = {1, 2, 1, 2}, colMinus = {2, 1, 2, 1};
/// restart(): Restores the starting Magnet Dominoes model so this toy can be tried again.
void restart() {
    state = new int[8];
    previousState = null;
    Arrays.fill(state, -1);
}

/// pole(): Converts one domino’s orientation into the sign at a requested cell.
int pole(int x, int y) {
    int s = state[y * 2 + x / 2];
    if (s <= 0) {
        return 0;
    }
    int first = s == 1 ? 1 : -1;
    return x % 2 == 0 ? first : -first;
}

void cycle(int domino) {
    if (domino >= 0 && domino < 8) {
        previousState = copyState();
        state[domino] = state[domino] == -1 ? 1 : state[domino] == 1 ? 2 : state[domino] == 2 ? 0 : -1;
    }
}

boolean won() {
    for (int s : state) {
        if (s < 0) {
            return false;
        }
    }
    int[] cp = new int[4], cm = new int[4];
    for (int y = 0; y < 4; y++) {
        int plus = 0, minus = 0;
        for (int x = 0; x < 4; x++) {
            int p = pole(x, y);
            if (p == 1) {
                plus++;
                cp[x]++;
            }
            if (p == -1) {
                minus++;
                cm[x]++;
            }
            if (p != 0 && ((x < 3 && p == pole(x + 1, y)) || (y < 3 && p == pole(x, y + 1)))) {
                return false;
            }
        }
        if (plus != rowPlus[y] || minus != rowMinus[y]) {
            return false;
        }
    }
    return Arrays.equals(cp, colPlus) && Arrays.equals(cm, colMinus);
}

/// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
void draw(Graphics2D g) {
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
    for (int i = 0; i < 4; i++) {
        g.setColor(Color.BLACK);
        g.drawString(colPlus[i] + "/" + colMinus[i], 109 + i * 60, 35);
        g.drawString(rowPlus[i] + "/" + rowMinus[i], 35, 85 + i * 60);
    }
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            int xx = 95 + x * 60, yy = 50 + y * 60;
            int s = state[y * 2 + x / 2], p = pole(x, y);
            g.setColor(s < 0 ? new Color(225, 225, 230) : p == 1 ? new Color(255, 200, 190) : p == -1 ? new Color(190, 215, 255) : new Color(240, 240, 220));
            g.fillRect(xx, yy, 58, 58);
            g.setColor(Color.BLACK);
            g.drawString(s < 0 ? "?" : p == 1 ? "+" : p == -1 ? "-" : "·", xx + 22, yy + 35);
        }
    }
    g.setStroke(new BasicStroke(3));
    g.setColor(Color.DARK_GRAY);
    for (int y = 0; y < 4; y++) {
        for (int d = 0; d < 2; d++) {
            g.drawRect(95 + d * 120, 50 + y * 60, 118, 58);
        }
    }
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    g.drawString(won() ? "All poles balanced!" : "Edge numbers are + / - counts. Equal poles cannot touch.", 20, 345);
    g.drawString("? is undecided; · is a deliberately neutral domino.", 20, 375);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_U) {
        undo();
    }
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (x >= 95 && y >= 50 && x < 335 && y < 290) {
        cycle(((y - 50) / 60) * 2 + (x - 95) / 120);
    }
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; disposing the
/// window closes this view.
void showWindow() {
    var frame = new JFrame("Magnet Dominoes");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click a domino: +\u2212 / \u2212+ / neutral / ?<br>U: undo one change   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

/// main(): Starts the model, then schedules the supplied window on Swing’s event thread.
/// The prepared Undo control is U: it restores one previous edit. R starts this puzzle again.
void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}

int[] previousState;

/// undo(): Restores exactly one saved model snapshot and consumes that undo opportunity.
void undo() {
    if (previousState != null) {
        state = previousState;
        previousState = null;
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
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
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
/// - `%` — The remainder after division; useful for repeated positions or turns.
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
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `public` — Makes this declaration accessible to other code.
/// - `protected` — A visibility keyword used by supplied library callbacks, such as paintComponent.
/// - `super` — In super.paintComponent(g), asks the supplied parent implementation to paint its standard
///   background first.
/// - `null` — No object is present.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
