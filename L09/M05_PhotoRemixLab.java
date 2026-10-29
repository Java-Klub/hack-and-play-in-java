// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
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
/// Run the program, then compare Pixels counted with the image total; Up/Down changes the
/// poster threshold and R restores the demo.
void countColour(int rgb) {
    colourCounts.put(rgb, 1);
}

// <editor-fold defaultstate="expanded" desc="Learn · F24">
/// ### Map: look up a value by its key
///
/// A `Map<String, Integer>` associates text keys with integer values. Each key has at most one stored
/// value.
///
/// ```java
/// Map<String, Integer> badges = new HashMap<>();
/// badges.put("moon", 4);
/// IO.println(badges.getOrDefault("moon", 0));
/// IO.println(badges.getOrDefault("sun", 0));
/// ```
///
/// The output is `4`, then `0`. `getOrDefault(key, fallback)` reads an existing value, or returns the
/// fallback when the key is absent. This lookup does not insert anything. `put(key, value)` stores a
/// value and replaces any previous value for that key; it does not add numbers automatically. Use
/// `containsKey(key)` when you need to distinguish absence explicitly. These examples do not store
/// null values.
///
/// Predict: after `badges.put("moon", 9)`, what will the same two lookups return?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int rgb` — One integer carries the packed colour used as a map key.
/// - `colourCounts.put` — Stores a value under rgb, replacing any value already stored there.
/// - `rgb, 1` — This pair currently assigns one even when rgb has appeared before.
///
/// ### What the prepared part already does
///
/// remix calls countColour for each demo pixel and creates one two-colour poster. draw reads the
/// real total from the same map; Up/Down changes the threshold and R restarts the fixed demo.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="For this game">
/// ### Read before you replace
///
/// The needed lookup is `map.getOrDefault(key, fallback)`. With this `Map<Integer, Integer>`, the key
/// is a packed colour integer and the returned value is its count. For example, if key 17 stores 6,
/// `map.getOrDefault(17, 0)` returns 6; for absent key 23 it returns 0 without inserting 23.
/// `map.put(key, value)` then stores exactly the value supplied, replacing any previous count.
///
/// Predict the required totals after three pixels with the same colour and one different pixel.
/// Explain why clearing the map once before a new remix is useful, while clearing it inside
/// countColour would forget earlier pixels.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// put replaces the previous value.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Read the existing count, using zero when the key is missing.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Add one to that old count before storing it under the same key.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
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
    result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            countColour(source.getRGB(x, y));
            var colour = new Color(source.getRGB(x, y));
            int brightness = (colour.getRed() * 2126 + colour.getGreen() * 7152 + colour.getBlue() * 722) / 10000;
            result.setRGB(x, y, (brightness < threshold ? new Color(25, 45, 80) : new Color(255, 205, 120)).getRGB());
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
    g.drawString("Two-colour poster | Threshold " + threshold, 12, 22);
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
