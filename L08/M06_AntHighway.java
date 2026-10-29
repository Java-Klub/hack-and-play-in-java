// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L08.06 · AntHighway
///
/// Nech malého mravence zatáčet, přebarvovat políčka a postupně tvořit překvapivou stopu. Sleduj, zda z
/// jednoduchého opakovaného pravidla vznikne dálnice.
///
/// **Tvoje mise:** Západní směr posílá mravence na východ a křiví jeho stopu. Oprav směrové posuny v
/// step() a zachovej zatáčení, přebarvování i přechod přes okraje.
///
/// **Dnes trénuješ:** Grid neighbours (sousedé v mřížce): souběžná pole dx a dy popisují jeden krok pro
/// každý hlavní směr. Stejný index směru musí vybírat odpovídající vodorovný i svislý posun.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L08/M06_AntHighway.java`
/// - Kontrola této mise: `java L08/Check.java M06_AntHighway.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše a kliknutím na One step si prohlédni jeden krok
/// mravence, potom pomocí Run/Pause nech vyrůst delší stopu. Po opravě: ulož soubor, spusť uvedenou
/// kontrolu a zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F18 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
void step() {
    direction = Math.floorMod(direction + (black[y][x] ? -1 : 1), 4);
    black[y][x] = !black[y][x];
    int[] dx = {0, 1, 0, -1};
    int[] dy = {-1, 0, 1, 0};
    x = Math.floorMod(x + dx[direction], 81);
    y = Math.floorMod(y + dy[direction], 61);
    steps++;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F18">
/// ### Grids: nejdřív řádek, potom sloupec
///
/// V array of rows (poli řádků) první index (pozice) vybírá řádek a druhý pozici v něm. Řádky mohou
/// mít různé délky. Neighbour (soused) je posun od buňky, nikoli další absolutní pozice. Před
/// kontrolou existence kandidáta urči povolené směry.
///
/// ```java
/// int[][] mosaic = {{3, 6, 9}, {2, 4, 8}};
/// int row = 0;
/// int column = 1;
/// IO.println(mosaic[row][column + 1]);
/// ```
///
/// Výstup je devět: řádek nula a sloupec dva vyberou tento element (prvek). Pohyb dolů mění row;
/// doprava mění column. Který z těchto pohybů z pravé horní cell (buňky) zůstane uvnitř? U úhlopříčky
/// se mění obě souřadnice; bounds (meze) musíš ověřit před přístupem ke kandidátovi.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `black[y][x] = !black[y][x]` — Negation (logická negace) přepne barvu právě navštíveného políčka.
/// - `int[] dx` — Vodorovné posuny vybírané podle indexu směru mravence.
/// - `dx[direction]` — Čte vodorovnou část kroku pro právě tento směr.
/// - `Math.floorMod` — Převádí souřadnice a indexy směru do platného nezáporného rozsahu i při přechodu
///   přes okraj.
///
/// ### Co už obstarává připravená část
///
/// Tlačítka spustí jeden krok nebo omezené dávky pomocí Swing timer (časovače). reset() vymaže desku a
/// kreslení ukazuje uložené barvy i mravence; step() tvoří celé pravidlo pohybu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Směry 0,1,2,3 znamenají sever,východ,jih,západ.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Západ mění x o -1 a ponechává y.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Použij dx={0,1,0,-1}; dy už dodává {-1,0,1,0}.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
boolean[][] black = new boolean[61][81];
int x = 40;
int y = 30;
int direction;
long steps;
boolean running;

/// reset(): Obnovuje počáteční kolo Ant Highway a maže stav patřící starému kolu.
void reset() {
    black = new boolean[61][81];
    x = 40;
    y = 30;
    direction = 0;
    steps = 0;
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; časovače končí při
/// zavření okna.
void showWindow() {
    var frame = new JFrame("Ant Highway");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var canvas = new JPanel() {

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics;
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 648, 488);
            g.setColor(Color.BLACK);
            for (int row = 0; row < 61; row++) {
                for (int col = 0; col < 81; col++) {
                    if (black[row][col]) {
                        g.fillRect(col * 8, row * 8, 8, 8);
                    }
                }
            }
            g.setColor(Color.RED);
            g.fillRect(x * 8, y * 8, 8, 8);
        }
    };
    canvas.setPreferredSize(new Dimension(648, 488));
    var controls = new JPanel();
    var label = new JLabel("Steps: 0");
    var run = new JButton("Run");
    run.addActionListener(event -> {
        running = !running;
        run.setText(running ? "Pause" : "Run");
    });
    var single = new JButton("One step");
    single.addActionListener(event -> {
        running = false;
        run.setText("Run");
        step();
        label.setText("Steps: " + steps);
        canvas.repaint();
    });
    var restart = new JButton("Reset");
    restart.addActionListener(event -> {
        running = false;
        run.setText("Run");
        reset();
        label.setText("Steps: 0");
        canvas.repaint();
    });
    controls.add(run);
    controls.add(single);
    controls.add(restart);
    controls.add(label);
    var timer = new Timer(25, event -> {
        if (running) {
            for (int i = 0; i < 100; i++) {
                step();
            }
        }
        label.setText("Steps: " + steps);
        canvas.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {

        public void windowClosed(WindowEvent event) {
            timer.stop();
        }
    });
    timer.start();
    frame.add(canvas, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

/// main(): Spouští model a potom plánuje dodané okno na Swing event thread.
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
/// - `boolean` — Hodnota true nebo false: ano nebo ne.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
/// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
///   List<String> lomené závorky popisují element type (typ prvku).
/// - `?` — V condition ? first : second vybere první hodnotu, když condition (podmínka) platí, jinak
///   druhou.
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
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
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
