// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import module java.base;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

class M02_FlockPlayground {
    /// # L12.02 · FlockPlayground
    ///
    /// Guide a flock by moving an obstacle through its path. Watch how local steering choices turn many
    /// separate birds into a flowing group.
    ///
    /// **Your mission:** Birds receive no push from the obstacle because the adapter always returns zero.
    /// Make the installed avoidance rule receive the real distance.
    ///
    /// **Today you learn:** Composition lets the flock use an Avoidance role without owning its formula.
    /// Passing the actual distance matters: a replacement rule may react differently nearby and farther away.
    ///
    /// From the course folder containing README.md:
    /// - Play: `java L12/M02_FlockPlayground.java`
    /// - Check this mission: `java L12/Check.java M02_FlockPlayground.java`
    ///
    /// After the play command, move the orange mouse obstacle into the birds’ path and use Pause/Run or
    /// New flock to compare their response. After your repair: save the file, run the check command
    /// above, then play again.
    ///
    /// Need an explanation? Recall · F34 explains the Java skill; Read this code explains these lines.
    /// Hints 1 and 2 open separately; Repair outline reveals the direction.
    double obstaclePush(double distance) {
        return avoidance.strength(distance);
    }

    // <editor-fold defaultstate="collapsed" desc="Recall · F34">
    /// ### Interface: ask a role to do its job
    ///
    /// An interface names an operation its implementations provide. The caller can use that role without
    /// choosing the implementation's formula.
    ///
    /// ```java
    /// interface Points {
    ///     int award(int n);
    /// }
    /// record DoublePoints() implements Points {
    ///     public int award(int n) {
    ///         return n * 2;
    ///     }
    /// }
    /// ```
    ///
    /// With `Points rule = new DoublePoints();`, `rule.award(3)` returns 6. `implements Points` promises
    /// the operation; the implementation must supply its public method. The variable rule refers to the
    /// selected object, so call its operation with the current input rather than repeating a fixed
    /// formula in the caller. Another implementation can supply another rule through the same interface.
    ///
    /// Predict: what does this implementation return for `rule.award(5)`, and which code would change to
    /// award three times the input?
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Read this code">
    /// ## Read these particular lines
    ///
    /// - `double distance` — The actual distance supplied by the current bird’s obstacle calculation.
    /// - `obstaclePush` — This adapter supplies only strength; the caller already knows the push direction.
    /// - `return 0;` — Reports no obstacle contribution regardless of distance or installed behaviour.
    ///
    /// ### What the prepared part already does
    ///
    /// step combines separation, alignment and cohesion with obstaclePush before applying a speed limit. The
    /// supplied Avoidance interface and field hold the replaceable rule; the window moves the obstacle and
    /// owns pause and reset.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
    /// The interface receives distance and supplies only a strength.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
    /// The field already holds the default implementation.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
    /// Delegate to the field; do not return a constant or copy the default formula.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
    final int COUNT = 36;
    double[] x = new double[COUNT];
    double[] y = new double[COUNT];
    double[] vx = new double[COUNT];
    double[] vy = new double[COUNT];
    double obstacleX = 300;
    double obstacleY = 200;
    boolean running = true;

    /// Restore the starting game state.
    void reset(long seed) {
        var random = new Random(seed);
        for (int i = 0; i < COUNT; i++) {
            x[i] = random.nextDouble() * 600;
            y[i] = random.nextDouble() * 400;
            double angle = random.nextDouble() * Math.PI * 2;
            vx[i] = Math.cos(angle) * 2;
            vy[i] = Math.sin(angle) * 2;
        }
    }

