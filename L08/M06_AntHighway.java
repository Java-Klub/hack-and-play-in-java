// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L08.06 · AntHighway
///
/// Let a tiny ant turn, repaint tiles, and slowly build a surprising trail. Watch for a highway emerging
/// from its simple repeated rule.
///
/// **Your mission:** The west direction sends the ant east, distorting its trail. Repair the direction
/// offsets in step() while preserving turning, repainting, and edge wrapping.
///
/// **Today you learn:** Grid neighbours: parallel dx and dy arrays describe one cardinal step per
/// direction. The same direction index must select matching horizontal and vertical offsets.
///
/// Run the program and click One step to inspect one ant move before using Run/Pause to grow a
/// longer trail.
void step() {
    direction = Math.floorMod(direction + (black[y][x] ? -1 : 1), 4);
    black[y][x] = !black[y][x];
    int[] dx = {0, 1, 0, 1};
    int[] dy = {-1, 0, 1, 0};
    x = Math.floorMod(x + dx[direction], 81);
    y = Math.floorMod(y + dy[direction], 61);
    steps++;
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
/// - `black[y][x] = !black[y][x]` — Negation flips the current tile’s boolean colour.
/// - `int[] dx` — Horizontal movement offsets indexed by the ant’s direction.
/// - `dx[direction]` — Reads the horizontal part of this particular direction’s step.
/// - `Math.floorMod` — Wraps coordinates and direction indices into a valid nonnegative range.
///
/// ### What the prepared part already does
///
/// The buttons call one step or run bounded batches on a Swing timer. reset() clears the board, and
/// painting shows stored tile colours and the current ant; step() is the whole movement rule.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Directions 0,1,2,3 mean north,east,south,west.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// West changes x by -1 and leaves y unchanged.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Use dx={0,1,0,-1}; dy already supplies {-1,0,1,0}.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
boolean[][] black = new boolean[61][81];
int x = 40;
int y = 30;
int direction;
long steps;
boolean running;

/// reset(): Restores the starting Ant Highway round, clearing the state that belongs to the old
/// round.
void reset() {
    black = new boolean[61][81];
    x = 40;
    y = 30;
    direction = 0;
    steps = 0;
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; timers stop on
/// window close.
void showWindow() {
    var frame = new JFrame("Ant Highway");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var canvas = new JPanel() {

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics;
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 648, 488);
            g.setColor(Color.BLACK);
            for (int row = 0; row < 61; row++) {
                for (int col = 0; col < 81; col++) {
                    if (black[row][col]) {
                        g.fillRect(col * 8, row * 8, 8, 8);
                    }
                }
            }
            g.setColor(Color.RED);
            g.fillRect(x * 8, y * 8, 8, 8);
        }
    };
    canvas.setPreferredSize(new Dimension(648, 488));
    var controls = new JPanel();
    var label = new JLabel("Steps: 0");
    var run = new JButton("Run");
    run.addActionListener(event -> {
        running = !running;
        run.setText(running ? "Pause" : "Run");
    });
    var single = new JButton("One step");
    single.addActionListener(event -> {
        running = false;
        run.setText("Run");
        step();
        label.setText("Steps: " + steps);
        canvas.repaint();
    });
    var restart = new JButton("Reset");
    restart.addActionListener(event -> {
        running = false;
        run.setText("Run");
        reset();
        label.setText("Steps: 0");
        canvas.repaint();
    });
    controls.add(run);
    controls.add(single);
    controls.add(restart);
    controls.add(label);
    var timer = new Timer(25, event -> {
        if (running) {
            for (int i = 0; i < 100; i++) {
                step();
            }
        }
        label.setText("Steps: " + steps);
        canvas.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {

        public void windowClosed(WindowEvent event) {
            timer.stop();
        }
    });
    timer.start();
    frame.add(canvas, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

/// main(): Starts the model, then schedules the supplied window on Swing’s event thread.
void main() {
    reset();
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
/// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
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
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
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
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
