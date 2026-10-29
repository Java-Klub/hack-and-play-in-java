// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
// </editor-fold>

/// # L06.10 · Tents
///
/// Pitch tents beside trees while matching the edge counts and leaving space between tents. First make
/// sure the campsite map reports trees in the right places.
///
/// **Your mission:** One tree example cannot catch swapped coordinates or missing trees. Expand
/// learnerChecks() to probe both occupied and empty positions across the authored map.
///
/// **Today you learn:** Writing functional checks: select examples that distinguish implementations which
/// agree on the obvious case. Asymmetrical coordinates make a swapped-axis bug observable.
///
/// Click an empty cell beside a tree to pitch a tent, then run the checks
/// to see whether your map examples catch the cheats.
record TreeExample(int x, int y, boolean expected) {
}

TreeExample[] learnerChecks() {
    return new TreeExample[]{new TreeExample(0, 0, true), new TreeExample(3, 0, true),
    new TreeExample(0, 3, false), new TreeExample(1, 3, true), new TreeExample(4, 4, true), new TreeExample(-1, 0, false)};
}

// <editor-fold defaultstate="collapsed" desc="Recall · F33">
/// ### Checks: choose an example that could disagree
///
/// A functional check needs an input and an independently predicted result. A passing example is weak
/// if a plausible wrong rule gives the same answer. Read this separate calculation and its example;
/// the equality produces a boolean that reports agreement.
///
/// ```java
/// int twice(int value) {
///     return value + value;
/// }
///
/// void main() {
///     boolean agrees = twice(0) == 0;
///     IO.println(agrees);
/// }
/// ```
///
/// This prints true: twice zero is zero. An implementation always returning zero also passes. Choose
/// a different input and derive its expected result yourself. What mistake does your new example
/// expose? Keep each check's starting data independent so one example cannot accidentally prepare
/// another.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `record TreeExample(int x, int y, boolean expected)` — Stores a map position and whether a tree
///   should be reported there.
/// - `TreeExample[] learnerChecks()` — Returns the examples the functional checker will use.
/// - `new TreeExample(0, 0, true)` — The existing example covers only the tree at the upper-left
///   position.
/// - `return new TreeExample[]` — Builds an array of example values rather than changing the campsite
///   itself.
///
/// ### What the prepared part already does
///
/// treeAt() is reused by clicks and rendering. The supplied win check handles tent counts, separation,
/// and a backtracking tree-to-tent matching; your examples inspect the map lookup, not that search.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Your expectation comes from the map
///
/// `TreeExample(x,y,expected)` describes one occupancy question, not a sequence of tent placements.
/// The authored `trees` array stores JDK Points with horizontal `x` and vertical `y` fields. Read
/// that data or the visible tree symbols to predict true or false. Choose an asymmetric position
/// where exchanging x and y changes the answer, then an empty position. Explain what each example
/// rules out; repeated copies of the initial example add no evidence.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// (0,0) is symmetric and cannot expose swapped coordinates.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Compare the real tree at (3,0) with the empty transposed place (0,3).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Include a later tree and an outside coordinate; every expected value must still agree with the
/// real map.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Make it yours">
/// Build a counterfeit treeAt that agrees with your examples but lies about one other cell. Exchange
/// only the callable rule with a partner, who must discover a new example that exposes it. Restore the
/// real rule afterwards and explain which blind spot the extra example removed.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 500, HEIGHT = 420;

Point[] trees = {new Point(0, 0), new Point(3, 0), new Point(1, 3), new Point(4, 4)};
int[] rowCounts = {1, 1, 0, 1, 1}, columnCounts = {0, 2, 0, 1, 1};
boolean[][] tents;
/// restart(): Restores the starting Tents model so this toy can be tried again.
void restart() {
    tents = new boolean[5][5];
}

/// treeAt(): Looks up a tree by x/y coordinates; both clicks and rendering reuse it.
boolean treeAt(int x, int y) {
    for (var p : trees) {
        if (p.x == x && p.y == y) {
            return true;
        }
    }
    return false;
}

