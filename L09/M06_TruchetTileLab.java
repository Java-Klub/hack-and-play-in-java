// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L09.06 · TruchetTileLab
///
/// Steer a winding tile pattern while it grows. Keep one orientation from running too far ahead, so a
/// Map becomes a drawing rule rather than only a score caption.
///
/// **Your mission:** Build chooseOrientation(preferred). Accept only LEFT or RIGHT. Usually return the
/// preferred orientation, but if its current count already leads the other by at least two, choose the
/// other. Increment only the chosen count and return that choice. Invalid text must throw without
/// changing the Map. Your decision steers the real arcs as the pattern grows.
///
/// **Today you learn:** Use stored key/value relationships to make a real picture or generation
/// decision, not just a caption.
///
/// Run the program and inspect the curved pattern and LEFT/RIGHT counts. After the repair, both counts
/// account for the tiles and the steering policy changes the actual curves.
String chooseOrientation(String preferred) {
    if (!preferred.equals("LEFT") && !preferred.equals("RIGHT")) throw new IllegalArgumentException("Use LEFT or RIGHT.");
    String other = preferred.equals("LEFT") ? "RIGHT" : "LEFT";
    int count = orientationCounts.getOrDefault(preferred, 0);
    int otherCount = orientationCounts.getOrDefault(other, 0);
    String chosen = count - otherCount >= 2 ? other : preferred;
    orientationCounts.put(chosen, orientationCounts.getOrDefault(chosen, 0) + 1);
    return chosen;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F24">
/// ### A Map remembers a value for each key
///
/// A List remembers positions. A Map remembers an association: each key leads to one current value.
/// Here a colour name leads to a count.
///
/// ```java
/// Map<String, Integer> marbles = new HashMap<>();
/// marbles.put("blue", 2);
/// int before = marbles.getOrDefault("blue", 0);
/// marbles.put("blue", before + 1);
/// IO.println(marbles.getOrDefault("gold", 0));
/// ```
///
/// Blue now has count 3; the printed gold count is 0. `getOrDefault(key, fallback)` reads the fallback
/// for an absent key without inserting it. `put` replaces that key's old value: it does not add
/// automatically. Other keys remain unchanged.
///
/// Use `containsKey` when absence itself matters. Use `entrySet()` to visit key/value pairs: each entry
/// provides `getKey()` and `getValue()`. A Map does not promise that the first entry is the largest;
/// compare values explicitly when choosing a winner. Predict blue after another identical update. Then
/// predict what `getOrDefault("gold", 9)` returns and whether gold is now stored.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read this part
///
/// Absent keys start with count 0 via getOrDefault. The lead is preferredCount - otherCount before
/// adding this tile. Two LEFT choices from an empty Map are allowed; the third LEFT request must become
/// RIGHT. The renderer draws the returned choice and resets the Map for a new picture. Never increment
/// a requested orientation that you did not choose.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Read both counts before changing either.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Name the other orientation and decide whether the requested side is already two ahead.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Reject unknown names, choose using the old difference, increment the chosen key with getOrDefault,
/// and return it.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Change the permitted lead to four, or deliberately favour LEFT. Compare the resulting loops using
/// the same random seed, so only your steering rule changes.
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
                String orientation = chooseOrientation(random.nextBoolean() ? "LEFT" : "RIGHT");
                if (orientation.equals("LEFT")) {
                    g.drawArc(x - size / 2, y - size / 2, size, size, 270, 90);
                    g.drawArc(x + size / 2, y + size / 2, size, size, 90, 90);
                } else {
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
