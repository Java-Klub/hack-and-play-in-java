// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
// </editor-fold>

/// # L05.12 · InequalityLatin
///
/// Vyplň latinský čtverec a dodrž přitom malá znaménka nerovnosti mezi políčky. I správný řádek může
/// zkazit jediné drobné porovnání.
///
/// **Tvoje mise:** Zobrazená nápověda „menší než“ přijímá opačný vztah. Oprav Rule.satisfiedBy() a využij
/// polohu uloženou v každém Rule.
///
/// **Dnes trénuješ:** Records (datové záznamy): pravidlo může nést svá data i dotaz nad nimi. Components
/// (složky) x a y vybírají sousední čísla k porovnání.
///
/// Spusť Run File a opakovaným klikáním na volné políčko měň jeho číslo při sledování okolních znamének
/// nerovnosti.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F19 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
record Rule(int x, int y) {
    boolean satisfiedBy(int[][] values) {
        return values[y][x] > values[y][x + 1];
    }
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F19">
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
/// - `record Rule(int x, int y)` — Ukládá sloupec a řádek určující právě tuto nápovědu.
/// - `boolean satisfiedBy(int[][] values)` — Dotaz ano/ne, jehož argument (vstup) je celá číselná mřížka.
/// - `values[y][x]` — Nejprve vybírá řádek a potom sloupec uvnitř tohoto řádku.
/// - `x + 1` — Odkazuje na bezprostředně sousední sloupec vpravo.
///
/// ### Co už obstarává připravená část
///
/// won() zvlášť kontroluje jedinečnost čísel a potom se každého uloženého Rule ptá na aktuální desku.
/// Kreslení umísťuje nápovědu podle stejných souřadnic záznamu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// x a y patří tomuto Rule record, nikoli globálnímu kurzoru.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Řádek je values[y]; pravý soused má sloupec x+1.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Vrať values[y][x] < values[y][x+1]; rovnost musí dát false.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 500, HEIGHT = 400;

Rule[] rules = {new Rule(0, 0), new Rule(2, 0), new Rule(1, 1), new Rule(0, 2), new Rule(1, 3), new Rule(2, 3)};
int[][] givens = {{1, 0, 0, 0}, {0, 0, 0, 1}, {0, 4, 0, 0}, {0, 0, 2, 0}};
int[][] cells;
/// restart(): Obnovuje počáteční model Inequality Latin pro další pokus.
void restart() {
    cells = Arrays.stream(givens).map(int[]::clone).toArray(int[][]::new);
}

void cycle(int x, int y) {
    if (x >= 0 && y >= 0 && x < 4 && y < 4 && givens[y][x] == 0) {
        cells[y][x] = (cells[y][x] + 1) % 5;
    }
}

/// won(): Odmítá duplicity latinského čtverce a každou nerovnost předává jejímu Rule record.
boolean won() {
    for (int i = 0; i < 4; i++) {
        boolean[] row = new boolean[5], col = new boolean[5];
        for (int j = 0; j < 4; j++) {
            int a = cells[i][j], b = cells[j][i];
            if (a < 1 || a > 4 || b < 1 || b > 4 || row[a] || col[b]) {
                return false;
            }
            if (givens[i][j] != 0 && a != givens[i][j]) {
                return false;
            }
            row[a] = true;
            col[b] = true;
        }
    }
    for (var r : rules) {
        if (!r.satisfiedBy(cells)) {
            return false;
        }
    }
    return true;
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(Graphics2D g) {
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            int xx = 85 + x * 72, yy = 45 + y * 72;
            g.setColor(givens[y][x] != 0 ? new Color(215, 225, 245) : new Color(240, 240, 240));
            g.fillRect(xx, yy, 52, 52);
            g.setColor(Color.BLACK);
            if (cells[y][x] != 0) {
                g.drawString("" + cells[y][x], xx + 19, yy + 34);
            }
        }
    }
    for (var r : rules) {
        g.drawString("<", 140 + r.x * 72, 79 + r.y * 72);
    }
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    g.drawString(won() ? "Latin square complete!" : "Use 1-4 once in each row and column; obey every <", 20, 370);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (x >= 85 && y >= 45 && x < 373 && y < 333 && (x - 85) % 72 < 52 && (y - 45) % 72 < 52) {
        cycle((x - 85) / 72, (y - 45) / 72);
    }
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; zrušení okna
/// uzavírá toto zobrazení.
void showWindow() {
    var frame = new JFrame("Inequality Latin");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click a free cell: 1 / 2 / 3 / 4 / blank   R: reset</div></html>", SwingConstants.CENTER);
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

