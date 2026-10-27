// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L02.04 · Spirograph
///
/// Draw closed, looping curves inspired by a pen carried around inside a larger circle. Change the inner
/// radius and pen distance to collect very different patterns.
///
/// **Your mission:** The curve calculation rounds away part of the radius ratio. Repair radiusRatio so
/// integer radii can still give fractional ratios, including a result smaller than one.
///
/// **Today you learn:** Type of an expression: Java evaluates the division before returning it. A double
/// result declaration alone cannot rescue integer division; use an example such as 40 divided by 80 to
/// reason about the intended fraction.
///
/// Use Run File, change the inner radius with Left/Right and the pen distance with Up/Down, then compare
/// the redrawn curves.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F04 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
double radiusRatio(int outer, int innerRadius) {
    return (outer - innerRadius) / innerRadius;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F04">
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
/// - `double radiusRatio(int outer, int innerRadius)` — The result can be fractional, although the two
///   received radii are whole numbers.
/// - `outer - innerRadius` — Subtract the smaller radius from the larger one before calculating their
///   ratio.
/// - `(outer - innerRadius)` — Parentheses keep this subtraction together as the numerator.
/// - `/ innerRadius` — The division currently has two whole-number operands; its fractional part can be
///   lost here.
/// - `return` — Give the completed ratio to point, which uses it in the real curve.
///
/// ### What the prepared part already does
///
/// point turns the ratio into pen positions. period and gcd determine when the curve closes; render
/// joins positions into lines, while the prepared keyboard controls change the two shape
/// parameters.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="For this game">
/// The radii arrive as int parameters, so an expression made only from those values can still use
/// integer division inside a double-returning method. A cast applies to the value immediately
/// following it. Neutral example: with seven seats and four rows, `(double) seats / rows` gives 1.75,
/// while `(double) (seats / rows)` gives only 1.0 because the fraction was already discarded. The
/// cast does not change the parameter declaration. Now predict those two expressions for five seats
/// and two rows, then decide where a conversion belongs in your own calculation.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// A double return type does not undo earlier integer division.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Convert one operand before the division.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Use (outer - innerRadius) / (double) innerRadius.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 640, HEIGHT = 440;

int inner, pen;
BufferedImage canvas;
int gcd(int a, int b) {
    while (b != 0) {
        int next = a % b;
        a = b;
        b = next;
    }
    return a;
}

double period() {
    return 2 * Math.PI * inner / gcd(120, inner);
}

Point2D.Double point(double t) {
    double a = 120 - inner;
    return new Point2D.Double(a * Math.cos(t) + pen * Math.cos(radiusRatio(120, inner) * t), a * Math.sin(t) - pen * Math.sin(radiusRatio(120, inner) * t));
}

int samples() {
    return Math.min(14000, (int)(period() * 280) + 1);
}

void render() {
    canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    var g = canvas.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(Color.getHSBColor(0, .75f, .75f));
    double scale = (HEIGHT - 65) / 2.0 / Math.max(1, 120 - inner + pen), period = period();
    var previous = point(0);
    int n = samples();
    for (int i = 1; i <= n; i++) {
        var p = point(period * i / n);
        g.draw(new Line2D.Double(WIDTH / 2 + previous.x * scale, HEIGHT / 2 + previous.y * scale, WIDTH / 2 + p.x * scale, HEIGHT / 2 + p.y * scale));
        previous = p;
    }
    g.dispose();
}

void restart() {
    inner = 45;
    pen = 65;
    render();
}

void adjust(int r, int d) {
    inner = Math.max(15, Math.min(110, inner + r));
    pen = Math.max(0, Math.min(140, pen + d));
    render();
}

void draw(Graphics2D g) {
    g.drawImage(canvas, 0, 0, null);
    g.setColor(Color.BLACK);
    g.drawString("Inner radius: " + inner + "  Pen distance: " + pen, 10, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_LEFT -> adjust(-5, 0);
            case KeyEvent.VK_RIGHT -> adjust(5, 0);
            case KeyEvent.VK_UP -> adjust(0, 5);
            case KeyEvent.VK_DOWN -> adjust(0, -5);
        }
    }
}

void showWindow() {
    var frame = new JFrame("Spirograph");
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
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Left/Right: inner radius   Up/Down: pen distance   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

void main() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("This game needs a graphical display; model checks still work headlessly.");
        return;
    }
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}

/// Prepared entry points: main, restart, draw, render, showWindow.
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
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `%` — The remainder after division; useful for repeated positions or turns.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
/// - `/` — Divides numbers. Dividing two int values discards the fractional part.
/// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
///   direction. In List<String>, angle brackets describe the element type.
/// - `:` — In for (int value : values), reads each element in turn. It also separates the two
///   alternatives of ? : or follows a switch label.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `while` — Keeps repeating its body while its condition stays true.
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
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>

