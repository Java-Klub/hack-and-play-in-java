// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L09.05 · PhotoRemixLab
///
/// Remix a generated picture with image effects and inspect its colours. The pixel counter should account
/// for every point, even a large patch of the same colour.
///
/// **Your mission:** Repeated colours keep a count of one, so the displayed pixel total is too small.
/// Repair the tally without changing the picture effects.
///
/// **Today you learn:** A Map associates each colour key with its current count. A repeated key needs an
/// updated total; put replaces the old value rather than adding to it automatically.
///
/// After Run File, press M to change the moon picture’s remix mode, adjust it with arrow keys and read
/// the pixel counter above the image.
/// After your repair: Save → Test File → play again.
void countColour(int rgb) {
    colourCounts.put(rgb, 1);
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int rgb` — One integer carries the packed colour used as a map key.
/// - `colourCounts.put` — Stores a value under rgb, replacing any value already stored there.
/// - `rgb, 1` — This pair currently assigns one even when rgb has appeared before.
///
/// ### What the prepared part already does
///
/// The remix loop calls countColour for the original source pixels. countedPixels adds the map values for
/// the visible caption; the supplied demo includes repeated colours so the mistake is visible.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. put replaces the previous value.
/// 2. Read the existing count, using zero when the key is missing.
/// 3. Add one to that old count before storing it under the same key.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 640, HEIGHT = 440;

BufferedImage source, result;
boolean pixelated, original;
int threshold, block;
String message;
/// Supplied restart: restore the initial model before the UI uses it.
void restart() {
    source = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < 300; y++) {
        for (int x = 0; x < 400; x++) {
            source.setRGB(x, y, new Color(x * 255 / 399, y * 255 / 299, (x + y) * 255 / 698).getRGB());
        }
    }
    var moon = source.createGraphics();
    moon.setColor(Color.WHITE);
    moon.fillOval(120, 70, 150, 150);
    moon.dispose();
    threshold = 128;
    block = 12;
    pixelated = original = false;
    message = "O: load a personal image";
    remix();
}

void remix() {
    int w = source.getWidth(), h = source.getHeight();
    colourCounts.clear();
    for (int yy = 0; yy < h; yy++) {
        for (int xx = 0; xx < w; xx++) countColour(source.getRGB(xx, yy));
    }
    result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    if (!pixelated) {
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                var c = new Color(source.getRGB(x, y));
                int light = (c.getRed() * 2126 + c.getGreen() * 7152 + c.getBlue() * 722) / 10000;
                result.setRGB(x, y, (light < threshold ? new Color(25, 45, 80) : new Color(255, 205, 120)).getRGB());
            }
        }
    }
    else {
        for (int y = 0; y < h; y += block) {
            for (int x = 0; x < w; x += block) {
                long r = 0, g = 0, b = 0, count = 0;
                for (int yy = y; yy < Math.min(h, y + block); yy++) {
                    for (int xx = x; xx < Math.min(w, x + block); xx++) {
                        var c = new Color(source.getRGB(xx, yy));
                        r += c.getRed();
                        g += c.getGreen();
                        b += c.getBlue();
                        count++;
                    }
                }
                int rgb = new Color((int)(r / count), (int)(g / count), (int)(b / count)).getRGB();
                for (int yy = y; yy < Math.min(h, y + block); yy++) {
                    for (int xx = x; xx < Math.min(w, x + block); xx++) {
                        result.setRGB(xx, yy, rgb);
                    }
                }
            }
        }
    }
}

void adjust(int t, int size) {
    threshold = Math.max(16, Math.min(240, threshold + t));
    block = Math.max(2, Math.min(40, block + size));
    remix();
}

void load(File file) throws IOException {
    try(var input = ImageIO.createImageInputStream(file)) {
        if (input == null) {
            throw new IOException("Cannot read this file");
        }
        var readers = ImageIO.getImageReaders(input);
        if (!readers.hasNext()) {
            throw new IOException("Unsupported image");
        }
        var reader = readers.next();
        try {
            reader.setInput(input);
            int w = reader.getWidth(0), h = reader.getHeight(0);
            if ((long)w * h > 20000000) {
                throw new IOException("Image is too large (maximum 20 million pixels)");
            }
            var loaded = reader.read(0);
            double scale = Math.min(1, Math.min(600.0 / w, 350.0 / h));
            source = new BufferedImage(Math.max(1, (int)(w * scale)), Math.max(1, (int)(h * scale)), BufferedImage.TYPE_INT_RGB);
            var g = source.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, source.getWidth(), source.getHeight());
            g.drawImage(loaded, 0, 0, source.getWidth(), source.getHeight(), null);
            g.dispose();
            remix();
        } finally {
            reader.dispose();
        }
    }
}

void choose() {
    var chooser = new JFileChooser();
    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        try {
            load(chooser.getSelectedFile());
            message = "Loaded " + chooser.getSelectedFile().getName();
        } catch (IOException e) {
            message = e.getMessage();
        }
    }
}

/// Supplied export boundary: a local path is used; encoding is handled by the JDK.
void save(File file) throws IOException {
    ImageIO.write(result, "png", file);
}

/// Supplied export boundary: a local path is used; encoding is handled by the JDK.
void save() {
    try {
/// Supplied export boundary: a local path is used; encoding is handled by the JDK.
        save(new File("photo-remix.png"));
        message = "Saved photo-remix.png";
    } catch (IOException e) {
        message = "Save failed: " + e.getMessage();
    }
}

/// Supplied drawing: reads model values; repainting is not another turn.
void draw(Graphics2D g) {
    var image = original ? source : result;
    g.drawImage(image, (WIDTH - image.getWidth()) / 2, 45, null);
    g.setColor(Color.BLACK);
    g.drawString((original ? "Original" : pixelated ? "Pixel portrait" : "Two-colour poster") + "   Threshold " + threshold + "   Block " + block, 12, 22);
    g.drawString(message + " | Pixels counted: " + countedPixels() + " / " + source.getWidth() * source.getHeight(), 12, 420);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Supplied restart: restore the initial model before the UI uses it.
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_O -> choose();
            case KeyEvent.VK_M -> {
                pixelated = !pixelated;
                remix();
            }
            case KeyEvent.VK_SPACE -> original = !original;
            case KeyEvent.VK_UP -> adjust(8, 0);
            case KeyEvent.VK_DOWN -> adjust(-8, 0);
            case KeyEvent.VK_LEFT -> adjust(0, -2);
            case KeyEvent.VK_RIGHT -> adjust(0, 2);
            case KeyEvent.VK_S -> save();
        }
    }
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Photo Remix Lab");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            var g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/// Supplied drawing: reads model values; repainting is not another turn.
            draw(g);
            g.dispose();
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    panel.setBackground(Color.WHITE);
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent e) {
            key(e.getKeyCode(), true);
            panel.repaint();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">O: open image   M: poster/pixels   Arrows: threshold/block   Space: original   S: save   R:<br>reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

Map<Integer, Integer> colourCounts = new HashMap<>();

int countedPixels() {
    int total = 0;
    for (int count : colourCounts.values()) total += count;
    return total;
}

/// Start the real program with Run File. Test File checks the rule without opening a window.
/// remix clears colourCounts and counts source pixels, then creates the transformed image.
/// draw shows the real tally; load/save and Swing input are supplied boundaries.
void main() {
/// Supplied restart: restore the initial model before the UI uses it.
    restart();
    SwingUtilities.invokeLater(this::showWindow);
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
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
/// - `final` — The named value cannot be assigned another value afterwards. A referenced mutable object
///   can still change.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
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
/// - `?` — In condition ? first : second, chooses the first value when the condition is true, otherwise
///   the second.
/// - `:` — In for (int value : values), reads each element in turn. It also separates the two
///   alternatives of ? : or follows a switch label.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `else` — The alternative when the preceding if condition is false.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `switch` — Selects one case from a set of alternatives. It can also produce a result.
/// - `case` — One alternative of a switch; the arrow points to what it does or returns.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `public` — Makes this declaration accessible to other code.
/// - `protected` — A visibility keyword used by supplied library callbacks, such as paintComponent.
/// - `super` — In super.paintComponent(g), asks the supplied parent implementation to paint its standard
///   background first.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `throws` — In a method declaration: tells callers which failure they may need to handle.
/// - `Map` — Associates keys with values. A missing key needs an explicit fallback or decision.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>

