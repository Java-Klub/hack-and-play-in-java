// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L09.12 · TileStampStudio
///
/// The stamp editor already accepts numbers. Give it a tiny command language so a planned sequence can
/// zoom the density without calculating every new number yourself.
///
/// **Your mission:** Add denser to double the current repeat count and sparser to halve it only when it
/// is even. Commands ignore case and surrounding whitespace. Keep numeric choices 1–12. Reject a typo,
/// an odd halving or any result outside 1–12 without changing the live pattern. A command is relative
/// to the last accepted setting, not always the initial one.
///
/// **Today you learn:** Explore the boundary between text, a proposed value and a committed live
/// setting. Named commands and numeric text share validation but take different conversion paths.
///
/// Draw an asymmetric tile. Enter 3 and click Repeats, then try denser, denser, sparser. The starter
/// refuses those words; your command tool must visibly produce densities 6, 12, 6. Clear resets the
/// drawing, not the density.

// <editor-fold defaultstate="expanded" desc="Program · explore the rules">
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
    int size = 288;
    var image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < size; y++) {
        for (int x = 0; x < size; x++) {
            image.setRGB(x, y, palette[tile[(y * repeats * 8 / size) % 8][(x * repeats * 8 / size) % 8]]);
        }
    }
    return image;
}

boolean applyRepeats(String text) {
    String command = text.strip();
    try {
        int candidate;
        if (command.equalsIgnoreCase("denser")) {
            candidate = repeats * 2;
        } else if (command.equalsIgnoreCase("sparser")) {
            if (repeats % 2 != 0) return false;
            candidate = repeats / 2;
        } else {
            candidate = Integer.parseInt(command);
        }
        if (candidate < 1 || candidate > 12) return false;
        repeats = candidate;
        return true;
    } catch (NumberFormatException invalid) {
        return false;
    }
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

void main() {
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>


// <editor-fold defaultstate="collapsed" desc="Recall · F31">
/// ### Text conversion and valid values are different questions
///
/// A number field starts as text. Conversion can fail because no number was written; validation can
/// reject a perfectly readable number. Decide the two policies separately.
///
/// ```java
/// int readZoom(String text) {
///     try {
///         int zoom = Integer.parseInt(text.strip());
///         if (zoom < 1 || zoom > 4) throw new IllegalArgumentException("Zoom: 1..4");
///         return zoom;
///     } catch (NumberFormatException typo) {
///         return 2;
///     }
/// }
/// ```
///
/// Trace `" 3 "`: strip removes surrounding whitespace, parseInt produces 3, bounds accept it. `"oops"`
/// cannot convert, so catch returns 2. `"7"` converts successfully but fails validation; the explicitly
/// thrown IllegalArgumentException leaves this method. Catching only NumberFormatException keeps those
/// paths distinct.
///
/// For a decimal, use Double.parseDouble; also consider NaN and infinity, which are readable but not
/// finite numbers. For an interactive setting, parse into a local candidate before changing the stored
/// field. Predict the path for `"0"`, `"two"` and `"4"`. Which inputs should preserve the previous live
/// setting?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ### Observe, then trace the flow
///
/// Trace the text from the button through conversion to a possible setting and finally to the live
/// preview. An intentional word is not a malformed number: recognise it before numeric parsing. A
/// relative command reads the current accepted value. Work on a local candidate so a refused command
/// cannot damage the previous picture. Odd/even uses remainder: a whole number divided by two has
/// remainder zero exactly when it is even.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Experiment and replay">
/// Draw one off-centre mark and execute the planned 3 → denser → denser → sparser sequence. Compare
/// each visible density with the plan. Try denser at twelve, sparser at three and a typo; the previous
/// picture must remain. Finally enter an ordinary number again.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Write the expected density after each of 3, denser, denser, sparser. Then predict one more denser
/// from twelve.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Find where the Repeats button converts text. Recognise the two words before the existing integer
/// parser; keep one shared range check afterwards.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Find applyRepeats. Compute a local candidate from repeats for denser/sparser, refuse odd halving,
/// otherwise parse the number. Commit only a candidate inside 1–12 and preserve the old value on every
/// refusal.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Invent a reset-density command with your favourite sparse setting. Decide whether it should also
/// erase the tile, then make that distinction clear in the interface.
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
