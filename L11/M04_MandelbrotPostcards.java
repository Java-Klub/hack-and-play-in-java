// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L11.04 · MandelbrotPostcards
///
/// Send a postcard from the edge of the Mandelbrot set, where changing scale reveals new detail. Seeds
/// should give the same landscape different, repeatable colour moods.
///
/// **Your mission:** Create palette: build 16 bright colours with one Random created from seed. Each
/// colour uses the next random integer hue from 0 through 359, divided by 360.0, with saturation 0.8
/// and brightness 1. Return the array for the supplied fractal renderer.
///
/// **Today you learn:** Create one seeded random generator and consume a reproducible sequence while
/// constructing a complete palette.
///
/// Run the program, then choose a palette seed and horizontal span, or press Enter for 42 and
/// 3.2. Close the picture before rerunning. The same seed repeats the palette; span changes the view.
int[] palette(long seed) {
    int[] colours = new int[16];
    // Create one seeded generator and fill every colour slot.
    return colours;
}

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

// <editor-fold defaultstate="expanded" desc="Read this code">
/// ### Reproducibility is a sequence, not one nearly equal draw
///
/// Create one generator before filling the array. Each nextInt(360) advances it and returns 0..359.
/// Recreating Random(seed) inside the loop would repeat its first hue in every slot. A neutral
/// example: `var r = new Random(7); int a = r.nextInt(6); int b = r.nextInt(6);` makes two draws;
/// another generator seeded 7 repeats the pair. It need not make a and b different. Predict what
/// happens when only the second generator is seeded 8.
///
/// HSBtoRGB converts hue, saturation and brightness into the packed colour expected by setRGB. The
/// supplied renderer selects a palette slot from a point's escape count and keeps interior points
/// black. The seed colours the landscape; it does not move it. span is the visible horizontal width
/// and centreX/centreY locate its centre. Those are the controls for investigating a smaller region.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Make the Random once, then traverse all 16 slots.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Use nextInt(360); divide by a fractional 360 before converting HSB to RGB.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Store one HSBtoRGB result per slot and return the completed array. The renderer already calls
/// palette.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
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


void main() {
    try {
        String seedText = IO.readln("Palette seed [42], /q to quit: ");
        if (seedText == null || seedText.strip().equalsIgnoreCase("/q")) return;
        String spanText = IO.readln("Horizontal span [3.2], smaller means a closer view, /q to quit: ");
        if (spanText == null || spanText.strip().equalsIgnoreCase("/q")) return;
        long seed = seedText.isBlank() ? 42 : Long.parseLong(seedText.strip());
        double span = spanText.isBlank() ? 3.2 : Double.parseDouble(spanText.strip());
        var image = render(seed, span);
        IO.println("Centre: " + centerX + ", " + centerY + "; span: " + span + "; palette seed: " + seed);
        showImages("Mandelbrot Postcards", new String[]{"Seed " + seed + ", span " + span}, new BufferedImage[]{image});
    } catch (Exception failure) {
        IO.println("Cannot draw this view: " + failure.getMessage());
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
