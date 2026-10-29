// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
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
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L02/M04_Spirograph.java`
/// - Kontrola této mise: `java L02/Check.java M04_Spirograph.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše, šipkami vlevo/vpravo měň vnitřní poloměr a
/// nahoru/dolů vzdálenost pera a porovnávej překreslené křivky. Po opravě: ulož soubor, spusť
/// uvedenou kontrolu a zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F04 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
double radiusRatio(int outer, int innerRadius) {
    return (outer - innerRadius) / (double) innerRadius;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F04">
/// Arithmetic (aritmetika) používá operand type (typy operandů) v okamžiku provedení operace. Jsou-li
/// oba operand u `/` celočíselné, Java zahodí desetinnou část. Pozdější uložení výsledku do `double`
/// ji nevrátí.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// int pages = 7;
/// double first = pages / 2;
/// double second = pages / 2.0;
/// double third = (double) pages / 2;
/// ```
///
/// Předpověz tři hodnoty: `3.0`, `3.5`, `3.5`. Cast (převod) `(double)` převede toto použití `pages`
/// před dělením; nezmění deklarovaný type (typ) proměnné. Závorky seskupují výpočty, `*` násobí a `/`
/// dělí. Kromě kladného input (vstupu) ověř i záporný a nulu. Pojmenuj jednotky: vzdálenost a
/// rychlost jsou různé veličiny, i když obě používají `double`. O výsledku rozhoduje celý expression
/// (výraz), nikoli jen type napsaný před jménem method (metody).
///
/// Nyní předpověz všechny tři výsledky, pokud pages obsahuje devítku.
// </editor-fold>

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
/// point převede poměr na pozice pera. period a gcd určí uzavření křivky; render spojí pozice
/// čarami a připravená klávesnice mění dva parametry tvaru.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// Poloměry přicházejí jako int parameter (celočíselné parametry), takže výraz tvořený jen těmito
/// hodnotami může používat integer division (celočíselné dělení) i uvnitř method (metody) vracející
/// double. Cast (převod) se vztahuje na hodnotu bezprostředně za ním. Neutrální příklad: se sedmi
/// seats a čtyřmi rows dá `(double) seats / rows` hodnotu 1.75, zatímco `(double) (seats / rows)`
/// pouze 1.0, protože desetinná část už zmizela. Cast nemění declaration (deklaraci) parametru. Nyní
/// předpověz oba výrazy pro pět seats a dvě rows a potom rozhodni, kam převod patří ve tvém výpočtu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Návratový typ double nevrátí desetinnou část ztracenou dřívějším celočíselným dělením.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Převeď jeden operand ještě před dělením.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Použij (outer - innerRadius) / (double) innerRadius.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 440;

int inner, pen;
BufferedImage canvas;
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
    g.setColor(Color.getHSBColor(0, .75f, .75f));
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
    render();
}

void adjust(int r, int d) {
    inner = Math.max(15, Math.min(110, inner + r));
    pen = Math.max(0, Math.min(140, pen + d));
    render();
}

void draw(Graphics2D g) {
    g.drawImage(canvas, 0, 0, null);
    g.setColor(Color.BLACK);
    g.drawString("Inner radius: " + inner + "  Pen distance: " + pen, 10, 22);
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
    var help = new JLabel("<html><div style=\"text-align:center\">Left/Right: inner radius   Up/Down: pen distance   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

void main() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("This game needs a graphical display; model checks still work headlessly.");
        return;
    }
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}

/// Připravené vstupní body: main, restart, draw, render, showWindow.
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
/// - `null` — Žádný object (objekt) tu není.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

