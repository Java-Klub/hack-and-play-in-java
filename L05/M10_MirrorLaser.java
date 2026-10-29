// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import module java.base;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
// </editor-fold>

class M10_MirrorLaser {
    /// # L05.10 · MirrorLaser
    ///
    /// Turn mirrors to guide a laser to its receiver. A single misplaced step can make the beam appear to
    /// walk sideways.
    ///
    /// **Your mission:** The ray mixes up horizontal and vertical movement. Repair RayState.advance() so one
    /// step agrees with the direction carried by that ray.
    ///
    /// **Today you learn:** Records: bundle related values into one readable state. Moving creates a new ray
    /// value while the original position and direction remain available.
    ///
    /// From the course folder containing README.md:
    /// - Play: `java L05/M10_MirrorLaser.java`
    /// - Check this mission: `java L05/Check.java M10_MirrorLaser.java`
    ///
    /// the play command, click a mirror to rotate it, and press Space to trace the beam through your
    /// arrangement. After your repair: save the file, run the check command above, then play again.
    ///
    /// Need an explanation? Learn · F19 explains the Java skill; Read this code explains these lines.
    /// Hints 1 and 2 open separately; Repair outline reveals the direction.
    record RayState(int x, int y, int dx, int dy) {
        RayState advance() {
            return new RayState(x + dy, y + dx, dx, dy);
        }
    }

    // <editor-fold defaultstate="expanded" desc="Learn · F19">
    /// ### Records: one named value
    ///
    /// A record groups related components. Its constructor receives them in declaration order; accessors
    /// such as `ink()` read them. Inside the record, its own methods can use the component names
    /// directly. Making a new value leaves the original components unchanged. Generated `equals` compares
    /// the component values.
    ///
    /// ```java
    /// record Frame(String ink, int dots) {
    ///     Frame recoloured(String nextInk) {
    ///         return new Frame(nextInk, dots);
    ///     }
    /// }
    /// ```
    ///
    /// Calling `recoloured("gold")` on `new Frame("violet", 6)` returns gold and six; the original stays
    /// violet and six. Would another recolouring back to violet compare equal to the original? The same
    /// names now explain what two otherwise anonymous values mean.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Read this code">
    /// ## Read these particular lines
    ///
    /// - `record RayState(int x, int y, int dx, int dy)` — Names four components belonging to one ray state.
    /// - `RayState advance()` — This method returns another ray state rather than returning no result.
    /// - `new RayState(` — Constructs a separate record value from the supplied component values.
    /// - `dx, dy` — These two components carry horizontal and vertical direction into the next state.
    ///
    /// ### What the prepared part already does
    ///
    /// traceFrom() reflects directions at mirrors, tracks previously visited ray states to stop loops, and
    /// calls advance() between cells. The supplied painter draws that resulting path.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="For this game">
    /// ### Predict a value, not a moving object
    ///
    /// Before editing, write the four components of a ray at `(7,3)` whose direction is `(-1,0)`. Predict
    /// the components after one advance: which describe the new position and which still describe
    /// direction? The constructor's argument order follows the record declaration. Then explain why
    /// another variable still referring to the earlier RayState must retain its original components. No
    /// field mutation is needed to describe the next value.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
    /// A record bundles position and direction into one named value.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
    /// advance() must return a new value; it cannot mutate this record.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
    /// Pass x+dx, y+dy, dx, dy to the RayState constructor in that order.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
    final int WIDTH = 480, HEIGHT = 420;

    int[][] mirrors;
    ArrayList<Point> beam = new ArrayList<>();
    String result;
    /// restart(): Restores the starting Mirror Laser model so this toy can be tried again.
    void restart() {
        mirrors = new int[6][6];
        mirrors[2][2] = 2;
        mirrors[0][2] = 2;
        mirrors[0][5] = 1;
        beam.clear();
        result = "Rotate the three mirrors, then trace";
    }

