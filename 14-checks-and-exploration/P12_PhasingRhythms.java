// <editor-fold defaultstate="collapsed" desc="Prepared tools">
// </editor-fold>

/// # 14.12 · PhasingRhythms
///
/// Layer two short rhythms that run at slightly different intervals. Their changing alignment creates new
/// accents even though neither individual pattern changes.
///
/// **Your mission:** Create makeVoice(beat, gain): its returned Voice must produce gain × tap(time,
/// beat). Install two voices using (0.12, 1) and (0.13, 0.7). Keep the supplied mix combining both
/// roles; change no playback code.
///
/// **Today you learn:** Author an implementation of a small interface and install it in the running
/// program. Keep its calculation separate from input, updates and drawing.
///
/// Run the program, then compare Play two voices with Play one voice in Java; listen for
/// changing overlap, use Stop and replay after repairing mix.
Voice makeVoice(double beat, double gain) {
    if (!Double.isFinite(beat) || beat <= 0 || !Double.isFinite(gain) || gain < 0 || gain > 1) {
        throw new IllegalArgumentException("Positive beat; gain 0..1.");
    }
    return time -> {
        double sample = tap(time, beat);
        return gain * sample;
    };
}

Voice first = makeVoice(0.12, 1);
Voice second = makeVoice(0.13, 0.7);

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Compose a miniature duet: choose the two beat intervals and gains, then alter the boolean rhythm
/// for a recognisable repeated phrase. Compare a near-unison duet with equal intervals. Stop before
/// changing it and replay from the beginning for a fair comparison.
// </editor-fold>

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

// <editor-fold defaultstate="expanded" desc="Read this code">
/// ### Construct voices; keep the mixer ignorant of their recipes
///
/// Voice promises sample(time). makeVoice captures beat and gain, while time arrives later for every
/// audio sample. The supplied tap calculates an individual rhythm. Your implementation applies gain
/// to that result; it must not advance a shared clock or call playback.
///
/// A neutral example:
///
/// ```java
/// DoubleUnaryOperator scale(double factor) {
///     return x -> {
///         double result = x * factor;
///         return result;
///     };
/// }
/// ```
///
/// The JDK DoubleUnaryOperator role maps one double to another. This factory creates scale(0.5),
/// which maps 0.8 to 0.4. Predict the output for -0.6. A captured factor describes a particular rule,
/// not a changing global input.
///
/// Here gain 0 gives silence; gain 1 preserves tap exactly. mix asks both stored Voice values at the
/// same time and combines them at a safe level. Creating a factory but leaving first/second as silent
/// lambdas keeps the game silent. Wire both, then compare equal beat intervals with the specified
/// slight mismatch.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Delegate rhythm generation to tap(time, beat), then scale its result.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Return the lambda from makeVoice; do not call it during factory creation.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Replace both silent field initialisers with factory calls using the specified beat/gain pairs.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
double mix(double time) {
    return 0.17 * (first.sample(time) + second.sample(time));
}

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
