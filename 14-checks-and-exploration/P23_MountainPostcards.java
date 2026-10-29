// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # 14.23 · MountainPostcards
///
/// Generate a mountain silhouette and compare it with its reflected twin. Mirroring should change which
/// peaks stand on the left without inventing a different mountain.
///
/// **Your mission:** Refactor a working picture. render currently reverses a ridge inline; extract
/// that responsibility into reverseRidge, then replace the inline block with a call to your helper.
/// Keep the original unmirrored option, do not mutate the input, and preserve the picture’s
/// behaviour.
///
/// **Today you learn:** Extract an operation from existing working code, give it a contract, and
/// connect the original caller to it. Preserve behaviour while changing responsibility.
///
/// Inspect New rule and Original rule before changing the code. After extracting the helper, rerun
/// the same seed: both views must preserve their earlier appearance. The helper check starts RED even
/// though the inline picture already works.
double[] reverseRidge(double[] heights) {
    // Extract the reversal responsibility from render into this method.
    return heights.clone();
}

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
            if (mirrored) {
                double[] reversed = new double[heights.length];
                for (int i = 0; i < heights.length; i++) {
                    reversed[i] = heights[heights.length - 1 - i];
                }
                heights = reversed;
            }
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

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Design a mountain logo by combining the original ridge and its reversed copy in one image, with
/// different colours. Choose whether they should meet at the centre or overlap. Generation must still
/// receive the original untouched ridge values.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Recall · F36">
/// ### Transfer: keep the old promise while adding a choice
///
/// Extracting a helper gives one responsibility a name. Its inputs and result form a contract you can
/// reuse elsewhere.
///
/// ```java
/// String surround(String text, boolean stars) {
///     String edge = stars ? "*" : "#";
///     return edge + text + edge;
/// }
/// ```
///
/// `surround("hi", false)` returns `"#hi#"`; the old hash style still works. The caller can request a
/// new style without reimplementing the operation. Test both choices, an empty input and a different
/// word. Tests establish results, while explaining who chooses a style and who builds the label
/// establishes the responsibility split. A successful new mode should not silently change the old
/// one.
///
/// Predict: what will `surround("", true)` produce, and which part should change if the surrounding
/// symbol changes?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Read this code">
/// ### Refactoring changes responsibilities while keeping results
///
/// The starting picture already mirrors correctly. Its inline reversal is useful behaviour, not a bug
/// to erase. Give that operation a reusable contract: return a fresh reversed array and leave the
/// input alone. Then let render request the operation by name instead of repeating its
/// implementation. The picture should not suddenly look different after this refactoring.
///
/// Neutral example: [5, 9, 1, 7] becomes [7, 1, 9, 5]. Index i in the result reads index length - 1 -
/// i in the original. Predict the result for [8] and for an empty array. An empty loop has no
/// accesses. clone alone makes an independent copy but preserves order.
///
/// The checks exercise the helper's results and independence, and the real renderer's two options.
/// They do not inspect your source to enforce a particular call. In your self-review, find the sole
/// reversal implementation and show the call from render: keeping two correct copies would pass some
/// behaviour checks but miss the refactoring goal.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Keep the working inline reversal as your reference while building the helper.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// A returned array must be new. Move the reversal loop into reverseRidge and return its result.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Replace the whole mirrored inline block with a call to reverseRidge. Compare both pictures before
/// and after.
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




boolean mirrored = true;

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
    mirrored = true;
    var changed = render(42, 0.65);
    mirrored = false;
    var original = render(42, 0.65);
    mirrored = true;
    showImages("Mountain Postcards", new String[]{"New rule", "Original rule"}, new java.awt.image.BufferedImage[]{changed, original});
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
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
// </editor-fold>
