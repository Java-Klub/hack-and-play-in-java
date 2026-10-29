// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L11.11 · MirrorCylinderPicture
///
/// Turn a word from your text file into a fan of curved bands, as if preparing it for a cylindrical
/// mirror. Inspect its distortion directly in the Java picture and try a different word on the next
/// run.
///
/// **Your mission:** Create readCaption: open the chosen UTF-8 text file with newBufferedReader, read
/// its first line, strip surrounding whitespace, and accept 1..8 characters. Report an empty, blank
/// or too-long first line with IOException. The rendered word must come from this file.
///
/// **Today you learn:** Implement a real file boundary: open the chosen resource, route actual
/// contents through it and close it reliably. The artifact must survive beyond a method call.
///
/// The first run creates data/mirror-word.txt containing JAVA. Edit its first line to MOON, then run
/// again: the Java picture must change. Keep at most eight characters so the word fits the picture.
String readCaption(Path path) throws IOException {
    // Read, validate and return the first line of the chosen file.
    return "JAVA";
}

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Author a word-art collection by editing the first line of data/mirror-word.txt between runs. Try
/// MOON, a short name and a punctuation mark. Keep one saved text file per design; compare how a
/// narrow and a wide word distort around the same circle.
// </editor-fold>

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

// <editor-fold defaultstate="expanded" desc="Read this code">
/// ### A text file becomes the artwork's input
///
/// Files.newBufferedReader(path) opens a UTF-8 character reader. reader.readLine() returns the next
/// line without its line ending, or null when no line exists. A blank line is the String "", which
/// differs from null. Check null before calling String methods. A neutral file containing "  NOVA  "
/// on its first line yields "NOVA" after strip; a second line is outside this task's contract.
/// Predict the result for a file with a blank first line and NOVA on the second.
///
/// try-with-resources closes the reader if reading, validation or return exits the block. IOException
/// preserves missing-file and bad-content failures for main to report. main creates
/// data/mirror-word.txt with JAVA only if absent, reads it into caption, and renders that caption
/// directly. Edit this small text file between runs to author a different picture.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Handle the possibility of null before stripping the first line.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Use try-with-resources around newBufferedReader(path); do not parse the filename as content.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Read one line, reject null, strip it, reject length outside 1..8, and return the accepted text.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String caption = "JAVA";

double[] ground(double angle, double height) {
    double x = 60 * Math.cos(angle);
    double y = 60 * Math.sin(angle);
    double dx = x;
    double dy = y + 600;
    double dz = height - 300;
    double dot = dx * Math.cos(angle) + dy * Math.sin(angle);
    dx -= 2 * dot * Math.cos(angle);
    dy -= 2 * dot * Math.sin(angle);
    double distance = -height / dz;
    return new double[]{x + distance * dx, y + distance * dy};
}

/// Supplied image creation: generation and drawing remain visible in this file.
BufferedImage render(long seed, double option) {
    if (!Double.isFinite(option) || option < 30 || option > 140) {
        throw new IllegalArgumentException("Maximum mirror height: 30..140.");
    }
    var source = new BufferedImage(500, 160, BufferedImage.TYPE_INT_RGB);
    var ink = source.createGraphics();
    ink.setColor(Color.WHITE);
    ink.fillRect(0, 0, 500, 160);
    ink.setColor(Color.getHSBColor(new Random(seed).nextFloat(), 0.9F, 0.7F));
    ink.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.min(100, 440 / Math.max(1, caption.length()))));
    ink.drawString(caption, 30, 120);
    ink.dispose();
    var image = new BufferedImage(900, 800, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 900, 800);
        for (int v = 0; v < 160; v++) {
            for (int u = 0; u < 500; u++) {
                double angle = -Math.PI / 2 + (u / 499.0 - 0.5) * 1.4;
                var point = ground(angle, 5 + option * (1 - v / 159.0));
                g.setColor(new Color(source.getRGB(u, v)));
                g.fillRect(450 + (int)point[0], 700 + (int)point[1], 4, 4);
            }
        }
        g.setColor(Color.BLACK);
        g.drawOval(390, 640, 120, 120);
        g.drawString("Cylinder radius 60. View from (0,-600,300), relative to circle centre.", 15, 785);
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
    Path input = Path.of("data", "mirror-word.txt");
    try {
        if (!Files.exists(input)) {
            Files.createDirectories(input.toAbsolutePath().getParent());
            Files.writeString(input, "JAVA\n");
        }
        caption = readCaption(input);
        IO.println("Read " + caption + " from " + input.toAbsolutePath());
        IO.println("Edit the first line of that text file and run again to author another picture.");
        showImages("Mirror Cylinder Picture", new String[]{caption}, new BufferedImage[]{render(42, 125)});
    } catch (Exception failure) {
        IO.println("Cannot read the word artwork: " + failure.getMessage());
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
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `null` — No object is present.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `throws` — In a method declaration: tells callers which failure they may need to handle.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line.
/// - `Path` — A file-system location, not the file’s contents.
/// - `Files` — JDK operations for reading and writing local files.
// </editor-fold>
