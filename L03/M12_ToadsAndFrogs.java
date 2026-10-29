// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
// </editor-fold>

/// # L03.12 · ToadsAndFrogs
///
/// Vyměň dvě skupiny tvorů přes jedinou mezeru. Ropuchy jdou doprava, žáby doleva a skok smí překonat
/// jednoho soupeře, takže jediný nepromyšlený krok může celou hádanku zablokovat.
///
/// **Tvoje mise · Slož pravidlo pohybu:** Napiš dvě propojená pravidla. direction vrátí +1 pro ropuchu 1
/// a -1 pro žábu 2.
/// destination má toto pravidlo zavolat a spočítat výchozí pozici plus vzdálenost se znaménkem.
/// Připravené move se ptá na krok o jedno i skok o dvě pole; uvnitř výpočtů tvory neposunuj.
///
/// **Dnes trénuješ:** Vrať užitečnou hodnotu: pravidlo směru popisuje krok, aniž samo cokoli přesouvá.
/// Volající část může vrácený směr použít pro jediný krok i delší skok.
///
/// Spusť program a stiskni 5, abys požádal žábu vedle počáteční mezery o krok doleva; R obnoví
/// výchozí uspořádání.
int direction(int piece) {
    if (piece == 1) {
        return 1;
    } else {
        return -1;
    }
}

int destination(int origin, int distance, int piece) {
    int step = direction(piece);
    return origin + distance * step;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F13">
/// Method (metoda) zabalí operaci pod jediné jméno. Parameter (parametry) jsou místní jména informací
/// přijatých při každém call (volání); argument (argumenty) jsou skutečně předané hodnoty. `return`
/// předá jediný výsledek zpět volající části.
///
/// ```java
/// int remaining(int capacity, int used) {
///     return capacity - used;
/// }
///
/// void main() {
///     IO.println(remaining(12, 5));
///     IO.println(remaining(9, 4));
/// }
/// ```
///
/// Předpověz obě vypsané hodnoty: sedmičku a pětku. Čárka odděluje parameter či argument a jejich
/// pořadí rozhoduje, kterou hodnotu jméno dostane. Jména popisují role; informace si sama
/// nezískávají. Tato method počítá bez vypisování a změny připraveného state (stavu programu). Při
/// opravě výpočtu vyzkoušej druhou dvojici argument, aby se za jediný případ nemohla schovat náhodná
/// pevná odpověď. Method se provede až při skutečném call, nikoli samotným zapsáním definice.
///
/// Nyní předpověz remaining(10, 6).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Malý řetězec, který je tvůj
///
/// move → destination → direction → posun se znaménkem → kandidátní pole. Čárka odděluje tři role
/// parameter (parametrů); pořadí argument (skutečných vstupů) jim musí odpovídat. destination nezná
/// desku a lze zkusit s libovolným origin. Připravené move řeší prázdná pole, skákání přes soupeře,
/// hranice a skutečné zápisy. Kandidát mimo desku se odmítne až tam.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Průchod hodnotami · zkus předpovědět">
/// Helper (pomocná metoda) může volat další helper. destination dostane výchozí pozici, nezápornou
/// vzdálenost a druh tvora. direction odpoví jen znaménkem; násobení vzdáleností vytvoří posun se
/// znaménkem, který se přičte k výchozí pozici. Jiný chodec z 5 doleva o 2 skončí na 3. Vrácené číslo
/// je jen kandidát; move ověří povolené přistání.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Po vyřešení standardní výměny změň jen počáteční rozmístění v restart a vytvoř kratší hádanku na
/// stejných sedmi polích. Dej ji kamarádovi s navrženým limitem tahů. Než výzvu zadáš, ověř vlastní
/// řešení.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Odděl druh tvora, vzdálenost a současnou pozici jako tři různé údaje.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Vrácený směr použij jako násobitel a posun se znaménkem přičti k origin.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// direction vrací 1 nebo -1. V destination použij int step = direction(piece); potom return origin +
/// distance * step;.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 600, HEIGHT = 330;

int[] cells;
int moves;
void restart() {
    cells = new int[]{1, 1, 1, 0, 2, 2, 2};
    moves = 0;
}

boolean move(int index) {
    if (index < 0 || index >= 7 || cells[index] == 0 || won()) {
        return false;
    }
    int piece = cells[index];
    int step = destination(index, 1, piece);
    int jump = destination(index, 2, piece);
    int destination = -1;
    if (step >= 0 && step < 7 && cells[step] == 0) {
        destination = step;
    }
    else {
        if (jump >= 0 && jump < 7 && cells[step] == 3 - piece && cells[jump] == 0) {
            destination = jump;
        }
    }
    if (destination < 0) {
        return false;
    }
    cells[destination] = piece;
    cells[index] = 0;
    moves++;
    return true;
}

boolean won() {
    return Arrays.equals(cells, new int[]{2, 2, 2, 0, 1, 1, 1});
}

void draw(Graphics2D g) {
    for (int i = 0; i < 7; i++) {
        int x = 35 + i * 75;
        g.setColor(new Color(220, 235, 205));
        g.fillOval(x, 170, 65, 35);
        if (cells[i] != 0) {
            g.setColor(cells[i] == 1 ? new Color(60, 150, 100) : new Color(80, 130, 210));
            g.fillOval(x + 9, 130, 46, 46);
            g.setColor(Color.WHITE);
            g.drawString(cells[i] == 1 ? ">" : "<", x + 27, 158);
        }
        g.setColor(Color.BLACK);
        g.drawString("" + (i + 1), x + 29, 235);
    }
    g.setColor(Color.BLACK);
    g.drawString(won() ? "Groups swapped in " + moves + " moves!" : "Swap both groups. Step into the gap, or jump one opponent.", 20, 60);
    g.drawString("Forward only. Stuck? R starts again.", 20, 285);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code >= KeyEvent.VK_1 && code <= KeyEvent.VK_7) {
        move(code - KeyEvent.VK_1);
    }
}

void press(int x, int y, int button) {
    if (x >= 35 && x < 560 && y >= 125 && y <= 210) {
        move((x - 35) / 75);
    }
}

void showWindow() {
    var frame = new JFrame("Toads and Frogs");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click a piece or press 1-7   Toads &gt; right, frogs &lt; left   R: restart</div></html>", SwingConstants.CENTER);
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
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
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
