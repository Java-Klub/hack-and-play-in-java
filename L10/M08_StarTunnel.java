// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;
// </editor-fold>

/// # L10.08 · StarTunnel
///
/// Prolétej tunelem hvězd a řiď jeho úběžník. Blízké hvězdy mají uhánět ven, zatímco vzdálené se shlukují
/// u středu.
///
/// **Tvoje mise:** Dodatečný posun hloubky kreslí hvězdy v nesprávném měřítku. Obnov použití požadované
/// hloubky a zachovej ochranu před nebezpečným dělením u blízké roviny.
///
/// **Dnes trénuješ:** Projection (promítání) mění pozici modelu a hloubku na bod obrazovky. Menší
/// bezpečná hloubka dává větší odchylku; převod má důsledně použít předaný požadavek.
///
/// Po spuštění programu kliknutím nebo tažením spusť a řiď hvězdný tunel; Space pozastaví pohyb
/// a R obnoví výchozí pohled.
Point2D.Double project(Star star, double depth) {
    double z = Math.max(5, depth + 100);
    return new Point2D.Double(centreX + star.x * 260 / z, centreY + star.y * 260 / z);
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F29">
/// ### Rendering (vykreslení): ze stavu na pixely
///
/// Rendering čte model (stav programu) a kreslí jeho podobu. Screen x (obrazovkové x) roste doprava;
/// screen y roste dolů. Kreslicí metody často přijímají levý horní roh a velikost, nikoli střed.
///
/// ```java
/// int tileX = 3;
/// int tileY = 2;
/// int size = 10;
/// g.fillRect(tileX * size, tileY * size, size, size);
/// ```
///
/// `g` je připravený kreslicí nástroj `Graphics2D`. Příkaz vybarví čtverec 10 × 10 s levým horním
/// rohem `(30, 20)`. Modelové souřadnice zůstanou `(3, 2)`. Repainting (překreslení) nemá posouvat
/// objekty ani zahajovat další tah: okno může požadovat kreslení mnohokrát.
///
/// Předpověz: pokud se pouze tileY změní na 4, která obrazovková souřadnice se změní a o kolik?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `Math.max(5, depth + 100)` — Omezí dělitel bezpečným minimem, ale zároveň mění požadovanou hloubku.
/// - `star.x * 260 / z` — Mění vodorovný posun modelu podle převrácené hloubky.
/// - `centreX +` — Umístí promítnutý posun kolem úběžníku řízeného hráčem.
///
/// ### Co už obstarává připravená část
///
/// step posouvá hvězdy jedinou pevnou rychlostí a draw používá project pro perspektivu.
/// Kliknutí/tažení řídí a spouští; Space pozastaví a R obnoví pohled.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Perspektiva dělí posun x/y hloubkou.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Omezení chrání nulovou či zápornou zkušební hloubku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Použij max(5, depth) bez další vzdálenosti.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 440;

record Star(double x, double y, double z) {
}

ArrayList<Star> stars = new ArrayList<>();
Random random;
double centreX, centreY, speed;
boolean paused;
Star fresh() {
    return new Star(random.nextDouble() * 1000 - 500, random.nextDouble() * 800 - 400, 800);
}

/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
void restart() {
    random = new Random(38);
    stars.clear();
    for (int i = 0; i < 220; i++) {
        var s = fresh();
        stars.add(new Star(s.x, s.y, 20 + random.nextDouble() * 780));
    }
    centreX = WIDTH / 2;
    centreY = HEIGHT / 2;
    speed = 3;
    paused = true;
}

void aim(int x, int y) {
    centreX = Math.max(0, Math.min(WIDTH, x));
    centreY = Math.max(0, Math.min(HEIGHT, y));
}


/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
void step() {
    if (paused) {
        return;
    }
    for (int i = 0; i < stars.size(); i++) {
        var s = stars.get(i);
        double z = s.z - speed;
        stars.set(i, z < 5 ? fresh() : new Star(s.x, s.y, z));
    }
}

/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
void draw(Graphics2D g) {
    g.setColor(new Color(7, 10, 24));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    for (var s : stars) {
        var head = project(s, s.z);
        var tail = project(s, s.z + speed * 4);
        int light = (int)Math.min(255, 100 + 155 * (1 - s.z / 800));
        g.setColor(new Color(light, light, 255));
        g.draw(new Line2D.Double(tail, head));
    }
    g.setColor(Color.WHITE);
    g.drawString("Speed: " + (int)speed + "   " + (paused ? "Click or Space to fly" : "Drag to steer the vanishing point"), 12, 22);
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
    aim(x, y);
    paused = false;
}

void drag(int x, int y) {
    aim(x, y);
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Star Tunnel");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click/drag: steer + start   Space: pause   R: reset</div></html>", SwingConstants.CENTER);
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
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
/// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
///   se ale dál měnit může.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
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
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