    /// Supplied bounded update: one call means one model step; the active repair is described above.
    void step() {
        double[] nx = new double[COUNT];
        double[] ny = new double[COUNT];
        for (int i = 0; i < COUNT; i++) {
            double ax = 0;
            double ay = 0;
            int neighbours = 0;
            for (int j = 0; j < COUNT; j++) {
                if (i == j) {
                    continue;
                }
                double dx = x[j] - x[i];
                double dy = y[j] - y[i];
                double distance = Math.hypot(dx, dy);
                if (distance > 0 && distance < 80) {
                    ax += (vx[j] - vx[i]) * 0.04 + dx * 0.001;
                    ay += (vy[j] - vy[i]) * 0.04 + dy * 0.001;
                    if (distance < 24) {
                        ax -= dx * 1.8 / (distance * distance);
                        ay -= dy * 1.8 / (distance * distance);
                    }
                    neighbours++;
                }
            }
            if (neighbours > 0) {
                ax /= neighbours;
                ay /= neighbours;
            }
            double dx = x[i] - obstacleX;
            double dy = y[i] - obstacleY;
            double distance = Math.hypot(dx, dy);
            if (distance > 0 && distance < 75) {
                ax += dx / distance * obstaclePush(distance);
                ay += dy / distance * obstaclePush(distance);
            }
            nx[i] = vx[i] + ax;
            ny[i] = vy[i] + ay;
            double speed = Math.hypot(nx[i], ny[i]);
            if (speed > 3) {
                nx[i] *= 3 / speed;
                ny[i] *= 3 / speed;
            }
        }
        vx = nx;
        vy = ny;
        for (int i = 0; i < COUNT; i++) {
            x[i] = (x[i] + vx[i] + 600) % 600;
            y[i] = (y[i] + vy[i] + 400) % 400;
        }
    }

    /// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
    void showWindow() {
        var frame = new JFrame("Flock Playground");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        var canvas = new JPanel(){
        
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D g = (Graphics2D)graphics;
                g.setColor(new Color(15, 25, 40));
                g.fillRect(0, 0, 600, 400);
                g.setColor(Color.ORANGE);
                g.drawOval((int)obstacleX - 35, (int)obstacleY - 35, 70, 70);
                g.setColor(Color.CYAN);
                for (int i = 0; i < COUNT; i++) {
                    double angle = Math.atan2(vy[i], vx[i]);
                    var bird = new Polygon();
                    for (double turn : new double[]{0, 2.5, -2.5}) {
                        bird.addPoint((int)(x[i] + 8 * Math.cos(angle + turn)), (int)(y[i] + 8 * Math.sin(angle + turn)));
                    }
                    g.fillPolygon(bird);
                }
            }
        };
        canvas.setPreferredSize(new Dimension(600, 400));
        var controls = new JPanel();
        canvas.addMouseMotionListener(new MouseMotionAdapter(){
        
            public void mouseMoved(MouseEvent event) {
                obstacleX = event.getX();
                obstacleY = event.getY();
            }
        });
        var pause = new JButton("Pause / Run");
        pause.addActionListener(event -> running = !running);
        var restart = new JButton("New flock");
        restart.addActionListener(event -> {
    /// Restore the starting game state.
            reset(System.nanoTime());
            canvas.repaint();
        });
        controls.add(pause);
        controls.add(restart);
        var timer = new Timer(25, event -> {
            if (running) {
    /// Supplied bounded update: one call means one model step; the active repair is described above.
                step();
            }
            canvas.repaint();
        });
        frame.addWindowListener(new WindowAdapter(){
        
            public void windowClosed(WindowEvent event) {
                timer.stop();
            }
        });
        timer.start();
        frame.add(canvas, BorderLayout.CENTER);
        frame.add(controls, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    interface Avoidance { double strength(double distance); }
    Avoidance avoidance = distance -> distance > 0 && distance < 75 ? 0.2 : 0;

    /// Use the play command above to start the program. The check command checks the rule without
    /// opening a window. The supplied part supports only the main game or experiment described above.
    void main() {
    /// Restore the starting game state.
        reset(42);
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
    /// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
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
    /// - `interface` — Describes a role that different implementations can fulfil. The caller can use that
    ///   role without choosing its internals.
    /// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
    /// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
    /// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
    /// - `JPanel` — A window area that can display the game’s drawing or controls.
    /// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
    ///   later.
    // </editor-fold>

}
