// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.Timer;
// </editor-fold>

/// # L10.05 · SpringToy
///
/// Odtáhni závaží, pusť je a sleduj stopu pohybu pružiny. Měň tuhost a tlumení a hledej pomalé houpání
/// nebo živé poskakování.
///
/// **Tvoje mise:** Malování okna posouvá fyziku, takže i překreslení může pohnout zastaveným závažím.
/// Obnov draw method (kreslicí metodu), která jen čte aktuální model (model pravidel).
///
/// **Dnes trénuješ:** Rendering (vykreslení) má být věrný snapshot (snímek), ne další hodiny. Pohyb už
/// spravuje timer (časovač); požadavek na kreslení může přijít bez nového kroku simulace.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L10/M05_SpringToy.java`
/// - Kontrola této mise: `java L10/Check.java M05_SpringToy.java`
///
/// Po spuštění programu odtáhni a pusť závaží, pomocí Space zastav pohyb a šipkami měň tuhost a
/// tlumení pružiny. Po opravě: ulož soubor, spusť uvedenou kontrolu a zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F29 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
void draw(Graphics2D g) {
// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
    step();
    g.setColor(new Color(225, 230, 240));
    for (int i = 1; i < trail.size(); i++) {
        g.draw(new Line2D.Double(trail.get(i - 1), trail.get(i)));
    }
    g.setColor(Color.GRAY);
    g.setStroke(new BasicStroke(2));
    var spring = new Path2D.Double();
    spring.moveTo(WIDTH / 2, 65);
    double dx = x - WIDTH / 2, dy = y - 65, len = Math.max(1, Math.hypot(dx, dy));
    for (int i = 1; i < 20; i++) {
        double a = i / 20.0, zig = (i % 2 == 0 ? -7 : 7);
        spring.lineTo(WIDTH / 2 + dx * a - dy / len * zig, 65 + dy * a + dx / len * zig);
    }
    spring.lineTo(x, y);
    g.draw(spring);
    g.setColor(Color.BLACK);
    g.fillRect(WIDTH / 2 - 20, 58, 40, 7);
    g.setColor(new Color(65, 110, 210));
    g.fillOval((int)x - 18, (int)y - 18, 36, 36);
    g.setColor(Color.BLACK);
    g.drawString(String.format("Stiffness %.3f   Damping %.3f   %s", stiffness, damping, paused ? "Paused" : "Moving"), 15, 25);
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
/// - `step();` — Volá fyzikální update (aktualizaci); zvaž, proč by požadavek kreslení měl posouvat čas.
/// - `trail.size()` — Čte počet uložených pozic dostupných pro kreslení stopy.
/// - `g.draw(spring)` — Nakreslí připravený tvar pružiny bez potřeby změnit pozici závaží.
///
/// ### Co už obstarává připravená část
///
/// Připravený timer (časovač) volá step a zvlášť požádá o překreslení. Step počítá zrychlení pružiny a
/// ukládá pozice; draw sestaví klikatou pružinu, závaží i popisek z existujících hodnot.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Porovnej odpovědnosti step a draw.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Podívej se na první akci v draw; repaint (překreslení) může nastat častěji než tick (časový krok).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Odstraň nadbytečný update (aktualizaci) modelu a zachovej kreslení.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 440;

double x, y, vx, vy, stiffness, damping;
boolean paused, grabbed;
ArrayList<Point2D.Double> trail = new ArrayList<>();
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
void restart() {
    x = WIDTH / 2;
    y = 260;
    vx = vy = 0;
    stiffness = .012;
    damping = .985;
    paused = true;
    grabbed = false;
    trail.clear();
}

void tune(double k, double d) {
    stiffness = Math.max(.002, Math.min(.04, stiffness + k));
    damping = Math.max(.9, Math.min(1, damping + d));
}

void pull(int xx, int yy) {
    x = Math.max(12, Math.min(WIDTH - 12, xx));
    y = Math.max(95, Math.min(HEIGHT - 12, yy));
    vx = vy = 0;
}

/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
void step() {
    if (paused || grabbed) {
        return;
    }
    double dx = x - WIDTH / 2, dy = y - 65, length = Math.max(1, Math.hypot(dx, dy));
    double force = -stiffness * (length - 115);
    vx = (vx + force * dx / length) * damping;
    vy = (vy + force * dy / length + .06) * damping;
    double speed = Math.hypot(vx, vy);
    if (speed > 8) {
        vx *= 8 / speed;
        vy *= 8 / speed;
    }
    x += vx;
    y += vy;
    if (x < 12 || x > WIDTH - 12) {
        x = Math.max(12, Math.min(WIDTH - 12, x));
        vx *= -.5;
    }
    if (y < 80 || y > HEIGHT - 12) {
        y = Math.max(80, Math.min(HEIGHT - 12, y));
        vy *= -.5;
    }
    trail.add(new Point2D.Double(x, y));
    if (trail.size()>800) {
        trail.remove(0);
    }
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_SPACE -> paused = !paused;
            case KeyEvent.VK_UP -> tune(.002, 0);
            case KeyEvent.VK_DOWN -> tune(-.002, 0);
            case KeyEvent.VK_LEFT -> tune(0, -.005);
            case KeyEvent.VK_RIGHT -> tune(0, .005);
        }
    }
}

void press(int x, int y, int button) {
    if (Math.hypot(x - this.x, y - this.y)<30) {
        grabbed = true;
        pull(x, y);
    }
}

void drag(int x, int y) {
    if (grabbed) {
        pull(x, y);
    }
}

void release(int x, int y) {
    if (grabbed) {
        pull(x, y);
        grabbed = false;
        paused = false;
    }
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Spring Toy");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Drag mass: pull / release   Space: pause   Up/Down: stiffness   Left/Right: damping   R:<br>reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

/// Program spusť uvedeným command (příkazem) pro hraní. Kontrola hry ověří pravidlo bez otevření
/// okna. Připravená část zajišťuje pouze uvedenou hlavní hru nebo pokus.
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
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

