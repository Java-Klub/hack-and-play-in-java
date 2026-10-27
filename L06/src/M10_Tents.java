// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
// </editor-fold>

/// # L06.10 · Tents
///
/// Postav stany vedle stromů, dodrž počty na okrajích a nech mezi stany místo. Nejdřív ověř, že mapa
/// kempu hlásí stromy na správných místech.
///
/// **Tvoje mise:** Jediný příklad stromu neodhalí prohozené souřadnice ani zapomenuté stromy. Rozšiř
/// learnerChecks() o obsazené i prázdné pozice na připravené mapě.
///
/// **Dnes trénuješ:** Writing functional checks (psaní funkčních kontrol): vybírej příklady, na kterých
/// se odliší řešení shodná v prvním snadném případě. Nesouměrné souřadnice zviditelní prohození os.
///
/// Spusť Run File a klikni na prázdné políčko vedle stromu pro postavení stanu, potom přes Test File
/// prověř své příklady čtení mapy.
/// Po opravě: Save → Test File → zahraj si znovu.
record TreeExample(int x, int y, boolean expected) {
}

TreeExample[] learnerChecks() {
    return new TreeExample[]{new TreeExample(0, 0, true)};
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `record TreeExample(int x, int y, boolean expected)` — Ukládá místo na mapě a údaj, zda tam má být
///   hlášen strom.
/// - `TreeExample[] learnerChecks()` — Vrací příklady, které použije functional checker (funkční
///   kontrola).
/// - `new TreeExample(0, 0, true)` — Existující příklad pokrývá jen strom vlevo nahoře.
/// - `return new TreeExample[]` — Vytváří array (pole) příkladů místo změny samotného kempu.
///
/// ### Co už obstarává připravená část
///
/// treeAt() používá klikání i kreslení. Připravená kontrola výhry řeší počty, odstupy i backtracking
/// (hledání s návratem) pro přiřazení stromů ke stanům; tvé příklady prověřují čtení mapy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. (0,0) je symetrické a záměnu souřadnic neodhalí.
/// 2. Porovnej skutečný strom (3,0) s prázdnou převrácenou pozicí (0,3).
/// 3. Přidej pozdější strom a souřadnici mimo plochu; každé očekávání musí souhlasit se skutečnou mapou.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 500, HEIGHT = 420;

Point[] trees = {new Point(0, 0), new Point(3, 0), new Point(1, 3), new Point(4, 4)};
int[] rowCounts = {1, 1, 0, 1, 1}, columnCounts = {0, 2, 0, 1, 1};
boolean[][] tents;
/// restart(): Obnovuje počáteční model Tents pro další pokus.
void restart() {
    tents = new boolean[5][5];
}

/// treeAt(): Hledá strom podle x/y; používá se při kliknutí i kreslení.
boolean treeAt(int x, int y) {
    for (var p : trees) {
        if (p.x == x && p.y == y) {
            return true;
        }
    }
    return false;
}

void toggle(int x, int y) {
    if (x >= 0 && y >= 0 && x < 5 && y < 5 && !treeAt(x, y)) {
        tents[y][x] = !tents[y][x];
    }
}

boolean separated() {
    for (int y = 0; y < 5; y++) {
        for (int x = 0; x < 5; x++) {
            if (tents[y][x]) {
                for (int yy = y - 1; yy <= y + 1; yy++) {
                    for (int xx = x - 1; xx <= x + 1; xx++) {
                        if (xx >= 0 && yy >= 0 && xx < 5 && yy < 5 && (xx != x || yy != y) && tents[yy][xx]) {
                            return false;
                        }
                    }
                }
            }
        }
    }
    return true;
}

/// match(): Dodané hledání s návratem přiřadí každému stanu jiný sousední strom.
boolean match(ArrayList<Point> all, int index, boolean[] used) {
    if (index == all.size()) {
        return true;
    }
    var p = all.get(index);
    for (int i = 0; i < trees.length; i++) {
        if (!used[i] && Math.abs(p.x - trees[i].x) + Math.abs(p.y - trees[i].y) == 1) {
            used[i] = true;
            if (match(all, index + 1, used)) {
                return true;
            }
            used[i] = false;
        }
    }
    return false;
}

/// won(): Kontroluje počty, oddělení a úplné párování stanů a stromů jedna ku jedné.
boolean won() {
    var all = new ArrayList<Point>();
    int[] columns = new int[5];
    for (int y = 0; y < 5; y++) {
        int count = 0;
        for (int x = 0; x < 5; x++) {
            if (tents[y][x]) {
                if (treeAt(x, y)) {
                    return false;
                }
                count++;
                columns[x]++;
                all.add(new Point(x, y));
            }
        }
        if (count != rowCounts[y]) {
            return false;
        }
    }
    return Arrays.equals(columns, columnCounts) && separated() && all.size() == trees.length && match(all, 0, new boolean[trees.length]);
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(Graphics2D g) {
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
    for (int i = 0; i < 5; i++) {
        g.setColor(Color.BLACK);
        g.drawString("" + columnCounts[i], 105 + i * 55, 40);
        g.drawString("" + rowCounts[i], 50, 80 + i * 55);
    }
    for (int y = 0; y < 5; y++) {
        for (int x = 0; x < 5; x++) {
            int xx = 85 + x * 55, yy = 50 + y * 55;
            g.setColor(new Color(232, 240, 222));
            g.fillRect(xx, yy, 53, 53);
            if (treeAt(x, y)) {
                g.setColor(new Color(45, 125, 65));
                g.fillOval(xx + 12, yy + 7, 30, 30);
                g.fillRect(xx + 24, yy + 28, 6, 18);
            }
            if (tents[y][x]) {
                g.setColor(Color.ORANGE);
                g.fillPolygon(new int[]{xx + 7, xx + 27, xx + 47}, new int[]{yy + 44, yy + 9, yy + 44}, 3);
            }
        }
    }
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    g.setColor(Color.BLACK);
    g.drawString(won() ? "Camp complete!" : "Match counts; tents may not touch, even diagonally", 20, 365);
    g.drawString("Each tent needs its own adjacent tree (not diagonally).", 20, 390);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (x >= 85 && y >= 50 && x < 360 && y < 325) {
        toggle((x - 85) / 55, (y - 50) / 55);
    }
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; zrušení okna
/// uzavírá toto zobrazení.
void showWindow() {
    var frame = new JFrame("Tents");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click: tent / empty   Match edge counts; tents must not touch   R:<br>reset</div></html>", SwingConstants.CENTER);
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
    verifyExamples();
    SwingUtilities.invokeLater(this::showWindow);
}

/// verifyExamples(): Start ověřuje tvé příklady skutečnými neměnícími dotazy; herní stav se nemění.
void verifyExamples() {
    for (var e : learnerChecks()) {
        if (treeAt(e.x(), e.y()) != e.expected()) {
            throw new IllegalStateException("Tree example has wrong expected result");
        }
    }
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
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `public` — Zpřístupňuje tuto declaration (deklaraci) dalšímu kódu.
/// - `protected` — Visibility keyword (klíčové slovo přístupnosti) používané připraveným library callback
///   (knihovním zpětným voláním), například paintComponent.
/// - `super` — V super.paintComponent(g) požádá připravenou parent implementation (nadřazenou
///   implementaci), aby nejprve vykreslila běžné pozadí.
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

