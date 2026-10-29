/// # Úkol · SecretBeeps
/// Lekce: 13.5 Nech kreslení pozorovat model — 05-model-view.html
///
/// Proměň krátkou zprávu v tečky, čárky a pípání. Zkus delší mezeru mezi slovy a poslechni si, jak
/// samotné časování usnadní či ztíží rozlišení stejných značek.
///
/// **Tvoje mise:** Požadovaná mezera slov se ověří, ale vždy ji nahradí sedm jednotek. Rozšiř převod
/// časování a zachovej původní sedmijednotkový režim i ostatní mezery.
///
/// Po spuštění programu napiš zprávu nebo stiskni Enter pro E E; v Java okně porovnej Play gap
/// 10 s Play gap 7 a pomocí Stop či dalšího přehrání prozkoumej mezery mezi slovy.

List<Integer> units(String text, int wordGap) {
    if (wordGap < 3 || wordGap > 12) throw new IllegalArgumentException("Word gap: 3..12 units.");
    var result = new ArrayList<Integer>();
    String[] words = code(text).split(" / ");
    for (int w = 0; w < words.length; w++) {
        if (w > 0) {
            result.add(-7);
        }
        String[] letters = words[w].split(" ");
        for (int l = 0; l < letters.length; l++) {
            if (l > 0) {
                result.add(-3);
            }
            for (int m = 0; m < letters[l].length(); m++) {
                if (m > 0) {
                    result.add(-1);
                }
                result.add(letters[l].charAt(m) == '.' ? 1 : 3);
            }
        }
    }
    return result;
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Vytvoř slyšitelnou hádanku iniciál: zvol krátkou zprávu, kterou partner může poznat, a dolaď word
/// gap (mezeru mezi slovy) pro poslech. Potom zpomal délku tečky a zachovej všechny poměry délek.
/// Nové časování se může záměrně lišit od původních kontrol.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int RATE = 22050;

String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
String[] morse = {
    ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..",
    ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.",
    "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", "-----",
    ".----", "..---", "...--", "....-", ".....", "-....", "--...", "---..", "----."
};

String code(String text) {
    if (text.isBlank() || text.length() > 40 || !text.matches("[A-Za-z0-9 ]+")) {
        throw new IllegalArgumentException("Use 1..40 ASCII letters, digits and spaces.");
    }
    var words = new StringJoiner(" / ");
    for (String word : text.strip().toUpperCase(Locale.ROOT).split(" +")) {
        var letters = new StringJoiner(" ");
        for (char c : word.toCharArray()) {
            letters.add(morse[alphabet.indexOf(c)]);
        }
        words.add(letters.toString());
    }
    return words.toString();
}

double[] signal(String text, int milliseconds) {
    if (milliseconds < 20 || milliseconds > 100) {
        throw new IllegalArgumentException("Unit duration: 20..100 ms.");
    }
    var units = units(text, wordGap);
    int frames = RATE * milliseconds / 1000;
    double[] signal = new double[units.stream().mapToInt(Math::abs).sum() * frames];
    int offset = 0;
    for (int unit : units) {
        int length = Math.abs(unit) * frames;
        if (unit > 0) {
            for (int i = 0; i < length; i++) {
                double fade = Math.min(1, Math.min(i, length - 1 - i) / 40.0);
                signal[offset + i] = 0.2 * fade * Math.sin(2 * Math.PI * 650 * i / RATE);
            }
        }
        offset += length;
    }
    return signal;
}

int wordGap = 7;
List<Integer> units(String text) {
    return units(text, 7);
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
    String input = IO.readln("Message (blank E E, q quits): ");
    if (input == null || input.strip().equalsIgnoreCase("q")) return;
    String text = input.isBlank() ? "E E" : input;
    try {
        wordGap = 10;
        var changed = signal(text, 60);
        wordGap = 7;
        var original = signal(text, 60);
        showAudio("Secret Beeps", "Message: " + text + " | " + code(text), new String[]{"gap 10", "gap 7"}, new double[][]{changed, original});
    } catch (IllegalArgumentException failure) {
        IO.println(failure.getMessage());
    }
}
// </editor-fold>
