// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import module java.base;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
// </editor-fold>

class M08_DragonCurve {
    /// # L04.08 · DragonCurve
    ///
    /// Grow a paper-folding dragon from a trail of left and right turns. Repeating a small folding
    /// recipe creates a surprisingly elaborate curve, which the program displays as a coloured picture.
    ///
    /// **Your mission:** The curve receives one fold fewer than requested. Repair only the outer loop in
    /// foldTurns so zero folds does no work and every positive request runs that many folding passes.
    ///
    /// **Today you learn:** For as a repetition count: the outer counter counts complete folds, not turns
    /// already stored. Check the tiny requests zero and one before reasoning about a detailed eleven-fold
    /// picture.
    ///
    /// From the course folder containing README.md:
    /// - Play: `java L04/M08_DragonCurve.java`
    /// - Check this mission: `java L04/Check.java M08_DragonCurve.java`
    ///
    /// Run the play command above: the picture appears immediately in a Java window. After editing the
    /// source, close the window and run the play command again; the check command checks the
    /// calculation separately.
    ///
    /// Need an explanation? Recall · F11 explains the Java skill; Read this code explains these lines.
    /// Hints 1 and 2 open separately; Repair outline reveals the direction.
    List<Integer> foldTurns(int folds) {
        var turns = new ArrayList<Integer>();
        for (int fold = 1; fold < folds; fold++) {
            int old = turns.size();
            turns.add(1);
            for (int i = old - 1; i >= 0; i--) {
                turns.add(-turns.get(i));
            }
        }
        return turns;
    }

    // <editor-fold defaultstate="collapsed" desc="Recall · F11">
    /// A counted `for` puts initialization, a continue condition and progress in one header.
    /// Initialization happens once. Then Java checks the condition, runs the body if true, performs
    /// progress, and checks again.
    ///
    /// These example statements belong inside a called method.
    ///
    /// ```java
    /// for (int floor = 2; floor <= 4; floor++) {
    ///     IO.println(floor);
    /// }
    /// ```
    ///
    /// Predict the visited values: `2`, `3`, `4`. There are three passes, even though the last printed
    /// number is four. The two semicolons separate the header’s three parts; `floor++` adds one after a
    /// pass. The braces group the repeated statements. If the starting value already fails the condition,
    /// no pass occurs. Count positions and gaps separately: three positions have two gaps. Write down a
    /// tiny trace before changing a bound.
    ///
    /// Now predict the output if floor starts at five.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Read this code">
    /// ## Read these particular lines
    ///
    /// - `for (int fold = 1; fold < folds; fold++)` — This is the outer repetition range you own; folds is
    ///   the requested number, while fold tracks the current pass.
    /// - `int old = turns.size();` — Remember how many turns existed before this fold; the inner copy must
    ///   use that earlier portion.
    /// - `turns.add(1);` — The prepared recipe appends one right turn at the centre of each fold.
    /// - `for (int i = old - 1; i >= 0; i--)` — This supplied inner loop reads the old turns backwards; i--
    ///   moves towards the first stored turn.
    /// - `turns.add(-turns.get(i));` — Copy a turn with its direction reversed. Keep this ready-made folding
    ///   rule intact.
    /// - `return turns;` — Give the finished turn sequence to the supplied path-drawing code.
    ///
    /// ### What the prepared part already does
    ///
    /// The prepared inner loop supplies one complete fold by reversing earlier turns and changing their
    /// directions. Later methods convert the sequence to positions, fit it on the canvas, colour it and
    /// show the picture in the prepared window. The prepared Java window shows the curve directly; its
    /// folding recipe stays unchanged.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
    /// Try folds = 1 with the current starting fold.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
    /// The outer counter should visit 0 through folds - 1.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
    /// Initialize fold to 0; keep fold < folds.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
    List<Point> path(int folds) {
        if (folds < 0 || folds > 15) {
            throw new IllegalArgumentException("Folds: 0..15.");
        }
        var turns = foldTurns(folds);
        var points = new ArrayList<Point>();
        int x = 0;
        int y = 0;
        int direction = 0;
        int[] dx = {1, 0, -1, 0};
        int[] dy = {0, 1, 0, -1};
        points.add(new Point(x, y));
        x++;
        points.add(new Point(x, y));
        for (int turn : turns) {
            direction = Math.floorMod(direction + turn, 4);
            x += dx[direction];
            y += dy[direction];
            points.add(new Point(x, y));
        }
        return points;
    }

    BufferedImage render(long seed, double option) {
        if (!Double.isFinite(option)) {
            throw new IllegalArgumentException("Finite fold count required.");
        }
        var points = path((int)option);
        int minX = points.stream().mapToInt((p)->p.x).min().orElseThrow();
        int minY = points.stream().mapToInt((p)->p.y).min().orElseThrow();
        int maxX = points.stream().mapToInt((p)->p.x).max().orElseThrow();
        int maxY = points.stream().mapToInt((p)->p.y).max().orElseThrow();
        double scale = 450.0 / Math.max(1, Math.max(maxX - minX, maxY - minY));
        var image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        var g = image.createGraphics();
        try {
            for (int i = 1; i < points.size(); i++) {
                g.setColor(Color.getHSBColor((seed % 100) / 100.0F + i / (float)points.size(), 0.7F, 1));
                Point a = points.get(i - 1);
                Point b = points.get(i);
                g.drawLine(25 + (int)((a.x - minX) * scale), 25 + (int)((a.y - minY) * scale),
                    25 + (int)((b.x - minX) * scale), 25 + (int)((b.y - minY) * scale));
            }
        } finally {
            g.dispose();
        }
        return image;
    }

    // Prepared display of the picture calculated above.
    void showPicture() {
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            IO.println("Image preview needs a graphical display.");
            return;
        }
        var image = render(42, 11);
        javax.swing.SwingUtilities.invokeLater(() -> {
            var frame = new javax.swing.JFrame("DragonCurve");
            var picture = new javax.swing.JLabel(new javax.swing.ImageIcon(image));
            picture.setOpaque(true);
            picture.setBackground(new java.awt.Color(35, 39, 48));
            frame.add(picture);
            frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        });
    }

    void main() {
        showPicture();
    }

    /// Prepared entry points: main, render.
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
    /// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
    /// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
    /// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
    ///   type.
    /// - `=` — Stores the value on the right under the name on the left. It does not compare them.
    /// - `||` — At least one condition must be true; if the first is true, Java skips the second.
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
    /// - `try` — A block whose possible failures can be handled by the following catch.
    /// - `throw` — Reports a failure by creating or passing an exception.
    /// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
    /// - `IO` — Console input and output. println writes a line.
    // </editor-fold>

}
