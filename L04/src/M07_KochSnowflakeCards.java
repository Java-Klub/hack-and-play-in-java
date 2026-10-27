// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L04.07 · KochSnowflakeCards
///
/// Make a snowflake postcard whose three sides grow smaller zigzags. The supplied edge builder can add
/// intricate detail, while your part decides whether the shape closes all the way around.
///
/// **Your mission:** The saved snowflake is missing a whole side. Repair the side loop in snowflake so
/// corner indices 0, 1 and 2 are each visited once; keep the prepared edge builder.
///
/// **Today you learn:** For ranges over positions: three stored corners have indices 0, 1 and 2. The
/// loop’s initial value and stopping comparison together decide which sides exist in the picture.
///
/// Use Run File, refresh the project in NetBeans Files and open koch-snowflake-cards.png to inspect the
/// missing side.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F11 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
int snowflake(Graphics2D g, int level) {
    int[] xs = {270, 70, 470};
    int[] ys = {45, 390, 390};
    int segments = 0;
    for (int side = 1; side < 3; side++) {
        int next = (side + 1) % 3;
        segments += edge(g, xs[side], ys[side], xs[next], ys[next], level);
    }
    return segments;
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
/// - `for (int side = 1; side < 3; side++)` — The initial side, the continue condition and the one-step
///   increase form this loop’s range.
/// - `int next = (side + 1) % 3;` — The supplied expression chooses the following corner and wraps from
///   the last one back to the first.
/// - `xs[side]` — Look up the horizontal position of this corner; the square brackets select a stored
///   entry.
/// - `segments += edge(g, xs[side], ys[side], xs[next], ys[next], level);` — Ask the prepared edge action
///   to draw this side and add its returned segment count.
/// - `return segments;` — Return the total number of drawn small segments after every selected side is
///   finished.
///
/// ### What the prepared part already does
///
/// xs and ys contain the three corner positions. edge already breaks a side into smaller zigzags,
/// repeatedly when the level is higher; you need not change that recursive machinery. render supplies the
/// canvas and colours, while save and main write koch-snowflake-cards.png.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Starting at one omits the first side.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The three indices are 0,1,2, with 3 as the excluded bound.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Start side = 0 and keep side < 3.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int edge(Graphics2D g, double x, double y, double nx, double ny, int level) {
    if (level == 0) {
        g.drawLine((int)x, (int)y, (int)nx, (int)ny);
        return 1;
    }
    double dx = (nx - x) / 3;
    double dy = (ny - y) / 3;
    double ax = x + dx;
    double ay = y + dy;
    double bx = ax + dx * 0.5 - dy * Math.sqrt(3) / 2;
    double by = ay + dx * Math.sqrt(3) / 2 + dy * 0.5;
    return edge(g, x, y, ax, ay, level - 1)
        + edge(g, ax, ay, bx, by, level - 1)
        + edge(g, bx, by, x + 2 * dx, y + 2 * dy, level - 1)
        + edge(g, x + 2 * dx, y + 2 * dy, nx, ny, level - 1);
}

BufferedImage render(long seed, double option) {
    int level = (int)option;
    if (!Double.isFinite(option) || level < 0 || level > 6) {
        throw new IllegalArgumentException("Level: 0..6.");
    }
    var image = new BufferedImage(540, 480, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 540, 480);
        g.setColor(Color.getHSBColor(new Random(seed).nextFloat(), 0.8F, 0.7F));
        snowflake(g, level);
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
        Path path = Path.of(args.length > 0 ? args[0] : "koch-snowflake-cards.png");
        long seed = args.length > 1 ? Long.parseLong(args[1]) : 42;
        double option = args.length > 2 ? Double.parseDouble(args[2]) : 4;
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
/// - `==` — Compares primitive values. For text contents, use equals instead.
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
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `throws` — In a method declaration: tells callers which failure they may need to handle.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
/// - `Path` — A file-system location, not the file’s contents.
/// - `Files` — JDK operations for reading and writing local files.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
// </editor-fold>

