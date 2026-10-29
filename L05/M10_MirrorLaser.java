// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
// </editor-fold>

/// # L05.10 · MirrorLaser
///
/// Otáčej zrcadla a doveď laser k přijímači. Jediný špatný krok způsobí, že paprsek zdánlivě kráčí bokem.
///
/// **Tvoje mise:** Paprsek zaměňuje vodorovný a svislý pohyb. Oprav RayState.advance(), aby krok
/// odpovídal směru uloženému v paprsku.
///
/// **Dnes trénuješ:** Records (datové záznamy): spoj související hodnoty do přehledného stavu. Pohyb
/// vytvoří novou hodnotu paprsku a původní poloha i směr zůstanou k dispozici.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L05/M10_MirrorLaser.java`
/// - Kontrola této mise: `java L05/Check.java M10_MirrorLaser.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše, kliknutím otoč zrcadlo a mezerníkem nech paprsek
/// projít tvým uspořádáním. Po opravě: ulož soubor, spusť uvedenou kontrolu a zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F19 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
record RayState(int x, int y, int dx, int dy) {
    RayState advance() {
        return new RayState(x + dx, y + dy, dx, dy);
    }
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F19">
/// ### Records: jedna pojmenovaná hodnota
///
/// Record sdružuje související component (složky). Constructor (konstruktor) je přijímá v pořadí
/// declaration (deklarace); accessor (přístupová metoda) jako `ink()` je čte. Vlastní method (metody)
/// uvnitř record mohou používat přímo jména složek. Vytvoření nové hodnoty původní složky nezmění.
/// Připravené `equals` porovnává jejich hodnoty.
///
/// ```java
/// record Frame(String ink, int dots) {
///     Frame recoloured(String nextInk) {
///         return new Frame(nextInk, dots);
///     }
/// }
/// ```
///
/// Volání `recoloured("gold")` na `new Frame("violet", 6)` vrátí gold a šest; původní hodnota zůstane
/// violet a šest. Rovná se další přebarvení zpět na violet původní hodnotě? Jména vysvětlují, co dvě
/// jinak anonymní hodnoty znamenají.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `record RayState(int x, int y, int dx, int dy)` — Pojmenovává čtyři components (složky) jednoho
///   stavu paprsku.
/// - `RayState advance()` — Tato method (metoda) vrací další stav paprsku místo prázdného výsledku.
/// - `new RayState(` — Vytvoří samostatnou hodnotu record (záznamu) z předaných složek.
/// - `dx, dy` — Tyto dvě složky přenášejí vodorovný a svislý směr do dalšího stavu.
///
/// ### Co už obstarává připravená část
///
/// traceFrom() odráží směry na zrcadlech, pamatuje navštívené stavy kvůli zacyklení a mezi políčky volá
/// advance(). Připravené kreslení zobrazuje výslednou cestu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Předpověz hodnotu, ne pohyb objektu
///
/// Před úpravou napiš čtyři component (složky) paprsku v `(7,3)` se směrem `(-1,0)`. Předpověz je po
/// jediném advance: které popisují novou pozici a které stále směr? Pořadí constructor argument
/// (argumentů konstruktoru) sleduje declaration (deklaraci) record. Potom vysvětli, proč jiná
/// variable (proměnná) odkazující na předchozí RayState musí zachovat původní složky. Popis další
/// hodnoty nepotřebuje field mutation (změnu položky objektu).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Record spojuje pozici a směr do jedné pojmenované hodnoty.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// advance() vrací novou hodnotu; tento record nemění.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Do konstruktoru RayState předej v pořadí x+dx, y+dy, dx, dy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 480, HEIGHT = 420;

int[][] mirrors;
ArrayList<Point> beam = new ArrayList<>();
String result;
/// restart(): Obnovuje počáteční model Mirror Laser pro další pokus.
void restart() {
    mirrors = new int[6][6];
    mirrors[2][2] = 2;
    mirrors[0][2] = 2;
    mirrors[0][5] = 1;
    beam.clear();
    result = "Rotate the three mirrors, then trace";
}

/// traceFrom(): Ukládá navštívené pozice se směrem, odráží na zrcadlech a tvoří viditelný paprsek.
String traceFrom(int x, int y, int dx, int dy) {
    var seen = new HashSet<RayState>();
    beam.clear();
    var ray = new RayState(x, y, dx, dy);
    while (ray.x() >= 0 && ray.y() >= 0 && ray.x() < 6 && ray.y() < 6) {
        if (!seen.add(ray)) {
            return "Loop detected";
        }
        beam.add(new Point(ray.x(), ray.y()));
        if (ray.x() == 5 && ray.y() == 5) {
            return "Target reached!";
        }
        int mirror = mirrors[ray.y()][ray.x()];
        int nextDx = ray.dx(), nextDy = ray.dy();
        if (mirror == 1) {
            nextDx = -ray.dy();
            nextDy = -ray.dx();
        }
        if (mirror == 2) {
            nextDx = ray.dy();
            nextDy = ray.dx();
        }
        ray = new RayState(ray.x(), ray.y(), nextDx, nextDy).advance();
    }
    beam.add(new Point(ray.x(), ray.y()));
    return "Beam escaped - try another rotation";
}

void trace() {
    result = traceFrom(0, 2, 1, 0);
}

void rotate(int x, int y) {
    if (x >= 0 && x < 6 && y >= 0 && y < 6 && mirrors[y][x] != 0) {
        mirrors[y][x] = 3 - mirrors[y][x];
        beam.clear();
        result = "Mirror rotated";
    }
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(Graphics2D g) {
    g.setColor(Color.LIGHT_GRAY);
    for (int i = 0; i <= 6; i++) {
        g.drawLine(70 + i * 52, 35, 70 + i * 52, 347);
        g.drawLine(70, 35 + i * 52, 382, 35 + i * 52);
    }
    g.setStroke(new BasicStroke(4));
    for (int y = 0; y < 6; y++) {
        for (int x = 0; x < 6; x++) {
            if (mirrors[y][x] != 0) {
                g.setColor(Color.BLUE);
                int xx = 70 + x * 52, yy = 35 + y * 52;
                if (mirrors[y][x] == 1) {
                    g.drawLine(xx + 8, yy + 44, xx + 44, yy + 8);
                }
                else {
                    g.drawLine(xx + 8, yy + 8, xx + 44, yy + 44);
                }
            }
        }
    }
    g.setColor(Color.RED);
    g.drawString("SOURCE >", 4, 171);
    g.setColor(new Color(40, 170, 80));
    g.fillOval(70 + 5 * 52 + 15, 35 + 5 * 52 + 15, 22, 22);
    g.setColor(Color.ORANGE);
    g.setStroke(new BasicStroke(2));
    int xx = 44, yy = 165;
    for (var p : beam) {
        int nx = 96 + p.x * 52, ny = 61 + p.y * 52;
        g.drawLine(xx, yy, nx, ny);
        xx = nx;
        yy = ny;
    }
    g.setColor(Color.BLACK);
    g.drawString(result, 40, 390);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code == KeyEvent.VK_SPACE) {
        trace();
    }
}

void press(int x, int y, int button) {
    if (x >= 70 && y >= 35 && x < 382 && y < 347) {
        rotate((x - 70) / 52, (y - 35) / 52);
    }
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; zrušení okna
/// uzavírá toto zobrazení.
void showWindow() {
    var frame = new JFrame("Mirror Laser");
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
    };
    panel.addMouseListener(mouse);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Click mirrors: rotate   Space: trace beam   R: restart</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

/// main(): Spouští model a potom plánuje dodané okno na Swing event thread.
void main() {
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
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
/// - `/` — Dělí čísla. Dělení dvou int hodnot zahodí desetinnou část.
/// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
///   List<String> lomené závorky popisují element type (typ prvku).
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `public` — Zpřístupňuje tuto declaration (deklaraci) dalšímu kódu.
/// - `protected` — Visibility keyword (klíčové slovo přístupnosti) používané připraveným library callback
///   (knihovním zpětným voláním), například paintComponent.
/// - `super` — V super.paintComponent(g) požádá připravenou parent implementation (nadřazenou
///   implementaci), aby nejprve vykreslila běžné pozadí.
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

