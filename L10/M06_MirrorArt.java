// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L10.06 · MirrorArt
///
/// Draw one stroke and let a ring of reflected copies turn it into a rosette. Changing the number of
/// mirror axes can make the same gesture look completely different.
///
/// **Your mission:** The copies slide toward the left because a rotated x coordinate is never returned to
/// the canvas centre. Repair that conversion while preserving the rotation and reflection.
///
/// **Today you learn:** A coordinate transform first works relative to a chosen centre and then returns
/// to screen coordinates. The supplied trigonometry handles rotation; check which centre offsets are
/// still missing.
///
/// Run the program, then drag a stroke to inspect its reflections, use Up/Down to change mirror
/// axes and R to clear and restart.
Point2D.Double transform(int x, int y, int copy, boolean reflect) {
    double dx = x - WIDTH / 2, dy = (y - HEIGHT / 2) * (reflect ? -1 : 1), a = 2 * Math.PI * copy / axes;
    return new Point2D.Double(dx * Math.cos(a) - dy * Math.sin(a), HEIGHT / 2 + dx * Math.sin(a) + dy * Math.cos(a));
}

// <editor-fold defaultstate="collapsed" desc="Recall · F29">
/// ### Rendering: translate state into pixels
///
/// Rendering reads the model and draws what it describes. Screen x increases rightward; screen y
/// increases downward. Drawing methods often accept the upper-left corner and a size, rather than a
/// centre.
///
/// ```java
/// int tileX = 3;
/// int tileY = 2;
/// int size = 10;
/// g.fillRect(tileX * size, tileY * size, size, size);
/// ```
///
/// Here `g` is the supplied `Graphics2D` drawing tool. This paints a 10-by-10 square with upper-left
/// corner `(30, 20)`. The model coordinates stay `(3, 2)`. Repainting should not move objects or
/// advance a round: a window may request painting many times.
///
/// Predict: if only tileY becomes 4, which screen coordinate changes, and by how much?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `x - WIDTH / 2` — Moves the origin from the left edge to the canvas centre.
/// - `reflect ? -1 : 1` — Chooses whether the centred vertical distance is mirrored.
/// - `new Point2D.Double` — Returns a new screen point; it does not move the original stroke.
/// - `HEIGHT / 2 +` — The vertical result already restores its screen-centre offset.
///
/// ### What the prepared part already does
///
/// stroke calls the editable transform for each reflected copy and stores the strokes on one
/// canvas. Up/Down changes axes; R clears the picture and restores the starting experiment.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Coordinates are first made relative to the centre.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Rotation works in that relative space.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// The returned point must add WIDTH/2 to x and HEIGHT/2 to y.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 640, HEIGHT = 440;

BufferedImage canvas;
int axes, lastX, lastY;
boolean drawing;
String message;
void clear() {
    var g = canvas.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.dispose();
}

/// Supplied restart: restore the initial model before the UI uses it.
void restart() {
    canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    clear();
    axes = 6;
    drawing = false;
    message = "Draw with the mouse";
}

void adjust(int amount) {
    axes = Math.max(2, Math.min(12, axes + amount));
}

void stroke(int x1, int y1, int x2, int y2) {
    var g = canvas.createGraphics();
    g.setColor(new Color(40, 80, 170));
    g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    for (int i = 0; i < axes; i++) {
        for (boolean reflected : new boolean[]{false, true}) {
            g.draw(new Line2D.Double(transform(x1, y1, i, reflected), transform(x2, y2, i, reflected)));
        }
    }
    g.dispose();
}


/// Supplied drawing: reads model values; repainting is not another turn.
void draw(Graphics2D g) {
    g.drawImage(canvas, 0, 0, null);
    g.setColor(Color.GRAY);
    g.drawLine(WIDTH / 2 - 4, HEIGHT / 2, WIDTH / 2 + 4, HEIGHT / 2);
    g.drawLine(WIDTH / 2, HEIGHT / 2 - 4, WIDTH / 2, HEIGHT / 2 + 4);
    g.setColor(Color.BLACK);
    g.drawString("Axes: " + axes + "   " + message, 10, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Supplied restart: restore the initial model before the UI uses it.
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_UP -> adjust(1);
            case KeyEvent.VK_DOWN -> adjust(-1);
        }
    }
}

void press(int x, int y, int button) {
    drawing = true;
    lastX = x;
    lastY = y;
}

void drag(int x, int y) {
    if (drawing) {
        stroke(lastX, lastY, x, y);
        lastX = x;
        lastY = y;
    }
}

void release(int x, int y) {
    drawing = false;
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Mirror Art");
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
        @Override public void mouseReleased(MouseEvent e) {
            release(e.getX(), e.getY());
            panel.repaint();
        }
    };
    panel.addMouseListener(mouse);
    panel.addMouseMotionListener(mouse);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Drag: draw   Up/Down: axes   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
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
/// - `double` — A type for approximate fractional numbers. Java uses a decimal point.
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
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `++` — Increases a numeric value by one.
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
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
