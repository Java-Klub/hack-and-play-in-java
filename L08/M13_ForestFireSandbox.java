// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L08.13 · ForestFireSandbox
///
/// Plant trees, cut a firebreak, and ignite one end of the forest. Explore how a toy fire front spreads
/// one neighbourhood at a time.
///
/// **Your mission:** Fire can cascade across many trees during one tick and old flames fail to clear.
/// Repair nextForest() so every cell advances from the same previous forest.
///
/// **Today you learn:** Simultaneous updates: compute a complete new grid from an unchanged old grid.
/// Newly ignited trees must wait for the next generation before igniting their own neighbours.
///
/// From the course folder containing README.md:
/// - Play: `java L08/M13_ForestFireSandbox.java`
/// - Check this mission: `java L08/Check.java M13_ForestFireSandbox.java`
///
/// the play command, choose Fire and click a tree to ignite it, then click Step to inspect how the
/// front moves before trying Run/Pause. After your repair: save the file, run the check command
/// above, then play again.
///
/// Need an explanation? Recall · F26 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
int[][] nextForest(int[][] before) {
    int[][] next = before;
    int[][] offsets = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
    for (int y = 0; y < 40; y++) {
        for (int x = 0; x < 60; x++) {
            if (before[y][x] != 1) {
                continue;
            }
            next[y][x] = 1;
            for (var d : offsets) {
                int nx = x + d[0], ny = y + d[1];
                if (nx >= 0 && nx < 60 && ny >= 0 && ny < 40 && before[ny][nx] == 2) {
                    next[y][x] = 2;
                }
            }
        }
    }
    return next;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F26">
/// ### Simultaneous updates: read one moment, build another
///
/// A simultaneous step gives every calculation the same old data. Write results into separate storage
/// and publish that storage after finishing the whole pass. Otherwise an earlier write can silently
/// become a later input, making traversal order part of the rule.
///
/// ```java
/// int[] tones = {2, 5, 9};
/// int[] shifted = new int[tones.length];
/// for (int i = 0; i < tones.length; i++) {
///     shifted[i] = tones[(i + 1) % tones.length];
/// }
/// ```
///
/// The result is `[5,9,2]`; tones stays `[2,5,9]` because every read uses it unchanged. Would
/// visiting the indices in reverse order change the result? The new array starts with zero values, so
/// every required output must be deliberately computed. A copied snapshot and a new output buffer
/// serve different roles.
///
/// Here `% tones.length` wraps the index after the last element back to zero.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int[][] next = before` — The new and old grids currently share storage, defeating a simultaneous
///   step.
/// - `int[][] offsets` — Lists the four cardinal neighbours, deliberately excluding diagonals.
/// - `before[y][x] != 1` — Only old tree cells enter this propagation branch.
/// - `before[ny][nx] == 2` — Tests for a fire in the previous generation, not one just created in next.
///
/// ### What the prepared part already does
///
/// edit() supplies Tree, Fire, and Erase brushes, while step() publishes the result of nextForest() once.
/// Buttons and a stopped-on-close timer handle stepping and running; this is a propagation toy, not a
/// wildfire predictor.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Decide every output cell's next meaning
///
/// This toy uses zero for empty, one for a tree and two for fire. A new int grid begins empty because
/// its cells start at zero. The transition contract is: old empty cells stay empty; old fire
/// disappears; an old tree stays a tree unless an old cardinal neighbour was burning. New fire can
/// influence only the following generation. Draw a three-tree line with fire at one end and predict
/// two separate steps. Which data must remain unchanged while the first step is being computed?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Writing into before lets a new fire immediately ignite the next tree in traversal order.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// A new zero-filled matrix also makes old fire disappear naturally.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Allocate a fresh 40×60 next matrix; read only before and publish next once after every cell is
/// computed.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[][] cells = new int[40][60];
boolean running;
int brush = 1;

/// reset(): Restores the starting Forest Fire Sandbox round, clearing the state that belongs to the
/// old round.
void reset(long seed) {
    var random = new Random(seed);
    cells = new int[40][60];
    for (int y = 0; y < 40; y++) {
        for (int x = 0; x < 60; x++) {
            cells[y][x] = random.nextDouble() < 0.64 ? 1 : 0;
        }
    }
}

/// edit(): Supplied brush validation plants, erases or ignites only an existing tree.
boolean edit(int x, int y, int value) {
    if (x < 0 || x >= 60 || y < 0 || y >= 40 || value < 0 || value > 2 || value == 2 && cells[y][x] != 1) {
        return false;
    }
    cells[y][x] = value;
    return true;
}

void step() {
    cells = nextForest(cells);
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; timers stop on
/// window close.
void showWindow() {
    var frame = new JFrame("Forest Fire Sandbox");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var canvas = new JPanel() {

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics;
            Color[] colours = {new Color(30, 25, 15), new Color(35, 145, 55), Color.ORANGE};
            for (int y = 0; y < 40; y++) {
                for (int x = 0; x < 60; x++) {
                    g.setColor(colours[cells[y][x]]);
                    g.fillRect(x * 10, y * 10, 10, 10);
                }
            }
        }
    };
    canvas.setPreferredSize(new Dimension(600, 400));
    var controls = new JPanel();
    var brushes = new JComboBox<>(new String[]{"Tree", "Fire", "Erase"});
    brushes.addActionListener(event -> brush = new int[]{1, 2, 0}[brushes.getSelectedIndex()]);
    brushes.setSelectedIndex(1);
    canvas.addMouseListener(new MouseAdapter() {

        public void mousePressed(MouseEvent event) {
            edit(event.getX() / 10, event.getY() / 10, brush);
            canvas.repaint();
        }
    });
    var run = new JButton("Run");
    var single = new JButton("Step");
    single.addActionListener(event -> {
        running = false;
        run.setText("Run");
        step();
        canvas.repaint();
    });
    run.addActionListener(event -> {
        running = !running;
        run.setText(running ? "Pause" : "Run");
    });
    var restart = new JButton("New forest");
    restart.addActionListener(event -> {
        running = false;
        run.setText("Run");
        reset(System.nanoTime());
        canvas.repaint();
    });
    controls.add(brushes);
    controls.add(single);
    controls.add(run);
    controls.add(restart);
    var timer = new Timer(100, event -> {
        if (running) {
            step();
            canvas.repaint();
        }
    });
    frame.addWindowListener(new WindowAdapter() {

        public void windowClosed(WindowEvent event) {
            timer.stop();
        }
    });
    timer.start();
    frame.add(new JLabel("Choose Fire and click a tree to ignite it; Step pauses and advances once."), BorderLayout.NORTH);
    frame.add(canvas, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

/// main(): Starts the model, then schedules the supplied window on Swing’s event thread.
void main() {
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
/// - `boolean` — A value that is either true or false.
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
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
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>

