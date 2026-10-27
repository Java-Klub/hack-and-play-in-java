// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L07.03 · TrailDuel
///
/// Podělte se o klávesnici a veďte dvě svítící stopy, dokud někomu nedojde bezpečné místo. Chyba řízení
/// za tebou nechá trvalou zeď.
///
/// **Tvoje mise:** Ovládání nahoru posílá stopu dolů. Oprav directionFor() a zachovej obě rozložení
/// kláves i zpracování nesouvisejících kláves.
///
/// **Dnes trénuješ:** Switch expressions (výrazy s výběrem větve): několik vstupních případů může sdílet
/// jednu vracenou hodnotu. Direction sdružuje vodorovnou a svislou změnu pro danou klávesu.
///
/// Spusť Run File a hrajte společně na jedné klávesnici, první člověk řídí pomocí W, A, S, D a druhý
/// šipkami.
/// Po opravě: Save → Test File → zahraj si znovu.
Direction directionFor(int code) {
    return switch (code) {
        case KeyEvent.VK_W, KeyEvent.VK_UP -> new Direction(0, 1);
        case KeyEvent.VK_S, KeyEvent.VK_DOWN -> new Direction(0, 1);
        case KeyEvent.VK_A, KeyEvent.VK_LEFT -> new Direction(-1, 0);
        case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> new Direction(1, 0);
        default -> null;
    };
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `case KeyEvent.VK_W, KeyEvent.VK_UP` — Dva key codes (kódy kláves) sdílejí jednu větev místo
///   zdvojení pravidla.
/// - `new Direction(0, 1)` — Vytvoří vodorovný a svislý posun; souřadnice y na obrazovce roste dolů.
/// - `default -> null` — Nesouvisející klávesa nevytvoří žádný požadavek směru.
/// - `return switch (code)` — Převede kód klávesy na jednu hodnotu pro připravenou reakci na vstup.
///
/// ### Co už obstarává připravená část
///
/// key() předá vrácený Direction do turn(), které brání obratu zpět a odloží jednu zatáčku. step()
/// prověří oba hráče na stejné desce před prodloužením kterékoli stopy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Obrazovkové y roste dolů, takže nahoru má dy=-1.
/// 2. Sdružené case labels mohou dvěma klávesám vracet stejný record.
/// 3. Pro W i nahoru vrať Direction(0,-1); default zůstává null pro jiné klávesy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 600, HEIGHT = 430;

int[][] trail;
int ax, ay, bx, by, adx, ady, bdx, bdy, anx, any, bnx, bny, result;
boolean started, aTurn, bTurn;
/// restart(): Obnovuje počáteční model Trail Duel pro další pokus.
void restart() {
    trail = new int[20][30];
    ax = 5;
    ay = 10;
    bx = 24;
    by = 10;
    adx = anx = 1;
    ady = any = 0;
    bdx = bnx = -1;
    bdy = bny = 0;
    trail[ay][ax] = 1;
    trail[by][bx] = 2;
    result = 0;
    started = aTurn = bTurn = false;
}

boolean blocked(int x, int y) {
    return x < 0 || y < 0 || x >= 30 || y >= 20 || trail[y][x] != 0;
}

/// turn(): Připraví vybranému hráči nejvýše jednu změnu směru bez obrácení.
void turn(boolean first, int dx, int dy) {
    if (result != 0) {
        return;
    }
    if (first && !aTurn && dx != -adx && dy != -ady) {
        anx = dx;
        any = dy;
        aTurn = true;
    }
    if (!first && !bTurn && dx != -bdx && dy != -bdy) {
        bnx = dx;
        bny = dy;
        bTurn = true;
    }
    started = true;
}

/// step(): Dodaná aktualizace porovná oba cíle se stejnou deskou stop před přidáním obou hlav.
void step() {
    if (!started || result != 0) {
        return;
    }
    adx = anx;
    ady = any;
    bdx = bnx;
    bdy = bny;
    aTurn = bTurn = false;
    int xa = ax + adx, ya = ay + ady, xb = bx + bdx, yb = by + bdy;
    boolean deadA = blocked(xa, ya), deadB = blocked(xb, yb);
    if (xa == xb && ya == yb) {
        deadA = deadB = true;
    }
    if (deadA || deadB) {
        result = deadA && deadB ? 3 : deadA ? 2 : 1;
        return;
    }
    ax = xa;
    ay = ya;
    bx = xb;
    by = yb;
    trail[ay][ax] = 1;
    trail[by][bx] = 2;
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(Graphics2D g) {
    g.setColor(new Color(18, 23, 35));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    for (int y = 0; y < 20; y++) {
        for (int x = 0; x < 30; x++) {
            if (trail[y][x] != 0) {
                g.setColor(trail[y][x] == 1 ? new Color(40, 200, 215) : new Color(255, 145, 65));
                g.fillRect(x * 20 + 1, y * 20 + 1, 18, 18);
            }
        }
    }
    g.setColor(Color.WHITE);
    g.drawRect(ax * 20, ay * 20, 19, 19);
    g.drawRect(bx * 20, by * 20, 19, 19);
    g.drawString(result == 3 ? "TIE - both crashed" : result == 1 ? "CYAN WINS" : result == 2 ? "ORANGE WINS" : !started ? "Press a direction to start" : "Avoid every trail, including your own", 12, 418);
}

void key(int code, boolean pressed) {
    if (!pressed) {
        return;
    }
    if (code == KeyEvent.VK_R) {
        restart();
        return;
    }
    var direction = directionFor(code);
    if (direction != null) {
        boolean first = code == KeyEvent.VK_W || code == KeyEvent.VK_A || code == KeyEvent.VK_S || code == KeyEvent.VK_D;
        turn(first, direction.dx(), direction.dy());
    }
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; časovače končí při
/// zavření okna.
void showWindow() {
    var frame = new JFrame("Trail Duel");
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
    var timer = new Timer(100, e -> {
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
    var help = new JLabel("<html><div style=\"text-align:center\">Cyan: W A S D   Orange: arrows   R: restart</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

/// main(): Spouští model a potom plánuje dodané okno na Swing event thread.
void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}

record Direction(int dx, int dy) {
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
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
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
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

