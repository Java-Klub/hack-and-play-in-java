// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.List;
// </editor-fold>

/// # L04.08 · DragonCurve
///
/// Grow a paper-folding dragon from a trail of left and right turns. Repeating a small folding recipe
/// creates a surprisingly elaborate curve, which the program saves as a coloured picture.
///
/// **Your mission:** The curve receives one fold fewer than requested. Repair only the outer loop in
/// foldTurns so zero folds does no work and every positive request runs that many folding passes.
///
/// **Today you learn:** For as a repetition count: the outer counter counts complete folds, not turns
/// already stored. Check the tiny requests zero and one before reasoning about a detailed eleven-fold
/// picture.
///
/// Use Run File, refresh NetBeans Files and open dragon-curve.png; then use Test File to compare the
/// exact zero-fold and one-fold cases.
/// After your repair: Save → Test File → play again.
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
/// directions. Later methods convert the sequence to positions, fit it on the canvas, colour it and write
/// dragon-curve.png. No window or fold slider is part of this exporter.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. Try folds = 1 with the current starting fold.
/// 2. The outer counter should visit 0 through folds - 1.
/// 3. Initialize fold to 0; keep fold < folds.
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

void save(Path path, long seed, double option) throws Exception {
    Files.createDirectories(path.toAbsolutePath().getParent());
    if (!ImageIO.write(render(seed, option), "png", path.toFile())) {
        throw new IllegalStateException("PNG writer missing.");
    }
}

void main(String[] args) throws Exception {
    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "dragon-curve.png");
        long seed = args.length > 1 ? Long.parseLong(args[1]) : 42;
        double option = args.length > 2 ? Double.parseDouble(args[2]) : 11;
        save(path, seed, option);
        IO.println("Saved: " + path.toAbsolutePath());
    } catch (IllegalArgumentException invalid) {
        IO.println("Invalid options: " + invalid.getMessage());
    }
}

/// Prepared entry points: main, render, save.
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
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
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
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `throws` — In a method declaration: tells callers which failure they may need to handle.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
/// - `Path` — A file-system location, not the file’s contents.
/// - `Files` — JDK operations for reading and writing local files.
// </editor-fold>

