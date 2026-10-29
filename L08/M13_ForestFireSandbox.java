// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L08.13 · ForestFireSandbox
///
/// Zasaď stromy, vysekej průsek a zapal jeden konec lesa. Prozkoumej, jak se modelová požární fronta šíří
/// po jednotlivých sousedstvích.
///
/// **Tvoje mise:** Oheň může během jediného kroku přeskočit přes mnoho stromů a staré plameny nezmizí.
/// Oprav nextForest(), aby všechna políčka vycházela ze stejného předchozího lesa.
///
/// **Dnes trénuješ:** Simultaneous updates (současné aktualizace): vytvoř úplnou novou mřížku z nezměněné
/// staré. Nově zapálené stromy musí s vlastním šířením počkat na další generaci.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L08/M13_ForestFireSandbox.java`
/// - Kontrola této mise: `java L08/Check.java M13_ForestFireSandbox.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše, vyber Fire a kliknutím zapal strom, potom přes
/// Step sleduj pohyb fronty, než vyzkoušíš Run/Pause. Po opravě: ulož soubor, spusť uvedenou
/// kontrolu a zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F26 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
int[][] nextForest(int[][] before) {
    int[][] next = before;
    int[][] offsets = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
    for (int y = 0; y < 40; y++) {
        for (int x = 0; x < 60; x++) {
            if (before[y][x] != 1) {
                continue;
            }
            next[y][x] = 1;
            for (var d : offsets) {
                int nx = x + d[0], ny = y + d[1];
                if (nx >= 0 && nx < 60 && ny >= 0 && ny < 40 && before[ny][nx] == 2) {
                    next[y][x] = 2;
                }
            }
        }
    }
    return next;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F26">
/// ### Simultaneous updates: čti okamžik, sestav další
///
/// Simultaneous step (současný krok) dává všem výpočtům stejná původní data. Výsledky zapisuj do
/// odděleného úložiště a zveřejni je po celém průchodu. Jinak se dřívější zápis skrytě stane
/// pozdějším input (vstupem) a pořadí průchodu změní pravidlo.
///
/// ```java
/// int[] tones = {2, 5, 9};
/// int[] shifted = new int[tones.length];
/// for (int i = 0; i < tones.length; i++) {
///     shifted[i] = tones[(i + 1) % tones.length];
/// }
/// ```
///
/// Výsledek je `[5,9,2]`; tones zůstane `[2,5,9]`, protože se čte nezměněné. Změnil by obrácený
/// průchod výsledek? Nové array začíná nulovými hodnotami, takže každý potřebný výstup musíš záměrně
/// vypočítat. Zkopírovaný snapshot (snímek) a nový output buffer (výstupní prostor) plní různé role.
///
/// Zde `% tones.length` používá remainder (zbytek po dělení), aby se index za posledním element
/// (prvkem) vrátil na nulu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int[][] next = before` — Nová a stará mřížka nyní sdílejí úložiště, což porušuje současný krok.
/// - `int[][] offsets` — Vypisuje čtyři hlavní směrové sousedy a záměrně vynechává diagonály.
/// - `before[y][x] != 1` — Do této větve šíření vstupují jen původní stromová políčka.
/// - `before[ny][nx] == 2` — Testuje oheň v předchozí generaci, nikoli oheň právě vytvořený v next.
///
/// ### Co už obstarává připravená část
///
/// edit() poskytuje nástroje Tree, Fire a Erase; step() jednou zveřejní výsledek nextForest(). Tlačítka a
/// timer (časovač), který se při zavření zastaví, řeší běh; jde o hračku šíření, ne předpověď požáru.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Rozhodni další význam každé output cell (výstupní buňky)
///
/// Hračka používá nulu pro prázdno, jedničku pro strom a dvojku pro oheň. Nové int grid (pole mřížky)
/// začíná prázdné, protože buňky mají nulu. Transition contract (smlouva přechodu): staré prázdno
/// zůstane, starý oheň zmizí a starý strom zůstane stromem, pokud nehořel některý původní soused ve
/// čtyřech směrech. Nový oheň ovlivní až další generaci. Nakresli řadu tří stromů s ohněm na konci a
/// předpověz dva kroky. Která data musí při výpočtu prvního kroku zůstat nezměněná?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Zápis do before dovolí novému ohni okamžitě zapálit další strom podle pořadí průchodu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Nová nulová matice také přirozeně nechá starý oheň zaniknout.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Vytvoř novou next matici 40 × 60; čti jen before a next zveřejni až po výpočtu všech polí.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[][] cells = new int[40][60];
boolean running;
int brush = 1;

/// reset(): Obnovuje počáteční kolo Forest Fire Sandbox a maže stav patřící starému kolu.
void reset(long seed) {
    var random = new Random(seed);
    cells = new int[40][60];
    for (int y = 0; y < 40; y++) {
        for (int x = 0; x < 60; x++) {
            cells[y][x] = random.nextDouble() < 0.64 ? 1 : 0;
        }
    }
}

/// edit(): Dodaná validace štětce sází, maže nebo zapaluje jen existující strom.
boolean edit(int x, int y, int value) {
    if (x < 0 || x >= 60 || y < 0 || y >= 40 || value < 0 || value > 2 || value == 2 && cells[y][x] != 1) {
        return false;
    }
    cells[y][x] = value;
    return true;
}

void step() {
    cells = nextForest(cells);
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; časovače končí při
/// zavření okna.
void showWindow() {
    var frame = new JFrame("Forest Fire Sandbox");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var canvas = new JPanel() {

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics;
            Color[] colours = {new Color(30, 25, 15), new Color(35, 145, 55), Color.ORANGE};
            for (int y = 0; y < 40; y++) {
                for (int x = 0; x < 60; x++) {
                    g.setColor(colours[cells[y][x]]);
                    g.fillRect(x * 10, y * 10, 10, 10);
                }
            }
        }
    };
    canvas.setPreferredSize(new Dimension(600, 400));
    var controls = new JPanel();
    var brushes = new JComboBox<>(new String[]{"Tree", "Fire", "Erase"});
    brushes.addActionListener(event -> brush = new int[]{1, 2, 0}[brushes.getSelectedIndex()]);
    brushes.setSelectedIndex(1);
    canvas.addMouseListener(new MouseAdapter() {

        public void mousePressed(MouseEvent event) {
            edit(event.getX() / 10, event.getY() / 10, brush);
            canvas.repaint();
        }
    });
    var run = new JButton("Run");
    var single = new JButton("Step");
    single.addActionListener(event -> {
        running = false;
        run.setText("Run");
        step();
        canvas.repaint();
    });
    run.addActionListener(event -> {
        running = !running;
        run.setText(running ? "Pause" : "Run");
    });
    var restart = new JButton("New forest");
    restart.addActionListener(event -> {
        running = false;
        run.setText("Run");
        reset(System.nanoTime());
        canvas.repaint();
    });
    controls.add(brushes);
    controls.add(single);
    controls.add(run);
    controls.add(restart);
    var timer = new Timer(100, event -> {
        if (running) {
            step();
            canvas.repaint();
        }
    });
    frame.addWindowListener(new WindowAdapter() {

        public void windowClosed(WindowEvent event) {
            timer.stop();
        }
    });
    timer.start();
    frame.add(new JLabel("Choose Fire and click a tree to ignite it; Step pauses and advances once."), BorderLayout.NORTH);
    frame.add(canvas, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

/// main(): Spouští model a potom plánuje dodané okno na Swing event thread.
void main() {
    reset(42);
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
/// - `boolean` — Hodnota true nebo false: ano nebo ne.
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
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
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `public` — Zpřístupňuje tuto declaration (deklaraci) dalšímu kódu.
/// - `protected` — Visibility keyword (klíčové slovo přístupnosti) používané připraveným library callback
///   (knihovním zpětným voláním), například paintComponent.
/// - `super` — V super.paintComponent(g) požádá připravenou parent implementation (nadřazenou
///   implementaci), aby nejprve vykreslila běžné pozadí.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

