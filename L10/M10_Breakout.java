// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L10.10 · Breakout
///
/// The brick game is already playable. Make a practice edition for learning difficult rebounds, where
/// one bad moment does not erase the wall you have chipped away.
///
/// **Your mission:** Turn the normal three-attempt game into unlimited practice. A miss must serve
/// another ball forever while keeping cleared bricks and score. Restart still restores the whole
/// wall; clearing the wall still wins and stops motion. A very large attempt count is not the rule:
/// remove the loss dependency and make the display honest.
///
/// **Investigation:** Hack a working game rule by tracing every consequence of a miss.
///
/// Space or click launches; move the mouse to aim the bat. Deliberately miss several times before the
/// hack, then repeat afterwards. R rebuilds the wall and waits for a fresh launch.

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Author a brick mosaic by choosing which entries of bricks begin true. Make a pattern with at least
/// one deliberate gap and update the win target to the number you actually placed. Can a friend clear
/// your design?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Recall · F30">
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
/// ### State is a graph of permitted transitions
///
/// A stopped round is a state, not merely a message drawn on screen. Read what a collision changes,
/// which later guard observes it, and what restart restores.
///
/// ```java
/// String state = "RUN";
/// void finish() { state = "DONE"; }
/// void tick() {
///     if (!state.equals("RUN")) return;
///     IO.println("Advance");
/// }
/// ```
///
/// tick prints Advance before finish, but not after it. Predict finish, tick, tick. Keeping a win
/// state while removing a loss transition does not mean deleting the shared stopped-state guard.
/// Write three promises before changing the game: what a miss preserves, what a win freezes, and what
/// restart recreates. Test them separately; changing the visible label alone changes none of the
/// transitions.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Find the branch reached when the ball is below the room, then follow every value changed there
/// into drawing and restart.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The miss branch in step changes lives and may set GAME OVER. serve already prepares one new ball
/// without rebuilding bricks.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Use serve for each miss, keep the PLAY guard and YOU WIN transition, and remove the obsolete lives
/// state and attempt display. Verify restart and the last brick.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program to explore">
void step() {
    if (!state.equals("PLAY")) return;
    double previousX = ballX, previousY = ballY;
    ballX += velocityX;
    ballY += velocityY;
    if (ballX < RADIUS || ballX > WIDTH - RADIUS) {
        ballX = Math.max(RADIUS, Math.min(WIDTH - RADIUS, ballX));
        velocityX = -velocityX;
    }
    if (ballY < RADIUS) {
        ballY = RADIUS;
        velocityY = Math.abs(velocityY);
    }
    if (velocityY > 0 && previousY + RADIUS <= BAT_Y && ballY + RADIUS >= BAT_Y
            && ballX + RADIUS >= batX && ballX - RADIUS <= batX + BAT_WIDTH) {
        ballY = BAT_Y - RADIUS;
        velocityY = -Math.abs(velocityY);
        velocityX = (ballX - batX - BAT_WIDTH / 2.0) / (BAT_WIDTH / 2.0) * 4;
    }
    hit: for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 6; column++) {
            int x = 32 + column * 96, y = 40 + row * 28;
            if (!bricks[row][column] || ballX + RADIUS < x || ballX - RADIUS > x + 88
                    || ballY + RADIUS < y || ballY - RADIUS > y + 20) continue;
            bricks[row][column] = false;
            score++;
            if (previousY + RADIUS <= y || previousY - RADIUS >= y + 20) {
                velocityY = -velocityY;
                ballY = previousY < y ? y - RADIUS : y + 20 + RADIUS;
            } else {
                velocityX = -velocityX;
                ballX = previousX < x ? x - RADIUS : x + 88 + RADIUS;
            }
            break hit;
        }
    }
    if (score == 18) state = "YOU WIN";
    if (ballY > HEIGHT + RADIUS) {
        lives--;
        if (lives == 0) state = "GAME OVER";
        else serve();
    }
}

final int WIDTH = 640, HEIGHT = 400, RADIUS = 7, BAT_WIDTH = 90, BAT_Y = 370;
final boolean[][] bricks = new boolean[3][6];
double ballX, ballY, velocityX, velocityY;
int batX, lives, score;
String state;

/// Supplied restart: restore the initial model before the UI uses it.
void restart() {
    for (var row : bricks) java.util.Arrays.fill(row, true);
    batX = (WIDTH - BAT_WIDTH) / 2;
    lives = 3;
    score = 0;
    state = "PLAY";
    serve();
}

void serve() {
    ballX = WIDTH / 2.0;
    ballY = 270;
    velocityX = 3.2;
    velocityY = -3.5;
}

void aim(int mouseX) {
    batX = Math.max(0, Math.min(WIDTH - BAT_WIDTH, mouseX - BAT_WIDTH / 2));
}

/// Supplied drawing: reads model values; repainting is not another turn.
void draw(Graphics g) {
    g.setColor(new Color(21, 22, 42));
    g.fillRect(0, 0, WIDTH, HEIGHT + 42);
    Color[] colours = {new Color(255, 113, 133), new Color(255, 191, 92), new Color(93, 217, 211)};
    for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 6; column++) {
            if (!bricks[row][column]) continue;
            g.setColor(colours[row]);
            g.fillRoundRect(32 + column * 96, 40 + row * 28, 88, 20, 6, 6);
        }
    }
    g.setColor(Color.WHITE);
    g.fillRoundRect(batX, BAT_Y, BAT_WIDTH, 12, 8, 8);
    g.fillOval((int) ballX - RADIUS, (int) ballY - RADIUS, RADIUS * 2, RADIUS * 2);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
    g.drawString("Bricks: " + score + "/18    Attempts: " + lives + "    " + state, 16, HEIGHT + 18);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Mouse: move paddle     R: restart", 16, HEIGHT + 36);
    if (!state.equals("PLAY")) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        g.drawString(state + " - press R", 160, 230);
    }
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Breakout - Brick Bash");
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
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT + 42));
    panel.setFocusable(true);
    panel.addMouseMotionListener(new MouseMotionAdapter() {
        @Override public void mouseMoved(MouseEvent event) { aim(event.getX()); }
        @Override public void mouseDragged(MouseEvent event) { aim(event.getX()); }
    });
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_SPACE) launched[0] = true;
            if (event.getKeyCode() == KeyEvent.VK_R) { restart(); launched[0] = false; }
        }
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
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
