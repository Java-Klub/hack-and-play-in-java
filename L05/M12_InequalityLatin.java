// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
// </editor-fold>

/// # L05.12 · InequalityLatin
///
/// Fill a Latin square while obeying the little inequality signs between its cells. A correct row can
/// still lose to one tiny comparison.
///
/// **Your mission:** A displayed less-than clue accepts the opposite relationship. Repair
/// Rule.satisfiedBy() using each Rule’s own position.
///
/// **Today you learn:** Records: a rule can carry both its data and a query about that data. Its x and y
/// components choose which neighbouring numbers to compare.
///
/// From the course folder containing README.md:
/// - Play: `java L05/M12_InequalityLatin.java`
/// - Check this mission: `java L05/Check.java M12_InequalityLatin.java`
///
/// Run the play command above and repeatedly click a free cell to cycle its number while checking
/// the inequality signs beside it. After your repair: save the file, run the check command above,
/// then play again.
///
/// Need an explanation? Recall · F19 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
record Rule(int x, int y) {
    boolean satisfiedBy(int[][] values) {
        return values[y][x] < values[y][x + 1];
    }
}

// <editor-fold defaultstate="collapsed" desc="Recall · F19">
/// ### Records: one named value
///
/// A record groups related components. Its constructor receives them in declaration order; accessors
/// such as `ink()` read them. Inside the record, its own methods can use the component names
/// directly. Making a new value leaves the original components unchanged. Generated `equals` compares
/// the component values.
///
/// ```java
/// record Frame(String ink, int dots) {
///     Frame recoloured(String nextInk) {
///         return new Frame(nextInk, dots);
///     }
/// }
/// ```
///
/// Calling `recoloured("gold")` on `new Frame("violet", 6)` returns gold and six; the original stays
/// violet and six. Would another recolouring back to violet compare equal to the original? The same
/// names now explain what two otherwise anonymous values mean.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `record Rule(int x, int y)` — Stores the column and row identifying this particular clue.
/// - `boolean satisfiedBy(int[][] values)` — A yes/no query receiving the whole number grid as its input.
/// - `values[y][x]` — Selects a row first, then a column within that row.
/// - `x + 1` — Addresses the immediately neighbouring column to the right.
///
/// ### What the prepared part already does
///
/// won() separately checks Latin-square uniqueness, then asks every stored Rule about the current grid.
/// The drawing code places the clue from the same record coordinates.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// x and y belong to this Rule record, not to a global cursor.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// A row is values[y]; the neighbour on its right has column x+1.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Return values[y][x] < values[y][x+1], including a false result for equality.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 500, HEIGHT = 400;

Rule[] rules = {new Rule(0, 0), new Rule(2, 0), new Rule(1, 1), new Rule(0, 2), new Rule(1, 3), new Rule(2, 3)};
int[][] givens = {{1, 0, 0, 0}, {0, 0, 0, 1}, {0, 4, 0, 0}, {0, 0, 2, 0}};
int[][] cells;
/// restart(): Restores the starting Inequality Latin model so this toy can be tried again.
void restart() {
    cells = Arrays.stream(givens).map(int[]::clone).toArray(int[][]::new);
}

void cycle(int x, int y) {
    if (x >= 0 && y >= 0 && x < 4 && y < 4 && givens[y][x] == 0) {
        cells[y][x] = (cells[y][x] + 1) % 5;
    }
}

/// won(): Rejects Latin-square duplicates and delegates each inequality to its Rule record.
boolean won() {
    for (int i = 0; i < 4; i++) {
        boolean[] row = new boolean[5], col = new boolean[5];
        for (int j = 0; j < 4; j++) {
            int a = cells[i][j], b = cells[j][i];
            if (a < 1 || a > 4 || b < 1 || b > 4 || row[a] || col[b]) {
                return false;
            }
            if (givens[i][j] != 0 && a != givens[i][j]) {
                return false;
            }
            row[a] = true;
            col[b] = true;
        }
    }
    for (var r : rules) {
        if (!r.satisfiedBy(cells)) {
            return false;
        }
    }
    return true;
}

/// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
void draw(Graphics2D g) {
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            int xx = 85 + x * 72, yy = 45 + y * 72;
            g.setColor(givens[y][x] != 0 ? new Color(215, 225, 245) : new Color(240, 240, 240));
            g.fillRect(xx, yy, 52, 52);
            g.setColor(Color.BLACK);
            if (cells[y][x] != 0) {
                g.drawString("" + cells[y][x], xx + 19, yy + 34);
            }
        }
    }
    for (var r : rules) {
        g.drawString("<", 140 + r.x * 72, 79 + r.y * 72);
    }
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    g.drawString(won() ? "Latin square complete!" : "Use 1-4 once in each row and column; obey every <", 20, 370);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (x >= 85 && y >= 45 && x < 373 && y < 333 && (x - 85) % 72 < 52 && (y - 45) % 72 < 52) {
        cycle((x - 85) / 72, (y - 45) / 72);
    }
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; disposing the
/// window closes this view.
void showWindow() {
    var frame = new JFrame("Inequality Latin");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click a free cell: 1 / 2 / 3 / 4 / blank   R: reset</div></html>", SwingConstants.CENTER);
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
/// - `record` — A small named bundle of values. Its component accessors use the component names with ().
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
