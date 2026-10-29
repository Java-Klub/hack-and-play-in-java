// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L01.08 · SunflowerSpiral
///
/// Grow a sunflower-like arrangement from a thousand coloured dots. Small changes in the angle between
/// dots can turn a spiral into a few spokes.
///
/// **Your mission · Tune a visual instrument:** The default picture collapses into two spokes. Repair
/// defaultAngle so the ordinary
/// run uses 137.5 degrees and spreads the seeds into a spiral.
///
/// **Today you learn:** Named numeric values: a descriptive name connects one chosen angle to the later
/// picture. A double can store a fractional angle, for example half a degree.
///
/// Run the program: the picture appears immediately in a Java window.
double defaultAngle = 180;

// <editor-fold defaultstate="collapsed" desc="Recall · F03">
/// A variable gives a stored value a name. Its type describes the values it can hold: `int` holds
/// whole numbers and `double` can hold a fractional part. Initialization supplies the first value;
/// later assignment replaces it.
///
/// These example statements belong inside a called method.
///
/// ```java
/// int charge = 4;
/// charge = charge + 3;
/// IO.println(charge);
/// ```
///
/// Predict the output. First `charge` stores four. On the second line, Java reads the old value to
/// calculate the right-hand expression, then stores seven under the same name. `=` is assignment, not
/// a mathematical claim that both sides remain equal forever. Other statements reading the name now
/// obtain seven. A name does not update itself: some executed assignment must change its value. A
/// `final` variable cannot be assigned again.
///
/// Now predict the final charge if its initialization uses six instead of four.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `double` — A numeric type that can hold a fractional part, as well as whole values such as 180.
/// - `defaultAngle` — The name of the angle used when the program starts with ordinary defaults.
/// - `=` — Initialize this named value from the number on the right.
/// - `180` — The current angle turns halfway around a circle, so successive dots alternate directions.
/// - `;` — The declaration ends here; the later picture code reads the stored angle.
///
/// ### What the prepared part already does
///
/// position computes each dot’s location, render colours the dots, and the prepared window shows
/// the result directly. main passes your default into that ready-made mathematics; you do not need
/// to rewrite the spiral formula.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="For this game">
/// The program builds the picture in memory and shows it directly in Java. The angle is in
/// degrees; Java writes fractional numbers with a decimal point. Rerun the file after changing
/// defaultAngle so the new value is loaded. The mission checks verify behaviour, not an old
/// picture left in an earlier window.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Follow the values · predict a case">
/// A double literal uses a decimal point: 12.5. A comma is not an alternative decimal separator in
/// Java source. The name defaultAngle stores one value that many dot positions read. A small
/// numerical change can have a large visual effect because that same change is repeated across many
/// dots.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// After restoring 137.5, try angles 120, 137 and 137.6. Before each run predict spokes or a
/// spread-out pattern. Pick one image as an alien flower and give your friend its angle as a recipe,
/// without showing the picture first.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The current 180-degree turn alternates between two directions.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// A decimal point in Java is a dot.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Set double defaultAngle = 137.5; then run the program to see the new picture.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
double[] position(int index, double degrees) {
    double angle = Math.toRadians(degrees) * index;
    double radius = 7 * Math.sqrt(index);
    return new double[]{250 + radius * Math.cos(angle), 250 + radius * Math.sin(angle)};
}

BufferedImage render(long seed, double degrees) {
    if (!Double.isFinite(degrees) || degrees < 1 || degrees > 360) {
        throw new IllegalArgumentException("Angle: 1..360 degrees.");
    }
    var image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        float hue = new Random(seed).nextFloat();
        for (int i = 0; i < 1000; i++) {
            var point = position(i, degrees);
            g.setColor(Color.getHSBColor(hue + i / 1000.0F, 0.65F, 1));
            g.fillOval((int)point[0] - 3, (int)point[1] - 3, 6, 6);
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
    var image = render(42, defaultAngle);
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame("SunflowerSpiral");
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
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
/// - `/` — Divides numbers. Dividing two int values discards the fractional part.
/// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
///   direction. In List<String>, angle brackets describe the element type.
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line.
// </editor-fold>
