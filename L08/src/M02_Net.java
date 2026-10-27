// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
// </editor-fold>

/// # L08.02 · Net
///
/// Otáčej dílky potrubí, dokud nedostane energii celá síť ze zdroje. Zelené políčko má mít skutečné
/// oboustranné spojení.
///
/// **Tvoje mise:** Trubka mířící na prázdnou stranu souseda ho přesto napájí. Oprav connected(), aby
/// spojení vyžadovalo odpovídající vývody na obou dílcích.
///
/// **Dnes trénuješ:** Grid neighbours (sousedé v mřížce): sousedství samo neznamená propojení. Pro
/// přechod určitým směrem prověř vývod i příchozí stranu souseda.
///
/// Spusť Run File a kliknutím otáčej potrubí dílku, přitom sleduj, které dílky při spojení se zdrojem
/// zezelenají.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F18 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
boolean[][] connected() {
    boolean[][] reached = new boolean[4][4];
    var queue = new ArrayList<Point>();
    queue.add(new Point(0, 0));
    reached[0][0] = true;
    for (int i = 0; i < queue.size(); i++) {
        var p = queue.get(i);
        for (int d = 0; d < 4; d++) {
            int x = p.x + dx[d], y = p.y + dy[d];
            if (x >= 0 && y >= 0 && x < 4 && y < 4 && !reached[y][x] && (tiles[p.y][p.x]&(1<<d)) != 0 && (tiles[y][x]&(1<<((d + 2) % 4))) != 0) {
                reached[y][x] = true;
                queue.add(new Point(x, y));
            }
        }
    }
    return reached;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F18">
/// ### Grids: nejdřív řádek, potom sloupec
///
/// V array of rows (poli řádků) první index (pozice) vybírá řádek a druhý pozici v něm. Řádky mohou
/// mít různé délky. Neighbour (soused) je posun od buňky, nikoli další absolutní pozice. Před
/// kontrolou existence kandidáta urči povolené směry.
///
/// ```java
/// int[][] mosaic = {{3, 6, 9}, {2, 4, 8}};
/// int row = 0;
/// int column = 1;
/// IO.println(mosaic[row][column + 1]);
/// ```
///
/// Výstup je devět: řádek nula a sloupec dva vyberou tento element (prvek). Pohyb dolů mění row;
/// doprava mění column. Který z těchto pohybů z pravé horní cell (buňky) zůstane uvnitř? U úhlopříčky
/// se mění obě souřadnice; bounds (meze) musíš ověřit před přístupem ke kandidátovi.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `p.x + dx[d]` — Použije vodorovný posun vybraného směru k nalezení souseda.
/// - `1<<d` — Připravený bit shift (bitový posun) vybere bit přiřazený směru d.
/// - `tiles[p.y][p.x]&(1<<d)` — Bitwise AND (bitové a) zjišťuje, zda má aktuální dílek daný vývod.
/// - `!reached[y][x]` — Brání opětovnému zařazení už objevených dílků do fronty.
///
/// ### Co už obstarává připravená část
///
/// Připravený průchod s queue (frontou) začíná ve zdroji a označuje dosažené dílky. Otáčení mění bity
/// vývodů, kreslení barví dosažené dílky a won() přidává kontrolu úplné uzavřené sítě.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Čtení jediného port bit (bitu otvoru)
///
/// Každý směr má jeden bit. Bit position (pozice bitu) začíná nulou: `1 << d` dává mask (masku) 1, 2,
/// 4, 8 pro směry 0, 1, 2, 3. Bitwise `&` (bitový součin) zachová jen pozice zapnuté na obou
/// stranách. Liší se od boolean `&&` (logického a).
///
/// ```java
/// int flags = 5;
/// int selected = 1 << 2;
/// IO.println((flags & selected) != 0);
/// ```
///
/// Výstup je true: 5 = 1 + 4, takže `5 & 4` dává 4, zatímco `5 & 2` dává 0. Nenulový výsledek
/// znamená, že vybraný bit je zapnutý. Nyní předpověz test bitu tři, pokud flags má hodnotu 10. Směry
/// zde běží Up, Right, Down, Left, proto jsou protilehlé otvory o dva kroky dál ve čtyřsměrném kruhu.
/// Před přijetím souseda ověř obě strany spojení, ne jen existenci souseda.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// d vybírá nahoru, doprava, dolů nebo doleva.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Protilehlý směr je (d+2)%4.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Před vložením do queue vyžaduj bit d aktuálního dílku i protilehlý bit souseda.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 460, HEIGHT = 420;

int[][] tiles = new int[4][4];
int[] dx = {0, 1, 0, -1}, dy = {-1, 0, 1, 0};
void solvedBoard() {
    tiles = new int[4][4];
    for (int i = 0; i < 15; i++) {
        int y = i / 4, x = y % 2 == 0 ? i % 4 : 3 - i % 4;
        int yy = (i + 1) / 4, xx = yy % 2 == 0 ? (i + 1) % 4 : 3 - (i + 1) % 4;
        int d = yy > y ? 2 : xx > x ? 1 : 3;
        tiles[y][x]|=1<<d;
        tiles[yy][xx]|=1<<((d + 2) % 4);
    }
}

/// restart(): Obnovuje počáteční model Net pro další pokus.
void restart() {
    solvedBoard();
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            for (int i = 0; i < (x + 2 * y + 1) % 4; i++) {
                rotate(x, y);
            }
        }
    }
}

void rotate(int x, int y) {
    if (x >= 0 && y >= 0 && x < 4 && y < 4) {
        tiles[y][x] = ((tiles[y][x]<<1)&15)|(tiles[y][x]>>3);
    }
}

/// won(): Vyžaduje dosažení všech dílků, žádné otevřené porty a přesný počet hran stromu.
boolean won() {
    var reached = connected();
    int ports = 0;
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            if (!reached[y][x]) {
                return false;
            }
            for (int d = 0; d < 4; d++) {
                if ((tiles[y][x]&(1<<d)) != 0) {
                    ports++;
                    int xx = x + dx[d], yy = y + dy[d];
                    if (xx < 0 || yy < 0 || xx >= 4 || yy >= 4 || (tiles[yy][xx]&(1<<((d + 2) % 4))) == 0) {
                        return false;
                    }
                }
            }
        }
    }
    return ports == 30;
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(Graphics2D g) {
    var reached = connected();
    g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            int xx = 100 + x * 75, yy = 65 + y * 75;
            g.setColor(new Color(235, 238, 243));
            g.fillRect(xx - 35, yy - 35, 70, 70);
            g.setColor(reached[y][x] ? new Color(35, 165, 115) : Color.GRAY);
            for (int d = 0; d < 4; d++) {
                if ((tiles[y][x]&(1<<d)) != 0) {
                    g.drawLine(xx, yy, xx + dx[d] * 34, yy + dy[d] * 34);
                }
            }
            g.fillOval(xx - 7, yy - 7, 14, 14);
        }
    }
    g.setColor(Color.BLACK);
    g.drawString("SOURCE", 70, 18);
    g.drawString(won() ? "Connected tree complete!" : "Connect every tile; leave no open ports or loops", 35, 390);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (x >= 65 && x < 365 && y >= 30 && y < 330) {
        rotate((x - 65) / 75, (y - 30) / 75);
    }
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; zrušení okna
/// uzavírá toto zobrazení.
void showWindow() {
    var frame = new JFrame("Net");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click tiles: rotate clockwise   Green: joined to source   R:<br>restart</div></html>", SwingConstants.CENTER);
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

