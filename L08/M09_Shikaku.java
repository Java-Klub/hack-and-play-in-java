// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
// </editor-fold>

/// # L08.09 · Shikaku
///
/// Draw rectangles around clues so each rectangle’s area matches its one number. Remove a bad guess or
/// undo it and try another partition.
///
/// **Your mission:** Undo shares the live rectangle list, so adding or removing a box also rewrites the
/// saved state. Repair snapshot() to preserve list membership.
///
/// **Today you learn:** Copies and aliasing: copy the mutable container when its immutable elements are
/// safe to share. Box records need not be duplicated just to preserve which boxes are present.
///
/// From the course folder containing README.md:
/// - Play: `java L08/M09_Shikaku.java`
/// - Check this mission: `java L08/Check.java M09_Shikaku.java`
///
/// Run the play command above and drag a rectangle around one clue, then press U to try undoing it
/// or right-click to remove a rectangle. After your repair: save the file, run the check command
/// above, then play again.
///
/// Need an explanation? Recall · F20 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
ArrayList<Box> snapshot() {
    return new ArrayList<>(boxes);
}

// <editor-fold defaultstate="collapsed" desc="Recall · F20">
/// ### References: another name or another object?
///
/// An array variable stores a reference. Assignment can give two names access to one array; writing
/// through either name affects that shared object. Calling `clone()` on a one-dimensional int array
/// creates another array with copied values. The original and copy can then change independently.
///
/// ```java
/// int[] wave = {4, 1};
/// int[] shared = wave;
/// int[] saved = wave.clone();
/// wave[1] = 7;
/// IO.println(shared[1]);
/// IO.println(saved[1]);
/// ```
///
/// The output is seven, then one: shared sees the write, saved retains its copied value. After
/// `saved[0] = 8`, predict `wave[0]` and `saved[0]`. `clone()` copies one layer: for a grid, its
/// elements are row references, so copying the outside still shares rows. Ask which objects remain
/// shared, not merely whether a new outer object exists.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `ArrayList<Box>` — A mutable list whose elements are Box record values.
/// - `snapshot()` — Requests the rectangle membership before the next real edit.
/// - `return boxes` — Returns the same list object, so later membership changes remain shared.
///
/// ### What the prepared part already does
///
/// add() validates area, clue count, and overlap; remove() finds a selected rectangle. Both save a
/// snapshot before committing, and the supplied undo() restores that list once.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="For this game">
/// ### A mutable list needs a new container
///
/// ArrayList stores an ordered, changeable group of elements. `<String>` below names the element
/// type; `<>` lets the constructor infer it. The constructor accepting another collection copies its
/// membership into a new mutable list.
///
/// ```java
/// var draft = new ArrayList<String>();
/// draft.add("swirl");
/// var kept = new ArrayList<>(draft);
/// draft.clear();
/// IO.println(kept.size());
/// ```
///
/// The output is one: `add` appended a value, `clear` emptied only draft, and `size` counts kept's
/// elements. Predict adding another draft element. In this game Box contains only fixed int
/// components, so sharing Box values is safe while sharing the changeable list is not. Undo needs a
/// mutable container because later drawing can add or remove rectangles.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The list changes, but each Box record keeps its components forever.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Returning boxes aliases the same list; cloning every Box is unnecessary.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Return new ArrayList<>(boxes) so membership changes are independent.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 470, HEIGHT = 400;

record Box(int x, int y, int width, int height) {
    boolean contains(int xx, int yy) {
        return xx >= x && yy >= y && xx < x + width && yy < y + height;
    }
}

int[][] clues = {{0, 0, 0, 6, 0}, {6, 0, 0, 0, 0}, {0, 0, 0, 0, 4}, {0, 0, 3, 0, 0}, {0, 4, 0, 2, 0}};
ArrayList<Box> boxes = new ArrayList<>();
int startX = -1, startY, endX, endY;
String message;
/// restart(): Restores the starting Shikaku model so this toy can be tried again.
void restart() {
    boxes.clear();
    previousBoxes = null;
    startX = -1;
    message = "Drag a rectangle around exactly one clue";
}

/// add(): Validates a rectangle’s clue, area and non-overlap before saving and committing it.
boolean add(int x1, int y1, int x2, int y2) {
    int x = Math.min(x1, x2), y = Math.min(y1, y2), w = Math.abs(x1 - x2) + 1, h = Math.abs(y1 - y2) + 1;
    if (x < 0 || y < 0 || x + w > 5 || y + h > 5) {
        return false;
    }
    int count = 0, area = 0;
    for (int yy = y; yy < y + h; yy++) {
        for (int xx = x; xx < x + w; xx++) {
            if (clues[yy][xx] != 0) {
                count++;
                area = clues[yy][xx];
            }
            for (var b : boxes) {
                if (b.contains(xx, yy)) {
                    return false;
                }
            }
        }
    }
    if (count != 1 || area != w * h) {
        return false;
    }
    previousBoxes = snapshot();
    boxes.add(new Box(x, y, w, h));
    return true;
}

