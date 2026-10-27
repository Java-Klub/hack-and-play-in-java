// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
// </editor-fold>

/// # L06.09 · LightsOut
///
/// Turn every light off by clicking crosses of cells. Become the tester who catches convincing but unfair
/// copies of the toggle rule.
///
/// **Your mission:** The centre-only example cannot catch every wrong rule. Expand learnerChecks() with
/// valid board examples that distinguish different cross shapes.
///
/// **Today you learn:** Writing functional checks: choose inputs and expected results that could disprove
/// a broken rule. The learner’s work is the examples, not a replacement flip() algorithm.
///
/// Run File and click cells to try extinguishing every light, then use Test File to see which wrong rule
/// your examples still miss.
/// After your repair: Save → Test File → play again.
record CrossExample(int row, int column, int expected) {
}

CrossExample[] learnerChecks() {
    return new CrossExample[]{new CrossExample(2, 2, 5), new CrossExample(0, 0, 3), new CrossExample(0, 2, 4)};
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `record CrossExample(int row, int column, int expected)` — One example bundles a click position with
///   the expected number of lit cells on an empty board.
/// - `CrossExample[]` — An array lets the checker run several independent examples.
/// - `new CrossExample(2, 2, 5)` — The existing example selects a legal centre cell and its expected
///   result.
/// - `return` — Hands your example data to both the startup check and Test File.
///
/// ### What the prepared part already does
///
/// flip() is already correct, so Run File remains playable. Startup checks examples against that rule;
/// Test File also tries named wrong rules and reports which one your examples failed to reject.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. Start from an all-off board for each example; expected counts lit cells after one flip.
/// 2. A centre has five cells, an edge has four and a corner has three.
/// 3. Keep the centre case and add (0,0,3) and (0,2,4); explain which mutant each catches.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int SIZE = 5;
boolean[][] lights = new boolean[SIZE][SIZE];
int moves;
JButton[][] buttons = new JButton[SIZE][SIZE];
JLabel status = new JLabel();

/// flip(): Toggles the centre and valid cardinal neighbours; applying it twice restores the board.
void flip(int row, int col) {
    int[][] offsets = {{0, 0}, {-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    for (var offset : offsets) {
        int r = row + offset[0];
        int c = col + offset[1];
        if (r >= 0 && r < SIZE && c >= 0 && c < SIZE) {
            lights[r][c] = !lights[r][c];
        }
    }
}

boolean solved() {
    for (var row : lights) {
        for (boolean light : row) {
            if (light) {
                return false;
            }
        }
    }
    return true;
}

/// reset(): Restores the starting Lights Out round, clearing the state that belongs to the old
/// round.
void reset(long seed) {
    lights = new boolean[SIZE][SIZE];
    var random = new Random(seed);
    for (int i = 0; i < 40; i++) {
        flip(random.nextInt(SIZE), random.nextInt(SIZE));
    }
    if (solved()) {
        flip(2, 2);
    }
    moves = 0;
}

boolean play(int row, int col) {
    if (row < 0 || row >= SIZE || col < 0 || col >= SIZE || solved()) {
        return false;
    }
    flip(row, col);
    moves++;
    return true;
}

/// refresh(): Copies the current model values into button labels, enabled states and status text.
void refresh() {
    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            buttons[r][c].setText(lights[r][c] ? "ON" : "off");
            buttons[r][c].setBackground(lights[r][c] ? Color.YELLOW : Color.DARK_GRAY);
            buttons[r][c].setForeground(lights[r][c] ? Color.BLACK : Color.WHITE);
            buttons[r][c].setOpaque(true);
        }
    }
    status.setText((solved() ? "All lights out! " : "Turn every light off. ") + "Moves: " + moves);
}

/// show(): Supplied later topic: Swing button callbacks invoke the model and refresh its visible
/// state.
void show() {
    var frame = new JFrame("Lights Out");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var grid = new JPanel(new GridLayout(SIZE, SIZE, 3, 3));
    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            int row = r;
            int col = c;
            var button = new JButton();
            buttons[r][c] = button;
            button.addActionListener(event -> {
                play(row, col);
                refresh();
            });
            grid.add(button);
        }
    }
    var restart = new JButton("New puzzle");
    restart.addActionListener(event -> {
        reset(System.nanoTime());
        refresh();
    });
    frame.add(status, BorderLayout.NORTH);
    frame.add(grid, BorderLayout.CENTER);
    frame.add(restart, BorderLayout.SOUTH);
    refresh();
    frame.setSize(440, 480);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

/// main(): Starts the model, then schedules the supplied window on Swing’s event thread.
void main() {
    verifyExamples();
    reset(System.nanoTime());
    SwingUtilities.invokeLater(this::show);
}

/// verifyExamples(): Startup runs your examples against the real game rule; supplied save/restore
/// keeps the round unchanged.
void verifyExamples() {
    boolean[][] saved = lights;
    try {
        for (var example : learnerChecks()) {
            if (example.row()<0 || example.row() >= SIZE || example.column()<0 || example.column() >= SIZE) {
                throw new IllegalArgumentException("Cross examples must use valid game cells 0..4");
            }
            lights = new boolean[SIZE][SIZE];
            flip(example.row(), example.column());
            int count = 0;
            for (var row : lights) {
                for (boolean on : row) {
                    if (on) {
                        count++;
                    }
                }
            }
            if (count != example.expected()) {
                throw new IllegalStateException("Cross example has wrong expected result");
            }
        }
    } finally {
        lights = saved;
    }
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
/// - `final` — The named value cannot be assigned another value afterwards. A referenced mutable object
///   can still change.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
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
/// - `record` — A small named bundle of values. Its component accessors use the component names with ().
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>

