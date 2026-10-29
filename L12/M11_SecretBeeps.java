// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
// </editor-fold>

/// # L12.11 · SecretBeeps
///
/// Proměň krátkou zprávu v tečky, čárky a pípání. Zkus delší mezeru mezi slovy a poslechni si, jak
/// samotné časování usnadní či ztíží rozlišení stejných značek.
///
/// **Tvoje mise:** Požadovaná mezera slov se ověří, ale vždy ji nahradí sedm jednotek. Rozšiř převod
/// časování a zachovej původní sedmijednotkový režim i ostatní mezery.
///
/// **Dnes trénuješ:** Přenes existující encoding rule (pravidlo kódování) do parameterised operation
/// (operace s parametrem). Zachovej časování písmen a značek, zatímco nový parameter (parametr) řídí
/// oddělení slov; původní chování ověř, ale nekopíruj kvůli němu celý postup.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L12/M11_SecretBeeps.java`
/// - Kontrola této mise: `java L12/Check.java M11_SecretBeeps.java`
///
/// Po spuštění programu napiš zprávu nebo stiskni Enter pro E E; v Java okně porovnej Play gap 10 s
/// Play gap 7 a pomocí Stop či dalšího přehrání prozkoumej mezery mezi slovy. Po opravě: ulož
/// soubor, spusť uvedenou kontrolu a zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F36 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
List<Integer> units(String text, int wordGap) {
    if (wordGap < 3 || wordGap > 12) throw new IllegalArgumentException("Word gap: 3..12 units.");
    var result = new ArrayList<Integer>();
    String[] words = code(text).split(" / ");
    for (int w = 0; w < words.length; w++) {
        if (w > 0) {
            result.add(-wordGap);
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

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F36">
/// ### Transfer (přenos): zachovej slib a přidej volbu
///
/// Extract helper (vyčlenění pomocné metody) pojmenuje jednu odpovědnost. Vstupy a výsledek tvoří
/// contract (smlouvu), kterou můžeš využít jinde.
///
/// ```java
/// String surround(String text, boolean stars) {
///     String edge = stars ? "*" : "#";
///     return edge + text + edge;
/// }
/// ```
///
/// `surround("hi", false)` vrátí `"#hi#"`; původní styl s mřížkami dál funguje. Volající může požádat
/// o nový styl bez opakování celé operace. Ověř obě volby, prázdný vstup i jiné slovo. Tests (testy)
/// ověří výsledky; vysvětlení, kdo vybírá styl a kdo sestavuje popisek, ověří rozdělení odpovědností.
/// Úspěšný nový režim nemá potichu měnit původní.
///
/// Předpověz: co vytvoří `surround("", true)` a která část by se měnila při změně obklopujícího
/// znaku?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `wordGap < 3 || wordGap > 12` — Ověří požadovanou mezeru před sestavením časových jednotek.
/// - `result.add(-7)` — Záporné jednotky znamenají ticho; tato konstanta nyní určuje každou mezeru slov.
/// - `result.add(-3)` — Zachovává samostatnou tříjednotkovou pause (pauzu) mezi písmeny.
/// - `charAt(m) == '.' ? 1 : 3` — Tečka je jedna znějící jednotka a čárka tři.
///
/// ### Co už obstarává připravená část
///
/// code vytvoří Morse značky, units určí jejich délky a signal vytvoří samples (vzorky). Dvě volby
/// Play porovnají požadovanou desetijednotkovou mezeru mezi slovy s původními sedmi jednotkami.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Záporné unit (jednotky) představují délku ticha.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Slova odděluje jen branch (větev) w>0.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Tam použij -wordGap, dvě ostatní mezery zachovej a vysvětli oddělení kódování textu od časování.
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

/// Připravená syntéza: omezené sample loop (cykly vzorků) tvoří zvuk; automatické přehrávání zde není.
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
/// - `'` — Jednoduché uvozovky ohraničují jeden character (znak), například značku na desce.
/// - `char` — Type (typ) pro jednu UTF-16 jednotku, často jednoduchý znak desky jako X. Pro text slouží
///   String.
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
/// - `?` — V condition ? first : second vybere první hodnotu, když condition (podmínka) platí, jinak
///   druhou.
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>

