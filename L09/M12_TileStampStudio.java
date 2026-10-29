// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L09.12 · TileStampStudio
///
/// Stamp your own tile across a surface and watch a small motif become a pattern. The repeat control
/// should let you choose a sparse design or a dense one.
///
/// **Your mission:** Every accepted number becomes six repeats. Make a valid choice reach the live
/// pattern, and make rejected text preserve the previous setting.
///
/// **Today you learn:** Parse and validate a candidate before committing it to shared state. Returning
/// success should mean the requested setting was actually applied, not merely that parsing worked.
///
/// From the course folder containing README.md:
/// - Play: `java L09/M12_TileStampStudio.java`
/// - Check this mission: `java L09/Check.java M12_TileStampStudio.java`
///
/// After the play command, click tile cells to switch ink on or off; enter 3 and click Repeats to
/// change the pattern density. Clear resets the tile. After your repair: save the file, run the
/// check command above, then play again.
///
/// Need an explanation? Recall · F31 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
boolean applyRepeats(String text) {
    try {
        int candidate = Integer.parseInt(text.strip());
        if (candidate < 1 || candidate > 12) return false;
        repeats = candidate;
        return true;
    } catch (NumberFormatException invalid) {
        return false;
    }
}

// <editor-fold defaultstate="collapsed" desc="Recall · F31">
/// ### Parse, validate, then commit
///
/// Parsing converts text; validation checks whether the resulting number is allowed. These can fail
/// differently.
///
/// ```java
/// int readCopies(String text) {
///     try {
///         int n = Integer.parseInt(text.strip());
///         if (n < 1 || n > 5) {
///             throw new IllegalArgumentException("Copies: 1..5");
///         }
///         return n;
///     } catch (NumberFormatException invalid) {
///         return 3;
///     }
/// }
/// ```
///
/// `readCopies(" 2 ")` returns `2`: conversion and validation succeed. `"oops"` fails during
/// conversion and reaches `catch`, returning `3`. A numeric `"8"` converts successfully, then throws
/// a different exception; this narrow catch does not handle it. `return` ends the method; `throw`
/// leaves the normal path. Change stored game state only after validation succeeds.
///
/// Predict the path for `"five"`, `"5"` and `"0"`.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int candidate` — Keeps a proposed value separate from the currently active repeats field.
/// - `candidate < 1 || candidate > 12` — Rejects an integer outside the supported repeat range before
///   changing the model.
/// - `repeats = 6` — Commits a constant instead of the parsed proposal.
/// - `return false;` — Signals rejection so the caller can keep the previous pattern setting.
///
/// ### What the prepared part already does
///
/// Each click switches one source tile cell between ink and blank. render repeats that tile, and
/// the Repeats button calls applyRepeats for the actual preview; Clear resets the tile.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Validate before changing the stamp
///
/// Parsing and changing the model are different steps. If repeats was 4, malformed `"oops"` must
/// return false and leave 4 stored. Numeric `"13"` also leaves 4, because it fails the range check;
/// this rejection uses return false rather than throwing. For accepted `"3"`, the stored repeat count
/// must reflect that accepted request. Trace the current code: which line stores a constant after the
/// candidate has already passed validation? Predict the stored value after valid input followed by a
/// typo; a failed second attempt must not erase the first success.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Parse into a temporary candidate, not directly into the field.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Both range checks must happen before assignment.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// On success assign the candidate rather than the original default.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[][] tile = new int[8][8];
int[] palette = {0xFFFFFF, 0x182030};
JButton[][] buttons = new JButton[8][8];

boolean paint(int row, int col, int colour) {
    if (row < 0 || row >= 8 || col < 0 || col >= 8 || colour < 0 || colour >= palette.length) {
        return false;
    }
    tile[row][col] = colour;
    return true;
}

/// Supplied image creation: generation and drawing remain visible in this file.
BufferedImage render(int repeats) {
    if (repeats < 1 || repeats > 12) {
        throw new IllegalArgumentException("Repeats: 1..12.");
    }
    int size = repeats * 8 * 6;
    var image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < size; y++) {
        for (int x = 0; x < size; x++) {
            image.setRGB(x, y, palette[tile[y / 6 % 8][x / 6 % 8]]);
        }
    }
    return image;
}

void refresh() {
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            buttons[row][col].setBackground(new Color(palette[tile[row][col]]));
            buttons[row][col].setOpaque(true);
        }
    }
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Tile Stamp Studio");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var preview = new JPanel(){
        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(render(repeats), 0, 0, null);
        }
    };
    preview.setPreferredSize(new Dimension(288, 288));
    var grid = new JPanel(new GridLayout(8, 8, 1, 1));
    grid.setPreferredSize(new Dimension(288, 288));
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            int r = row;
            int c = col;
            var button = new JButton();
            buttons[row][col] = button;
            button.addActionListener(event -> {
                paint(r, c, tile[r][c] == 0 ? 1 : 0);
                refresh();
                preview.repaint();
            });
            grid.add(button);
        }
    }
    var controls = new JPanel();
    var clear = new JButton("Clear");
    clear.addActionListener(event -> {
        tile = new int[8][8];
        refresh();
        preview.repaint();
    });
    var status = new JLabel("Repeated preview");
    var repeatText = new JTextField("6", 3);
    var apply = new JButton("Repeats");
    apply.addActionListener(event -> {
        boolean accepted = applyRepeats(repeatText.getText());
        status.setText(accepted ? "Repeats: " + repeats : "Use a whole number 1..12");
        preview.repaint();
    });
    controls.add(repeatText);
    controls.add(apply);
    controls.add(clear);
    frame.add(grid, BorderLayout.WEST);
    frame.add(preview, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    frame.add(status, BorderLayout.NORTH);
    refresh();
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

int repeats = 6;

/// Use the play command above to start the program. The check command checks the rule without
/// opening a window. The supplied part supports only the main game or experiment described above.
void main() {
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
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
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
/// - `protected` — A visibility keyword used by supplied library callbacks, such as paintComponent.
/// - `super` — In super.paintComponent(g), asks the supplied parent implementation to paint its standard
///   background first.
/// - `null` — No object is present.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>

