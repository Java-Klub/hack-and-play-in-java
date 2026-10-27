// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
// </editor-fold>

/// # L08.08 · MagnetDominoes
///
/// Vybírej polaritu magnetických domin podle řádkových a sloupcových nápověd. Rychlé vrácení tahu ti
/// dovolí bezpečně zkusit jiné uspořádání.
///
/// **Tvoje mise:** Vrácení tahu ponechá naposledy změněnou polaritu místo předchozí. Oprav copyState(),
/// aby uložené stavy domin byly nezávislé.
///
/// **Dnes trénuješ:** Copies and aliasing (kopie a sdílené odkazy): dvě jména mohou sdílet jedno
/// měnitelné pole. Zde stačí kopírovat jednorozměrné hodnoty, protože stav každého domina je int.
///
/// Spusť Run File, kliknutím na domino změň polaritu a klávesou U zkus obnovit předchozí volbu.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F20 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
int[] copyState() {
    return state;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F20">
/// ### References: jiné jméno, nebo jiný objekt?
///
/// Array variable (proměnná pole) uchovává reference (odkaz). Assignment (přiřazení) může dát dvěma
/// jménům přístup k jedinému array (poli); zápis přes kterékoli změní sdílený objekt. `clone()` na
/// jednorozměrném int array vytvoří další array se zkopírovanými hodnotami. Původní array i kopie se
/// potom mohou měnit nezávisle.
///
/// ```java
/// int[] wave = {4, 1};
/// int[] shared = wave;
/// int[] saved = wave.clone();
/// wave[1] = 7;
/// IO.println(shared[1]);
/// IO.println(saved[1]);
/// ```
///
/// Výstup je sedm a potom jedna: shared vidí zápis, saved uchová kopii. Po `saved[0] = 8` předpověz
/// `wave[0]` a `saved[0]`. `clone()` kopíruje jednu vrstvu: element (prvky) mřížky jsou reference na
/// řádky, takže kopie vnějšího array řádky dál sdílí. Ptej se, které objekty zůstaly společné.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int[]` — Jeden prvek array (pole) ukládá zvolený stav jednoho domina.
/// - `copyState()` — Pojmenovává místo, kde se požaduje zachování stavu před úpravou.
/// - `return state` — Předá stejný mutable object (měnitelný objekt), pokud nevznikne skutečná kopie.
///
/// ### Co už obstarává připravená část
///
/// cycle() před změnou domina ukládá copyState() a undo() obnovuje uložené pole. pole() vykládá orientaci
/// na políčku; won() řeší počty i zakázané stejné sousedy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Kopíruj hodnoty, ne pozdější obrazovku
///
/// Polarita každého dílku je int v jediném array (poli). Jeho `clone()` kopíruje tato čísla do
/// odděleného úložiště. Zde není třeba kopírovat další row object (objekty řádků) či vnořené List
/// (seznamy). Předpověz uloženou polaritu po dvojí změně živého dílku; stále má popisovat okamžik
/// pořízení snapshot (snímku). Připravené undo z těchto dat obnoví pracovní state (stav). Vysvětli,
/// proč repaint (překreslení) neopraví sdílené úložiště.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// previousState musí přežít pozdější zápis do state[domino].
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Alias stejné reference uvidí tento zápis také.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// V copyState() vrať state.clone(); pravidla polarity ponech.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 520, HEIGHT = 400;

int[] state;
int[] rowPlus = {1, 2, 1, 2}, rowMinus = {1, 2, 1, 2}, colPlus = {1, 2, 1, 2}, colMinus = {2, 1, 2, 1};
/// restart(): Obnovuje počáteční model Magnet Dominoes pro další pokus.
void restart() {
    state = new int[8];
    previousState = null;
    Arrays.fill(state, -1);
}

/// pole(): Převádí orientaci jednoho domina na znaménko požadovaného pole.
int pole(int x, int y) {
    int s = state[y * 2 + x / 2];
    if (s <= 0) {
        return 0;
    }
    int first = s == 1 ? 1 : -1;
    return x % 2 == 0 ? first : -first;
}

void cycle(int domino) {
    if (domino >= 0 && domino < 8) {
        previousState = copyState();
        state[domino] = state[domino] == -1 ? 1 : state[domino] == 1 ? 2 : state[domino] == 2 ? 0 : -1;
    }
}

boolean won() {
    for (int s : state) {
        if (s < 0) {
            return false;
        }
    }
    int[] cp = new int[4], cm = new int[4];
    for (int y = 0; y < 4; y++) {
        int plus = 0, minus = 0;
        for (int x = 0; x < 4; x++) {
            int p = pole(x, y);
            if (p == 1) {
                plus++;
                cp[x]++;
            }
            if (p == -1) {
                minus++;
                cm[x]++;
            }
            if (p != 0 && ((x < 3 && p == pole(x + 1, y)) || (y < 3 && p == pole(x, y + 1)))) {
                return false;
            }
        }
        if (plus != rowPlus[y] || minus != rowMinus[y]) {
            return false;
        }
    }
    return Arrays.equals(cp, colPlus) && Arrays.equals(cm, colMinus);
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(Graphics2D g) {
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
    for (int i = 0; i < 4; i++) {
        g.setColor(Color.BLACK);
        g.drawString(colPlus[i] + "/" + colMinus[i], 109 + i * 60, 35);
        g.drawString(rowPlus[i] + "/" + rowMinus[i], 35, 85 + i * 60);
    }
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            int xx = 95 + x * 60, yy = 50 + y * 60;
            int s = state[y * 2 + x / 2], p = pole(x, y);
            g.setColor(s < 0 ? new Color(225, 225, 230) : p == 1 ? new Color(255, 200, 190) : p == -1 ? new Color(190, 215, 255) : new Color(240, 240, 220));
            g.fillRect(xx, yy, 58, 58);
            g.setColor(Color.BLACK);
            g.drawString(s < 0 ? "?" : p == 1 ? "+" : p == -1 ? "-" : "·", xx + 22, yy + 35);
        }
    }
    g.setStroke(new BasicStroke(3));
    g.setColor(Color.DARK_GRAY);
    for (int y = 0; y < 4; y++) {
        for (int d = 0; d < 2; d++) {
            g.drawRect(95 + d * 120, 50 + y * 60, 118, 58);
        }
    }
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    g.drawString(won() ? "All poles balanced!" : "Edge numbers are + / - counts. Equal poles cannot touch.", 20, 345);
    g.drawString("? is undecided; · is a deliberately neutral domino.", 20, 375);
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
    if (x >= 95 && y >= 50 && x < 335 && y < 290) {
        cycle(((y - 50) / 60) * 2 + (x - 95) / 120);
    }
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; zrušení okna
/// uzavírá toto zobrazení.
void showWindow() {
    var frame = new JFrame("Magnet Dominoes");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click a domino: +\u2212 / \u2212+ / neutral / ?<br>U: undo one change   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

/// main(): Spouští model a potom plánuje dodané okno na Swing event thread.
/// Připravené Undo (vrácení změny) ovládá U: obnoví jeden předchozí tah. R začne hádanku znovu.
void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}

int[] previousState;

/// undo(): Obnovuje právě jeden uložený snapshot modelu a spotřebuje možnost vrácení.
void undo() {
    if (previousState != null) {
        state = previousState;
        previousState = null;
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
/// - `null` — Žádný object (objekt) tu není.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

