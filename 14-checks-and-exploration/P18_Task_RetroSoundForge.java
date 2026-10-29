/// # Solved task · RetroSoundForge
/// Lesson: 14.3 Ask which kind of data arrived — 03-patterns.html
///
/// Original assignment (this version is complete):
///
/// **Make the retro tone wobble.** Play wobble currently holds one pitch. It should smoothly vary
/// around the supplied centre frequency.
///
/// **Your task:** Edit the Wobble branch of `frequency` below. Complete eight full oscillations
/// above and below the centre over the whole sound. `depth` is the maximum deviation in either
/// direction; time `t` runs from 0 to 1. The For this game section explains the exact sine rule
/// and examples.
///
/// **Result:** Play wobble plays a varying tone. Zero depth keeps it steady. Preserve Play laser,
/// Play jump and the rejection of invalid times.

double frequency(Sound sound, double t) {
    if (!Double.isFinite(t) || t < 0 || t > 1) throw new IllegalArgumentException("Time: 0..1.");
    return switch (sound) {
        case Laser(var start, var ratio) -> start * Math.pow(ratio, t);
        case Jump(var base, var rise) -> base + rise * t;
        case Wobble(var base, var depth) -> base + depth * Math.sin(t * Math.PI * 16);
    };
}

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Author a robot greeting from two different Sound records: synthesise a short jump followed by a
/// gentle wobble and join the sample arrays. Give the greeting a name. Keep playback quiet and
/// compare the timing rather than increasing amplitude.
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

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int RATE = 22050;

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
