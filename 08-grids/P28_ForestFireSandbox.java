// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # 08.28 · ForestFireSandbox
///
/// The sandbox already spreads a toy fire to four neighbouring trees. Change the world to a
/// deliberately one-way east wind, then design a firebreak and test a prediction.
///
/// **Your mission:** In the new toy model, a tree ignites only if the cell immediately west of it was
/// burning in the previous generation. Old fire becomes empty; empty cells stay empty. Never wrap at
/// borders, mutate the old forest, or spread twice within one tick. North, south, west and diagonal
/// spread must stop. Keep the painting and Step/Run controls.
///
/// **Today you learn:** Compare two neighbourhood hypotheses while preserving a shared old generation.
/// A new wind rule changes which facts are read, not when new facts become visible.
///
/// Use Erase to make space, Tree to draw a short horizontal line, and Fire to ignite its middle. Step
/// pauses and advances once. In the starter it spreads both ways; after the hack only the east/right
/// half burns one cell per step.

// <editor-fold defaultstate="expanded" desc="Program · explore the rules">
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

int[][] nextForest(int[][] before) {
    int[][] next = new int[40][60];
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


// <editor-fold defaultstate="collapsed" desc="Recall · F26">
/// ### One generation uses one shared past
///
/// A simultaneous update means every new cell reads the same old world. If you overwrite the old cells
/// as you go, the next column may see a future that its neighbour never saw.
///
/// Consider a toy line: a cell turns on when it or its left neighbour was on. The left edge has an off
/// neighbour. For old `[true,false,false]`, the next row must be `[true,true,false]`.
///
/// ```java
/// boolean[] old = {true, false, false};
/// boolean[] next = new boolean[old.length];
/// for (int x = 0; x < old.length; x++) {
///     next[x] = old[x] || (x > 0 && old[x - 1]);
/// }
/// ```
///
/// Why not three true cells? The third cell reads old[1], which is false, not the newly written
/// next[1]. Old remains untouched, so visiting columns in reverse would give the same result. Publish
/// the complete next row only after computing it.
///
/// Draw OLD above NEXT, with arrows from the cells read to the cell written. Predict the second
/// generation. A two-dimensional world follows the same principle, with separate writable rows as well
/// as a separate outer array.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ### Observe, then trace the flow
///
/// Draw OLD above NEXT and predict a tiny line before running it. A tree reads the old neighbour that
/// could send fire into it: eastward travel means reading the neighbour to the west. Do not confuse
/// travel direction with lookup direction. The drawing and brushes already work, so use them to build a
/// controlled experiment rather than changing random forests repeatedly.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Experiment and replay">
/// Place one fire with trees on all four sides, then Step. Only the eastern tree should ignite and the
/// old fire should clear. Add a second eastern tree: it waits one more Step. Break that line with an
/// empty square and predict where the front stops.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// For OLD [tree, fire, tree, tree], draw the next line. A tree beyond the newly lit tree must wait.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Bound the search to the generation calculation called by Step. Keep its new grid; change the
/// neighbour evidence used for a tree.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Find nextForest. For a tree at (x,y), read only old[y][x-1] when x>0. Preserve old-fire-to-empty and
/// empty-to-empty transitions and return an independently built next grid.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Invent a north-east wind with two explicit incoming neighbours. Reuse the same painted test scene to
/// distinguish it from both earlier models.
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
