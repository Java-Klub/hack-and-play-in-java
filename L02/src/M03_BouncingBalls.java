// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;
// </editor-fold>

/// # L02.03 · BouncingBalls
///
/// Throw several coloured balls into a room and watch gravity and wall rebounds shape their motion.
/// You can compare gentle and fast throws.
///
/// **Your mission:** Balls lose their horizontal speed at a side wall instead of bouncing back. Repair
/// bounceSpeed so a wall retains three quarters of the incoming speed magnitude.
///
/// **Today you learn:** Fractional arithmetic: grouping can make an integer division happen before a
/// multiplication. Compare an exact whole-number ratio with a fractional ratio, then predict the retained
/// speed of a ball arriving at speed 4.
///
/// Use Run File, drag sideways to throw a ball at a wall and watch its rebound; Space pauses the room and
/// R empties it.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F04 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
double bounceSpeed(double speed) {
    return Math.abs(speed) * (3 / 4);
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
/// ## Read these particular lines
///
/// - `double bounceSpeed(double speed)` — A calculation that receives a signed speed and returns the
///   nonnegative magnitude after a bounce.
/// - `Math.abs(speed)` — A supplied calculation of absolute value: for example, -4 becomes 4. The dot
///   selects abs and parentheses pass speed to it.
/// - `*` — Multiply the magnitude by the fraction retained by the wall.
/// - `(3 / 4)` — The parentheses evaluate integer 3 / 4 first, which discards its fractional part.
/// - `return` — Return the magnitude; the caller already chooses the new positive or negative direction.
///
/// ### What the prepared part already does
///
/// add creates and limits a throw. step calls your rule at side and top walls, then supplies gravity and
/// a separate floor response. draw paints the stored balls; the mouse code creates throws.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// 3 / 4 with integer operands is zero.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Math.abs is already supplied to remove the old direction sign.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Multiply by 3.0 / 4, or by 0.75.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 640, HEIGHT = 440;

record Ball(double x, double y, double vx, double vy, Color colour) {
}

ArrayList<Ball> balls = new ArrayList<>();
Random random = new Random(9);
int startX, startY, aimX, aimY;
boolean aiming, paused;
void restart() {
    balls.clear();
    aiming = false;
    paused = true;
}

void add(double x, double y, double vx, double vy) {
    if (balls.size() >= 60) {
        return;
    }
    double speed = Math.hypot(vx, vy);
    if (speed > 12) {
        vx *= 12 / speed;
        vy *= 12 / speed;
    }
    balls.add(new Ball(Math.max(10, Math.min(WIDTH - 10, x)), Math.max(10, Math.min(HEIGHT - 10, y)), vx, vy, Color.getHSBColor(random.nextFloat(), .7f, .85f)));
    paused = false;
}

void step() {
    if (paused) {
        return;
    }
    for (int i = 0; i < balls.size(); i++) {
        var b = balls.get(i);
        double vx = b.vx * .998, vy = b.vy + .15, x = b.x + vx, y = b.y + vy;
        if (x < 10) {
            x = 10;
            vx = bounceSpeed(vx);
        }
        if (x > WIDTH - 10) {
            x = WIDTH - 10;
            vx = -bounceSpeed(vx);
        }
        if (y < 10) {
            y = 10;
            vy = bounceSpeed(vy);
        }
        if (y > HEIGHT - 10) {
            y = HEIGHT - 10;
            vy = -Math.abs(vy) * .72;
            vx *= .97;
            if (Math.abs(vy)<.6) {
                vy = 0;
            }
        }
        balls.set(i, new Ball(x, y, vx, vy, b.colour));
    }
}

void draw(Graphics2D g) {
    g.setColor(new Color(240, 243, 250));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    for (var b : balls) {
        g.setColor(b.colour);
        g.fillOval((int)b.x - 10, (int)b.y - 10, 20, 20);
    }
    if (aiming) {
        g.setColor(Color.GRAY);
        g.drawLine(startX, startY, aimX, aimY);
    }
    g.setColor(Color.BLACK);
    g.drawString("Balls: " + balls.size() + " / 60   " + (paused ? "Paused" : "Gravity on") + "   Drag to throw a ball; Space pauses; R clears", 12, 23);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code == KeyEvent.VK_SPACE) {
        paused = !paused;
    }
}

void press(int x, int y, int button) {
    startX = aimX = x;
    startY = aimY = y;
    aiming = true;
}

void drag(int x, int y) {
    aimX = x;
    aimY = y;
}

void release(int x, int y) {
    if (aiming) {
        add(startX, startY, (x - startX) * .08, (y - startY) * .08);
        aiming = false;
    }
}

void showWindow() {
    var frame = new JFrame("Bouncing Balls");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Drag: add and throw a ball   Space: pause   R: clear room</div></html>", SwingConstants.CENTER);
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
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
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
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>

