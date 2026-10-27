// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L12.01 · ReactionDiffusionSkins
///
/// Let two virtual chemicals spread and react until spots or stripes emerge. A tiny local recipe can
/// change the whole skin of the creature you imagine wearing it.
///
/// **Your mission:** The reaction is always zero, so the selected chemistry never affects the pattern.
/// Reconnect the replaceable rule to the current cell’s real values.
///
/// **Today you learn:** A small interface can name one replaceable calculation. reactionAt should ask the
/// installed Chemistry role about this cell, so a different implementation changes chemistry without
/// replacing diffusion.
///
/// After Run File, inspect the Java picture; close its window, edit the active rule, Save and Test
/// File, then Run File again to compare.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Learn · F34 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
double reactionAt(int x, int y) {
    return 0;
}

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

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `double` — The returned reaction is a decimal quantity, not a yes/no decision.
/// - `reactionAt(int x, int y)` — The caller identifies which cell’s current chemical values are needed.
/// - `return 0;` — Ignores the cell and installed rule, reporting no reaction everywhere.
///
/// ### What the prepared part already does
///
/// reset creates concentration grids; step asks reactionAt for the local reaction before writing
/// the next grids. render performs one bounded run and shows the resulting skin in Java.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Chemistry names a two-value calculation role.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Read a[y][x] and b[y][x], then pass both to the field’s reaction method.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Do not hard-code one formula; the caller must also work when chemistry is replaced.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
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
Chemistry chemistry = (a, b) -> a * b * b;

/// Start the real program with Run File. Test File checks the rule without opening a window.
/// The supplied part supports only the main game or experiment described above.
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
    try {
        showImages("Reaction Diffusion Skins", new String[]{"Result"}, new java.awt.image.BufferedImage[]{render(42, 1200)});
    } catch (Exception failure) {
        IO.println("Cannot draw this result; check the editable rule: " + failure);
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

