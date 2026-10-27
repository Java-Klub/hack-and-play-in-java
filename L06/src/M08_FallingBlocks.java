// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L06.08 · FallingBlocks
///
/// Steer and rotate falling pieces into a stack. A trial rotation near a wall should either fit or be
/// refused cleanly.
///
/// **Your mission:** Checking a proposed piece position changes the real piece position, causing jumps
/// and double movement. Repair fits() to remain only a query.
///
/// **Today you learn:** Pure contracts: validation reads the board and a candidate position, then returns
/// a boolean. Only its caller decides whether to commit the candidate.
///
/// Run File, steer the falling piece with Left and Right, and press Up to try a rotation near the stack
/// or wall.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F15 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
boolean fits(int[][] cells, int x, int y) {
    for (var c : cells) {
        int xx = x + c[0], yy = y + c[1];
        if (xx < 0 || xx >= 10 || yy < 0 || yy >= 16 || board[yy][xx] != 0) {
            return false;
        }
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F15">
/// ### Contracts: ask without changing the world
///
/// A contract describes accepted inputs and the meaning of a result. A pure calculation uses its
/// inputs without changing other state. A query may read the current model, but it should still leave
/// that model untouched. Reading an answer and committing an action are separate responsibilities.
///
/// ```java
/// int contrast(int light, int dark) {
///     return Math.abs(light - dark);
/// }
/// ```
///
/// `Math.abs` gives the nonnegative magnitude, removing a negative sign. This method can be called
/// repeatedly without consuming anything. Arguments `(9, 4)` give five: the difference is five.
/// Predict `(4, 9)`. Which stored values would you compare before and after a query to show that it
/// caused no side effect? A rejected candidate deserves the same protection.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `px = x` — Writes the candidate coordinate into the real piece’s field.
/// - `for (var c : cells)` — Checks every relative cell belonging to the proposed piece.
/// - `x + c[0]` — Turns one relative cell offset into a board column.
/// - `board[yy][xx] != 0` — A nonzero saved board cell is already occupied.
///
/// ### What the prepared part already does
///
/// Movement and rotate() propose coordinates and call fits() before accepting them. The timer advances
/// falling pieces; clearRows() compacts completed rows. These mechanisms already own the actual changes.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Read the callers: each checks fits before changing position or shape.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// A query can run many times while searching a hard drop.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Remove the px/py assignments inside fits; preserve its bounds and occupied-cell checks.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 540, HEIGHT = 430;

int[][] board = new int[16][10];
int[][] piece;
int px, py, kind, lines, ticks, nextKind;
boolean started, over;
int[][] shape(int type) {
    return switch (type) {
        case 0 -> new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}};
        case 1 -> new int[][]{{-1, 0}, {0, 0}, {1, 0}};
        default -> new int[][]{{0, -1}, {0, 0}, {1, 0}};
    };
}

/// restart(): Restores the starting Falling Blocks model so this toy can be tried again.
void restart() {
    board = new int[16][10];
    lines = ticks = nextKind = 0;
    started = over = false;
    spawn();
}

void spawn() {
    kind = nextKind++ % 3;
    piece = shape(kind);
    px = 4;
    py = 1;
    if (!fits(piece, px, py)) {
        over = true;
    }
}

void move(int dx) {
    if (!over && fits(piece, px + dx, py)) {
        px += dx;
    }
}

/// rotate(): Supplied coordinate rotation proposes (-y,x), committing only when fits succeeds.
void rotate() {
    if (over || kind == 0) {
        return;
    }
    int[][] rotated = new int[piece.length][2];
    for (int i = 0; i < piece.length; i++) {
        rotated[i][0] = -piece[i][1];
        rotated[i][1] = piece[i][0];
    }
    if (fits(rotated, px, py)) {
        piece = rotated;
    }
}

/// clearRows(): Compacts non-full rows downward and creates empty rows at the top.
int clearRows() {
    int destination = 15, removed = 0;
    for (int y = 15; y >= 0; y--) {
        boolean full = true;
        for (int cell : board[y]) {
            if (cell == 0) {
                full = false;
            }
        }
        if (full) {
            removed++;
        }
        else {
            board[destination--] = board[y].clone();
        }
    }
    while (destination >= 0) {
        board[destination--] = new int[10];
    }
    lines += removed;
    return removed;
}

void down() {
    if (over) {
        return;
    }
    if (fits(piece, px, py + 1)) {
        py++;
    }
    else {
        for (var c : piece) {
            board[py + c[1]][px + c[0]] = kind + 1;
        }
        clearRows();
        spawn();
    }
}

void drop() {
    if (over) {
        return;
    }
    while (fits(piece, px, py + 1)) {
        py++;
    }
    down();
}

void step() {
    if (started && !over && ++ticks % 25 == 0) {
        down();
    }
}

/// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
void draw(Graphics2D g) {
    Color[] colours = {Color.WHITE, new Color(255, 180, 60), new Color(60, 180, 240), new Color(170, 110, 230)};
    g.setColor(new Color(22, 28, 40));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    for (int y = 0; y < 16; y++) {
        for (int x = 0; x < 10; x++) {
            g.setColor(board[y][x] == 0 ? new Color(45, 50, 65) : colours[board[y][x]]);
            g.fillRect(30 + x * 24, 25 + y * 24, 23, 23);
        }
    }
    g.setColor(colours[kind + 1]);
    for (var c : piece) {
        g.fillRect(30 + (px + c[0]) * 24, 25 + (py + c[1]) * 24, 23, 23);
    }
    g.setColor(Color.WHITE);
    g.drawString("Lines: " + lines, 295, 65);
    g.drawString(over ? "TOP OUT - R: retry" : !started ? "Press an arrow" : "Three shapes; no wall kicks", 295, 95);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && !over && code != KeyEvent.VK_R) {
        if (code == KeyEvent.VK_LEFT) {
            started = true;
            move(-1);
        }
        if (code == KeyEvent.VK_RIGHT) {
            started = true;
            move(1);
        }
        if (code == KeyEvent.VK_UP) {
            started = true;
            rotate();
        }
        if (code == KeyEvent.VK_DOWN) {
            started = true;
            down();
        }
        if (code == KeyEvent.VK_SPACE) {
            started = true;
            drop();
        }
    }
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; timers stop on
/// window close.
void showWindow() {
    var frame = new JFrame("Falling Blocks");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Left / Right: move   Up: rotate   Down: lower   Space: drop   R: restart</div></html>", SwingConstants.CENTER);
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
/// - `+=` — Adds to the old value and stores the result back under the same name.
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
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `else` — The alternative when the preceding if condition is false.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `while` — Keeps repeating its body while its condition stays true.
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

