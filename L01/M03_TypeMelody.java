// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
// </editor-fold>

/// # L01.03 · TypeMelody
///
/// Turn a short written message into a tiny melody. Each letter gets a note, while other characters leave
/// a quiet time slot, so different messages have different rhythms.
///
/// **Your mission:** The first message contains four silent slots. Change the message supplied by
/// defaultMessage so the opening melody spells JAVA.
///
/// **Today you learn:** Text literals: double quotes make their contents text data. For example, a quoted
/// digit stays a character in the message; it does not become a calculation.
///
/// From the course folder containing README.md:
/// - Play: `java L01/M03_TypeMelody.java`
/// - Check this mission: `java L01/Check.java M03_TypeMelody.java`
///
/// Run the play command above to open the Java player showing the message. Press Play / Replay;
/// digits are silent rests, so the starter 1234 should be silent. Stop ends playback.
///
/// Need an explanation? Learn · F02 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
String defaultMessage() {
    return "JAVA";
}

// <editor-fold defaultstate="expanded" desc="Learn · F02">
/// A String literal is text written between double quotes. Those outside quotes tell Java where the
/// text begins and ends; they are not printed. Digits inside quotes are text too. An expression, by
/// contrast, can calculate a value before printing it.
///
/// These example statements belong inside a called method.
///
/// ```java
/// IO.println("Shelf 4");
/// IO.println(4 + 2);
/// IO.println("Shelf \"B\"");
/// ```
///
/// Predict the three lines. They are `Shelf 4`, `6`, and `Shelf "B"`. In the last literal, each `\"`
/// represents one quote belonging to the text. Its backslash is notation, not another printed
/// character. `IO.println` prints its argument and ends the line. A space inside a literal remains a
/// real space; changing comments does not change the printed message.
///
/// Now predict `IO.println(4 + 5);` and `IO.println("4 + 5");`.
// </editor-fold>

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
/// main calls defaultMessage to get the text and opens the prepared Java audio player. synthesize
/// turns its letters into sound samples, and the prepared player plays them. That audio machinery
/// is supplied; the prepared player owns and closes its Java audio line.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="For this game">
/// Java plays the sound directly; no file or separate player is needed. The panel shows the actual
/// message. The starter digits are rests, not an audio failure. After editing defaultMessage, save,
/// close the window and run the play command again. The new window loads the edited message; Play /
/// Replay repeats the current window's message.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Only the text between the quotation marks is your message.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// This ready-made synthesizer gives digits silent time slots.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Use return "JAVA"; then close the player and run the play command again.
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

// Prepared Java audio: a bounded worker owns the line; Stop and window close cancel it.
volatile int playbackGeneration;
javax.sound.sampled.SourceDataLine soundLine;

synchronized void stopSound() {
    playbackGeneration++;
    if (soundLine != null) {
        soundLine.stop();
        soundLine.close();
        soundLine = null;
    }
}

void playSound(byte[] pcm, javax.swing.JLabel status) {
    stopSound();
    int generation = playbackGeneration;
    status.setText("Playing the current message. Digits are silent rests.");
    var worker = new Thread(() -> {
        var format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        try (var line = AudioSystem.getSourceDataLine(format)) {
            line.open(format);
            synchronized (this) {
                if (generation != playbackGeneration) return;
                soundLine = line;
            }
            line.start();
            int offset = 0;
            while (generation == playbackGeneration && offset < pcm.length) {
                int written = line.write(pcm, offset, Math.min(4096, pcm.length - offset));
                if (written <= 0) break;
                offset += written;
            }
            if (generation == playbackGeneration) {
                line.drain();
                boolean finished = offset == pcm.length;
                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (generation == playbackGeneration) status.setText(finished
                        ? "Finished. Play / Replay starts the message again."
                        : "Playback ended early. Play / Replay tries again.");
                });
            }
        } catch (Exception unavailable) {
            if (generation == playbackGeneration) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (generation == playbackGeneration) status.setText("Audio unavailable: " + unavailable.getMessage());
                });
            }
        } finally {
            synchronized (this) {
                if (generation == playbackGeneration) soundLine = null;
            }
        }
    }, "melody-player");
    worker.setDaemon(true);
    worker.start();
}

void showPlayer() {
    String message = defaultMessage();
    byte[] pcm = synthesize(message, 42);
    boolean allRests = message.matches("[^A-Za-z]*");
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("Message: " + message + ". Letters make notes; digits are silent rests.");
        return;
    }
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame("Type Melody");
        var description = new javax.swing.JLabel("<html>Message: <b>" + message.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
            + "</b><br>Letters A-Z make notes. Digits and other characters are silent rests.<br>"
            + (allRests ? "This message is all rests: 1234 has four silent slots.<br>" : "This message contains notes. Press Play to listen.<br>")
            + "Edit defaultMessage, save, close this window and run the play command again.</html>");
        var status = new javax.swing.JLabel("Click Play to hear this message. Stop ends playback.");
        var buttons = new javax.swing.JPanel();
        var play = new javax.swing.JButton("Play / Replay");
        play.addActionListener(event -> playSound(pcm, status));
        var stop = new javax.swing.JButton("Stop");
        stop.addActionListener(event -> { stopSound(); status.setText("Stopped. Play replays the current message."); });
        buttons.add(play);
        buttons.add(stop);
        var content = new javax.swing.JPanel(new java.awt.BorderLayout(10, 10));
        content.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        content.add(description, java.awt.BorderLayout.NORTH);
        content.add(status);
        content.add(buttons, java.awt.BorderLayout.SOUTH);
        frame.add(content);
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent event) { stopSound(); }
        });
        frame.pack(); frame.setLocationByPlatform(true); frame.setVisible(true);
    });
}

void main() {
    showPlayer();
}

/// Prepared entry points: main.
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
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line.
// </editor-fold>

