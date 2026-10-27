// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L12.12 · MountainPostcards
///
/// Generate a mountain silhouette and compare it with its reflected twin. Mirroring should change which
/// peaks stand on the left without inventing a different mountain.
///
/// **Your mission:** The mirror option makes an unchanged copy of the ridge. Build a reversed result
/// while preserving the original height array and the old unmirrored mode.
///
/// **Today you learn:** A pure transformation can produce a new arrangement without mutating its input.
/// Extracting ridge reversal gives the new option one responsibility and keeps generation independent
/// from presentation.
///
/// After Run File, enter n for mountain-old.png, then rerun and press Enter for the mirrored
/// mountain-new.png.
/// After your repair: Save → Test File → play again.
double[] reverseRidge(double[] heights) {
    double[] reversed = new double[heights.length];
    for (int i = 0; i < heights.length; i++) reversed[i] = heights[heights.length - 1 - i];
    return reversed;
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `double[] heights` — An ordered array of ridge heights, from one side of the picture to the other.
/// - `heights.clone()` — Creates an independent array but keeps every value at its original index.
/// - `return` — Supplies the result array to the renderer; it need not be the input object.
///
/// ### What the prepared part already does
///
/// The supplied midpoint generator builds the ridge once from a seed. render chooses the old ridge or
/// reverseRidge’s result and paints it; main asks for mirror mode and saves a separately named PNG for
/// comparison.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. The last source value belongs at result index zero.
/// 2. Map destination i to source length-1-i.
/// 3. Explain why reversing data belongs in a separate value transform rather than inside random ridge
///   generation.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
double[] ridge(Random random, double roughness) {
    double[] heights = new double[257];
    heights[0] = random.nextDouble() * 40;
    heights[256] = random.nextDouble() * 40;
    double amplitude = 90;
    for (int gap = 256; gap > 1; gap /= 2) {
        for (int left = 0; left < 256; left += gap) {
            heights[left + gap / 2] = (heights[left] + heights[left + gap]) / 2 + (random.nextDouble() * 2 - 1) * amplitude;
        }
        amplitude *= roughness;
    }
    return heights;
}

/// Supplied image creation: generation and drawing remain visible in this file.
BufferedImage render(long seed, double roughness) {
    if (!Double.isFinite(roughness) || roughness < 0.2 || roughness > 0.9) {
        throw new IllegalArgumentException("Roughness: 0.2..0.9.");
    }
    var image = new BufferedImage(640, 400, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    var random = new Random(seed);
    try {
        for (int y = 0; y < 400; y++) {
            g.setColor(new Color(255 - y / 4, 120 - y / 6, 120 + y / 4));
            g.drawLine(0, y, 640, y);
        }
        for (int layer = 0; layer < 5; layer++) {
            var heights = ridge(random, roughness);
            if (mirrored) heights = reverseRidge(heights);
            var polygon = new Polygon();
            polygon.addPoint(0, 400);
            for (int x = 0; x < 257; x++) {
                polygon.addPoint(x * 640 / 256, Math.max(20, Math.min(390, 170 + layer * 38 + (int)heights[x])));
            }
            polygon.addPoint(640, 400);
            g.setColor(new Color(100 - layer * 18, 95 - layer * 15, 135 - layer * 20));
            g.fillPolygon(polygon);
        }
    } finally {
        g.dispose();
    }
    return image;
}

/// Supplied export boundary: a local path is used; encoding is handled by the JDK.
void save(Path path, long seed, double option) throws Exception {
    Files.createDirectories(path.toAbsolutePath().getParent());
    if (!ImageIO.write(render(seed, option), "png", path.toFile())) {
        throw new IllegalStateException("PNG writer missing.");
    }
}

boolean mirrored = true;

/// Start the real program with Run File. Test File checks the rule without opening a window.
/// ridge creates seeded heights; reverseRidge owns only the new mirror capability.
/// render composes five layers and calls the transform; save/main keep local PNG export.
void main(String[] args) throws Exception {
    if (args.length == 0) {
        String entered = IO.readln("Mirror ridge? y/n (blank y, q quits): ");
        if (entered == null || entered.strip().equalsIgnoreCase("q")) return;
        mirrored = !entered.strip().equalsIgnoreCase("n");
        args = new String[]{"mountain-" + (mirrored ? "new" : "old") + ".png"};
    }

    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "mountain-postcards.png");
        long seed = args.length > 1 ? Long.parseLong(args[1]) : 42;
        double option = args.length > 2 ? Double.parseDouble(args[2]) : 0.65;
/// Supplied export boundary: a local path is used; encoding is handled by the JDK.
        save(path, seed, option);
        IO.println("Saved: " + path.toAbsolutePath());
    } catch (IllegalArgumentException invalid) {
        IO.println("Invalid options: " + invalid.getMessage());
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
/// - `+=` — Adds to the old value and stores the result back under the same name.
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
/// - `throws` — In a method declaration: tells callers which failure they may need to handle.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
/// - `Path` — A file-system location, not the file’s contents.
/// - `Files` — JDK operations for reading and writing local files.
// </editor-fold>

