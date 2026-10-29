// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
// </editor-fold>

/// # 08.14 · Minesweeper
///
/// The normal mine game and a point inspection lens already work. Expand that lens into a local X-ray
/// tool, then use what it reveals to plan a safe route.
///
/// **Your mission:** With Inspect selected, a click must show every mine in the clicked 3×3
/// neighbourhood, including the centre, but none outside it. A new inspection replaces the old one.
/// Inspection must not reveal safe cells, place flags, advance progress or lose the round. Corners must
/// clip safely; New board clears the lens. Ordinary reveal and mine clues stay unchanged.
///
/// **Today you learn:** Explore how a click updates a separate view of grid data. Bounds and
/// neighbourhood coordinates belong to the tool; the real game state must remain independent.
///
/// Select Inspect and click: the starter peeks at only that square. After expanding the lens, inspect
/// beside a suspected mine to see diagonal as well as straight neighbours. Turn Inspect off to reveal
/// safe cells normally; Flag mode and New board still work.

// <editor-fold defaultstate="expanded" desc="Program · explore the rules">
final int SIZE = 6;
final int MINE_COUNT = 6;
boolean[][] mines = new boolean[SIZE][SIZE];
boolean[][] revealed = new boolean[SIZE][SIZE];
boolean[][] flags = new boolean[SIZE][SIZE];
boolean lost;
boolean[][] inspected = new boolean[SIZE][SIZE];
int opened;
JButton[][] buttons = new JButton[SIZE][SIZE];
JLabel status = new JLabel();
JToggleButton flagMode = new JToggleButton("Flag mode");
JToggleButton inspectMode = new JToggleButton("Inspect");

int adjacent(int row, int col) {
    int count = 0;
    for (int r = row - 1; r <= row + 1; r++) {
        for (int c = col - 1; c <= col + 1; c++) {
            if (inside(r, c) && (r != row || c != col) && mines[r][c]) count++;
        }
    }
    return count;
}

boolean inside(int row, int col) {
    return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
}

/// reset(): Restores the starting Minesweeper round, clearing the state that belongs to the old
/// round.
void reset(long seed) {
    mines = new boolean[SIZE][SIZE];
    revealed = new boolean[SIZE][SIZE];
    flags = new boolean[SIZE][SIZE];
    inspected = new boolean[SIZE][SIZE];
    var random = new Random(seed);
    int placed = 0;
    while (placed < MINE_COUNT) {
        int row = random.nextInt(SIZE);
        int col = random.nextInt(SIZE);
        if (!mines[row][col]) {
            mines[row][col] = true;
            placed++;
        }
    }
    lost = false;
    opened = 0;
}

boolean won() {
    return !lost && opened == SIZE * SIZE - MINE_COUNT;
}

/// reveal(): Supplied recursive flood marks before revisiting neighbours, opening safe zero
/// regions.
boolean reveal(int row, int col) {
    if (!inside(row, col) || revealed[row][col] || flags[row][col] || lost || won()) {
        return false;
    }
    revealed[row][col] = true;
    if (mines[row][col]) {
        lost = true;
    } else {
        opened++;
        if (adjacent(row, col) == 0) {
            for (int r = row - 1; r <= row + 1; r++) {
                for (int c = col - 1; c <= col + 1; c++) {
                    reveal(r, c);
                }
            }
        }
    }
    return true;
}

void inspect(int row, int col) {
    inspected = new boolean[SIZE][SIZE];
    if (!inside(row, col)) return;
    for (int r = row - 1; r <= row + 1; r++) {
        for (int c = col - 1; c <= col + 1; c++) {
            if (inside(r, c)) inspected[r][c] = mines[r][c];
        }
    }
}

boolean toggleFlag(int row, int col) {
    if (!inside(row, col) || revealed[row][col] || lost || won()) {
        return false;
    }
    flags[row][col] = !flags[row][col];
    return true;
}

/// refresh(): Copies the current model values into button labels, enabled states and status text.
void refresh() {
    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            String text = inspected[r][c] ? "*" : flags[r][c] ? "F" : "?";
            if (revealed[r][c] || (lost && mines[r][c])) {
                int count = adjacent(r, c);
                text = mines[r][c] ? "*" : count == 0 ? "" : Integer.toString(count);
            }
            buttons[r][c].setText(text);
            buttons[r][c].setEnabled(!revealed[r][c] && !lost && !won());
        }
    }
    status.setText(lost ? "Mine! Start a new board." : won() ? "All safe cells found!" : "Safe cells: " + opened + "/" + (SIZE * SIZE - MINE_COUNT) + ". First click can hit a mine.");
}

/// show(): Supplied later topic: Swing button callbacks invoke the model and refresh its visible
/// state.
void show() {
    var frame = new JFrame("Minesweeper");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var grid = new JPanel(new GridLayout(SIZE, SIZE, 2, 2));
    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            int row = r;
            int col = c;
            var button = new JButton();
            buttons[r][c] = button;
            button.addActionListener(event -> {
                if (inspectMode.isSelected()) {
                    inspect(row, col);
                }
                else if (flagMode.isSelected()) {
                    toggleFlag(row, col);
                }
                else {
                    reveal(row, col);
                }
                refresh();
            });
            grid.add(button);
        }
    }
    var controls = new JPanel();
    var restart = new JButton("New board");
    restart.addActionListener(event -> {
        reset(System.nanoTime());
        flagMode.setSelected(false);
        inspectMode.setSelected(false);
        refresh();
    });
    controls.add(inspectMode);
    controls.add(flagMode);
    controls.add(restart);
    frame.add(status, BorderLayout.NORTH);
    frame.add(grid, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    refresh();
    frame.setSize(520, 510);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

/// main(): Starts the model, then schedules the supplied window on Swing’s event thread.
void main() {
    reset(System.nanoTime());
    SwingUtilities.invokeLater(this::show);
}
// </editor-fold>


// <editor-fold defaultstate="expanded" desc="Learn · F18">
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
/// ### Observe, then trace the flow
///
/// The lens is an observation, not a move. Follow a button click to the selected input mode, then
/// distinguish the information drawn on a button from the cells actually counted as opened. A mask is a
/// same-shaped boolean grid marking which mines the lens currently shows. Replacing that mask avoids
/// leaving old discoveries on screen.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Experiment and replay">
/// Inspect two different places and confirm that stars from the first view disappear. The safe-cell
/// count stays fixed even when the lens sees a mine. Try a corner and then turn Inspect off before
/// making a planned safe move. New board must erase the previous inspection.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Compare Inspect with a real reveal: the progress number must change only for the latter.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Search the Inspect button’s action path. Keep its grid separate from flags and the real
/// revealed-cell grid; do not change the existing clue counter.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Find inspect. It currently copies only the clicked mine into inspected. Replace that single-point
/// work with bounded row/column ranges one step around the click, using a fresh mask each time.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Limit your lens to three uses per board and display the remaining charges. Can that smaller
/// advantage still help a partner finish a board?
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
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `else` — The alternative when the preceding if condition is false.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
