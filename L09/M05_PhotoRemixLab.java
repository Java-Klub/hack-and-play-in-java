// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L09.05 · PhotoRemixLab
///
/// Remix a generated picture into a colour spotlight. Find its most common original colour and make
/// every pixel of that colour glow cyan against a two-tone poster.
///
/// **Your mission:** Build dominantColour(): choose the colour key with the largest stored count; break
/// ties by the smaller signed RGB integer. Return 0 for an empty Map and leave all counts unchanged.
/// The prepared remix uses your result to highlight every pixel of that colour in cyan; the unfinished
/// selector highlights nothing in the demo.
///
/// **Today you learn:** Use stored key/value relationships to make a real picture or generation
/// decision, not just a caption.
///
/// Run the program and look for a cyan moon; Up/Down changes the poster threshold and R restores the
/// demo. The unfinished selector leaves the moon yellow instead.
int dominantColour() {
    return 0;
}

// <editor-fold defaultstate="expanded" desc="Learn · F24">
/// ### A Map remembers a value for each key
///
/// A List remembers positions. A Map remembers an association: each key leads to one current value.
/// Here a colour name leads to a count.
///
/// ```java
/// Map<String, Integer> marbles = new HashMap<>();
/// marbles.put("blue", 2);
/// int before = marbles.getOrDefault("blue", 0);
/// marbles.put("blue", before + 1);
/// IO.println(marbles.getOrDefault("gold", 0));
/// ```
///
/// Blue now has count 3; the printed gold count is 0. `getOrDefault(key, fallback)` reads the fallback
/// for an absent key without inserting it. `put` replaces that key's old value: it does not add
/// automatically. Other keys remain unchanged.
///
/// Use `containsKey` when absence itself matters. Use `entrySet()` to visit key/value pairs: each entry
/// provides `getKey()` and `getValue()`. A Map does not promise that the first entry is the largest;
/// compare values explicitly when choosing a winner. Predict blue after another identical update. Then
/// predict what `getOrDefault("gold", 9)` returns and whether gold is now stored.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read this part
///
/// `colourCounts.entrySet()` exposes each key/count pair. `entry.getKey()` reads the RGB integer;
/// getValue() reads its frequency. Track the best key and best count separately: Map order is not a
/// ranking. The supplied countColour builds counts before dominantColour is called. Then remix paints
/// the selected original colour cyan, so your decision changes the actual picture, not only a caption.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Treat the empty Map as a separate result, then compare candidates.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// A candidate wins with a higher count, or with an equal count and a smaller key.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Traverse entrySet, remember the best key/count, update on either winning comparison, and return the
/// key without editing the Map.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Create a different spotlight rule: highlight the rarest colour, or colour each region by its
/// frequency. Compare your visual choice on the same demo before changing the source picture.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
void countColour(int rgb) {
    colourCounts.put(rgb, colourCounts.getOrDefault(rgb, 0) + 1);
}

final int WIDTH = 640, HEIGHT = 440;

BufferedImage source, result;
int threshold;
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
    remix();
}

void remix() {
    int width = source.getWidth(), height = source.getHeight();
    colourCounts.clear();
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) countColour(source.getRGB(x, y));
    }
    int selected = dominantColour();
    result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            var colour = new Color(source.getRGB(x, y));
            int brightness = (colour.getRed() * 2126 + colour.getGreen() * 7152 + colour.getBlue() * 722) / 10000;
            int poster = (brightness < threshold ? new Color(25, 45, 80) : new Color(255, 205, 120)).getRGB();
            result.setRGB(x, y, source.getRGB(x, y) == selected ? Color.CYAN.getRGB() : poster);
        }
    }
}

void adjust(int amount) {
    threshold = Math.max(16, Math.min(240, threshold + amount));
    remix();
}


/// Supplied drawing: reads model values; repainting is not another turn.
void draw(Graphics2D g) {
    g.drawImage(result, (WIDTH - result.getWidth()) / 2, 45, null);
    g.setColor(Color.BLACK);
    g.drawString("Spotlight most common colour | Threshold " + threshold, 12, 22);
    g.drawString("Pixels counted: " + countedPixels() + " / " + source.getWidth() * source.getHeight(), 12, 420);
}

void key(int code, boolean pressed) {
    if (!pressed) return;
    switch (code) {
        case KeyEvent.VK_R -> restart();
        case KeyEvent.VK_UP -> adjust(8);
        case KeyEvent.VK_DOWN -> adjust(-8);
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
    var help = new JLabel("<html><div style=\"text-align:center\">Up/Down: poster threshold   R: restart</div></html>", SwingConstants.CENTER);
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
/// - `boolean` — A value that is either true or false.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `final` — The named value cannot be assigned another value afterwards. A referenced mutable object
///   can still change.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
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
/// - `null` — No object is present.
/// - `Map` — Associates keys with values. A missing key needs an explicit fallback or decision.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
