// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.Color;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L01.09 · PixelMonsters
///
/// Generate a symmetric pixel monster and explore how one colour changes its expression. The
/// transparent background makes its blocky shape stand out.
///
/// **Your mission:** The monster has lost its bright eyes. Repair the one shared eyeColour so both eye
/// blocks become white without editing each eye separately.
///
/// **Today you learn:** One named value, several readers: both eyes receive the same stored colour. A
/// single deliberate change should therefore affect two places in the picture.
///
/// From the course folder containing README.md:
/// - Play: `java L01/M09_PixelMonsters.java`
/// - Check this mission: `java L01/Check.java M09_PixelMonsters.java`
///
/// Run the play command above: the picture appears immediately in a Java window. After editing the
/// source, close the window and run the play command again; the check command checks the
/// calculation separately.
///
/// Need an explanation? Recall · F03 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
int eyeColour = Color.BLACK.getRGB();

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
/// - `int eyeColour` — A named whole-number colour value. The supplied palette call produces its pixel
///   encoding; you need not decode that number.
/// - `=` — Store the colour returned by the right-hand expression.
/// - `Color.BLACK` — A named black colour from the supplied Java palette.
/// - `.` — A dot selects a named part or action: first a colour from Color, then an action on that
///   colour.
/// - `getRGB()` — Ask the selected colour for the numeric form needed by the picture; empty parentheses
///   mean no additional input.
/// - `;` — Finish storing this one shared colour before the later picture is built.
///
/// ### What the prepared part already does
///
/// render uses the same eyeColour for the left and right eye, while a prepared random routine makes
/// the symmetric body. main renders seed 42 and shows the picture in the prepared window.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// The picture always uses seed 42, allowing a direct comparison of the eyes before and after an
/// edit. Color supplies named colours such as BLACK, WHITE, RED and YELLOW; getRGB() returns their
/// pixel encoding. You change one shared colour, not two eye positions.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The two eye pixels already read eyeColour.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Color.WHITE.getRGB() is a prepared way to obtain the white colour value.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Replace Color.BLACK with Color.WHITE in the initialization.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
BufferedImage render(long seed) {
    var random = new Random(seed);
    int[][] cells = new int[16][16];
    int body = Color.HSBtoRGB(random.nextFloat(), 0.7f, 0.9f);
    for (int row = 2; row < 14; row++) {
        int width = 3 + random.nextInt(5);
        for (int col = 8 - width; col < 8; col++) {
            if (random.nextInt(5) != 0) {
                cells[row][col] = body;
                cells[row][15 - col] = body;
            }
        }
    }
    cells[5][5] = eyeColour;
    cells[5][10] = eyeColour;
    cells[6][5] = 0xFF111111;
    cells[6][10] = 0xFF111111;
    var image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
    var drawing = image.createGraphics();
    try {
        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 16; col++) {
                drawing.setColor(new Color(cells[row][col], true));
                drawing.fillRect(col * 16, row * 16, 16, 16);
            }
        }
    } finally {
        drawing.dispose();
    }
    return image;
}

// Prepared display of the picture calculated above.
void showPicture() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("Image preview needs a graphical display.");
        return;
    }
    var image = render(42);
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame("PixelMonsters");
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
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `!=` — Checks that two values differ.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
/// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
///   direction. In List<String>, angle brackets describe the element type.
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line.
// </editor-fold>
