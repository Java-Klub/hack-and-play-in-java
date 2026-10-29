// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.Timer;
// </editor-fold>

/// # L10.01 · AsteroidDrift
///
/// Steer a little ship through drifting asteroids and carve a safe path with shots. The fire control has
/// gone silent even though the rest of the flight still works.
///
/// **Your mission:** Holding Space produces no shots because the stored firing action does nothing.
/// Reconnect the delayed action without firing during its creation.
///
/// **Today you learn:** A callback is code packaged for a later event. firingAction returns that package;
/// the game loop decides when to run it while Space is held.
///
/// From the course folder containing README.md:
/// - Play: `java L10/M01_AsteroidDrift.java`
/// - Check this mission: `java L10/Check.java M01_AsteroidDrift.java`
///
/// After the play command, steer and thrust with the arrow keys, hold Space to fire and press R to
/// restart the flight. After your repair: save the file, run the check command above, then play
/// again.
///
/// Need an explanation? Learn · F28 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
Runnable firingAction() {
    return this::fire;
}

// <editor-fold defaultstate="expanded" desc="Learn · F28">
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
/// - `Runnable` — A JDK role for an action with no arguments and no returned result.
/// - `firingAction()` — Creates an action; its name does not mean the shot should happen immediately.
/// - `() -> {}` — A lambda with no parameters and an empty body: running it currently changes nothing.
///
/// ### What the prepared part already does
///
/// The supplied fire method handles cooldown and bullet creation. The held-key update runs fireCallback,
/// then advances shots and asteroids; the Swing window handles keys and stops its timer when closed.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// A Runnable represents an action, not its result.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Return a method reference or a lambda that calls fire later.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Keep fire’s cooldown/lost guards; do not copy those rules into the callback.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 420;

record Rock(double x, double y, double vx, double vy, double radius) {
}

record Shot(double x, double y, double vx, double vy, int life) {
}

ArrayList<Rock> rocks = new ArrayList<>();
ArrayList<Shot> shots = new ArrayList<>();
double x, y, vx, vy, angle;
int cooldown;
boolean started, lost;
/// Supplied restart: restore the initial model before the UI uses it.
void restart() {
    x = WIDTH / 2;
    y = HEIGHT / 2;
    vx = vy = 0;
    angle = -Math.PI / 2;
    cooldown = 0;
    started = lost = false;
    rocks.clear();
    shots.clear();
    for (int i = 0; i < 5; i++) {
        rocks.add(new Rock(40 + i * 115, 40, .6 + i * .1, .45, 19));
    }
}

double wrap(double value, double size) {
    return (value % size + size) % size;
}

void fire() {
    if (cooldown != 0 || lost || rocks.isEmpty()) {
        return;
    }
    started = true;
    shots.add(new Shot(x + Math.cos(angle) * 15, y + Math.sin(angle) * 15,
    vx + Math.cos(angle) * 6, vy + Math.sin(angle) * 6, 70));
    cooldown = 10;
}

/// Supplied bounded update: one call means one model step; the active repair is described above.
void step() {
    if (!started || lost || rocks.isEmpty()) {
        return;
    }
    if (held[KeyEvent.VK_LEFT]) {
        angle -= .07;
    }
    if (held[KeyEvent.VK_RIGHT]) {
        angle += .07;
    }
    if (held[KeyEvent.VK_UP]) {
        vx += Math.cos(angle) * .09;
        vy += Math.sin(angle) * .09;
    }
    double speed = Math.hypot(vx, vy);
    if (speed > 5) {
        vx *= 5 / speed;
        vy *= 5 / speed;
    }
    x = wrap(x + vx, WIDTH);
    y = wrap(y + vy, HEIGHT);
    if (cooldown > 0) {
        cooldown--;
    }
    if (held[KeyEvent.VK_SPACE]) {
        fireCallback.run();
    }
    for (int i = 0; i < rocks.size(); i++) {
        var r = rocks.get(i);
        rocks.set(i, new Rock(wrap(r.x + r.vx, WIDTH), wrap(r.y + r.vy, HEIGHT), r.vx, r.vy, r.radius));
    }
    for (int i = shots.size() - 1; i >= 0; i--) {
        var s = shots.get(i);
        s = new Shot(wrap(s.x + s.vx, WIDTH), wrap(s.y + s.vy, HEIGHT), s.vx, s.vy, s.life - 1);
        boolean hit = false;
        for (int j = rocks.size() - 1; j >= 0; j--) {
            var r = rocks.get(j);
            if (Math.hypot(s.x - r.x, s.y - r.y) < r.radius + 2) {
                rocks.remove(j);
                hit = true;
                break;
            }
        }
        if (hit || s.life <= 0) {
            shots.remove(i);
        }
        else {
            shots.set(i, s);
        }
    }
    for (var r : rocks) {
        if (Math.hypot(x - r.x, y - r.y) < r.radius + 8) {
            lost = true;
        }
    }
}

/// Supplied drawing: reads model values; repainting is not another turn.
void draw(Graphics2D g) {
    g.setColor(new Color(16, 22, 38));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(Color.LIGHT_GRAY);
    for (var r : rocks) {
        g.drawOval((int)(r.x - r.radius), (int)(r.y - r.radius), (int)(2 * r.radius), (int)(2 * r.radius));
    }
    g.setColor(Color.ORANGE);
    for (var s : shots) {
        g.fillOval((int)s.x - 2, (int)s.y - 2, 4, 4);
    }
    var ship = new Path2D.Double();
    for (int i = 0; i < 3; i++) {
        double a = angle + (i == 0 ? 0 : i == 1 ? 2.5 : -2.5);
        double px = x + Math.cos(a) * 13, py = y + Math.sin(a) * 13;
        if (i == 0) {
            ship.moveTo(px, py);
        }
        else {
            ship.lineTo(px, py);
        }
    }
    ship.closePath();
    g.setColor(lost ? Color.RED : Color.CYAN);
    g.draw(ship);
    g.setColor(Color.WHITE);
    g.drawString(!started ? "Press an arrow or Space to launch" : lost ? "Ship lost! R: retry" : rocks.isEmpty() ? "All five cleared! R: replay" : "Rocks: " + rocks.size(), 15, 22);
}

boolean[] held = new boolean[512];
void clearKeys() {
    Arrays.fill(held, false);
}

void key(int code, boolean pressed) {
    if (code >= 0 && code < held.length) {
        held[code] = pressed;
    }
    if (pressed && code == KeyEvent.VK_R) {
/// Supplied restart: restore the initial model before the UI uses it.
        restart();
        clearKeys();
    }
    if (pressed && (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_UP || code == KeyEvent.VK_SPACE)) {
        started = true;
    }
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Asteroid Drift");
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
        @Override public void keyReleased(KeyEvent e) {
            key(e.getKeyCode(), false);
        }
    });
    var timer = new Timer(20, e -> {
/// Supplied bounded update: one call means one model step; the active repair is described above.
        step();
        panel.repaint();
    });
    frame.addWindowFocusListener(new WindowAdapter() {
        @Override public void windowLostFocus(WindowEvent e) {
            clearKeys();
        }
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent e) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Arrows: turn / thrust   Space: fire   R: restart</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

Runnable fireCallback = firingAction();

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
/// - `!=` — Checks that two values differ.
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
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `else` — The alternative when the preceding if condition is false.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
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

