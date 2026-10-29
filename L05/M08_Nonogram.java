// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
// </editor-fold>

/// # L05.08 · Nonogram
///
/// Odhal obrázek z políček jen podle číselných skupin vedle řádků a sloupců. Každá nápověda musí obrázek
/// popisovat poctivě.
///
/// **Tvoje mise:** Černá skupina na konci pole nemá bílou buňku, která ji uzavře. Doplň rozhodnutí za
/// průchodem: ulož zbývající kladnou sérii, ale nevytvářej nulovou nápovědu pro bílý či prázdný řádek.
/// Dřívější skupiny zachovej.
///
/// **Dnes trénuješ:** Traversal and search (průchod a hledání): při čtení řádku udržuj průběžný počet a
/// všimni si i skupiny ukončené koncem vstupu místo bílého políčka.
///
/// Podle čísel u řádků a sloupců označuj levým kliknutím černá a pravým bílá
/// políčka.
int[] runs(int[] values) {
    var result = new ArrayList<Integer>();
    int count = 0;
    for (int value : values) {
        if (value == 1) {
            count++;
        }
        else {
            if (count > 0) {
                result.add(count);
                count = 0;
            }
        }
    }
    if (count > 0) {
        result.add(count);
    }
    return result.stream().mapToInt(Integer::intValue).toArray();
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F17">
/// ### Traversal: vždy jedna otázka
///
/// Traversal (průchod) navštěvuje hodnoty; search (hledání) může skončit první shodou, count
/// (počítání) musí prohlédnout všechny relevantní hodnoty. Ve for-each loop (cyklu pro každý prvek)
/// dostane jméno před dvojtečkou jednu hodnotu ze sequence (posloupnosti) za ní. Průběžný počet založ
/// mimo opakované tělo, aby dřívější shody přežily.
///
/// ```java
/// int[] pulses = {2, 0, 4};
/// int strong = 0;
/// for (int pulse : pulses) {
///     if (pulse > 1) {
///         strong++;
///     }
/// }
/// IO.println(strong);
/// ```
///
/// Výsledek je dvě, protože pulzy dva a čtyři splňují condition (podmínku). Kdybys potřeboval pouze
/// vědět, zda existuje nějaký silný pulz, kdy už můžeš odpovědět? Jaký počet má mít prázdné array
/// (pole)?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Jak číst tento kód">
/// Připravený průchod uzavírá sérii při nule. Konec vstupu ale nemá další buňku, která by tuto větev
/// spustila.
///
/// | přečtený vstup | count | dokončený result |
/// | --- | --- | --- |
/// | 1,1 | 2 | prázdný |
/// | 1,1,0 | 0 | 2 |
/// | 1,1,0,1 | 1 | 2 |
///
/// Závěrečný stav ještě obsahuje užitečný count; result potřebuje 2 i 1. `result.add(count)` uloží
/// nalezenou délku. Rozhodni, kdy tento krok patří za smyčku. Předpověz závěr pro samé bílé buňky a
/// jednu černou. Převod stream je připravený; pouze vrací získané délky jako int array (pole).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Dokončení run (souvislé skupiny)
///
/// `result` je připravený rostoucí List (seznam). Jeho `add(count)` připojí délku jedné dokončené run
/// (souvislé skupiny); nenahrazuje dřívější délky. Závěrečný stream conversion (převod proudu) jen
/// vrátí zaznamenané délky jako int[]. Soustřeď se na okamžik dokončení skupiny. Porovnej řádek
/// končící bíle s řádkem končícím černě: co ukončí poslední skupinu? Prázdný či čistě bílý řádek
/// nesmí vymyslet černou skupinu nulové délky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Porovnej závěrečný count pro černý a bílý konec řádku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Poslední skupina potřebuje připojení za průchodem, chráněné proti nule.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Za smyčkou přidej count jen při kladné hodnotě; připravený převod result zachovej.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta">
/// Nakresli v připravených datech vlastní malou černobílou ikonu. Clues (číselné nápovědy) nech
/// vytvořit pomocí runs místo ručního zadání. Spolužákovi dej jen nápovědy a porovnej jeho rekonstrukci
/// s originálem.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 450, HEIGHT = 420;

int[][] picture = {{0, 0, 1, 0, 0}, {0, 1, 1, 1, 0}, {1, 1, 1, 1, 1}, {0, 1, 1, 1, 0}, {0, 0, 1, 0, 0}};
int[][] cells;
int[][] rowClues = new int[5][], colClues = new int[5][];
/// restart(): Obnovuje počáteční model Nonogram pro další pokus.
void restart() {
    cells = new int[5][5];
    for (int i = 0; i < 5; i++) {
        rowClues[i] = runs(picture[i]);
        int[] col = new int[5];
        for (int y = 0; y < 5; y++) {
            col[y] = picture[y][i];
        }
        colClues[i] = runs(col);
    }
}

void mark(int x, int y, boolean white) {
    if (x >= 0 && y >= 0 && x < 5 && y < 5) {
        cells[y][x] = white ? (cells[y][x] == 2 ? 0 : 2) : (cells[y][x] == 1 ? 0 : 1);
    }
}

/// won(): Porovnává skupiny řádků a sloupců s nápovědami a vyžaduje rozhodnutí všech polí.
boolean won() {
    for (int i = 0; i < 5; i++) {
        for (int c : cells[i]) {
            if (c == 0) {
                return false;
            }
        }
        int[] col = new int[5];
        for (int y = 0; y < 5; y++) {
            col[y] = cells[y][i];
        }
        if (!Arrays.equals(runs(cells[i]), rowClues[i]) || !Arrays.equals(runs(col), colClues[i])) {
            return false;
        }
    }
    return true;
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(Graphics2D g) {
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
    for (int i = 0; i < 5; i++) {
        g.setColor(Color.BLACK);
        g.drawString(Arrays.toString(rowClues[i]), 25, 110 + i * 50);
        g.drawString(colClues[i].length == 0 ? "0" : "" + colClues[i][0], 105 + i * 50, 65);
    }
    for (int y = 0; y < 5; y++) {
        for (int x = 0; x < 5; x++) {
            int xx = 85 + x * 50, yy = 80 + y * 50;
            g.setColor(cells[y][x] == 1 ? new Color(35, 50, 75) : cells[y][x] == 2 ? Color.WHITE : new Color(215, 220, 230));
            g.fillRect(xx, yy, 48, 48);
            if (cells[y][x] == 2) {
                g.setColor(Color.GRAY);
                g.drawLine(xx + 18, yy + 18, xx + 30, yy + 30);
                g.drawLine(xx + 30, yy + 18, xx + 18, yy + 30);
            }
        }
    }
    g.setColor(Color.BLACK);
    g.drawString(won() ? "All clues satisfied!" : "Grey = undecided; mark every square", 25, 380);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (x >= 85 && y >= 80 && x < 335 && y < 330) {
        mark((x - 85) / 50, (y - 80) / 50, button == MouseEvent.BUTTON3);
    }
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; zrušení okna
/// uzavírá toto zobrazení.
void showWindow() {
    var frame = new JFrame("Nonogram");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Left click: black / undecided   Right click: white / undecided<br>R: reset</div></html>", SwingConstants.CENTER);
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
