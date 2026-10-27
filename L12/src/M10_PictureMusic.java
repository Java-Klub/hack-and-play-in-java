// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
// </editor-fold>

/// # L12.10 · PictureMusic
///
/// Listen to a little picture as if its rows were notes and its columns were time. Transposing it should
/// move the tune up or down while preserving its rhythm and outline.
///
/// **Your mission:** The transpose input is accepted but has no effect on pitch. Extend the extracted
/// frequency rule while keeping shift zero identical to the original sound.
///
/// **Today you learn:** A small extracted operation can add one new capability without changing the
/// caller’s job. pitchFrequency combines a familiar row-to-note rule with a semitone shift; zero remains
/// the compatibility case.
///
/// After Run File, enter 0 and then rerun with 12, opening picture-music-0.wav and picture-music-12.wav
/// in a player to compare pitch.
/// After your repair: Save → Test File → play again.
double pitchFrequency(int row, int shift) {
    if (row < 0 || row >= 12 || shift < -12 || shift > 12) throw new IllegalArgumentException("Row 0..11; transpose -12..12.");
    return 220 * Math.pow(2, (11 - row) / 12.0);
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int row, int shift` — The operation receives both picture position and requested transposition.
/// - `shift < -12 || shift > 12` — Keeps transposition within one octave in either direction.
/// - `(11 - row) / 12.0` — Converts row distance into an octave fraction; the decimal divisor preserves
///   fractional steps.
/// - `220 * Math.pow(2,` — Scales a base frequency by an octave ratio, not by adding a fixed number of
///   hertz.
///
/// ### What the prepared part already does
///
/// The supplied picture-to-sound loop turns brightness into note strength and calls pitchFrequency for
/// row pitches. main asks for transpose and write saves the WAV; explain why that new rule belongs in the
/// small operation rather than in every caller.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. Pitch changes live in the exponent, not the output amplitude.
/// 2. Add shift to 11-row before dividing by 12.0.
/// 3. Retain row/shift guards and explain why picture sampling stays outside this helper.
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

java.awt.image.BufferedImage demo() {
    var image = new java.awt.image.BufferedImage(32, 12, java.awt.image.BufferedImage.TYPE_INT_RGB);
    for (int x = 0; x < 32; x++) {
        image.setRGB(x, 5 + (int)(4 * Math.sin(x * 0.6)), 0xFFFFFF);
    }
    return image;
}

/// Supplied synthesis: bounded sample loops build audio; there is no autoplay.
double[] signal(java.awt.image.BufferedImage image) {
    int step = RATE / 10;
    double[] signal = new double[32 * step];
    for (int col = 0; col < 32; col++) {
        for (int row = 0; row < 12; row++) {
            int x = Math.min(image.getWidth() - 1, (int)((col + 0.5) * image.getWidth() / 32));
            int y = Math.min(image.getHeight() - 1, (int)((row + 0.5) * image.getHeight() / 12));
            int rgb = image.getRGB(x, y);
            double brightness = (((rgb >> 16) & 255) + ((rgb >> 8) & 255) + (rgb & 255)) / 765.0;
            double frequency = pitchFrequency(row, transpose);
            for (int i = 0; i < step; i++) {
                double fade = Math.min(1, Math.min(i, step - 1 - i) / 100.0);
                signal[col * step + i] += brightness / 40 * fade * Math.sin(2 * Math.PI * frequency * i / RATE);
            }
        }
    }
    return signal;
}

int transpose = 0;

/// Start the real program with Run File. Test File checks the rule without opening a window.
/// pitchFrequency owns row/transpose mathematics; signal owns brightness and time-column sampling.
/// demo gives an original image; main can use a local image and optional transpose argument; write exports WAV.
void main(String[] args) throws Exception {
    if (args.length == 0) {
        String entered = IO.readln("Transpose -12..12 (blank 12, q quits): ");
        if (entered == null || entered.strip().equalsIgnoreCase("q")) return;
        String shift = entered.isBlank() ? "12" : entered.strip();
        args = new String[]{"picture-music-" + shift + ".wav", "-", shift};
    }

    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    Path path = Path.of(args.length > 0 ? args[0] : "picture-music.wav");
    var image = args.length > 1 && !args[1].equals("-") ? javax.imageio.ImageIO.read(Path.of(args[1]).toFile()) : demo();
    if (image == null) {
        IO.println("Use a readable local image.");
        return;
    }
    if (args.length > 2) transpose = Integer.parseInt(args[2]);
/// Supplied WAV encoding: bounded PCM samples and try-with-resources own the stream.
    write(path, signal(image));
    IO.println("Saved sonification: " + path.toAbsolutePath());
    IO.println("32 columns become time; 12 rows become pitches; brightness sets volume. This is not recorded space sound.");
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
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `throws` — In a method declaration: tells callers which failure they may need to handle.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
/// - `Path` — A file-system location, not the file’s contents.
/// - `Files` — JDK operations for reading and writing local files.
// </editor-fold>

