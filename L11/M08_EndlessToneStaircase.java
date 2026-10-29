// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import module java.base;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
// </editor-fold>

class M08_EndlessToneStaircase {
    /// # L11.08 · EndlessToneStaircase
    ///
    /// Vytvoř zvukové schodiště, které díky vrstvám oktáv zdánlivě stále stoupá. Porovnej konečný zvuk s
    /// obyčejně stoupajícím tónem a poslouchej, kde začne působit iluze.
    ///
    /// **Tvoje mise:** WAV se zapíše do pracovního adresáře, i když požádáš o vnořený cíl. Zachovej úplný
    /// zvolený Path (cestu) až do konečného zápisu.
    ///
    /// **Dnes trénuješ:** Path (cesta) nese adresáře i název souboru. Ponechání jen posledního názvu změní
    /// cíl; příprava nadřazených adresářů neopraví pozdější zápis jinam.
    ///
    /// command pro hraní zapíše exports/shepard.wav přes write a načte právě tento WAV pro Java
    /// Play/Stop. Chybný cíl vyvolá jasnou zprávu v výstup v terminálu; jiný soubor se nepřehraje. Po
    /// opravě: ulož soubor, spusť uvedenou kontrolu a zahraj si znovu.
    ///
    /// Z hlavní složky obsahující README.md:
    /// - Hraní: `java L11/M08_EndlessToneStaircase.java`
    /// - Kontrola této mise: `java L11/Check.java M08_EndlessToneStaircase.java`
    ///
    /// Potřebuješ výklad? Nová dovednost · F32 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
    /// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
    void write(Path path, double[] signal) throws Exception {
        byte[] pcm = new byte[signal.length * 2];
        for (int i = 0; i < signal.length; i++) {
            int value = (int)(Math.max(-0.8, Math.min(0.8, signal[i])) * 32767);
            pcm[i * 2] = (byte)value;
            pcm[i * 2 + 1] = (byte)(value >> 8);
        }
        Files.createDirectories(path.toAbsolutePath().getParent());
        var format = new AudioFormat(RATE, 16, 1, true, false);
        try (var stream = new AudioInputStream(new ByteArrayInputStream(pcm), format, signal.length)) {
            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, path.toFile());
        }
    }

    // <editor-fold defaultstate="expanded" desc="Nová dovednost · F32">
    /// ### Files (soubory): cíl a životnost prostředku
    ///
    /// `Path` popisuje umístění; jeho vytvoření nevytvoří soubor. Uvnitř metody s deklarací `throws
    /// IOException` tento kód zapíše poznámku:
    ///
    /// ```java
    /// Path file = Path.of("exports", "note.txt");
    /// Files.createDirectories(file.getParent());
    /// try (var writer = Files.newBufferedWriter(file)) {
    ///     writer.write("Hi");
    /// }
    /// ```
    ///
    /// Výsledek je exports/note.txt obsahující `Hi`. `getParent()` vybere exports; `getFileName()` vybere
    /// jen note.txt a zahodí složku. Try-with-resources (automatické uzavření prostředku) uzavře writer
    /// na konci bloku i při selhání zápisu. `throws` nechá I/O failure (chybu vstupu/výstupu) řešit
    /// volajícího; tady ji neřeší. Také obrazové a zvukové encodery (kodéry) potřebují skutečný cíl,
    /// nikoli nesouvisející paměťový buffer (vyrovnávací paměť).
    ///
    /// Předpověz: pokud file změníš na `Path.of("cards", "one.txt")`, kam se poznámka zapíše?
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
    /// ## Jak číst právě tyto řádky
    ///
    /// - `path.toAbsolutePath().getParent()` — Najde nadřazený adresář úplného cíle, aby mohl vzniknout.
    /// - `path.getFileName()` — Vybere jen poslední název a zahodí požadovaný adresář.
    /// - `try (var stream =` — Po skončení bloku zavře vlastní audio stream (zvukový proud), i když zápis
    ///   selže.
    /// - `AudioSystem.write` — Zakóduje připravené sample (vzorky) do WAV v předaném cíli.
    ///
    /// ### Co už obstarává připravená část
    ///
    /// signal vytvoří jeden konečný vrstvený tón. main jej pomocí write zapíše do exports/shepard.wav,
    /// ověří tentýž požadovaný WAV a nabídne Java Play/Stop pro skutečná data souboru.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
    /// GetFileName záměrně zahazuje nadřazenou cestu.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
    /// Původní Path už ukazuje na zvolený cíl.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
    /// Použij jej v AudioSystem.write; zachovej createDirectories i try-with-resources.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
    final int RATE = 22050;

    /// Připravená syntéza: omezené sample loop (cykly vzorků) tvoří zvuk; automatické přehrávání zde není.
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

    /// Program spusť uvedeným command (příkazem) pro hraní. Kontrola hry ověří pravidlo bez otevření
    /// okna. Připravená část zajišťuje pouze uvedenou hlavní hru nebo pokus.

    /// Připravený Java Sound: worker (pracovní vlákno) vlastní kanál; Stop a zavření jej uvolní.
    /// Play/Stop používají oddělený Java Sound worker (pracovní vlákno); zavření jej uvolní.


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

    // <editor-fold defaultstate="collapsed" desc="Malá mapa Javy · volitelné">
    /// ## Neznámý zápis?
    ///
    /// Tato malá mapa je dobrovolná. Najdi jen znak nebo slovo, kterému nerozumíš. Není třeba se ji učit
    /// dopředu.
    ///
    /// - `///` — Slova pro čtenáře. Java tyto comment (komentářové) řádky při spuštění přeskočí.
    /// - `import` — Zpřístupní v souboru jména z knihovny; knihovnu tím nespustí.
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
    /// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
    ///   textu nepatří.
    /// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
    ///   se ale dál měnit může.
    /// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
    /// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
    /// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
    /// - `++` — Zvětší číselnou hodnotu o jedna.
    /// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
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
    /// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
    /// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
    /// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
    /// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
    ///   zpracovat.
    /// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek.
    /// - `Path` — Umístění v souborech, nikoli obsah souboru.
    /// - `Files` — JDK operace pro čtení a zápis místních souborů.
    // </editor-fold>

}
