// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
// </editor-fold>

/// # L04.03 · Flood
///
/// Grow a single coloured region from the top-left corner until it covers the board. Choose a new colour
/// that connects useful neighbouring patches and see how far each choice spreads.
///
/// **Your mission:** A colour change reaches the first neighbours and then abandons discovered cells.
/// Repair spread so it processes the entire growing work list, while preserving the supplied neighbour
/// checks.
///
/// **Today you learn:** While with growing work: queue stores cells waiting to be examined. After each
/// pass, the index advances and new neighbours may be added; compare with the current size rather than an
/// assumed fixed number of passes.
///
/// Run the program and choose a colour by clicking its palette square or pressing 1–4; watch
/// which connected cells change and use R to restore the board.
void spread(ArrayList<Point> queue, int old, int colour) {
    int index = 0;
    while (index < queue.size() && index < 1) {
        Point point = queue.get(index);
        visitNeighbours(queue, point, old, colour);
        index++;
    }
}

// <editor-fold defaultstate="collapsed" desc="Recall · F10">
/// A `while` checks its condition before every pass, including the first. If the condition is false
/// immediately, its body runs zero times. Something must make progress towards stopping, or
/// deliberately wait for new input.
///
/// These example statements belong inside a called method.
///
/// ```java
/// int energy = 5;
/// while (energy > 0) {
///     IO.println(energy);
///     energy -= 2;
/// }
/// ```
///
/// Predict the printed numbers and the final stored value. The output is `5`, `3`, `1`; afterwards
/// energy is `-1`, so the next condition fails. The condition is not checked halfway through the
/// body. `energy -= 2` subtracts two and stores the result; without it, the loop repeats forever.
/// `return` ends the method; `break` ends the loop. Trace the condition and changing values
/// separately, especially when the data being searched can grow.
///
/// Now predict the trace if energy starts at four.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int index = 0;` — Begin processing at the first discovered cell in the supplied work list.
/// - `while (index < queue.size() && index < 1)` — The condition is checked again on every pass,
///   including after queue has grown. Both comparisons currently restrict it.
/// - `Point point = queue.get(index);` — Take the coordinates of the next discovered cell; Point is the
///   supplied pair of coordinates.
/// - `visitNeighbours(queue, point, old, colour);` — The supplied action recolours eligible neighbours
///   and appends them to this same work list.
/// - `index++;` — Advance beyond the cell just processed; a larger list does not mean revisiting its
///   earlier entries.
///
/// ### What the prepared part already does
///
/// fill changes the starting cell and puts it in queue. visitNeighbours checks four directions and board
/// boundaries; it marks each new cell before adding it, so the finite board cannot keep adding the same
/// cell. draw, key and the window code are supplied.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// Here queue is a prepared work list of discovered cell coordinates. index counts how many entries
/// have already been processed; queue.size() reports how many have been discovered so far. For
/// example, a pass starting with index 0 and size 1 can discover two neighbours: after index
/// advances, it is 1 while size is 3, so work remains. The size is deliberately checked again.
/// visitNeighbours recolours each new cell before adding it, preventing repeated discovery of that
/// same cell. There are only 64 board cells. You own the loop's continuation, not the coordinate or
/// neighbour machinery.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// visitNeighbours can append more cells to queue.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Recheck the current queue.size() after every iteration.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Use while (index < queue.size()); keep index++ after the visit.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
void visitNeighbours(ArrayList<Point> queue, Point point, int old, int colour) {
    for (var delta : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
        int x = point.x + delta[0];
        int y = point.y + delta[1];
        if (x >= 0 && y >= 0 && x < 8 && y < 8 && board[y][x] == old) {
            board[y][x] = colour;
            queue.add(new Point(x, y));
        }
    }
}

final int WIDTH = 520, HEIGHT = 420;

int[][] board;
int moves;
Color[] colours = {new Color(239, 97, 97), new Color(65, 164, 222), new Color(250, 194, 63), new Color(109, 195, 121)};
void restart() {
    board = new int[8][8];
    moves = 0;
    var random = new Random(5421);
    for (var row : board) {
        for (int x = 0; x < 8; x++) {
            row[x] = random.nextInt(4);
        }
    }
}

void fill(int colour) {
    if (colour < 0 || colour >= 4 || colour == board[0][0] || won()) {
        return;
    }
    int old = board[0][0];
    var queue = new ArrayList<Point>();
    queue.add(new Point(0, 0));
    board[0][0] = colour;
    spread(queue, old, colour);
    moves++;
}

boolean won() {
    for (var row : board) {
        for (int c : row) {
            if (c != board[0][0]) {
                return false;
            }
        }
    }
    return true;
}

void draw(Graphics2D g) {
    for (int y = 0; y < 8; y++) {
        for (int x = 0; x < 8; x++) {
            g.setColor(colours[board[y][x]]);
            g.fillRect(25 + x * 40, 25 + y * 40, 39, 39);
        }
    }
    for (int c = 0; c < 4; c++) {
        g.setColor(colours[c]);
        g.fillRect(380, 50 + c * 65, 100, 45);
        g.setColor(Color.BLACK);
        g.drawString("" + (c + 1), 425, 78 + c * 65);
    }
    g.setColor(Color.BLACK);
    g.drawString((won() ? "Filled! " : "") + "Moves: " + moves, 25, 380);
    g.drawString("Seed 5421 - compare your next attempt", 25, 403);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code >= KeyEvent.VK_1 && code <= KeyEvent.VK_4) {
        fill(code - KeyEvent.VK_1);
    }
}

void press(int x, int y, int button) {
    if (x >= 380 && x < 480 && y >= 50 && y < 310 && (y - 50) % 65 < 45) {
        fill((y - 50) / 65);
    }
}

void showWindow() {
    var frame = new JFrame("Flood");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click a colour or press 1-4   R: same board</div></html>", SwingConstants.CENTER);
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

/// Prepared entry points: main, restart, draw, showWindow.
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
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `public` — Makes this declaration accessible to other code.
/// - `protected` — A visibility keyword used by supplied library callbacks, such as paintComponent.
/// - `super` — In super.paintComponent(g), asks the supplied parent implementation to paint its standard
///   background first.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
