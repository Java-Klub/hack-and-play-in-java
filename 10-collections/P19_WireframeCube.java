// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # 10.19 · WireframeCube
///
/// Otáčej drátěnou krychlí a sleduj, jak plochá kresba naznačuje hloubku. Počítadlo hran má popsat tvar,
/// i když vstup uvede hranu dvakrát v opačném směru.
///
/// **Tvoje mise:** Sestav uniqueEdges od prázdného Set (množiny). Projdi dvojice, přeskoč konce mimo
/// rozsah i hranu do stejného bodu, menší konec dej první a vlož normalised Edge (sjednocenou hranu).
/// Vrať různé hrany v pořadí prvního výskytu.
///
/// **Dnes trénuješ:** Set (množinu), equality (rovnost) a insertion (vložení) použiješ při tvorbě
/// celé operace, ne jen poslední podmínky.
///
/// Po spuštění programu pomocí Pause/Run zastav otáčení krychle, přečti Distinct edges a pomocí
/// Reset angle obnov pohled.
Set<Edge> uniqueEdges(int[][] candidates) {
    Set<Edge> result = new LinkedHashSet<>();
    // Zde projdi, ověř, sjednoť a vlož kandidáty.
    return result;
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Přidej krychli tajný podpis úhlopříček pomocí nových candidate edges (kandidátních hran), včetně
/// několika obrácených duplicit. Na papíře předpověz počet různých hran a ověř jej v okně dřív, než
/// změníš otáčení.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F23">
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
/// ### Equality (rovnost) potřebuje společný zápis
///
/// Představ si neorientovanou silnici mezi městy 2 a 5. (2, 5) i (5, 2) popisují stejnou cestu, ale
/// dva record values (balíčky) s tímto pořadím složek nejsou stejné. Zvol convention (dohodu)
/// menší-první: obě budou (2, 5). Set teď rozezná duplicitu běžnou rovností record. Předpověz různé
/// silnice v [(4,1), (1,4), (4,6)].
///
/// Math.min(a,b) a Math.max(a,b) vracejí menší a větší konec. LinkedHashSet zachovává insertion order
/// (pořadí vložení), neslibuje číselné řazení. Krychle má osm vrcholů s indexy 0..7. uniqueEdges
/// hlídá platnost i normalisation (sjednocení zápisu); otočení a kreslení jen používají výsledek.
/// Neplatná dvojice nic nepřidá, ale nesmí zabránit přečtení pozdějších platných dvojic.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Nejdřív sestav průchod a validaci. Prázdný vstup má vrátit prázdný Set.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Pro přijatou dvojici vytvoř canonical Edge (jednotný zápis) s menším a větším koncem.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Vkládej sjednocené Edge do LinkedHashSet a po cyklu tento Set vrať.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
double angle;
boolean running = true;
int[][] edges = {{0, 1}, {0, 2}, {0, 4}, {1, 3}, {1, 5}, {2, 3}, {2, 6}, {3, 7}, {4, 5}, {4, 6}, {5, 7}, {6, 7}, {1, 0}};

/// Připravený restart: vytvoří počáteční model (model pravidel); neotevírá okno.
void reset(long seed) {
    angle = 0;
}

int[][] project(double angle) {
    int[][] result = new int[8][2];
    for (int i = 0; i < 8; i++) {
        double x = (i & 1) == 0 ? -1 : 1;
        double y = (i & 2) == 0 ? -1 : 1;
        double z = (i & 4) == 0 ? -1 : 1;
        double rotatedX = x * Math.cos(angle) + z * Math.sin(angle);
        double rotatedZ = -x * Math.sin(angle) + z * Math.cos(angle);
        double rotatedY = y * Math.cos(angle * 0.7) - rotatedZ * Math.sin(angle * 0.7);
        double depth = y * Math.sin(angle * 0.7) + rotatedZ * Math.cos(angle * 0.7);
        result[i][0] = 300 + (int)(rotatedX * 350 / (4 + depth));
        result[i][1] = 200 + (int)(rotatedY * 350 / (4 + depth));
    }
    return result;
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Wireframe Cube");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var canvas = new JPanel(){

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics;
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 600, 400);
            g.setColor(Color.CYAN);
            var points = project(angle);
            var distinct = uniqueEdges(edges);
            for (var edge : distinct) {
                g.drawLine(points[edge.a()][0], points[edge.a()][1], points[edge.b()][0], points[edge.b()][1]);
            }
            g.drawString("Distinct edges: " + distinct.size(), 12, 20);
        }
    };
    canvas.setPreferredSize(new Dimension(600, 400));
    var controls = new JPanel();
    var pause = new JButton("Pause / Run");
    pause.addActionListener(event -> running = !running);
    var restart = new JButton("Reset angle");
    restart.addActionListener(event -> {
/// Připravený restart: vytvoří počáteční model (model pravidel); neotevírá okno.
        reset(42);
        canvas.repaint();
    });
    controls.add(pause);
    controls.add(restart);
    var timer = new Timer(25, event -> {
        if (running) {
            angle += 0.025;
        }
        canvas.repaint();
    });
    frame.addWindowListener(new WindowAdapter(){

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

record Edge(int a, int b) {}

void main() {
/// Připravený restart: vytvoří počáteční model (model pravidel); neotevírá okno.
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
/// - `double` — Numeric type (číselný typ) pro přibližná desetinná čísla. Java používá desetinnou tečku.
/// - `boolean` — Hodnota true nebo false: ano nebo ne.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
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
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `public` — Zpřístupňuje tuto declaration (deklaraci) dalšímu kódu.
/// - `protected` — Visibility keyword (klíčové slovo přístupnosti) používané připraveným library callback
///   (knihovním zpětným voláním), například paintComponent.
/// - `super` — V super.paintComponent(g) požádá připravenou parent implementation (nadřazenou
///   implementaci), aby nejprve vykreslila běžné pozadí.
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `Set` — Uchovává různé hodnoty; stejná hodnota je přítomná nejvýše jednou.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
