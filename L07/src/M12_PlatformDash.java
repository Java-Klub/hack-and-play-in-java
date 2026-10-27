// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
import javax.swing.Timer;
// </editor-fold>

/// # L07.12 · PlatformDash
///
/// Run and jump across a few platforms to reach the flag. Jumping upward through a platform and landing
/// on it later should feel natural.
///
/// **Your mission:** Platforms snap the player onto their top even during the wrong part of a jump.
/// Repair the landing branch of step() to accept genuine top crossings only.
///
/// **Today you learn:** Invariants: a landing must agree with direction and previous position, not just
/// overlap in the newest frame. Preserve the distinction between rising, falling, and standing.
///
/// Run File, run with Left and Right, and press Space to jump toward the platforms and flag.
/// After your repair: Save → Test File → play again.
void step() {
    if (!started || won) {
        return;
    }
    x += (held[KeyEvent.VK_RIGHT] ? 3.4 : 0) - (held[KeyEvent.VK_LEFT] ? 3.4 : 0);
    x = Math.max(0, Math.min(WIDTH - 20, x));
    double oldBottom = y + 28;
    vy += .25;
    y += vy;
    grounded = false;
    if (true) {
        for (var p : platforms) {
            if (x + 20 > p.x && x < p.x + p.width && y + 28 >= p.y) {
                y = p.y - 28;
                vy = 0;
                grounded = true;
                break;
            }
        }
    }
    if (grounded && x > 495 && y < 200) {
        won = true;
    }
    if (y > HEIGHT) {
        falls++;
        attempt();
        clearKeys();
    }
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `double oldBottom = y + 28` — Remembers the feet position before this update’s vertical movement.
/// - `vy += .25` — Adds gravity to vertical velocity before updating position.
/// - `x + 20 > p.x && x < p.x + p.width` — Tests horizontal overlap between the player and one platform.
/// - `grounded = true` — Declares that a real landing occurred, which permits the next jump.
///
/// ### What the prepared part already does
///
/// jump() supplies upward velocity only while grounded. The rest of step() applies input and gravity,
/// checks the flag, and restarts a fallen attempt; platform drawing uses the same rectangles.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. A current overlap alone does not say which side the player came from.
/// 2. Compare oldBottom with the platform top before the movement.
/// 3. Require vy>=0 and oldBottom<=p.y as well as the new bottom crossing p.y.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 420;

Rectangle[] platforms = {new Rectangle(30, 360, 150, 14), new Rectangle(220, 280, 130, 14), new Rectangle(400, 200, 150, 14)};
double x, y, vy;
boolean started, grounded, won;
int falls;
void attempt() {
    x = 60;
    y = 332;
    vy = 0;
    grounded = true;
    started = false;
}

/// restart(): Restores the starting Platform Dash model so this toy can be tried again.
void restart() {
    falls = 0;
    won = false;
    attempt();
}

/// jump(): Starts upward velocity only while grounded; there is no mid-air jump.
void jump() {
    if (!won && grounded) {
        started = true;
        grounded = false;
        vy = -8.8;
    }
}

/// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
void draw(Graphics2D g) {
    g.setColor(new Color(225, 240, 255));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(new Color(55, 100, 85));
    for (var p : platforms) {
        g.fill(p);
    }
    g.setColor(Color.ORANGE);
    g.fillRect(520, 160, 8, 40);
    g.fillPolygon(new int[]{528, 555, 528}, new int[]{160, 170, 180}, 3);
    g.setColor(new Color(60, 80, 200));
    g.fillRect((int)x, (int)y, 20, 28);
    g.setColor(Color.BLACK);
    g.drawString(won ? "Goal reached! R: replay" : "Falls: " + falls + (!started ? " - press an arrow or Space" : ""), 15, 25);
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
    if (pressed && (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT)) {
        started = true;
    }
    if (pressed && code == KeyEvent.VK_SPACE) {
        jump();
    }
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; timers stop on
/// window close.
void showWindow() {
    var frame = new JFrame("Platform Dash");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Left / Right: run   Space: jump   Reach the flag   R: restart</div></html>", SwingConstants.CENTER);
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

