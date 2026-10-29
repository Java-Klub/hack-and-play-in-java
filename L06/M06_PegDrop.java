// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import javax.swing.Timer;
// </editor-fold>

/// # L06.06 · PegDrop
///
/// Drop a disc through pegs and aim for the valuable bins. The label below a bin should agree with the
/// points you receive.
///
/// **Your mission:** Every bin pays the first bin’s value. Repair scoreAt(int index) so a valid bin query
/// returns its own score without awarding points itself.
///
/// **Today you learn:** Pure contracts: reading a score and awarding a score are different
/// responsibilities. scoreAt() must return the requested value without altering totals or physics.
///
/// Run the program and click above a bin to release a disc, then compare the landing bin’s
/// label with the awarded points.
int scoreAt(int index) {
    // This supplied guard reports a bad index; exception handling comes in L09.
    if (index < 0 || index >= values.length) {
        throw new IllegalArgumentException("Unknown bin");
    }
    return values[0];
}

// <editor-fold defaultstate="collapsed" desc="Recall · F15">
/// ### Contracts: ask without changing the world
///
/// A contract describes accepted inputs and the meaning of a result. A pure calculation uses its
/// inputs without changing other state. A query may read the current model, but it should still leave
/// that model untouched. Reading an answer and committing an action are separate responsibilities.
///
/// ```java
/// int contrast(int light, int dark) {
///     return Math.abs(light - dark);
/// }
/// ```
///
/// `Math.abs` gives the nonnegative magnitude, removing a negative sign. This method can be called
/// repeatedly without consuming anything. Arguments `(9, 4)` give five: the difference is five.
/// Predict `(4, 9)`. Which stored values would you compare before and after a query to show that it
/// caused no side effect? A rejected candidate deserves the same protection.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int scoreAt(int index)` — A whole-number result is requested for one bin index.
/// - `index < 0 || index >= values.length` — The supplied guard recognises indices outside the available
///   bins.
/// - `values.length` — The number of stored bin scores; it is not itself a valid index.
/// - `return values[0]` — Reads one array entry and returns it; this does not add points to a total.
///
/// ### What the prepared part already does
///
/// step() already handles peg collisions and landing, then calls scoreAt() for the destination bin. Keep
/// the supplied bad-index guard; exceptions and their handling are explained in L09.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The values array is the scoring table drawn under the board.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The caller already decides when a disc has finished falling.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// The supplied guard already rejects invalid indices. Return values[index]; awarding score remains
/// in step().
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 440;

ArrayList<Point> pegs = new ArrayList<>();
double x, y, vx, vy;
boolean active, done;
int score, bin;
int[] values = {10, 30, 50, 100, 100, 50, 30, 10};
/// restart(): Restores the starting Peg Drop model so this toy can be tried again.
void restart() {
    pegs.clear();
    for (int row = 0; row < 7; row++) {
        for (int column = 0; column < 8; column++) {
            int xx = 35 + column * 75 + (row % 2) * 37;
            if (xx < WIDTH - 15) {
                pegs.add(new Point(xx, 75 + row * 40));
            }
        }
    }
    x = WIDTH / 2;
    y = 25;
    vx = vy = 0;
    active = done = false;
    score = 0;
    bin = -1;
}

void drop(int xx) {
    if (active || done) {
        return;
    }
    x = Math.max(20, Math.min(WIDTH - 20, xx));
    y = 25;
    vx = vy = 0;
    active = true;
}

/// step(): Uses bounded substeps, peg normals and separation, then awards the destination-bin
/// score.
void step() {
    if (!active) {
        return;
    }
    for (int sub = 0; sub < 4 && active; sub++) {
        vy = Math.min(7, vy + .04);
        x += vx / 4;
        y += vy / 4;
        for (var p : pegs) {
            double dx = x - p.x, dy = y - p.y, distance = Math.hypot(dx, dy);
            if (distance < 14) {
                double nx = distance<.001 ? 1 : dx / distance, ny = distance<.001 ? 0 : dy / distance;
                x = p.x + nx * 14.01;
                y = p.y + ny * 14.01;
                double normal = vx * nx + vy * ny;
                if (normal < 0) {
                    vx -= 1.65 * normal * nx;
                    vy -= 1.65 * normal * ny;
                }
                if (Math.abs(nx)<.1 && Math.abs(vx)<.1) {
                    vx += p.x % 2 == 0 ? .12 : -.12;
                }
            }
        }
        if (x < 9) {
            x = 9;
            vx = Math.abs(vx) * .7;
        }
        if (x > WIDTH - 9) {
            x = WIDTH - 9;
            vx = -Math.abs(vx) * .7;
        }
        vx = Math.max(-6, Math.min(6, vx));
        if (y >= HEIGHT - 12) {
            y = HEIGHT - 12;
            active = false;
            done = true;
            bin = Math.min(7, (int)x / 75);
            score = scoreAt(bin);
        }
    }
}

/// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
void draw(Graphics2D g) {
    g.setColor(new Color(235, 242, 250));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(new Color(80, 100, 130));
    for (var p : pegs) {
        g.fillOval(p.x - 5, p.y - 5, 10, 10);
    }
    g.setColor(Color.GRAY);
    for (int i = 0; i <= 8; i++) {
        g.drawLine(i * 75, 370, i * 75, HEIGHT);
    }
    for (int i = 0; i < 8; i++) {
        g.drawString("" + values[i], i * 75 + 25, HEIGHT - 20);
    }
    g.setColor(Color.ORANGE);
    g.fillOval((int)x - 9, (int)y - 9, 18, 18);
    g.setColor(Color.BLACK);
    g.drawString(done ? "Bin " + (bin + 1) + " - " + score + " points! R: another disc" : active ? "Bouncing..." : "Click across the top to choose and drop a disc", 12, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code == KeyEvent.VK_SPACE) {
        drop(WIDTH / 2);
    }
}

void press(int x, int y, int button) {
    drop(x);
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; timers stop on
/// window close.
void showWindow() {
    var frame = new JFrame("Peg Drop");
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
    };
    panel.addMouseListener(mouse);
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click: choose a drop position   Space: centre drop   R: next disc</div></html>", SwingConstants.CENTER);
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
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `public` — Makes this declaration accessible to other code.
/// - `protected` — A visibility keyword used by supplied library callbacks, such as paintComponent.
/// - `super` — In super.paintComponent(g), asks the supplied parent implementation to paint its standard
///   background first.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
