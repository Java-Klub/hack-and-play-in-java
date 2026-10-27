// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L12.02 · FlockPlayground
///
/// Veď hejno pohybem překážky do jeho dráhy. Sleduj, jak místní volby směru promění mnoho samostatných
/// ptáků v plynoucí skupinu.
///
/// **Tvoje mise:** Ptáci nedostanou od překážky žádný příspěvek, protože spojovací část stále vrací nulu.
/// Ať připravené pravidlo vyhýbání dostane skutečnou vzdálenost.
///
/// **Dnes trénuješ:** Composition (skládání) dovolí hejnu používat roli Avoidance bez vlastnictví jejího
/// vzorce. Předání skutečné vzdálenosti je podstatné: jiné pravidlo může reagovat jinak zblízka a zdálky.
///
/// Po Run File posouvej oranžovou překážku myší do dráhy ptáků a pomocí Pause/Run či New flock porovnej
/// jejich reakci.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F34 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
double obstaclePush(double distance) {
    return 0;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F34">
/// ### Interface (rozhraní): požádej vybranou roli o práci
///
/// Interface pojmenovává operaci, kterou dodají jeho implementations (implementace). Volající používá
/// tuto roli bez výběru jejího výpočtu.
///
/// ```java
/// interface Points {
///     int award(int n);
/// }
/// record DoublePoints() implements Points {
///     public int award(int n) {
///         return n * 2;
///     }
/// }
/// ```
///
/// Při `Points rule = new DoublePoints();` vrátí `rule.award(3)` hodnotu 6. `implements Points`
/// slibuje danou operaci; implementace musí dodat její public method (veřejnou metodu). Proměnná rule
/// označuje vybraný objekt, proto volej jeho operaci se současným vstupem místo opakování pevného
/// výpočtu ve volajícím. Jiná implementace může dodat jiná pravidla přes stejné rozhraní.
///
/// Předpověz: co tato implementace vrátí pro `rule.award(5)` a kde by se měnil kód pro trojnásobek
/// vstupu?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `double distance` — Skutečná vzdálenost předaná z výpočtu překážky pro aktuálního ptáka.
/// - `obstaclePush` — Tato spojovací část dodává jen sílu; caller (volající část) už zná směr.
/// - `return 0;` — Hlásí nulový příspěvek překážky bez ohledu na vzdálenost či připravené chování.
///
/// ### Co už obstarává připravená část
///
/// Step spojí separation (oddělování), alignment (sjednocení směru) a cohesion (soudržnost) s
/// obstaclePush před omezením rychlosti. Připravené Avoidance interface (rozhraní) a field (položka
/// objektu) nesou vyměnitelné pravidlo; okno posouvá překážku a spravuje pause i reset.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Interface (rozhraní) dostane vzdálenost a dodá jen sílu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Field (položka objektu) už obsahuje výchozí implementaci.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Volej tento field; nevracej konstantu a nekopíruj výchozí vzorec.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int COUNT = 36;
double[] x = new double[COUNT];
double[] y = new double[COUNT];
double[] vx = new double[COUNT];
double[] vy = new double[COUNT];
double obstacleX = 300;
double obstacleY = 200;
boolean running = true;

/// Připravený restart: vytvoří počáteční model (model pravidel); neotevírá okno.
void reset(long seed) {
    var random = new Random(seed);
    for (int i = 0; i < COUNT; i++) {
        x[i] = random.nextDouble() * 600;
        y[i] = random.nextDouble() * 400;
        double angle = random.nextDouble() * Math.PI * 2;
        vx[i] = Math.cos(angle) * 2;
        vy[i] = Math.sin(angle) * 2;
    }
}

/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
void step() {
    double[] nx = new double[COUNT];
    double[] ny = new double[COUNT];
    for (int i = 0; i < COUNT; i++) {
        double ax = 0;
        double ay = 0;
        int neighbours = 0;
        for (int j = 0; j < COUNT; j++) {
            if (i == j) {
                continue;
            }
            double dx = x[j] - x[i];
            double dy = y[j] - y[i];
            double distance = Math.hypot(dx, dy);
            if (distance > 0 && distance < 80) {
                ax += (vx[j] - vx[i]) * 0.04 + dx * 0.001;
                ay += (vy[j] - vy[i]) * 0.04 + dy * 0.001;
                if (distance < 24) {
                    ax -= dx * 1.8 / (distance * distance);
                    ay -= dy * 1.8 / (distance * distance);
                }
                neighbours++;
            }
        }
        if (neighbours > 0) {
            ax /= neighbours;
            ay /= neighbours;
        }
        double dx = x[i] - obstacleX;
        double dy = y[i] - obstacleY;
        double distance = Math.hypot(dx, dy);
        if (distance > 0 && distance < 75) {
            ax += dx / distance * obstaclePush(distance);
            ay += dy / distance * obstaclePush(distance);
        }
        nx[i] = vx[i] + ax;
        ny[i] = vy[i] + ay;
        double speed = Math.hypot(nx[i], ny[i]);
        if (speed > 3) {
            nx[i] *= 3 / speed;
            ny[i] *= 3 / speed;
        }
    }
    vx = nx;
    vy = ny;
    for (int i = 0; i < COUNT; i++) {
        x[i] = (x[i] + vx[i] + 600) % 600;
        y[i] = (y[i] + vy[i] + 400) % 400;
    }
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Flock Playground");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var canvas = new JPanel(){
        
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics;
            g.setColor(new Color(15, 25, 40));
            g.fillRect(0, 0, 600, 400);
            g.setColor(Color.ORANGE);
            g.drawOval((int)obstacleX - 35, (int)obstacleY - 35, 70, 70);
            g.setColor(Color.CYAN);
            for (int i = 0; i < COUNT; i++) {
                double angle = Math.atan2(vy[i], vx[i]);
                var bird = new Polygon();
                for (double turn : new double[]{0, 2.5, -2.5}) {
                    bird.addPoint((int)(x[i] + 8 * Math.cos(angle + turn)), (int)(y[i] + 8 * Math.sin(angle + turn)));
                }
                g.fillPolygon(bird);
            }
        }
    };
    canvas.setPreferredSize(new Dimension(600, 400));
    var controls = new JPanel();
    canvas.addMouseMotionListener(new MouseMotionAdapter(){
        
        public void mouseMoved(MouseEvent event) {
            obstacleX = event.getX();
            obstacleY = event.getY();
        }
    });
    var pause = new JButton("Pause / Run");
    pause.addActionListener(event -> running = !running);
    var restart = new JButton("New flock");
    restart.addActionListener(event -> {
/// Připravený restart: vytvoří počáteční model (model pravidel); neotevírá okno.
        reset(System.nanoTime());
        canvas.repaint();
    });
    controls.add(pause);
    controls.add(restart);
    var timer = new Timer(25, event -> {
        if (running) {
/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
            step();
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

interface Avoidance { double strength(double distance); }
Avoidance avoidance = distance -> distance > 0 && distance < 75 ? 0.2 : 0;

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// ObstaclePush dodává skutečný příspěvek překážky uvnitř step.
/// Reset tvoří opakovatelné hejno; showWindow posouvá překážku a uklidí připravený timer (časovač).
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
/// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
///   se ale dál měnit může.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
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
/// - `interface` — Popisuje role (roli), kterou mohou plnit různé implementation (implementace). Volající
///   používá roli bez znalosti jejích vnitřností.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

