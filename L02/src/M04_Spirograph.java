// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L02.04 · Spirograph
///
/// Kresli uzavřené spletité křivky inspirované perem pohybujícím se uvnitř větší kružnice. Změnou
/// vnitřního poloměru a vzdálenosti pera získáš výrazně odlišné vzory.
///
/// **Tvoje mise:** Výpočet křivky zahazuje část poměru poloměrů. Oprav radiusRatio tak, aby celé poloměry
/// mohly dát desetinný poměr, včetně výsledku menšího než jedna.
///
/// **Dnes trénuješ:** Typ expression (výrazu): Java vyhodnotí dělení před vrácením výsledku. Samotný
/// návratový typ double nezachrání celočíselné dělení; k úvaze o požadovaném zlomku použij například 40
/// děleno 80.
///
/// Použij Run File, šipkami vlevo/vpravo měň vnitřní poloměr a nahoru/dolů vzdálenost pera a porovnávej
/// překreslené křivky.
/// Po opravě: Save → Test File → zahraj si znovu.
double radiusRatio(int outer, int innerRadius) {
    return (outer - innerRadius) / innerRadius;
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `double radiusRatio(int outer, int innerRadius)` — Výsledek může být desetinný, přestože oba přijaté
///   poloměry jsou celá čísla.
/// - `outer - innerRadius` — Před výpočtem poměru odečti menší poloměr od většího.
/// - `(outer - innerRadius)` — Závorky ponechávají toto odčítání pohromadě jako čitatel.
/// - `/ innerRadius` — Dělení nyní pracuje se dvěma celočíselnými operand (operandy); právě zde se může
///   ztratit desetinná část.
/// - `return` — Předej hotový poměr do point, která jej používá ve skutečné křivce.
///
/// ### Co už obstarává připravená část
///
/// point převede poměr na pozice pera. period a gcd určí uzavření křivky; render spojí pozice čarami a
/// pozdější klávesnice a ukládání dodávají změny nastavení i export PNG.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Návratový typ double nevrátí desetinnou část ztracenou dřívějším celočíselným dělením.
/// 2. Převeď jeden operand ještě před dělením.
/// 3. Použij (outer - innerRadius) / (double) innerRadius.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 440;

int inner, pen, colour;
BufferedImage canvas;
String message;
int gcd(int a, int b) {
    while (b != 0) {
        int next = a % b;
        a = b;
        b = next;
    }
    return a;
}

double period() {
    return 2 * Math.PI * inner / gcd(120, inner);
}

Point2D.Double point(double t) {
    double a = 120 - inner;
    return new Point2D.Double(a * Math.cos(t) + pen * Math.cos(radiusRatio(120, inner) * t), a * Math.sin(t) - pen * Math.sin(radiusRatio(120, inner) * t));
}

int samples() {
    return Math.min(14000, (int)(period() * 280) + 1);
}

void render() {
    canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    var g = canvas.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(Color.getHSBColor(colour / 8f, .75f, .75f));
    double scale = (HEIGHT - 65) / 2.0 / Math.max(1, 120 - inner + pen), period = period();
    var previous = point(0);
    int n = samples();
    for (int i = 1; i <= n; i++) {
        var p = point(period * i / n);
        g.draw(new Line2D.Double(WIDTH / 2 + previous.x * scale, HEIGHT / 2 + previous.y * scale, WIDTH / 2 + p.x * scale, HEIGHT / 2 + p.y * scale));
        previous = p;
    }
    g.dispose();
}

void restart() {
    inner = 45;
    pen = 65;
    colour = 0;
    message = "Change parameters to draw another closed curve";
    render();
}

void adjust(int r, int d) {
    inner = Math.max(15, Math.min(110, inner + r));
    pen = Math.max(0, Math.min(140, pen + d));
    render();
}

void save(File file) throws IOException {
    ImageIO.write(canvas, "png", file);
}

void save() {
    try {
        save(new File("spirograph.png"));
        message = "Saved spirograph.png";
    } catch (IOException e) {
        message = "Save failed: " + e.getMessage();
    }
}

void draw(Graphics2D g) {
    g.drawImage(canvas, 0, 0, null);
    g.setColor(Color.BLACK);
    g.drawString("Inner radius: " + inner + "  Pen distance: " + pen + "  " + message, 10, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_LEFT -> adjust(-5, 0);
            case KeyEvent.VK_RIGHT -> adjust(5, 0);
            case KeyEvent.VK_UP -> adjust(0, 5);
            case KeyEvent.VK_DOWN -> adjust(0, -5);
            case KeyEvent.VK_C -> {
                colour = (colour + 1) % 8;
                render();
            }
            case KeyEvent.VK_S -> save();
        }
    }
}

void showWindow() {
    var frame = new JFrame("Spirograph");
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
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Left/Right: inner radius   Up/Down: pen distance   C: colour   S: save PNG   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}

/// Připravené vstupní body: main, restart, draw, render, save, showWindow.
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
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
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
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
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
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

