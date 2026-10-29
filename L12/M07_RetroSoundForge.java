// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import module java.base;
// </editor-fold>

class M07_RetroSoundForge {
    /// # L12.07 · RetroSoundForge
    ///
    /// Forge a short retro sound: a falling laser, a rising jump or a wobbling tone. Their tiny formulas turn
    /// typed parameter cards into very different sound effects.
    ///
    /// **Your mission:** Wobble has become a flat tone because its depth component is unused. Restore
    /// modulation for the requested time while keeping the other formulas and time limits.
    ///
    /// **Today you learn:** A record-pattern switch can choose a formula and unpack its parameters together.
    /// Different Wobble values must use their own centre and excursion, and time must still affect the
    /// result.
    ///
    /// From the course folder containing README.md:
    /// - Play: `java L12/M07_RetroSoundForge.java`
    /// - Check this mission: `java L12/Check.java M07_RetroSoundForge.java`
    ///
    /// After the play command, compare the Java Play wobble, Play laser and Play jump buttons; Stop
    /// interrupts sound, and replay makes the wobble repair audible. After your repair: save the file,
    /// run the check command above, then play again.
    ///
    /// Need an explanation? Recall · F35 explains the Java skill; Read this code explains these lines.
    /// Hints 1 and 2 open separately; Repair outline reveals the direction.
    double frequency(Sound sound, double t) {
        if (!Double.isFinite(t) || t < 0 || t > 1) throw new IllegalArgumentException("Time: 0..1.");
        return switch (sound) {
            case Laser(var start, var ratio) -> start * Math.pow(ratio, t);
            case Jump(var base, var rise) -> base + rise * t;
            case Wobble(var base, var depth) -> base;
        };
    }

    // <editor-fold defaultstate="collapsed" desc="Recall · F35">
    /// ### Record patterns: choose a shape and unpack its data
    ///
    /// ```java
    /// sealed interface Badge permits Name, Stars {}
    /// record Name(String text) implements Badge {}
    /// record Stars(int count) implements Badge {}
    /// String label(Badge badge) {
    ///     return switch (badge) {
    ///         case Name(var text) -> text;
    ///         case Stars(var n) when n > 0 -> n + " stars";
    ///         case Stars(var n) -> "none";
    ///     };
    /// }
    /// ```
    ///
    /// `label(new Stars(2))` returns `"2 stars"`. Sealed closes the alternatives; permits lists them. A
    /// record pattern matches its record type and gives component values local names, in declaration
    /// order. `var` infers their types. The `when` guard must also pass; otherwise matching continues.
    /// The last Stars case handles nonpositive counts. Every permitted alternative needs handling here.
    ///
    /// Predict: which case handles `new Stars(0)`, and what text does `new Name("Nova")` produce?
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Read this code">
    /// ## Read these particular lines
    ///
    /// - `case Wobble(var base, var depth)` — Names the two values carried by this sound alternative.
    /// - `-> base` — Currently returns the centre frequency alone, independent of depth and time.
    /// - `t < 0 || t > 1` — Rejects times outside the normalised interval before selecting a formula.
    /// - `Math.pow(ratio, t)` — The supplied Laser formula uses an exponential ratio rather than a linear
    ///   rise.
    ///
    /// ### What the prepared part already does
    ///
    /// chooseSound creates the three typed sound alternatives; signal repeatedly asks frequency to
    /// interpret them. Java Play/Stop lets you compare wobble, laser and jump without a WAV export.
    // </editor-fold>

    // <editor-fold defaultstate="expanded" desc="For this game">
    /// ### The wobble rule, before any Java repair
    ///
    /// The contract is centre + depth × sin(16πt), with normalised time t from 0 to 1. That means eight
    /// complete modulation cycles during the sound, regardless of its duration in seconds. The sine
    /// ranges from -1 to +1, so depth is the maximum excursion below or above the centre.
    ///
    /// `Math.sin(angle)` expects radians; `Math.PI` supplies π. A complete turn is 2π radians. At t = 0
    /// the sine is zero; at t = 1/32 its angle is π/2 and the sine is +1; at t = 3/32 it is -1. For a
    /// separate Wobble with centre 250 and depth 40, the first two frequencies are 250 and 290. Predict
    /// the third, then change only the depth. Translate the mathematical rule using the two matched
    /// components and t. Keep the time guard and the distinct Laser and Jump behaviours.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
    /// The first Wobble component is the centre frequency; the second is the excursion depth.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
    /// At t = 1/32 the specified modulation reaches +1; at t = 3/32 it reaches -1. Use the rule in For
    /// this game.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
    /// Compose the centre and depth with Math.sin and the angle described in For this game. Preserve the
    /// other cases and the time guard.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
    final int RATE = 22050;


    /// Prepared bounded loops build the audio samples.
    double[] signal(String kind, double seconds) {
        if (!Double.isFinite(seconds) || seconds < 0.1 || seconds > 3) {
            throw new IllegalArgumentException("Duration: 0.1..3 seconds.");
        }
        Sound sound = chooseSound(kind);
        double[] signal = new double[(int)(RATE * seconds)];
        double phase = 0;
        for (int i = 0; i < signal.length; i++) {
            double t = i / (double)signal.length;
            phase += 2 * Math.PI * frequency(sound, t) / RATE;
            double fade = Math.min(1, Math.min(i, signal.length - 1 - i) / 150.0);
            signal[i] = 0.25 * Math.exp(-3 * t) * fade * Math.sin(phase);
        }
        return signal;
    }

    sealed interface Sound permits Laser, Jump, Wobble {}
    record Laser(double start, double ratio) implements Sound {}
    record Jump(double base, double rise) implements Sound {}
    record Wobble(double base, double depth) implements Sound {}
    Sound chooseSound(String kind) {
        return switch (kind) {
            case "laser" -> new Laser(1600, 0.08);
            case "jump" -> new Jump(180, 1100);
            case "wobble" -> new Wobble(400, 180);
            default -> throw new IllegalArgumentException("Sound: laser, jump or wobble.");
        };
    }

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
        showAudio("Retro Sound Forge", "Wobble should bend up and down.", new String[]{"wobble", "laser", "jump"},
                new double[][]{signal("wobble", 0.7), signal("laser", 0.7), signal("jump", 0.7)});
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
    /// - `switch` — Selects one case from a set of alternatives. It can also produce a result.
    /// - `case` — One alternative of a switch; the arrow points to what it does or returns.
    /// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
    ///   later.
    /// - `implements` — Says that a record or class fulfils the named interface role.
    /// - `record` — A small named bundle of values. Its component accessors use the component names with ().
    /// - `interface` — Describes a role that different implementations can fulfil. The caller can use that
    ///   role without choosing its internals.
    /// - `sealed` — Restricts a data type to its explicitly listed alternatives.
    /// - `null` — No object is present.
    /// - `try` — A block whose possible failures can be handled by the following catch.
    /// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
    /// - `throw` — Reports a failure by creating or passing an exception.
    // </editor-fold>

}
