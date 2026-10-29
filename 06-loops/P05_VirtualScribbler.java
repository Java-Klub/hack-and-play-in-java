// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.Timer;
// </editor-fold>

/// # 06.05 · VirtualScribbler
///
/// Nech neposedné pero rozvíjet kresbu po plátně a potom řiď jeho tah a roztěkanost. Zastav
/// zajímavý vzor a prohlédni si ho před vyčištěním plátna pro další pokus.
///
///
/// **Tvoje mise:** Změň Signature na opakovatelný čtvercový podpis: začni uprostřed, projeď čtyři rovné
/// stejně dlouhé strany po třiceti krocích pera a skonči pozastavený v počátku. Počítadlo ukáže 120.
/// Dřívější drift (zatáčení), jitter (náhodné odchylky), poloha ani kresba podpis neovlivní. Kreslení i
/// krokovací tlačítka už fungují.
///
/// **Dnes se učíš:** For loop (smyčka for) může opakovat malý program složený z více akcí. Call
/// (volání) mění stav, ze kterého vyjde další akce.
///
/// Klikni na Signature, potom prozkoumej pero pomocí Space a šipek. Klikni znovu na Signature: současný
/// recept dá novou přímku místo čtverce.
void signature() {
    restart();
    drift = 0;
    jitter = 0;
    oneShot(24);
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F11">
/// Counted for (počítaná smyčka for) sdruží initialization (počáteční nastavení), podmínku
/// pokračování a postup do jediného záhlaví. Nastavení proběhne jednou. Potom Java ověří condition
/// (podmínku), při pravdě provede tělo, udělá postup a znovu ověří podmínku.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// for (int floor = 2; floor <= 4; floor++) {
///     IO.println(floor);
/// }
/// ```
///
/// Předpověz navštívené hodnoty: `2`, `3`, `4`. Jsou to tři průchody, i když poslední vypsané číslo
/// je čtyřka. Dva středníky oddělují tři části záhlaví; `floor++` přičte jedničku po průchodu.
/// Složené závorky sdružují opakované statement (příkazy). Pokud počáteční hodnota podmínku už
/// nesplňuje, neproběhne žádný průchod. Rozlišuj počet pozic a mezer: tři pozice mají dvě mezery. Než
/// změníš hranici, napiš drobnou trace (stopu průběhu).
///
/// Nyní předpověz output (výstup), pokud floor začíná na pětce.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Jak číst tento kód">
/// Opakovaná kresba je program, nikoli obrázkový soubor. Vypiš akce jedné strany a změnu potřebnou před
/// další stranou; potom rozděl, co patří dovnitř opakování a co mimo něj.
///
/// U jiného receptu se třemi částmi po dvou krocích postupuje počítadlo 0 → 2 → 4 → 6. Otočení mění
/// směr, ale samo nekreslí krok. Předpověz počet pro pět takových částí. Sleduj skutečné tlačítko
/// Signature k jeho akci a porovnej ji s krokovacími tlačítky.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// Pero ujede 2.2 pixelu za krok. angle měří směr v radians (radiánech): 0 doprava, Math.PI / 2 dolů,
/// Math.PI doleva a 3 * Math.PI / 2 nahoru. Přičtení Math.PI / 2 znamená čtvrtotáčku. Nulové drift a
/// jitter zachovají zvolený směr. paused true brání kreslení; restart vyčistí plátno a vystředí pero.
/// Jde o existující hodnoty model (modelu), nikoli nové kreslicí API.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Odděl přípravu, opakovanou stranu a závěrečné pozastavení. Které akce mají nastat jen jednou?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Signature volá signature. burst kreslí požadovaný počet kroků; angle určuje směr, drift a jitter jej
/// před krokem mění.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Jednou resetuj, odstraň drift a jitter, čtyřikrát opakuj třicet kroků a čtvrtotáčku, nakonec pozastav.
/// Kreslení strany musí být povoleno.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta">
/// Vymysli druhý podpis se dvěma různými opakovanými motivy. Pojmenuj ho, před kreslením odvoď počet
/// kroků a nech spolužáka rekonstruovat recept z kresby.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
void burst(int count) {
    if (count < 0 || count > 60) {
        return;
    }
    for (int i = 0; i < count; i++) {
        step();
    }
}

final int WIDTH = 640, HEIGHT = 450;

BufferedImage canvas;
Random random;
double x, y, angle;
int drift, jitter, steps;
boolean paused;
void clear() {
    var g = canvas.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.dispose();
}

void restart() {
    canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    clear();
    random = new Random(71);
    x = WIDTH / 2;
    y = HEIGHT / 2;
    angle = 0;
    drift = 3;
    jitter = 4;
    steps = 0;
    paused = true;
}

void adjust(int d, int j) {
    drift = Math.max(-10, Math.min(10, drift + d));
    jitter = Math.max(0, Math.min(10, jitter + j));
}

void step() {
    if (paused) {
        return;
    }
    angle += drift * .008 + random.nextGaussian() * jitter * .04;
    double nx = x + Math.cos(angle) * 2.2, ny = y + Math.sin(angle) * 2.2;
    if (nx < 2 || nx > WIDTH - 3) {
        angle = Math.PI - angle;
        nx = Math.max(2, Math.min(WIDTH - 3, nx));
    }
    if (ny < 2 || ny > HEIGHT - 3) {
        angle = -angle;
        ny = Math.max(2, Math.min(HEIGHT - 3, ny));
    }
    var g = canvas.createGraphics();
    g.setColor(Color.getHSBColor((steps++ % 1500) / 1500f, .75f, .7f));
    g.setStroke(new BasicStroke(1.5f));
    g.draw(new Line2D.Double(x, y, nx, ny));
    g.dispose();
    x = nx;
    y = ny;
}

void draw(Graphics2D g) {
    g.drawImage(canvas, 0, 0, null);
    g.setColor(Color.RED);
    g.drawOval((int)x - 4, (int)y - 4, 8, 8);
    g.setColor(new Color(245, 245, 245));
    g.fillRect(0, 0, WIDTH, 28);
    g.setColor(Color.BLACK);
    g.drawString("Steps: " + steps + "  Drift: " + drift + "  Jitter: " + jitter + "  " + (paused ? "Paused" : "Drawing"), 10, 20);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_SPACE -> paused = !paused;
            case KeyEvent.VK_LEFT -> adjust(-1, 0);
            case KeyEvent.VK_RIGHT -> adjust(1, 0);
            case KeyEvent.VK_UP -> adjust(0, 1);
            case KeyEvent.VK_DOWN -> adjust(0, -1);
            case KeyEvent.VK_C -> clear();
        }
    }
}

