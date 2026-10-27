// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L10.14 · Snake
///
/// Guide a growing snake toward food without folding it into itself. Fast key presses should request
/// sensible turns, not rewrite the rules between two movements.
///
/// **Your mission:** A turn changes the committed direction immediately instead of waiting for the next
/// tick. Repair the queued intention and keep one legal turn per movement.
///
/// **Today you learn:** Input and simulation time are separate. A key event proposes a direction; the
/// next tick applies the proposal once, so reversal checks still refer to the last real movement.
///
/// After Run File, guide the snake with arrow keys toward food and press R to restart when the round
/// ends.
/// After your repair: Save → Test File → play again.
void turn(int x, int y) {
    if (turnQueued || lost || won || Math.abs(x) + Math.abs(y) != 1) return;
    if (x == -dx && y == -dy) return;
    plannedX = x;
    plannedY = y;
    turnQueued = true;
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `turnQueued || lost || won` — Any of these states is enough to reject a new turn request.
/// - `Math.abs(x) + Math.abs(y) != 1` — Allows only one unit along one axis, excluding diagonals and no
///   movement.
/// - `x == -dx && y == -dy` — Recognises a request directly opposite to the committed direction.
/// - `dx = x;` — Currently changes the committed horizontal direction during input handling.
///
/// ### What the prepared part already does
///
/// The supplied step commits next-direction fields, moves the snake and checks food and collisions. The
/// window turns arrow keys into turn calls; restart clears the queued-turn state.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. turn records intention; step commits it.
/// 2. Do not assign dx/dy inside turn.
/// 3. Keep turnQueued so a second key cannot reverse the first queued turn before the tick.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
record Cell(int x, int y) { }
final int COLUMNS = 20, ROWS = 15, TILE = 24;
final Random random = new Random();
final List<Cell> body = new ArrayList<>();
Cell food;
int dx, dy, plannedX, plannedY, score;
boolean turnQueued, lost, won;

/// Supplied restart: restore the initial model before the UI uses it.
void restart() {
    body.clear();
    body.add(new Cell(5, 7));
    body.add(new Cell(4, 7));
    body.add(new Cell(3, 7));
    dx = plannedX = 1;
    dy = plannedY = score = 0;
    turnQueued = lost = won = false;
    placeFood();
}

void placeFood() {
    var free = new ArrayList<Cell>();
    for (int y = 0; y < ROWS; y++) {
        for (int x = 0; x < COLUMNS; x++) {
            var cell = new Cell(x, y);
            if (!body.contains(cell)) free.add(cell);
        }
    }
    won = free.isEmpty();
    food = won ? null : free.get(random.nextInt(free.size()));
}

/// Supplied bounded update: one call means one model step; the active repair is described above.
void step() {
    if (lost || won) return;
    dx = plannedX;
    dy = plannedY;
    turnQueued = false;
    var head = body.getFirst();
    var next = new Cell(head.x() + dx, head.y() + dy);
    boolean growing = next.equals(food);
    int occupied = body.size() - (growing ? 0 : 1);
    if (next.x() < 0 || next.x() >= COLUMNS || next.y() < 0 || next.y() >= ROWS
            || body.subList(0, occupied).contains(next)) {
        lost = true;
        return;
    }
    body.addFirst(next);
    if (growing) {
        score++;
        placeFood();
    } else {
        body.removeLast();
    }
}

/// Supplied drawing: reads model values; repainting is not another turn.
void draw(Graphics g) {
    g.setColor(new Color(14, 27, 30));
    g.fillRect(0, 0, COLUMNS * TILE, ROWS * TILE + 50);
    g.setColor(new Color(25, 44, 46));
    for (int x = 0; x <= COLUMNS; x++) g.drawLine(x * TILE, 0, x * TILE, ROWS * TILE);
    for (int y = 0; y <= ROWS; y++) g.drawLine(0, y * TILE, COLUMNS * TILE, y * TILE);
    if (food != null) {
        g.setColor(new Color(255, 116, 114));
        g.fillOval(food.x() * TILE + 4, food.y() * TILE + 4, TILE - 8, TILE - 8);
    }
    for (int index = 0; index < body.size(); index++) {
        Cell cell = body.get(index);
        g.setColor(index == 0 ? Color.WHITE : new Color(89, 222, 154));
        g.fillRoundRect(cell.x() * TILE + 2, cell.y() * TILE + 2, TILE - 4, TILE - 4, 6, 6);
    }
    g.setColor(Color.WHITE);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
    String status = lost ? "CRASH!" : won ? "FULL BOARD - YOU WIN!" : "SNACK SNAKE";
    g.drawString(status + "   Score: " + score, 12, ROWS * TILE + 21);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Arrow keys: turn     R: restart", 12, ROWS * TILE + 41);
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Snake - Snack Snake");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
/// Supplied drawing: reads model values; repainting is not another turn.
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(COLUMNS * TILE, ROWS * TILE + 50));
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent event) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_LEFT -> turn(-1, 0);
                case KeyEvent.VK_RIGHT -> turn(1, 0);
                case KeyEvent.VK_UP -> turn(0, -1);
                case KeyEvent.VK_DOWN -> turn(0, 1);
                case KeyEvent.VK_R -> restart();
            }
        }
    });
    var timer = new Timer(125, event -> {
/// Supplied bounded update: one call means one model step; the active repair is described above.
        step();
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent event) { timer.stop(); }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

/// Start the real program with Run File. Test File checks the rule without opening a window.
/// turn is the active boundary; the model exposes it for deterministic checks without a window.
/// restart restores initial fields; draw reads state; showWindow supplies listeners, one timer and window-close cleanup.
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
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
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
/// - `else` — The alternative when the preceding if condition is false.
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
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>

