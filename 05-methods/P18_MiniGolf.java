// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # 05.18 · MiniGolf
///
/// Putt a ball across a small course, use the walls for rebounds and try to enter the hole slowly. Longer
/// pulls should give stronger shots, while friction gradually stops the ball.
///
/// **Your mission · Build and connect a shot:** The ball has no launch power. Implement dragVelocity (100
/// → 8, -25 → -2, 0 → 0),
/// then connect both inputs of applyPull to that calculation: horizontal must supply vx and vertical
/// must supply vy. The prepared shot limiter stays unchanged.
///
/// **Today you learn:** Numeric scale and units: a distance must be converted into a speed with the
/// intended ratio. Check a small positive example, a negative one and zero before judging the whole
/// moving scene.
///
/// Run the program, pull backwards from the resting ball and release for a putt; let it stop
/// before the next shot, or press R to restart.
double dragVelocity(double distance) {
    return distance / 12.5;
}

void applyPull(double horizontal, double vertical) {
    vx = dragVelocity(horizontal);
    vy = dragVelocity(vertical);
}

// <editor-fold defaultstate="collapsed" desc="Recall · F04">
/// Arithmetic uses the operand types at the moment an operation runs. When both operands of `/` are
/// whole numbers, Java discards the fractional part. Storing that result in a `double` afterwards
/// does not recover it.
///
/// These example statements belong inside a called method.
///
/// ```java
/// int pages = 7;
/// double first = pages / 2;
/// double second = pages / 2.0;
/// double third = (double) pages / 2;
/// ```
///
/// Predict the three values: `3.0`, `3.5`, `3.5`. The cast `(double)` converts this use of `pages`
/// before division; it does not change the variable’s declared type. Parentheses group calculations,
/// while `*` multiplies and `/` divides. Check a negative input and zero as well as a positive one.
/// Name the units: a distance and a speed are different quantities even when both use `double`.
///
/// Now predict all three results when pages is nine.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Two calculations, one rule
///
/// `double distance` receives one signed distance; `return` hands its converted speed back.
/// `applyPull(horizontal, vertical)` receives two different values. Its two assignments must keep
/// their roles: horizontal → vx and vertical → vy. shoot already calls applyPull when you release the
/// mouse, then limits excessive speed and counts the stroke. You are building the conversion and its
/// connection, not the timer or the mouse handler.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// distance is already a double. Compare distance / 125 and distance / 125.0: both preserve fractions
/// and both use the same scale. Getting eight speed units from a hundred pixels requires a different
/// scale. applyPull must then actually store the results in vx and vy. The prepared shoot already
/// limits excessive power; do not add a second limit here.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Follow the values · predict a case">
/// The required conversion is distance 100 → speed 8, distance -25 → speed -2, and zero → zero. Since
/// distance is a double, changing 125 to 125.0 would not change the old scale. applyPull receives
/// horizontal and vertical distances separately. A call can return a value directly into an
/// assignment, such as `vx = dragVelocity(horizontal);`. No printing is involved.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Make a gentle and an exaggerated course by changing only the conversion scale. Try the same pull
/// in both. Find a setting that lets a friend reach the hole without bouncing off a wall, then
/// restore the original scale if you want the original check to pass.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Work out the speed for one pixel, then use the same scale for both directions.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// applyPull needs two separate calls, each with its matching parameter.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Return distance / 12.5 from dragVelocity. In applyPull assign dragVelocity(horizontal) to vx and
/// dragVelocity(vertical) to vy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 420;

double x, y, vx, vy, aimX, aimY;
boolean aiming, won;
int strokes;
void restart() {
    x = 90;
    y = 110;
    vx = vy = 0;
    strokes = 0;
    aiming = won = false;
}

boolean resting() {
    return Math.hypot(vx, vy)<.05;
}

void begin(int xx, int yy) {
    if (!won && resting() && Math.hypot(xx - x, yy - y)<22) {
        aiming = true;
        aimX = xx;
        aimY = yy;
    }
}

void shoot(int xx, int yy) {
    if (!aiming) {
        return;
    }
    aiming = false;
    applyPull(x - xx, y - yy);
    double speed = Math.hypot(vx, vy);
    if (speed > 10) {
        vx *= 10 / speed;
        vy *= 10 / speed;
    }
    if (speed>.1) {
        strokes++;
    }
}

void step() {
    if (won || resting()) {
        return;
    }
    x += vx;
    y += vy;
    vx *= .985;
    vy *= .985;
    if (x < 28) {
        x = 28;
        vx = Math.abs(vx) * .8;
    }
    if (x > 572) {
        x = 572;
        vx = -Math.abs(vx) * .8;
    }
    if (y < 28) {
        y = 28;
        vy = Math.abs(vy) * .8;
    }
    if (y > 372) {
        y = 372;
        vy = -Math.abs(vy) * .8;
    }
    if (Math.hypot(x - 500, y - 310)<12 && Math.hypot(vx, vy)<1.5) {
        x = 500;
        y = 310;
        vx = vy = 0;
        won = true;
    }
    if (resting()) {
        vx = vy = 0;
    }
}

void draw(Graphics2D g) {
    g.setColor(new Color(40, 120, 70));
    g.fillRect(20, 20, 560, 360);
    g.setStroke(new BasicStroke(5));
    g.setColor(new Color(90, 60, 40));
    g.drawRect(20, 20, 560, 360);
    g.setColor(Color.BLACK);
    g.fillOval(486, 296, 28, 28);
    g.setColor(Color.YELLOW);
    g.drawLine(500, 310, 500, 270);
    g.fillPolygon(new int[]{500, 520, 500}, new int[]{270, 280, 290}, 3);
    g.setColor(Color.WHITE);
    g.fillOval((int)x - 8, (int)y - 8, 16, 16);
    if (aiming) {
        g.setStroke(new BasicStroke(2));
        g.drawLine((int)x, (int)y, (int)aimX, (int)aimY);
    }
    g.setColor(Color.BLACK);
    g.drawString(won ? "Holed in " + strokes + " strokes!" : "Strokes: " + strokes + " - enter the hole slowly", 20, 408);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    begin(x, y);
}

void drag(int x, int y) {
    if (aiming) {
        aimX = x;
        aimY = y;
    }
}

void release(int x, int y) {
    shoot(x, y);
}

void showWindow() {
    var frame = new JFrame("Mini Golf");
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
    var timer = new Timer(20, e -> {
        step();
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent e) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Drag backward from the ball; release: putt   R: restart</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

void main() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("This game needs a graphical display; model checks still work headlessly.");
        return;
    }
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}

/// Prepared entry points: main, restart, draw, showWindow.
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
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
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
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
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
