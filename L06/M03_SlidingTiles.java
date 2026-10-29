// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
// </editor-fold>

/// # L06.03 · SlidingTiles
///
/// Slide numbered tiles into order with one empty space. Challenge a friend on the same puzzle and
/// compare how many moves you need.
///
/// **Your mission:** A new puzzle starts with moves already counted during shuffling. Repair the final
/// counter reset without changing how solvable boards are created.
///
/// **Today you learn:** Scope and state lifetime: the shuffle deliberately uses the normal move method.
/// Its effects belong to a field that must be reset after setup, not hidden by a temporary variable.
///
/// Click a numbered tile immediately beside the gap to slide it into the
/// empty space.
void reset(long seed) {
    for (int i = 0; i < 9; i++) {
        tiles[i] = (i + 1) % 9;
    }
    var random = new Random(seed);
    for (int step = 0; step < 100; step++) {
        var choices = new ArrayList<Integer>();
        for (int i = 0; i < 9; i++) {
            if (canMove(i)) {
                choices.add(i);
            }
        }
        slide(choices.get(random.nextInt(choices.size())));
    }
    if (solved()) {
        slide(7);
    }
    int moves = 0;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F14">
/// ### Scope: which storage does this name mean?
///
/// A field belongs to the current object and can survive between method calls. A local variable
/// belongs to one call or block. A declaration includes a type; assignment to an existing name does
/// not declare another variable. A local name can shadow a field with the same spelling.
///
/// ```java
/// int volume = 7;
///
/// void whisper() {
///     int volume = 2;
///     IO.println(volume);
/// }
/// ```
///
/// `whisper()` prints two while the field stays seven: the local declaration created separate
/// storage. Predict a later field read; `this.volume` explicitly names the field when needed. Which
/// value should survive several calls, and which value belongs to only one temporary calculation?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `new Random(seed)` — Starts a repeatable shuffle from the provided seed value.
/// - `choices.add(i)` — Remembers one currently legal tile position as a shuffle candidate.
/// - `slide(` — Calls the real move method, including its normal counter update.
/// - `int moves = 0` — A local declaration whose lifetime ends with this method call.
///
/// ### What the prepared part already does
///
/// canMove() finds tiles next to the gap, and slide() both moves a tile and counts it. reset() shuffles
/// through legal moves so the puzzle stays solvable; button callbacks and drawing are supplied.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The seed controls tile order, not the move counter.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The counter shown by refresh() is the field declared above the methods.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Remove the local int declaration from the reset assignment.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Make it yours">
/// Replace random shuffling with five legal slides you deliberately choose through the existing slide
/// method. Give a partner the resulting board and keep your reverse solution. The setup may move tiles,
/// but the displayed player count must still begin at zero.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[] tiles = new int[9];
int moves;
JButton[] buttons = new JButton[9];
JLabel status = new JLabel();

int gap() {
    for (int i = 0; i < tiles.length; i++) {
        if (tiles[i] == 0) {
            return i;
        }
    }
    throw new IllegalStateException("The board needs a gap.");
}

/// canMove(): Accepts a tile exactly one Manhattan step from the gap, excluding row wrapping.
boolean canMove(int index) {
    if (index < 0 || index >= tiles.length) {
        return false;
    }
    int empty = gap();
    return Math.abs(index / 3 - empty / 3) + Math.abs(index % 3 - empty % 3) == 1;
}

void slide(int index) {
    int empty = gap();
    tiles[empty] = tiles[index];
    tiles[index] = 0;
}

boolean solved() {
    for (int i = 0; i < 8; i++) {
        if (tiles[i] != i + 1) {
            return false;
        }
    }
    return tiles[8] == 0;
}

boolean play(int index) {
    if (!canMove(index) || solved()) {
        return false;
    }
    slide(index);
    moves++;
    return true;
}

/// refresh(): Copies the current model values into button labels, enabled states and status text.
void refresh() {
    for (int i = 0; i < 9; i++) {
        buttons[i].setText(tiles[i] == 0 ? "" : Integer.toString(tiles[i]));
        buttons[i].setEnabled(tiles[i] != 0 && !solved());
    }
    status.setText((solved() ? "Solved! " : "Click a tile next to the gap. ") + "Moves: " + moves);
}

/// show(): Supplied later topic: Swing button callbacks invoke the model and refresh its visible
/// state.
void show() {
    var frame = new JFrame("Sliding Tiles");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var grid = new JPanel(new GridLayout(3, 3, 4, 4));
    for (int i = 0; i < 9; i++) {
        int index = i;
        var button = new JButton();
        buttons[i] = button;
        button.addActionListener(event -> {
            play(index);
            refresh();
        });
        grid.add(button);
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
    frame.setSize(380, 390);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

/// main(): Starts the model, then schedules the supplied window on Swing’s event thread.
void main() {
    reset(System.nanoTime());
    SwingUtilities.invokeLater(this::show);
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
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
