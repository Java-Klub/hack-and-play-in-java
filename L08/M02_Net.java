// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import module java.base;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
// </editor-fold>

class M02_Net {
    /// # L08.02 · Net
    ///
    /// Rotate pipe tiles until the whole network receives power from its source. A green tile should have a
    /// genuine two-sided connection.
    ///
    /// **Your mission:** A pipe pointing at a neighbour’s blank side still powers that neighbour. Repair
    /// connected() so a connection needs compatible ports on both tiles.
    ///
    /// **Today you learn:** Grid neighbours: adjacency alone is not connectivity. For a move in one
    /// direction, inspect both that outgoing port and the neighbour’s incoming side.
    ///
    /// From the course folder containing README.md:
    /// - Play: `java L08/M02_Net.java`
    /// - Check this mission: `java L08/Check.java M02_Net.java`
    ///
    /// Run the play command above and click a tile to rotate its pipes, watching which tiles turn green
    /// as they connect to the source. After your repair: save the file, run the check command above,
    /// then play again.
    ///
    /// Need an explanation? Recall · F18 explains the Java skill; Read this code explains these lines.
    /// Hints 1 and 2 open separately; Repair outline reveals the direction.
    boolean[][] connected() {
        boolean[][] reached = new boolean[4][4];
        var queue = new ArrayList<Point>();
        queue.add(new Point(0, 0));
        reached[0][0] = true;
        for (int i = 0; i < queue.size(); i++) {
            var p = queue.get(i);
            for (int d = 0; d < 4; d++) {
                int x = p.x + dx[d], y = p.y + dy[d];
                if (x >= 0 && y >= 0 && x < 4 && y < 4 && !reached[y][x] && (tiles[p.y][p.x]&(1<<d)) != 0) {
                    reached[y][x] = true;
                    queue.add(new Point(x, y));
                }
            }
        }
        return reached;
    }

    // <editor-fold defaultstate="collapsed" desc="Recall · F18">
    /// ### Grids: choose the row before the column
    ///
    /// In an array of rows, the first index selects a row and the second selects a position within it.
    /// Rows can have different lengths. A neighbour is a displacement from a cell, not another absolute
    /// position. Define the permitted directions before checking whether a candidate exists.
    ///
    /// ```java
    /// int[][] mosaic = {{3, 6, 9}, {2, 4, 8}};
    /// int row = 0;
    /// int column = 1;
    /// IO.println(mosaic[row][column + 1]);
    /// ```
    ///
    /// The output is nine: row zero and column two select that element. Moving down changes row; moving
    /// right changes column. From the top-right cell, which of those two moves stays inside? For a
    /// diagonal both coordinates change; bounds must be checked before accessing the candidate.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Read this code">
    /// ## Read these particular lines
    ///
    /// - `p.x + dx[d]` — Uses the selected direction’s horizontal offset to find a neighbour.
    /// - `1<<d` — A supplied bit shift selects the bit assigned to direction d.
    /// - `tiles[p.y][p.x]&(1<<d)` — Bitwise AND asks whether the current tile has that outgoing port.
    /// - `!reached[y][x]` — Prevents already discovered tiles from being queued again.
    ///
    /// ### What the prepared part already does
    ///
    /// The supplied queue traversal starts at the source and records reached tiles. Rotation changes port
    /// bits, drawing colours reached tiles, and won() adds complete-network and no-open-port checks.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="For this game">
    /// ### Reading one port bit
    ///
    /// Each direction has one bit. Bit positions start at zero: `1 << d` gives the masks 1, 2, 4, 8 for
    /// directions 0, 1, 2, 3. Bitwise `&` retains only positions set on both sides. This is different
    /// from boolean `&&`.
    ///
    /// ```java
    /// int flags = 5;
    /// int selected = 1 << 2;
    /// IO.println((flags & selected) != 0);
    /// ```
    ///
    /// The output is true: 5 = 1 + 4, so `5 & 4` gives 4, whereas `5 & 2` gives 0. A nonzero result means
    /// the selected bit is set. Now predict testing bit three when flags is 10. Directions here run Up,
    /// Right, Down, Left, so opposite ports are two steps apart around the four-direction cycle. Before
    /// admitting a neighbour, ask about both sides of that connection, not merely whether the neighbour
    /// exists.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
    /// d selects Up, Right, Down or Left.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
    /// The opposite direction is (d+2)%4.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
    /// Require both the current tile’s d bit and the neighbour’s opposite bit before adding it to the
    /// queue.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
    final int WIDTH = 460, HEIGHT = 420;

