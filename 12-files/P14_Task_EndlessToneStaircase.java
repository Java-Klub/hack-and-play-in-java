import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;

/// # Úkol · EndlessToneStaircase
/// Lekce: 12.4 Přečti data ze skutečného souboru — 04-files.html
///
/// Vytvoř konečné tónové schodiště z překrytých octave layers (oktávových vrstev). Poslouchej, zda
/// opakované stoupání maskuje svůj návrat; dojem závisí na posluchači a přehrávání.
///
/// **Tvoje mise:** Sestav v write zápis WAV. Připrav parent directory (nadřazenou složku), pomocí
/// try-with-resources otevři cílový output stream (výstupní proud) i připravený audio(signal) a zapiš
/// WAVE do cíle. Nezkracuj Path ani nezapisuj do náhradního buffer (zásobníku).
///
/// Program zapíše exports/shepard.wav přes write a načte právě tento WAV pro Java Play/Stop. Chybný
/// cíl vyvolá jasnou zprávu v terminálu; jiný soubor se nepřehraje.

void write(Path path, double[] signal) throws Exception {
    // Zde připrav složku, otevři oba prostředky a zapiš WAV.
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Vyrob dvě krátké zvukové pohlednice s různým počtem octave layers (oktávových vrstev). Dej jim
/// různá jména a při dalším spuštění otevři uložené WAV. Který počet nejlépe maskuje zdánlivý návrat?
/// Jde o poslechový soud, nikoli slib, že každý uslyší nekonečné stoupání.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
