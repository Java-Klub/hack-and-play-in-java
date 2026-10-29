// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # 10.19 · WireframeCube
///
/// Rotate a wire cube and watch a flat drawing suggest depth. Its edge counter should describe the shape,
/// even if an input list names an edge twice in opposite directions.
///
/// **Your mission:** Build uniqueEdges from an empty set. Traverse every candidate pair, skip
/// out-of-range endpoints and self-edges, put the smaller endpoint first, and insert the normalised
/// Edge. Return the distinct edges in their first-seen order.
///
/// **Today you learn:** Use Set membership, equality and insertion deliberately while implementing
/// the whole operation, not just its final condition.
///
/// Run the program, then use Pause/Run to stop the rotating cube, read Distinct edges and
/// choose Reset angle to restore the view.
Set<Edge> uniqueEdges(int[][] candidates) {
    Set<Edge> result = new LinkedHashSet<>();
    // Traverse, validate, normalise and insert the candidates here.
    return result;
}

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Give the cube a secret diagonal signature by adding candidate edges, including some deliberately
/// reversed duplicates. Predict the distinct edge count on paper and check it in the window before
/// changing the rotation.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Recall · F23">
/// ### Set: membership without duplicates
///
/// A `Set` answers whether a value belongs to a collection. It does not reserve an indexed place for
/// each insertion.
///
/// ```java
/// Set<String> stickers = new HashSet<>();
/// IO.println(stickers.add("moon"));
/// IO.println(stickers.add("moon"));
/// IO.println(stickers.size());
/// ```
///
/// The output is `true`, `false`, then `1`. The first add changes membership; the second finds an
/// equal value already present. `contains(value)` asks without changing the set. `remove(value)`
/// removes that value and reports whether it was present. A record's generated equality compares its
/// component values, so two separately created equal records represent the same set member. Do not
/// depend on HashSet iteration order.
///
/// Predict: after removing moon and adding sun, what are the size and the result of
/// `contains("moon")`?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Read this code">
/// ### Equality needs a shared representation
///
/// Imagine an undirected road between towns 2 and 5. (2, 5) and (5, 2) describe the same road, but
/// two record values with those component orders are not equal. Choose the convention smaller-first:
/// both become (2, 5). A Set can now remove the duplicate using ordinary record equality. Predict the
/// distinct roads in [(4,1), (1,4), (4,6)].
///
/// Math.min(a,b) and Math.max(a,b) return the smaller and larger endpoint. LinkedHashSet keeps
/// insertion order, unlike a promise to sort numerically. The prepared cube has eight vertices,
/// indexed 0..7. uniqueEdges owns input validity and normalisation; the rotation and drawing code
/// merely use the returned edges. An invalid pair contributes nothing and must not prevent later
/// valid pairs from being read.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// First build the traversal and validation. An empty input should return an empty set.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Make one canonical Edge for each accepted pair, using its smaller and larger endpoints.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Insert normalised Edge values into the LinkedHashSet and return that set after the loop.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
double angle;
boolean running = true;
int[][] edges = {{0, 1}, {0, 2}, {0, 4}, {1, 3}, {1, 5}, {2, 3}, {2, 6}, {3, 7}, {4, 5}, {4, 6}, {5, 7}, {6, 7}, {1, 0}};

/// Restore the starting game state.
void reset(long seed) {
    angle = 0;
}

int[][] project(double angle) {
    int[][] result = new int[8][2];
    for (int i = 0; i < 8; i++) {
        double x = (i & 1) == 0 ? -1 : 1;
        double y = (i & 2) == 0 ? -1 : 1;
        double z = (i & 4) == 0 ? -1 : 1;
        double rotatedX = x * Math.cos(angle) + z * Math.sin(angle);
        double rotatedZ = -x * Math.sin(angle) + z * Math.cos(angle);
        double rotatedY = y * Math.cos(angle * 0.7) - rotatedZ * Math.sin(angle * 0.7);
        double depth = y * Math.sin(angle * 0.7) + rotatedZ * Math.cos(angle * 0.7);
        result[i][0] = 300 + (int)(rotatedX * 350 / (4 + depth));
        result[i][1] = 200 + (int)(rotatedY * 350 / (4 + depth));
    }
    return result;
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Wireframe Cube");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var canvas = new JPanel(){

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics;
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 600, 400);
            g.setColor(Color.CYAN);
            var points = project(angle);
            var distinct = uniqueEdges(edges);
            for (var edge : distinct) {
                g.drawLine(points[edge.a()][0], points[edge.a()][1], points[edge.b()][0], points[edge.b()][1]);
            }
            g.drawString("Distinct edges: " + distinct.size(), 12, 20);
        }
    };
    canvas.setPreferredSize(new Dimension(600, 400));
    var controls = new JPanel();
    var pause = new JButton("Pause / Run");
    pause.addActionListener(event -> running = !running);
    var restart = new JButton("Reset angle");
    restart.addActionListener(event -> {
/// Restore the starting game state.
        reset(42);
        canvas.repaint();
    });
    controls.add(pause);
    controls.add(restart);
    var timer = new Timer(25, event -> {
        if (running) {
            angle += 0.025;
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

record Edge(int a, int b) {}

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
/// - `==` — Compares primitive values. For text contents, use equals instead.
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
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `public` — Makes this declaration accessible to other code.
/// - `protected` — A visibility keyword used by supplied library callbacks, such as paintComponent.
/// - `super` — In super.paintComponent(g), asks the supplied parent implementation to paint its standard
///   background first.
/// - `record` — A small named bundle of values. Its component accessors use the component names with ().
/// - `Set` — Stores distinct values; the same value is present at most once.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
