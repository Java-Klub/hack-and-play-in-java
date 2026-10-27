// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L07.03 · TrailDuel
///
/// Share one keyboard and steer two glowing trails until someone runs out of safe space. A steering
/// mistake leaves a permanent wall behind you.
///
/// **Your mission:** The up controls send a trail downward. Repair directionFor() while preserving both
/// keyboard layouts and the handling of unrelated keys.
///
/// **Today you learn:** Switch expressions: several input cases can share one returned value. A Direction
/// groups the horizontal and vertical changes for that key.
///
/// Run File and play together on one keyboard, with one person steering using W, A, S, D and the other
/// using the arrow keys.
/// After your repair: Save → Test File → play again.
Direction directionFor(int code) {
    return switch (code) {
        case KeyEvent.VK_W, KeyEvent.VK_UP -> new Direction(0, -1);
        case KeyEvent.VK_S, KeyEvent.VK_DOWN -> new Direction(0, 1);
        case KeyEvent.VK_A, KeyEvent.VK_LEFT -> new Direction(-1, 0);
        case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> new Direction(1, 0);
        default -> null;
    };
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `case KeyEvent.VK_W, KeyEvent.VK_UP` — Two key codes share one case instead of duplicating their
///   rule.
/// - `new Direction(0, 1)` — Constructs horizontal/vertical offsets; screen y increases downward.
/// - `default -> null` — An unrelated key produces no direction request.
/// - `return switch (code)` — Turns a key code into one value consumed by the existing input handler.
///
/// ### What the prepared part already does
///
/// key() sends the returned Direction to turn(), which prevents reversals and queues one turn. step()
/// checks both players against the same trail board before extending either trail.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. Screen y grows downward, so up has dy=-1.
/// 2. Grouped case labels can return the same record for two keys.
/// 3. Return Direction(0,-1) for W and Up; default remains null for non-direction keys.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 430;

int[][] trail;
int ax, ay, bx, by, adx, ady, bdx, bdy, anx, any, bnx, bny, result;
boolean started, aTurn, bTurn;
/// restart(): Restores the starting Trail Duel model so this toy can be tried again.
void restart() {
    trail = new int[20][30];
    ax = 5;
    ay = 10;
    bx = 24;
    by = 10;
    adx = anx = 1;
    ady = any = 0;
    bdx = bnx = -1;
    bdy = bny = 0;
    trail[ay][ax] = 1;
    trail[by][bx] = 2;
    result = 0;
    started = aTurn = bTurn = false;
}

boolean blocked(int x, int y) {
    return x < 0 || y < 0 || x >= 30 || y >= 20 || trail[y][x] != 0;
}

/// turn(): Queues at most one non-reversing turn for the chosen player.
void turn(boolean first, int dx, int dy) {
    if (result != 0) {
        return;
    }
    if (first && !aTurn && dx != -adx && dy != -ady) {
        anx = dx;
        any = dy;
        aTurn = true;
    }
    if (!first && !bTurn && dx != -bdx && dy != -bdy) {
        bnx = dx;
        bny = dy;
        bTurn = true;
    }
    started = true;
}

/// step(): Supplied update evaluates both destinations against the same trail board before adding
/// either head.
void step() {
    if (!started || result != 0) {
        return;
    }
    adx = anx;
    ady = any;
    bdx = bnx;
    bdy = bny;
    aTurn = bTurn = false;
    int xa = ax + adx, ya = ay + ady, xb = bx + bdx, yb = by + bdy;
    boolean deadA = blocked(xa, ya), deadB = blocked(xb, yb);
    if (xa == xb && ya == yb) {
        deadA = deadB = true;
    }
    if (deadA || deadB) {
        result = deadA && deadB ? 3 : deadA ? 2 : 1;
        return;
    }
    ax = xa;
    ay = ya;
    bx = xb;
    by = yb;
    trail[ay][ax] = 1;
    trail[by][bx] = 2;
}

/// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
void draw(Graphics2D g) {
    g.setColor(new Color(18, 23, 35));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    for (int y = 0; y < 20; y++) {
        for (int x = 0; x < 30; x++) {
            if (trail[y][x] != 0) {
                g.setColor(trail[y][x] == 1 ? new Color(40, 200, 215) : new Color(255, 145, 65));
                g.fillRect(x * 20 + 1, y * 20 + 1, 18, 18);
            }
        }
    }
    g.setColor(Color.WHITE);
    g.drawRect(ax * 20, ay * 20, 19, 19);
    g.drawRect(bx * 20, by * 20, 19, 19);
    g.drawString(result == 3 ? "TIE - both crashed" : result == 1 ? "CYAN WINS" : result == 2 ? "ORANGE WINS" : !started ? "Press a direction to start" : "Avoid every trail, including your own", 12, 418);
}

void key(int code, boolean pressed) {
    if (!pressed) {
        return;
    }
    if (code == KeyEvent.VK_R) {
        restart();
        return;
    }
    var direction = directionFor(code);
    if (direction != null) {
        boolean first = code == KeyEvent.VK_W || code == KeyEvent.VK_A || code == KeyEvent.VK_S || code == KeyEvent.VK_D;
        turn(first, direction.dx(), direction.dy());
    }
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; timers stop on
/// window close.
void showWindow() {
    var frame = new JFrame("Trail Duel");
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
    var timer = new Timer(100, e -> {
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
    var help = new JLabel("<html><div style=\"text-align:center\">Cyan: W A S D   Orange: arrows   R: restart</div></html>", SwingConstants.CENTER);
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

record Direction(int dx, int dy) {
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
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>

