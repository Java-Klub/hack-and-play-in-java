// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # 13.15 · SoftLanding
///
/// The limited-tank lander already works. Turn it into a flight trainer so you can practise the landing
/// rather than ration a shrinking tank.
///
/// **Your mission:** Make thrust work even with a zero fuel counter and never spend that counter.
/// Preserve the original engine strengths, independent gravity, combined controls, touchdown rules and
/// terminal crashes. Find which conditions actually restrict powered motion; simply drawing a larger
/// flame is not enough.
///
/// **Today you learn:** Separate the invariant you intentionally remove from the physical rules you
/// keep. A visual effect and an acceleration are different observations.
///
/// Up brakes the fall; Left/Right steer; R restarts. In the normal game, watch fuel fall while
/// thrusting. After the hack it stays fixed, but releasing all keys still lets gravity accelerate the
/// fall.

// <editor-fold defaultstate="expanded" desc="Program · explore the rules">
void step() {
    if (!state.equals("FLYING")) {
        return;
    }
    velocityY += 0.04;
    applyThrust();
    x += velocityX;
    y += velocityY;
    if (x < HALF || x > WIDTH - HALF || y < HALF) {
        state = "CRASH";
        return;
    }
    if (y + HALF >= GROUND) {
        y = GROUND - HALF;
        boolean onPad = x - HALF >= PAD_X && x + HALF <= PAD_X + PAD_WIDTH;
        boolean gentle = Math.abs(velocityX) <= 1.2 && velocityY >= 0 && velocityY <= 1.5;
        state = onPad && gentle ? "LANDED!" : "CRASH";
    }
}

void applyThrust() {
    if (left) velocityX -= 0.06;
    if (right) velocityX += 0.06;
    if (up) velocityY -= 0.09;
}

final int WIDTH = 600, HEIGHT = 420, GROUND = 385, PAD_X = 380, PAD_WIDTH = 140, HALF = 12;
double x, y, velocityX, velocityY;
int fuel;
boolean left, right, up;
String state;

/// clearKeys(): Releases held thrust on restart and focus loss so input cannot remain stuck.
void clearKeys() {
    left = right = up = false;
}

/// restart(): Restores the starting Soft Landing model so this toy can be tried again.
void restart() {
    x = 150;
    y = 60;
    velocityX = 1.2;
    velocityY = 0;
    fuel = 240;
    state = "READY";
    clearKeys();
}

void begin() {
    if (state.equals("READY")) {
        state = "FLYING";
    }
}

/// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
void draw(Graphics g) {
    g.setColor(new Color(14, 20, 39));
    g.fillRect(0, 0, WIDTH, HEIGHT + 60);
    g.setColor(new Color(74, 79, 101));
    g.fillRect(0, GROUND, WIDTH, HEIGHT - GROUND);
    g.setColor(new Color(87, 226, 166));
    g.fillRect(PAD_X, GROUND, PAD_WIDTH, 8);
    g.setColor(Color.WHITE);
    g.drawString("LAND HERE", PAD_X + 34, GROUND + 25);
    if (state.equals("FLYING")) {
        g.setColor(new Color(255, 164, 70));
        if (up) {
            g.fillPolygon(new int[]{(int) x - 7, (int) x, (int) x + 7},
            new int[]{(int) y + HALF, (int) y + 30, (int) y + HALF}, 3);
        }
        if (left) {
            g.fillRect((int) x + HALF, (int) y - 3, 12, 6);
        }
        if (right) {
            g.fillRect((int) x - HALF - 12, (int) y - 3, 12, 6);
        }
    }
    g.setColor(state.equals("CRASH") ? new Color(255, 115, 120) : new Color(126, 209, 255));
    g.fillRoundRect((int) x - HALF, (int) y - HALF, HALF * 2, HALF * 2, 6, 6);
    g.setColor(Color.WHITE);
    g.fillOval((int) x - 5, (int) y - 6, 10, 9);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
    g.drawString(state + "    Fuel: " + fuel, 14, HEIGHT + 22);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Speed X: %.2f   Down: %.2f   Safe: |X| <= 1.2, Down <= 1.5".formatted(velocityX, velocityY), 14, HEIGHT + 41);
    g.drawString("Arrows: thrust and start     Up: brake fall     R: restart", 14, HEIGHT + 57);
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; timers stop on
/// window close.
void showWindow() {
    var frame = new JFrame("Soft Landing");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT + 60));
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        void change(KeyEvent event, boolean pressed) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    left = pressed;
                    if (pressed) {
                        begin();
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    right = pressed;
                    if (pressed) {
                        begin();
                    }
                }
                case KeyEvent.VK_UP -> {
                    up = pressed;
                    if (pressed) {
                        begin();
                    }
                }
                case KeyEvent.VK_R -> {
                    if (pressed) {
                        restart();
                    }
                }
            }
        }
        @Override public void keyPressed(KeyEvent event) {
            change(event, true);
        }
        @Override public void keyReleased(KeyEvent event) {
            change(event, false);
        }
    });
    var timer = new Timer(20, event -> {
        step();
        panel.repaint();
    });
    frame.addWindowFocusListener(new WindowAdapter() {
        @Override public void windowLostFocus(WindowEvent event) {
            clearKeys();
        }
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent event) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
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
/// Write the promise before changing the rule: a total stays constant, a resource stays non-negative,
/// or a shape stays outside a wall. Then list every field that one action must change together. Predict
/// the two jar values for amount 0 and amount 7. An invariant describes the intended world; it does not
/// forbid inventing a different, clearly stated rule in your own variant.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ### Observe, then trace the flow
///
/// An input can influence a simulation through several stages: a held-key flag, an acceleration, a
/// changed position and finally a drawing. Trace the acceleration path first. The training rule removes
/// scarcity; it does not remove falling, collisions or the need for a gentle landing. Opposing
/// horizontal engines can cancel while the upward engine still works.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Experiment and replay">
/// Compare three short replays: no keys, Up only, and Up with both horizontal keys. The counter must
/// stay fixed in all three. Speed still reveals gravity and engine effects. A crash must remain stopped
/// until R; unlimited fuel is not invulnerability.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Watch the counter and vertical speed separately. Which must change after releasing Up?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Bound the search to the small engine action and the separate gravity update, not the landing
/// geometry. Search the counter’s reads and writes.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// applyThrust contains the fuel gate and fuel decrement. Remove the scarcity rules while retaining the
/// independent key contributions; step still adds gravity. The flame condition in draw should agree
/// with unlimited training thrust.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Create a low-gravity training planet, then try to land using only three planned bursts. Keep the
/// training tank unlimited so only your flight plan changes.
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
/// - `final` — The named value cannot be assigned another value afterwards. A referenced mutable object
///   can still change.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
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
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
