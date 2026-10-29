// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L07.08 · MiniInvaders
///
/// Your ship already fights fairly: one shot destroys one alien. Hack the weapon into a controlled
/// piercing pulse that can cut through two targets, but never drill through the entire formation.
///
/// **Your mission:** Make each newly fired pulse survive its first distinct hit and end after its
/// second. Each alien scores once; a spent pulse scores nothing, and a new shot starts with a fresh
/// two-hit allowance. Keep movement, aiming, win/loss and restart working. Observe the normal one-hit
/// lifetime before locating how a shot is born and ends.
///
/// **Today you learn:** Replace one invariant with another: a pulse has a finite hit allowance, and
/// consuming that allowance must agree with the projectile lifetime.
///
/// Steer with Left/Right and fire with Space; R restarts. Aim at a vertical column. Before the hack
/// each pulse stops at the first alien; afterwards watch the same pulse remove a second alien and then
/// disappear.

// <editor-fold defaultstate="expanded" desc="Program · explore the rules">
void step() {
    if (!state.equals("PLAY")) {
        return;
    }
    shipX = Math.max(16, Math.min(WIDTH - 16, shipX + (right ? 5 : 0) - (left ? 5 : 0)));
    if (fire && !bulletActive) {
        bulletActive = true;
        hitsLeft = 2;
        bulletX = shipX;
        bulletY = SHIP_Y - 14;
    }
    if (bulletActive) {
        bulletY -= 7;
        if (bulletY < -10) {
            bulletActive = false;
        }
    }
    int minColumn = 5, maxColumn = -1;
    for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 5; column++) {
            if (!aliens[row][column]) {
                continue;
            }
            minColumn = Math.min(minColumn, column);
            maxColumn = Math.max(maxColumn, column);
        }
    }
    if (maxColumn == -1) {
        state = "YOU WIN";
        return;
    }
    formationX += speedX;
    double minimum = -minColumn * 60, maximum = WIDTH - maxColumn * 60 - 28;
    if (formationX < minimum || formationX > maximum) {
        formationX = Math.max(minimum, Math.min(maximum, formationX));
        speedX = -speedX;
        formationY += 18;
    }
    hit : for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 5; column++) {
            double x = formationX + column * 60, y = formationY + row * 34;
            if (aliens[row][column] && bulletActive && bulletX >= x && bulletX <= x + 28
            && bulletY <= y + 18 && bulletY + 10 >= y) {
                hitAlien(row, column);
                break hit;
            }
        }
    }
    if (countAliens() == 0) {
        state = "YOU WIN";
    }
    for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 5; column++) {
            if (aliens[row][column] && formationY + row * 34 + 18 >= SHIP_Y - 12) {
                state = "INVASION";
            }
        }
    }
}

void hitAlien(int row, int column) {
    if (!bulletActive || hitsLeft <= 0 || !aliens[row][column]) return;
    aliens[row][column] = false;
    score++;
    hitsLeft--;
    if (hitsLeft == 0) bulletActive = false;
}

final int WIDTH = 600, HEIGHT = 420, SHIP_Y = 388;
final boolean[][] aliens = new boolean[3][5];
double shipX, formationX, formationY, speedX, bulletX, bulletY;
boolean left, right, fire, bulletActive;
int score;
int hitsLeft;
String state;

void clearKeys() {
    left = right = fire = false;
}

/// restart(): Restores the starting Mini Invaders model so this toy can be tried again.
void restart() {
    for (var row : aliens) {
        java.util.Arrays.fill(row, true);
    }
    shipX = WIDTH / 2.0;
    formationX = 60;
    formationY = 45;
    speedX = 1.2;
    bulletActive = false;
    score = 0;
    hitsLeft = 0;
    state = "PLAY";
    clearKeys();
}

/// countAliens(): Counts only currently alive cells, independent of the score counter.
int countAliens() {
    int count = 0;
    for (var row : aliens) {
        for (boolean alive : row) {
            if (alive) {
                count++;
            }
        }
    }
    return count;
}

/// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
void draw(Graphics g) {
    g.setColor(new Color(12, 20, 34));
    g.fillRect(0, 0, WIDTH, HEIGHT + 42);
    g.setColor(new Color(39, 51, 72));
    for (int x = 15; x < WIDTH; x += 47) {
        for (int y = 15; y < HEIGHT; y += 61) {
            g.fillRect(x, y, 2, 2);
        }
    }
    g.setColor(new Color(130, 237, 151));
    for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 5; column++) {
            if (!aliens[row][column]) {
                continue;
            }
            int x = (int) formationX + column * 60, y = (int) formationY + row * 34;
            g.fillRect(x, y, 28, 18);
            g.clearRect(x + 6, y + 5, 4, 4);
            g.clearRect(x + 18, y + 5, 4, 4);
        }
    }
    g.setColor(new Color(82, 201, 250));
    g.fillPolygon(new int[]{(int) shipX - 15, (int) shipX, (int) shipX + 15},
    new int[]{SHIP_Y + 12, SHIP_Y - 12, SHIP_Y + 12}, 3);
    if (bulletActive) {
        g.setColor(new Color(255, 222, 93));
        g.fillRect((int) bulletX - 2, (int) bulletY, 4, 10);
    }
    g.setColor(Color.WHITE);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
    g.drawString("Hits: " + score + "/15    " + state, 14, HEIGHT + 18);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Left / Right: move     Space: fire     R: restart", 14, HEIGHT + 36);
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; timers stop on
/// window close.
void showWindow() {
    var frame = new JFrame("Mini Invaders - Last Row");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT + 42));
    var status = new JLabel("Left / Right / Space: start   Clear all aliens before they descend   R: restart");
    frame.add(status, BorderLayout.NORTH);
    panel.setFocusable(true);
    boolean[] started = {false};
    panel.addKeyListener(new KeyAdapter() {
        void change(KeyEvent event, boolean pressed) {
            if (pressed && (event.getKeyCode() == KeyEvent.VK_LEFT
                    || event.getKeyCode() == KeyEvent.VK_RIGHT || event.getKeyCode() == KeyEvent.VK_SPACE)) {
                started[0] = true;
            }
            switch (event.getKeyCode()) {
                case KeyEvent.VK_LEFT -> left = pressed;
                case KeyEvent.VK_RIGHT -> right = pressed;
                case KeyEvent.VK_SPACE -> fire = pressed;
                case KeyEvent.VK_R -> {
                    if (pressed) {
                        restart();
                        started[0] = false;
                    }
                }
            }
        }
        @Override public void keyPressed(KeyEvent event) {
            change(event, true);
        }
        @Override public void keyReleased(KeyEvent event) {
            change(event, false);
        }
    });
    var timer = new Timer(20, event -> {
        if (started[0]) {
            step();
        }
        panel.repaint();
    });
    frame.addWindowFocusListener(new WindowAdapter() {
        @Override public void windowLostFocus(WindowEvent event) {
            clearKeys();
        }
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent event) {
            timer.stop();
        }
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

/// main(): Starts the model, then schedules the supplied window on Swing’s event thread.
void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>


// <editor-fold defaultstate="collapsed" desc="Recall · F25">
/// ### An invariant survives each action
///
/// An invariant is a promise about valid state. It is useful before and after a move, not merely when
/// the game ends. Think of moving glow stones between two jars:
///
/// ```java
/// int violet = 6;
/// int gold = 2;
/// int amount = 3;
/// if (amount >= 0 && amount <= violet) {
///     violet -= amount;
///     gold += amount;
/// }
/// IO.println(violet + gold);
/// ```
///
/// Trace: `(6, 2)` becomes `(3, 5)`, and the printed total is still 8. The two assignments belong to
/// one accepted action. Changing only one jar would silently create or destroy stones. A request for 9
/// fails the guard and changes neither jar.
///
/// Write the promise before changing the rule: a total stays constant, a resource stays non-negative,
/// or a shape stays outside a wall. Then list every field that one action must change together. Predict
/// the two jar values for amount 0 and amount 7. An invariant describes the intended world; it does not
/// forbid inventing a different, clearly stated rule in your own variant.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ### Observe, then trace the flow
///
/// Treat a projectile as a small life story: creation, movement, accepted contact, and expiry. The
/// score is a consequence of a real contact, not a timer tick. A removed target cannot be hit again.
/// First locate those events from their visible effects; then decide which state must travel with the
/// current shot. A counter can express a finite allowance without changing the formation geometry.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Experiment and replay">
/// Use the same column for a before/after comparison. Record whether the pulse survives one hit, two
/// hits, and a miss. Check that the score grows once per removed alien and that restarting does not
/// carry a half-used pulse into the next battle.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// One immortal boolean is not enough to express exactly two contacts. Describe the allowance before
/// and after each accepted hit.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Find the input-to-shot creation branch and the alien-contact action. A fresh allowance belongs at
/// creation, while a contact consumes it.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// The shot is created in step and contact is handled by hitAlien. Add a per-shot counter, initialise
/// it for each new shot, decrease only for a live target, and end the bullet at zero. Reset that state
/// on restart.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Make a third-hit charge that costs a long pause before firing again. Decide how the player can see
/// the tradeoff instead of merely making the weapon stronger.
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
