// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L11.03 · WireframeCube
///
/// Otáčej drátěnou krychlí a sleduj, jak plochá kresba naznačuje hloubku. Počítadlo hran má popsat tvar,
/// i když vstup uvede hranu dvakrát v opačném směru.
///
/// **Tvoje mise:** Stejná neorientovaná hrana se započítá dvakrát při obráceném pořadí konců. Ať
/// rovnocenné dvojice představují jednu odlišnou hranu a chybné dvojice se odmítnou.
///
/// **Dnes trénuješ:** Set (množina) odstraní stejné hodnoty, ne hodnoty, které jen nám připadají
/// rovnocenné. Dej neorientované hraně jednotnou podobu, aby record equality (rovnost record) poznala
/// obrácený vstup.
///
/// Po Run File pomocí Pause/Run zastav otáčení krychle, přečti Distinct edges a pomocí Reset angle obnov
/// pohled.
/// Po opravě: Save → Test File → zahraj si znovu.
Set<Edge> uniqueEdges(int[][] candidates) {
    Set<Edge> result = new LinkedHashSet<>();
    for (int[] pair : candidates) {
        int a = pair[0], b = pair[1];
        if (a < 0 || b < 0 || a >= 8 || b >= 8 || a == b) continue;
        result.add(new Edge(Math.min(a, b), Math.max(a, b)));
    }
    return result;
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `new LinkedHashSet<>()` — Mutable Set (měnitelná množina), která zachovává první pořadí vložení a
///   odstraňuje stejné hodnoty.
/// - `a == b` — Odmítne hranu z vrcholu do sebe ještě před vložením do kolekce.
/// - `new Edge(a, b)` — Record equality (rovnost record) rozliší Edge(1,0) a Edge(0,1), pokud jejich
///   podoba není sjednocená.
///
/// ### Co už obstarává připravená část
///
/// Připravené otáčení a promítání vrcholů nakreslí každou vrácenou hranu. Scéna záměrně obsahuje
/// obrácenou duplicitu, proto popisek Distinct edges odhalí problém; ovládání okna mění jen pohled.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Edge(0,1) a Edge(1,0) nejsou rovné, dokud nezvolíš jednotné vyjádření.
/// 2. Použij min/max pro canonical record value (jednotnou hodnotu record).
/// 3. Set (množina) pak odstraní stejné i obrácené kopie bez dalšího hledacího loop (cyklu).
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

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// UniqueEdges sjednotí upravitelná data hran; project počítá osm bodů obrazovky.
/// Plátno kreslí uniqueEdges i jejich počet; showWindow spravuje otáčení/pauzu a úklid timer (časovače).
void main(String[] args) {
    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
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
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
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

