// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
// </editor-fold>

/// # L03.12 · ToadsAndFrogs
///
/// Swap two groups of creatures across a single gap. Toads travel right, frogs travel left, and a jump
/// may cross one opponent, so one careless step can block the whole puzzle.
///
/// **Your mission:** The frogs have copied the toads’ direction. Repair direction so its result depends
/// on the received piece rather than always pointing right.
///
/// **Today you learn:** Return a useful value: the direction rule describes a step without moving
/// anything itself. The caller can use the returned direction for both a single step and a longer jump.
///
/// Use Run File and press 5 to ask the frog beside the initial gap to move left; R restores the starting
/// arrangement.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F13 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
int direction(int piece) {
    return 1;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F13">
/// A method packages an operation under one name. Parameters are local names for the information
/// received on each call; arguments are the actual supplied values. `return` gives one result back to
/// the caller.
///
/// ```java
/// int remaining(int capacity, int used) {
///     return capacity - used;
/// }
///
/// void main() {
///     IO.println(remaining(12, 5));
///     IO.println(remaining(9, 4));
/// }
/// ```
///
/// Predict both printed values: seven and five. The comma separates the parameters or arguments, and
/// their order decides which value each name receives. The names describe roles; they do not fetch
/// information by magic. This method calculates without printing or changing the supplied program
/// state. When repairing a calculation, try a second pair of arguments so an accidental fixed answer
/// cannot hide.
///
/// Now predict remaining(10, 6).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int direction(int piece)` — Receive a piece kind as a whole number and return a signed whole-number
///   direction.
/// - `piece` — A parameter naming this call’s creature kind: 1 for a toad and 2 for a frog.
/// - `return 1;` — The current fixed result means one position to the right, regardless of the received
///   kind.
/// - `{` — The shared body can choose a different result for each parameter value; the supplied movement
///   stays elsewhere.
///
/// ### What the prepared part already does
///
/// move asks direction for its signed step, then checks the gap, board bounds and any jumped opponent.
/// won checks the group swap; draw and the supplied input code let you select a creature.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The result is a direction, not a new board position.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The caller adds this result to the selected position.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// If piece == 1 return 1; otherwise return -1.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 330;

int[] cells;
int moves;
void restart() {
    cells = new int[]{1, 1, 1, 0, 2, 2, 2};
    moves = 0;
}

boolean move(int index) {
    if (index < 0 || index >= 7 || cells[index] == 0 || won()) {
        return false;
    }
    int piece = cells[index], d = direction(piece), step = index + d, jump = index + 2 * d, destination = -1;
    if (step >= 0 && step < 7 && cells[step] == 0) {
        destination = step;
    }
    else {
        if (jump >= 0 && jump < 7 && cells[step] == 3 - piece && cells[jump] == 0) {
            destination = jump;
        }
    }
    if (destination < 0) {
        return false;
    }
    cells[destination] = piece;
    cells[index] = 0;
    moves++;
    return true;
}

boolean won() {
    return Arrays.equals(cells, new int[]{2, 2, 2, 0, 1, 1, 1});
}

void draw(Graphics2D g) {
    for (int i = 0; i < 7; i++) {
        int x = 35 + i * 75;
        g.setColor(new Color(220, 235, 205));
        g.fillOval(x, 170, 65, 35);
        if (cells[i] != 0) {
            g.setColor(cells[i] == 1 ? new Color(60, 150, 100) : new Color(80, 130, 210));
            g.fillOval(x + 9, 130, 46, 46);
            g.setColor(Color.WHITE);
            g.drawString(cells[i] == 1 ? ">" : "<", x + 27, 158);
        }
        g.setColor(Color.BLACK);
        g.drawString("" + (i + 1), x + 29, 235);
    }
    g.setColor(Color.BLACK);
    g.drawString(won() ? "Groups swapped in " + moves + " moves!" : "Swap both groups. Step into the gap, or jump one opponent.", 20, 60);
    g.drawString("Forward only. Stuck? R starts again.", 20, 285);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code >= KeyEvent.VK_1 && code <= KeyEvent.VK_7) {
        move(code - KeyEvent.VK_1);
    }
}

void press(int x, int y, int button) {
    if (x >= 35 && x < 560 && y >= 125 && y <= 210) {
        move((x - 35) / 75);
    }
}

void showWindow() {
    var frame = new JFrame("Toads and Frogs");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click a piece or press 1-7   Toads &gt; right, frogs &lt; left   R: restart</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

void main() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("This game needs a graphical display; model checks still work headlessly.");
        return;
    }
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}

/// Prepared entry points: main, restart, draw, showWindow.
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

