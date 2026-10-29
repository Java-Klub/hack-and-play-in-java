// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L10.09 · Pong
///
/// A two-player paddle duel already works. Turn it into a training partner you can beat, and find the
/// situations where a very simple opponent surprises you.
///
/// **Your mission:** Make B switch the right paddle between a person and a bot. The bot watches the
/// ball now, moves at 3 pixels per tick toward it, and stays still when the ball is within 8 pixels
/// of its paddle centre. Human controls keep their 5-pixel speed. Predict whether this slower bot
/// will always lose, then play to challenge your prediction.
///
/// **Investigation:** Form a hypothesis about a simple opponent, then build and connect it.
///
/// Space or click starts. W/S controls the left paddle; Up/Down controls a human right paddle. B
/// selects the opponent; R resets. First to five wins.

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Invent a comeback rule: after a goal the trailing player receives a longer paddle, but both sides
/// return to normal at a tie. Decide where scoring ends and the next serve begins before adding your
/// rule. Challenge someone to a five-point match.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Learn · F30">
/// ### Input requests; a tick advances time
///
/// An event can record an intention without changing the whole simulation immediately. A tick
/// performs one bounded update. Keep drawing separate from both.
///
/// ```java
/// int height = 0;
/// int pending = 0;
/// void press() {
///     pending = 2;
/// }
/// void tick() {
///     height += pending;
///     pending = 0;
/// }
/// ```
///
/// After `press()`, height is still 0. One `tick()` changes it to 2 and consumes the request; another
/// tick leaves it at 2. Repeating the input before that tick still records one request, rather than
/// two updates. A supplied timer can call tick, but paused or finished games should reject further
/// advancement. Restart must restore the initial state.
///
/// Predict: from the initial state, what happens after press, tick, press, tick?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Investigator notebook · worked example">
/// ### A tick can use a policy instead of a key
///
/// An input event records a choice; a tick consumes the current choice while moving the model. A bot
/// can calculate a choice at that same boundary without pretending to press keys.
///
/// ```java
/// int direction(double target, double centre) {
///     if (Math.abs(target - centre) <= 2) return 0;
///     return target > centre ? 1 : -1;
/// }
/// ```
///
/// This independent example returns 0 for target 11 and centre 10, but +1 for target 15. Predict
/// target 6. The quiet interval avoids twitching near the target. Multiply a direction by a speed
/// only when updating a position, then clamp that position to the permitted range. A policy decides;
/// the tick moves. A finished-game guard must still run before either human or bot movement.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Look for where rightY changes rather than where the paddle is drawn. Compare the two paddles in
/// one tick.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// botDirection can return -1, 0 or +1. Its inputs are the current ball height and the top of the
/// paddle; add half PADDLE to get its centre.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// When bot is true, use botDirection at speed 3; otherwise keep the right-hand key calculation at
/// speed 5. Preserve clampPaddle, the winner guard and reset behaviour.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program to explore">
void step() {
    if (winner != 0) return;
    leftY = clampPaddle(leftY + (leftDown ? 5 : 0) - (leftUp ? 5 : 0));
    int direction = bot ? botDirection(ballY, rightY) : (rightDown ? 1 : 0) - (rightUp ? 1 : 0);
    rightY = clampPaddle(rightY + direction * (bot ? 3 : 5));
    ballX += velocityX;
    ballY += velocityY;
    if (ballY < RADIUS) {
        ballY = RADIUS;
        velocityY = Math.abs(velocityY);
    }
    if (ballY > HEIGHT - RADIUS) {
        ballY = HEIGHT - RADIUS;
        velocityY = -Math.abs(velocityY);
    }
    if (velocityX < 0 && ballX - RADIUS <= 36 && ballX + RADIUS >= 24
            && ballY + RADIUS >= leftY && ballY - RADIUS <= leftY + PADDLE) {
        bounce(1, leftY);
    }
    if (velocityX > 0 && ballX + RADIUS >= WIDTH - 36 && ballX - RADIUS <= WIDTH - 24
            && ballY + RADIUS >= rightY && ballY - RADIUS <= rightY + PADDLE) {
        bounce(-1, rightY);
    }
    if (ballX < -RADIUS) {
        rightScore++;
        serve(-1);
    } else if (ballX > WIDTH + RADIUS) {
        leftScore++;
        serve(1);
    }
    if (leftScore >= 5) winner = 1;
    if (rightScore >= 5) winner = 2;
}

