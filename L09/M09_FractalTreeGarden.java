// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L09.09 · FractalTreeGarden
///
/// Grow a branching tree by changing one angle. A small turn can transform a narrow twig into a sprawling
/// canopy worth saving as a postcard.
///
/// **Your mission:** Mistyped angle text quietly selects 10 instead of the promised default 28. Repair
/// that fallback while still rejecting numeric values outside the allowed range.
///
/// **Today you learn:** Parsing turns text into a number and can fail before range checking begins. Catch
/// the specific text-conversion failure; an out-of-range number is a different problem.
///
/// From the course folder containing README.md:
/// - Play: `java L09/M09_FractalTreeGarden.java`
/// - Check this mission: `java L09/Check.java M09_FractalTreeGarden.java`
///
/// After the play command, enter an angle 10–70 or try oops; read Chosen value and inspect the Java
/// picture. Close it and run the play command again for another input. After your repair: save the
/// file, run the check command above, then play again.
///
/// Need an explanation? Learn · F31 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
double readAngle(String text) {
    try {
        double value = Double.parseDouble(text.strip());
        if (!Double.isFinite(value) || value < 10 || value > 70) throw new IllegalArgumentException("angle: 10..70.");
        return value;
    } catch (NumberFormatException invalid) {
        return 10;
    }
}

// <editor-fold defaultstate="expanded" desc="Learn · F31">
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
/// - `Double.parseDouble(text.strip())` — Removes outer whitespace, then attempts a decimal-number
///   conversion.
/// - `!Double.isFinite(value)` — Rejects NaN and infinities, which are not usable branching angles.
/// - `catch (NumberFormatException invalid)` — Handles malformed numeric text, rather than every possible
///   programming error.
/// - `return 10;` — The current fallback determines the visible tree after a typing mistake.
///
/// ### What the prepared part already does
///
/// main sends the one typed angle to readAngle, then render draws the bounded branch pattern. A
/// valid value gives a Java picture; malformed text and a numeric range violation are separate
/// cases.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Trace three different paths
///
/// For readAngle, `" 24 "` is stripped, converted and returned as 24. `"oops"` cannot be converted
/// and enters the NumberFormatException catch; inspect its current result and compare it with the
/// advertised default. `"90"` converts successfully but fails the range guard. Its
/// IllegalArgumentException is not this catch's NumberFormatException, so it reaches the caller. NaN
/// and infinity are also numeric special values, rejected by isFinite. Predict which lines run for
/// `"70"` and `"9"`; do not treat every failure as a reason to replace the input with a default.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Strip surrounding whitespace before parsing.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Catch NumberFormatException, not every programming error.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// The fallback is 28; range guards remain separate from that fallback.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
double shortening = 0.7;

int branches(Graphics2D g, double x, double y, double length, double heading, double fork, int depth) {
    double nx = x + Math.cos(heading) * length;
    double ny = y + Math.sin(heading) * length;
    g.drawLine((int)x, (int)y, (int)nx, (int)ny);
    if (depth == 0) {
        return 1;
    }
    return 1
        + branches(g, nx, ny, length * shortening, heading - fork, fork, depth - 1)
        + branches(g, nx, ny, length * shortening, heading + fork, fork, depth - 1);
}

/// Supplied image creation: generation and drawing remain visible in this file.
BufferedImage render(long seed, double angle) {
    if (!Double.isFinite(angle) || angle < 10 || angle > 70 || shortening < 0.4 || shortening > 0.78) {
        throw new IllegalArgumentException("Angle 10..70; optional shortening 0.4..0.78.");
    }
    var image = new BufferedImage(600, 480, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(new Color(15, 24, 32));
        g.fillRect(0, 0, 600, 480);
        g.setColor(Color.getHSBColor(new Random(seed).nextFloat(), 0.6F, 1));
        branches(g, 300, 470, 120, -Math.PI / 2, Math.toRadians(angle), 8);
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
    String input = IO.readln("Angle 10..70 (blank/default 28, q quits): ");
    if (input == null || input.strip().equalsIgnoreCase("q")) return;
    try {
        var option = readAngle(input);
        IO.println("Chosen value: " + option);
        showImages("Fractal Tree Garden", new String[]{"Value " + option}, new java.awt.image.BufferedImage[]{render(42, option)});
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
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
/// - `/` — Divides numbers. Dividing two int values discards the fractional part.
/// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
///   direction. In List<String>, angle brackets describe the element type.
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
// </editor-fold>

