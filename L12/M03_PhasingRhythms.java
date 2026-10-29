// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import module java.base;
// </editor-fold>

class M03_PhasingRhythms {
    /// # L12.03 · PhasingRhythms
    ///
    /// Polož přes sebe dva krátké rytmy s trochu odlišnými rozestupy. Jejich měnící se souběh tvoří nové
    /// důrazy, přestože se jednotlivé vzory nemění.
    ///
    /// **Tvoje mise:** Do směsi dorazí jen první hlas. Obnov výsledek obou hlasů ve stejném požadovaném čase
    /// a nepřepisuj zvuk jednotlivých hlasů.
    ///
    /// **Dnes trénuješ:** Composition (skládání) spojuje malé role přes společné interface (rozhraní). Obě
    /// Voice odpovídají na převod času na sample (vzorek); mix spojuje odpovědi a nemusí znát vznik
    /// jednotlivých rytmů.
    ///
    /// Z hlavní složky obsahující README.md:
    /// - Hraní: `java L12/M03_PhasingRhythms.java`
    /// - Kontrola této mise: `java L12/Check.java M03_PhasingRhythms.java`
    ///
    /// Po spuštění programu v Java okně porovnej Play two voices s Play one voice; poslouchej
    /// proměnlivé překrývání, použij Stop a po opravě mix si zvuk přehraj znovu. Po opravě: ulož
    /// soubor, spusť uvedenou kontrolu a zahraj si znovu.
    ///
    /// Potřebuješ výklad? Připomenutí · F34 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
    /// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
    double mix(double time) {
        return 0.17 * first.sample(time);
    }

    // <editor-fold defaultstate="collapsed" desc="Připomenutí · F34">
    /// ### Interface (rozhraní): požádej vybranou roli o práci
    ///
    /// Interface pojmenovává operaci, kterou dodají jeho implementations (implementace). Volající používá
    /// tuto roli bez výběru jejího výpočtu.
    ///
    /// ```java
    /// interface Points {
    ///     int award(int n);
    /// }
    /// record DoublePoints() implements Points {
    ///     public int award(int n) {
    ///         return n * 2;
    ///     }
    /// }
    /// ```
    ///
    /// Při `Points rule = new DoublePoints();` vrátí `rule.award(3)` hodnotu 6. `implements Points`
    /// slibuje danou operaci; implementace musí dodat její public method (veřejnou metodu). Proměnná rule
    /// označuje vybraný objekt, proto volej jeho operaci se současným vstupem místo opakování pevného
    /// výpočtu ve volajícím. Jiná implementace může dodat jiná pravidla přes stejné rozhraní.
    ///
    /// Předpověz: co tato implementace vrátí pro `rule.award(5)` a kde by se měnil kód pro trojnásobek
    /// vstupu?
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
    /// ## Jak číst právě tyto řádky
    ///
    /// - `mix(double time)` — Caller (volající část) žádá jeden společný sample (vzorek) právě v tomto
    ///   okamžiku.
    /// - `first.sample(time)` — Zavolá připravený první hlas s časem od caller (volající části).
    /// - `0.17 *` — Použije zesílení výstupu; zvaž, na které přispívající hlasy má působit.
    ///
    /// ### Co už obstarává připravená část
    ///
    /// signal žádá mix o každý sample (vzorek); first a second dodají dva rytmické intervaly. Přehrání
    /// porovná současný mix obou rolí s jediným hlasem, aby bylo možné slyšet chybějící roli.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
    /// Voice popisuje roli převodu času na sample (vzorek).
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
    /// Nevolej jen první field (položku objektu) a nepoužívej dva různé časy.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
    /// Zesílení nech vně součtu, aby se použilo stejně pro oba vyměnitelné hlasy.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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

    /// Připravená syntéza: omezené sample loop (cykly vzorků) tvoří zvuk; automatické přehrávání zde není.
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
    Voice first = time -> tap(time, 0.12);
    Voice second = time -> tap(time, 0.13);

    /// Program spusť uvedeným command (příkazem) pro hraní. Kontrola hry ověří pravidlo bez otevření
    /// okna. Připravená část zajišťuje pouze uvedenou hlavní hru nebo pokus.
    volatile javax.sound.sampled.SourceDataLine playingLine;
    volatile int playbackVersion;

    /// Připravený Java Sound: worker (pracovní vlákno) vlastní kanál; Stop a zavření jej uvolní.
    /// Play/Stop používají oddělený Java Sound worker (pracovní vlákno); zavření jej uvolní.
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

    // <editor-fold defaultstate="collapsed" desc="Malá mapa Javy · volitelné">
    /// ## Neznámý zápis?
    ///
    /// Tato malá mapa je dobrovolná. Najdi jen znak nebo slovo, kterému nerozumíš. Není třeba se ji učit
    /// dopředu.
    ///
    /// - `///` — Slova pro čtenáře. Java tyto comment (komentářové) řádky při spuštění přeskočí.
    /// - `;` — Ukončuje jeden statement (příkaz), podobně jako tečka větu. V záhlaví for odděluje jeho tři
    ///   části.
    /// - `()` — Za jménem: volání akce nebo seznam parameter (parametrů) při jejím popisu. Prázdné závorky
    ///   znamenají bez předávaných argument (argumentů).
    /// - `{}` — Složené závorky ohraničují block (blok kódu). Otevírací a uzavírací závorka patří k sobě.
    /// - `.` — Zpřístupňuje něco patřícího hodnotě nebo type (typu) vlevo, například IO.println.
    /// - `void` — Tato method (metoda) provede akci a nevrací výslednou hodnotu.
    /// - `main` — Tady Java program zahajuje. Ostatní method declaration (popisy metod) se neprovedou jen
    ///   proto, že jsou v souboru výše.
    /// - `int` — Whole-number type (typ pro celá čísla) s omezeným rozsahem.
    /// - `double` — Numeric type (číselný typ) pro přibližná desetinná čísla. Java používá desetinnou tečku.
    /// - `boolean` — Hodnota true nebo false: ano nebo ne.
    /// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
    /// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
    ///   textu nepatří.
    /// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
    ///   se ale dál měnit může.
    /// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
    /// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
    /// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
    /// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
    /// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
    /// - `++` — Zvětší číselnou hodnotu o jedna.
    /// - `%` — Remainder (zbytek po dělení); hodí se pro opakující se pozice nebo tahy.
    /// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
    /// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
    /// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
    ///   (balíčku).
    /// - `/` — Dělí čísla. Dělení dvou int hodnot zahodí desetinnou část.
    /// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
    ///   List<String> lomené závorky popisují element type (typ prvku).
    /// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
    ///   (prvek); length je počet prvků.
    /// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
    /// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
    /// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
    /// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
    /// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
    ///   až při pozdějším volání.
    /// - `interface` — Popisuje role (roli), kterou mohou plnit různé implementation (implementace). Volající
    ///   používá roli bez znalosti jejích vnitřností.
    /// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
    /// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
    /// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
    // </editor-fold>

}
