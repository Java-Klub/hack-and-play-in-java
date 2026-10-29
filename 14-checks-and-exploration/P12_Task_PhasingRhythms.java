/// # Solved task · PhasingRhythms
/// Lesson: 14.2 Compose objects by their behaviour — 02-interfaces.html
///
/// Original assignment (this version is complete):
///
/// Layer two short rhythms that run at slightly different intervals. Their changing alignment creates new
/// accents even though neither individual pattern changes.
///
/// **Your mission:** Create makeVoice(beat, gain): its returned Voice must produce gain × tap(time,
/// beat). Install two voices using (0.12, 1) and (0.13, 0.7). Keep the supplied mix combining both
/// roles; change no playback code.

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
