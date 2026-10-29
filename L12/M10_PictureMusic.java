// <editor-fold defaultstate="collapsed" desc="Prepared tools">
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
/// From the course folder containing README.md:
/// - Play: `java L12/M10_PictureMusic.java`
/// - Check this mission: `java L12/Check.java M10_PictureMusic.java`
///
/// After the play command, inspect the Java picture and compare Play shift 12 with Play original;
/// Stop interrupts playback, and the repaired shift should change pitch while preserving rhythm.
/// After your repair: save the file, run the check command above, then play again.
///
/// Need an explanation? Recall · F36 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
double pitchFrequency(int row, int shift) {
    if (row < 0 || row >= 12 || shift < -12 || shift > 12) throw new IllegalArgumentException("Row 0..11; transpose -12..12.");
    return 220 * Math.pow(2, (11 - row + shift) / 12.0);
}

// <editor-fold defaultstate="collapsed" desc="Recall · F36">
/// ### Transfer: keep the old promise while adding a choice
///
/// Extracting a helper gives one responsibility a name. Its inputs and result form a contract you can
/// reuse elsewhere.
///
/// ```java
/// String surround(String text, boolean stars) {
///     String edge = stars ? "*" : "#";
///     return edge + text + edge;
/// }
/// ```
///
/// `surround("hi", false)` returns `"#hi#"`; the old hash style still works. The caller can request a
/// new style without reimplementing the operation. Test both choices, an empty input and a different
/// word. Tests establish results, while explaining who chooses a style and who builds the label
/// establishes the responsibility split. A successful new mode should not silently change the old
/// one.
///
/// Predict: what will `surround("", true)` produce, and which part should change if the surrounding
/// symbol changes?
// </editor-fold>

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
/// demo supplies the picture; signal reads its brightness as volume and asks pitchFrequency for
/// each row. The two Play choices compare shift 12 with zero using the same picture and rhythm.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Pitch changes live in the exponent, not the output amplitude.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Add shift to 11-row before dividing by 12.0.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Retain row/shift guards and explain why picture sampling stays outside this helper.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int RATE = 22050;


java.awt.image.BufferedImage demo() {
    var image = new java.awt.image.BufferedImage(32, 12, java.awt.image.BufferedImage.TYPE_INT_RGB);
    for (int x = 0; x < 32; x++) {
        image.setRGB(x, 5 + (int)(4 * Math.sin(x * 0.6)), 0xFFFFFF);
    }
    return image;
}

/// Prepared bounded loops build the audio samples.
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

/// Use the play command above to start the program. The check command checks the rule without
/// opening a window. The supplied part supports only the main game or experiment described above.
volatile javax.sound.sampled.SourceDataLine playingLine;
volatile int playbackVersion;

/// Supplied Java Sound: a worker owns the line; Stop and window closing release it.
/// Play/Stop use a separate Java Sound worker; closing releases it.
void stopAudio() {
    playbackVersion++;
    var line = playingLine;
    playingLine = null;
    if (line != null) {
        line.stop();
        line.flush();
        line.close();
    }
}

void playAudio(double[] samples, javax.swing.JLabel status) {
    stopAudio();
    int ticket = playbackVersion;
    status.setText("Playing in Java; Stop interrupts playback.");
    var worker = new Thread(() -> {
        javax.sound.sampled.SourceDataLine line = null;
        try {
            byte[] pcm = new byte[samples.length * 2];
            for (int i = 0; i < samples.length; i++) {
                int value = (int)(Math.max(-0.8, Math.min(0.8, samples[i])) * 32767);
                pcm[i * 2] = (byte)value;
                pcm[i * 2 + 1] = (byte)(value >> 8);
            }
            var format = new javax.sound.sampled.AudioFormat(RATE, 16, 1, true, false);
            line = javax.sound.sampled.AudioSystem.getSourceDataLine(format);
            line.open(format);
            if (ticket != playbackVersion) return;
            playingLine = line;
            line.start();
            int offset = 0;
            while (offset < pcm.length && ticket == playbackVersion) {
                int written = line.write(pcm, offset, Math.min(4096, pcm.length - offset));
                if (written <= 0) break;
                offset += written;
            }
            if (ticket == playbackVersion) {
                line.drain();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (ticket == playbackVersion) status.setText("Finished; Play replays the same sound.");
                });
            }
        } catch (Exception failure) {
            if (ticket == playbackVersion) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (ticket == playbackVersion) status.setText("Audio unavailable: " + failure.getMessage());
                });
            }
        } finally {
            if (line != null) {
                line.stop();
                line.close();
                if (playingLine == line) playingLine = null;
            }
        }
    }, "java-sound-player");
    worker.setDaemon(true);
    worker.start();
}

void showAudio(java.awt.image.BufferedImage picture, String title, String detail, String[] captions, double[][] sounds) {
    if (java.awt.GraphicsEnvironment.isHeadless()) return;
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame(title);
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        var status = new javax.swing.JLabel(detail);
        var controls = new javax.swing.JPanel();
        for (int i = 0; i < sounds.length; i++) {
            int index = i;
            var play = new javax.swing.JButton("Play " + captions[i]);
            play.addActionListener(event -> playAudio(sounds[index], status));
            controls.add(play);
        }
        var stop = new javax.swing.JButton("Stop");
        stop.addActionListener(event -> {
            stopAudio();
            status.setText("Stopped; choose Play to listen again.");
        });
        controls.add(stop);
        frame.add(status, java.awt.BorderLayout.NORTH);
        frame.add(new javax.swing.JLabel(new javax.swing.ImageIcon(picture.getScaledInstance(640, 240, java.awt.Image.SCALE_FAST))), java.awt.BorderLayout.CENTER);
        frame.add(controls, java.awt.BorderLayout.SOUTH);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent event) { stopAudio(); }
        });
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}

void main() {
    var image = demo();
    transpose = 12;
    var shifted = signal(image);
    transpose = 0;
    var original = signal(image);
    showAudio(image, "Picture Music", "The same image: shifted notes keep the original rhythm.", new String[]{"shift 12", "original"}, new double[][]{shifted, original});
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Small Java map · optional">
/// ## An unfamiliar piece of Java?
///
/// This small map is optional. Look up just the symbol or word you need; no need to learn it all first.
///
/// - `///` — Words for the reader. Java skips these comment lines when running.
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
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `null` — No object is present.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `throw` — Reports a failure by creating or passing an exception.
// </editor-fold>