    int[][] tiles = new int[4][4];
    int[] dx = {0, 1, 0, -1}, dy = {-1, 0, 1, 0};
    void solvedBoard() {
        tiles = new int[4][4];
        for (int i = 0; i < 15; i++) {
            int y = i / 4, x = y % 2 == 0 ? i % 4 : 3 - i % 4;
            int yy = (i + 1) / 4, xx = yy % 2 == 0 ? (i + 1) % 4 : 3 - (i + 1) % 4;
            int d = yy > y ? 2 : xx > x ? 1 : 3;
            tiles[y][x]|=1<<d;
            tiles[yy][xx]|=1<<((d + 2) % 4);
        }
    }

    /// restart(): Restores the starting Net model so this toy can be tried again.
    void restart() {
        solvedBoard();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                for (int i = 0; i < (x + 2 * y + 1) % 4; i++) {
                    rotate(x, y);
                }
            }
        }
    }

    void rotate(int x, int y) {
        if (x >= 0 && y >= 0 && x < 4 && y < 4) {
            tiles[y][x] = ((tiles[y][x]<<1)&15)|(tiles[y][x]>>3);
        }
    }

    /// won(): Requires all tiles reached, no open ports and exactly a tree’s number of edges.
    boolean won() {
        var reached = connected();
        int ports = 0;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (!reached[y][x]) {
                    return false;
                }
                for (int d = 0; d < 4; d++) {
                    if ((tiles[y][x]&(1<<d)) != 0) {
                        ports++;
                        int xx = x + dx[d], yy = y + dy[d];
                        if (xx < 0 || yy < 0 || xx >= 4 || yy >= 4 || (tiles[yy][xx]&(1<<((d + 2) % 4))) == 0) {
                            return false;
                        }
                    }
                }
            }
        }
        return ports == 30;
    }

    /// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
    void draw(Graphics2D g) {
        var reached = connected();
        g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                int xx = 100 + x * 75, yy = 65 + y * 75;
                g.setColor(new Color(235, 238, 243));
                g.fillRect(xx - 35, yy - 35, 70, 70);
                g.setColor(reached[y][x] ? new Color(35, 165, 115) : Color.GRAY);
                for (int d = 0; d < 4; d++) {
                    if ((tiles[y][x]&(1<<d)) != 0) {
                        g.drawLine(xx, yy, xx + dx[d] * 34, yy + dy[d] * 34);
                    }
                }
                g.fillOval(xx - 7, yy - 7, 14, 14);
            }
        }
        g.setColor(Color.BLACK);
        g.drawString("SOURCE", 70, 18);
        g.drawString(won() ? "Connected tree complete!" : "Connect every tile; leave no open ports or loops", 35, 390);
    }

    void key(int code, boolean pressed) {
        if (pressed && code == KeyEvent.VK_R) {
            restart();
        }
    }

    void press(int x, int y, int button) {
        if (x >= 65 && x < 365 && y >= 30 && y < 330) {
            rotate((x - 65) / 75, (y - 30) / 75);
        }
    }

    /// showWindow(): Supplied later topic: Swing listeners forward input to the model; disposing the
    /// window closes this view.
    void showWindow() {
        var frame = new JFrame("Net");
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
        var help = new JLabel("<html><div style=\"text-align:center\">Click tiles: rotate clockwise   Green: joined to source   R:<br>restart</div></html>", SwingConstants.CENTER);
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
    /// - `||` — At least one condition must be true; if the first is true, Java skips the second.
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
