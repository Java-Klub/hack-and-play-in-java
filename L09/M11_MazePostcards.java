// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L09.11 · MazePostcards
///
/// Make a maze postcard with a new layout from each seed. Its width changes how much twisting corridor
/// fits on the page.
///
/// **Your mission:** A typo produces a tiny two-column maze instead of the promised default 20. Repair
/// malformed-text handling while keeping the numeric 2–50 limit.
///
/// **Today you learn:** Input recovery should have a precise contract: malformed text gets a deliberate
/// default, while a parsed value still has to pass its bounds. A specific catch keeps these cases
/// separate.
///
/// From the course folder containing README.md:
/// - Play: `java L09/M11_MazePostcards.java`
/// - Check this mission: `java L09/Check.java M11_MazePostcards.java`
///
/// After the play command, enter a column count 2–50 or try oops; read Chosen value and inspect the
/// Java picture. Close it and run the play command again for another input. After your repair: save
/// the file, run the check command above, then play again.
///
/// Need an explanation? Recall · F31 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
int readColumns(String text) {
    try {
        int columns = Integer.parseInt(text.strip());
        if (columns < 2 || columns > 50) throw new IllegalArgumentException("Columns: 2..50.");
        return columns;
    } catch (NumberFormatException invalid) {
        return 2;
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
/// - `int columns` — Stores a whole number of maze columns; fractional input cannot become this integer.
/// - `columns < 2 || columns > 50` — Either comparison can reject a parsed but unusable maze size.
/// - `catch (NumberFormatException invalid)` — Selects the recovery path for malformed number text.
/// - `return 2;` — This value currently becomes the maze width after a typo.
///
/// ### What the prepared part already does
///
/// main sends the typed columns to readColumns. The supplied stack-based generator builds bounded
/// passages and render presents their walls directly in Java.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### A number can parse and still be rejected
///
/// For readColumns, `" 8 "` converts to 8 and returns normally. `"eight"` fails conversion and
/// reaches the NumberFormatException catch. `"1"` converts to 1, then the explicit bounds check
/// throws IllegalArgumentException, which is not handled by that specific catch. Trace `"2"` and
/// `"50"` to confirm both endpoints are allowed. Then predict whether `"51"` and `"fifty"` follow the
/// same path. The maze generator should receive either an accepted numeric width or the documented
/// malformed-text default, not an arbitrary tiny width.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The fallback handles a conversion failure, not every invalid value.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Keep the inclusive range guard outside the NumberFormatException case.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// main passes this result into the real generate call through render.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
boolean[][] generate(long seed, int columns, int rows) {
    if (columns < 2 || columns > 50 || rows < 2 || rows > 40) {
        throw new IllegalArgumentException("Maze dimensions: 2..50 by 2..40.");
    }
    boolean[][] open = new boolean[rows * 2 + 1][columns * 2 + 1];
    var random = new Random(seed);
    var stack = new ArrayDeque<Point>();
    stack.push(new Point(1, 1));
    open[1][1] = true;
    int[][] offsets = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}};
    while (!stack.isEmpty()) {
        Point cell = stack.peek();
        var choices = new ArrayList<Point>();
        for (var offset : offsets) {
            int x = cell.x + offset[0];
            int y = cell.y + offset[1];
            if (x > 0 && x < columns * 2 && y > 0 && y < rows * 2 && !open[y][x]) {
                choices.add(new Point(x, y));
            }
        }
        if (choices.isEmpty()) {
            stack.pop();
        } else {
            Point next = choices.get(random.nextInt(choices.size()));
            open[(cell.y + next.y) / 2][(cell.x + next.x) / 2] = true;
            open[next.y][next.x] = true;
            stack.push(next);
        }
    }
    open[1][0] = true;
    open[rows * 2 - 1][columns * 2] = true;
    return open;
}

/// Supplied image creation: generation and drawing remain visible in this file.
BufferedImage render(long seed, double option) {
    if (!Double.isFinite(option)) {
        throw new IllegalArgumentException("Finite dimensions required.");
    }
    var open = generate(seed, (int)option, 15);
    var image = new BufferedImage(open[0].length * 12, open.length * 12, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(Color.WHITE);
        for (int y = 0; y < open.length; y++) {
            for (int x = 0; x < open[y].length; x++) {
                if (open[y][x]) {
                    g.fillRect(x * 12, y * 12, 12, 12);
                }
            }
        }
    } finally {
        g.dispose();
    }
    return image;
}


/// Use the play command above to start the program. The check command checks the rule without
/// opening a window. The supplied part supports only the main game or experiment described above.
void showImages(String title, String[] captions, java.awt.image.BufferedImage[] images) {
    if (java.awt.GraphicsEnvironment.isHeadless()) return;
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame(title);
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        var pictures = new javax.swing.JPanel(new java.awt.GridLayout(1, images.length, 8, 8));
        for (int i = 0; i < images.length; i++) {
            var picture = new javax.swing.JPanel(new java.awt.BorderLayout());
            picture.add(new javax.swing.JLabel(captions[i], javax.swing.SwingConstants.CENTER), java.awt.BorderLayout.NORTH);
            double scale = Math.min(1, Math.min(900.0 / images.length / images[i].getWidth(), 550.0 / images[i].getHeight()));
            var icon = new javax.swing.ImageIcon(images[i].getScaledInstance(
                    Math.max(1, (int)(images[i].getWidth() * scale)),
                    Math.max(1, (int)(images[i].getHeight() * scale)), java.awt.Image.SCALE_FAST));
            picture.add(new javax.swing.JLabel(icon), java.awt.BorderLayout.CENTER);
            pictures.add(picture);
        }
        frame.add(pictures);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}


void main() {
    String input = IO.readln("Columns 2..50 (blank/default 20, q quits): ");
    if (input == null || input.strip().equalsIgnoreCase("q")) return;
    try {
        var option = readColumns(input);
        IO.println("Chosen value: " + option);
        showImages("Maze Postcards", new String[]{"Value " + option}, new java.awt.image.BufferedImage[]{render(42, option)});
    } catch (IllegalArgumentException failure) {
        IO.println("Invalid input: " + failure.getMessage());
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
/// - `double` — A type for approximate fractional numbers. Java uses a decimal point.
/// - `boolean` — A value that is either true or false.
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
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
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
