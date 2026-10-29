// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
// </editor-fold>

/// # L11.08 · EndlessToneStaircase
///
/// Make a tone staircase that seems to keep climbing by blending octave layers. Compare its finite sound
/// with a plain rising tone and listen for where the illusion takes over.
///
/// **Your mission:** The WAV is written in the working directory even when you request a nested
/// destination. Preserve the complete chosen path through the final write operation.
///
/// **Today you learn:** A Path carries both directories and filename. Keeping only its last name changes
/// the destination; preparing parent directories does not fix a later write to another location.
///
/// From the course folder containing README.md:
/// - Play: `java L11/M08_EndlessToneStaircase.java`
/// - Check this mission: `java L11/Check.java M08_EndlessToneStaircase.java`
///
/// the play command writes exports/shepard.wav through write and loads that exact WAV for Java
/// Play/Stop. A wrong destination produces a clear terminal output failure; no substitute file is
/// played. After your repair: save the file, run the check command above, then play again.
///
/// Need an explanation? Learn · F32 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
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
        AudioSystem.write(stream, AudioFileFormat.Type.WAVE, Path.of(path.getFileName().toString()).toFile());
    }
}

// <editor-fold defaultstate="expanded" desc="Learn · F32">
/// ### Files: a destination and a resource lifetime
///
/// A `Path` describes a location; constructing it does not create a file. Inside a method that
/// declares `throws IOException`, this writes a note:
///
/// ```java
/// Path file = Path.of("exports", "note.txt");
/// Files.createDirectories(file.getParent());
/// try (var writer = Files.newBufferedWriter(file)) {
///     writer.write("Hi");
/// }
/// ```
///
/// The result is exports/note.txt containing `Hi`. `getParent()` selects exports; `getFileName()`
/// selects only note.txt and loses the directory. Try-with-resources closes writer when the block
/// ends, including when writing fails. `throws` lets the caller handle an I/O failure; it does not
/// handle it here. Image and audio encoders likewise need the actual destination, not an unrelated
/// memory buffer.
///
/// Predict: if file becomes `Path.of("cards", "one.txt")`, where is the note written?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `path.toAbsolutePath().getParent()` — Finds the full destination’s parent directory so it can be
///   created.
/// - `path.getFileName()` — Extracts only the last name and discards the requested directory.
/// - `try (var stream =` — Closes the owned audio stream when the block finishes, even if writing fails.
/// - `AudioSystem.write` — Encodes the supplied samples as a WAV at the destination it receives.
///
/// ### What the prepared part already does
///
/// signal builds one finite layered tone. main writes it to exports/shepard.wav using write,
/// verifies that same requested WAV, then offers Java Play/Stop for those actual file bytes.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// getFileName deliberately discards the parent path.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The original Path already points to the chosen destination.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Use that Path in AudioSystem.write; retain createDirectories and try-with-resources.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int RATE = 22050;

/// Prepared bounded loops build the audio samples.
double[] signal(int layers) {
    if (layers < 2 || layers > 8) {
        throw new IllegalArgumentException("Octave layers: 2..8.");
    }
    int noteLength = RATE / 4;
    double[] signal = new double[noteLength * 12 * 3];
    for (int i = 0; i < signal.length; i++) {
        int step = i / noteLength % 12;
        int within = i % noteLength;
        double fade = Math.min(1, Math.min(within, noteLength - 1 - within) / 200.0);
        double sum = 0;
        double weightSum = 0;
        for (int octave = 0; octave < layers; octave++) {
            double pitch = octave + step / 12.0;
            double weight = Math.exp(-Math.pow((pitch - layers / 2.0) / 1.3, 2));
            double frequency = 27.5 * Math.pow(2, pitch);
            sum += weight * Math.sin(2 * Math.PI * frequency * within / RATE);
            weightSum += weight;
        }
        signal[i] = 0.18 * fade * sum / weightSum;
    }
    return signal;
}

/// Use the play command above to start the program. The check command checks the rule without
/// opening a window. The supplied part supports only the main game or experiment described above.

/// Supplied Java Sound: a worker owns the line; Stop and window closing release it.
/// Play/Stop use a separate Java Sound worker; closing releases it.


void main() {
    Path path = Path.of("exports", "shepard.wav");
    try {
        Files.deleteIfExists(path);
        write(path, signal(6));
        try (var check = AudioSystem.getAudioInputStream(path.toFile())) {
            IO.println("Loaded " + path + ": " + check.getFrameLength() + " frames.");
        }
        if (java.awt.GraphicsEnvironment.isHeadless()) return;
        var clip = AudioSystem.getClip();
        try (var input = AudioSystem.getAudioInputStream(path.toFile())) { clip.open(input); }
        javax.swing.SwingUtilities.invokeLater(() -> {
            var frame = new javax.swing.JFrame("Endless Tone Staircase: " + path);
            frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
            var controls = new javax.swing.JPanel();
            var play = new javax.swing.JButton("Play");
            play.addActionListener(event -> { clip.setFramePosition(0); clip.start(); });
            var stop = new javax.swing.JButton("Stop");
            stop.addActionListener(event -> clip.stop());
            controls.add(play);
            controls.add(stop);
            frame.add(controls);
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosed(java.awt.event.WindowEvent event) { clip.close(); }
            });
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        });
    } catch (Exception failure) {
        IO.println("Cannot load requested WAV " + path + ": " + failure.getMessage() + ". Check write; Save the source, run its check command, then play again.");
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
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `final` — The named value cannot be assigned another value afterwards. A referenced mutable object
///   can still change.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
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
/// - `IO` — Console input and output. println writes a line.
/// - `Path` — A file-system location, not the file’s contents.
/// - `Files` — JDK operations for reading and writing local files.
// </editor-fold>
