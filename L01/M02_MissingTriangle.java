// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
// </editor-fold>

/// # L01.02 · MissingTriangle
///
/// Three cut-out black discs can make you see a white triangle that was never drawn. Rotate one disc and
/// watch the suggested triangle disappear.
///
/// **Your mission:** The discs currently look solid because one drawing action hides another. Repair
/// paintDisc so each disc keeps its white bite.
///
/// **Today you learn:** Sequence and layers: when paintDisc is called, its two calls run in their written
/// order. Predict which colour is left on top. The definition itself waits until the supplied drawing
/// code calls it.
///
/// Run the program, click a disc to rotate it and press O to reveal the outlines the program
/// actually draws.
///
/// Lines starting with `///` are your guide; they do not run as Java instructions.
void paintDisc(Graphics2D g, int i) {
    darkDisc(g, i);
    whiteBite(g, i);
}

// <editor-fold defaultstate="collapsed" desc="Recall · F01">
/// A method is a named action. Defining it makes the action available; a call asks it to run. Java
/// starts this program at `main`, even though another method could be written first.
///
/// ```java
/// void announce() {
///     IO.println("Before");
/// }
///
/// void main() {
///     announce();
///     IO.println("After");
/// }
/// ```
///
/// Predict the two output lines before reading on. The call `announce();` enters that method, prints
/// `Before`, then returns to the next statement in `main`, which prints `After`. Inside a called
/// method, these statements run in order. `void` means no result value is returned; `()` supplies no
/// arguments here. Braces surround a body and a semicolon ends each call. Comments explain the
/// program but do not run.
///
/// Now predict what happens if main calls announce twice before its final print.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `void paintDisc` — This names a drawing action that returns no result; it paints as its effect.
/// - `(Graphics2D g, int i)` — The pen g and disc number i arrive from the supplied code. The comma
///   separates these two inputs.
/// - `{` — Everything up to the matching } belongs to this action.
/// - `whiteBite(g, i);` — Paint the white cut-out on disc i with pen g. The same two inputs are passed
///   together.
/// - `darkDisc(g, i);` — Paint a solid black disc; this can cover a cut-out drawn earlier.
/// - `;` — A semicolon finishes each action call. Moving a whole call moves its semicolon too.
///
/// ### What the prepared part already does
///
/// The /// lines explain the task and do not run. main below starts the program and opens the window; the
/// window asks draw to paint, and draw calls your action for all three discs. darkDisc and whiteBite
/// contain the shapes, while rotate and the remaining code handle turning and clicks.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Find the two ready-made drawing actions.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The white shape must be the last layer.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Call darkDisc(g, i) before whiteBite(g, i).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
void darkDisc(Graphics2D g, int i) {
    g.setColor(Color.BLACK);
    g.fillOval(cx[i] - 65, cy[i] - 65, 130, 130);
}

void whiteBite(Graphics2D g, int i) {
    g.setColor(Color.WHITE);
    g.fillArc(cx[i] - 66, cy[i] - 66, 132, 132, base[i] + rotation[i], 60);
}

final int WIDTH = 600, HEIGHT = 450;

int[] rotation = new int[3];
int[] cx = {300, 160, 440}, cy = {80, 320, 320}, base = {-120, 0, 120};
boolean outlines;
void restart() {
    rotation = new int[3];
    outlines = false;
}

void rotate(int index) {
    if (index >= 0 && index < 3) {
        rotation[index] = (rotation[index] + 30) % 360;
    }
}

void draw(Graphics2D g) {
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    for (int i = 0; i < 3; i++) {
        paintDisc(g, i);
        if (outlines) {
            g.setColor(Color.RED);
            g.drawOval(cx[i] - 65, cy[i] - 65, 130, 130);
            for (int side = 0; side < 2; side++) {
                double a = Math.toRadians(base[i] + rotation[i] + side * 60);
                g.drawLine(cx[i], cy[i], cx[i] + (int)(65 * Math.cos(a)), cy[i] - (int)(65 * Math.sin(a)));
            }
        }
    }
    g.setColor(Color.DARK_GRAY);
    g.drawString("Is there a white triangle? No triangle is filled by the program.", 15, 420);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code >= KeyEvent.VK_1 && code <= KeyEvent.VK_3) {
        rotate(code - KeyEvent.VK_1);
    }
    if (pressed && code == KeyEvent.VK_O) {
        outlines = !outlines;
    }
}

void press(int x, int y, int button) {
    for (int i = 0; i < 3; i++) {
        if (Math.hypot(x - cx[i], y - cy[i])<65) {
            rotate(i);
        }
    }
}

void showWindow() {
    var frame = new JFrame("Missing Triangle");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click a disc or press 1-3: rotate   O: actual outlines   R: align</div></html>", SwingConstants.CENTER);
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
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `%` — The remainder after division; useful for repeated positions or turns.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
/// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
///   direction. In List<String>, angle brackets describe the element type.
/// - `:` — In for (int value : values), reads each element in turn. It also separates the two
///   alternatives of ? : or follows a switch label.
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
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
