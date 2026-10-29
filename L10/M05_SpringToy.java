// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import module java.base;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.Timer;
// </editor-fold>

class M05_SpringToy {
    /// # L10.05 · SpringToy
    ///
    /// Drag a weight, release it and watch a spring trace its motion. Tune the spring and damping to find a
    /// slow wobble or a lively bounce.
    ///
    /// **Your mission:** Painting the window advances the physics, so even repainting can move a paused
    /// weight. Restore a drawing method that only observes the current model.
    ///
    /// **Today you learn:** Rendering should be a faithful snapshot, not another clock. The timer already
    /// owns movement; a paint request may arrive for reasons unrelated to a new simulation step.
    ///
    /// From the course folder containing README.md:
    /// - Play: `java L10/M05_SpringToy.java`
    /// - Check this mission: `java L10/Check.java M05_SpringToy.java`
    ///
    /// After the play command, drag and release the weight, press Space to pause and use arrow keys to
    /// tune spring stiffness and damping. After your repair: save the file, run the check command
    /// above, then play again.
    ///
    /// Need an explanation? Recall · F29 explains the Java skill; Read this code explains these lines.
    /// Hints 1 and 2 open separately; Repair outline reveals the direction.
    void draw(Graphics2D g) {
        g.setColor(new Color(225, 230, 240));
        for (int i = 1; i < trail.size(); i++) {
            g.draw(new Line2D.Double(trail.get(i - 1), trail.get(i)));
        }
        g.setColor(Color.GRAY);
        g.setStroke(new BasicStroke(2));
        var spring = new Path2D.Double();
        spring.moveTo(WIDTH / 2, 65);
        double dx = x - WIDTH / 2, dy = y - 65, len = Math.max(1, Math.hypot(dx, dy));
        for (int i = 1; i < 20; i++) {
            double a = i / 20.0, zig = (i % 2 == 0 ? -7 : 7);
            spring.lineTo(WIDTH / 2 + dx * a - dy / len * zig, 65 + dy * a + dx / len * zig);
        }
        spring.lineTo(x, y);
        g.draw(spring);
        g.setColor(Color.BLACK);
        g.fillRect(WIDTH / 2 - 20, 58, 40, 7);
        g.setColor(new Color(65, 110, 210));
        g.fillOval((int)x - 18, (int)y - 18, 36, 36);
        g.setColor(Color.BLACK);
        g.drawString(String.format("Stiffness %.3f   Damping %.3f   %s", stiffness, damping, paused ? "Paused" : "Moving"), 15, 25);
    }

    // <editor-fold defaultstate="collapsed" desc="Recall · F29">
    /// ### Rendering: translate state into pixels
    ///
    /// Rendering reads the model and draws what it describes. Screen x increases rightward; screen y
    /// increases downward. Drawing methods often accept the upper-left corner and a size, rather than a
    /// centre.
    ///
    /// ```java
    /// int tileX = 3;
    /// int tileY = 2;
    /// int size = 10;
    /// g.fillRect(tileX * size, tileY * size, size, size);
    /// ```
    ///
    /// Here `g` is the supplied `Graphics2D` drawing tool. This paints a 10-by-10 square with upper-left
    /// corner `(30, 20)`. The model coordinates stay `(3, 2)`. Repainting should not move objects or
    /// advance a round: a window may request painting many times.
    ///
    /// Predict: if only tileY becomes 4, which screen coordinate changes, and by how much?
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Read this code">
    /// ## Read these particular lines
    ///
    /// - `step();` — Calls the physics update; ask why a drawing request should advance time.
    /// - `trail.size()` — Reads how many recorded positions are available for drawing the trace.
    /// - `g.draw(spring)` — Draws the prepared spring shape without needing to alter the weight position.
    ///
    /// ### What the prepared part already does
    ///
    /// The supplied timer invokes step and requests repainting separately. step calculates spring
    /// acceleration and records positions; draw builds the zigzag spring, weight and caption from the
    /// existing values.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
    /// Compare the responsibilities of step and draw.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
    /// Look at the first action in draw; repaint can happen more often than a timer tick.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
    /// Remove the extra model update, while retaining all painting.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
    final int WIDTH = 640, HEIGHT = 440;

    double x, y, vx, vy, stiffness, damping;
    boolean paused, grabbed;
    ArrayList<Point2D.Double> trail = new ArrayList<>();
    /// Supplied restart: restore the initial model before the UI uses it.
    void restart() {
        x = WIDTH / 2;
        y = 260;
        vx = vy = 0;
        stiffness = .012;
        damping = .985;
        paused = true;
        grabbed = false;
        trail.clear();
    }

    void tune(double k, double d) {
        stiffness = Math.max(.002, Math.min(.04, stiffness + k));
        damping = Math.max(.9, Math.min(1, damping + d));
    }

    void pull(int xx, int yy) {
        x = Math.max(12, Math.min(WIDTH - 12, xx));
        y = Math.max(95, Math.min(HEIGHT - 12, yy));
        vx = vy = 0;
    }

    /// Supplied bounded update: one call means one model step; the active repair is described above.
    void step() {
        if (paused || grabbed) {
            return;
        }
        double dx = x - WIDTH / 2, dy = y - 65, length = Math.max(1, Math.hypot(dx, dy));
        double force = -stiffness * (length - 115);
        vx = (vx + force * dx / length) * damping;
        vy = (vy + force * dy / length + .06) * damping;
        double speed = Math.hypot(vx, vy);
        if (speed > 8) {
            vx *= 8 / speed;
            vy *= 8 / speed;
        }
        x += vx;
        y += vy;
        if (x < 12 || x > WIDTH - 12) {
            x = Math.max(12, Math.min(WIDTH - 12, x));
            vx *= -.5;
        }
        if (y < 80 || y > HEIGHT - 12) {
            y = Math.max(80, Math.min(HEIGHT - 12, y));
            vy *= -.5;
        }
        trail.add(new Point2D.Double(x, y));
        if (trail.size()>800) {
            trail.remove(0);
        }
    }

    void key(int code, boolean pressed) {
        if (pressed && code == KeyEvent.VK_R) {
    /// Supplied restart: restore the initial model before the UI uses it.
            restart();
        }
        if (pressed) {
            switch (code) {
                case KeyEvent.VK_SPACE -> paused = !paused;
                case KeyEvent.VK_UP -> tune(.002, 0);
                case KeyEvent.VK_DOWN -> tune(-.002, 0);
                case KeyEvent.VK_LEFT -> tune(0, -.005);
                case KeyEvent.VK_RIGHT -> tune(0, .005);
            }
        }
    }

    void press(int x, int y, int button) {
        if (Math.hypot(x - this.x, y - this.y)<30) {
            grabbed = true;
            pull(x, y);
        }
    }

    void drag(int x, int y) {
        if (grabbed) {
            pull(x, y);
        }
    }

    void release(int x, int y) {
        if (grabbed) {
            pull(x, y);
            grabbed = false;
            paused = false;
        }
    }

    /// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
    void showWindow() {
        var frame = new JFrame("Spring Toy");
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
        var help = new JLabel("<html><div style=\"text-align:center\">Drag mass: pull / release   Space: pause   Up/Down: stiffness   Left/Right: damping   R:<br>reset</div></html>", SwingConstants.CENTER);
        help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        frame.add(help, BorderLayout.SOUTH);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        panel.requestFocusInWindow();
        timer.start();
    }

    /// Use the play command above to start the program. The check command checks the rule without
    /// opening a window. The supplied part supports only the main game or experiment described above.
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

}
