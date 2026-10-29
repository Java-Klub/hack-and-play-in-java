// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L09.11 · MazePostcards
///
/// Make a maze postcard with a new layout from each seed. Its width changes how much twisting corridor
/// fits on the page.
///
/// **Your mission:** Build readColumns(text): accept integers 2–50 inclusive, plus case-insensitive
/// aliases tiny=6 and wide=40 after stripping whitespace. Malformed text falls back to 20; readable
/// out-of-range integers are rejected. The unfinished selector currently ignores every requested size
/// and alias.
///
/// **Today you learn:** Build a deliberate input boundary: distinguish conversion, validation and
/// committing a live setting.
///
/// Run the program and enter tiny, wide, or a number 2–50; try oops for the fallback. Read Chosen
/// value, inspect the picture, then close it and rerun to compare.
int readColumns(String text) {
    String word = text.strip();
    if (word.equalsIgnoreCase("tiny")) return 6;
    if (word.equalsIgnoreCase("wide")) return 40;
    try {
        int columns = Integer.parseInt(word);
        if (columns < 2 || columns > 50) throw new IllegalArgumentException("Columns: 2..50.");
        return columns;
    } catch (NumberFormatException invalid) {
        return 20;
    }
}

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
/// ## Read this part
///
/// Handle the named aliases before numeric parsing, or they would look like typos. equalsIgnoreCase
/// compares text while ignoring letter case. Then apply the same conversion/validation split as a
/// numeric-only field. A true numeric -1 must not become a default maze. The picture is generated from
/// the returned column count.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Recognise intentional words before deciding that nonnumeric text is malformed.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Both aliases should ignore surrounding whitespace and letter case.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Return the two named sizes first; then parse, validate 2–50, and catch only format failure for
/// fallback 20.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Invent a third named postcard size, such as snake for a long narrow maze. State its numeric meaning
/// and compare the actual path on your new shape.
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
