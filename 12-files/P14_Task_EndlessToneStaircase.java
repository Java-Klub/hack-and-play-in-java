import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;

/// # Solved task · EndlessToneStaircase
/// Lesson: 12.4 Read data from a real file — 04-files.html
///
/// Original assignment (this version is complete):
///
/// Make a finite tone staircase from overlapping octave layers. Listen for whether the repeated climb
/// seems to hide its return; the impression depends on the listener and playback.
///
/// **Your mission:** Build the WAV destination boundary in write. Prepare its parent directory, open
/// the destination output stream and the supplied audio(signal) stream with try-with-resources, and
/// encode WAVE into that destination. Do not shorten the Path or write to a substitute buffer.
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
