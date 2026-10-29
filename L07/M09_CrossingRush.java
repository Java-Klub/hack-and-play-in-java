// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import module java.base;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.Timer;
// </editor-fold>

class M09_CrossingRush {
    /// # L07.09 · CrossingRush
    ///
    /// Hop between traffic lanes and reach the green bank. Escaping sideways out of the window should not be
    /// a secret safe route.
    ///
    /// **Your mission:** Repeated sideways hops can carry the player beyond the visible playfield. Repair
    /// hop() so the whole player remains within the side boundaries.
    ///
    /// **Today you learn:** Invariants: every accepted position must keep the player’s full shape inside the
    /// field. The centre coordinate alone is not the whole collision shape.
    ///
    /// From the course folder containing README.md:
    /// - Play: `java L07/M09_CrossingRush.java`
    /// - Check this mission: `java L07/Check.java M09_CrossingRush.java`
    ///
    /// Run the play command above and hop with the arrow keys between passing cars until you reach the
    /// green bank at the top. After your repair: save the file, run the check command above, then play
    /// again.
    ///
    /// Need an explanation? Recall · F25 explains the Java skill; Read this code explains these lines.
    /// Hints 1 and 2 open separately; Repair outline reveals the direction.
    void hop(int dx, int dy) {
        if (lost || won) {
            return;
        }
        started = true;
        x = Math.max(12, Math.min(WIDTH - 12, x + dx));
        y = Math.min(370, y + dy);
        collide();
        if (!lost && y < 45) {
            won = true;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Recall · F25">
    /// ### Invariant: a promise after every action
    ///
    /// An invariant is a relationship that must remain true across accepted actions. Some actions move a
    /// quantity between places rather than creating more. Validate the complete request before making
    /// changes, then apply it once. Rejection should preserve the earlier state.
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
    /// The containers become three and five; the printed total remains eight because the transfer
    /// balances. Now consider nine units: what must remain unchanged? Explain why moving only one side
    /// would violate the total even if both individual values looked legal.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Read this code">
    /// ## Read these particular lines
    ///
    /// - `if (lost || won)` — Finished rounds reject further movement.
    /// - `x = x + dx` — Commits the horizontal position; the proposed result needs to obey the boundary rule.
    /// - `Math.min(370, y + dy)` — An existing bound limits movement at the bottom edge.
    /// - `collide()` — Checks traffic after the new player position has been selected.
    ///
    /// ### What the prepared part already does
    ///
    /// collide() compares the player with the same car rectangles used for drawing. The supplied timer moves
    /// traffic; hop() owns player input and freezes movement after a win or loss.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
    /// The player radius margin is 12 pixels.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
    /// The postcondition is 12 <= x <= WIDTH-12 after every active hop.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
    /// Clamp x+dx between those limits; do not remove collision or terminal-round checks.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
    final int WIDTH = 600, HEIGHT = 420;

    record Car(double x, int lane, double speed) {
    }

    ArrayList<Car> cars = new ArrayList<>();
    int x, y;
    boolean started, lost, won;
    /// restart(): Restores the starting Crossing Rush model so this toy can be tried again.
    void restart() {
        x = 300;
        y = 370;
        started = lost = won = false;
        cars.clear();
        for (int lane = 0; lane < 5; lane++) {
            for (int n = 0; n < 3; n++) {
                cars.add(new Car(n * 230 + lane * 47, lane, (lane % 2 == 0 ? 1 : -1) * (1.3 + lane * .28)));
            }
        }
    }

    /// collide(): Compares the player box with the same car rectangles used for drawing.
    void collide() {
        for (var car : cars) {
            if (new Rectangle2D.Double(car.x, 65 + car.lane * 50, 65, 32).intersects(x - 11, y - 11, 22, 22)) {
                lost = true;
            }
        }
    }

    void step() {
        if (!started || lost || won) {
            return;
        }
        for (int i = 0; i < cars.size(); i++) {
            var c = cars.get(i);
            double xx = c.x + c.speed;
            if (xx > WIDTH + 10) {
                xx = -75;
            }
            if (xx<-75) {
                xx = WIDTH + 10;
            }
            cars.set(i, new Car(xx, c.lane, c.speed));
        }
        collide();
    }

    /// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
    void draw(Graphics2D g) {
        g.setColor(new Color(100, 180, 120));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        for (int lane = 0; lane < 5; lane++) {
            g.setColor(new Color(65, 70, 80));
            g.fillRect(0, 60 + lane * 50, WIDTH, 44);
        }
        for (var c : cars) {
            g.setColor(c.lane % 2 == 0 ? Color.ORANGE : Color.CYAN);
            g.fillRect((int)c.x, 65 + c.lane * 50, 65, 32);
        }
        g.setColor(lost ? Color.RED : Color.YELLOW);
        g.fillOval(x - 11, y - 11, 22, 22);
        g.setColor(Color.BLACK);
        g.drawString(won ? "Across safely! R: another run" : lost ? "Traffic got you! R: retry" : !started ? "Arrow keys: hop to the green goal" : "Five lanes; choose your moment", 12, 22);
    }

    void key(int code, boolean pressed) {
        if (pressed && code == KeyEvent.VK_R) {
            restart();
        }
        if (pressed) {
            switch (code) {
                case KeyEvent.VK_LEFT -> hop(-30, 0);
                case KeyEvent.VK_RIGHT -> hop(30, 0);
                case KeyEvent.VK_UP -> hop(0, -50);
                case KeyEvent.VK_DOWN -> hop(0, 50);
            }
        }
    }

    /// showWindow(): Supplied later topic: Swing listeners forward input to the model; timers stop on
    /// window close.
    void showWindow() {
        var frame = new JFrame("Crossing Rush");
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
        var help = new JLabel("<html><div style=\"text-align:center\">Arrows: hop   Reach the top green bank   R: restart</div></html>", SwingConstants.CENTER);
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
    /// - `%` — The remainder after division; useful for repeated positions or turns.
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
    /// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
    /// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
    /// - `JPanel` — A window area that can display the game’s drawing or controls.
    /// - `@Override` — Marks a supplied replacement for a library action, such as painting.
    /// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
    ///   later.
    // </editor-fold>

}
