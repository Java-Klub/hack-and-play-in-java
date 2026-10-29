// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import module java.base;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
// </editor-fold>

class M05_KnightTour {
    /// # L12.05 · KnightTour
    ///
    /// Tour a small board with a chess knight, landing on each square at most once. A legal move should
    /// arrive at the square you clicked, not its reflection across the diagonal.
    ///
    /// **Your mission:** Move commands swap their carried coordinates. Repair the command routing while
    /// preserving rejected positions, the knight rule and Restart.
    ///
    /// **Today you learn:** A sealed family describes the allowed command alternatives. Record patterns
    /// unpack a Move’s components; an exhaustive switch chooses its rule and a guard separates usable
    /// coordinates from rejected ones.
    ///
    /// From the course folder containing README.md:
    /// - Play: `java L12/M05_KnightTour.java`
    /// - Check this mission: `java L12/Check.java M05_KnightTour.java`
    ///
    /// After the play command, click a green knight move such as zero-based column 1, row 2 from the
    /// start, and press R to restart the tour. After your repair: save the file, run the check command
    /// above, then play again.
    ///
    /// Need an explanation? Learn · F35 explains the Java skill; Read this code explains these lines.
    /// Hints 1 and 2 open separately; Repair outline reveals the direction.
    boolean apply(Command command) {
        return switch (command) {
            case Move(var xx, var yy) when xx >= 0 && yy >= 0 && xx < 5 && yy < 5 -> move(xx, yy);
            case Move(var xx, var yy) -> false;
            case Restart() -> { restart(); yield true; }
        };
    }

    // <editor-fold defaultstate="expanded" desc="Learn · F35">
    /// ### Record patterns: choose a shape and unpack its data
    ///
    /// ```java
    /// sealed interface Badge permits Name, Stars {}
    /// record Name(String text) implements Badge {}
    /// record Stars(int count) implements Badge {}
    /// String label(Badge badge) {
    ///     return switch (badge) {
    ///         case Name(var text) -> text;
    ///         case Stars(var n) when n > 0 -> n + " stars";
    ///         case Stars(var n) -> "none";
    ///     };
    /// }
    /// ```
    ///
    /// `label(new Stars(2))` returns `"2 stars"`. Sealed closes the alternatives; permits lists them. A
    /// record pattern matches its record type and gives component values local names, in declaration
    /// order. `var` infers their types. The `when` guard must also pass; otherwise matching continues.
    /// The last Stars case handles nonpositive counts. Every permitted alternative needs handling here.
    ///
    /// Predict: which case handles `new Stars(0)`, and what text does `new Name("Nova")` produce?
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Read this code">
    /// ## Read these particular lines
    ///
    /// - `case Move(var xx, var yy)` — Unpacks the record in component order and names its two carried
    ///   coordinates.
    /// - `when xx >= 0` — Starts a guard that refines this Move case before its action runs.
    /// - `move(yy, xx)` — Forwards the matched values in reversed order; both orders may accidentally be
    ///   legal knight moves.
    /// - `yield true` — Returns a value from the block belonging to this switch expression.
    ///
    /// ### What the prepared part already does
    ///
    /// The mouse handler constructs Move and the R key constructs Restart. apply routes them; the supplied
    /// move routine checks knight distance and visited squares, while drawing highlights legal choices.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="For this game">
    /// ### Component order carries meaning
    ///
    /// The declaration `Move(int x, int y)` gives its components an order. Matching a Move created with
    /// values 2 and 1 binds xx to 2 and yy to 1; the local names do not exchange those values. Before
    /// running, predict the requested square and compare it with the square passed to move. On this
    /// board, exchanging the coordinates can still be a legal knight jump, so merely accepting a move is
    /// not enough evidence. Also trace an off-board Move: it should reach the rejecting alternative
    /// without changing the board. Restart is a different alternative, not a pair of coordinates.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
    /// Read the Move record declaration: the first component is x and the second is y. The pattern names
    /// receive values in that order.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
    /// Move(1,2) must arrive at x=1,y=2 even though the swapped knight move is also legal.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
    /// Keep every closed alternative handled; a bad coordinate returns false before moving.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
    final int WIDTH = 520, HEIGHT = 420;

    int[][] visited;
    int x, y, count;
    /// Supplied restart: restore the initial model before the UI uses it.
    void restart() {
        visited = new int[5][5];
        x = y = 0;
        count = 1;
        visited[0][0] = 1;
    }

    boolean legal(int xx, int yy) {
        return xx >= 0 && yy >= 0 && xx < 5 && yy < 5 && visited[yy][xx] == 0 && Math.abs(xx - x) * Math.abs(yy - y) == 2;
    }

    boolean move(int xx, int yy) {
        if (!legal(xx, yy)) {
            return false;
        }
        x = xx;
        y = yy;
        visited[y][x] = ++count;
        return true;
    }

    boolean won() {
        return count == 25;
    }

    /// Supplied drawing: reads model values; repainting is not another turn.
    void draw(Graphics2D g) {
        for (int yy = 0; yy < 5; yy++) {
            for (int xx = 0; xx < 5; xx++) {
                int px = 100 + xx * 60, py = 35 + yy * 60;
                g.setColor((xx + yy) % 2 == 0 ? new Color(235, 225, 205) : new Color(135, 155, 145));
                g.fillRect(px, py, 60, 60);
                if (legal(xx, yy)) {
                    g.setColor(new Color(75, 190, 120));
                    g.fillOval(px + 22, py + 22, 16, 16);
                }
                if (visited[yy][xx]>0) {
                    g.setColor(Color.DARK_GRAY);
                    g.drawString("" + visited[yy][xx], px + 23, py + 35);
                }
            }
        }
        g.setColor(new Color(65, 75, 200));
        g.drawOval(105 + x * 60, 40 + y * 60, 50, 50);
        g.setColor(Color.BLACK);
        boolean canContinue = false;
        for (int row = 0; row < 5; row++) {
            for (int column = 0; column < 5; column++) canContinue |= legal(column, row);
        }
        g.drawString(won() ? "All 25 squares visited! R: replay" : !canContinue ? "No legal moves remain. R: try a new tour" : "Visited " + count + " / 25. Green dots are legal next moves.", 20, 380);
    }

    void key(int code, boolean pressed) {
        if (pressed && code == KeyEvent.VK_R) {
            apply(new Restart());
        }
    }

    void press(int x, int y, int button) {
        if (x >= 100 && y >= 35 && x < 400 && y < 335) {
            apply(new Move((x - 100) / 60, (y - 35) / 60));
        }
    }

    /// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
    void showWindow() {
        var frame = new JFrame("Knight Tour");
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
        var help = new JLabel("<html><div style=\"text-align:center\">Click a green legal move   Visit every square once   R: restart</div></html>", SwingConstants.CENTER);
        help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        frame.add(help, BorderLayout.SOUTH);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        panel.requestFocusInWindow();
    }

    sealed interface Command permits Move, Restart {}
    record Move(int x, int y) implements Command {}
    record Restart() implements Command {}

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
    /// - `implements` — Says that a record or class fulfils the named interface role.
    /// - `record` — A small named bundle of values. Its component accessors use the component names with ().
    /// - `interface` — Describes a role that different implementations can fulfil. The caller can use that
    ///   role without choosing its internals.
    /// - `sealed` — Restricts a data type to its explicitly listed alternatives.
    /// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
    /// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
    /// - `JPanel` — A window area that can display the game’s drawing or controls.
    /// - `@Override` — Marks a supplied replacement for a library action, such as painting.
    /// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
    ///   later.
    // </editor-fold>

}
