// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L10.13 · RippleTank
///
/// Tap a still tank and send rings across its surface. Two taps can meet, strengthen or cancel, turning a
/// few clicks into a moving interference pattern.
///
/// **Your mission:** A valid tap adds a disturbance but pauses the tank immediately. Restore the
/// transition that lets a new disturbance actually spread.
///
/// **Today you learn:** Input can change both model data and lifecycle state. Here a valid interior tap
/// starts motion; an out-of-bounds tap must not silently resume or change the water.
///
/// From the course folder containing README.md:
/// - Play: `java L10/M13_RippleTank.java`
/// - Check this mission: `java L10/Check.java M13_RippleTank.java`
///
/// After the play command, click inside the water to make ripples, press Space to pause and R to
/// restore a still tank. After your repair: save the file, run the check command above, then play
/// again.
///
/// Need an explanation? Recall · F30 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
void tap(int x, int y) {
    if (x > 0 && y > 0 && x < COLUMNS - 1 && y < ROWS - 1) {
        height[y][x] = Math.min(120, height[y][x] + 60);
        paused = true;
    }
}

// <editor-fold defaultstate="collapsed" desc="Recall · F30">
/// ### Input requests; a tick advances time
///
/// An event can record an intention without changing the whole simulation immediately. A tick
/// performs one bounded update. Keep drawing separate from both.
///
/// ```java
/// int height = 0;
/// int pending = 0;
/// void press() {
///     pending = 2;
/// }
/// void tick() {
///     height += pending;
///     pending = 0;
/// }
/// ```
///
/// After `press()`, height is still 0. One `tick()` changes it to 2 and consumes the request; another
/// tick leaves it at 2. Repeating the input before that tick still records one request, rather than
/// two updates. A supplied timer can call tick, but paused or finished games should reject further
/// advancement. Restart must restore the initial state.
///
/// Predict: from the initial state, what happens after press, tick, press, tick?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `x > 0 && y > 0` — Both coordinates must stay inside the protected boundary.
/// - `height[y][x]` — The first index selects a row; the second selects a column in that row.
/// - `Math.min(120, height[y][x] + 60)` — Adds a bounded disturbance instead of allowing unlimited
///   repeated taps.
/// - `paused = true;` — Currently stops the clock immediately after a successful tap.
///
/// ### What the prepared part already does
///
/// The mouse handler converts screen pixels into tank cells and calls tap. The supplied step spreads
/// heights through independent next buffers; rendering colours the current heights and restart restores
/// still water.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The input handler should activate the existing clock, not calculate the wave.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Change the paused value only after a valid interior tap.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Keep the old height cap and let step read the supplied double buffer.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 640, HEIGHT = 440;

final int COLUMNS = 64, ROWS = 44;
double[][] height, previous;
boolean paused;
/// Supplied restart: restore the initial model before the UI uses it.
void restart() {
    height = new double[ROWS][COLUMNS];
    previous = new double[ROWS][COLUMNS];
    paused = true;
}

/// Supplied bounded update: one call means one model step; the active repair is described above.
void step() {
    if (paused) {
        return;
    }
    double[][] next = new double[ROWS][COLUMNS];
    for (int y = 1; y < ROWS - 1; y++) {
        for (int x = 1; x < COLUMNS - 1; x++) {
            next[y][x] = ((height[y][x - 1] + height[y][x + 1] + height[y - 1][x] + height[y + 1][x]) / 2 - previous[y][x]) * .992;
        }
    }
    previous = height;
    height = next;
}

double energy() {
    double sum = 0;
    for (var row : height) {
        for (double h : row) {
            sum += h * h;
        }
    }
    return sum;
}

/// Supplied drawing: reads model values; repainting is not another turn.
void draw(Graphics2D g) {
    for (int y = 0; y < ROWS; y++) {
        for (int x = 0; x < COLUMNS; x++) {
            int value = (int)Math.max(-100, Math.min(100, height[y][x] * 3));
            g.setColor(new Color(70 + Math.max(0, value), 140 + value, 210 - Math.max(0, -value)));
            g.fillRect(x * 10, y * 10, 10, 10);
        }
    }
    g.setColor(Color.WHITE);
    g.drawString(paused ? "Paused - click to disturb water" : "Click more ripples; watch interference and edge reflections", 12, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Supplied restart: restore the initial model before the UI uses it.
        restart();
    }
    if (pressed && code == KeyEvent.VK_SPACE) {
        paused = !paused;
    }
}

void press(int x, int y, int button) {
    tap(x / 10, y / 10);
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Ripple Tank");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            var g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/// Supplied drawing: reads model values; repainting is not another turn.
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
    var timer = new Timer(20, e -> {
/// Supplied bounded update: one call means one model step; the active repair is described above.
        step();
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent e) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Click: ripple   Space: pause   R: still water</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

/// Use the play command above to start the program. The check command checks the rule without
/// opening a window. The supplied part supports only the main game or experiment described above.
void main() {
/// Supplied restart: restore the initial model before the UI uses it.
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
/// - `+=` — Adds to the old value and stores the result back under the same name.
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

