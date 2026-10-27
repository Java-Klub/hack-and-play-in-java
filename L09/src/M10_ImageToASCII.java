// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L09.10 · ImageToASCII
///
/// Turn a picture into a wall of characters. Change its text width to discover how much detail survives
/// when pixels become dots, colons and other marks.
///
/// **Your mission:** A mistyped width shrinks the result to four characters rather than using the
/// advertised width 80. Repair that fallback without accepting numbers outside 4–160.
///
/// **Today you learn:** A narrow parsing boundary separates unrecognisable text from valid integers with
/// invalid values. Only the expected conversion exception should select the fallback width.
///
/// After Run File, enter an image width from 4 to 160 or try oops, then inspect the character picture in
/// Output and portrait.txt.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F31 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
int readWidth(String text) {
    try {
        int value = Integer.parseInt(text.strip());
        if (value < 4 || value > 160) throw new IllegalArgumentException("width: 4..160.");
        return value;
    } catch (NumberFormatException invalid) {
        return 4;
    }
}

// <editor-fold defaultstate="collapsed" desc="Recall · F31">
/// ### Parse, validate, then commit
///
/// Parsing converts text; validation checks whether the resulting number is allowed. These can fail
/// differently.
///
/// ```java
/// int readCopies(String text) {
///     try {
///         int n = Integer.parseInt(text.strip());
///         if (n < 1 || n > 5) {
///             throw new IllegalArgumentException("Copies: 1..5");
///         }
///         return n;
///     } catch (NumberFormatException invalid) {
///         return 3;
///     }
/// }
/// ```
///
/// `readCopies(" 2 ")` returns `2`: conversion and validation succeed. `"oops"` fails during
/// conversion and reaches `catch`, returning `3`. A numeric `"8"` converts successfully, then throws
/// a different exception; this narrow catch does not handle it. `return` ends the method; `throw`
/// leaves the normal path. Change stored game state only after validation succeeds.
///
/// Predict the path for `"five"`, `"5"` and `"0"`.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `Integer.parseInt(text.strip())` — Converts a whole-number string after trimming its outer spaces.
/// - `value < 4 || value > 160` — The || means either bound violation is enough to reject the number.
/// - `catch (NumberFormatException invalid)` — Runs for text such as oops; a numeric range error is not
///   caught here.
/// - `return 4;` — This fallback controls the width after unparseable input.
///
/// ### What the prepared part already does
///
/// main asks for width and prints the chosen value. The supplied converter samples brightness, chooses a
/// character from its palette and saves the same text in portrait.txt.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Conversion failure is not a bad numeric width
///
/// Trace `" 12 "`: strip removes outer spaces, parseInt produces 12, the guard passes and return
/// produces 12. `"twelve"` fails inside parseInt, so the narrow catch chooses a fallback. `"200"`
/// parses but the explicit range guard throws IllegalArgumentException; the NumberFormatException
/// catch does not intercept that different failure. Predict the paths for `"4"`, `"160"` and `"3"`.
/// Keep both inclusive limits and compare the typo path with the width promised by the prompt.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Strip surrounding whitespace before parsing.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Catch NumberFormatException, not every programming error.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// The fallback is 80; range guards remain separate from that fallback.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
BufferedImage demo() {
    var image = new BufferedImage(240, 160, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < 160; y++) {
        for (int x = 0; x < 240; x++) {
            image.setRGB(x, y, Color.HSBtoRGB(x / 240.0F, 0.7F, 0.2F + y / 200.0F));
        }
    }
    return image;
}

String convert(BufferedImage image, int width) {
    if (width < 4 || width > 160) {
        throw new IllegalArgumentException("Width: 4..160.");
    }
    int rows = Math.max(1, width * image.getHeight() / image.getWidth() / 2);
    String ramp = " .:-=+*#%@";
    var result = new StringBuilder();
    for (int row = 0; row < rows; row++) {
        for (int col = 0; col < width; col++) {
            int rgb = image.getRGB(col * image.getWidth() / width, row * image.getHeight() / rows);
            int light = (((rgb >> 16) & 255) * 299 + ((rgb >> 8) & 255) * 587 + (rgb & 255) * 114) / 1000;
            result.append(ramp.charAt(light * (ramp.length() - 1) / 255));
        }
        result.append('\n');
    }
    return result.toString();
}

/// Start the real program with Run File. Test File checks the rule without opening a window.
/// convert samples image brightness through a supplied ASCII ramp.
/// main reads this option; save/export and rendering stay supplied.
void main(String[] args) throws Exception {
    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    String enteredOption = "";
    if (args.length <= 2) {
        enteredOption = IO.readln("width 4..160 (blank/default 80, q quits): ");
        if (enteredOption == null || enteredOption.strip().equalsIgnoreCase("q")) return;
    }
    try {
        var input = args.length == 0 || args[0].equals("-") ? demo() : ImageIO.read(Path.of(args[0]).toFile());
        if (input == null) {
            throw new IllegalArgumentException("Input must be a readable image.");
        }
        Path output = Path.of(args.length > 1 ? args[1] : "portrait.txt");
        int option = readWidth(args.length > 2 ? args[2] : enteredOption);
        IO.println("width: " + option);
        Files.createDirectories(output.toAbsolutePath().getParent());
        String text = convert(input, option);
        Files.writeString(output, text);
        IO.print(text);
        IO.println("Saved: " + output.toAbsolutePath());
    } catch (IllegalArgumentException invalid) {
        IO.println("Invalid options: " + invalid.getMessage());
    }
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
/// - `'` — Single quotes enclose one character, such as a board marker.
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `++` — Increases a numeric value by one.
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
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `throws` — In a method declaration: tells callers which failure they may need to handle.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
/// - `Path` — A file-system location, not the file’s contents.
/// - `Files` — JDK operations for reading and writing local files.
// </editor-fold>

