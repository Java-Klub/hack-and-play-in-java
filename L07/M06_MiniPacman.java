// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L07.06 · MiniPacman
///
/// Collect every dot while a ghost chases you through the maze. Reaching the last dot and meeting the
/// ghost can happen in the same step.
///
/// **Your mission:** Build afterContact(): capture returns CAUGHT even on the last dot; otherwise zero
/// remaining dots returns WON, and every other situation returns PLAY. The unfinished transition
/// currently keeps every round alive.
///
/// **Today you learn:** Describe one state transition with an explicit priority when conditions
/// overlap.
///
/// Run the program and press an arrow key to start moving through the maze, steering toward
/// dots while avoiding the ghost.
enum RoundState {
    PLAY, CAUGHT, WON
}

RoundState afterContact(boolean caught, int dotsLeft) {
    if (caught) return RoundState.CAUGHT;
    if (dotsLeft == 0) return RoundState.WON;
    return RoundState.PLAY;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F21">
/// ### Give a state a name
///
/// An enum declares a closed set of names. A variable of that type holds one name at a time, so an
/// object cannot be both OPEN and LOCKED in the same variable.
///
/// ```java
/// enum Door { LOCKED, OPEN }
/// Door afterKey(Door before, boolean hasKey) {
///     if (before == Door.LOCKED && hasKey) return Door.OPEN;
///     return before;
/// }
/// ```
///
/// Trace `afterKey(Door.LOCKED, true)`: both parts of `&&` are true, so OPEN is returned. With no key,
/// the original LOCKED is returned. Notice that calling this method alone does not store the result.
/// The caller can choose `door = afterKey(door, hasKey)` when a transition is meant to happen.
///
/// Draw a two-circle diagram, LOCKED → OPEN, with “key” on the arrow. Ask what should happen to an
/// already open door. Predict the return value for OPEN with a key, and explain which return is
/// reached. In a game, state priorities are rules you must state explicitly, not properties supplied by
/// enum.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read this part
///
/// RoundState lists three mutually exclusive outcomes. `caught` and `dotsLeft` are supplied facts for
/// the same step, not separate rounds. Evaluate the priority first. The caller stores the returned
/// state and the prepared timer stops advancing a finished round.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// List the outcomes for caught/not caught crossed with zero/nonzero dots.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The simultaneous capture-and-last-dot case decides the order of your conditions.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Return CAUGHT for capture first, WON for no dots next, and PLAY otherwise.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Make a clearly labelled alternate ending where the last dot wins a tie. Compare the two endings on
/// the same last-dot capture and explain which feels fairer.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
record Cell(int x, int y) {
}

final String[] map = {
"#############", "#...........#", "#.###...###.#",
"#...#...#...#", "#...#...#...#", "#...........#",
"#.###...###.#", "#...........#", "#############"
};
final int TILE = 30;
final boolean[][] pellets = new boolean[map.length][map[0].length()];
final Random random = new Random();
int playerX, playerY, ghostX, ghostY, dx, dy, wantedX, wantedY, remaining, ticks;
boolean started;

RoundState state;

boolean open(int x, int y) {
    return y >= 0 && y < map.length && x >= 0 && x < map[y].length() && map[y].charAt(x) != '#';
}

/// restart(): Restores the starting Mini Pacman model so this toy can be tried again.
void restart() {
    playerX = playerY = 1;
    ghostX = 11;
    ghostY = 7;
    dx = dy = wantedX = wantedY = remaining = ticks = 0;
    started = false;
    state = RoundState.PLAY;
    for (int y = 0; y < map.length; y++) {
        for (int x = 0; x < map[y].length(); x++) {
            pellets[y][x] = open(x, y) && (x != playerX || y != playerY);
            if (pellets[y][x]) {
                remaining++;
            }
        }
    }
}

void direction(int x, int y) {
    if (Math.abs(x) + Math.abs(y) != 1) {
        return;
    }
    wantedX = x;
    wantedY = y;
    started = true;
}

/// moveGhost(): Supplied greedy legal-neighbour search pursues the player without crossing walls.
void moveGhost() {
    var choices = new ArrayList<Cell>();
    int best = Integer.MAX_VALUE;
    for (var delta : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
        int x = ghostX + delta[0], y = ghostY + delta[1];
        if (!open(x, y)) {
            continue;
        }
        int distance = Math.abs(x - playerX) + Math.abs(y - playerY);
        if (distance < best) {
            choices.clear();
            best = distance;
        }
        if (distance == best) {
            choices.add(new Cell(x, y));
        }
    }
    if (!choices.isEmpty()) {
        var next = choices.get(random.nextInt(choices.size()));
        ghostX = next.x();
        ghostY = next.y();
    }
}

void step() {
    if (!started || state != RoundState.PLAY) {
        return;
    }
    if (open(playerX + wantedX, playerY + wantedY)) {
        dx = wantedX;
        dy = wantedY;
    }
    if (open(playerX + dx, playerY + dy)) {
        playerX += dx;
        playerY += dy;
    }
    if (pellets[playerY][playerX]) {
        pellets[playerY][playerX] = false;
        remaining--;
    }
    boolean caught = playerX == ghostX && playerY == ghostY;
    if (++ticks % 3 == 0 && !caught) {
        moveGhost();
    }
    caught|=playerX == ghostX && playerY == ghostY;
    state = afterContact(caught, remaining);
}

/// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
void draw(Graphics g) {
    int width = map[0].length() * TILE, height = map.length * TILE;
    g.setColor(new Color(16, 18, 38));
    g.fillRect(0, 0, width, height + 54);
    for (int y = 0; y < map.length; y++) {
        for (int x = 0; x < map[y].length(); x++) {
            if (!open(x, y)) {
                g.setColor(new Color(52, 78, 153));
                g.fillRoundRect(x * TILE + 2, y * TILE + 2, TILE - 4, TILE - 4, 8, 8);
            } else {
                if (pellets[y][x]) {
                    g.setColor(new Color(255, 219, 167));
                    g.fillOval(x * TILE + 12, y * TILE + 12, 6, 6);
                }
            }
        }
    }
    g.setColor(new Color(255, 222, 71));
    g.fillOval(playerX * TILE + 4, playerY * TILE + 4, TILE - 8, TILE - 8);
    g.setColor(new Color(255, 117, 173));
    g.fillRoundRect(ghostX * TILE + 4, ghostY * TILE + 4, TILE - 8, TILE - 8, 12, 12);
    g.setColor(Color.WHITE);
    g.fillOval(ghostX * TILE + 8, ghostY * TILE + 10, 5, 7);
    g.fillOval(ghostX * TILE + 17, ghostY * TILE + 10, 5, 7);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
    g.drawString("Dots left: " + remaining + "   " + (started ? state : "ARROWS TO START"), 10, height + 23);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Arrow keys: steer     R: restart", 10, height + 44);
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; timers stop on
/// window close.
void showWindow() {
    var frame = new JFrame("Mini Pacman - Maze Muncher");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(map[0].length() * TILE, map.length * TILE + 54));
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent event) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_LEFT -> direction(-1, 0);
                case KeyEvent.VK_RIGHT -> direction(1, 0);
                case KeyEvent.VK_UP -> direction(0, -1);
                case KeyEvent.VK_DOWN -> direction(0, 1);
                case KeyEvent.VK_R -> restart();
            }
        }
    });
    var timer = new Timer(130, event -> {
        step();
        panel.repaint();
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
/// - `boolean` — A value that is either true or false.
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `'` — Single quotes enclose one character, such as a board marker.
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
/// - `record` — A small named bundle of values. Its component accessors use the component names with ().
/// - `enum` — A type whose values are a fixed named set, such as READY, PLAYING and FINISHED.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
