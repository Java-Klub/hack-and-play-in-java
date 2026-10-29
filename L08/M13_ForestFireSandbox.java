// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L08.13 · ForestFireSandbox
///
/// Sandbox už šíří modelový oheň ke čtyřem sousedním stromům. Změň svět na záměrně jednosměrný východní
/// vítr, navrhni protipožární pás a ověř předpověď.
///
/// **Tvoje mise:** V novém modelu strom vzplane jen tehdy, když cell (buňka) bezprostředně západně od
/// něj hořela v minulé generation (generaci). Starý oheň zhasne, prázdná cell zůstane prázdná.
/// Nepropojuj protější okraje, neměň starý les a nešiř dvakrát během tick (kroku). Šíření na sever,
/// jih, západ i diagonal (úhlopříčně) se zastaví. Zachovej kreslení a Step/Run.
///
/// **Dnes trénuješ:** Porovnej dvě hypotézy neighbourhood (okolí) při zachování stejné staré generation
/// (generace). Nové pravidlo větru mění čtená fakta, nikoli okamžik zveřejnění nových.
///
/// Pomocí Erase udělej místo, Tree nakresli krátkou vodorovnou řadu a Fire zapal její střed. Step
/// pozastaví a provede jeden krok. Výchozí svět šíří oheň oběma směry; po zásahu hoří jen
/// východní/pravá polovina po jedné cell (buňce).

// <editor-fold defaultstate="expanded" desc="Program · prozkoumej pravidla">
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

int[][] nextForest(int[][] before) {
    int[][] next = new int[40][60];
    for (int y = 0; y < 40; y++) {
        for (int x = 0; x < 60; x++) {
            if (before[y][x] == 1) {
                next[y][x] = x > 0 && before[y][x - 1] == 2 ? 2 : 1;
            }
        }
    }
    return next;
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


// <editor-fold defaultstate="collapsed" desc="Připomenutí · F26">
/// ### Generation (generace) sdílí stejnou minulost
///
/// Simultaneous update (současná aktualizace) znamená, že každá nová cell (buňka) čte stejný minulý
/// svět. Při přepisování starých cell za pochodu by další column (sloupec) viděl budoucnost, kterou
/// soused neviděl.
///
/// Zkus malé pravidlo: cell se rozsvítí, jestliže předtím svítila ona nebo její levý neighbour
/// (soused). Za levým okrajem je zhasnuto. Ze starého `[true,false,false]` musí vzniknout
/// `[true,true,false]`.
///
/// ```java
/// boolean[] old = {true, false, false};
/// boolean[] next = new boolean[old.length];
/// for (int x = 0; x < old.length; x++) {
///     next[x] = old[x] || (x > 0 && old[x - 1]);
/// }
/// ```
///
/// Proč ne tři true? Třetí cell čte old[1], tedy false, nikoli právě zapsané next[1]. Old zůstává
/// nedotčené, takže obrácené pořadí průchodu dá stejný výsledek. Hotové next zveřejni až po výpočtu
/// celé generation.
///
/// Nakresli OLD nad NEXT a šipky od čtených cell k zapisované cell. Předpověz druhou generation.
/// Two-dimensional grid (dvourozměrná mřížka) potřebuje stejný princip včetně samostatných
/// zapisovatelných row (řádků).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ### Pozoruj a sleduj tok
///
/// Nakresli OLD nad NEXT a před spuštěním předpověz malou řadu. Strom čte starého neighbour (souseda),
/// od kterého může přijít oheň: pohyb východně znamená čtení západního souseda. Nepleť směr pohybu se
/// směrem čtení. Kreslení i štětce fungují; použij je k řízenému pokusu místo opakované změny náhodného
/// lesa.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pokus a opakování">
/// Umísti oheň se stromy ze všech čtyř stran a stiskni Step. Vzplane jen východní strom a starý oheň
/// zhasne. Druhý východní strom musí počkat na další Step. Přeruš řadu prázdnou cell (buňkou) a
/// předpověz zastavení.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Pro OLD [tree, fire, tree, tree] nakresli další řadu. Strom za čerstvým ohněm musí počkat.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Omez hledání na výpočet generation (generace), který volá Step. Zachovej jeho nový grid (mřížku);
/// změň důkaz od neighbour (souseda) pro strom.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Najdi nextForest. Pro strom na (x,y) čti jen old[y][x-1], pokud x>0. Zachovej přechod starého ohně i
/// prázdna na empty a vrať samostatně vytvořený next grid (příští mřížku).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta · dobrovolně">
/// Vymysli severovýchodní vítr se dvěma výslovnými příchozími neighbour (sousedy). Stejnou nakreslenou
/// scénou jej odliš od obou dřívějších modelů.
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
