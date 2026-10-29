// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # 12.19 · SecretPNGPostcard
///
/// The message carrier already works. Now give it real artwork: use your own PNG instead of a picture
/// that is regenerated inside the program.
///
/// **Your mission:** Use data/cover.png as the actual artwork for a new secret postcard. Read that
/// input, hide the entered message and write the separate data/postcard.png without changing the
/// cover. Enter later must reopen the saved postcard unchanged. Replacing the cover must affect the
/// next newly written card, not rewrite an older one automatically.
///
/// **Investigation:** Investigate persisted input data and make a real image-to-postcard tool.
///
/// The first run creates a sample cover. Put another PNG at the printed cover path, type a new
/// message and compare the resulting card. Enter opens the existing card; /q quits.

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// Design a visual riddle in your cover image and send two different messages in separate cards.
/// Compare the images at normal size before decoding, then compare which low pixel bits changed. Keep
/// both original artworks intact.
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

// <editor-fold defaultstate="expanded" desc="Investigator notebook · worked example">
/// ### Follow bytes across two distinct file roles
///
/// An input path identifies what you read; an output path identifies what you create. Changing an
/// in-memory object does not rewrite its input file unless you explicitly write there. Keep input
/// validation ahead of opening the destination so an unreadable input does not erase useful output.
///
/// ```java
/// String original = Files.readString(Path.of("draft.txt"));
/// String edited = original.toUpperCase(Locale.ROOT);
/// Files.writeString(Path.of("poster.txt"), edited);
/// ```
///
/// If draft.txt contains Nova, poster.txt receives NOVA while draft.txt still contains Nova. Predict
/// a second run after draft.txt is changed to Orbit. Images have a similar boundary: ImageIO.read
/// decodes an image, a transformation creates a result, and ImageIO.write encodes that result. A PNG
/// encoder preserves the low bits used by this toy's message format. Opening an existing card is a
/// read operation; creating a card is a separate choice.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Find where a new message and its background meet. Compare that route with opening an already saved
/// card.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// main routes new cards through postcardFromCover. Its current body still calls the old
/// generated-background writer; encode can already hide text in any supplied image.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Read and validate cover with ImageIO, encode that image and text before opening the output, then
/// write PNG to the separate destination. Preserve the cover and reject using it as its own
/// destination.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program to explore">
void save(Path path, long seed, double option) throws Exception {
    Files.createDirectories(path.toAbsolutePath().getParent());
    try (var output = Files.newOutputStream(path)) {
        if (!ImageIO.write(render(seed, option), "png", output)) {
            throw new IOException("PNG writer missing.");
        }
    }
}

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
BufferedImage background(long seed) {
    var image = new BufferedImage(400, 240, BufferedImage.TYPE_INT_RGB);
    var random = new Random(seed);
    for (int y = 0; y < 240; y++) {
        for (int x = 0; x < 400; x++) {
            image.setRGB(x, y, Color.HSBtoRGB(x / 400.0F, 0.5F, 0.6F + random.nextFloat() * 0.4F));
        }
    }
    return image;
}

BufferedImage render(long seed, double option) {
    return encode(background(seed), message);
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


void postcardFromCover(Path cover, Path destination, String text) throws Exception {
    if (Files.exists(destination) && Files.isSameFile(cover, destination)) {
        throw new IllegalArgumentException("Cover and postcard must be different files.");
    }
    message = text;
    save(destination, 42, 1);
}

void main() {
    Path cover = Path.of("data", "cover.png");
    Path card = Path.of("data", "postcard.png");
    try {
        if (!Files.exists(cover)) {
            Files.createDirectories(cover.toAbsolutePath().getParent());
            if (!ImageIO.write(background(42), "png", cover.toFile())) throw new IOException("PNG writer missing.");
        }
        IO.println("Artwork input: " + cover.toAbsolutePath());
        String text = IO.readln("New secret message, Enter opens the saved postcard, /q to quit: ");
        if (text == null || text.strip().equalsIgnoreCase("/q")) return;
        if (!text.isEmpty() || !Files.exists(card)) {
            String chosen = text.isEmpty() ? "Pixel moon" : text;
            postcardFromCover(cover, card, chosen);
        }
        String recovered = readMessage(card);
        if (!text.isEmpty() && !recovered.equals(text)) throw new IOException("The saved message differs from the new text.");
        var image = ImageIO.read(card.toFile());
        if (image == null) throw new IOException("The postcard is not a readable PNG.");
        IO.println("Recovered from " + card.toAbsolutePath() + ": " + recovered);
        showImages("Secret PNG Postcard", new String[]{recovered}, new BufferedImage[]{image});
    } catch (Exception failure) {
        IO.println("Cannot open the postcard: " + failure.getMessage());
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
