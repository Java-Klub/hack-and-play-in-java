// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import javax.swing.Timer;
// </editor-fold>

/// # L10.11 · OrbitPainter
///
/// Vystřel částici kolem přitažlivého středu a nech její dráhu malovat barevnou stopu. Hledej dlouhý
/// smyčkový let, který neopustí plátno.
///
/// **Tvoje mise:** Podmínka pause (pozastavení) je obrácená, takže se dráha za běhu neposouvá. Oprav tick
/// guard (podmínku časového kroku) a při zastavení zachovej pozice i stopu.
///
/// **Dnes trénuješ:** Pause (pozastavení) slibuje, že tick (časový krok) nic nezmění. Boolean guard
/// (logická podmínka) musí tento slib dodržet ještě před připravenou fyzikou či trvalým kreslením.
///
/// Po spuštění programu stiskni Space pro start nebo tažením zvol novou počáteční rychlost;
/// Space pozastaví pohyb a R vymaže stopu a obnoví pokus.
void step() {
    if (!paused) {
        return;
    }
    double dx = WIDTH / 2 - x, dy = HEIGHT / 2 - y, r2 = dx * dx + dy * dy + 25 * 25;
    vx += 600 * dx / Math.pow(r2, 1.5);
    vy += 600 * dy / Math.pow(r2, 1.5);
    double nx = x + vx, ny = y + vy;
    var g = canvas.createGraphics();
    g.setColor(Color.getHSBColor((ticks++ % 1000) / 1000f, .8f, 1));
    g.draw(new Line2D.Double(x, y, nx, ny));
    g.dispose();
    x = nx;
    y = ny;
    if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
        paused = true;
        message = "Escaped - drag a new launch";
    }
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F30">
/// ### Input (vstup) požádá; tick (krok) posune čas
///
/// Event (událost) může zaznamenat záměr bez okamžité změny celé simulace. Tick provede jednu
/// omezenou aktualizaci. Kreslení drž oddělené od obou činností.
///
/// ```java
/// int height = 0;
/// int pending = 0;
/// void press() {
///     pending = 2;
/// }
/// void tick() {
///     height += pending;
///     pending = 0;
/// }
/// ```
///
/// Po `press()` je height stále 0. Jeden `tick()` jej změní na 2 a požadavek spotřebuje; další tick
/// jej ponechá na 2. Opakovaný vstup před tímto krokem stále zaznamená jeden požadavek místo dvou
/// aktualizací. Připravený timer (časovač) může tick volat, ale pozastavená nebo dokončená hra nemá
/// pokračovat. Restart musí obnovit počáteční stav.
///
/// Předpověz: co od počátečního stavu udělá pořadí press, tick, press, tick?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `!paused` — Znamená bez pozastavení; porovnej význam s předčasným návratem pod ním.
/// - `return;` — Zabrání všem dalším změnám v tomto tick (časovém kroku), ne jen změně pozice.
/// - `ticks++` — Posune počítadlo barevného času při malování nové části stopy.
///
/// ### Co už obstarává připravená část
///
/// step posune orbit (oběžnou dráhu) a nakreslí stopu. Tažení zvolí počáteční rychlost, Space
/// spustí/pozastaví a R stopu vymaže a obnoví pokus.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Paused je důvod neprovádět práci.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Return nech v branch (větvi) pro pauzu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Přitažlivost a vzorec stopy pod guard (podmínkou) už jsou správné.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 440;

BufferedImage canvas;
double x, y, vx, vy, aimX, aimY;
boolean paused, aiming;
int ticks;
String message;
void clear() {
    var g = canvas.createGraphics();
    g.setColor(new Color(12, 17, 30));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.dispose();
}

/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
void restart() {
    canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    clear();
    x = 440;
    y = 220;
    vx = 0;
    vy = 2.1;
    ticks = 0;
    paused = true;
    aiming = false;
    message = "Space: default orbit, or drag a new launch";
}

void launch(double xx, double yy, double endX, double endY) {
    x = xx;
    y = yy;
    vx = (endX - xx) * .025;
    vy = (endY - yy) * .025;
    double speed = Math.hypot(vx, vy);
    if (speed > 6) {
        vx *= 6 / speed;
        vy *= 6 / speed;
    }
    paused = false;
    aiming = false;
    message = "Painting orbit";
}


/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
void draw(Graphics2D g) {
    g.drawImage(canvas, 0, 0, null);
    g.setColor(Color.WHITE);
    g.fillOval(WIDTH / 2 - 7, HEIGHT / 2 - 7, 14, 14);
    g.setColor(Color.YELLOW);
    g.fillOval((int)x - 3, (int)y - 3, 6, 6);
    if (aiming) {
        g.setColor(Color.CYAN);
        g.draw(new Line2D.Double(x, y, aimX, aimY));
    }
    g.setColor(Color.WHITE);
    g.drawString(message, 12, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_SPACE -> paused = !paused;
        }
    }
}

void press(int x, int y, int button) {
    x = Math.max(0, Math.min(WIDTH - 1, x));
    y = Math.max(0, Math.min(HEIGHT - 1, y));
    this.x = aimX = x;
    this.y = aimY = y;
    paused = true;
    aiming = true;
}

void drag(int x, int y) {
    if (aiming) {
        aimX = x;
        aimY = y;
    }
}

void release(int x, int y) {
    if (aiming) {
        launch(this.x, this.y, x, y);
    }
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Orbit Painter");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            var g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
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
    var mouse = new MouseAdapter() {
        @Override public void mousePressed(MouseEvent e) {
            panel.requestFocusInWindow();
            press(e.getX(), e.getY(), e.getButton());
            panel.repaint();
        }
        @Override public void mouseDragged(MouseEvent e) {
            drag(e.getX(), e.getY());
            panel.repaint();
        }
        @Override public void mouseReleased(MouseEvent e) {
            release(e.getX(), e.getY());
            panel.repaint();
        }
    };
    panel.addMouseListener(mouse);
    panel.addMouseMotionListener(mouse);
    var timer = new Timer(20, e -> {
/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
        step();
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent e) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Drag: position + launch velocity   Space: pause   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

void main() {
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
    restart();
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
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
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
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