void oneShot(int count) {
    paused = false;
    burst(count);
    paused = true;
}

void showWindow() {
    var frame = new JFrame("Virtual Scribbler");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            var g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            draw(g);
            g.dispose();
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    panel.setBackground(Color.WHITE);
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent e) {
            key(e.getKeyCode(), true);
            panel.repaint();
        }
    });
    var timer = new Timer(20, e -> {
        burst(4);
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent e) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Space: play / pause   Arrows: drift / jitter   C: clear   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    var controls = new JPanel(new BorderLayout());
    controls.add(help, BorderLayout.NORTH);
    var buttons = new JPanel();
    var stamp = new JButton("Signature");
    stamp.setFocusable(false);
    stamp.addActionListener(e -> {
        signature();
        panel.repaint();
        panel.requestFocusInWindow();
    });
    buttons.add(stamp);
    for (int count : new int[]{1, 12}) {
        var button = new JButton(count == 1 ? "1 step" : "12 steps");
        button.setFocusable(false);
        button.addActionListener(e -> {
            oneShot(count);
            panel.repaint();
            panel.requestFocusInWindow();
        });
        buttons.add(button);
    }
    controls.add(buttons, BorderLayout.SOUTH);
    frame.add(controls, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

void main() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("This game needs a graphical display; model checks still work headlessly.");
        return;
    }
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}

/// Připravené vstupní body: main, restart, draw, showWindow.
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
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
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
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `switch` — Vybere case (větev) z několika možností. Může také vrátit výsledek.
/// - `case` — Jedna větev switch; šipka ukazuje, co větev provede nebo vrátí.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `public` — Zpřístupňuje tuto declaration (deklaraci) dalšímu kódu.
/// - `protected` — Visibility keyword (klíčové slovo přístupnosti) používané připraveným library callback
///   (knihovním zpětným voláním), například paintComponent.
/// - `super` — V super.paintComponent(g) požádá připravenou parent implementation (nadřazenou
///   implementaci), aby nejprve vykreslila běžné pozadí.
/// - `null` — Žádný object (objekt) tu není.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
