// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
// </editor-fold>

/// # L01.03 · TypeMelody
///
/// Proměň krátkou napsanou zprávu v malou melodii. Každé písmeno dostane tón, zatímco jiné znaky nechají
/// tichý úsek, takže různé zprávy vytvářejí různé rytmy.
///
/// **Tvoje mise:** První zpráva obsahuje čtyři tiché úseky. Změň zprávu poskytovanou defaultMessage
/// tak, aby úvodní melodie vyjadřovala JAVA.
///
/// **Dnes trénuješ:** Text literal (doslovný text): dvojité uvozovky označí svůj obsah jako textová data.
/// Číslice v uvozovkách například zůstane znakem zprávy; nestane se výpočtem.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L01/M03_TypeMelody.java`
/// - Kontrola této mise: `java L01/Check.java M03_TypeMelody.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše: otevře se Java přehrávač se zprávou. Stiskni Play
/// / Replay; číslice tvoří tiché pauzy, takže výchozí 1234 má být tiché. Stop přehrávání ukončí.
///
/// Potřebuješ výklad? Nová dovednost · F02 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
String defaultMessage() {
    return "1234";
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F02">
/// String literal (doslovně zapsaný řetězec) je text mezi dvojitými uvozovkami. Vnější uvozovky
/// říkají Javě, kde text začíná a končí; nevypisují se. I číslice v uvozovkách jsou text. Expression
/// (výraz) naopak může před vypsáním vypočítat hodnotu.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// IO.println("Shelf 4");
/// IO.println(4 + 2);
/// IO.println("Shelf \"B\"");
/// ```
///
/// Předpověz tři řádky. Jsou to `Shelf 4`, `6` a `Shelf "B"`. V posledním literal představuje každé
/// `\"` jednu uvozovku patřící do textu. Zpětné lomítko je zápis, nikoli další vypsaný znak.
/// `IO.println` vypíše svůj argument (předanou hodnotu) a ukončí řádek. Mezery uvnitř literal
/// zůstávají skutečnými mezerami; změna comment (komentářů) vypsanou zprávu nezmění. Rozlišuj tedy
/// znaky zapsané ve source (zdrojovém textu) a znaky, které se opravdu objeví v output (výstupu).
///
/// Nyní předpověz `IO.println(4 + 5);` a `IO.println("4 + 5");`.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `String` — Výsledkem je String (řetězec): posloupnost textových znaků.
/// - `defaultMessage()` — Jméno s prázdnými závorkami je připravená otázka bez input (vstupu): jaká
///   zpráva se má použít?
/// - `{` — Složené závorky uzavírají statement (příkazy), které na otázku odpovídají.
/// - `return` — Předej následující hodnotu zpět části, která požádala o zprávu.
/// - `"1234"` — Uvozovky patří k zápisu Javy; čtyři znaky uvnitř se stanou skutečnou zprávou.
/// - `;` — Zde ukonči tento return statement (příkaz vrácení hodnoty).
///
/// ### Co už obstarává připravená část
///
/// main zavolá defaultMessage, aby získala text, a otevře připravený Java přehrávač. synthesize
/// převede písmena na zvukové vzorky a připravený přehrávač je přehraje. Tato zvuková část je
/// připravená; připravený Java přehrávač vlastní a uzavírá zvukový prostředek.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// Zvuk přehrává přímo Java. Není třeba hledat soubor ani jiný přehrávač. Panel ukáže skutečnou
/// zprávu; výchozí číslice jsou pauzy, nikoli porucha zvuku. Po úpravě defaultMessage ulož source
/// (zdrojový text), zavři okno a znovu spusť command pro hraní. Nové okno načte upravenou zprávu;
/// Play / Replay opakuje zprávu současného okna.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Zprávou je pouze text mezi uvozovkami.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Připravený synthesizer (syntetizér) přiřazuje číslicím tiché úseky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Použij return "JAVA"; potom zavři přehrávač a znovu spusť command pro hraní.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int SAMPLE_RATE = 22050;
final int NOTE_SAMPLES = 2205;

byte[] synthesize(String text, long seed) {
    if (text.isEmpty() || text.length() > 120) {
        throw new IllegalArgumentException("Use 1..120 characters.");
    }
    int[] scale = {0, 2, 4, 5, 7, 9, 11};
    int root = 60 + new Random(seed).nextInt(12);
    byte[] pcm = new byte[text.length() * NOTE_SAMPLES * 2];
    for (int letter = 0; letter < text.length(); letter++) {
        char character = text.charAt(letter);
        if (character >= 'A' && character <= 'Z') character = (char) (character + 'a' - 'A');
        if (character < 'a' || character > 'z') continue;
        int index = character - 'a';
        int note = root + scale[index % scale.length] + 12 * (index / 7 % 2);
        double frequency = 440 * Math.pow(2, (note - 69) / 12.0);
        for (int sample = 0; sample < NOTE_SAMPLES; sample++) {
            double fade = Math.min(1.0, Math.min(sample, NOTE_SAMPLES - 1 - sample) / 220.0);
            int value = (int) (6000 * fade * Math.sin(2 * Math.PI * frequency * sample / SAMPLE_RATE));
            int offset = (letter * NOTE_SAMPLES + sample) * 2;
            pcm[offset] = (byte) value;
            pcm[offset + 1] = (byte) (value >> 8);
        }
    }
    return pcm;
}

// Prepared Java audio: a bounded worker owns the line; Stop and window close cancel it.
volatile int playbackGeneration;
javax.sound.sampled.SourceDataLine soundLine;

synchronized void stopSound() {
    playbackGeneration++;
    if (soundLine != null) {
        soundLine.stop();
        soundLine.close();
        soundLine = null;
    }
}

void playSound(byte[] pcm, javax.swing.JLabel status) {
    stopSound();
    int generation = playbackGeneration;
    status.setText("Playing the current message. Digits are silent rests.");
    var worker = new Thread(() -> {
        var format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        try (var line = AudioSystem.getSourceDataLine(format)) {
            line.open(format);
            synchronized (this) {
                if (generation != playbackGeneration) return;
                soundLine = line;
            }
            line.start();
            int offset = 0;
            while (generation == playbackGeneration && offset < pcm.length) {
                int written = line.write(pcm, offset, Math.min(4096, pcm.length - offset));
                if (written <= 0) break;
                offset += written;
            }
            if (generation == playbackGeneration) {
                line.drain();
                boolean finished = offset == pcm.length;
                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (generation == playbackGeneration) status.setText(finished
                        ? "Finished. Play / Replay starts the message again."
                        : "Playback ended early. Play / Replay tries again.");
                });
            }
        } catch (Exception unavailable) {
            if (generation == playbackGeneration) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (generation == playbackGeneration) status.setText("Audio unavailable: " + unavailable.getMessage());
                });
            }
        } finally {
            synchronized (this) {
                if (generation == playbackGeneration) soundLine = null;
            }
        }
    }, "melody-player");
    worker.setDaemon(true);
    worker.start();
}

