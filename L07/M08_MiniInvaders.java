// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L07.08 · MiniInvaders
///
/// Clear a descending alien formation with one bullet at a time. Aim carefully: a good hit should not
/// secretly become a drilling laser.
///
/// **Your mission:** A bullet survives a hit and can score again on later updates. Repair the hit branch
/// of step() so one shot cannot destroy multiple aliens.
///
/// **Today you learn:** Invariants: one active shot has one lifetime and at most one scored hit. Updating
/// enemy state and projectile state must preserve that relationship.
///
/// Run the program, steer with Left and Right, and hold Space to fire again whenever the
/// previous bullet has finished.
void step() {
    if (!state.equals("PLAY")) {
        return;
    }
    shipX = Math.max(16, Math.min(WIDTH - 16, shipX + (right ? 5 : 0) - (left ? 5 : 0)));
    if (fire && !bulletActive) {
        bulletActive = true;
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
                aliens[row][column] = false;
                score++;
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

// <editor-fold defaultstate="collapsed" desc="Recall · F25">
/// ### Invariant: a promise after every action
///
/// An invariant is a relationship that must remain true across accepted actions. Some actions move a
/// quantity between places rather than creating more. Validate the complete request before making
/// changes, then apply it once. Rejection should preserve the earlier state.
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
/// The containers become three and five; the printed total remains eight because the transfer
/// balances. Now consider nine units: what must remain unchanged? Explain why moving only one side
/// would violate the total even if both individual values looked legal.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `bulletActive` — A boolean records whether a shot still exists, independently of its coordinates.
/// - `aliens[row][column] = false` — Removes the hit enemy from the live formation.
/// - `score++` — Awards one hit point; the same projectile must not earn it twice.
/// - `break hit` — Leaves the labelled hit-search loop for this update; it does not end the bullet’s
///   lifetime.
///
/// ### What the prepared part already does
///
/// The rest of step() moves the ship, advances the formation and bullet, and checks victory or invasion.
/// A Swing timer calls it repeatedly; countAliens() reads the surviving enemy grid.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The labelled break stops only this frame’s search.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Without changing bulletActive, the same shot survives into later frames.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Set bulletActive=false inside the successful hit branch before leaving the search.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 420, SHIP_Y = 388;
final boolean[][] aliens = new boolean[3][5];
double shipX, formationX, formationY, speedX, bulletX, bulletY;
boolean left, right, fire, bulletActive;
int score;
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
