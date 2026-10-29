// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
// </editor-fold>

/// # L09.04 · VoronoiStainedGlass
///
/// Scatter coloured seeds and let every point of the picture join its nearest one. Different seeds turn
/// the same space into new stained-glass windows.
///
/// **Your mission:** Generating the first seed fails because the chosen collection cannot grow. Provide a
/// list suitable for building the requested seed collection.
///
/// **Today you learn:** List describes ordered access, but not every List permits mutation. This method
/// builds its result incrementally, so its chosen implementation must accept add.
///
/// The program tries to build the picture. If seedList refuses to add a seed, read terminal
/// output. The repaired program opens the coloured picture.
List<Seed> seedList(Random random, int count) {
    if (count < 0 || count > 100) throw new IllegalArgumentException("Seeds: 0..100.");
    List<Seed> seeds = new ArrayList<>();
    for (int i = 0; i < count; i++) {
        seeds.add(new Seed(random.nextInt(400), random.nextInt(300), Color.HSBtoRGB(random.nextFloat(), 0.65F, 1)));
    }
    return seeds;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F22">
/// ### List: a sequence you can change
///
/// A `List<String>` holds text in order. `new ArrayList<>()` makes a mutable list: its contents can
/// change without replacing the list itself.
///
/// ```java
/// List<String> snacks = new ArrayList<>();
/// snacks.add("apple");
/// snacks.add("nuts");
/// IO.println(snacks.get(0));
/// IO.println(snacks.size());
/// ```
///
/// The output is `apple`, then `2`. `add(value)` appends one item; `get(index)` reads without
/// removing it. Indexes start at zero. `isEmpty()` checks for no items. `removeLast()` removes and
/// returns the final item; call it only on a nonempty list. A list made directly by `List.of(...)`
/// cannot be changed, but `new ArrayList<>(List.of(...))` can.
///
/// Predict: after `snacks.removeLast()`, what are its size and first item?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `List<Seed>` — The element type promises that each stored value is a Seed.
/// - `List.of()` — An empty immutable list: empty describes its size, immutable its inability to change.
/// - `seeds.add` — Adds one generated value to the collection being built.
/// - `random.nextInt(400)` — Chooses a legal x coordinate from zero through 399.
///
/// ### What the prepared part already does
///
/// render asks seedList for the centres and assigns each picture point to its nearest centre;
/// showImages presents the memory image in Java.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// List.of makes a fixed unmodifiable result.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Choose a mutable implementation while retaining the List<Seed> role.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Append inside the counted loop; an empty loop must return an empty mutable list.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
record Seed(int x, int y, int colour) {}

int nearest(List<Seed> seeds, int x, int y) {
    int winner = 0;
    long distance = Long.MAX_VALUE;
    for (int i = 0; i < seeds.size(); i++) {
        long dx = x - seeds.get(i).x;
        long dy = y - seeds.get(i).y;
        long candidate = dx * dx + dy * dy;
        if (candidate < distance) {
            distance = candidate;
            winner = i;
        }
    }
    return winner;
}

/// Supplied image creation: generation and drawing remain visible in this file.
BufferedImage render(long seed, double option) {
    int count = (int)option;
    if (!Double.isFinite(option) || count < 2 || count > 100) {
        throw new IllegalArgumentException("Seeds: 2..100.");
    }
    var random = new Random(seed);
    var seeds = seedList(random, count);
    var image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
    int[][] cells = new int[300][400];
    for (int y = 0; y < 300; y++) {
        for (int x = 0; x < 400; x++) {
            cells[y][x] = nearest(seeds, x, y);
        }
    }
    for (int y = 0; y < 300; y++) {
        for (int x = 0; x < 400; x++) {
            boolean border = x > 0 && cells[y][x] != cells[y][x - 1] || y > 0 && cells[y][x] != cells[y - 1][x];
            image.setRGB(x, y, border ? 0 : seeds.get(cells[y][x]).colour);
        }
    }
    return image;
}


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
        showImages("Voronoi Stained Glass", new String[]{"Result"}, new java.awt.image.BufferedImage[]{render(42, 20)});
    } catch (Exception failure) {
        IO.println("Cannot build the picture. Check seedList; Save the source, run its check command, then play again.");
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
/// - `record` — A small named bundle of values. Its component accessors use the component names with ().
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line.
// </editor-fold>
