// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import module java.base;
// </editor-fold>

class M11_SecretBeeps {
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
    /// From the course folder containing README.md:
    /// - Play: `java L12/M11_SecretBeeps.java`
    /// - Check this mission: `java L12/Check.java M11_SecretBeeps.java`
    ///
    /// After the play command, type a message or press Enter for E E; compare Play gap 10 with Play gap
    /// 7 in Java, and use Stop or replay to hear word separation. After your repair: save the file, run
    /// the check command above, then play again.
    ///
    /// Need an explanation? Recall · F36 explains the Java skill; Read this code explains these lines.
    /// Hints 1 and 2 open separately; Repair outline reveals the direction.
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
    /// - `wordGap < 3 || wordGap > 12` — Validates the requested gap before building any timing units.
    /// - `result.add(-7)` — Negative units mean silence; this constant currently fixes every word gap.
    /// - `result.add(-3)` — Keeps the separate three-unit pause between letters.
    /// - `charAt(m) == '.' ? 1 : 3` — A dot is one sounding unit and a dash three.
    ///
    /// ### What the prepared part already does
    ///
    /// code supplies Morse marks, units determines their lengths and signal produces samples. The two
    /// Play choices compare the required ten-unit word gap with the original seven-unit rule.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
    /// Negative unit values represent silence lengths.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
    /// Only the w>0 branch separates words.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
    /// Use -wordGap there, leave the other two gap rules intact, and explain why text coding stays
    /// separate from timing.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
    final int RATE = 22050;


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

    /// Prepared bounded loops build the audio samples.
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
        String input = IO.readln("Message (blank E E, q quits): ");
        if (input == null || input.strip().equalsIgnoreCase("q")) return;
        String text = input.isBlank() ? "E E" : input;
        try {
            wordGap = 10;
            var changed = signal(text, 60);
            wordGap = 7;
            var original = signal(text, 60);
            showAudio("Secret Beeps", "Message: " + text + " | " + code(text), new String[]{"gap 10", "gap 7"}, new double[][]{changed, original});
        } catch (IllegalArgumentException failure) {
            IO.println(failure.getMessage());
        }
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
    /// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
    /// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
    // </editor-fold>

}
