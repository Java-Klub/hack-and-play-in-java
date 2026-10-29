// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L11.04 · MandelbrotPostcards
///
/// A postcard arrived without its colour recipe. Its landscape is easy to redraw; the little colour
/// strip is your clue to the missing seed.
///
/// **Your mission:** A mystery palette came from one seed between 0 and 255. Build a finder that
/// returns every seed whose palette begins with the supplied colour sample, in increasing order. An
/// impossible sample returns no candidates. Keep the normal postcard generator working; use your tool
/// to choose a seed for the picture rather than guessing it.
///
/// **Investigation:** Solve a deterministic mystery by building a bounded search tool.
///
/// Read the mystery sample and candidate report, then choose a palette seed and span. The picture
/// shows the reference colour strip. Enter keeps the normal defaults; /q quits.

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Curate a two-card exhibition: choose a palette seed and a span for the whole set, then choose
/// another span and centre near an edge. Record all four numbers, not just the seed. A palette
/// changes colour; span and centre change which landscape is visible.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Learn · F27">
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

// <editor-fold defaultstate="expanded" desc="Investigator notebook · worked example">
/// ### Exhaust a small space; do not confuse a clue with an answer
///
/// Seeded generation is repeatable when calls occur in the same order. A search can regenerate
/// candidates and compare their observable results, without reversing Random's internal mathematics.
///
/// ```java
/// var matches = new ArrayList<Integer>();
/// for (int n = 0; n <= 20; n++) {
///     if (n * n % 10 == 6) matches.add(n);
/// }
/// ```
///
/// This neutral search finds 4, 6, 14 and 16. Predict the result if the upper bound becomes 10.
/// Return all matches: a short clue may identify several possibilities. A colour sample is a prefix,
/// not necessarily a whole palette. Compare its entries to the beginning of each generated array and
/// stop comparing that candidate at the first mismatch. Equal individual colours do not prove equal
/// palettes; the sequence is the evidence.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Find the operation that turns a seed into the complete colour sequence. Check where the mystery
/// sample reaches the report.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The unfinished matchingSeeds tool sits beside main. It can reuse palette without changing escape
/// or rendering.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Try seeds 0..255, compare each supplied sample entry to that palette prefix, and collect every
/// matching long seed. Keep the supplied sample-length guard.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program to explore">
int[] palette(long seed) {
    var random = new Random(seed);
    int[] colours = new int[16];
    for (int i = 0; i < colours.length; i++) {
        float hue = random.nextInt(360) / 360.0F;
        colours[i] = Color.HSBtoRGB(hue, 0.8F, 1);
    }
    return colours;
}

BufferedImage render(long seed, double span) {
    if (!Double.isFinite(span) || span <= 1.0E-5 || span > 6 || !Double.isFinite(centerX + centerY)) {
        throw new IllegalArgumentException("Span must be 0.00001..6.");
    }
    var image = new BufferedImage(480, 360, BufferedImage.TYPE_INT_RGB);
    int[] colours = palette(seed);
    for (int y = 0; y < 360; y++) {
        for (int x = 0; x < 480; x++) {
            int n = escape(centerX + (x / 479.0 - 0.5) * span, centerY + (y / 359.0 - 0.5) * span * 0.75);
            image.setRGB(x, y, n == 160 ? 0 : colours[n % colours.length]);
        }
    }
    return image;
}

int escape(double x, double y) {
    double a = 0;
    double b = 0;
    int steps = 0;
    while (a * a + b * b <= 4 && steps < 160) {
        double next = a * a - b * b + x;
        b = 2 * a * b + y;
        a = next;
        steps++;
    }
    return steps;
}
double centerX = -0.6;
double centerY = 0;


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


java.util.List<Long> matchingSeeds(int[] sample) {
    if (sample.length < 1 || sample.length > 16) {
        throw new IllegalArgumentException("Supply 1..16 palette colours.");
    }
    return java.util.List.of();
}

BufferedImage paletteStrip(int[] colours) {
    var image = new BufferedImage(colours.length * 30, 40, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) image.setRGB(x, y, colours[x / 30]);
    }
    return image;
}

void main() {
    int[] mystery = {-13420801, -13369474, -52415, -13369522, -13369416, -27085, -13369386, -6684877, -5112013, -52256, -13369355, -35789, -52405, -6684877, -13369372, -13409281};
    try {
        IO.println("Mystery palette: " + java.util.Arrays.toString(mystery));
        IO.println("Matching seeds 0..255: " + matchingSeeds(mystery));
        String seedText = IO.readln("Palette seed [42], /q to quit: ");
        if (seedText == null || seedText.strip().equalsIgnoreCase("/q")) return;
        String spanText = IO.readln("Horizontal span [3.2], /q to quit: ");
        if (spanText == null || spanText.strip().equalsIgnoreCase("/q")) return;
        long seed = seedText.isBlank() ? 42 : Long.parseLong(seedText.strip());
        double size = spanText.isBlank() ? 3.2 : Double.parseDouble(spanText.strip());
        showImages("Mandelbrot Mystery", new String[]{"Mystery palette", "Chosen seed " + seed},
                   new BufferedImage[]{paletteStrip(mystery), render(seed, size)});
    } catch (Exception failure) {
        IO.println("Cannot draw this experiment: " + failure.getMessage());
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
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
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
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
