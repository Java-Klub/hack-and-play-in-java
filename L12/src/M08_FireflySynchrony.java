// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L12.08 · FireflySynchrony
///
/// Watch a field of blinking fireflies gradually find a shared rhythm. Change the coupling and compare
/// independent flashes with a crowd that starts to pulse together.
///
/// **Your mission:** Every valid coupling command sets zero, so the slider cannot strengthen synchrony.
/// Restore its carried value and keep invalid commands from changing the previous setting.
///
/// **Today you learn:** A guarded record-pattern case accepts only the usable values of one command
/// alternative. The remaining Coupling case deliberately does nothing; Toggle and Restart have
/// independent effects.
///
/// After Run File, move Coupling toward 2 and watch the synchrony number, using Pause/Run or New phases
/// to compare the flashes.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F35 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
void apply(Control control) {
    switch (control) {
        case Coupling(var value) when Double.isFinite(value) && value >= 0 && value <= 2 -> coupling = value;
        case Coupling(var value) -> { }
        case Toggle() -> running = !running;
        case Restart(var seed) -> reset(seed);
    }
}

// <editor-fold defaultstate="collapsed" desc="Recall · F35">
/// ### Record patterns: choose a shape and unpack its data
///
/// ```java
/// sealed interface Badge permits Name, Stars {}
/// record Name(String text) implements Badge {}
/// record Stars(int count) implements Badge {}
/// String label(Badge badge) {
///     return switch (badge) {
///         case Name(var text) -> text;
///         case Stars(var n) when n > 0 -> n + " stars";
///         case Stars(var n) -> "none";
///     };
/// }
/// ```
///
/// `label(new Stars(2))` returns `"2 stars"`. Sealed closes the alternatives; permits lists them. A
/// record pattern matches its record type and gives component values local names, in declaration
/// order. `var` infers their types. The `when` guard must also pass; otherwise matching continues.
/// The last Stars case handles nonpositive counts. Every permitted alternative needs handling here.
///
/// Predict: which case handles `new Stars(0)`, and what text does `new Name("Nova")` produce?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `case Coupling(var value)` — Extracts this command’s requested strength.
/// - `Double.isFinite(value) && value >= 0 && value <= 2` — All three conditions must hold before the
///   accepted Coupling action runs.
/// - `coupling = 0` — Currently discards the accepted value and always disables coupling.
/// - `case Coupling(var value) -> { }` — Handles rejected strengths without altering the previous state.
///
/// ### What the prepared part already does
///
/// The slider and buttons construct Control records and send them through apply. The supplied step
/// computes a new phase snapshot, order measures synchrony, and reset produces repeatable starting phases
/// from a seed.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### A guarded match does not discard the matched value
///
/// Trace Coupling with value 1.5: the record type matches and all three guard conditions pass. The
/// value is still available to the chosen action. Next trace value -1: the guard fails and the
/// following Coupling case deliberately makes no change. Starting from an accepted setting, predict
/// the setting after that rejected command. Toggle carries no components, while Restart carries a
/// seed; neither should accidentally reuse a coupling value. This is why type matching, guard
/// checking and the case action are three separate questions.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The guard already separates valid and invalid Coupling values.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The matched var value is the new strength, not a constant zero.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Keep the fallback Coupling case and the other closed alternatives exhaustive.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
double[] phase = new double[40];
double[] frequency = new double[40];
double coupling = 1;
boolean running = true;

/// Supplied restart: create the initial model; no native window is created here.
void reset(long seed) {
    var random = new Random(seed);
    for (int i = 0; i < phase.length; i++) {
        phase[i] = random.nextDouble() * Math.PI * 2;
        frequency[i] = 1 + (random.nextDouble() - 0.5) * 0.04;
    }
}

double order() {
    double x = 0;
    double y = 0;
    for (double value : phase) {
        x += Math.cos(value);
        y += Math.sin(value);
    }
    return Math.hypot(x, y) / phase.length;
}

/// Supplied bounded update: one call means one model step; the active repair is described above.
void step() {
    double[] next = new double[phase.length];
    for (int i = 0; i < phase.length; i++) {
        double pull = 0;
        for (double other : phase) {
            pull += Math.sin(other - phase[i]);
        }
        next[i] = (phase[i] + 0.03 * (frequency[i] + coupling * pull / phase.length) + Math.PI * 2) % (Math.PI * 2);
    }
    phase = next;
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Firefly Synchrony");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var canvas = new JPanel(){
        
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics;
            g.setColor(new Color(10, 20, 35));
            g.fillRect(0, 0, 600, 400);
            for (int i = 0; i < phase.length; i++) {
                g.setColor(Math.cos(phase[i]) > 0.94 ? Color.YELLOW : new Color(50, 70, 35));
                g.fillOval(35 + i % 8 * 70, 30 + i / 8 * 70, 24, 24);
            }
            g.setColor(Color.WHITE);
            g.drawString("Synchrony: " + String.format(Locale.ROOT, "%.2f", order()), 15, 390);
        }
    };
    canvas.setPreferredSize(new Dimension(600, 400));
    var controls = new JPanel();
    var strength = new JSlider(0, 200, 100);
    strength.addChangeListener(event -> apply(new Coupling(strength.getValue() / 100.0)));
    var pause = new JButton("Pause / Run");
    pause.addActionListener(event -> apply(new Toggle()));
    var reset = new JButton("New phases");
    reset.addActionListener(event -> {
        apply(new Restart(System.nanoTime()));
        canvas.repaint();
    });
    controls.add(new JLabel("Coupling"));
    controls.add(strength);
    controls.add(pause);
    controls.add(reset);
    var timer = new Timer(25, event -> {
        if (running) {
/// Supplied bounded update: one call means one model step; the active repair is described above.
            step();
        }
        canvas.repaint();
    });
    frame.addWindowListener(new WindowAdapter(){
        
        public void windowClosed(WindowEvent event) {
            timer.stop();
        }
    });
    timer.start();
    frame.add(canvas, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

sealed interface Control permits Coupling, Toggle, Restart {}
record Coupling(double value) implements Control {}
record Toggle() implements Control {}
record Restart(long seed) implements Control {}

/// Start the real program with Run File. Test File checks the rule without opening a window.
/// Slider/buttons construct Control records; apply pattern-matches their components.
/// reset is seeded, step computes a next snapshot, order measures synchrony; the supplied window owns the timer.
void main(String[] args) {
    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
/// Supplied restart: create the initial model; no native window is created here.
    reset(42);
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
/// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `%` — The remainder after division; useful for repeated positions or turns.
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
/// - `implements` — Says that a record or class fulfils the named interface role.
/// - `record` — A small named bundle of values. Its component accessors use the component names with ().
/// - `interface` — Describes a role that different implementations can fulfil. The caller can use that
///   role without choosing its internals.
/// - `sealed` — Restricts a data type to its explicitly listed alternatives.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>

