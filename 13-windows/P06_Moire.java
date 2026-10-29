// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Point2D;
// </editor-fold>

/// # 13.06 · Moire
///
/// Polož jemné mřížky přes sebe a sleduj vznik velkých pohyblivých pásů. Moaré vzniká jejich překrytím,
/// nikoli přímým kreslením těchto pásů.
///
/// **Tvoje mise:** Otočená mřížka je vodorovně vystředěná, ale svisle přilepená k hornímu okraji. Oprav
/// screen coordinate (souřadnice obrazovky), aby se otáčela kolem zamýšleného středu.
///
/// **Dnes trénuješ:** Grid coordinate (souřadnice mřížky) a screen coordinate (souřadnice obrazovky) mají
/// různé počátky. Vzorec otáčení je připravený; obě výsledné osy musí ležet kolem stejného středu plátna.
///
/// Po spuštění programu pomocí Left/Right otáčej jednu grid (mřížku) a Up/Down měň rozestupy
/// pro zkoumání překrytí; R obnoví počáteční vzor.
Point2D.Double gridPoint(int column, int row, double a) {
    double x = column * spacing, y = row * spacing;
    return new Point2D.Double(WIDTH / 2 + x * Math.cos(a) - y * Math.sin(a), x * Math.sin(a) + y * Math.cos(a));
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Vytvoř detektor pohybu bez pohyblivých součástí: zvol dvě kontrastní barvy mřížek a najdi úhel,
/// kde drobný stisk klávesy vyvolá zdánlivě obrovské vlny. Zapiš úhel a rozteč, aby je někdo jiný
/// mohl zopakovat.
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

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `column * spacing` — Převede číslo sloupce mřížky na vodorovnou vzdálenost od jejího počátku.
/// - `Math.cos(a)` — Součást připraveného vzorce otáčení; a je úhel v radiánech.
/// - `WIDTH / 2 +` — Po otočení přidá vodorovný posun ke středu obrazovky.
/// - `x * Math.sin(a) + y * Math.cos(a)` — Vytvoří otočenou svislou vzdálenost, ještě ne její absolutní
///   pozici na obrazovce.
///
/// ### Co už obstarává připravená část
///
/// draw překryje dvě bodové grids (mřížky) pomocí gridPoint. Left/Right otáčí jednu grid a Up/Down
/// mění rozestup; R obnoví počáteční geometrii.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Místní bod (0,0) má skončit ve středu plátna.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Výsledek y potřebuje posun středu stejně jako x.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Ověř (1,0) při čtvrtotočce; odhalí chybu osy či znaménka.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 440;

double angle;
int spacing;
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
void restart() {
    angle = .07;
    spacing = 22;
}

void adjust(int amount) {
    spacing = Math.max(12, Math.min(40, spacing + amount));
}

int extent() {
    return (int)Math.ceil(Math.hypot(WIDTH, HEIGHT) / 2 / spacing) + 1;
}


/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
void draw(Graphics2D g) {
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    int limit = extent();
    for (int layer = 0; layer < 2; layer++) {
        double a = layer == 0 ? 0 : angle;
        g.setColor(layer == 0 ? new Color(20, 20, 20, 200) : new Color(40, 80, 170, 170));
        for (int y = -limit; y <= limit; y++) {
            for (int x = -limit; x <= limit; x++) {
                var p = gridPoint(x, y, a);
                g.fillOval((int)p.x - 2, (int)p.y - 2, 4, 4);
            }
        }
    }
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, 28);
    g.setColor(Color.BLACK);
    g.drawString(String.format("Angle %.2f degrees   Spacing %d", Math.toDegrees(angle), spacing), 10, 20);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_LEFT -> angle -= .01;
            case KeyEvent.VK_RIGHT -> angle += .01;
            case KeyEvent.VK_UP -> adjust(1);
            case KeyEvent.VK_DOWN -> adjust(-1);
        }
    }
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Moir\u00e9");
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
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Left/Right: rotate   Up/Down: spacing   R: reset</div></html>", SwingConstants.CENTER);
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
