// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L09.02 · GapHopper
///
/// Keep a tiny flyer alive between moving pipes. The obstacles should form an endless course rather than
/// quietly disappear behind you.
///
/// **Your mission:** A recycled pipe is inserted at the wrong end, eventually disrupting the course.
/// Restore the ordered queue of obstacles without changing flight physics.
///
/// **Today you learn:** A List can represent left-to-right order, not just a bag of objects. Removing the
/// expired first pipe and placing its replacement must preserve that meaning.
///
/// From the course folder containing README.md:
/// - Play: `java L09/M02_GapHopper.java`
/// - Check this mission: `java L09/Check.java M02_GapHopper.java`
///
/// After the play command, keep the flyer between pipes by pressing Space or clicking, and press R
/// to restart after a crash. After your repair: save the file, run the check command above, then
/// play again.
///
/// Need an explanation? Recall · F22 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
void recyclePipes() {
    if (!pipes.isEmpty() && pipes.getFirst().x + PIPE_WIDTH < 0) {
        double furthest = pipes.getLast().x;
        pipes.removeFirst();
        pipes.add(new Pipe(furthest + 260, newGap()));
    }
}

// <editor-fold defaultstate="collapsed" desc="Recall · F22">
/// ### List: a sequence you can change
///
/// A `List<String>` holds text in order. `new ArrayList<>()` makes a mutable list: its contents can
/// change without replacing the list itself.
///
/// ```java
/// List<String> snacks = new ArrayList<>();
/// snacks.add("apple");
/// snacks.add("nuts");
/// IO.println(snacks.get(0));
/// IO.println(snacks.size());
/// ```
///
/// The output is `apple`, then `2`. `add(value)` appends one item; `get(index)` reads without
/// removing it. Indexes start at zero. `isEmpty()` checks for no items. `removeLast()` removes and
/// returns the final item; call it only on a nonempty list. A list made directly by `List.of(...)`
/// cannot be changed, but `new ArrayList<>(List.of(...))` can.
///
/// Predict: after `snacks.removeLast()`, what are its size and first item?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `!pipes.isEmpty()` — The ! reverses the answer: continue only when a first pipe exists.
/// - `pipes.getLast().x` — Reads the farthest pipe position without removing that pipe.
/// - `pipes.removeFirst()` — Removes the expired head and shifts the remaining order.
/// - `pipes.addFirst` — Inserts at index zero; compare that choice with the new pipe’s far-right
///   position.
///
/// ### What the prepared part already does
///
/// Each step moves pipes and checks scoring and collisions before recyclePipes runs. newGap chooses a
/// bounded opening; the supplied window handles the jump key, restart and timer cleanup.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The oldest obstacle is at index zero.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Read the last position before removing the expired first element.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Removal and append must preserve a two-obstacle order; do not mutate the list during the earlier
/// for-each.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
static class Pipe {
    double x;
    int gapY;
    boolean scored;
    Pipe(double x, int gapY) {
        this.x = x;
        this.gapY = gapY;
    }
}

final int WIDTH = 500, HEIGHT = 400, BIRD_X = 110, RADIUS = 10, PIPE_WIDTH = 55, GAP = 130;
final Random random = new Random();
final java.util.List<Pipe> pipes = new ArrayList<>(java.util.List.of(new Pipe(340, 200), new Pipe(600, 200)));
double birdY, velocityY;
int score;
boolean running, lost, spaceDown;

int newGap() {
    return 100 + random.nextInt(201);
}

/// Supplied restart: restore the initial model before the UI uses it.
void restart() {
    birdY = 180;
    velocityY = 0;
    score = 0;
    running = lost = spaceDown = false;
    for (int index = 0; index < pipes.size(); index++) {
        pipes.get(index).x = 340 + index * 260;
        pipes.get(index).gapY = newGap();
        pipes.get(index).scored = false;
    }
}

void flap() {
    if (lost) return;
    running = true;
    velocityY = -5.5;
}

/// Supplied bounded update: one call means one model step; the active repair is described above.
void step() {
    if (!running || lost) return;
    velocityY += 0.28;
    birdY += velocityY;
    if (birdY - RADIUS < 0 || birdY + RADIUS > HEIGHT) lost = true;
    for (Pipe pipe : pipes) pipe.x -= 2.5;
    for (Pipe pipe : pipes) {
        boolean overlap = BIRD_X + RADIUS > pipe.x && BIRD_X - RADIUS < pipe.x + PIPE_WIDTH;
        if (overlap && (birdY - RADIUS < pipe.gapY - GAP / 2
                || birdY + RADIUS > pipe.gapY + GAP / 2)) lost = true;
        if (!pipe.scored && pipe.x + PIPE_WIDTH < BIRD_X - RADIUS) {
            pipe.scored = true;
            score++;
        }

    }
    recyclePipes();
}

/// Supplied drawing: reads model values; repainting is not another turn.
void draw(Graphics g) {
    g.setColor(new Color(31, 51, 82));
    g.fillRect(0, 0, WIDTH, HEIGHT + 44);
    for (Pipe pipe : pipes) {
        g.setColor(new Color(74, 197, 158));
        g.fillRect((int) pipe.x, 0, PIPE_WIDTH, pipe.gapY - GAP / 2);
        g.fillRect((int) pipe.x, pipe.gapY + GAP / 2, PIPE_WIDTH, HEIGHT - pipe.gapY - GAP / 2);
        g.setColor(new Color(134, 239, 176));
        g.fillRect((int) pipe.x, pipe.gapY - GAP / 2 - 10, PIPE_WIDTH, 10);
        g.fillRect((int) pipe.x, pipe.gapY + GAP / 2, PIPE_WIDTH, 10);
    }
    g.setColor(new Color(255, 218, 87));
    g.fillOval(BIRD_X - RADIUS, (int) birdY - RADIUS, RADIUS * 2, RADIUS * 2);
    g.setColor(Color.BLACK);
    g.fillOval(BIRD_X + 3, (int) birdY - 5, 4, 4);
    g.setColor(Color.WHITE);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
    String status = lost ? "CRASH! Press R" : running ? "Score: " + score : "SPACE OR CLICK TO START";
    g.drawString(status, 16, HEIGHT + 22);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Space / click: flap     R: restart", 16, HEIGHT + 40);
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Gap Hopper");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
/// Supplied drawing: reads model values; repainting is not another turn.
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT + 44));
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_R) restart();
            if (event.getKeyCode() == KeyEvent.VK_SPACE && !spaceDown) {
                spaceDown = true;
                flap();
            }
        }
        @Override public void keyReleased(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_SPACE) spaceDown = false;
        }
    });
    panel.addMouseListener(new MouseAdapter() {
        @Override public void mousePressed(MouseEvent event) { flap(); }
    });
    var timer = new Timer(20, event -> {
/// Supplied bounded update: one call means one model step; the active repair is described above.
        step();
        panel.repaint();
    });
    frame.addWindowFocusListener(new WindowAdapter() {
        @Override public void windowLostFocus(WindowEvent event) { spaceDown = false; }
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
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `class` — Defines a kind of object. These compact source files already have an implicit class named
///   after the file.
/// - `static` — Belongs to the class rather than to one particular game object. A supplied test main
///   often uses it.
/// - `public` — Makes this declaration accessible to other code.
/// - `protected` — A visibility keyword used by supplied library callbacks, such as paintComponent.
/// - `super` — In super.paintComponent(g), asks the supplied parent implementation to paint its standard
///   background first.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>

