// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
// </editor-fold>

/// # L12.11 · SecretBeeps
///
/// Turn a short message into dots, dashes and beeps. Try a longer word gap and hear how timing alone
/// makes the same symbols easier or harder to separate.
///
/// **Your mission:** The requested word gap is validated but always replaced by seven units. Extend the
/// timing conversion while keeping the original seven-unit mode and all other gaps.
///
/// **Today you learn:** Transfer an existing encoding rule into a parameterised operation. Keep letter
/// and mark timing stable while one new parameter controls word separation; compatibility is a behaviour
/// to check, not a reason to duplicate the whole algorithm.
///
/// After Run File, press Enter for E E, choose a word gap of 7 or 10 and open the matching
/// secret-beeps-gap7.wav or secret-beeps-gap10.wav.
/// After your repair: Save → Test File → play again.
List<Integer> units(String text, int wordGap) {
    if (wordGap < 3 || wordGap > 12) throw new IllegalArgumentException("Word gap: 3..12 units.");
    var result = new ArrayList<Integer>();
    String[] words = code(text).split(" / ");
    for (int w = 0; w < words.length; w++) {
        if (w > 0) {
            result.add(-wordGap);
        }
        String[] letters = words[w].split(" ");
        for (int l = 0; l < letters.length; l++) {
            if (l > 0) {
                result.add(-3);
            }
            for (int m = 0; m < letters[l].length(); m++) {
                if (m > 0) {
                    result.add(-1);
                }
                result.add(letters[l].charAt(m) == '.' ? 1 : 3);
            }
        }
    }
    return result;
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `wordGap < 3 || wordGap > 12` — Validates the requested gap before building any timing units.
/// - `result.add(-7)` — Negative units mean silence; this constant currently fixes every word gap.
/// - `result.add(-3)` — Keeps the separate three-unit pause between letters.
/// - `charAt(m) == '.' ? 1 : 3` — A dot is one sounding unit and a dash three.
///
/// ### What the prepared part already does
///
/// code supplies the Morse symbols and separators. units turns them into signed sound/silence durations,
/// signal builds the samples and write exports WAV; the original one-argument units call keeps its
/// seven-unit default.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. Negative unit values represent silence lengths.
/// 2. Only the w>0 branch separates words.
/// 3. Use -wordGap there, leave the other two gap rules intact, and explain why text coding stays
///   separate from timing.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int RATE = 22050;

/// Supplied WAV encoding: bounded PCM samples and try-with-resources own the stream.
void write(Path path, double[] signal) throws Exception {
    byte[] pcm = new byte[signal.length * 2];
    for (int i = 0; i < signal.length; i++) {
        int value = (int)(Math.max(-0.8, Math.min(0.8, signal[i])) * 32767);
        pcm[i * 2] = (byte)value;
        pcm[i * 2 + 1] = (byte)(value >> 8);
    }
    Files.createDirectories(path.toAbsolutePath().getParent());
    var format = new AudioFormat(RATE, 16, 1, true, false);
    try (var stream = new AudioInputStream(new ByteArrayInputStream(pcm), format, signal.length)) {
        AudioSystem.write(stream, AudioFileFormat.Type.WAVE, path.toFile());
    }
}
String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
String[] morse = {
    ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..",
    ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.",
    "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", "-----",
    ".----", "..---", "...--", "....-", ".....", "-....", "--...", "---..", "----."
};

String code(String text) {
    if (text.isBlank() || text.length() > 40 || !text.matches("[A-Za-z0-9 ]+")) {
        throw new IllegalArgumentException("Use 1..40 ASCII letters, digits and spaces.");
    }
    var words = new StringJoiner(" / ");
    for (String word : text.strip().toUpperCase(Locale.ROOT).split(" +")) {
        var letters = new StringJoiner(" ");
        for (char c : word.toCharArray()) {
            letters.add(morse[alphabet.indexOf(c)]);
        }
        words.add(letters.toString());
    }
    return words.toString();
}

/// Supplied synthesis: bounded sample loops build audio; there is no autoplay.
double[] signal(String text, int milliseconds) {
    if (milliseconds < 20 || milliseconds > 100) {
        throw new IllegalArgumentException("Unit duration: 20..100 ms.");
    }
    var units = units(text, wordGap);
    int frames = RATE * milliseconds / 1000;
    double[] signal = new double[units.stream().mapToInt(Math::abs).sum() * frames];
    int offset = 0;
    for (int unit : units) {
        int length = Math.abs(unit) * frames;
        if (unit > 0) {
            for (int i = 0; i < length; i++) {
                double fade = Math.min(1, Math.min(i, length - 1 - i) / 40.0);
                signal[offset + i] = 0.2 * fade * Math.sin(2 * Math.PI * 650 * i / RATE);
            }
        }
        offset += length;
    }
    return signal;
}

int wordGap = 7;
List<Integer> units(String text) {
    return units(text, 7);
}

/// Start the real program with Run File. Test File checks the rule without opening a window.
/// code maps ASCII letters/digits to Morse; units owns signed tone/silence lengths.
/// signal expands those lengths to samples using wordGap; main optionally selects the new fourth argument and write exports WAV.
void main(String[] args) throws Exception {
    if (args.length == 0) {
        String entered = IO.readln("Message (blank E E, q quits): ");
        if (entered == null || entered.strip().equalsIgnoreCase("q")) return;
        String message = entered.isBlank() ? "E E" : entered;
        String gap = IO.readln("Word gap 3..12 (blank 10, q quits): ");
        if (gap == null || gap.strip().equalsIgnoreCase("q")) return;
        gap = gap.isBlank() ? "10" : gap.strip();
        args = new String[]{"secret-beeps-gap" + gap + ".wav", message, "60", gap};
    }

    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "secret-beeps.wav");
        String text = args.length > 1 ? args[1] : "SOS JAVA";
        int unit = args.length > 2 ? Integer.parseInt(args[2]) : 60;
        if (args.length > 3) wordGap = Integer.parseInt(args[3]);
        IO.println(code(text));
/// Supplied WAV encoding: bounded PCM samples and try-with-resources own the stream.
        write(path, signal(text, unit));
        IO.println("Saved: " + path.toAbsolutePath());
    } catch (IllegalArgumentException invalid) {
        IO.println(invalid.getMessage());
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
/// - `double` — A type for approximate fractional numbers. Java uses a decimal point.
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `'` — Single quotes enclose one character, such as a board marker.
/// - `char` — A type for one UTF-16 code unit, often a simple board character such as X. Text uses
///   String.
/// - `final` — The named value cannot be assigned another value afterwards. A referenced mutable object
///   can still change.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
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
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `throws` — In a method declaration: tells callers which failure they may need to handle.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
/// - `Path` — A file-system location, not the file’s contents.
/// - `Files` — JDK operations for reading and writing local files.
// </editor-fold>

