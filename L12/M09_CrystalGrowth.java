// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L12.09 · CrystalGrowth
///
/// Grow a crystal by letting wandering particles stick to an existing cluster. Allowing diagonal contact
/// should give the growth a different character from edge-only contact.
///
/// **Your mission:** The new diagonal mode behaves exactly like the old four-neighbour mode. Extend the
/// extracted contact rule while preserving the original option and safe boundary checks.
///
/// **Today you learn:** Transfer means reusing a familiar neighbourhood rule in a new option. A named
/// contact operation should explain the difference between four and eight neighbours while leaving the
/// growth algorithm unchanged.
///
/// Run the program, then compare New rule with Original rule from the same seed; after the
/// repair they should show the intended difference. Close the window before replaying.
boolean touching(boolean[][] grid, int x, int y, boolean diagonals) {
    for (int dy = -1; dy <= 1; dy++) {
        for (int dx = -1; dx <= 1; dx++) {
            if (dx == 0 && dy == 0) continue;
            if (dx != 0 && dy != 0) continue;
            int nx = x + dx, ny = y + dy;
            if (ny >= 0 && ny < grid.length && nx >= 0 && nx < grid[ny].length && grid[ny][nx]) return true;
        }
    }
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Create a third contact rule that accepts only diagonals. Keep the original four-neighbour and
/// eight-neighbour options named and separate. Grow all three from the same seed and particle budget,
/// then describe the branches you actually obtain.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Learn · F36">
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

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `dx == 0 && dy == 0` — Recognises the centre cell, which is not its own neighbour.
/// - `dx != 0 && dy != 0` — Recognises a diagonal offset because both axes change.
/// - `boolean diagonals` — The caller’s choice exists here but currently does not affect the rule.
/// - `ny < grid.length && nx >= 0 && nx < grid[ny].length` — Checks row and column bounds before reading
///   the candidate cell.
///
/// ### What the prepared part already does
///
/// grow performs bounded particle walks and asks touching when a particle joins the crystal. main
/// compares the new diagonal rule with the original rule using the same seed.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The old rule rejects diagonal dx/dy pairs.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Reject those pairs only when diagonals is false.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Keep centre exclusion and per-row bounds; explain why the extracted helper owns neighbour rules
/// while grow owns the walk.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
boolean[][] grow(long seed, int particles) {
    if (particles < 1 || particles > 1200) {
        throw new IllegalArgumentException("Particles: 1..1200.");
    }
    boolean[][] stuck = new boolean[161][161];
    stuck[80][80] = true;
    var random = new Random(seed);
    double radius = 2;
    int[] dx = {1, -1, 0, 0};
    int[] dy = {0, 0, 1, -1};
    for (int particle = 0; particle < particles && radius < 65; particle++) {
        double angle = random.nextDouble() * Math.PI * 2;
        double launch = radius + 6;
        int x = 80 + (int)(Math.cos(angle) * launch);
        int y = 80 + (int)(Math.sin(angle) * launch);
        for (int walk = 0; walk < 12000; walk++) {
            if (x < 1 || x > 159 || y < 1 || y > 159 || Math.hypot(x - 80, y - 80) > launch + 15) {
                break;
            }
            if (!stuck[y][x] && touching(stuck, x, y, diagonalGrowth)) {
                stuck[y][x] = true;
                radius = Math.max(radius, Math.hypot(x - 80, y - 80));
                break;
            }
            int direction = random.nextInt(4);
            x += dx[direction];
            y += dy[direction];
        }
    }
    return stuck;
}

/// Supplied image creation: generation and drawing remain visible in this file.
BufferedImage render(long seed, double option) {
    if (!Double.isFinite(option)) {
        throw new IllegalArgumentException("Finite particle count required.");
    }
    var stuck = grow(seed, (int)option);
    var image = new BufferedImage(483, 483, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        for (int y = 0; y < 161; y++) {
            for (int x = 0; x < 161; x++) {
                if (stuck[y][x]) {
                    g.setColor(Color.getHSBColor((float)Math.hypot(x - 80, y - 80) / 90, 0.6F, 1));
                    g.fillRect(x * 3, y * 3, 3, 3);
                }
            }
        }
    } finally {
        g.dispose();
    }
    return image;
}


boolean diagonalGrowth = true;

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
    diagonalGrowth = true;
    var changed = render(42, 600);
    diagonalGrowth = false;
    var original = render(42, 600);
    diagonalGrowth = true;
    showImages("Crystal Growth", new String[]{"New rule", "Original rule"}, new java.awt.image.BufferedImage[]{changed, original});
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
/// - `!=` — Checks that two values differ.
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
