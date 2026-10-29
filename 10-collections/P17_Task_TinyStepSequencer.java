import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/// # Solved task · TinyStepSequencer
/// Lesson: 10.2 Remember a first visit — 02-sets.html
///
/// Original assignment (this version is complete):
///
/// Build a tiny drum pattern by switching cells on and off. Four rows and eight beats are enough to make
/// a loop worth replaying and comparing with a friend’s.
///
/// **Your mission:** Write toggle as a complete two-way operation after the supplied bounds check.
/// Create a Note for this cell; remove it if present, otherwise add it. Return true for a valid cell,
/// and leave every other cell unchanged.

boolean toggle(int row, int step) {
    if (row < 0 || row >= 4 || step < 0 || step >= 8) return false;
    Note note = new Note(row, step);
    if (notes.contains(note)) {
        notes.remove(note);
    } else {
        notes.add(note);
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Write a call-and-response drum loop with a friend: you own the first four columns and they own the
/// last four. Add a second starting pattern in reset, then explain which Note values repeat and which
/// remain distinct in the Set.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int RATE = 22050;

record Note(int row, int step) {}
Set<Note> notes = new HashSet<>();
JToggleButton[][] buttons = new JToggleButton[4][8];
int bpm = 120;

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
    var restore = new JButton("Restart");
    restore.addActionListener(event -> {
        reset();
        refresh();
    });
    var status = new JLabel("Click cells, then Play to hear four loops in Java.");
    var play = new JButton("Play");
    play.addActionListener(event -> playAudio(sequence(4), status));
    var stop = new JButton("Stop");
    stop.addActionListener(event -> { stopAudio(); status.setText("Stopped; Play starts the current notes again."); });
    frame.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override public void windowClosed(java.awt.event.WindowEvent event) { stopAudio(); }
    });
    controls.add(new JLabel("BPM:"));
    controls.add(tempo);
    controls.add(restore);
    controls.add(play);
    controls.add(stop);
    frame.add(status, BorderLayout.NORTH);
    frame.add(grid, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    refresh();
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
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

void main() {
    reset();
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>
