import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

/// # Solved task · TypeMelody
/// Lesson: 3.3 Let the player shape the story — 03-stories.html
///
/// Original assignment (this version is complete):
///
/// **Turn a message into a melody.** Run the program and press Play / Replay. In `J4V4`, letters
/// make sounds and digits leave silence.
///
/// **Your task:** In `defaultMessage` below, change only the characters between the double
/// quotation marks. The new message must be exactly `JAVA`. Keep `return`, the outer quotation
/// marks and the semicolon.
///
/// **Result:** A newly launched window will show Message: JAVA. Play / Replay will play four
/// notes instead of two notes separated by silence.

String defaultMessage() {
    return "JAVA";
}

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// After the JAVA check, write a short musical signature for a friend. Keep some digits or spaces as
/// rests. Compare JAVA, J A V A and your own message: which sounds recognisable? The original check
/// still asks for JAVA; your signature is a separate experiment.
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
            + (allRests ? "This message contains only rests. Use letters A-Z for notes.<br>" : "This message contains notes. Press Play to listen.<br>")
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

// </editor-fold>
