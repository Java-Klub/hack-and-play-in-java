// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import module java.base;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
// </editor-fold>

class M07_TwoNoisyPictures {
    /// # L11.07 · TwoNoisyPictures
    ///
    /// Split a secret into two noisy black-and-white pictures. Each share should look unhelpful on its own,
    /// while their overlay reveals the hidden shape.
    ///
    /// **Your mission:** Every small block reuses the same shuffle because the generator is restarted inside
    /// the pixel loop. Restore a varied, reproducible sequence across the whole image.
    ///
    /// **Today you learn:** A seeded Random is a continuing source of choices. Recreating it with the same
    /// seed repeats its first choice; reusing one instance lets later blocks receive later choices.
    ///
    /// From the course folder containing README.md:
    /// - Play: `java L11/M07_TwoNoisyPictures.java`
    /// - Check this mission: `java L11/Check.java M07_TwoNoisyPictures.java`
    ///
    /// After the play command, type a seed or press Enter for 42; inspect the Java picture, then close
    /// it and repeat the same seed or try 43 to compare. After your repair: save the file, run the
    /// check command above, then play again.
    ///
    /// Need an explanation? Recall · F27 explains the Java skill; Read this code explains these lines.
    /// Hints 1 and 2 open separately; Repair outline reveals the direction.
    BufferedImage[] shares(BufferedImage secret, long seed) {
        var images = new BufferedImage[3];
        for (int i = 0; i < 3; i++) {
            images[i] = new BufferedImage(secret.getWidth() * 2, secret.getHeight() * 2, BufferedImage.TYPE_INT_RGB);
        }
        var random = new Random(seed);
        for (int y = 0; y < secret.getHeight(); y++) {
            for (int x = 0; x < secret.getWidth(); x++) {
                var order = new ArrayList<>(List.of(0, 1, 2, 3));
                Collections.shuffle(order, random);
                boolean ink = (secret.getRGB(x, y) & 255) > 127;
                for (int i = 0; i < 4; i++) {
                    int dx = order.get(i) % 2;
                    int dy = order.get(i) / 2;
                    boolean first = i < 2;
                    boolean second = ink ? !first : first;
                    images[0].setRGB(x * 2 + dx, y * 2 + dy, first ? 0 : 0xFFFFFF);
                    images[1].setRGB(x * 2 + dx, y * 2 + dy, second ? 0 : 0xFFFFFF);
                    images[2].setRGB(x * 2 + dx, y * 2 + dy, first || second ? 0 : 0xFFFFFF);
                }
            }
        }
        return images;
    }

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
    /// - `var random = new Random(seed)` — Creates one generator for the whole operation, ready to keep its
    ///   sequence position.
    /// - `Collections.shuffle(order, new Random(seed))` — Currently restarts randomness for every block
    ///   instead of continuing the existing generator.
    /// - `x * 2 + dx` — Places one subpixel within the two-by-two block for this source pixel.
    /// - `first || second` — The overlay is black when either share marks that subpixel black.
    ///
    /// ### What the prepared part already does
    ///
    /// shares creates two noisy pictures from the message mask. render combines them; main shows the
    /// overlay and both shares together, so the message can be compared with its ingredients.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
    /// new Random(seed) restarts at the beginning of the sequence.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
    /// The method already creates one Random before the pixel loops.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
    /// Pass that existing object to every Collections.shuffle call.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
    String message = "JAVA";

    BufferedImage mask() {
        if (message.isBlank() || message.length() > 7) {
            throw new IllegalArgumentException("Message: 1..7 characters.");
        }
        var image = new BufferedImage(180, 60, BufferedImage.TYPE_INT_RGB);
        var g = image.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 38));
            g.drawString(message, 5, 45);
        } finally {
            g.dispose();
        }
        return image;
    }

    /// Supplied image creation: generation and drawing remain visible in this file.
    BufferedImage render(long seed, double option) {
        return shares(mask(), seed)[2];
    }


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
        String input = IO.readln("Seed (blank 42, q quits): ");
        if (input == null || input.strip().equalsIgnoreCase("q")) return;
        try {
            long seed = input.isBlank() ? 42 : Long.parseLong(input.strip());
            var pair = shares(mask(), seed);
            showImages("Two Noisy Pictures", new String[]{"Overlay", "Share 1", "Share 2"},
                    new java.awt.image.BufferedImage[]{render(seed, 1), pair[0], pair[1]});
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
    /// - `boolean` — A value that is either true or false.
    /// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
    /// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
    /// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
    /// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
    ///   type.
    /// - `=` — Stores the value on the right under the name on the left. It does not compare them.
    /// - `==` — Compares primitive values. For text contents, use equals instead.
    /// - `||` — At least one condition must be true; if the first is true, Java skips the second.
    /// - `!` — In front of a boolean expression, reverses true and false.
    /// - `++` — Increases a numeric value by one.
    /// - `%` — The remainder after division; useful for repeated positions or turns.
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
    /// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
    /// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
    /// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
    // </editor-fold>

}
