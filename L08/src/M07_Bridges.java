// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.util.Arrays;
// </editor-fold>

/// # L08.07 · Bridges
///
/// Connect islands with single or double bridges until all counts agree. Try a risky link and use undo to
/// compare the result.
///
/// **Your mission:** Undo restores the same already-changed bridge array, so nothing is undone. Repair
/// snapshot() to preserve the bridge counts from before the edit.
///
/// **Today you learn:** Copies and aliasing: returning another reference to an array is not copying its
/// values. A saved snapshot must survive later writes to the working state.
///
/// Run File, click a faint connection to change its bridge count, and press U to try undoing that edit.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Learn · F20 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
int[] snapshot() {
    return bridges;
}

// <editor-fold defaultstate="expanded" desc="Learn · F20">
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
/// - `int[]` — Bridge counts are plain int elements in one array, with no mutable nested rows.
/// - `snapshot()` — The caller expects a saved model state, not a new visible game action.
/// - `return bridges` — Returns a reference to the current array; both names would observe future writes.
///
/// ### What the prepared part already does
///
/// choose() saves a snapshot before a real edge change. undo() restores it once; crossing() supplies
/// bridge geometry and won() checks counts and connectivity, so those algorithms stay as prepared.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="For this game">
/// ### A saved array must own its cells
///
/// The neutral wave example distinguishes an alias from a clone. `clone()` creates storage
/// immediately; making another variable or returning the array reference does not. For this int[] the
/// cells themselves are plain values, so one copied layer is sufficient. Predict the saved and
/// working counts after one connection changes. Also check timing: the caller captures the snapshot
/// before the edit, so copying only after the edit could not preserve the earlier state.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Assigning bridges to another variable copies the reference, not its cells.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// A one-dimensional int[] contains values, so one clone is enough.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Return bridges.clone(); the supplied undo() then restores an independent state.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 470, HEIGHT = 420;

Point[] islands = {new Point(0, 0), new Point(2, 0), new Point(4, 0), new Point(0, 2), new Point(4, 2), new Point(0, 4), new Point(2, 4), new Point(4, 4)};
int[][] edges = {{0, 1}, {1, 2}, {0, 3}, {2, 4}, {3, 5}, {4, 7}, {5, 6}, {6, 7}, {1, 6}, {3, 4}};
int[] counts = {2, 2, 2, 2, 2, 2, 2, 2};
int[] bridges;
/// restart(): Restores the starting Bridges model so this toy can be tried again.
void restart() {
    bridges = new int[edges.length];
    previousBridges = null;
}

/// crossing(): Supplied segment geometry excludes shared endpoints before testing bridge crossings.
boolean crossing(int a, int b) {
    var e = edges[a];
    var f = edges[b];
    for (int x : e) {
        for (int y : f) {
            if (x == y) {
                return false;
            }
        }
    }
    return Line2D.linesIntersect(islands[e[0]].x, islands[e[0]].y, islands[e[1]].x, islands[e[1]].y,
    islands[f[0]].x, islands[f[0]].y, islands[f[1]].x, islands[f[1]].y);
}

boolean won() {
    int[] actual = new int[8];
    boolean[] reached = new boolean[8];
    reached[0] = true;
    for (int i = 0; i < edges.length; i++) {
        if (bridges[i]>0) {
            if (bridges[i]>2) {
                return false;
            }
            actual[edges[i][0]] += bridges[i];
            actual[edges[i][1]] += bridges[i];
            for (int j = 0; j < i; j++) {
                if (bridges[j]>0 && crossing(i, j)) {
                    return false;
                }
            }
        }
    }
    if (!Arrays.equals(actual, counts)) {
        return false;
    }
    for (int pass = 0; pass < 8; pass++) {
        for (int i = 0; i < edges.length; i++) {
            if (bridges[i]>0 && (reached[edges[i][0]] || reached[edges[i][1]])) {
                reached[edges[i][0]] = reached[edges[i][1]] = true;
            }
        }
    }
    for (boolean r : reached) {
        if (!r) {
            return false;
        }
    }
    return true;
}

void choose(int x, int y) {
    double best = 14;
    int chosen = -1;
    for (int i = 0; i < edges.length; i++) {
        var a = islands[edges[i][0]];
        var b = islands[edges[i][1]];
        double distance = Line2D.ptSegDist(70 + a.x * 75, 45 + a.y * 75, 70 + b.x * 75, 45 + b.y * 75, x, y);
        if (distance < best) {
            best = distance;
            chosen = i;
        }
    }
    if (chosen >= 0) {
        previousBridges = snapshot();
        bridges[chosen] = (bridges[chosen] + 1) % 3;
    }
}

/// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
void draw(Graphics2D g) {
    for (int i = 0; i < edges.length; i++) {
        var a = islands[edges[i][0]];
        var b = islands[edges[i][1]];
        int ax = 70 + a.x * 75, ay = 45 + a.y * 75, bx = 70 + b.x * 75, by = 45 + b.y * 75;
        g.setColor(bridges[i] == 0 ? new Color(225, 225, 225) : new Color(50, 110, 155));
        g.setStroke(new BasicStroke(bridges[i] == 0 ? 1 : 3));
        if (bridges[i]<2) {
            g.drawLine(ax, ay, bx, by);
        }
        else {
            if (ax == bx) {
                g.drawLine(ax - 4, ay, bx - 4, by);
                g.drawLine(ax + 4, ay, bx + 4, by);
            }
            else {
                g.drawLine(ax, ay - 4, bx, by - 4);
                g.drawLine(ax, ay + 4, bx, by + 4);
            }
        }
    }
    for (int i = 0; i < 8; i++) {
        int x = 70 + islands[i].x * 75, y = 45 + islands[i].y * 75;
        g.setColor(Color.WHITE);
        g.fillOval(x - 17, y - 17, 34, 34);
        g.setColor(Color.BLACK);
        g.drawOval(x - 17, y - 17, 34, 34);
        g.drawString("" + counts[i], x - 4, y + 5);
    }
    g.setColor(Color.BLACK);
    g.drawString(won() ? "All islands joined!" : "Match numbers and join one network; no crossings", 20, 390);
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
    choose(x, y);
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; disposing the
/// window closes this view.
void showWindow() {
    var frame = new JFrame("Bridges");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click a faint edge: 0 / 1 / 2 bridges<br>U: undo one change   R: reset</div></html>", SwingConstants.CENTER);
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

int[] previousBridges;

/// undo(): Restores exactly one saved model snapshot and consumes that undo opportunity.
void undo() {
    if (previousBridges != null) {
        bridges = previousBridges;
        previousBridges = null;
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
/// - `double` — A type for approximate fractional numbers. Java uses a decimal point.
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
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `%` — The remainder after division; useful for repeated positions or turns.
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
/// - `else` — The alternative when the preceding if condition is false.
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

