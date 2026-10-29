// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # 05.18 · MiniGolf
///
/// Odpal míček přes malé hřiště, využij odrazy od stěn a zkus pomalu zajet do jamky. Delší natažení má
/// dát silnější úder, zatímco tření míček postupně zastaví.
///
/// **Tvoje mise · Sestav a zapoj úder:** Míček nemá sílu úderu. Doplň dragVelocity (100 → 8, -25 → -2, 0
/// → 0), potom oba
/// input (vstupy) applyPull zapoj do tohoto výpočtu: horizontal má dodat vx a vertical má dodat vy.
/// Připravené omezení síly zachovej.
///
/// **Dnes trénuješ:** Číselné měřítko a jednotky: vzdálenost se musí převést na rychlost ve správném
/// poměru. Než posoudíš celou pohyblivou scénu, ověř malý kladný příklad, záporný příklad a nulu.
///
/// Spusť program, natáhni myš dozadu od stojícího míčku a pusť ji pro odpal; před dalším úderem
/// počkej na zastavení nebo stiskni R pro nový začátek.
double dragVelocity(double distance) {
    return 0;
}

void applyPull(double horizontal, double vertical) {
    vx = 0;
    vy = 0;
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
/// ## Dva výpočty, jedno pravidlo
///
/// `double distance` přijme jednu vzdálenost se znaménkem; `return` vrátí převedenou rychlost.
/// `applyPull(horizontal, vertical)` přijme dvě různé hodnoty. Jeho dvě assignment (přiřazení) musí
/// zachovat role: horizontal → vx a vertical → vy. shoot už volá applyPull při puštění myši, potom
/// omezí přílišnou rychlost a započítá úder. Sestavuješ převod a jeho zapojení, nikoli timer
/// (časovač) nebo ovládání myši.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// distance už má type (typ) double. Porovnej distance / 125 a distance / 125.0: oba výpočty
/// zachovávají desetinnou část a oba mají stejné měřítko. Požadovaných osm jednotek rychlosti ze sta
/// pixelů vyžaduje jiné měřítko. Potom musí applyPull výsledek skutečně uložit do vx a vy. Připravený
/// shoot už omezuje extrémní sílu; zde další omezení nepřidávej.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Průchod hodnotami · zkus předpovědět">
/// Požadovaný převod je vzdálenost 100 → rychlost 8, vzdálenost -25 → rychlost -2 a nula → nula.
/// Protože distance má type double, změna 125 na 125.0 by dřívější měřítko nezměnila. applyPull
/// dostává zvlášť vodorovnou a svislou vzdálenost. Call (volání) může vrátit hodnotu přímo do
/// assignment (přiřazení), například `vx = dragVelocity(horizontal);`. Nic se nevypisuje.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Změnou samotného měřítka převodu vytvoř jemné a přehnané hřiště. V obou zkus stejný tah. Najdi
/// nastavení, ve kterém kamarád dosáhne jamky bez odrazu od stěny; pro původní kontrolu vrať původní
/// měřítko.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Odvoď rychlost pro jeden pixel a stejné měřítko použij pro oba směry.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// applyPull potřebuje dvě samostatná volání, každé se svým odpovídajícím parameter (parametrem).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Z dragVelocity vrať distance / 12.5. V applyPull přiřaď dragVelocity(horizontal) do vx a
/// dragVelocity(vertical) do vy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 600, HEIGHT = 420;

double x, y, vx, vy, aimX, aimY;
boolean aiming, won;
int strokes;
void restart() {
    x = 90;
    y = 110;
    vx = vy = 0;
    strokes = 0;
    aiming = won = false;
}

boolean resting() {
    return Math.hypot(vx, vy)<.05;
}

void begin(int xx, int yy) {
    if (!won && resting() && Math.hypot(xx - x, yy - y)<22) {
        aiming = true;
        aimX = xx;
        aimY = yy;
    }
}

void shoot(int xx, int yy) {
    if (!aiming) {
        return;
    }
    aiming = false;
    applyPull(x - xx, y - yy);
    double speed = Math.hypot(vx, vy);
    if (speed > 10) {
        vx *= 10 / speed;
        vy *= 10 / speed;
    }
    if (speed>.1) {
        strokes++;
    }
}

void step() {
    if (won || resting()) {
        return;
    }
    x += vx;
    y += vy;
    vx *= .985;
    vy *= .985;
    if (x < 28) {
        x = 28;
        vx = Math.abs(vx) * .8;
    }
    if (x > 572) {
        x = 572;
        vx = -Math.abs(vx) * .8;
    }
    if (y < 28) {
        y = 28;
        vy = Math.abs(vy) * .8;
    }
    if (y > 372) {
        y = 372;
        vy = -Math.abs(vy) * .8;
    }
    if (Math.hypot(x - 500, y - 310)<12 && Math.hypot(vx, vy)<1.5) {
        x = 500;
        y = 310;
        vx = vy = 0;
        won = true;
    }
    if (resting()) {
        vx = vy = 0;
    }
}

void draw(Graphics2D g) {
    g.setColor(new Color(40, 120, 70));
    g.fillRect(20, 20, 560, 360);
    g.setStroke(new BasicStroke(5));
    g.setColor(new Color(90, 60, 40));
    g.drawRect(20, 20, 560, 360);
    g.setColor(Color.BLACK);
    g.fillOval(486, 296, 28, 28);
    g.setColor(Color.YELLOW);
    g.drawLine(500, 310, 500, 270);
    g.fillPolygon(new int[]{500, 520, 500}, new int[]{270, 280, 290}, 3);
    g.setColor(Color.WHITE);
    g.fillOval((int)x - 8, (int)y - 8, 16, 16);
    if (aiming) {
        g.setStroke(new BasicStroke(2));
        g.drawLine((int)x, (int)y, (int)aimX, (int)aimY);
    }
    g.setColor(Color.BLACK);
    g.drawString(won ? "Holed in " + strokes + " strokes!" : "Strokes: " + strokes + " - enter the hole slowly", 20, 408);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    begin(x, y);
}

void drag(int x, int y) {
    if (aiming) {
        aimX = x;
        aimY = y;
    }
}

void release(int x, int y) {
    shoot(x, y);
}

void showWindow() {
    var frame = new JFrame("Mini Golf");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Drag backward from the ball; release: putt   R: restart</div></html>", SwingConstants.CENTER);
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
