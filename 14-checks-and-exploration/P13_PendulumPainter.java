// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # 14.13 · PendulumPainter
///
/// Let a pendulum draw its own luminous trail. Change length and gravity to discover how the same
/// starting swing produces different motion.
///
/// **Your mission:** Implement makeAcceleration and install its result in acceleration. Its role
/// receives angle and length and returns -gravity × sin(angle) / length, using the current gravity
/// field. The supplied accelerationNow adapter and step integration stay unchanged.
///
/// **Today you learn:** Author an implementation of a small interface and install it in the running
/// program. Keep its calculation separate from input, updates and drawing.
///
/// Run the program, then adjust Length and Gravity to explore the pendulum trace; Pause/Run
/// controls motion and Reset clears and restarts it.
Acceleration makeAcceleration() {
    return (angle, length) -> {
        double restoring = -gravity * Math.sin(angle);
        return restoring / length;
    };
}

Acceleration acceleration = makeAcceleration();

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Create an alien pendulum by replacing the sine restoring rule with a gentle linear rule near the
/// centre. Compare equal initial angle, length and gravity. Label it as a new simulation rule rather
/// than a more accurate physical model.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Recall · F34">
/// ### Interface: ask a role to do its job
///
/// An interface names an operation its implementations provide. The caller can use that role without
/// choosing the implementation's formula.
///
/// ```java
/// interface Points {
///     int award(int n);
/// }
/// record DoublePoints() implements Points {
///     public int award(int n) {
///         return n * 2;
///     }
/// }
/// ```
///
/// With `Points rule = new DoublePoints();`, `rule.award(3)` returns 6. `implements Points` promises
/// the operation; the implementation must supply its public method. The variable rule refers to the
/// selected object, so call its operation with the current input rather than repeating a fixed
/// formula in the caller. Another implementation can supply another rule through the same interface.
///
/// Predict: what does this implementation return for `rule.award(5)`, and which code would change to
/// award three times the input?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Read this code">
/// ### Pass changing state; read the chosen setting
///
/// Acceleration names at(angle, length). The interface isolates the physical rule from the
/// integrator: your implementation returns a number, while step changes velocity, angle and the
/// trail. The mathematical contract is supplied; deriving pendulum physics is not this task. Math.sin
/// takes radians.
///
/// Worked example with gravity 200: at angle π/2, sin is 1, so a length of 100 gives -2. At angle 0
/// the result is zero. Predict the result at angle -π/2, length 100. Longer length reduces the
/// magnitude.
///
/// The lambda's angle and length come from each call. gravity is this object's field and should be
/// read when the role runs, so moving the Gravity slider affects the existing rule. Computing and
/// capturing a single acceleration during setup would freeze the starting state. Install the returned
/// role instead of leaving a silent substitute in the field.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Use the role parameters for angle and length; read the current gravity field.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Return a calculation, not a call to step. Math.sin(angle) supplies the sine.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Build the restoring force term, divide by the requested length, then initialise acceleration from
/// the factory.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
double accelerationNow() {
    return acceleration.at(angle, length);
}

double angle = 0.9;
double velocity;
double length = 180;
double gravity = 300;
boolean running = true;
java.awt.image.BufferedImage trace = new java.awt.image.BufferedImage(600, 450, java.awt.image.BufferedImage.TYPE_INT_RGB);
int ticks;

Point bob() {
    return new Point(300 + (int)(length * Math.sin(angle)), 70 + (int)(length * Math.cos(angle)));
}

/// Restore the starting game state.
void reset(long seed) {
    angle = 0.9;
    velocity = 0;
    ticks = 0;
    trace = new java.awt.image.BufferedImage(600, 450, java.awt.image.BufferedImage.TYPE_INT_RGB);
}

/// Supplied bounded update: one call means one model step; the active repair is described above.
void step() {
    Point before = bob();
    velocity += accelerationNow() * 0.03;
    velocity *= 0.999;
    angle += velocity * 0.03;
    Point after = bob();
    var g = trace.createGraphics();
    try {
        g.setColor(Color.getHSBColor(ticks++ / 700.0F, 0.8F, 1));
        g.drawLine(before.x, before.y, after.x, after.y);
    } finally {
        g.dispose();
    }
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Pendulum Painter");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var canvas = new JPanel(){

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics;
            g.drawImage(trace, 0, 0, null);
            Point point = bob();
            g.setColor(Color.GRAY);
            g.drawLine(300, 70, point.x, point.y);
            g.setColor(Color.WHITE);
            g.fillOval(point.x - 7, point.y - 7, 14, 14);
        }
    };
    canvas.setPreferredSize(new Dimension(600, 450));
    canvas.setBackground(Color.BLACK);
    var controls = new JPanel();
    var lengthControl = new JSlider(80, 230, 180);
    lengthControl.setPreferredSize(new Dimension(110, 25));
    lengthControl.addChangeListener(event -> length = lengthControl.getValue());
    var gravityControl = new JSlider(50, 600, 300);
    gravityControl.setPreferredSize(new Dimension(110, 25));
    gravityControl.addChangeListener(event -> gravity = gravityControl.getValue());
    var pause = new JButton("Pause / Run");
    pause.addActionListener(event -> running = !running);
    var reset = new JButton("Reset");
    reset.addActionListener(event -> {
/// Restore the starting game state.
        reset(42);
        canvas.repaint();
    });
    controls.add(new JLabel("Length"));
    controls.add(lengthControl);
    controls.add(new JLabel("Gravity"));
    controls.add(gravityControl);
    controls.add(pause);
    controls.add(reset);
    var timer = new Timer(20, event -> {
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

interface Acceleration { double at(double angle, double length); }


void main() {
/// Restore the starting game state.
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
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
/// - `/` — Divides numbers. Dividing two int values discards the fractional part.
/// - `:` — In for (int value : values), reads each element in turn. It also separates the two
///   alternatives of ? : or follows a switch label.
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
/// - `interface` — Describes a role that different implementations can fulfil. The caller can use that
///   role without choosing its internals.
/// - `null` — No object is present.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
