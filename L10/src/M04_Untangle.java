// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
// </editor-fold>

/// # L10.04 · Untangle
///
/// Rozmotej síť tažením očíslovaných bodů, až se její hrany přestanou křížit. Kolečka i spojnice se musí
/// shodnout, kde každý bod skutečně leží.
///
/// **Tvoje mise:** Kolečko bodu prohodí x a y, zatímco popisek i hrany používají správnou pozici. Oprav
/// kreslicí souřadnice a neposouvej samotnou hádanku.
///
/// **Dnes trénuješ:** Rendering (vykreslení) převádí coordinate (souřadnice) modelu na viditelné značky.
/// Levý horní roh kolečka je posunutý od středu, ale x stále znamená vodorovně a y svisle.
///
/// Po Run File tahej očíslované body a rozmotej křížení sítě; klávesou R ji můžeš znovu promíchat.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F29 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
void drawNode(Graphics2D g, Point point, int label) {
    g.setColor(new Color(35, 55, 90));
    g.fillOval(point.x - 13, point.y - 13, 26, 26);
    g.setColor(Color.WHITE);
    g.drawString("" + label, point.x - 4, point.y + 4);
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F29">
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
/// - `point.x` — Vodorovná coordinate (souřadnice) modelu; má souhlasit s konci hran.
/// - `point.y` — Svislá coordinate (souřadnice), která na této obrazovce roste dolů.
/// - `g.fillOval(point.y - 13, point.x - 13, 26, 26)` — FillOval očekává levou pozici, horní pozici,
///   šířku a výšku; odečtení 13 vystředí kruh o průměru 26.
///
/// ### Co už obstarává připravená část
///
/// Tažení mění skutečné hodnoty Point; připravená kontrola křížení vyhodnocuje hrany mezi nimi. DrawNode
/// pouze maluje jeden bod, proto oprava nemá změnit rozmístění ani pravidla.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// FillOval přijímá levý horní roh, nikoli střed.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Od x a y samostatně odečti poloměr.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Použij nestejný bod jako (120,70), nikoli bod na diagonále, který prohození os skryje.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 600, HEIGHT = 420;

int[][] edges = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 7}, {7, 0}, {0, 2}, {2, 4}, {4, 6}, {6, 0}};
Point[] points = new Point[8];
int selected = -1;
void layout(boolean scrambled) {
    int[] order = scrambled ? new int[]{0, 4, 1, 5, 2, 6, 3, 7} : new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    for (int i = 0; i < 8; i++) {
        double a = order[i] * Math.PI / 4;
        points[i] = new Point(300 + (int)(160 * Math.cos(a)), 210 + (int)(160 * Math.sin(a)));
    }
}

/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
void restart() {
    selected = -1;
    layout(true);
}

boolean crosses(int[] a, int[] b) {
    for (int x : a) {
        for (int y : b) {
            if (x == y) {
                return false;
            }
        }
    }
    return Line2D.linesIntersect(points[a[0]].x, points[a[0]].y, points[a[1]].x, points[a[1]].y,
    points[b[0]].x, points[b[0]].y, points[b[1]].x, points[b[1]].y);
}

int crossings() {
    int count = 0;
    for (int i = 0; i < edges.length; i++) {
        for (int j = i + 1; j < edges.length; j++) {
            if (crosses(edges[i], edges[j])) {
                count++;
            }
        }
    }
    return count;
}

void pick(int x, int y) {
    selected = -1;
    for (int i = 0; i < 8; i++) {
        if (points[i].distance(x, y)<15) {
            selected = i;
        }
    }
}

void moveSelected(int x, int y) {
    if (selected < 0) {
        return;
    }
    x = Math.max(18, Math.min(WIDTH - 18, x));
    y = Math.max(38, Math.min(HEIGHT - 18, y));
    for (int i = 0; i < 8; i++) {
        if (i != selected && points[i].distance(x, y)<25) {
            return;
        }
    }
    points[selected].setLocation(x, y);
}

/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
void draw(Graphics2D g) {
    g.setStroke(new BasicStroke(2));
    for (int i = 0; i < edges.length; i++) {
        boolean crossed = false;
        for (int j = 0; j < edges.length; j++) {
            if (i != j && crosses(edges[i], edges[j])) {
                crossed = true;
            }
        }
        g.setColor(crossed ? new Color(220, 70, 70) : new Color(65, 155, 130));
        var a = points[edges[i][0]];
        var b = points[edges[i][1]];
        g.drawLine(a.x, a.y, b.x, b.y);
    }
    for (int i = 0; i < 8; i++) {
        drawNode(g, points[i], i);
    }
    g.setColor(Color.BLACK);
    int n = crossings();
    g.drawString(n == 0 ? "Untangled! R: scramble again" : "Crossing edge pairs: " + n, 15, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
        restart();
    }
}

void press(int x, int y, int button) {
    pick(x, y);
}

void drag(int x, int y) {
    moveSelected(x, y);
}

void release(int x, int y) {
    selected = -1;
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Untangle");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Drag numbered nodes until every edge is green   R: restart</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Připravená část zajišťuje pouze uvedenou hlavní hru nebo pokus.
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
/// - `!=` — Ověří, že se dvě hodnoty liší.
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
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
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

