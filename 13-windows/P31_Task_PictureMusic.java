/// # Task · PictureMusic
/// Lesson: 13.5 Let painting observe the model — 05-model-view.html
///
/// **Play the same picture one octave higher.** Play shift 12 and Play original currently sound
/// the same: the accepted shift is ignored.
///
/// **Your task:** Extend `pitchFrequency` below with a semitone shift. `+12` doubles every
/// frequency, `-12` halves it, and zero preserves the original sound. The section below the
/// method explains other shifts. Keep the existing input bounds and the method connected to
/// playback.
///
/// **Result:** Play shift 12 sounds one octave higher than Play original. Rhythm, duration and
/// loudness stay unchanged; Stop still ends playback.

double pitchFrequency(int row, int shift) {
    if (row < 0 || row >= 12 || shift < -12 || shift > 12) throw new IllegalArgumentException("Row 0..11; transpose -12..12.");
    return 220 * Math.pow(2, (11 - row) / 12.0);
}

// <editor-fold defaultstate="expanded" desc="How to calculate semitones">
/// An octave doubles the frequency and consists of twelve equal semitone steps. For a shift of n
/// steps, multiply the original frequency by the ratio 2^(n/12).
///
/// For example, Java’s `Math.pow(2, 3 / 12.0)` calculates the ratio for three semitones. Starting
/// from 100 Hz gives about 118.92 Hz. The decimal divisor 12.0 keeps fractional octaves; integer
/// division would lose them. Predict the ratio for six semitones.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Compose a tiny melody by authoring the black cells in demo. Let a friend listen before seeing the
/// picture. Then change transposeShift by one octave and explain why the time pattern stays the same
/// even though the tune sounds higher.
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

volatile javax.sound.sampled.SourceDataLine playingLine;
volatile int playbackVersion;

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
