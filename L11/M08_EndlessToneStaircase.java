// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
// </editor-fold>

/// # L11.08 · EndlessToneStaircase
///
/// Make a finite tone staircase from overlapping octave layers. Listen for whether the repeated climb
/// seems to hide its return; the impression depends on the listener and playback.
///
/// **Your mission:** Build the WAV destination boundary in write. Prepare its parent directory, open
/// the destination output stream and the supplied audio(signal) stream with try-with-resources, and
/// encode WAVE into that destination. Do not shorten the Path or write to a substitute buffer.
///
/// **Today you learn:** Implement a real file boundary: open the chosen resource, route actual
/// contents through it and close it reliably. The artifact must survive beyond a method call.
///
/// The program writes exports/shepard.wav through write and loads that exact WAV for Java
/// Play/Stop. A wrong destination produces a clear terminal output failure; no substitute file
/// is played.
void write(Path path, double[] signal) throws Exception {
    Files.createDirectories(path.toAbsolutePath().getParent());
    try (var output = Files.newOutputStream(path);
         var input = audio(signal)) {
        AudioSystem.write(input, AudioFileFormat.Type.WAVE, output);
    }
}

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Produce two short sound postcards with different octave-layer counts. Give them distinct
/// filenames, then reopen and listen to the saved WAV files on a later run. Which layer count hides
/// the apparent reset best? This is a listening judgement, not a claim that every ear hears an
/// endless rise.
// </editor-fold>

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

// <editor-fold defaultstate="expanded" desc="Read this code">
/// ### A durable sound postcard
///
/// A Path names a destination; an OutputStream receives its bytes. For a neutral text example, `try
/// (var out = Files.newOutputStream(Path.of("note.txt"))) { out.write(new byte[]{65, 66}); }` writes
/// the two UTF-8 bytes for AB, then closes the stream even on failure. Predict which bytes a later
/// write must replace to save CD instead. Opening a stream alone does not encode a WAV header.
///
/// Here audio(signal) supplies the sample format and readable PCM bytes. AudioSystem.write(input,
/// WAVE, output) adds the WAV container. Two resource declarations are separated by a semicolon and
/// both are closed automatically. createDirectories permits an existing directory. main reopens
/// exactly exports/shepard.wav and offers Play/Stop. The saved container is the thing being tested
/// and heard.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Reuse audio(signal); sample conversion is supplied, destination ownership is yours.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Use the complete path for newOutputStream, after creating its absolute parent.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Put output and input in one try-with-resources and call the three-argument AudioSystem.write with
/// WAVE and output.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
AudioInputStream audio(double[] signal) {
    byte[] pcm = new byte[signal.length * 2];
    for (int i = 0; i < signal.length; i++) {
        int value = (int)(Math.max(-0.8, Math.min(0.8, signal[i])) * 32767);
        pcm[i * 2] = (byte)value;
        pcm[i * 2 + 1] = (byte)(value >> 8);
    }
    var format = new AudioFormat(RATE, 16, 1, true, false);
    return new AudioInputStream(new ByteArrayInputStream(pcm), format, signal.length);
}

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
