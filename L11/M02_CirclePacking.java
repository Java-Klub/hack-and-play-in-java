// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
// </editor-fold>

/// # L11.02 · CirclePacking
///
/// Let circles squeeze into the gaps between earlier circles. A growing cluster should use fresh centres
/// rather than repeatedly trying the same location.
///
/// **Your mission:** Fresh centres are rejected because the method asks whether they were already seen.
/// Repair the claim operation so only the first claim succeeds.
///
/// **Today you learn:** Set membership and insertion are different operations. The boolean result of an
/// insertion can report whether a centre was new while recording it for future attempts.
///
/// From the course folder containing README.md:
/// - Play: `java L11/M02_CirclePacking.java`
/// - Check this mission: `java L11/Check.java M02_CirclePacking.java`
///
/// After the play command, inspect the Java picture; close its window, edit the active rule, Save
/// and the check command, then run the play command again to compare. After your repair: save the
/// file, run the check command above, then play again.
///
/// Need an explanation? Recall · F23 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
boolean claimCentre(Set<Centre> seen, int x, int y) {
    return seen.contains(new Centre(x, y));
}

// <editor-fold defaultstate="collapsed" desc="Recall · F23">
/// ### Set: membership without duplicates
///
/// A `Set` answers whether a value belongs to a collection. It does not reserve an indexed place for
/// each insertion.
///
/// ```java
/// Set<String> stickers = new HashSet<>();
/// IO.println(stickers.add("moon"));
/// IO.println(stickers.add("moon"));
/// IO.println(stickers.size());
/// ```
///
/// The output is `true`, `false`, then `1`. The first add changes membership; the second finds an
/// equal value already present. `contains(value)` asks without changing the set. `remove(value)`
/// removes that value and reports whether it was present. A record's generated equality compares its
/// component values, so two separately created equal records represent the same set member. Do not
/// depend on HashSet iteration order.
///
/// Predict: after removing moon and adding sun, what are the size and the result of
/// `contains("moon")`?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `Set<Centre>` — Stores distinct centre values; equal coordinates represent the same centre.
/// - `seen.contains` — Checks membership only; it does not record a newly discovered centre.
/// - `new Centre(x, y)` — Creates a value record whose equality depends on x and y.
///
/// ### What the prepared part already does
///
/// pack proposes centres and asks claimCentre whether each is new, then grows circles until
/// contact. render displays the bounded packing; there is no file export or extra image mode.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// contains only asks; it does not remember a new centre.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// A record with the same x/y is equal to the earlier value.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Set.add both stores the value and reports whether it was new.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
record Bubble(double x, double y, double radius) {}

List<Bubble> pack(long seed, int count) {
    if (count < 1 || count > 200) {
        throw new IllegalArgumentException("Bubbles: 1..200.");
    }
    var random = new Random(seed);
    var bubbles = new ArrayList<Bubble>();
    Set<Centre> seen = new HashSet<>();
    for (int attempt = 0; attempt < 10000 && bubbles.size() < count; attempt++) {
        var candidate = new Bubble(2 + random.nextInt(396), 2 + random.nextInt(396), 1);
        if (!claimCentre(seen, (int)candidate.x(), (int)candidate.y())) continue;
        boolean free = true;
        for (var other : bubbles) {
            free &= Math.hypot(candidate.x - other.x, candidate.y - other.y) >= 3;
        }
        if (free) {
            bubbles.add(candidate);
        }
    }
    boolean[] stopped = new boolean[bubbles.size()];
    for (int step = 0; step < 400; step++) {
        for (int i = 0; i < bubbles.size(); i++) {
            if (stopped[i]) {
                continue;
            }
            var b = bubbles.get(i);
            double radius = b.radius + 0.5;
            boolean free = b.x >= radius && b.y >= radius && b.x + radius < 400 && b.y + radius < 400;
            for (int j = 0; j < bubbles.size() && free; j++) {
                var other = bubbles.get(j);
                if (i != j && Math.hypot(b.x - other.x, b.y - other.y) < radius + other.radius) {
                    free = false;
                }
            }
            if (free) {
                bubbles.set(i, new Bubble(b.x, b.y, radius));
            } else {
                stopped[i] = true;
            }
        }
    }
    return bubbles;
}

/// Supplied image creation: generation and drawing remain visible in this file.
BufferedImage render(long seed, double option) {
    if (!Double.isFinite(option)) {
        throw new IllegalArgumentException("Finite bubble count required.");
    }
    var bubbles = pack(seed, (int)option);
    var image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        for (int i = 0; i < bubbles.size(); i++) {
            var b = bubbles.get(i);
            g.setColor(Color.getHSBColor(i * 0.618F, 0.6F, 0.9F));
            g.fillOval((int)(b.x - b.radius), (int)(b.y - b.radius), (int)(b.radius * 2), (int)(b.radius * 2));
        }
    } finally {
        g.dispose();
    }
    return image;
}


record Centre(int x, int y) {}

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
        showImages("Circle Packing", new String[]{"Result"}, new java.awt.image.BufferedImage[]{render(42, 80)});
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
/// - `boolean` — A value that is either true or false.
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `!=` — Checks that two values differ.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
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
/// - `record` — A small named bundle of values. Its component accessors use the component names with ().
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `Set` — Stores distinct values; the same value is present at most once.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line.
// </editor-fold>
