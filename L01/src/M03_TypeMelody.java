// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
// </editor-fold>

/// # L01.03 · TypeMelody
///
/// Turn a short written message into a tiny melody. Each letter gets a note, while other characters leave
/// a quiet time slot, so different messages have different rhythms.
///
/// **Your mission:** The first WAV contains four silent slots. Change the message supplied by
/// defaultMessage so the opening melody spells JAVA.
///
/// **Today you learn:** Text literals: double quotes make their contents text data. For example, a quoted
/// digit stays a character in the message; it does not become a calculation.
///
/// Use Run File, locate melody.wav in the project’s Files tab and open the saved file in an audio player
/// to hear your message.
/// After your repair: Save → Test File → play again.
String defaultMessage() {
    return "JAVA";
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `String` — The result is a String: a sequence of text characters.
/// - `defaultMessage()` — The name followed by empty parentheses is a supplied question with no input:
///   what message should be used?
/// - `{` — The braces enclose the statements that answer that question.
/// - `return` — Give the following value back to the code that asked for the message.
/// - `"1234"` — The quotes are Java notation; the four characters inside become the actual message.
/// - `;` — End this return statement here.
///
/// ### What the prepared part already does
///
/// main calls defaultMessage to get the text and writes melody.wav. synthesize turns its letters into
/// sound samples, and write saves a playable WAV. That audio machinery is supplied; there is no live
/// sound player inside this program.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. Only the text between the quotation marks is your message.
/// 2. This ready-made synthesizer gives digits silent time slots.
/// 3. Use return "JAVA"; then export and open melody.wav again.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int SAMPLE_RATE = 22050;
final int NOTE_SAMPLES = 2205;

byte[] synthesize(String text, long seed) {
    if (text.isEmpty() || text.length() > 120) {
        throw new IllegalArgumentException("Use 1..120 characters.");
    }
    int[] scale = {0, 2, 4, 5, 7, 9, 11};
    int root = 60 + new Random(seed).nextInt(12);
    byte[] pcm = new byte[text.length() * NOTE_SAMPLES * 2];
    for (int letter = 0; letter < text.length(); letter++) {
        char character = text.charAt(letter);
        if (character >= 'A' && character <= 'Z') character = (char) (character + 'a' - 'A');
        if (character < 'a' || character > 'z') continue;
        int index = character - 'a';
        int note = root + scale[index % scale.length] + 12 * (index / 7 % 2);
        double frequency = 440 * Math.pow(2, (note - 69) / 12.0);
        for (int sample = 0; sample < NOTE_SAMPLES; sample++) {
            double fade = Math.min(1.0, Math.min(sample, NOTE_SAMPLES - 1 - sample) / 220.0);
            int value = (int) (6000 * fade * Math.sin(2 * Math.PI * frequency * sample / SAMPLE_RATE));
            int offset = (letter * NOTE_SAMPLES + sample) * 2;
            pcm[offset] = (byte) value;
            pcm[offset + 1] = (byte) (value >> 8);
        }
    }
    return pcm;
}

void write(String text, long seed, Path path) throws Exception {
    byte[] pcm = synthesize(text, seed);
    var format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
    Files.createDirectories(path.toAbsolutePath().getParent());
    try (var stream = new AudioInputStream(new ByteArrayInputStream(pcm), format, pcm.length / 2)) {
        AudioSystem.write(stream, AudioFileFormat.Type.WAVE, path.toFile());
    }
}

void main() throws Exception {
    Path path = Path.of("melody.wav");
    write(defaultMessage(), 42, path);
    IO.println("Saved: " + path.toAbsolutePath());
    IO.println("Open the WAV to hear the message; digits are rests.");
}

/// Prepared entry points: main, write.
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
/// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
/// - `final` — The named value cannot be assigned another value afterwards. A referenced mutable object
///   can still change.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
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
/// - `:` — In for (int value : values), reads each element in turn. It also separates the two
///   alternatives of ? : or follows a switch label.
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `throws` — In a method declaration: tells callers which failure they may need to handle.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
/// - `Path` — A file-system location, not the file’s contents.
/// - `Files` — JDK operations for reading and writing local files.
// </editor-fold>

