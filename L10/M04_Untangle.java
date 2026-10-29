// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
// </editor-fold>

/// # L10.04 · Untangle
///
/// Untangle a web by dragging numbered nodes until its edges stop crossing. The circles and their
/// connecting lines must agree about where each node really is.
///
/// **Your mission:** A node’s circle swaps x and y while its label and edges use the proper position.
/// Repair the drawing coordinates without moving the puzzle itself.
///
/// **Today you learn:** Rendering turns model coordinates into visible marks. A circle’s top-left corner
/// is offset from its centre, but x must still mean horizontal and y vertical.
///
/// From the course folder containing README.md:
/// - Play: `java L10/M04_Untangle.java`
/// - Check this mission: `java L10/Check.java M04_Untangle.java`
///
/// After the play command, drag numbered nodes to uncross the web, or press R to scramble it again.
/// After your repair: save the file, run the check command above, then play again.
///
/// Need an explanation? Learn · F29 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
void drawNode(Graphics2D g, Point point, int label) {
    g.setColor(new Color(35, 55, 90));
    g.fillOval(point.x - 13, point.y - 13, 26, 26);
    g.setColor(Color.WHITE);
    g.drawString("" + label, point.x - 4, point.y + 4);
}

// <editor-fold defaultstate="expanded" desc="Learn · F29">
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
/// - `point.x` — The model’s horizontal coordinate; it should agree with edge endpoints.
/// - `point.y` — The vertical coordinate, increasing downward on this screen.
/// - `g.fillOval(point.y - 13, point.x - 13, 26, 26)` — fillOval expects left, top, width and height;
///   subtracting 13 centres a 26-pixel circle.
///
/// ### What the prepared part already does
///
/// Dragging changes the real Point values; the supplied crossing check evaluates edges between those
/// points. drawNode only paints one node, so its repair should not change layout or game rules.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// fillOval takes the top-left corner, not the centre.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Subtract the radius from x and y independently.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Use an asymmetric point such as (120,70), not a diagonal point that hides swapped axes.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 420;

int[][] edges = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 7}, {7, 0}, {0, 2}, {2, 4}, {4, 6}, {6, 0}};
Point[] points = new Point[8];
int selected = -1;
void layout(boolean scrambled) {
    int[] order = scrambled ? new int[]{0, 4, 1, 5, 2, 6, 3, 7} : new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    for (int i = 0; i < 8; i++) {
        double a = order[i] * Math.PI / 4;
        points[i] = new Point(300 + (int)(160 * Math.cos(a)), 210 + (int)(160 * Math.sin(a)));
    }
}

/// Supplied restart: restore the initial model before the UI uses it.
void restart() {
    selected = -1;
    layout(true);
}

boolean crosses(int[] a, int[] b) {
    for (int x : a) {
        for (int y : b) {
            if (x == y) {
                return false;
            }
        }
    }
    return Line2D.linesIntersect(points[a[0]].x, points[a[0]].y, points[a[1]].x, points[a[1]].y,
    points[b[0]].x, points[b[0]].y, points[b[1]].x, points[b[1]].y);
}

int crossings() {
    int count = 0;
    for (int i = 0; i < edges.length; i++) {
        for (int j = i + 1; j < edges.length; j++) {
            if (crosses(edges[i], edges[j])) {
                count++;
            }
        }
    }
    return count;
}

void pick(int x, int y) {
    selected = -1;
    for (int i = 0; i < 8; i++) {
        if (points[i].distance(x, y)<15) {
            selected = i;
        }
    }
}

void moveSelected(int x, int y) {
    if (selected < 0) {
        return;
    }
    x = Math.max(18, Math.min(WIDTH - 18, x));
    y = Math.max(38, Math.min(HEIGHT - 18, y));
    for (int i = 0; i < 8; i++) {
        if (i != selected && points[i].distance(x, y)<25) {
            return;
        }
    }
    points[selected].setLocation(x, y);
}

/// Supplied drawing: reads model values; repainting is not another turn.
void draw(Graphics2D g) {
    g.setStroke(new BasicStroke(2));
    for (int i = 0; i < edges.length; i++) {
        boolean crossed = false;
        for (int j = 0; j < edges.length; j++) {
            if (i != j && crosses(edges[i], edges[j])) {
                crossed = true;
            }
        }
        g.setColor(crossed ? new Color(220, 70, 70) : new Color(65, 155, 130));
        var a = points[edges[i][0]];
        var b = points[edges[i][1]];
        g.drawLine(a.x, a.y, b.x, b.y);
    }
    for (int i = 0; i < 8; i++) {
        drawNode(g, points[i], i);
    }
    g.setColor(Color.BLACK);
    int n = crossings();
    g.drawString(n == 0 ? "Untangled! R: scramble again" : "Crossing edge pairs: " + n, 15, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Supplied restart: restore the initial model before the UI uses it.
        restart();
    }
}

void press(int x, int y, int button) {
    pick(x, y);
}

void drag(int x, int y) {
    moveSelected(x, y);
}

void release(int x, int y) {
    selected = -1;
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Untangle");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Drag numbered nodes until every edge is green   R: restart</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

/// Use the play command above to start the program. The check command checks the rule without
/// opening a window. The supplied part supports only the main game or experiment described above.
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
/// - `!=` — Checks that two values differ.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
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

