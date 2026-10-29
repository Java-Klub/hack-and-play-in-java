// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L11.05 · ChaosGame
///
/// Repeatedly jump halfway toward a randomly chosen corner. A surprisingly ordered fractal can grow from
/// a very small random rule.
///
/// **Your mission:** The final legal vertex is never chosen. Repair the choice range, including a single
/// available vertex and the no-vertices error.
///
/// **Today you learn:** Random bounds have an exclusive upper end. Translate a count of legal candidates
/// into the correct selection range without accidentally removing the last candidate.
///
/// From the course folder containing README.md:
/// - Play: `java L11/M05_ChaosGame.java`
/// - Check this mission: `java L11/Check.java M05_ChaosGame.java`
///
/// After the play command, type a seed or press Enter for 42; inspect the Java picture, then close
/// it and repeat the same seed or try 43 to compare. After your repair: save the file, run the
/// check command above, then play again.
///
/// Need an explanation? Recall · F27 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
int chooseVertex(Random random, int count) {
    if (count < 1) throw new IllegalArgumentException("No legal vertices.");
    return random.nextInt(Math.max(1, count - 1));
}

// <editor-fold defaultstate="collapsed" desc="Recall · F27">
/// ### Seeded randomness: repeat an experiment
///
/// A pseudorandom generator produces a sequence. A seed chooses its starting state; equal seeds and
/// equal calls reproduce equal results.
///
/// ```java
/// var first = new Random(9);
/// var second = new Random(9);
/// int a = first.nextInt(4);
/// int b = second.nextInt(4);
/// IO.println(a == b);
/// ```
///
/// This prints `true`. Each nextInt call advances that generator. `nextInt(4)` chooses an integer
/// from 0 through 3; the upper bound is excluded. Reuse one generator across a generated picture so
/// later choices use later positions in the sequence. Recreating it with the same seed inside a loop
/// repeatedly returns its first choice. Different seeds can still occasionally give equal individual
/// choices.
///
/// Predict: which integers can `nextInt(3)` return, and can it return 3?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `count < 1` — There is no legal choice when the candidate count is zero or negative.
/// - `random.nextInt` — Returns an integer starting at zero and strictly below its bound.
/// - `Math.max(1, count - 1)` — Keeps a positive bound but currently shrinks the legal range.
///
/// ### What the prepared part already does
///
/// render repeatedly asks chooseVertex for the next corner using one generator, moves the point and
/// paints its visits. main shows the chosen seed result directly in Java.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Random.nextInt(bound) excludes bound.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Subtracting one from count removes the final legal vertex.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Guard an empty range before making the bounded random call.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
double[] jump(double x, double y, double targetX, double targetY) {
    return new double[]{(x + targetX) / 2, (y + targetY) / 2};
}

/// Supplied image creation: generation and drawing remain visible in this file.
BufferedImage render(long seed, double option) {
    int count = (int)option;
    if (!Double.isFinite(option) || count < 100 || count > 300000) {
        throw new IllegalArgumentException("Points: 100..300000.");
    }
    var image = new BufferedImage(500, 420, BufferedImage.TYPE_INT_RGB);
    var random = new Random(seed);
    double[][] vertices = {{250, 15}, {15, 405}, {485, 405}};
    double[] point = {250, 210};
    for (int i = 0; i < count + 20; i++) {
        int corner = chooseVertex(random, 3);
        point = jump(point[0], point[1], vertices[corner][0], vertices[corner][1]);
        if (i >= 20) {
            image.setRGB((int)point[0], (int)point[1], Color.HSBtoRGB(corner / 3.0F, 0.6F, 1));
        }
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
    String input = IO.readln("Seed (blank 42, q quits): ");
    if (input == null || input.strip().equalsIgnoreCase("q")) return;
    try {
        long seed = input.isBlank() ? 42 : Long.parseLong(input.strip());
        showImages("Chaos Game", new String[]{"Seed " + seed}, new java.awt.image.BufferedImage[]{render(seed, 50000)});
    } catch (IllegalArgumentException failure) {
        IO.println("Use a whole-number seed: " + failure.getMessage());
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
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
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
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>

