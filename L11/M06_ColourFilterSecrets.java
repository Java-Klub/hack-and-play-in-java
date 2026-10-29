// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L11.06 · ColourFilterSecrets
///
/// Hide a clear message inside a noisy colour picture, then reveal it through a red-channel view.
/// Changing the seed should change the camouflage while leaving the secret readable.
///
/// **Your mission:** The noise stays identical for every requested seed. Repair the random source while
/// preserving the channel that carries the hidden mask.
///
/// **Today you learn:** One seeded Random should supply the noise across the whole image. Its
/// repeatability is useful for comparison, while a different seed should change the distracting channels.
///
/// Run the program, then type a seed or press Enter for 42; inspect the Java picture, then
/// close it and repeat the same seed or try 43 to compare.
BufferedImage hide(BufferedImage mask, long seed) {
    var random = new Random(seed);
    var image = new BufferedImage(mask.getWidth(), mask.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) {
            int red = (mask.getRGB(x, y) & 255) > 127 ? 240 : 15;
            image.setRGB(x, y, (red << 16) | (random.nextInt(256) << 8) | random.nextInt(256));
        }
    }
    return image;
}

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Hide your own tiny symbol by editing the mask drawing. Then move the signal to the green channel
/// and change the reveal operation to match. Show only the noisy image to a friend before revealing
/// the matching filter.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Recall · F27">
/// ### Seeded randomness: repeat an experiment
///
/// A pseudorandom generator produces a sequence. A seed chooses its starting state; equal seeds and
/// equal calls reproduce equal results.
///
/// ```java
/// var first = new Random(9);
/// var second = new Random(9);
/// int a = first.nextInt(4);
/// int b = second.nextInt(4);
/// IO.println(a == b);
/// ```
///
/// This prints `true`. Each nextInt call advances that generator. `nextInt(4)` chooses an integer
/// from 0 through 3; the upper bound is excluded. Reuse one generator across a generated picture so
/// later choices use later positions in the sequence. Recreating it with the same seed inside a loop
/// repeatedly returns its first choice. Different seeds can still occasionally give equal individual
/// choices.
///
/// Predict: which integers can `nextInt(3)` return, and can it return 3?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `new Random(0)` — Fixes the noise sequence to zero instead of the requested seed.
/// - `red << 16` — Places the secret-bearing red value into its part of the packed RGB integer.
/// - `random.nextInt(256)` — Draws a channel intensity from zero through 255.
/// - `|` — Combines the red, green and blue bit fields into one colour.
///
/// ### What the prepared part already does
///
/// hide uses the chosen seed to build colour noise over the message mask. render puts the hidden
/// view above its red-filter reveal in one Java image.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The mask determines the red channel; randomness changes green/blue.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Do not change the channel encoding or message.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Replace the hard-coded seed with the parameter; reuse that Random in the pixel loops.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String message = "SECRET";

BufferedImage mask(String text) {
    if (text.isBlank() || text.length() > 16) {
        throw new IllegalArgumentException("Message: 1..16 characters.");
    }
    var image = new BufferedImage(600, 120, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 48));
        g.drawString(text, 12, 80);
    } finally {
        g.dispose();
    }
    return image;
}

BufferedImage reveal(BufferedImage image) {
    var result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) {
            int red = (image.getRGB(x, y) >> 16) & 255;
            result.setRGB(x, y, red * 0x010101);
        }
    }
    return result;
}

/// Supplied image creation: generation and drawing remain visible in this file.
BufferedImage render(long seed, double option) {
    var hidden = hide(mask(message), seed);
    var clear = reveal(hidden);
    var image = new BufferedImage(600, 240, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.drawImage(hidden, 0, 0, null);
        g.drawImage(clear, 0, 120, null);
    } finally {
        g.dispose();
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
    String input = IO.readln("Seed (blank 42, q quits): ");
    if (input == null || input.strip().equalsIgnoreCase("q")) return;
    try {
        long seed = input.isBlank() ? 42 : Long.parseLong(input.strip());
        showImages("Colour Filter Secrets", new String[]{"Seed " + seed}, new java.awt.image.BufferedImage[]{render(seed, 1)});
    } catch (IllegalArgumentException failure) {
        IO.println("Use a whole-number seed: " + failure.getMessage());
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
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
