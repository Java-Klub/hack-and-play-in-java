// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # 13.05 · MirrorArt
///
/// Nakresli jeden tah a nech kruh zrcadlených kopií proměnit jej v růžici. Změna počtu os může ze
/// stejného gesta udělat úplně jiný obrazec.
///
/// **Tvoje mise:** Implementuj celý coordinate adapter (převod souřadnic) v transform. Odečti střed
/// plátna, případně odraz místní y, otoč o 2π × copy / axes a střed zase přičti. Vrať nový bod;
/// původní tah neměň.
///
/// **Dnes trénuješ:** Rendering (kreslení) nebo coordinate conversion (převod souřadnic) vytvoříš
/// jako řadu kroků: čti model, převeď hodnoty a kresli nebo vrať nový bod bez posunu času.
///
/// Po spuštění programu táhni čáru a prohlédni její odrazy, pomocí Up/Down měň osy zrcadlení a
/// R kresbu vymaž a obnov.
Point2D.Double transform(int x, int y, int copy, boolean reflect) {
    // Sestav místní souřadnice, odraz, otočení a návrat na plátno.
    return new Point2D.Double(0, 0);
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Navrhni dvoubarevný znak: v připraveném kreslicím cyklu dej odraženým tahům jinou barvu než
/// otočeným. Nakresli jediný promyšlený tah a vyber počet os, který jej promění ve tvůj znak.
// </editor-fold>

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

// <editor-fold defaultstate="expanded" desc="Jak číst tento kód">
/// ### Tři souřadnicové pohledy, jeden bod
///
/// Použij připravené matematické pravidlo: otočení místního (dx, dy) o úhel a dává x' = dx cos(a) -
/// dy sin(a) a y' = dx sin(a) + dy cos(a). Math.sin a Math.cos přijímají radians (radiány); celá
/// otočka je 2π. Pravidlo implementuješ, nemusíš objevit trigonometrii.
///
/// Příklad s jiným středem plátna (50, 40): screen point (60, 40) se změní na místní (10, 0).
/// Čtvrtotočka dá (0, 10), takže nový bod plátna je (50, 50). Předpověz nulové otočení a potom
/// čtvrtotočku pro (50, 30). Místní reflection (odraz) před otočením neguje dy. Kreslicí cyklus volí
/// copy i reflect a kreslí vrácené body; transform samo nekreslí.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Před otočením pracuj vzhledem ke středu plátna.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Reflection (odraz) mění místní dy, ne absolutní y obrazovky. Oba otočené výrazy počítej odděleně.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Spočti dx/dy, případně neguj dy, spočti úhel a obě otočené souřadnice, potom vytvoř bod posunutý
/// zpět.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 440;

BufferedImage canvas;
int axes, lastX, lastY;
boolean drawing;
String message;
void clear() {
    var g = canvas.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.dispose();
}

/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
void restart() {
    canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    clear();
    axes = 6;
    drawing = false;
    message = "Draw with the mouse";
}

void adjust(int amount) {
    axes = Math.max(2, Math.min(12, axes + amount));
}

void stroke(int x1, int y1, int x2, int y2) {
    var g = canvas.createGraphics();
    g.setColor(new Color(40, 80, 170));
    g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    for (int i = 0; i < axes; i++) {
        for (boolean reflected : new boolean[]{false, true}) {
            g.draw(new Line2D.Double(transform(x1, y1, i, reflected), transform(x2, y2, i, reflected)));
        }
    }
    g.dispose();
}


/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
void draw(Graphics2D g) {
    g.drawImage(canvas, 0, 0, null);
    g.setColor(Color.GRAY);
    g.drawLine(WIDTH / 2 - 4, HEIGHT / 2, WIDTH / 2 + 4, HEIGHT / 2);
    g.drawLine(WIDTH / 2, HEIGHT / 2 - 4, WIDTH / 2, HEIGHT / 2 + 4);
    g.setColor(Color.BLACK);
    g.drawString("Axes: " + axes + "   " + message, 10, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_UP -> adjust(1);
            case KeyEvent.VK_DOWN -> adjust(-1);
        }
    }
}

void press(int x, int y, int button) {
    drawing = true;
    lastX = x;
    lastY = y;
}

void drag(int x, int y) {
    if (drawing) {
        stroke(lastX, lastY, x, y);
        lastX = x;
        lastY = y;
    }
}

void release(int x, int y) {
    drawing = false;
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Mirror Art");
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
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Drag: draw   Up/Down: axes   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
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
