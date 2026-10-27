// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
// </editor-fold>

/// # L11.01 · TinyStepSequencer
///
/// Build a tiny drum pattern by switching cells on and off. Four rows and eight beats are enough to make
/// a loop worth exporting and comparing with a friend’s.
///
/// **Your mission:** A note can be switched on but not off; the second click leaves it selected. Repair
/// toggling while preserving invalid-coordinate handling.
///
/// **Today you learn:** A Set answers whether a distinct value is present. A toggle has two paths: a
/// previously absent Note becomes present, while an already present Note must stop belonging to the set.
///
/// After Run File, click cells to switch notes on and off, choose BPM and click Export WAV to save a
/// rhythm you can open in a player.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Learn · F23 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
boolean toggle(int row, int step) {
    if (row < 0 || row >= 4 || step < 0 || step >= 8) return false;
    Note note = new Note(row, step);
    notes.add(note);
    return true;
}

// <editor-fold defaultstate="expanded" desc="Learn · F23">
/// ### Set: membership without duplicates
///
/// A `Set` answers whether a value belongs to a collection. It does not reserve an indexed place for
/// each insertion.
///
/// ```java
/// Set<String> stickers = new HashSet<>();
/// IO.println(stickers.add("moon"));
/// IO.println(stickers.add("moon"));
/// IO.println(stickers.size());
/// ```
///
/// The output is `true`, `false`, then `1`. The first add changes membership; the second finds an
/// equal value already present. `contains(value)` asks without changing the set. `remove(value)`
/// removes that value and reports whether it was present. A record's generated equality compares its
/// component values, so two separately created equal records represent the same set member. Do not
/// depend on HashSet iteration order.
///
/// Predict: after removing moon and adding sun, what are the size and the result of
/// `contains("moon")`?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `new Note(row, step)` — A record value identifies one cell by both its row and beat.
/// - `notes.add(note)` — Adds a distinct note, but adding the same value twice does not remove it.
/// - `row < 0 || row >= 4` — Rejects rows outside the four-row grid before touching the set.
///
/// ### What the prepared part already does
///
/// Each button calls toggle and refreshes its selection from notes. The supplied synthesiser reads that
/// same set, combines finite instrument sounds and writes a WAV at the selected tempo; it does not
/// autoplay.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Equal Note records name the same grid position.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Set.remove returns whether the note was present.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Only add when removal found nothing; two toggles must restore the old Set.
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
record Note(int row, int step) {}
Set<Note> notes = new HashSet<>();
JToggleButton[][] buttons = new JToggleButton[4][8];
int bpm = 120;

/// Supplied restart: create the initial model; no native window is created here.
void reset() {
    notes.clear();
    for (int step = 0; step < 8; step++) {
        notes.add(new Note(step % 4, step));
    }
}

double[] sequence(int repeats) {
    if (bpm < 40 || bpm > 240 || repeats < 1 || repeats > 8) {
        throw new IllegalArgumentException("BPM 40..240; loops 1..8.");
    }
    double stepFrames = RATE * 30.0 / bpm;
    int[] pitches = {60, 64, 67, 72};
    double[] output = new double[(int)Math.round(stepFrames * 8 * repeats)];
    for (int step = 0; step < repeats * 8; step++) {
        int start = (int)Math.round(step * stepFrames);
        int end = (int)Math.round((step + 1) * stepFrames);
        int frames = end - start;
        for (int voice = 0; voice < 4; voice++) {
            if (!notes.contains(new Note(voice, step % 8))) {
                continue;
            }
            double frequency = 440 * Math.pow(2, (pitches[voice] - 69) / 12.0);
            for (int i = 0; i < frames; i++) {
                double fade = Math.min(1, Math.min(i, frames - 1 - i) / 100.0);
                output[start + i] += 0.12 * fade * Math.sin(2 * Math.PI * frequency * i / RATE);
            }
        }
    }
    return output;
}

void refresh() {
    for (int row = 0; row < 4; row++) {
        for (int step = 0; step < 8; step++) {
            buttons[row][step].setSelected(notes.contains(new Note(row, step)));
        }
    }
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Tiny Step Sequencer");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var grid = new JPanel(new GridLayout(4, 8, 4, 4));
    String[] names = {"C", "E", "G", "C+"};
    for (int row = 0; row < 4; row++) {
        for (int step = 0; step < 8; step++) {
            int r = row;
            int s = step;
            var button = new JToggleButton(names[row] + " " + (step + 1));
            button.addActionListener(event -> { toggle(r, s); refresh(); });
            buttons[row][step] = button;
            grid.add(button);
        }
    }
    var controls = new JPanel();
    var tempo = new JSpinner(new SpinnerNumberModel(120, 40, 240, 5));
    tempo.addChangeListener(event -> bpm = (Integer)tempo.getValue());
    var clear = new JButton("Clear");
    clear.addActionListener(event -> {
        notes.clear();
        refresh();
    });
    var restore = new JButton("Restore");
    restore.addActionListener(event -> {
/// Supplied restart: create the initial model; no native window is created here.
        reset();
        refresh();
    });
    var status = new JLabel("Export four loops; open the WAV to play or stop.");
    var export = new JButton("Export WAV");
    export.addActionListener(event -> {
        try {
/// Supplied WAV encoding: bounded PCM samples and try-with-resources own the stream.
            write(Path.of("tiny-sequencer.wav"), sequence(4));
            status.setText("Saved tiny-sequencer.wav");
        } catch (Exception failure) {
            status.setText(failure.getMessage());
        }
    });
    controls.add(new JLabel("BPM:"));
    controls.add(tempo);
    controls.add(clear);
    controls.add(restore);
    controls.add(export);
    frame.add(status, BorderLayout.NORTH);
    frame.add(grid, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    refresh();
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

/// Start the real program with Run File. Test File checks the rule without opening a window.
/// toggle is the real button action; notes contains immutable Note values.
/// sequence builds four voices at the chosen tempo; write exports them without autoplay; close disposes the window.
void main(String[] args) throws Exception {
    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
/// Supplied restart: create the initial model; no native window is created here.
    reset();
    if (args.length > 0 && args[0].equals("export")) {
        if (args.length > 2) {
            bpm = Integer.parseInt(args[2]);
        }
        Path path = Path.of(args.length > 1 ? args[1] : "tiny-sequencer.wav");
/// Supplied WAV encoding: bounded PCM samples and try-with-resources own the stream.
        write(path, sequence(4));
        IO.println("Saved: " + path.toAbsolutePath());
    } else {
        SwingUtilities.invokeLater(this::showWindow);
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
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `%` — The remainder after division; useful for repeated positions or turns.
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
/// - `else` — The alternative when the preceding if condition is false.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `record` — A small named bundle of values. Its component accessors use the component names with ().
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `throws` — In a method declaration: tells callers which failure they may need to handle.
/// - `Set` — Stores distinct values; the same value is present at most once.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
/// - `Path` — A file-system location, not the file’s contents.
/// - `Files` — JDK operations for reading and writing local files.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>

