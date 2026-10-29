// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
// </editor-fold>

/// # L08.11 · BinaryBalance
///
/// Fill a small board with balanced black and white cells without making a run of three. The orange clues
/// should stay fixed while your answers stay editable.
///
/// **Your mission:** A free cell locks after you edit it because answers also change the clue template.
/// Repair copyBoard() to separate both levels of the grid.
///
/// **Today you learn:** Copies and aliasing: an int[][] stores references to row arrays. Copying only the
/// outer array leaves mutable rows shared, so an independent board needs independent rows.
///
/// From the course folder containing README.md:
/// - Play: `java L08/M11_BinaryBalance.java`
/// - Check this mission: `java L08/Check.java M11_BinaryBalance.java`
///
/// Run the play command above and click a non-orange cell repeatedly to switch between unknown,
/// black, and white while following the balance rules. After your repair: save the file, run the
/// check command above, then play again.
///
/// Need an explanation? Recall · F20 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
int[][] copyBoard(int[][] original) {
    int[][] copy = new int[original.length][];
    for (int row = 0; row < original.length; row++) {
        copy[row] = original[row].clone();
    }
    return copy;
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
/// - `int[][]` — An outer array contains row references; each row contains the cell values.
/// - `original` — The supplied clue grid is input to copyBoard(), not a board to edit in place.
/// - `.clone()` — Clones one array level; it does not recursively copy arrays referred to by that array.
///
/// ### What the prepared part already does
///
/// restart() builds cells from givens through copyBoard(). cycle() protects any position whose given is
/// nonzero, while lineValid() and won() check balance and repeated runs; those checks are already
/// supplied.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="For this game">
/// ### Copying rows as well as the outside
///
/// A grid's outer array stores row references. An outer clone therefore copies references to the same
/// rows. Here is a separate two-row example with independent rows:
///
/// ```java
/// int[][] original = {{1}, {2}};
/// int[][] copied = {original[0].clone(), original[1].clone()};
/// copied[0][0] = 9;
/// IO.println(original[0][0]);
/// ```
///
/// The output stays one because the changed row belongs to copied. Predict the result if its first
/// row were merely `original[0]`. Your board has more rows: use the actual row count to repeat the
/// copying operation. Each row's clone preserves its current elements. Explain which layer would
/// remain shared if only the outside were copied.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// int[][] is an array of row references.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// original.clone() copies those row references, leaving cells shared.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Allocate copy[original.length][] and clone each original[row] before returning.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 480, HEIGHT = 400;

int[][] givens = {{1, 0, 0, 2}, {0, 2, 0, 0}, {0, 0, 2, 0}, {2, 0, 0, 1}};
int[][] cells;
/// restart(): Restores the starting Binary Balance model so this toy can be tried again.
void restart() {
    cells = copyBoard(givens);
}

void cycle(int x, int y) {
    if (x >= 0 && y >= 0 && x < 4 && y < 4 && givens[y][x] == 0) {
        cells[y][x] = (cells[y][x] + 1) % 3;
    }
}

/// lineValid(): Checks exact black/white balance and rejects a run of three equal colours.
boolean lineValid(int[] line) {
    int ones = 0;
    for (int i = 0; i < 4; i++) {
        if (line[i]<1 || line[i]>2) {
            return false;
        }
        if (line[i] == 1) {
            ones++;
        }
        if (i >= 2 && line[i] == line[i - 1] && line[i] == line[i - 2]) {
            return false;
        }
    }
    return ones == 2;
}

boolean won() {
    for (int y = 0; y < 4; y++) {
        if (!lineValid(cells[y])) {
            return false;
        }
        int[] column = new int[4];
        for (int x = 0; x < 4; x++) {
            column[x] = cells[x][y];
            if (givens[y][x] != 0 && cells[y][x] != givens[y][x]) {
                return false;
            }
        }
        if (!lineValid(column)) {
            return false;
        }
    }
    return true;
}

/// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
void draw(Graphics2D g) {
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            int xx = 90 + x * 65, yy = 45 + y * 65;
            g.setColor(new Color(205, 210, 220));
            g.fillRect(xx, yy, 62, 62);
            if (cells[y][x] != 0) {
                g.setColor(cells[y][x] == 1 ? Color.BLACK : Color.WHITE);
                g.fillOval(xx + 11, yy + 11, 40, 40);
            }
            if (givens[y][x] != 0) {
                g.setColor(new Color(225, 100, 45));
                g.drawRect(xx + 2, yy + 2, 58, 58);
            }
        }
    }
    g.setColor(Color.BLACK);
    g.drawString(won() ? "Balanced!" : "Two black and two white in every row and column", 20, 345);
    g.drawString("No three alike in a row. Orange borders are fixed.", 20, 370);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (x >= 90 && y >= 45 && x < 350 && y < 305) {
        cycle((x - 90) / 65, (y - 45) / 65);
    }
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; disposing the
/// window closes this view.
void showWindow() {
    var frame = new JFrame("Binary Balance");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click: unknown / black / white   Orange cells are fixed   R: reset</div></html>", SwingConstants.CENTER);
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
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>

