// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Path2D;
import java.awt.geom.Area;
// </editor-fold>

/// # L10.03 · ShadowPlayground
///
/// **Move the lamp on a grid.** The lamp does not respond to clicks or dragging yet. The
/// obstacles and their shadows already draw.
///
/// **Your task:** Fill the delayed action in `lightAction` below. Snap each click or drag
/// coordinate down to a multiple of 20 and pass both to the supplied `moveLight`. For example,
/// `(43, 91)` becomes `(40, 80)`. Move the lamp when the event arrives, not when the action is
/// created.
///
/// **Result:** The lamp jumps in twenty-pixel steps and the shadows change. `moveLight` still
/// keeps it inside the canvas bounds; R restores the starting position.

java.util.function.BiConsumer<Integer, Integer> lightAction() {
    return (x, y) -> {
        int snappedX = x / 20 * 20;
        int snappedY = y / 20 * 20;
        moveLight(snappedX, snappedY);
    };
}

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Invent a haunted lamp: clicks control its mirror image across the vertical centre instead of its
/// direct position. Predict where a left-edge click will place it, then make a shadow puppet scene
/// for a friend to identify.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Recall · F28">
/// ### Callback: give an action for later
///
/// A callback is an action another part of the program calls later, perhaps after a click. A
/// `Runnable` is an action with no supplied arguments and no returned result.
///
/// ```java
/// Runnable cheer = () -> IO.println("Hooray!");
/// IO.println("Ready");
/// cheer.run();
/// ```
///
/// The output is `Ready`, then `Hooray!`. Creating the lambda `() -> ...` does not execute its body.
/// Calling `run()` does. Braces allow several statements inside one callback. A method reference such
/// as `this::wave` similarly supplies this object's `wave` method without calling it yet; `wave()`
/// calls it immediately. The prepared UI decides when to invoke a callback.
///
/// Predict: what prints if the last line becomes two consecutive calls to `cheer.run()`?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Read this code">
/// ### Transform an event before forwarding it
///
/// For nonnegative pixel coordinates, integer division drops the remainder: 47 / 10 is 4, and
/// multiplying by 10 gives 40. Thus a 10-pixel grid turns (47, 23) into (40, 20). Predict where (9,
/// 31) lands on that grid before adapting the idea to this stage's 20-pixel grid.
///
/// BiConsumer<Integer, Integer> accepts two integer values and returns no result. `accept(43, 91)`
/// supplies the lambda's local x and y; calculating local values does not itself move the lamp. Only
/// the final moveLight call changes the shared model. press and drag both use this same action, so
/// snapping belongs here rather than in only one listener. The supplied clamping still handles
/// coordinates outside the canvas.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Calculate inside the lambda; click coordinates do not exist at action creation.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// For each nonnegative coordinate, divide by 20 as an int, then multiply by 20.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Store two snapped local values and call moveLight once with them.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 640, HEIGHT = 440;

Rectangle[] obstacles = {new Rectangle(190, 130, 70, 100), new Rectangle(380, 220, 100, 60)};
double lightX, lightY;
/// Supplied restart: restore the initial model before the UI uses it.
void restart() {
    lightX = 100;
    lightY = 80;
}

void moveLight(int x, int y) {
    lightX = Math.max(0, Math.min(WIDTH, x));
    lightY = Math.max(0, Math.min(HEIGHT, y));
}

Area shadow(Rectangle obstacle) {
    if (obstacle.contains(lightX, lightY)) {
        return new Area(new Rectangle(0, 0, WIDTH, HEIGHT));
    }
    Point[] corners = {new Point(obstacle.x, obstacle.y), new Point(obstacle.x + obstacle.width, obstacle.y), new Point(obstacle.x + obstacle.width, obstacle.y + obstacle.height), new Point(obstacle.x, obstacle.y + obstacle.height)};
    var area = new Area(obstacle);
    for (int i = 0; i < 4; i++) {
        var a = corners[i];
        var b = corners[(i + 1) % 4];
        double da = Math.max(.001, a.distance(lightX, lightY)), db = Math.max(.001, b.distance(lightX, lightY));
        var path = new Path2D.Double();
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.lineTo(b.x + (b.x - lightX) / db * 1500, b.y + (b.y - lightY) / db * 1500);
        path.lineTo(a.x + (a.x - lightX) / da * 1500, a.y + (a.y - lightY) / da * 1500);
        path.closePath();
        area.add(new Area(path));
    }
    return area;
}

/// Supplied drawing: reads model values; repainting is not another turn.
void draw(Graphics2D g) {
    g.setColor(new Color(255, 246, 205));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    var combined = new Area();
    boolean inside = false;
    for (var obstacle : obstacles) {
        combined.add(shadow(obstacle));
        if (obstacle.contains(lightX, lightY)) {
            inside = true;
        }
    }
    g.setColor(new Color(65, 75, 100, 160));
    g.fill(combined);
    g.setColor(new Color(45, 50, 65));
    for (var obstacle : obstacles) {
        g.fill(obstacle);
    }
    g.setColor(Color.ORANGE);
    g.fillOval((int)lightX - 10, (int)lightY - 10, 20, 20);
    g.setColor(Color.BLACK);
    g.drawString(inside ? "Light inside an obstacle - drag it outside" : "Drag the light around two opaque rectangles", 12, 23);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Supplied restart: restore the initial model before the UI uses it.
        restart();
    }
}

void press(int x, int y, int button) {
    lightCallback.accept(x, y);
}

void drag(int x, int y) {
    lightCallback.accept(x, y);
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Shadow Playground");
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
    };
    panel.addMouseListener(mouse);
    panel.addMouseMotionListener(mouse);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Click / drag: move the light   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

java.util.function.BiConsumer<Integer, Integer> lightCallback = lightAction();

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
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `final` — The named value cannot be assigned another value afterwards. A referenced mutable object
///   can still change.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
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
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `public` — Makes this declaration accessible to other code.
/// - `protected` — A visibility keyword used by supplied library callbacks, such as paintComponent.
/// - `super` — In super.paintComponent(g), asks the supplied parent implementation to paint its standard
///   background first.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
