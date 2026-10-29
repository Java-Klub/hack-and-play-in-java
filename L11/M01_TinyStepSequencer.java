// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
// </editor-fold>

/// # L11.01 · TinyStepSequencer
///
/// Sestav malý rytmus zapínáním a vypínáním políček. Čtyři řádky a osm dob stačí na smyčku, kterou můžeš
/// přehrát a porovnat s kamarádovou.
///
/// **Tvoje mise:** Napiš toggle jako celou obousměrnou operaci za připravenou kontrolou mezí. Vytvoř
/// Note pro buňku; je-li v Set (množině), odeber ji, jinak ji přidej. Pro platnou buňku vrať true a
/// všechny ostatní nech beze změny.
///
/// **Dnes trénuješ:** Set (množinu), equality (rovnost) a insertion (vložení) použiješ při tvorbě
/// celé operace, ne jen poslední podmínky.
///
/// Po spuštění programu klikáním přepínej tóny, zvol BPM a tlačítkem Play si čtyři opakování
/// přehraj přímo v Javě; Stop přeruší zvuk, Restart obnoví začátek.
boolean toggle(int row, int step) {
    if (row < 0 || row >= 4 || step < 0 || step >= 8) return false;
    // Rozhodni, zda Note této buňky odebrat, nebo přidat.
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Slož s kamarádem bubenickou otázku a odpověď: tvoje jsou první čtyři sloupce, jeho poslední čtyři.
/// V reset vytvoř druhý výchozí pattern (vzor) a vysvětli, které hodnoty Note se v Set (množině)
/// opakují a které jsou různé.
// </editor-fold>

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

// <editor-fold defaultstate="expanded" desc="Jak číst tento kód">
/// ### Přítomnost je otázka; vložení je akce
///
/// Pro Set<String> guests se guests.contains("Mira") ptá ano/ne a nic nemění. guests.add("Mira") Miru
/// vloží, zatímco guests.remove("Mira") ji odebere, pokud tam je. Dvě vložení Mira do prázdného Set
/// (množiny) nechají jedinou hodnotu. Předpověz obsah po jednom odebrání Mira a následném přidání
/// Nox.
///
/// Zde je Note record (balíček hodnot) s row a step. Dvě nezávisle vytvořené hodnoty Note(1, 3) jsou
/// stejné, protože mají stejné components (složky). Čtyři bubenické řádky mají indexy 0..3 a osm
/// kroků 0..7. Okno při kliknutí volá toggle, překreslí notes a signal čte stejný Set pro přehrávání.
/// Vyzkoušej dvě kliknutí do jedné buňky a potom jedno do jiné.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Jako membership key (klíč členství) použij Note s row i step.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Před remove nebo add se zeptej contains. Platné přepnutí vrací true v obou větvích.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Zachovej guard (ochrannou podmínku), vytvoř Note, rozhodni podle členství, odeber/přidej a vrať
/// true.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int RATE = 22050;


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
    var restore = new JButton("Restart");
    restore.addActionListener(event -> {
/// Připravený restart: vytvoří počáteční model (model pravidel); neotevírá okno.
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


void main() {
    reset();
    SwingUtilities.invokeLater(this::showWindow);
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
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
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
/// - `Set` — Uchovává různé hodnoty; stejná hodnota je přítomná nejvýše jednou.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
