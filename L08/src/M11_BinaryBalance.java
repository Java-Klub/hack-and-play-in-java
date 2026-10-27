// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
// </editor-fold>

/// # L08.11 · BinaryBalance
///
/// Vyplň malou desku vyváženými černými a bílými políčky bez trojice za sebou. Oranžové nápovědy mají
/// zůstat pevné, tvoje odpovědi měnitelné.
///
/// **Tvoje mise:** Volné políčko se po úpravě uzamkne, protože odpovědi mění i předlohu nápověd. Oprav
/// copyBoard(), aby se oddělily obě úrovně mřížky.
///
/// **Dnes trénuješ:** Copies and aliasing (kopie a sdílené odkazy): int[][] ukládá odkazy na řádková
/// pole. Kopie jen vnějšího pole nechá měnitelné řádky společné, proto nezávislá deska potřebuje
/// nezávislé řádky.
///
/// Spusť Run File a opakovaným klikáním na neoranžové políčko přepínej neznámou, černou a bílou podle
/// pravidel vyvážení.
/// Po opravě: Save → Test File → zahraj si znovu.
int[][] copyBoard(int[][] original) {
    int[][] copy = new int[original.length][];
    for (int row = 0; row < original.length; row++) {
        copy[row] = original[row].clone();
    }
    return copy;
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int[][]` — Vnější array (pole) obsahuje reference (odkazy) na řádky; každý řádek obsahuje hodnoty
///   políček.
/// - `original` — Dodaná mřížka nápověd je vstup copyBoard(), nikoli deska k přímému přepisování.
/// - `.clone()` — Kopíruje jednu úroveň pole; automaticky nekopíruje další pole, na která tato úroveň
///   odkazuje.
///
/// ### Co už obstarává připravená část
///
/// restart() vytváří cells z givens přes copyBoard(). cycle() chrání pozice s nenulovou nápovědou,
/// zatímco lineValid() a won() kontrolují vyvážení i opakované skupiny; tyto kontroly jsou připravené.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. int[][] je array referencí na řádky.
/// 2. original.clone() kopíruje reference řádků; buňky zůstávají sdílené.
/// 3. Vytvoř copy[original.length][] a před vrácením naklonuj každý original[row].
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 480, HEIGHT = 400;

int[][] givens = {{1, 0, 0, 2}, {0, 2, 0, 0}, {0, 0, 2, 0}, {2, 0, 0, 1}};
int[][] cells;
/// restart(): Obnovuje počáteční model Binary Balance pro další pokus.
void restart() {
    cells = copyBoard(givens);
}

void cycle(int x, int y) {
    if (x >= 0 && y >= 0 && x < 4 && y < 4 && givens[y][x] == 0) {
        cells[y][x] = (cells[y][x] + 1) % 3;
    }
}

/// lineValid(): Ověřuje přesné vyvážení černých/bílých a odmítá trojici stejné barvy.
boolean lineValid(int[] line) {
    int ones = 0;
    for (int i = 0; i < 4; i++) {
        if (line[i]<1 || line[i]>2) {
            return false;
        }
        if (line[i] == 1) {
            ones++;
        }
        if (i >= 2 && line[i] == line[i - 1] && line[i] == line[i - 2]) {
            return false;
        }
    }
    return ones == 2;
}

boolean won() {
    for (int y = 0; y < 4; y++) {
        if (!lineValid(cells[y])) {
            return false;
        }
        int[] column = new int[4];
        for (int x = 0; x < 4; x++) {
            column[x] = cells[x][y];
            if (givens[y][x] != 0 && cells[y][x] != givens[y][x]) {
                return false;
            }
        }
        if (!lineValid(column)) {
            return false;
        }
    }
    return true;
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(Graphics2D g) {
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            int xx = 90 + x * 65, yy = 45 + y * 65;
            g.setColor(new Color(205, 210, 220));
            g.fillRect(xx, yy, 62, 62);
            if (cells[y][x] != 0) {
                g.setColor(cells[y][x] == 1 ? Color.BLACK : Color.WHITE);
                g.fillOval(xx + 11, yy + 11, 40, 40);
            }
            if (givens[y][x] != 0) {
                g.setColor(new Color(225, 100, 45));
                g.drawRect(xx + 2, yy + 2, 58, 58);
            }
        }
    }
    g.setColor(Color.BLACK);
    g.drawString(won() ? "Balanced!" : "Two black and two white in every row and column", 20, 345);
    g.drawString("No three alike in a row. Orange borders are fixed.", 20, 370);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (x >= 90 && y >= 45 && x < 350 && y < 305) {
        cycle((x - 90) / 65, (y - 45) / 65);
    }
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; zrušení okna
/// uzavírá toto zobrazení.
void showWindow() {
    var frame = new JFrame("Binary Balance");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click: unknown / black / white   Orange cells are fixed   R: reset</div></html>", SwingConstants.CENTER);
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
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

