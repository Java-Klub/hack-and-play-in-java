// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
// </editor-fold>

/// # 08.15 · Net
///
/// Rotate pipe tiles until the whole network receives power from its source. A green tile should have a
/// genuine two-sided connection.
///
/// **Your mission:** Build linked(x,y,direction): a link exists only when the neighbouring tile is in
/// bounds, the current tile has an outgoing port in that direction, and the neighbour has the opposite
/// port. A blank touching edge is not a wire. The supplied search uses this rule to light the network.
///
/// **Today you learn:** Translate a small drawing into coordinates, bounds and neighbour relationships.
///
/// Run the program and click a tile to rotate its pipes, watching which tiles turn green as
/// they connect to the source.
boolean linked(int x, int y, int direction) {
    int nx = x + dx[direction], ny = y + dy[direction];
    if (nx < 0 || ny < 0 || nx >= 4 || ny >= 4) return false;
    int outgoing = 1 << direction;
    int incoming = 1 << ((direction + 2) % 4);
    return (tiles[y][x] & outgoing) != 0 && (tiles[ny][nx] & incoming) != 0;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F18">
/// ### Coordinates describe neighbours
///
/// A grid stores rows first: `board[y][x]`. Increasing x goes right; increasing y goes down on our
/// screens. A neighbour is the current coordinate plus a small offset.
///
/// ```text
///           (0,-1)
/// (-1,0)    HERE    (1,0)
///           (0,1)
/// ```
///
/// ```java
/// int[][] board = {{2, 5, 8}, {1, 4, 7}};
/// int x = 1, y = 0;
/// int nx = x + 0, ny = y + 1;
/// if (ny >= 0 && ny < board.length && nx >= 0 && nx < board[ny].length) {
///     IO.println(board[ny][nx]);
/// }
/// ```
///
/// The move goes from 5 to 4; the printed value is 4. Bounds are checked before indexing, because an
/// array has no cell outside its edges. A diagonal changes both coordinates. A corridor repeats the
/// same offset; a neighbourhood tries several offsets. These are different algorithms using the same
/// coordinate model.
///
/// Predict the result of offset `(-1,0)` from the same start. Which offset leaves this board? Mark the
/// start and destination on paper before combining directions in a loop.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read this part
///
/// Directions 0,1,2,3 mean north,east,south,west. `dx` and `dy` give their offsets. Bits are numbered
/// from zero: `1 << d` makes masks 1,2,4,8. A tile 5 = 1+4 has north and south: `5 & 4` is 4, while `5
/// & 2` is 0. A nonzero `&` result means that port exists. `(d+2)%4` finds the opposite direction.
/// Prepared connected() explores the network; you only decide one link.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Draw both sides of the proposed wire, not only the starting tile.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Find the neighbour with dx/dy, reject outside first, then test the outgoing and opposite masks.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// After bounds pass, require both bit tests to be nonzero; the network search will combine your
/// individual links.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Build a 4×4 pipe picture with one deceptive touch: tiles meet, but their ports do not. Let a partner
/// find that false connection before rotating anything.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
boolean[][] connected() {
    boolean[][] reached = new boolean[4][4];
    var queue = new ArrayList<Point>();
    queue.add(new Point(0, 0));
    reached[0][0] = true;
    for (int i = 0; i < queue.size(); i++) {
        var p = queue.get(i);
        for (int d = 0; d < 4; d++) {
            int x = p.x + dx[d], y = p.y + dy[d];
            if (linked(p.x, p.y, d) && !reached[y][x]) {
                reached[y][x] = true;
                queue.add(new Point(x, y));
            }
        }
    }
    return reached;
}

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
