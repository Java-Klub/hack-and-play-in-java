// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
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
/// After Run File, click different canvas positions to launch fireworks and use Space to pause or C to
/// clear the sky.
/// After your repair: Save → Test File → play again.
java.util.function.BiConsumer<Integer, Integer> burstAction() {
    return (x, y) -> {};
}

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
/// press passes the click to the stored burst callback. The supplied burst routine creates particles,
/// step advances their bounded lives and draw paints them; pause, reset and export already work.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. BiConsumer accepts two values later.
/// 2. Forward the values in x/y order; do not invoke burst while creating the callback.
/// 3. The supplied burst method already adds sixty particles and starts motion.
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

/// Supplied export boundary: a local path is used; encoding is handled by the JDK.
void save(File file) throws IOException {
    ImageIO.write(canvas, "png", file);
}

/// Supplied export boundary: a local path is used; encoding is handled by the JDK.
void save() {
    try {
/// Supplied export boundary: a local path is used; encoding is handled by the JDK.
        save(new File("fireworks.png"));
        message = "Saved fireworks.png";
    } catch (IOException e) {
        message = "Save failed: " + e.getMessage();
    }
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
            case KeyEvent.VK_C -> clear();
            case KeyEvent.VK_S -> save();
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click: burst   Space: pause   C: clear   S: save PNG   R: reset</div></html>", SwingConstants.CENTER);
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

/// Start the real program with Run File. Test File checks the rule without opening a window.
/// press invokes burstCallback; burst creates particles at those exact coordinates.
/// step ages particles and paints trails; draw only displays the canvas; existing export and timer cleanup are supplied.
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
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throws` — In a method declaration: tells callers which failure they may need to handle.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>

