// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
import javax.swing.Timer;
// </editor-fold>

/// # L07.11 · TiltTray
///
/// Tilt a tray and guide a marble around wooden blocks into a green target. Arriving slowly is harder
/// than merely touching the goal.
///
/// **Your mission:** A side collision reverses speed but leaves the marble inside the obstacle, causing
/// rattling. Repair horizontal separation in step().
///
/// **Today you learn:** Invariants: after collision handling, the marble must no longer overlap the block
/// on the resolved axis. Changing velocity alone cannot repair an invalid position.
///
/// Run the program and use the arrow keys to tilt the marble around blocks, braking before it
/// reaches the green target.
void step() {
    if (!started || won) {
        return;
    }
    vx += (held[KeyEvent.VK_RIGHT] ? .13 : 0) - (held[KeyEvent.VK_LEFT] ? .13 : 0);
    vy += (held[KeyEvent.VK_DOWN] ? .13 : 0) - (held[KeyEvent.VK_UP] ? .13 : 0);
    vx = Math.max(-3.5, Math.min(3.5, vx * .985));
    vy = Math.max(-3.5, Math.min(3.5, vy * .985));
    x += vx;
    for (var p : obstacles) {
        if (hit(p)) {
            x = vx > 0 ? p.x - 10 : p.x + p.width + 10;
            vx *= -.3;
        }
    }
    y += vy;
    for (var p : obstacles) {
        if (hit(p)) {
            y = vy > 0 ? p.y - 10 : p.y + p.height + 10;
            vy *= -.3;
        }
    }
    if (x < 20) {
        x = 20;
        vx = Math.abs(vx) * .3;
    }
    if (x > 580) {
        x = 580;
        vx = -Math.abs(vx) * .3;
    }
    if (y < 20) {
        y = 20;
        vy = Math.abs(vy) * .3;
    }
    if (y > 400) {
        y = 400;
        vy = -Math.abs(vy) * .3;
    }
    if (Math.hypot(x - 530, y - 345)<19 && Math.hypot(vx, vy)<1.5) {
        won = true;
    }
}

// <editor-fold defaultstate="collapsed" desc="Recall · F25">
/// ### An invariant survives each action
///
/// An invariant is a promise about valid state. It is useful before and after a move, not merely when
/// the game ends. Think of moving glow stones between two jars:
///
/// ```java
/// int violet = 6;
/// int gold = 2;
/// int amount = 3;
/// if (amount >= 0 && amount <= violet) {
///     violet -= amount;
///     gold += amount;
/// }
/// IO.println(violet + gold);
/// ```
///
/// Trace: `(6, 2)` becomes `(3, 5)`, and the printed total is still 8. The two assignments belong to
/// one accepted action. Changing only one jar would silently create or destroy stones. A request for 9
/// fails the guard and changes neither jar.
///
/// Write the promise before writing the repair: a total stays constant, a resource stays non-negative,
/// or a shape stays outside a wall. Then list every field that one action must change together. Predict
/// the two jar values for amount 0 and amount 7. An invariant describes the intended world; it does not
/// forbid inventing a different, clearly stated rule in your own variant.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `x += vx` — Advances horizontal position before obstacles are tested.
/// - `if (hit(p))` — Asks the supplied geometric query whether this block overlaps the marble.
/// - `x = x` — Assigns the same position back to itself, leaving overlap unresolved.
/// - `vx *= -.3` — Reverses and reduces horizontal velocity to model an inelastic bounce.
///
/// ### What the prepared part already does
///
/// hit() already tests a circle against a rectangle using the nearest point. step() resolves horizontal
/// then vertical movement, and separately handles tray walls and the slow-arrival target.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Reversing vx alone leaves the centre at its already-penetrating position.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The collision direction tells which obstacle side the ball came from.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Place x at p.x-10 when moving right, or p.x+p.width+10 when moving left, then damp/reverse vx.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Rearrange two obstacle rectangles into a new route. Place the start outside every obstacle and test
/// both approaches to one block.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 420;

Rectangle[] obstacles = {new Rectangle(180, 70, 40, 230), new Rectangle(350, 200, 130, 35)};
double x, y, vx, vy;
boolean started, won;
/// restart(): Restores the starting Tilt Tray model so this toy can be tried again.
void restart() {
    x = 65;
    y = 65;
    vx = vy = 0;
    started = won = false;
}

/// hit(): Supplied nearest-point geometry tests a circle against one rectangular obstacle.
boolean hit(Rectangle p) {
    return Math.hypot(x - Math.max(p.x, Math.min(p.x + p.width, x)), y - Math.max(p.y, Math.min(p.y + p.height, y)))<10;
}

/// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
void draw(Graphics2D g) {
    g.setColor(new Color(235, 222, 190));
    g.fillRect(10, 10, 580, 400);
    g.setColor(new Color(110, 85, 60));
    for (var p : obstacles) {
        g.fill(p);
    }
    g.setColor(new Color(80, 180, 120));
    g.fillOval(502, 317, 56, 56);
    g.setColor(new Color(65, 100, 180));
    g.fillOval((int)x - 10, (int)y - 10, 20, 20);
    g.setColor(Color.BLACK);
    g.drawString(won ? "Balanced into the target! R: replay" : !started ? "Arrow keys tilt the tray" : "Brake with the opposite arrow; enter green slowly", 20, 35);
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
        restart();
        clearKeys();
    }
    if (pressed && (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN)) {
        started = true;
    }
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; timers stop on
/// window close.
void showWindow() {
    var frame = new JFrame("Tilt Tray");
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
        @Override public void keyReleased(KeyEvent e) {
            key(e.getKeyCode(), false);
        }
    });
    var timer = new Timer(20, e -> {
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
    var help = new JLabel("<html><div style=\"text-align:center\">Arrows: tilt / brake   Guide the ball slowly into green   R: restart</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
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
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
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
