// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
// </editor-fold>

/// # L11.01 · TinyStepSequencer
///
/// Sestav malý rytmus zapínáním a vypínáním políček. Čtyři řádky a osm dob stačí na smyčku, kterou můžeš
/// uložit a porovnat s kamarádovou.
///
/// **Tvoje mise:** Notu lze zapnout, ale ne vypnout; druhé kliknutí ji nechá vybranou. Oprav přepínání a
/// zachovej zpracování chybných souřadnic.
///
/// **Dnes trénuješ:** Set (množina) říká, zda obsahuje danou odlišnou hodnotu. Toggle (přepnutí) má dvě
/// cesty: dosud nepřítomný Note přibude, přítomný Note má naopak přestat patřit do Set.
///
/// Po Run File klikáním zapínej a vypínej noty, vyber BPM a pomocí Export WAV ulož rytmus, který můžeš
/// otevřít v přehrávači.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F23 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
boolean toggle(int row, int step) {
    if (row < 0 || row >= 4 || step < 0 || step >= 8) return false;
    Note note = new Note(row, step);
    notes.add(note);
    return true;
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F23">
/// ### Set (množina): členství bez duplikátů
///
/// `Set` odpovídá, zda hodnota patří do kolekce. Každému vložení nepřiděluje indexovanou pozici.
///
/// ```java
/// Set<String> stickers = new HashSet<>();
/// IO.println(stickers.add("moon"));
/// IO.println(stickers.add("moon"));
/// IO.println(stickers.size());
/// ```
///
/// Výstup je `true`, `false`, potom `1`. První add změní membership (členství); druhý najde stejnou
/// hodnotu již uloženou. `contains(value)` se ptá bez změny množiny. `remove(value)` hodnotu odebere
/// a oznámí, zda byla přítomna. Record (datový záznam) má automatické porovnání podle hodnot svých
/// složek, takže dva samostatně vytvořené stejné záznamy představují stejného člena množiny. Na
/// pořadí průchodu přes HashSet nespoléhej.
///
/// Předpověz: po odebrání moon a přidání sun jaká bude velikost a výsledek `contains("moon")`?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `new Note(row, step)` — Record value (hodnota record) určuje jedno políčko řádkem i dobou.
/// - `notes.add(note)` — Přidá odlišnou notu, ale druhé přidání stejné hodnoty ji neodebere.
/// - `row < 0 || row >= 4` — Odmítne řádky mimo čtyřřádkovou mřížku před změnou Set (množiny).
///
/// ### Co už obstarává připravená část
///
/// Každé tlačítko volá toggle a obnoví výběr podle notes. Připravená syntéza čte stejnou Set (množinu),
/// spojuje konečné zvuky nástrojů a zapíše WAV ve zvoleném tempu; sama jej nepřehrává.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Rovné Note record pojmenovávají stejnou pozici mřížky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Set.remove vrací, zda nota existovala.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Přidávej jen tehdy, když remove nic nenašlo; dvě přepnutí musí obnovit starý Set (množinu).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int RATE = 22050;

/// Připravený WAV encoding (kódování): omezené PCM sample (vzorky) a try-with-resources spravují stream (proud).
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
record Note(int row, int step) {}
Set<Note> notes = new HashSet<>();
JToggleButton[][] buttons = new JToggleButton[4][8];
int bpm = 120;

/// Připravený restart: vytvoří počáteční model (model pravidel); neotevírá okno.
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

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
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
    var clear = new JButton("Clear");
    clear.addActionListener(event -> {
        notes.clear();
        refresh();
    });
    var restore = new JButton("Restore");
    restore.addActionListener(event -> {
/// Připravený restart: vytvoří počáteční model (model pravidel); neotevírá okno.
        reset();
        refresh();
    });
    var status = new JLabel("Export four loops; open the WAV to play or stop.");
    var export = new JButton("Export WAV");
    export.addActionListener(event -> {
        try {
/// Připravený WAV encoding (kódování): omezené PCM sample (vzorky) a try-with-resources spravují stream (proud).
            write(Path.of("tiny-sequencer.wav"), sequence(4));
            status.setText("Saved tiny-sequencer.wav");
        } catch (Exception failure) {
            status.setText(failure.getMessage());
        }
    });
    controls.add(new JLabel("BPM:"));
    controls.add(tempo);
    controls.add(clear);
    controls.add(restore);
    controls.add(export);
    frame.add(status, BorderLayout.NORTH);
    frame.add(grid, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    refresh();
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Toggle je skutečná akce tlačítka; notes obsahuje immutable Note (neměnné hodnoty Note).
/// Sequence tvoří čtyři hlasy ve zvoleném tempu; write je uloží bez automatického přehrání; zavření uvolní okno.
void main(String[] args) throws Exception {
    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
/// Připravený restart: vytvoří počáteční model (model pravidel); neotevírá okno.
    reset();
    if (args.length > 0 && args[0].equals("export")) {
        if (args.length > 2) {
            bpm = Integer.parseInt(args[2]);
        }
        Path path = Path.of(args.length > 1 ? args[1] : "tiny-sequencer.wav");
/// Připravený WAV encoding (kódování): omezené PCM sample (vzorky) a try-with-resources spravují stream (proud).
        write(path, sequence(4));
        IO.println("Saved: " + path.toAbsolutePath());
    } else {
        SwingUtilities.invokeLater(this::showWindow);
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
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
/// - `%` — Remainder (zbytek po dělení); hodí se pro opakující se pozice nebo tahy.
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
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `Set` — Uchovává různé hodnoty; stejná hodnota je přítomná nejvýše jednou.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
/// - `Path` — Umístění v souborech, nikoli obsah souboru.
/// - `Files` — JDK operace pro čtení a zápis místních souborů.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

