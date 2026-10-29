// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L11.09 · PixelSortingGlitch
///
/// Pull streaks out of a picture by sorting selected runs of pixels. The threshold changes where the
/// glitch catches and where the original image survives.
///
/// **Your mission:** The transformed picture is saved as lost-pixels.png instead of the chosen filename.
/// Repair its export destination without changing the input image.
///
/// **Today you learn:** A Path is part of the export contract, including its filename. The transformation
/// and the destination are separate choices; saving must honour both rather than invent a neighbouring
/// file.
///
/// From the course folder containing README.md:
/// - Play: `java L11/M09_PixelSortingGlitch.java`
/// - Check this mission: `java L11/Check.java M09_PixelSortingGlitch.java`
///
/// the play command writes exports/pixel-sorted.png through saveSorted and loads that exact file
/// into a Java window. If the requested file is missing, read terminal output and repair its
/// destination. After your repair: save the file, run the check command above, then play again.
///
/// Need an explanation? Recall · F32 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
void saveSorted(Path path, BufferedImage input, int threshold) throws IOException {
    Files.createDirectories(path.toAbsolutePath().getParent());
    if (!ImageIO.write(sort(input, threshold), "png", path.toFile())) {
        throw new IOException("PNG writer missing.");
    }
}

// <editor-fold defaultstate="collapsed" desc="Recall · F32">
/// ### Files: a destination and a resource lifetime
///
/// A `Path` describes a location; constructing it does not create a file. Inside a method that
/// declares `throws IOException`, this writes a note:
///
/// ```java
/// Path file = Path.of("exports", "note.txt");
/// Files.createDirectories(file.getParent());
/// try (var writer = Files.newBufferedWriter(file)) {
///     writer.write("Hi");
/// }
/// ```
///
/// The result is exports/note.txt containing `Hi`. `getParent()` selects exports; `getFileName()`
/// selects only note.txt and loses the directory. Try-with-resources closes writer when the block
/// ends, including when writing fails. `throws` lets the caller handle an I/O failure; it does not
/// handle it here. Image and audio encoders likewise need the actual destination, not an unrelated
/// memory buffer.
///
/// Predict: if file becomes `Path.of("cards", "one.txt")`, where is the note written?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `Files.createDirectories` — Prepares destination directories and also works when they already exist.
/// - `sort(input, threshold)` — Produces the transformed image from the supplied input and threshold.
/// - `path.resolveSibling("lost-pixels.png")` — Chooses another name in the same parent directory,
///   ignoring the requested filename.
/// - `!ImageIO.write` — A false writer result becomes an explicit export failure.
///
/// ### What the prepared part already does
///
/// main calls saveSorted with one fixed nested output path, then loads that exact PNG. The Java
/// picture appears only when the requested file can actually be decoded.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// resolveSibling changes the requested filename.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// sort already creates a separate output image.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Use the original Path for the encoder and keep the parent-directory creation.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
BufferedImage demo() {
    var image = new BufferedImage(240, 160, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < 160; y++) {
        for (int x = 0; x < 240; x++) {
            image.setRGB(x, y, Color.HSBtoRGB(x / 240.0F, 0.7F, 0.2F + y / 200.0F));
        }
    }
    return image;
}

int light(int rgb) {
    return (((rgb >> 16) & 255) + ((rgb >> 8) & 255) + (rgb & 255)) / 3;
}

BufferedImage sort(BufferedImage input, int threshold) {
    if (threshold < 0 || threshold > 255) {
        throw new IllegalArgumentException("Threshold: 0..255.");
    }
    var output = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
    for (int y = 0; y < input.getHeight(); y++) {
        int[] row = input.getRGB(0, y, input.getWidth(), 1, null, 0, input.getWidth());
        int start = 0;
        while (start < row.length) {
            if (light(row[start]) < threshold) {
                start++;
                continue;
            }
            int end = start;
            while (end < row.length && light(row[end]) >= threshold) {
                end++;
            }
            Integer[] streak = new Integer[end - start];
            for (int i = 0; i < streak.length; i++) {
                streak[i] = row[start + i];
            }
            Arrays.sort(streak, Comparator.comparingInt(this::light));
            for (int i = 0; i < streak.length; i++) {
                row[start + i] = streak[i];
            }
            start = end;
        }
        output.setRGB(0, y, row.length, 1, row, 0, row.length);
    }
    return output;
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
    Path output = Path.of("exports", "pixel-sorted.png");
    try {
        Files.deleteIfExists(output);
        saveSorted(output, demo(), 110);
        var image = ImageIO.read(output.toFile());
        if (image == null) throw new IOException("The requested PNG is empty or invalid.");
        IO.println("Loaded requested file: " + output.toAbsolutePath());
        showImages("Pixel Sorting Glitch", new String[]{output.toString()}, new java.awt.image.BufferedImage[]{image});
    } catch (IOException failure) {
        IO.println("Cannot load requested file " + output + ": " + failure.getMessage() + ". Repair saveSorted; Save the source, run its check command, then play again.");
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
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
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
/// - `:` — In for (int value : values), reads each element in turn. It also separates the two
///   alternatives of ? : or follows a switch label.
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `null` — No object is present.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `throws` — In a method declaration: tells callers which failure they may need to handle.
/// - `IO` — Console input and output. println writes a line.
/// - `Path` — A file-system location, not the file’s contents.
/// - `Files` — JDK operations for reading and writing local files.
// </editor-fold>