void toggle(int x, int y) {
    if (x >= 0 && y >= 0 && x < 5 && y < 5 && !treeAt(x, y)) {
        tents[y][x] = !tents[y][x];
    }
}

boolean separated() {
    for (int y = 0; y < 5; y++) {
        for (int x = 0; x < 5; x++) {
            if (tents[y][x]) {
                for (int yy = y - 1; yy <= y + 1; yy++) {
                    for (int xx = x - 1; xx <= x + 1; xx++) {
                        if (xx >= 0 && yy >= 0 && xx < 5 && yy < 5 && (xx != x || yy != y) && tents[yy][xx]) {
                            return false;
                        }
                    }
                }
            }
        }
    }
    return true;
}

/// match(): Supplied backtracking assigns a distinct neighbouring tree to each tent.
boolean match(ArrayList<Point> all, int index, boolean[] used) {
    if (index == all.size()) {
        return true;
    }
    var p = all.get(index);
    for (int i = 0; i < trees.length; i++) {
        if (!used[i] && Math.abs(p.x - trees[i].x) + Math.abs(p.y - trees[i].y) == 1) {
            used[i] = true;
            if (match(all, index + 1, used)) {
                return true;
            }
            used[i] = false;
        }
    }
    return false;
}

/// won(): Checks counts, separation and a complete one-to-one tent/tree matching.
boolean won() {
    var all = new ArrayList<Point>();
    int[] columns = new int[5];
    for (int y = 0; y < 5; y++) {
        int count = 0;
        for (int x = 0; x < 5; x++) {
            if (tents[y][x]) {
                if (treeAt(x, y)) {
                    return false;
                }
                count++;
                columns[x]++;
                all.add(new Point(x, y));
            }
        }
        if (count != rowCounts[y]) {
            return false;
        }
    }
    return Arrays.equals(columns, columnCounts) && separated() && all.size() == trees.length && match(all, 0, new boolean[trees.length]);
}

/// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
void draw(Graphics2D g) {
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
    for (int i = 0; i < 5; i++) {
        g.setColor(Color.BLACK);
        g.drawString("" + columnCounts[i], 105 + i * 55, 40);
        g.drawString("" + rowCounts[i], 50, 80 + i * 55);
    }
    for (int y = 0; y < 5; y++) {
        for (int x = 0; x < 5; x++) {
            int xx = 85 + x * 55, yy = 50 + y * 55;
            g.setColor(new Color(232, 240, 222));
            g.fillRect(xx, yy, 53, 53);
            if (treeAt(x, y)) {
                g.setColor(new Color(45, 125, 65));
                g.fillOval(xx + 12, yy + 7, 30, 30);
                g.fillRect(xx + 24, yy + 28, 6, 18);
            }
            if (tents[y][x]) {
                g.setColor(Color.ORANGE);
                g.fillPolygon(new int[]{xx + 7, xx + 27, xx + 47}, new int[]{yy + 44, yy + 9, yy + 44}, 3);
            }
        }
    }
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    g.setColor(Color.BLACK);
    g.drawString(won() ? "Camp complete!" : "Match counts; tents may not touch, even diagonally", 20, 365);
    g.drawString("Each tent needs its own adjacent tree (not diagonally).", 20, 390);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (x >= 85 && y >= 50 && x < 360 && y < 325) {
        toggle((x - 85) / 55, (y - 50) / 55);
    }
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; disposing the
/// window closes this view.
void showWindow() {
    var frame = new JFrame("Tents");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click: tent / empty   Match edge counts; tents must not touch   R:<br>reset</div></html>", SwingConstants.CENTER);
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
    verifyExamples();
    SwingUtilities.invokeLater(this::showWindow);
}

/// verifyExamples(): Startup checks your examples against real non-mutating queries; no game state
/// is changed.
void verifyExamples() {
    for (var e : learnerChecks()) {
        if (treeAt(e.x(), e.y()) != e.expected()) {
            throw new IllegalStateException("Cell x=" + e.x() + ", y=" + e.y()
                    + ": expected tree=" + e.expected() + ", actual=" + treeAt(e.x(), e.y()));
        }
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
/// - `record` — A small named bundle of values. Its component accessors use the component names with ().
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
