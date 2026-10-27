// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.util.Arrays;
// </editor-fold>

/// # L08.07 · Bridges
///
/// Propoj ostrovy jednoduchými nebo dvojitými mosty, dokud nebudou souhlasit počty. Vyzkoušej riskantní
/// spojení a vrať ho zpět pro porovnání.
///
/// **Tvoje mise:** Vrácení tahu obnoví stejné, už změněné pole mostů, takže se nic nevrátí. Oprav
/// snapshot(), aby uchovalo počty mostů před úpravou.
///
/// **Dnes trénuješ:** Copies and aliasing (kopie a sdílené odkazy): vrácení dalšího odkazu na pole
/// nekopíruje jeho hodnoty. Uložený snapshot (snímek) musí přežít pozdější zápisy do pracovního stavu.
///
/// Spusť Run File, kliknutím na slabě vyznačené spojení změň počet mostů a klávesou U zkus tuto úpravu
/// vrátit.
/// Po opravě: Save → Test File → zahraj si znovu.
int[] snapshot() {
    return bridges.clone();
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int[]` — Počty mostů jsou obyčejné hodnoty int v jednom array (poli), bez měnitelných vnořených
///   řádků.
/// - `snapshot()` — Volající očekává uložený stav modelu, nikoli nový viditelný herní tah.
/// - `return bridges` — Vrací reference (odkaz) na současné pole; obě jména by viděla pozdější zápisy.
///
/// ### Co už obstarává připravená část
///
/// choose() před skutečnou změnou hrany ukládá snapshot (snímek). undo() ho jednou obnoví; crossing()
/// poskytuje geometrii mostů a won() kontroluje počty i souvislost, takže tyto algoritmy zůstávají
/// připravené.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Přiřazení bridges jiné variable kopíruje referenci, nikoli buňky.
/// 2. Jednorozměrná int[] obsahuje hodnoty; stačí jeden clone.
/// 3. Vrať bridges.clone(); dodaná undo() potom obnoví nezávislý stav.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 470, HEIGHT = 420;

Point[] islands = {new Point(0, 0), new Point(2, 0), new Point(4, 0), new Point(0, 2), new Point(4, 2), new Point(0, 4), new Point(2, 4), new Point(4, 4)};
int[][] edges = {{0, 1}, {1, 2}, {0, 3}, {2, 4}, {3, 5}, {4, 7}, {5, 6}, {6, 7}, {1, 6}, {3, 4}};
int[] counts = {2, 2, 2, 2, 2, 2, 2, 2};
int[] bridges;
/// restart(): Obnovuje počáteční model Bridges pro další pokus.
void restart() {
    bridges = new int[edges.length];
    previousBridges = null;
}

/// crossing(): Dodaná geometrie úseček před testem křížení vynechá společné koncové body.
boolean crossing(int a, int b) {
    var e = edges[a];
    var f = edges[b];
    for (int x : e) {
        for (int y : f) {
            if (x == y) {
                return false;
            }
        }
    }
    return Line2D.linesIntersect(islands[e[0]].x, islands[e[0]].y, islands[e[1]].x, islands[e[1]].y,
    islands[f[0]].x, islands[f[0]].y, islands[f[1]].x, islands[f[1]].y);
}

boolean won() {
    int[] actual = new int[8];
    boolean[] reached = new boolean[8];
    reached[0] = true;
    for (int i = 0; i < edges.length; i++) {
        if (bridges[i]>0) {
            if (bridges[i]>2) {
                return false;
            }
            actual[edges[i][0]] += bridges[i];
            actual[edges[i][1]] += bridges[i];
            for (int j = 0; j < i; j++) {
                if (bridges[j]>0 && crossing(i, j)) {
                    return false;
                }
            }
        }
    }
    if (!Arrays.equals(actual, counts)) {
        return false;
    }
    for (int pass = 0; pass < 8; pass++) {
        for (int i = 0; i < edges.length; i++) {
            if (bridges[i]>0 && (reached[edges[i][0]] || reached[edges[i][1]])) {
                reached[edges[i][0]] = reached[edges[i][1]] = true;
            }
        }
    }
    for (boolean r : reached) {
        if (!r) {
            return false;
        }
    }
    return true;
}

void choose(int x, int y) {
    double best = 14;
    int chosen = -1;
    for (int i = 0; i < edges.length; i++) {
        var a = islands[edges[i][0]];
        var b = islands[edges[i][1]];
        double distance = Line2D.ptSegDist(70 + a.x * 75, 45 + a.y * 75, 70 + b.x * 75, 45 + b.y * 75, x, y);
        if (distance < best) {
            best = distance;
            chosen = i;
        }
    }
    if (chosen >= 0) {
        previousBridges = snapshot();
        bridges[chosen] = (bridges[chosen] + 1) % 3;
    }
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(Graphics2D g) {
    for (int i = 0; i < edges.length; i++) {
        var a = islands[edges[i][0]];
        var b = islands[edges[i][1]];
        int ax = 70 + a.x * 75, ay = 45 + a.y * 75, bx = 70 + b.x * 75, by = 45 + b.y * 75;
        g.setColor(bridges[i] == 0 ? new Color(225, 225, 225) : new Color(50, 110, 155));
        g.setStroke(new BasicStroke(bridges[i] == 0 ? 1 : 3));
        if (bridges[i]<2) {
            g.drawLine(ax, ay, bx, by);
        }
        else {
            if (ax == bx) {
                g.drawLine(ax - 4, ay, bx - 4, by);
                g.drawLine(ax + 4, ay, bx + 4, by);
            }
            else {
                g.drawLine(ax, ay - 4, bx, by - 4);
                g.drawLine(ax, ay + 4, bx, by + 4);
            }
        }
    }
    for (int i = 0; i < 8; i++) {
        int x = 70 + islands[i].x * 75, y = 45 + islands[i].y * 75;
        g.setColor(Color.WHITE);
        g.fillOval(x - 17, y - 17, 34, 34);
        g.setColor(Color.BLACK);
        g.drawOval(x - 17, y - 17, 34, 34);
        g.drawString("" + counts[i], x - 4, y + 5);
    }
    g.setColor(Color.BLACK);
    g.drawString(won() ? "All islands joined!" : "Match numbers and join one network; no crossings", 20, 390);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_U) {
        undo();
    }
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    choose(x, y);
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; zrušení okna
/// uzavírá toto zobrazení.
void showWindow() {
    var frame = new JFrame("Bridges");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click a faint edge: 0 / 1 / 2 bridges   R: reset</div></html>", SwingConstants.CENTER);
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

int[] previousBridges;

/// undo(): Obnovuje právě jeden uložený snapshot modelu a spotřebuje možnost vrácení.
void undo() {
    if (previousBridges != null) {
        bridges = previousBridges;
        previousBridges = null;
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
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
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
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `public` — Zpřístupňuje tuto declaration (deklaraci) dalšímu kódu.
/// - `protected` — Visibility keyword (klíčové slovo přístupnosti) používané připraveným library callback
///   (knihovním zpětným voláním), například paintComponent.
/// - `super` — V super.paintComponent(g) požádá připravenou parent implementation (nadřazenou
///   implementaci), aby nejprve vykreslila běžné pozadí.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