void showPlayer() {
    String message = defaultMessage();
    byte[] pcm = synthesize(message, 42);
    boolean allRests = message.matches("[^A-Za-z]*");
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("Message: " + message + ". Letters make notes; digits are silent rests.");
        return;
    }
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame("Type Melody");
        var description = new javax.swing.JLabel("<html>Message: <b>" + message.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
            + "</b><br>Letters A-Z make notes. Digits and other characters are silent rests.<br>"
            + (allRests ? "This message is all rests: 1234 has four silent slots.<br>" : "This message contains notes. Press Play to listen.<br>")
            + "Edit defaultMessage, save, close this window and run the play command again.</html>");
        var status = new javax.swing.JLabel("Click Play to hear this message. Stop ends playback.");
        var buttons = new javax.swing.JPanel();
        var play = new javax.swing.JButton("Play / Replay");
        play.addActionListener(event -> playSound(pcm, status));
        var stop = new javax.swing.JButton("Stop");
        stop.addActionListener(event -> { stopSound(); status.setText("Stopped. Play replays the current message."); });
        buttons.add(play);
        buttons.add(stop);
        var content = new javax.swing.JPanel(new java.awt.BorderLayout(10, 10));
        content.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        content.add(description, java.awt.BorderLayout.NORTH);
        content.add(status);
        content.add(buttons, java.awt.BorderLayout.SOUTH);
        frame.add(content);
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent event) { stopSound(); }
        });
        frame.pack(); frame.setLocationByPlatform(true); frame.setVisible(true);
    });
}

void main() {
    showPlayer();
}

/// Připravené vstupní body: main.
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
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `'` — Jednoduché uvozovky ohraničují jeden character (znak), například značku na desce.
/// - `char` — Type (typ) pro jednu UTF-16 jednotku, často jednoduchý znak desky jako X. Pro text slouží
///   String.
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
///   se ale dál měnit může.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `%` — Remainder (zbytek po dělení); hodí se pro opakující se pozice nebo tahy.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
/// - `/` — Dělí čísla. Dělení dvou int hodnot zahodí desetinnou část.
/// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
///   List<String> lomené závorky popisují element type (typ prvku).
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek.
// </editor-fold>

