// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Point2D;
import javax.swing.Timer;
// </editor-fold>

/// # L10.07 · Moire
///
/// Lay fine grids over each other and watch large moving bands appear. The moiré pattern comes from their
/// overlap rather than from drawing those bands directly.
///
/// **Your mission:** The rotated grid is centred horizontally but pinned vertically to the top. Repair
/// its screen coordinates so rotation happens around the intended centre.
///
/// **Today you learn:** Grid coordinates and screen coordinates use different origins. The rotation
/// formula is supplied; both resulting axes must be placed around the same canvas centre.
///
/// After Run File, rotate with Left/Right, change grid spacing with Up/Down and press Space to animate
/// the overlap.
/// After your repair: Save → Test File → play again.
Point2D.Double gridPoint(int column, int row, double a) {
    double x = column * spacing, y = row * spacing;
    return new Point2D.Double(WIDTH / 2 + x * Math.cos(a) - y * Math.sin(a), HEIGHT / 2 + x * Math.sin(a) + y * Math.cos(a));
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `column * spacing` — Turns a grid-column number into a horizontal distance from the grid origin.
/// - `Math.cos(a)` — Part of the supplied rotation formula; a is an angle in radians.
/// - `WIDTH / 2 +` — Adds the horizontal screen-centre offset after rotation.
/// - `x * Math.sin(a) + y * Math.cos(a)` — Produces a rotated vertical distance, not yet its absolute
///   screen position.
///
/// ### What the prepared part already does
///
/// The supplied grid painter joins transformed endpoints. Keyboard input changes angle and spacing, while
/// optional ticks animate the angle; gridPoint is the shared conversion used for actual drawing.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. The local point (0,0) should land at the canvas centre.
/// 2. The y result needs its centre offset just as x does.
/// 3. Check (1,0) at a quarter turn to expose axis/sign mistakes.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 640, HEIGHT = 440;

double angle;
int spacing;
boolean paused, third;
/// Supplied restart: restore the initial model before the UI uses it.
void restart() {
    angle = .07;
    spacing = 22;
    paused = true;
    third = false;
}

void adjust(int amount) {
    spacing = Math.max(12, Math.min(40, spacing + amount));
}

int extent() {
    return (int)Math.ceil(Math.hypot(WIDTH, HEIGHT) / 2 / spacing) + 1;
}

/// Supplied bounded update: one call means one model step; the active repair is described above.
void step() {
    if (!paused) {
        angle = (angle + .0015) % (2 * Math.PI);
    }
}

/// Supplied drawing: reads model values; repainting is not another turn.
void draw(Graphics2D g) {
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    int limit = extent();
    for (int layer = 0; layer < (third ? 3 : 2); layer++) {
        double a = layer == 0 ? 0 : layer == 1 ? angle : -angle;
        g.setColor(layer == 0 ? new Color(20, 20, 20, 200) : layer == 1 ? new Color(40, 80, 170, 170) : new Color(180, 60, 80, 150));
        for (int y = -limit; y <= limit; y++) {
            for (int x = -limit; x <= limit; x++) {
                var p = gridPoint(x, y, a);
                g.fillOval((int)p.x - 2, (int)p.y - 2, 4, 4);
            }
        }
    }
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, 28);
    g.setColor(Color.BLACK);
    g.drawString(String.format("Angle %.2f degrees   Spacing %d   %s", Math.toDegrees(angle), spacing, paused ? "Paused" : "Rotating"), 10, 20);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Supplied restart: restore the initial model before the UI uses it.
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_SPACE -> paused = !paused;
            case KeyEvent.VK_LEFT -> angle -= .01;
            case KeyEvent.VK_RIGHT -> angle += .01;
            case KeyEvent.VK_UP -> adjust(1);
            case KeyEvent.VK_DOWN -> adjust(-1);
            case KeyEvent.VK_T -> third = !third;
        }
    }
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Moir\u00e9");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Space: play/pause   Left/Right: rotate   Up/Down: spacing   T: third grid   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

/// Start the real program with Run File. Test File checks the rule without opening a window.
/// gridPoint turns model values into screen points and is called by the real drawing path.
/// Input changes model parameters; draw only reads them; the supplied UI owns timer and close behaviour.
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
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>

