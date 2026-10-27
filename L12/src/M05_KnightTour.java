// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
// </editor-fold>

/// # L12.05 · KnightTour
///
/// Projeď malou desku šachovým jezdcem a na každé políčko vstup nejvýše jednou. Platný tah má dojít na
/// místo kliknutí, ne na jeho odraz přes úhlopříčku.
///
/// **Tvoje mise:** Příkazy Move prohazují přenášené souřadnice. Oprav směrování příkazu a zachovej
/// odmítnuté pozice, pravidlo jezdce i Restart.
///
/// **Dnes trénuješ:** Sealed family (uzavřená rodina) popisuje povolené alternativy příkazu. Record
/// pattern (vzor record) rozbalí component (složky) Move; exhaustive switch (úplný switch) vybere
/// pravidlo a guard (podmínka) oddělí použitelné souřadnice od odmítnutých.
///
/// Po Run File klikni na zelený tah jezdce, například na sloupec 1 a řádek 2 při číslování od nuly, a
/// klávesou R začni cestu znovu.
/// Po opravě: Save → Test File → zahraj si znovu.
boolean apply(Command command) {
    return switch (command) {
        case Move(var xx, var yy) when xx >= 0 && yy >= 0 && xx < 5 && yy < 5 -> move(xx, yy);
        case Move(var xx, var yy) -> false;
        case Restart() -> { restart(); yield true; }
    };
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `case Move(var xx, var yy)` — Rozbalí record v pořadí component (složek) a pojmenuje jeho dvě
///   přenášené souřadnice.
/// - `when xx >= 0` — Začíná guard (podmínku), která zpřesní Move case (větev) před spuštěním akce.
/// - `move(yy, xx)` — Předá rozpoznané hodnoty obráceně; obě pořadí mohou náhodou být platné tahy jezdce.
/// - `yield true` — Vrátí hodnotu z bloku patřícího do tohoto switch expression (výrazu switch).
///
/// ### Co už obstarává připravená část
///
/// Obsluha myši vytvoří Move a klávesa R vytvoří Restart. Apply je přesměruje; připravený move ověří
/// vzdálenost jezdce a navštívená políčka, kreslení zvýrazní platné možnosti.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Obě var jména označují component (složky), nikoli preview pattern (předběžný vzor) pro primitivní
///   typ.
/// 2. Move(1,2) musí dojít na x=1,y=2, i když je obrácený tah jezdce také platný.
/// 3. Zachovej úplné zpracování alternativ; chybná coordinate (souřadnice) vrátí false před pohybem.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 520, HEIGHT = 420;

int[][] visited;
int x, y, count;
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
void restart() {
    visited = new int[5][5];
    x = y = 0;
    count = 1;
    visited[0][0] = 1;
}

boolean legal(int xx, int yy) {
    return xx >= 0 && yy >= 0 && xx < 5 && yy < 5 && visited[yy][xx] == 0 && Math.abs(xx - x) * Math.abs(yy - y) == 2;
}

boolean move(int xx, int yy) {
    if (!legal(xx, yy)) {
        return false;
    }
    x = xx;
    y = yy;
    visited[y][x] = ++count;
    return true;
}

boolean won() {
    return count == 25;
}

/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
void draw(Graphics2D g) {
    for (int yy = 0; yy < 5; yy++) {
        for (int xx = 0; xx < 5; xx++) {
            int px = 100 + xx * 60, py = 35 + yy * 60;
            g.setColor((xx + yy) % 2 == 0 ? new Color(235, 225, 205) : new Color(135, 155, 145));
            g.fillRect(px, py, 60, 60);
            if (legal(xx, yy)) {
                g.setColor(new Color(75, 190, 120));
                g.fillOval(px + 22, py + 22, 16, 16);
            }
            if (visited[yy][xx]>0) {
                g.setColor(Color.DARK_GRAY);
                g.drawString("" + visited[yy][xx], px + 23, py + 35);
            }
        }
    }
    g.setColor(new Color(65, 75, 200));
    g.drawOval(105 + x * 60, 40 + y * 60, 50, 50);
    g.setColor(Color.BLACK);
    g.drawString(won() ? "All 25 squares visited!" : "Visited " + count + " / 25. Green dots are legal next moves.", 20, 380);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        apply(new Restart());
    }
}

void press(int x, int y, int button) {
    if (x >= 100 && y >= 35 && x < 400 && y < 335) {
        apply(new Move((x - 100) / 60, (y - 35) / 60));
    }
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Knight Tour");
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
    };
    panel.addMouseListener(mouse);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Click a green legal move   Visit every square once   R: restart</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

sealed interface Command permits Move, Restart {}
record Move(int x, int y) implements Command {}
record Restart() implements Command {}

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Press vytváří Move; key vytváří Restart; apply zpracuje uzavřenou rodinu.
/// Move ověřuje vzdálenost jezdce a navštívené cell (buňky); draw a Swing input (vstup) jsou připravené.
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
/// - `implements` — Říká, že record nebo class (třída) plní roli uvedeného interface (rozhraní).
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `interface` — Popisuje role (roli), kterou mohou plnit různé implementation (implementace). Volající
///   používá roli bez znalosti jejích vnitřností.
/// - `sealed` — Omezuje data type (datový typ) na výslovně uvedené alternativy.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

