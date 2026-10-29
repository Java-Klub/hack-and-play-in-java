// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;
// </editor-fold>

/// # L10.02 · FireworkPainter
///
/// Paint a night sky with fireworks that bloom wherever you click. Each burst should remember the place
/// you chose, not a fixed point on the canvas.
///
/// **Your mission:** Clicking produces no burst because the returned action ignores both coordinates.
/// Make the deferred action affect the real firework model.
///
/// **Today you learn:** A two-argument callback carries click coordinates into a later action. Creating
/// the callback must leave particles alone; invoking it must use that particular click once.
///
/// From the course folder containing README.md:
/// - Play: `java L10/M02_FireworkPainter.java`
/// - Check this mission: `java L10/Check.java M02_FireworkPainter.java`
///
/// After the play command, click the canvas to launch fireworks; Space pauses the motion and R
/// clears and restarts the sky. After your repair: save the file, run the check command above, then
/// play again.
///
/// Need an explanation? Recall · F28 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
java.util.function.BiConsumer<Integer, Integer> burstAction() {
    return (x, y) -> {};
}

// <editor-fold defaultstate="collapsed" desc="Recall · F28">
/// ### Callback: give an action for later
///
/// A callback is an action another part of the program calls later, perhaps after a click. A
/// `Runnable` is an action with no supplied arguments and no returned result.
///
/// ```java
/// Runnable cheer = () -> IO.println("Hooray!");
/// IO.println("Ready");
/// cheer.run();
/// ```
///
/// The output is `Ready`, then `Hooray!`. Creating the lambda `() -> ...` does not execute its body.
/// Calling `run()` does. Braces allow several statements inside one callback. A method reference such
/// as `this::wave` similarly supplies this object's `wave` method without calling it yet; `wave()`
/// calls it immediately. The prepared UI decides when to invoke a callback.
///
/// Predict: what prints if the last line becomes two consecutive calls to `cheer.run()`?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `BiConsumer<Integer, Integer>` — A JDK action role accepting two integer values and returning no
///   value.
/// - `(x, y)` — These lambda parameters will receive coordinates when the action runs.
/// - `-> {}` — The arrow introduces the action body, which is currently empty.
///
/// ### What the prepared part already does
///
/// press calls the actual burstAction callback with the clicked position. step advances finite
/// particles and draw shows their trails; Space pauses and R clears and restarts.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// BiConsumer accepts two values later.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Forward the values in x/y order; do not invoke burst while creating the callback.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// The supplied burst method already adds sixty particles and starts motion.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 640, HEIGHT = 440;

record Particle(double x, double y, double vx, double vy, int age, Color colour) {
}

BufferedImage canvas;
ArrayList<Particle> particles = new ArrayList<>();
Random random = new Random(41);
boolean paused;
String message;
void clear() {
    var g = canvas.createGraphics();
    g.setColor(new Color(8, 12, 28));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.dispose();
    particles.clear();
}

/// Supplied restart: restore the initial model before the UI uses it.
void restart() {
    canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    clear();
    paused = true;
    message = "Click to burst";
}

void burst(int x, int y) {
    if (particles.size()>1140) {
        return;
    }
    Color colour = Color.getHSBColor(random.nextFloat(), .75f, 1);
    for (int i = 0; i < 60; i++) {
        double a = 2 * Math.PI * i / 60, velocity = 1 + random.nextDouble() * 3;
        particles.add(new Particle(x, y, Math.cos(a) * velocity, Math.sin(a) * velocity, 0, colour));
    }
    paused = false;
}

/// Supplied bounded update: one call means one model step; the active repair is described above.
void step() {
    if (paused) {
        return;
    }
    var g = canvas.createGraphics();
    for (int i = particles.size() - 1; i >= 0; i--) {
        var p = particles.get(i);
        double x = p.x + p.vx, y = p.y + p.vy;
        g.setColor(new Color(p.colour.getRed(), p.colour.getGreen(), p.colour.getBlue(), Math.max(0, 150 - p.age * 2)));
        g.draw(new Line2D.Double(p.x, p.y, x, y));
        if (p.age >= 65) {
            particles.remove(i);
        }
        else {
            particles.set(i, new Particle(x, y, p.vx * .992, p.vy + .035, p.age + 1, p.colour));
        }
    }
    g.dispose();
}


/// Supplied drawing: reads model values; repainting is not another turn.
void draw(Graphics2D g) {
    g.drawImage(canvas, 0, 0, null);
    g.setColor(Color.WHITE);
    g.drawString(message + "   Particles: " + particles.size() + "   " + (paused ? "Paused" : ""), 12, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Supplied restart: restore the initial model before the UI uses it.
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_SPACE -> paused = !paused;
        }
    }
}

void press(int x, int y, int button) {
    burstCallback.accept(x, y);
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Firework Painter");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click: burst   Space: pause   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

java.util.function.BiConsumer<Integer, Integer> burstCallback = burstAction();

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
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `else` — The alternative when the preceding if condition is false.
/// - `for` — Repeats a block over a range or a sequence of values.
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
/// - `record` — A small named bundle of values. Its component accessors use the component names with ().
/// - `null` — No object is present.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>

