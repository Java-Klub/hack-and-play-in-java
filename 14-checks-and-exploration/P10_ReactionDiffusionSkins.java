// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # 14.10 · ReactionDiffusionSkins
///
/// The creature-skin simulation already runs. Become its experiment designer: change one local recipe
/// at a time and test whether your explanation matches the pattern you actually obtain.
///
/// **Your mission:** Build a fair three-skin experiment: use the same seed and step count for
/// reaction recipes a×b², half that reaction, and zero reaction. Swap only the selected chemistry
/// role; reuse the same diffusion and rendering. Return the three pictures in that order and restore
/// the caller’s original role afterwards, including when a run fails.
///
/// **Investigation:** Investigate composition by swapping one role under controlled conditions.
///
/// The window compares Standard, Half reaction and No reaction from the same starting state. In the
/// starter they are copies of the same recipe. After your experiment tool is connected, compare all
/// three without changing the seed.

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// Invent a fourth recipe after the three controlled comparisons. Change only the local reaction and
/// give the observed pattern a creature name. Keep the same seed and duration so another pupil can
/// reproduce your experiment.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Learn · F34">
/// ### Interface: ask a role to do its job
///
/// An interface names an operation its implementations provide. The caller can use that role without
/// choosing the implementation's formula.
///
/// ```java
/// interface Points {
///     int award(int n);
/// }
/// record DoublePoints() implements Points {
///     public int award(int n) {
///         return n * 2;
///     }
/// }
/// ```
///
/// With `Points rule = new DoublePoints();`, `rule.award(3)` returns 6. `implements Points` promises
/// the operation; the implementation must supply its public method. The variable rule refers to the
/// selected object, so call its operation with the current input rather than repeating a fixed
/// formula in the caller. Another implementation can supply another rule through the same interface.
///
/// Predict: what does this implementation return for `rule.award(5)`, and which code would change to
/// award three times the input?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Investigator notebook · worked example">
/// ### Change one cause; restore what you borrowed
///
/// An interface field selects a calculation. Changing the selected implementation is different from
/// rewriting the caller or changing the initial data. Controlled comparison needs equal inputs and a
/// clear result order.
///
/// ```java
/// var before = rule;
/// try {
///     rule = alternative;
///     runExperiment();
/// } finally {
///     rule = before;
/// }
/// ```
///
/// This independent pattern restores rule whether runExperiment finishes or throws. Predict which
/// rule a later call uses if the experiment throws before returning. finally is a cleanup promise,
/// not an error-hiding catch. Capture each finished image before beginning the next run. Your
/// conclusions should name observed shapes rather than promise that every setting makes a particular
/// animal skin.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Trace the displayed picture back to the selected calculation, and find where each new render
/// resets its input grids.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// compareSkins currently repeats render without changing chemistry. The Chemistry interface supplies
/// just the reaction, while render resets the seed and advances diffusion.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Save the original Chemistry, install each of the three recipe lambdas and render equal inputs,
/// then restore the original in finally. Do not change step or its buffers.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program to explore">
Chemistry makeChemistry() {
    return (a, b) -> {
        double activated = a * b;
        return activated * b;
    };
}

Chemistry chemistry = makeChemistry();

double reactionAt(int x, int y) {
    return chemistry.reaction(a[y][x], b[y][x]);
}

final int SIZE = 64;
double[][] a;
double[][] b;

/// Restore the starting game state.
void reset(long seed) {
    var random = new Random(seed);
    a = new double[SIZE][SIZE];
    b = new double[SIZE][SIZE];
    for (int y = 0; y < SIZE; y++) {
        for (int x = 0; x < SIZE; x++) {
            a[y][x] = 1;
            if (x > 23 && x < 40 && y > 23 && y < 40) {
                a[y][x] = 0.5;
                b[y][x] = 0.8 + random.nextDouble() * 0.2;
            }
        }
    }
}

double laplace(double[][] field, int x, int y) {
    double sum = -field[y][x];
    for (int dy = -1; dy <= 1; dy++) {
        for (int dx = -1; dx <= 1; dx++) {
            if (dx == 0 && dy == 0) {
                continue;
            }
            double weight = dx == 0 || dy == 0 ? 0.2 : 0.05;
            sum += field[(y + dy + SIZE) % SIZE][(x + dx + SIZE) % SIZE] * weight;
        }
    }
    return sum;
}

/// Supplied bounded update: one call means one model step; the active repair is described above.
void step() {
    double[][] nextA = new double[SIZE][SIZE];
    double[][] nextB = new double[SIZE][SIZE];
    for (int y = 0; y < SIZE; y++) {
        for (int x = 0; x < SIZE; x++) {
            double reaction = reactionAt(x, y);
            nextA[y][x] = Math.clamp(a[y][x] + laplace(a, x, y) - reaction + 0.055 * (1 - a[y][x]), 0, 1);
            nextB[y][x] = Math.clamp(b[y][x] + 0.5 * laplace(b, x, y) + reaction - 0.117 * b[y][x], 0, 1);
        }
    }
    a = nextA;
    b = nextB;
}

/// Supplied image creation: generation and drawing remain visible in this file.
BufferedImage render(long seed, double option) {
    int count = (int)option;
    if (!Double.isFinite(option) || count < 1 || count > 3000) {
        throw new IllegalArgumentException("Steps: 1..3000.");
    }
/// Restore the starting game state.
    reset(seed);
    for (int i = 0; i < count; i++) {
/// Supplied bounded update: one call means one model step; the active repair is described above.
        step();
    }
    var image = new BufferedImage(SIZE * 6, SIZE * 6, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) {
            float value = (float)Math.clamp((a[y / 6][x / 6] - b[y / 6][x / 6]), 0, 1);
            image.setRGB(x, y, Color.HSBtoRGB(0.6F + value * 0.3F, 0.65F, value));
        }
    }
    return image;
}


interface Chemistry { double reaction(double a, double b); }


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


BufferedImage[] compareSkins(long seed, int steps) {
    Chemistry before = chemistry;
    try {
        chemistry = makeChemistry();
        BufferedImage standard = render(seed, steps);
        chemistry = (a, b) -> 0.5 * a * b * b;
        BufferedImage half = render(seed, steps);
        chemistry = (a, b) -> 0;
        BufferedImage none = render(seed, steps);
        return new BufferedImage[]{standard, half, none};
    } finally {
        chemistry = before;
    }
}

void main() {
    try {
        showImages("Skin Experiments", new String[]{"Standard", "Half reaction", "No reaction"}, compareSkins(42, 1200));
    } catch (Exception failure) {
        IO.println("Cannot run the experiment: " + failure.getMessage());
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
/// - `final` — The named value cannot be assigned another value afterwards. A referenced mutable object
///   can still change.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+=` — Adds to the old value and stores the result back under the same name.
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
/// - `interface` — Describes a role that different implementations can fulfil. The caller can use that
///   role without choosing its internals.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line.
// </editor-fold>
