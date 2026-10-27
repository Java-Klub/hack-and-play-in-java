// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
// </editor-fold>

/// # L12.07 · RetroSoundForge
///
/// Vyrob krátký retro zvuk: klesající laser, stoupající skok či kolísavý tón. Drobné vzorce promění
/// typované karty hodnot v úplně jiné WAV efekty.
///
/// **Tvoje mise:** Wobble se změnil v rovný tón, protože jeho depth component (složka hloubky) se
/// nepoužije. Obnov modulaci pro požadovaný čas a zachovej ostatní vzorce i časové meze.
///
/// **Dnes trénuješ:** Switch s record pattern (vzory record) může společně vybrat vzorec a rozbalit jeho
/// parameter (parametry). Různé Wobble musí používat vlastní střed i odchylku a čas musí dál ovlivňovat
/// výsledek.
///
/// Po Run File porovnej Java tlačítka Play wobble, Play laser a Play jump; Stop zvuk přeruší a
/// opakované přehrání umožní slyšet opravu wobble.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F35 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
double frequency(Sound sound, double t) {
    if (!Double.isFinite(t) || t < 0 || t > 1) throw new IllegalArgumentException("Time: 0..1.");
    return switch (sound) {
        case Laser(var start, var ratio) -> start * Math.pow(ratio, t);
        case Jump(var base, var rise) -> base + rise * t;
        case Wobble(var base, var depth) -> base + depth * Math.sin(t * Math.PI * 16);
    };
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F35">
/// ### Record patterns (vzory záznamů): vyber typ a rozbal data
///
/// ```java
/// sealed interface Badge permits Name, Stars {}
/// record Name(String text) implements Badge {}
/// record Stars(int count) implements Badge {}
/// String label(Badge badge) {
///     return switch (badge) {
///         case Name(var text) -> text;
///         case Stars(var n) when n > 0 -> n + " stars";
///         case Stars(var n) -> "none";
///     };
/// }
/// ```
///
/// `label(new Stars(2))` vrátí `"2 stars"`. Sealed uzavírá alternativy; permits je vyjmenovává.
/// Record pattern rozpozná typ záznamu a dá hodnotám složek místní jména v pořadí deklarace. `var`
/// odvodí jejich typy. Musí platit také when guard (dodatečná podmínka); jinak hledání pokračuje.
/// Poslední Stars větev zpracuje nekladné počty. Každá povolená alternativa zde potřebuje zpracování.
///
/// Předpověz: která větev zpracuje `new Stars(0)` a jaký text vznikne pro `new Name("Nova")`?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `case Wobble(var base, var depth)` — Pojmenuje dvě hodnoty přenášené touto zvukovou alternativou.
/// - `-> base` — Nyní vrací pouze střední frekvenci, nezávisle na depth i čase.
/// - `t < 0 || t > 1` — Odmítne časy mimo normalised interval (normalizované rozmezí) před výběrem
///   vzorce.
/// - `Math.pow(ratio, t)` — Připravený vzorec Laser používá exponenciální poměr místo přímého růstu.
///
/// ### Co už obstarává připravená část
///
/// chooseSound vytvoří tři typované zvukové alternativy; signal opakovaně žádá frequency o jejich
/// výklad. Java Play/Stop umožní porovnat wobble, laser a jump přímo zvukem.
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

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// První component (složka) Wobble je střední frekvence, druhá je hloubka odchylky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Při t = 1/32 má předepsaná modulace +1; při t = 3/32 má -1. Použij pravidlo v Pro tuto hru.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Spoj střed a hloubku s Math.sin a úhlem popsaným v Pro tuto hru. Zachovej ostatní case (větve) i
/// kontrolu času.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int RATE = 22050;


/// Připravená syntéza: omezené sample loop (cykly vzorků) tvoří zvuk; automatické přehrávání zde není.
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

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Připravená část zajišťuje pouze uvedenou hlavní hru nebo pokus.
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
    showAudio("Retro Sound Forge", "Wobble should bend up and down.", new String[]{"wobble", "laser", "jump"},
            new double[][]{signal("wobble", 0.7), signal("laser", 0.7), signal("jump", 0.7)});
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
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
///   se ale dál měnit může.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
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
/// - `switch` — Vybere case (větev) z několika možností. Může také vrátit výsledek.
/// - `case` — Jedna větev switch; šipka ukazuje, co větev provede nebo vrátí.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `implements` — Říká, že record nebo class (třída) plní roli uvedeného interface (rozhraní).
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `interface` — Popisuje role (roli), kterou mohou plnit různé implementation (implementace). Volající
///   používá roli bez znalosti jejích vnitřností.
/// - `sealed` — Omezuje data type (datový typ) na výslovně uvedené alternativy.
/// - `null` — Žádný object (objekt) tu není.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
// </editor-fold>

