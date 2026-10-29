// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L11.10 · SecretPNGPostcard
///
/// Send a secret postcard whose visible picture gives little away. The program hides your text in PNG
/// pixel data and tries to read the message back from the saved card.
///
/// **Your mission:** Implement save as the postcard writer: create parent directories, open the
/// requested output stream, and encode render(seed, option) as PNG into it. Use try-with-resources
/// and report a missing PNG writer. Then write your own message and reopen it on a later run.
///
/// **Today you learn:** Implement a real file boundary: open the chosen resource, route actual
/// contents through it and close it reliably. The artifact must survive beyond a method call.
///
/// Enter a new secret message, or press Enter to open data/postcard.png unchanged. On the first run,
/// Enter creates Pixel moon. A later run reads the persisted card; /q quits.
void save(Path path, long seed, double option) throws Exception {
    // Open the actual destination and write the encoded postcard into it.
}

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Send a real local postcard: write a short riddle, close the program, and let a friend open the
/// existing card without typing a new message. Then change only the visible background in render and
/// check whether an older saved card still reveals its original text.
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
/// ### Your message survives the next run
///
/// ImageIO.write(image, "png", output) returns true when it finds a PNG encoder; it sends the encoded
/// bytes to the supplied stream. A neutral example is a tiny painted icon saved to a notebook cover:
/// create its directory, open the cover.png stream in try-with-resources, then pass that stream to
/// ImageIO.write. After the block, another program can read the file. Predict what remains if the
/// stream is opened but the write call is omitted.
///
/// render already hides the current message in image pixels using encode. You own persistence, not
/// the bit algorithm. readMessage opens the actual PNG and extracts its stored text. main only writes
/// when you enter a new message or no postcard exists. Enter on a later run opens the existing
/// postcard unchanged, even in a fresh game object. Thus the file carries a message between runs, not
/// merely between two adjacent method calls.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The prepared render already returns the encoded postcard. save only owns its durable destination.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Create the directory, then use newOutputStream(path) in try-with-resources.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Call ImageIO.write(render(seed, option), "png", output), check its boolean result, and keep the
/// actual stream as its destination.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String readMessage(Path path) throws IOException {
    try (var input = Files.newInputStream(path)) {
        var image = ImageIO.read(input);
        if (image == null) throw new IOException("Input is not a readable image.");
        return decode(image);
    }
}

String message = "The moon has excellent Wi-Fi.";

BufferedImage encode(BufferedImage original, String text) {
    byte[] payload = text.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    int capacity = original.getWidth() * original.getHeight() * 3 / 8 - 4;
    if (payload.length > capacity) {
        throw new IllegalArgumentException("Message exceeds image capacity.");
    }
    byte[] data = new byte[payload.length + 4];
    java.nio.ByteBuffer.wrap(data).putInt(payload.length).put(payload);
    var image = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    g.drawImage(original, 0, 0, null);
    g.dispose();
    for (int bit = 0; bit < data.length * 8; bit++) {
        int pixel = bit / 3;
        int x = pixel % image.getWidth();
        int y = pixel / image.getWidth();
        int shift = 16 - bit % 3 * 8;
        int rgb = image.getRGB(x, y);
        int value = (data[bit / 8] >> (7 - bit % 8)) & 1;
        image.setRGB(x, y, (rgb & ~(1 << shift)) | (value << shift));
    }
    return image;
}

int readByte(BufferedImage image, int index) {
    int result = 0;
    for (int offset = 0; offset < 8; offset++) {
        int bit = index * 8 + offset;
        int pixel = bit / 3;
        int rgb = image.getRGB(pixel % image.getWidth(), pixel / image.getWidth());
        result = result * 2 + ((rgb >> (16 - bit % 3 * 8)) & 1);
    }
    return result;
}

String decode(BufferedImage image) {
    int capacity = image.getWidth() * image.getHeight() * 3 / 8;
    if (capacity < 4) {
        throw new IllegalArgumentException("Image is too small.");
    }
    int length = 0;
    for (int i = 0; i < 4; i++) {
        length = (length << 8) | readByte(image, i);
    }
    if (length < 0 || length > capacity - 4) {
        throw new IllegalArgumentException("Invalid message header.");
    }
    byte[] data = new byte[length];
    for (int i = 0; i < length; i++) {
        data[i] = (byte)readByte(image, i + 4);
    }
    return new String(data, java.nio.charset.StandardCharsets.UTF_8);
}

/// Supplied image creation: generation and drawing remain visible in this file.
BufferedImage render(long seed, double option) {
    var image = new BufferedImage(400, 240, BufferedImage.TYPE_INT_RGB);
    var random = new Random(seed);
    for (int y = 0; y < 240; y++) {
        for (int x = 0; x < 400; x++) {
            image.setRGB(x, y, Color.HSBtoRGB(x / 400.0F, 0.5F, 0.6F + random.nextFloat() * 0.4F));
        }
    }
    return encode(image, message);
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
    Path card = Path.of("data", "postcard.png");
    try {
        String text = IO.readln("New secret message, Enter opens the saved postcard, /q to quit: ");
        if (text == null || text.strip().equalsIgnoreCase("/q")) return;
        if (!text.isEmpty() || !Files.exists(card)) {
            message = text.isEmpty() ? "Pixel moon" : text;
            save(card, 42, 1);
        }
        String recovered = readMessage(card);
        if (!text.isEmpty() && !recovered.equals(text)) {
            throw new IOException("The saved postcard does not contain the new message.");
        }
        var image = ImageIO.read(card.toFile());
        if (image == null) throw new IOException("The postcard is not a readable PNG.");
        IO.println("Recovered from " + card.toAbsolutePath() + ": " + recovered);
        showImages("Secret PNG Postcard", new String[]{recovered}, new BufferedImage[]{image});
    } catch (Exception failure) {
        IO.println("Cannot open the saved postcard: " + failure.getMessage());
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
/// - `%` — The remainder after division; useful for repeated positions or turns.
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
