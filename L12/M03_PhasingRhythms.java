// <editor-fold defaultstate="collapsed" desc="Prepared tools">
// </editor-fold>

/// # L12.03 · PhasingRhythms
///
/// Layer two short rhythms that run at slightly different intervals. Their changing alignment creates new
/// accents even though neither individual pattern changes.
///
/// **Your mission:** Only the first voice reaches the mix. Restore a two-voice result at the same
/// requested time without rewriting either voice’s sound.
///
/// **Today you learn:** Composition combines small roles through their shared interface. Both Voice
/// values answer a time-to-sample question; the mixer combines their answers rather than knowing how each
/// rhythm works.
///
/// From the course folder containing README.md:
/// - Play: `java L12/M03_PhasingRhythms.java`
/// - Check this mission: `java L12/Check.java M03_PhasingRhythms.java`
///
/// After the play command, compare Play two voices with Play one voice in Java; listen for changing
/// overlap, use Stop and replay after repairing mix. After your repair: save the file, run the
/// check command above, then play again.
///
/// Need an explanation? Recall · F34 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
double mix(double time) {
    return 0.17 * first.sample(time);
}

// <editor-fold defaultstate="collapsed" desc="Recall · F34">
/// ### Interface: ask a role to do its job
///
/// An interface names an operation its implementations provide. The caller can use that role without
/// choosing the implementation's formula.
///
/// ```java
/// interface Points {
///     int award(int n);
/// }
/// record DoublePoints() implements Points {
///     public int award(int n) {
///         return n * 2;
///     }
/// }
/// ```
///
/// With `Points rule = new DoublePoints();`, `rule.award(3)` returns 6. `implements Points` promises
/// the operation; the implementation must supply its public method. The variable rule refers to the
/// selected object, so call its operation with the current input rather than repeating a fixed
/// formula in the caller. Another implementation can supply another rule through the same interface.
///
/// Predict: what does this implementation return for `rule.award(5)`, and which code would change to
/// award three times the input?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `mix(double time)` — The caller asks for one combined sample at this particular instant.
/// - `first.sample(time)` — Calls the installed first voice with the caller’s time.
/// - `0.17 *` — Applies the output gain; consider which contributing voices should share it.
///
/// ### What the prepared part already does
///
/// signal asks mix for each sample; first and second provide the two beat intervals. The only
/// playback choices compare the current two-role mix with one voice, making the missing role
/// audible.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Voice describes a time-to-sample role.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Do not call only the first field or use two different times.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Keep the gain outside the sum so both replaceable voices get the same treatment.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int RATE = 22050;


boolean[] rhythm = {true, false, true, false, true, true, false, true, false, true, false, true};

double tap(double time, double beat) {
    int index = (int)(time / beat);
    double within = time - index * beat;
    if (!rhythm[index % rhythm.length] || within > 0.035) {
        return 0;
    }
    return Math.sin(2 * Math.PI * 1200 * within) * Math.exp(-within * 120);
}

/// Prepared bounded loops build the audio samples.
double[] signal(double seconds) {
    if (!Double.isFinite(seconds) || seconds < 1 || seconds > 40) {
        throw new IllegalArgumentException("Duration: 1..40 seconds.");
    }
    double[] signal = new double[(int)(RATE * seconds)];
    for (int i = 0; i < signal.length; i++) {
        double time = i / (double)RATE;
        signal[i] = mix(time);
    }
    return signal;
}

interface Voice { double sample(double time); }
Voice first = time -> tap(time, 0.12);
Voice second = time -> tap(time, 0.13);

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

void showAudio(String title, String detail, String[] captions, double[][] sounds) {
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
    var both = signal(18.72);
    double[] solo = new double[both.length];
    for (int i = 0; i < solo.length; i++) solo[i] = 0.17 * first.sample(i / (double)RATE);
    showAudio("Phasing Rhythms", "Compare the changing overlap with one voice.", new String[]{"two voices", "one voice"}, new double[][]{both, solo});
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
/// - `boolean` — A value that is either true or false.
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `final` — The named value cannot be assigned another value afterwards. A referenced mutable object
///   can still change.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
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
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `interface` — Describes a role that different implementations can fulfil. The caller can use that
///   role without choosing its internals.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
// </editor-fold>