void remove(int x, int y) {
    boolean found = false;
    for (var box : boxes) {
        if (box.contains(x, y)) {
            found = true;
        }
    }
    if (found) {
        previousBoxes = snapshot();
        boxes.removeIf(box -> box.contains(x, y));
        message = "Rectangle removed";
    }
}

boolean won() {
    int area = 0;
    for (var b : boxes) {
        area += b.width * b.height;
    }
    return area == 25;
}

int cellX(int x) {
    return (x - 80) / 55;
}

int cellY(int y) {
    return (y - 40) / 55;
}

boolean inside(int x, int y) {
    return x >= 80 && y >= 40 && x < 355 && y < 315;
}

/// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
void draw(Graphics2D g) {
    Color[] palette = {new Color(210, 230, 255), new Color(255, 225, 195), new Color(215, 245, 210), new Color(240, 215, 250), new Color(255, 245, 185), new Color(200, 245, 240)};
    for (int i = 0; i < boxes.size(); i++) {
        var b = boxes.get(i);
        g.setColor(palette[i % palette.length]);
        g.fillRect(80 + b.x * 55, 40 + b.y * 55, b.width * 55, b.height * 55);
    }
    g.setColor(Color.LIGHT_GRAY);
    for (int i = 0; i <= 5; i++) {
        g.drawLine(80 + i * 55, 40, 80 + i * 55, 315);
        g.drawLine(80, 40 + i * 55, 355, 40 + i * 55);
    }
    g.setStroke(new BasicStroke(3));
    g.setColor(Color.DARK_GRAY);
    for (var b : boxes) {
        g.drawRect(80 + b.x * 55 + 2, 40 + b.y * 55 + 2, b.width * 55 - 4, b.height * 55 - 4);
    }
    if (startX >= 0) {
        g.setColor(Color.BLUE);
        g.drawRect(80 + Math.min(startX, endX) * 55, 40 + Math.min(startY, endY) * 55, (Math.abs(startX - endX) + 1) * 55, (Math.abs(startY - endY) + 1) * 55);
    }
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
    g.setColor(Color.BLACK);
    for (int y = 0; y < 5; y++) {
        for (int x = 0; x < 5; x++) {
            if (clues[y][x] != 0) {
                g.drawString("" + clues[y][x], 102 + x * 55, 74 + y * 55);
            }
        }
    }
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    g.drawString(won() ? "Every square covered!" : message, 20, 370);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_U) {
        undo();
    }
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (inside(x, y)) {
        if (button == MouseEvent.BUTTON3) {
            remove(cellX(x), cellY(y));
        }
        else {
            startX = endX = cellX(x);
            startY = endY = cellY(y);
        }
    }
}

void drag(int x, int y) {
    if (startX >= 0 && inside(x, y)) {
        endX = cellX(x);
        endY = cellY(y);
    }
}

void release(int x, int y) {
    if (startX >= 0) {
        if (inside(x, y)) {
            endX = cellX(x);
            endY = cellY(y);
            message = add(startX, startY, endX, endY) ? "Rectangle added" : "Rejected: check clue, area and overlap";
        }
        startX = -1;
    }
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; disposing the
/// window closes this view.
void showWindow() {
    var frame = new JFrame("Shikaku");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            var g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
    var mouse = new MouseAdapter() {
        @Override public void mousePressed(MouseEvent e) {
            panel.requestFocusInWindow();
            press(e.getX(), e.getY(), e.getButton());
            panel.repaint();
        }
        @Override public void mouseDragged(MouseEvent e) {
            drag(e.getX(), e.getY());
            panel.repaint();
        }
        @Override public void mouseReleased(MouseEvent e) {
            release(e.getX(), e.getY());
            panel.repaint();
        }
    };
    panel.addMouseListener(mouse);
    panel.addMouseMotionListener(mouse);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Drag: add rectangle   Right click: remove rectangle<br>U: undo one change   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

/// main(): Starts the model, then schedules the supplied window on Swing’s event thread.
/// The prepared Undo control is U: it restores one previous edit. R starts this puzzle again.
void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}

ArrayList<Box> previousBoxes;

/// undo(): Restores exactly one saved model snapshot and consumes that undo opportunity.
void undo() {
    if (previousBoxes != null) {
        boxes = previousBoxes;
        previousBoxes = null;
        startX = -1;
        message = "Previous layout restored";
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
/// - `boolean` — A value that is either true or false.
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `final` — The named value cannot be assigned another value afterwards. A referenced mutable object
///   can still change.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `!=` — Checks that two values differ.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+=` — Adds to the old value and stores the result back under the same name.
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
/// - `else` — The alternative when the preceding if condition is false.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `public` — Makes this declaration accessible to other code.
/// - `protected` — A visibility keyword used by supplied library callbacks, such as paintComponent.
/// - `super` — In super.paintComponent(g), asks the supplied parent implementation to paint its standard
///   background first.
/// - `record` — A small named bundle of values. Its component accessors use the component names with ().
/// - `null` — No object is present.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
