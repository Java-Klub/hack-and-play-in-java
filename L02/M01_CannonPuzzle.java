// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L02.01 · CannonPuzzle
///
/// Aim a cannon at the orange target and watch a shot curve under gravity. Each attempt gives you a
/// visible way to compare direction and launch power.
///
/// **Your mission · Wake the cannon:** The starter drops the shot at the muzzle because its calculated
/// speed is zero.
/// Repair launchSpeed so a drag of 100 gives speed 9 and an upward drag keeps its negative sign.
///
/// **Today you learn:** Arithmetic and numeric types: intermediate calculations matter. For example,
/// integer 5 / 2 gives 2, while a fractional operand can keep the result 2.5; the final return type does
/// not recover a fraction already lost.
///
/// Run the program, drag from the cannon towards the upper right and release to shoot at the
/// orange target; R prepares another attempt.
double launchSpeed(double drag) {
    return drag * 0.09;
}

// <editor-fold defaultstate="expanded" desc="Follow the values · predict a case">
/// A complete call has a route:
///
/// ```java
/// double half(double amount) {
///     return amount / 2;
/// }
/// double piece = half(9);
/// ```
///
/// The caller supplies argument 9. During this call parameter amount holds 9.0. The expression
/// produces 4.5; return sends it back and piece stores it. A declaration describes the input; a call
/// supplies its value. Return does not print. Predict half(-6), then follow launchSpeed(100) using
/// the same four steps.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Learn · F04">
/// Arithmetic uses the operand types at the moment an operation runs. When both operands of `/` are
/// whole numbers, Java discards the fractional part. Storing that result in a `double` afterwards
/// does not recover it.
///
/// These example statements belong inside a called method.
///
/// ```java
/// int pages = 7;
/// double first = pages / 2;
/// double second = pages / 2.0;
/// double third = (double) pages / 2;
/// ```
///
/// Predict the three values: `3.0`, `3.5`, `3.5`. The cast `(double)` converts this use of `pages`
/// before division; it does not change the variable’s declared type. Parentheses group calculations,
/// while `*` multiplies and `/` divides. Check a negative input and zero as well as a positive one.
/// Name the units: a distance and a speed are different quantities even when both use `double`.
///
/// Now predict all three results when pages is nine.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `double launchSpeed(double drag)` — A named calculation receives one signed drag distance and
///   returns a possibly fractional speed.
/// - `return` — Send the calculated speed back to launch, rather than printing it.
/// - `drag` — The distance supplied for this call; horizontal and vertical calls can receive different
///   values.
/// - `*` — Multiply the drag by the scaling result.
/// - `(9 / 100)` — Parentheses make this division happen first; the two integer literals currently
///   produce zero.
/// - `;` — End the returned expression after it has been evaluated.
///
/// ### What the prepared part already does
///
/// aim limits the direction and drag length. launch calls your calculation for both velocities, step adds
/// gravity and detects hits, and the remaining window code supplies the mouse and timer.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// After repairing the conversion, try two existing aim directions with the same pull length. Predict
/// which shot rises higher. Then deliberately change only the scale and see whether your first
/// prediction about direction still holds.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Evaluate 9 / 100 on its own: both numbers are integers.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Use a fractional factor rather than the truncated integer result.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Return drag * 0.09, or drag * 9.0 / 100.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 420;

double x, y, vx, vy, aimX, aimY;
boolean flying, hit, spent;
void restart() {
    x = 60;
    y = 350;
    vx = vy = 0;
    aimX = 150;
    aimY = 270;
    flying = hit = spent = false;
}

void aim(int xx, int yy) {
    if (flying || spent) {
        return;
    }
    double dx = xx - 60, dy = yy - 350, length = Math.hypot(dx, dy);
    if (length > 160) {
        dx *= 160 / length;
        dy *= 160 / length;
    }
    aimX = 60 + Math.max(0, dx);
    aimY = 350 + Math.min(0, dy);
}

void launch() {
    if (flying || spent) {
        return;
    }
    vx = launchSpeed(aimX - 60);
    vy = launchSpeed(aimY - 350);
    flying = true;
}

void step() {
    if (!flying) {
        return;
    }
    x += vx;
    y += vy;
    vy += .24;
    double xx = Math.max(490, Math.min(545, x)), yy = Math.max(275, Math.min(350, y));
    if (Math.hypot(x - xx, y - yy) <= 6) {
        hit = true;
        flying = false;
        spent = true;
    }
    else {
        if (y >= 350 || x > WIDTH + 6 || x<-6) {
            flying = false;
            spent = true;
        }
    }
}

void draw(Graphics2D g) {
    g.setColor(new Color(230, 242, 255));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(new Color(70, 135, 80));
    g.fillRect(0, 350, WIDTH, 70);
    g.setColor(hit ? Color.GREEN : Color.ORANGE);
    g.fillRect(490, 275, 55, 75);
    g.setColor(Color.DARK_GRAY);
    g.setStroke(new BasicStroke(12));
    g.drawLine(60, 350, (int)(60 + (aimX - 60) * .3), (int)(350 + (aimY - 350) * .3));
    g.fillOval(44, 334, 32, 32);
    if (!flying && !spent) {
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.GRAY);
        g.drawLine(60, 350, (int)aimX, (int)aimY);
    }
    g.setColor(Color.RED);
    g.fillOval((int)x - 6, (int)y - 6, 12, 12);
    g.setColor(Color.BLACK);
    g.drawString(hit ? "Target hit! R: replay" : spent ? "Missed - R: retry" : "Drag upward/right to aim; release to launch", 15, 25);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    aim(x, y);
}

void drag(int x, int y) {
    aim(x, y);
}

void release(int x, int y) {
    aim(x, y);
    launch();
}

void showWindow() {
    var frame = new JFrame("Cannon Puzzle");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Drag from the cannon toward your aim; release: fire   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
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
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `else` — The alternative when the preceding if condition is false.
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