    /// traceFrom(): Records visited position-plus-direction states, reflects at mirrors and builds the
    /// visible beam.
    String traceFrom(int x, int y, int dx, int dy) {
        var seen = new HashSet<RayState>();
        beam.clear();
        var ray = new RayState(x, y, dx, dy);
        while (ray.x() >= 0 && ray.y() >= 0 && ray.x() < 6 && ray.y() < 6) {
            if (!seen.add(ray)) {
                return "Loop detected";
            }
            beam.add(new Point(ray.x(), ray.y()));
            if (ray.x() == 5 && ray.y() == 5) {
                return "Target reached!";
            }
            int mirror = mirrors[ray.y()][ray.x()];
            int nextDx = ray.dx(), nextDy = ray.dy();
            if (mirror == 1) {
                nextDx = -ray.dy();
                nextDy = -ray.dx();
            }
            if (mirror == 2) {
                nextDx = ray.dy();
                nextDy = ray.dx();
            }
            ray = new RayState(ray.x(), ray.y(), nextDx, nextDy).advance();
        }
        beam.add(new Point(ray.x(), ray.y()));
        return "Beam escaped - try another rotation";
    }

    void trace() {
        result = traceFrom(0, 2, 1, 0);
    }

    void rotate(int x, int y) {
        if (x >= 0 && x < 6 && y >= 0 && y < 6 && mirrors[y][x] != 0) {
            mirrors[y][x] = 3 - mirrors[y][x];
            beam.clear();
            result = "Mirror rotated";
        }
    }

    /// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
    void draw(Graphics2D g) {
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= 6; i++) {
            g.drawLine(70 + i * 52, 35, 70 + i * 52, 347);
            g.drawLine(70, 35 + i * 52, 382, 35 + i * 52);
        }
        g.setStroke(new BasicStroke(4));
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 6; x++) {
                if (mirrors[y][x] != 0) {
                    g.setColor(Color.BLUE);
                    int xx = 70 + x * 52, yy = 35 + y * 52;
                    if (mirrors[y][x] == 1) {
                        g.drawLine(xx + 8, yy + 44, xx + 44, yy + 8);
                    }
                    else {
                        g.drawLine(xx + 8, yy + 8, xx + 44, yy + 44);
                    }
                }
            }
        }
        g.setColor(Color.RED);
        g.drawString("SOURCE >", 4, 171);
        g.setColor(new Color(40, 170, 80));
        g.fillOval(70 + 5 * 52 + 15, 35 + 5 * 52 + 15, 22, 22);
        g.setColor(Color.ORANGE);
        g.setStroke(new BasicStroke(2));
        int xx = 44, yy = 165;
        for (var p : beam) {
            int nx = 96 + p.x * 52, ny = 61 + p.y * 52;
            g.drawLine(xx, yy, nx, ny);
            xx = nx;
            yy = ny;
        }
        g.setColor(Color.BLACK);
        g.drawString(result, 40, 390);
    }

    void key(int code, boolean pressed) {
        if (pressed && code == KeyEvent.VK_R) {
            restart();
        }
        if (pressed && code == KeyEvent.VK_SPACE) {
            trace();
        }
    }

    void press(int x, int y, int button) {
        if (x >= 70 && y >= 35 && x < 382 && y < 347) {
            rotate((x - 70) / 52, (y - 35) / 52);
        }
    }

    /// showWindow(): Supplied later topic: Swing listeners forward input to the model; disposing the
    /// window closes this view.
    void showWindow() {
        var frame = new JFrame("Mirror Laser");
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
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        var help = new JLabel("<html><div style=\"text-align:center\">Click mirrors: rotate   Space: trace beam   R: restart</div></html>", SwingConstants.CENTER);
        help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        frame.add(help, BorderLayout.SOUTH);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        panel.requestFocusInWindow();
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
    /// - `!=` — Checks that two values differ.
    /// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
    /// - `!` — In front of a boolean expression, reverses true and false.
    /// - `++` — Increases a numeric value by one.
    /// - `+` — Adds numbers. When a String is involved, joins text instead.
    /// - `-` — Subtracts numbers; in front of one number, changes its sign.
    /// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
    ///   available.
    /// - `/` — Divides numbers. Dividing two int values discards the fractional part.
    /// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
    ///   direction. In List<String>, angle brackets describe the element type.
    /// - `:` — In for (int value : values), reads each element in turn. It also separates the two
    ///   alternatives of ? : or follows a switch label.
    /// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
    ///   count.
    /// - `new` — Creates a fresh value or object, such as a new array or window.
    /// - `return` — Ends this method call, optionally handing a result back to its caller.
    /// - `if` — Runs its body only when the condition in parentheses is true.
    /// - `else` — The alternative when the preceding if condition is false.
    /// - `for` — Repeats a block over a range or a sequence of values.
    /// - `while` — Keeps repeating its body while its condition stays true.
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

}
