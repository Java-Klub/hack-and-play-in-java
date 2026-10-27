// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Timer;
// </editor-fold>

/// # L10.11 · OrbitPainter
///
/// Launch a particle around a centre of attraction and let its orbit paint a coloured trail. Try to find
/// a long looping flight that stays on the canvas.
///
/// **Your mission:** The pause condition is reversed, so the orbit refuses to advance while running.
/// Repair the tick guard and keep paused positions and trail unchanged.
///
/// **Today you learn:** Pause is a promise that a tick changes nothing. The boolean guard must match that
/// promise before the supplied physics or persistent drawing begins.
///
/// After Run File, press Space to start the default orbit, drag to choose another launch and press C to
/// clear its trail.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F30 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
void step() {
    if (!paused) {
        return;
    }
    double dx = WIDTH / 2 - x, dy = HEIGHT / 2 - y, r2 = dx * dx + dy * dy + 25 * 25;
    vx += 600 * dx / Math.pow(r2, 1.5);
    vy += 600 * dy / Math.pow(r2, 1.5);
    double nx = x + vx, ny = y + vy;
    var g = canvas.createGraphics();
    g.setColor(Color.getHSBColor((ticks++ % 1000) / 1000f, .8f, 1));
    g.draw(new Line2D.Double(x, y, nx, ny));
    g.dispose();
    x = nx;
    y = ny;
    if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
        paused = true;
        message = "Escaped - drag a new launch";
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
/// - `!paused` — Means not paused; compare this meaning with the early return below it.
/// - `return;` — Prevents every later change in this tick, not just the position change.
/// - `ticks++` — Advances the colour-time counter as the new trail segment is painted.
///
/// ### What the prepared part already does
///
/// Dragging sets a new launch and the timer requests steps. The supplied softened attraction formula
/// updates velocity, draws one segment and pauses an escaped particle; clearing only removes the existing
/// canvas.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// paused describes a reason to do no work.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Keep the return inside the paused branch.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// The attraction and trail formula below the guard are already correct.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 640, HEIGHT = 440;

BufferedImage canvas;
double x, y, vx, vy, aimX, aimY;
boolean paused, aiming;
int ticks;
String message;
void clear() {
    var g = canvas.createGraphics();
    g.setColor(new Color(12, 17, 30));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.dispose();
}

/// Supplied restart: restore the initial model before the UI uses it.
void restart() {
    canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    clear();
    x = 440;
    y = 220;
    vx = 0;
    vy = 2.1;
    ticks = 0;
    paused = true;
    aiming = false;
    message = "Space: default orbit, or drag a new launch";
}

void launch(double xx, double yy, double endX, double endY) {
    x = xx;
    y = yy;
    vx = (endX - xx) * .025;
    vy = (endY - yy) * .025;
    double speed = Math.hypot(vx, vy);
    if (speed > 6) {
        vx *= 6 / speed;
        vy *= 6 / speed;
    }
    paused = false;
    aiming = false;
    message = "Painting orbit";
}

/// Supplied export boundary: a local path is used; encoding is handled by the JDK.
void save(File file) throws IOException {
    ImageIO.write(canvas, "png", file);
}

/// Supplied export boundary: a local path is used; encoding is handled by the JDK.
void save() {
    try {
/// Supplied export boundary: a local path is used; encoding is handled by the JDK.
        save(new File("orbit.png"));
        message = "Saved orbit.png";
    } catch (IOException e) {
        message = "Save failed: " + e.getMessage();
    }
}

/// Supplied drawing: reads model values; repainting is not another turn.
void draw(Graphics2D g) {
    g.drawImage(canvas, 0, 0, null);
    g.setColor(Color.WHITE);
    g.fillOval(WIDTH / 2 - 7, HEIGHT / 2 - 7, 14, 14);
    g.setColor(Color.YELLOW);
    g.fillOval((int)x - 3, (int)y - 3, 6, 6);
    if (aiming) {
        g.setColor(Color.CYAN);
        g.draw(new Line2D.Double(x, y, aimX, aimY));
    }
    g.setColor(Color.WHITE);
    g.drawString(message, 12, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Supplied restart: restore the initial model before the UI uses it.
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_SPACE -> paused = !paused;
            case KeyEvent.VK_C -> clear();
            case KeyEvent.VK_S -> save();
        }
    }
}

void press(int x, int y, int button) {
    x = Math.max(0, Math.min(WIDTH - 1, x));
    y = Math.max(0, Math.min(HEIGHT - 1, y));
    this.x = aimX = x;
    this.y = aimY = y;
    paused = true;
    aiming = true;
}

void drag(int x, int y) {
    if (aiming) {
        aimX = x;
        aimY = y;
    }
}

void release(int x, int y) {
    if (aiming) {
        launch(this.x, this.y, x, y);
    }
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Orbit Painter");
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
        @Override public void mouseDragged(MouseEvent e) {
            drag(e.getX(), e.getY());
            panel.repaint();
        }
        @Override public void mouseReleased(MouseEvent e) {
            release(e.getX(), e.getY());
            panel.repaint();
        }
    };
    panel.addMouseListener(mouse);
    panel.addMouseMotionListener(mouse);
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
    var help = new JLabel("<html><div style=\"text-align:center\">Drag: position + launch velocity   Space: pause   C: clear   S: save PNG   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

/// Start the real program with Run File. Test File checks the rule without opening a window.
/// step is the active boundary; the model exposes it for deterministic checks without a window.
/// restart restores initial fields; draw reads state; showWindow supplies listeners, one timer and window-close cleanup.
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
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
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
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `%` — The remainder after division; useful for repeated positions or turns.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
/// - `/` — Divides numbers. Dividing two int values discards the fractional part.
/// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
///   direction. In List<String>, angle brackets describe the element type.
/// - `:` — In for (int value : values), reads each element in turn. It also separates the two
///   alternatives of ? : or follows a switch label.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `switch` — Selects one case from a set of alternatives. It can also produce a result.
/// - `case` — One alternative of a switch; the arrow points to what it does or returns.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `public` — Makes this declaration accessible to other code.
/// - `protected` — A visibility keyword used by supplied library callbacks, such as paintComponent.
/// - `super` — In super.paintComponent(g), asks the supplied parent implementation to paint its standard
///   background first.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throws` — In a method declaration: tells callers which failure they may need to handle.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>

