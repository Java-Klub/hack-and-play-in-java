// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L07.05 · TargetPop
///
/// Pop moving targets against a thirty-second clock. Try a second round to beat your own accuracy and
/// score.
///
/// **Your mission:** At the deadline the game returns to its ready state instead of finishing. Repair
/// afterTime() so the final score survives until an intentional restart.
///
/// **Today you learn:** Enum states: a transition changes only the states to which it applies. Expiring
/// RUNNING must not accidentally restart a finished or not-yet-started round.
///
/// From the course folder containing README.md:
/// - Play: `java L07/M05_TargetPop.java`
/// - Check this mission: `java L07/Check.java M05_TargetPop.java`
///
/// Run the play command above and click a circle to begin the timed round, then keep clicking
/// targets until the clock expires. After your repair: save the file, run the check command above,
/// then play again.
///
/// Need an explanation? Recall · F21 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
enum Phase {
    READY, RUNNING, FINISHED
}

Phase afterTime(long now) {
    if (phase == Phase.RUNNING && now >= deadline) {
        return Phase.FINISHED;
    }
    return phase;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F21">
/// ### Enum: one of the named choices
///
/// An enum defines a finite set of values of one type. The type name and dot qualify a particular
/// value. Unlike arbitrary text, a misspelled enum value is a compiler error. A variable holds one
/// choice at a time; comparing enum values with `==` is appropriate.
///
/// ```java
/// enum Voice {
///     WHISPER, SING, SHOUT
/// }
///
/// Voice voice = Voice.SING;
/// IO.println(voice == Voice.SHOUT);
/// ```
///
/// The declaration belongs beside methods; the statements can be read inside a method. The comparison
/// prints false because SING and SHOUT are distinct values. Predict it after assigning SHOUT. Can
/// this single variable simultaneously claim SING and SHOUT? Explain what contradictory combinations
/// separate booleans might allow.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `enum Phase` — Defines the three named phases understood by clicks, timing, and drawing.
/// - `long now` — A whole-number time value supplied by the caller, making deadline behaviour testable.
/// - `now >= deadline` — The deadline itself already counts as expired, not just later times.
/// - `return phase` — Leaves the current state unchanged when no transition applies.
///
/// ### What the prepared part already does
///
/// click() starts a ready round and scores hits only while it runs. The timer calls afterTime() through
/// the update path; drawing and input both read phase to agree on what is allowed.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// afterTime() receives time explicitly, so the boundary can be tested without waiting.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Only RUNNING can expire; READY and FINISHED must remain unchanged.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// At now >= deadline return FINISHED, not READY.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 400, RADIUS = 26;
final long ROUND_NANOS = 30_000_000_000L, TARGET_NANOS = 1_200_000_000L;
final Random random = new Random();
int targetX, targetY, hits, misses, expired;
long deadline, targetDeadline;

Phase phase;

void spawn(long now) {
    targetX = RADIUS + random.nextInt(WIDTH - RADIUS * 2 + 1);
    targetY = RADIUS + random.nextInt(HEIGHT - RADIUS * 2 + 1);
    targetDeadline = now + TARGET_NANOS;
}

/// restart(): Restores the starting Target Pop model so this toy can be tried again.
void restart() {
    hits = misses = expired = 0;
    deadline = 0;
    phase = Phase.READY;
    spawn(0);
}

void tick(long now) {
    phase = afterTime(now);
    if (phase != Phase.RUNNING) {
        return;
    }
    if (now >= targetDeadline) {
        expired++;
        spawn(now);
    }
}

/// click(): Starts on first click, ignores finished rounds and scores a circular hit or miss.
void click(int x, int y, long now) {
    tick(now);
    if (phase == Phase.FINISHED) {
        return;
    }
    if (phase == Phase.READY) {
        phase = Phase.RUNNING;
        deadline = now + ROUND_NANOS;
        targetDeadline = now + TARGET_NANOS;
    }
    int dx = x - targetX, dy = y - targetY;
    if (dx * dx + dy * dy <= RADIUS * RADIUS) {
        hits++;
        spawn(now);
    } else {
        misses++;
    }
}

/// draw(): Supplied rendering reads this model; paint calls do not advance its rules.
void draw(Graphics g) {
    g.setColor(new Color(25, 26, 47));
    g.fillRect(0, 0, WIDTH, HEIGHT + 58);
    if (!(phase == Phase.FINISHED)) {
        g.setColor(new Color(255, 128, 155));
        g.fillOval(targetX - RADIUS, targetY - RADIUS, RADIUS * 2, RADIUS * 2);
        g.setColor(new Color(255, 223, 149));
        g.fillOval(targetX - 10, targetY - 10, 20, 20);
        g.setColor(Color.WHITE);
        g.drawLine(targetX - 5, targetY, targetX + 5, targetY);
        g.drawLine(targetX, targetY - 5, targetX, targetY + 5);
    }
    g.setColor(Color.WHITE);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
    long seconds = (phase != Phase.READY) ? Math.max(0, (deadline - System.nanoTime() + 999_999_999) / 1_000_000_000) : 30;
    String state = (phase == Phase.FINISHED) ? "TIME!" : (phase != Phase.READY) ? seconds + " seconds" : "CLICK TO START";
    g.drawString(state + "    Hits: " + hits + "    Misses: " + misses, 14, HEIGHT + 24);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Click the circles before they disappear.    Expired: " + expired + "    R: restart", 14, HEIGHT + 47);
    if ((phase == Phase.FINISHED)) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        g.drawString("You popped " + hits + "!  Press R", 110, HEIGHT / 2);
    }
}

/// showWindow(): Supplied later topic: Swing listeners forward input to the model; timers stop on
/// window close.
void showWindow() {
    var frame = new JFrame("Target Pop");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT + 58));
    panel.setFocusable(true);
    panel.addMouseListener(new MouseAdapter() {
        @Override public void mousePressed(MouseEvent event) {
            panel.requestFocusInWindow();
            if (event.getY() < HEIGHT) {
                click(event.getX(), event.getY(), System.nanoTime());
            }
            panel.repaint();
        }
    });
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_R) {
                restart();
            }
        }
    });
    var timer = new Timer(25, event -> {
        tick(System.nanoTime());
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
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
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
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `else` — The alternative when the preceding if condition is false.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `public` — Makes this declaration accessible to other code.
/// - `protected` — A visibility keyword used by supplied library callbacks, such as paintComponent.
/// - `super` — In super.paintComponent(g), asks the supplied parent implementation to paint its standard
///   background first.
/// - `enum` — A type whose values are a fixed named set, such as READY, PLAYING and FINISHED.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
