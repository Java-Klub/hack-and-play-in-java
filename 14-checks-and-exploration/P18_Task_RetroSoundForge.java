/// # Úkol · RetroSoundForge
/// Lekce: 14.3 Zjisti, která datová varianta přišla — 03-patterns.html
///
/// **Rozkolísej retro tón.** Play wobble ve výchozí verzi drží jedinou výšku. Má plynule kolísat
/// kolem zadané střední frekvence.
///
/// **Úkol:** Uprav větev Wobble v `frequency` níže. Během celého zvuku má proběhnout osm úplných
/// kolísání nad a pod středem. `depth` určuje největší odchylku oběma směry; čas `t` postupuje od
/// 0 do 1. Přesné sinusové pravidlo a příklady vysvětluje sekce Pro tuto hru.
///
/// **Výsledek:** Play wobble přehraje kolísavý tón. Při nulové hloubce zůstane rovný. Play laser,
/// Play jump a odmítání neplatného času zachovají původní chování.

double frequency(Sound sound, double t) {
    if (!Double.isFinite(t) || t < 0 || t > 1) throw new IllegalArgumentException("Time: 0..1.");
    return switch (sound) {
        case Laser(var start, var ratio) -> start * Math.pow(ratio, t);
        case Jump(var base, var rise) -> base + rise * t;
        case Wobble(var base, var depth) -> base;
    };
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Slož robotí pozdrav ze dvou různých Sound records (zvukových karet): vytvoř krátký jump a jemný
/// wobble a spoj pole vzorků. Dej pozdravu jméno. Zachovej tiché přehrávání; srovnávej časování místo
/// zvyšování amplitudy.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// ### Pravidlo wobble před opravou Javy
///
/// Contract (dohodnuté chování) je střed + hloubka × sin(16πt), s normalizovaným časem t od 0 do 1.
/// To znamená osm úplných modulačních cyklů během zvuku bez ohledu na jeho délku v sekundách. Sinus
/// je mezi -1 a +1, takže hloubka určuje největší odchylku pod nebo nad středem.
///
/// `Math.sin(angle)` očekává radiány; `Math.PI` dodává π. Celá otáčka je 2π radiánů. Při t = 0 je
/// sinus nula; při t = 1/32 má úhel π/2 a sinus +1; při t = 3/32 je -1. Pro jiný Wobble se středem
/// 250 a hloubkou 40 jsou první dvě frekvence 250 a 290. Předpověz třetí a potom změň jen hloubku.
/// Matematické pravidlo zapiš pomocí obou matched components (rozebraných složek) a t. Zachovej time
/// guard (kontrolu času) i odlišné chování Laser a Jump.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
