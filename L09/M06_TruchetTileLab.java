// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L09.06 · TruchetTileLab
///
/// Arrange curved tiles into a winding pattern where small local choices create long loops. Keep a
/// truthful score of the two tile orientations as the picture grows.
///
/// **Your mission:** The orientation caption reports one for a tile kind however often it occurs. Repair
/// the accumulated counts while preserving the generated pattern.
///
/// **Today you learn:** A Map can keep a separate tally for each named category. Updating one orientation
/// must retain its previous count and leave the other category untouched.
///
/// From the course folder containing README.md:
/// - Play: `java L09/M06_TruchetTileLab.java`
/// - Check this mission: `java L09/Check.java M06_TruchetTileLab.java`
///
/// After the play command, inspect the Java picture; close its window, edit the active rule, Save
/// and the check command, then run the play command again to compare. After your repair: save the
/// file, run the check command above, then play again.
///
/// Need an explanation? Recall · F24 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
void countOrientation(String orientation) {
    orientationCounts.put(orientation, orientationCounts.getOrDefault(orientation, 0) + 1);
}

// <editor-fold defaultstate="collapsed" desc="Recall · F24">
/// ### Map: look up a value by its key
///
/// A `Map<String, Integer>` associates text keys with integer values. Each key has at most one stored
/// value.
///
/// ```java
/// Map<String, Integer> badges = new HashMap<>();
/// badges.put("moon", 4);
/// IO.println(badges.getOrDefault("moon", 0));
/// IO.println(badges.getOrDefault("sun", 0));
/// ```
///
/// The output is `4`, then `0`. `getOrDefault(key, fallback)` reads an existing value, or returns the
/// fallback when the key is absent. This lookup does not insert anything. `put(key, value)` stores a
/// value and replaces any previous value for that key; it does not add numbers automatically. Use
/// `containsKey(key)` when you need to distinguish absence explicitly. These examples do not store
/// null values.
///
/// Predict: after `badges.put("moon", 9)`, what will the same two lookups return?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `String orientation` — The category name is the key, not a screen coordinate.
/// - `orientationCounts.put` — Writes the value for one orientation and replaces its previous value.
/// - `orientation, 1` — A fixed one loses all earlier occurrences of that category.
///
/// ### What the prepared part already does
///
/// render chooses tile orientations and calls countOrientation for each tile. Both the curved
/// picture and its LEFT/RIGHT caption use those real counts.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// LEFT and RIGHT are separate keys.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// A missing key means zero previous tiles.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Store previous count plus one, not a constant one.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
BufferedImage render(long seed, double option) {
    int count = (int)option;
    if (!Double.isFinite(option) || count < 2 || count > 30) {
        throw new IllegalArgumentException("Tiles per side: 2..30.");
    }
    orientationCounts.clear();
    int size = 480 / count;
    var image = new BufferedImage(size * count, size * count, BufferedImage.TYPE_INT_RGB);
    var random = new Random(seed);
    var g = image.createGraphics();
    try {
        g.setColor(new Color(245, 230, 205));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setColor(new Color(20, 65, 90));
        g.setStroke(new BasicStroke(Math.max(2, size / 7)));
        for (int row = 0; row < count; row++) {
            for (int col = 0; col < count; col++) {
                int x = col * size;
                int y = row * size;
                if (random.nextBoolean()) {
                    countOrientation("LEFT");
                    g.drawArc(x - size / 2, y - size / 2, size, size, 270, 90);
                    g.drawArc(x + size / 2, y + size / 2, size, size, 90, 90);
                } else {
                    countOrientation("RIGHT");
                    g.drawArc(x + size / 2, y - size / 2, size, size, 180, 90);
                    g.drawArc(x - size / 2, y + size / 2, size, size, 0, 90);
                }
            }
        }
        g.drawString("LEFT " + orientationCounts.getOrDefault("LEFT", 0) + " / RIGHT " + orientationCounts.getOrDefault("RIGHT", 0), 8, 14);
    } finally {
        g.dispose();
    }
    return image;
}


Map<String, Integer> orientationCounts = new HashMap<>();

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
    try {
        showImages("Truchet Tile Lab", new String[]{"Result"}, new java.awt.image.BufferedImage[]{render(42, 12)});
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
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
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
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `else` — The alternative when the preceding if condition is false.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Map` — Associates keys with values. A missing key needs an explicit fallback or decision.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line.
// </editor-fold>