final int WIDTH = 640, HEIGHT = 400, PADDLE = 80, RADIUS = 7;
double leftY, rightY, ballX, ballY, velocityX, velocityY;
int leftScore, rightScore, winner;
boolean leftUp, leftDown, rightUp, rightDown;

/// Supplied restart: restore the initial model before the UI uses it.
void restart() {
    leftY = rightY = (HEIGHT - PADDLE) / 2.0;
    leftScore = rightScore = winner = 0;
    bot = false;
    clearKeys();
    serve(1);
}

void clearKeys() {
    leftUp = leftDown = rightUp = rightDown = false;
}

void serve(int direction) {
    ballX = WIDTH / 2.0;
    ballY = HEIGHT / 2.0;
    velocityX = direction * 4;
    velocityY = 2.5;
}

double clampPaddle(double y) {
    return Math.max(0, Math.min(HEIGHT - PADDLE, y));
}

void bounce(int direction, double paddleY) {
    ballX = direction > 0 ? 36 + RADIUS : WIDTH - 36 - RADIUS;
    velocityX = direction * 4;
    velocityY = (ballY - paddleY - PADDLE / 2.0) / (PADDLE / 2.0) * 4;
    if (Math.abs(velocityY) < 0.8) velocityY = 0.8;
}

/// Supplied drawing: reads model values; repainting is not another turn.
void draw(Graphics g) {
    g.setColor(new Color(16, 23, 40));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(new Color(54, 65, 84));
    for (int y = 0; y < HEIGHT; y += 24) g.fillRect(WIDTH / 2 - 1, y, 2, 12);
    g.setColor(new Color(71, 230, 202));
    g.fillRect(24, (int) leftY, 12, PADDLE);
    g.setColor(new Color(255, 172, 91));
    g.fillRect(WIDTH - 36, (int) rightY, 12, PADDLE);
    g.setColor(Color.WHITE);
    g.fillOval((int) ballX - RADIUS, (int) ballY - RADIUS, RADIUS * 2, RADIUS * 2);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
    g.drawString(leftScore + "    " + rightScore, WIDTH / 2 - 42, 38);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    g.drawString("W/S: left    Up/Down: human right    B: opponent    R: restart", 32, HEIGHT - 24);
    g.drawString(bot ? "Opponent: Bot (3 px/tick)" : "Opponent: Human (5 px/tick)", 32, HEIGHT - 8);
    if (winner != 0) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        g.drawString((winner == 1 ? "LEFT" : "RIGHT") + " WINS!  Press R", 150, HEIGHT / 2);
    }
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Pong - Paddle Duel");
    boolean[] launched = {false};
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
/// Supplied drawing: reads model values; repainting is not another turn.
            draw(g);
            if (!launched[0]) {
                g.setColor(Color.WHITE);
                g.drawString("Space or click to start", 20, 65);
            }
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        void change(KeyEvent event, boolean pressed) {
            if (pressed && event.getKeyCode() == KeyEvent.VK_SPACE) launched[0] = true;
            switch (event.getKeyCode()) {
                case KeyEvent.VK_W -> leftUp = pressed;
                case KeyEvent.VK_S -> leftDown = pressed;
                case KeyEvent.VK_UP -> rightUp = pressed;
                case KeyEvent.VK_DOWN -> rightDown = pressed;
                case KeyEvent.VK_B -> { if (pressed) toggleBot(); }
                case KeyEvent.VK_R -> { if (pressed) { restart(); launched[0] = false; } }
            }
        }
        @Override public void keyPressed(KeyEvent event) { change(event, true); }
        @Override public void keyReleased(KeyEvent event) { change(event, false); }
    });
    panel.addMouseListener(new MouseAdapter() {
        @Override public void mousePressed(MouseEvent event) {
            launched[0] = true;
            panel.requestFocusInWindow();
        }
    });
    var timer = new Timer(16, event -> {
/// Supplied bounded update: one call means one model step; the active repair is described above.
        if (launched[0]) step();
        panel.repaint();
    });
    frame.addWindowFocusListener(new WindowAdapter() {
        @Override public void windowLostFocus(WindowEvent event) { clearKeys(); }
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent event) { timer.stop(); }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

boolean bot;

void toggleBot() {
    bot = !bot;
    rightUp = rightDown = false;
}

int botDirection(double targetY, double paddleTop) {
    double difference = targetY - (paddleTop + PADDLE / 2.0);
    if (Math.abs(difference) <= 8) return 0;
    return difference > 0 ? 1 : -1;
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
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
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
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
