// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L01.09 · PixelMonsters
///
/// Generate a tiny symmetric pixel monster and compare different seeds to collect a few favourites. The
/// transparent background lets its blocky shape stand out.
///
/// **Your mission:** The monster has lost its bright eyes. Repair the one shared eyeColour so both eye
/// blocks become white without editing each eye separately.
///
/// **Today you learn:** One named value, several readers: both eyes receive the same stored colour. A
/// single deliberate change should therefore affect two places in the picture.
///
/// Use Run File, accept monster.png and enter seed 42, then refresh Files and open monster.png; reuse
/// that seed to compare the repaired eyes.
/// After your repair: Save → Test File → play again.
int eyeColour = Color.WHITE.getRGB();

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
/// render uses the same eyeColour for the left and right eye, while a prepared random routine makes the
/// symmetric body. write saves the transparent PNG; main asks for its file name and seed.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. The two eye pixels already read eyeColour.
/// 2. Color.WHITE.getRGB() is a prepared way to obtain the white colour value.
/// 3. Replace Color.BLACK with Color.WHITE in the initialization.
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

void write(long seed, Path path) throws Exception {
    Files.createDirectories(path.toAbsolutePath().getParent());
    ImageIO.write(render(seed), "png", path.toFile());
}

void main(String[] args) throws Exception {
    String target = args.length > 0 ? args[0] : IO.readln("Output PNG path (Enter for monster.png, q to quit): ");
    if (target == null || target.equalsIgnoreCase("q")) return;
    if (target.isBlank()) target = "monster.png";
    String seedText = args.length > 1 ? args[1] : IO.readln("Integer seed (q to quit): ");
    if (seedText == null || seedText.equalsIgnoreCase("q")) return;
    try {
        long seed = Long.parseLong(seedText.strip());
        Path path = Path.of(target);
        write(seed, path);
        IO.println("Saved: " + path.toAbsolutePath());
        IO.println("Seed: " + seed + ". The image has a transparent background.");
    } catch (NumberFormatException invalidSeed) {
        IO.println("Use an integer seed.");
    }
}

/// Prepared entry points: main, render, write.
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
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `!=` — Checks that two values differ.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
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
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throws` — In a method declaration: tells callers which failure they may need to handle.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
/// - `Path` — A file-system location, not the file’s contents.
/// - `Files` — JDK operations for reading and writing local files.
// </editor-fold>

