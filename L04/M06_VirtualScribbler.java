// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.Timer;
// </editor-fold>

/// # L04.06 · VirtualScribbler
///
/// Let a restless pen grow a drawing across the canvas, then steer its drift and jitter. Pause at
/// an interesting pattern to examine it before clearing the canvas for another experiment.
///
/// **Your mission:** The burst action currently moves the pen zero times. Build a for loop that calls
/// step exactly count times for accepted counts 0–60. Preserve the supplied rejection and the pause
/// behaviour inside step.
///
/// **Today you learn:** Counted repetition: choose a for range by its number of passes. For count equal
/// to one, the body must run once; for zero it must not run at all.
///
/// Click 1 step and then 12 steps. These one-shot buttons pause automatic drawing so you can compare
/// the visible Steps count. The unfinished burst leaves it at zero.
void burst(int count) {
    if (count < 0 || count > 60) {
        return;
    }
}

// <editor-fold defaultstate="collapsed" desc="Recall · F11">
/// A counted `for` puts initialization, a continue condition and progress in one header.
/// Initialization happens once. Then Java checks the condition, runs the body if true, performs
/// progress, and checks again.
///
/// These example statements belong inside a called method.
///
/// ```java
/// for (int floor = 2; floor <= 4; floor++) {
///     IO.println(floor);
/// }
/// ```
///
/// Predict the visited values: `2`, `3`, `4`. There are three passes, even though the last printed
/// number is four. The two semicolons separate the header’s three parts; `floor++` adds one after a
/// pass. The braces group the repeated statements. If the starting value already fails the condition,
/// no pass occurs. Count positions and gaps separately: three positions have two gaps. Write down a
/// tiny trace before changing a bound.
///
/// Now predict the output if floor starts at five.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Read this code">
/// The guard already rejects counts outside 0–60. Your loop belongs after that guard; one iteration
/// makes one call to step.
///
/// | count | calls required | example visited counter values |
/// | --- | --- | --- |
/// | 0 | 0 | none |
/// | 1 | 1 | 0 |
/// | 4 | 4 | 0, 1, 2, 3 |
///
/// Choose initialization, continuation and progress together. A counter's last value is not the number
/// of passes. Predict the calls for count 12 before using its button. step keeps the canvas, randomness
/// and paused check. oneShot temporarily enables drawing, calls your burst, then leaves automatic
/// drawing paused; the timer uses the same burst for continuous play.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// Pause before comparing counts. Each one-shot button ends paused, even if continuous drawing was
/// running. The visible Steps value is the witness; no change to the pen physics is required.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Write the required call counts for zero, one and four before writing the for header.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Each pass should do just step(); progress belongs in the loop header.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// A zero-based counter below count gives count passes. Keep the existing guard before the loop.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Make it yours">
/// Design a pulse rhythm: alternating iterations make one and two calls to step. Keep a separate
/// written trace for a four-iteration burst before changing the drawing. Give the rhythm a name and
/// compare it with the ordinary one-call-per-iteration version.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 640, HEIGHT = 450;

BufferedImage canvas;
Random random;
double x, y, angle;
int drift, jitter, steps;
boolean paused;
void clear() {
    var g = canvas.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.dispose();
}

void restart() {
    canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    clear();
    random = new Random(71);
    x = WIDTH / 2;
    y = HEIGHT / 2;
    angle = 0;
    drift = 3;
    jitter = 4;
    steps = 0;
    paused = true;
}

void adjust(int d, int j) {
    drift = Math.max(-10, Math.min(10, drift + d));
    jitter = Math.max(0, Math.min(10, jitter + j));
}

void step() {
    if (paused) {
        return;
    }
    angle += drift * .008 + random.nextGaussian() * jitter * .04;
    double nx = x + Math.cos(angle) * 2.2, ny = y + Math.sin(angle) * 2.2;
    if (nx < 2 || nx > WIDTH - 3) {
        angle = Math.PI - angle;
        nx = Math.max(2, Math.min(WIDTH - 3, nx));
    }
    if (ny < 2 || ny > HEIGHT - 3) {
        angle = -angle;
        ny = Math.max(2, Math.min(HEIGHT - 3, ny));
    }
    var g = canvas.createGraphics();
    g.setColor(Color.getHSBColor((steps++ % 1500) / 1500f, .75f, .7f));
    g.setStroke(new BasicStroke(1.5f));
    g.draw(new Line2D.Double(x, y, nx, ny));
    g.dispose();
    x = nx;
    y = ny;
}

void draw(Graphics2D g) {
    g.drawImage(canvas, 0, 0, null);
    g.setColor(Color.RED);
    g.drawOval((int)x - 4, (int)y - 4, 8, 8);
    g.setColor(new Color(245, 245, 245));
    g.fillRect(0, 0, WIDTH, 28);
    g.setColor(Color.BLACK);
    g.drawString("Steps: " + steps + "  Drift: " + drift + "  Jitter: " + jitter + "  " + (paused ? "Paused" : "Drawing"), 10, 20);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_SPACE -> paused = !paused;
            case KeyEvent.VK_LEFT -> adjust(-1, 0);
            case KeyEvent.VK_RIGHT -> adjust(1, 0);
            case KeyEvent.VK_UP -> adjust(0, 1);
            case KeyEvent.VK_DOWN -> adjust(0, -1);
            case KeyEvent.VK_C -> clear();
        }
    }
}

void oneShot(int count) {
    paused = false;
    burst(count);
    paused = true;
}

void showWindow() {
    var frame = new JFrame("Virtual Scribbler");
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
    var timer = new Timer(20, e -> {
        burst(4);
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent e) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Space: play / pause   Arrows: drift / jitter   C: clear   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    var controls = new JPanel(new BorderLayout());
    controls.add(help, BorderLayout.NORTH);
    var buttons = new JPanel();
    for (int count : new int[]{1, 12}) {
        var button = new JButton(count == 1 ? "1 step" : "12 steps");
        button.setFocusable(false);
        button.addActionListener(e -> {
            oneShot(count);
            panel.repaint();
            panel.requestFocusInWindow();
        });
        buttons.add(button);
    }
    controls.add(buttons, BorderLayout.SOUTH);
    frame.add(controls, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
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
/// - `?` — In condition ? first : second, chooses the first value when the condition is true, otherwise
///   the second.
/// - `:` — In for (int value : values), reads each element in turn. It also separates the two
///   alternatives of ? : or follows a switch label.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
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
/// - `null` — No object is present.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
