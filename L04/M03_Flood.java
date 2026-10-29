// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
// </editor-fold>

/// # L04.03 · Flood
///
/// Rozšiřuj jednobarevnou oblast z levého horního rohu, až pokryje desku. Vyber novou barvu, která
/// propojí užitečné sousední ostrůvky, a sleduj, kam se každá volba rozlije.
///
/// **Tvoje mise:** Změna barvy dosáhne k prvním sousedům a potom opustí již objevená políčka. Oprav
/// spread tak, aby zpracovala celý rostoucí pracovní seznam; připravené kontroly sousedů zachovej.
///
/// **Dnes trénuješ:** While nad přibývající prací: queue uchovává políčka čekající na prohlédnutí. Po
/// každém průchodu index (pozice) postoupí a mohou přibýt noví sousedé; porovnávej s aktuální velikostí
/// místo předpokládaného pevného počtu průchodů.
///
/// Zvol barvu kliknutím na její políčko v paletě nebo stiskem 1–4; sleduj změny
/// propojených políček a pomocí R obnov desku.
void spread(ArrayList<Point> queue, int old, int colour) {
    int index = 0;
    while (index < queue.size()) {
        Point point = queue.get(index);
        visitNeighbours(queue, point, old, colour);
        index++;
    }
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F10">
/// `while` ověřuje condition (podmínku) před každým průchodem včetně prvního. Je-li hned false
/// (nepravda), tělo proběhne nula krát. Něco musí postupovat k ukončení nebo záměrně čekat na nový
/// input (vstup).
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// int energy = 5;
/// while (energy > 0) {
///     IO.println(energy);
///     energy -= 2;
/// }
/// ```
///
/// Předpověz vypsaná čísla a konečnou uloženou hodnotu. výstup v terminálu (výstup) je `5`, `3`,
/// `1`; potom je energy `-1`, takže další condition neplatí. Podmínka se neověřuje uprostřed těla.
/// `energy -= 2` odečte dvojku a uloží výsledek; bez postupu se loop opakuje donekonečna. `return`
/// ukončí method (metodu); `break` ukončí loop (smyčku). Sleduj condition a měnící se hodnoty
/// odděleně, zejména pokud prohledávaná data mohou přibývat. Samotná přítomnost while nezaručuje,
/// že program někdy skončí; důvod ukončení musí vyplývat z prováděných kroků.
///
/// Nyní předpověz trace (stopu průběhu), pokud energy začíná na čtyřech.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int index = 0;` — Začni zpracování u prvního objeveného políčka v připraveném pracovním seznamu.
/// - `while (index < queue.size() && index < 1)` — Podmínka se znovu ověřuje při každém průchodu, i když
///   queue mezitím vyrostla. Nyní ji omezují obě porovnání.
/// - `Point point = queue.get(index);` — Vezmi souřadnice dalšího objeveného políčka; Point je připravená
///   dvojice souřadnic.
/// - `visitNeighbours(queue, point, old, colour);` — Připravená akce přebarví vhodné sousedy a připojí je
///   do stejného pracovního seznamu.
/// - `index++;` — Postup za právě zpracované políčko; větší seznam neznamená opakování jeho dřívějších
///   položek.
///
/// ### Co už obstarává připravená část
///
/// fill změní počáteční políčko a vloží je do queue. visitNeighbours kontroluje čtyři směry a hranice
/// desky; nové políčko označí před přidáním, takže konečná deska nemůže stále přidávat totéž políčko.
/// draw, key a část vytvářející okno jsou připravené.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// Zde je queue připravený pracovní seznam souřadnic objevených políček. index počítá již zpracované
/// položky; queue.size() oznamuje dosud objevený počet. Například průchod začínající s index 0 a
/// velikostí 1 může objevit dva sousedy: po posunu má index 1 a velikost je 3, takže práce zbývá.
/// Velikost se záměrně ověřuje znovu. visitNeighbours každé nové políčko přebarví před přidáním, čímž
/// brání opakovanému objevování téhož políčka. Deska má pouze 64 políček. Upravuješ pokračování loop
/// (smyčky), nikoli připravenou práci se souřadnicemi a sousedy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// visitNeighbours může do queue (fronty) přidat další buňky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Po každé iteration (iteraci) znovu ověř aktuální queue.size().
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Použij while (index < queue.size()); za návštěvou zachovej index++.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta">
/// Vymysli variantu s omezeným inkoustem: spread zpracuje nejvýše tři čekající buňky. Nakresli pořadí
/// prvních návštěv a porovnej výsledek s úplným zaplavením. Zvol vlastní zásobu inkoustu a vysvětli,
/// proč nyní záleží na pořadí queue (fronty).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
void visitNeighbours(ArrayList<Point> queue, Point point, int old, int colour) {
    for (var delta : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
        int x = point.x + delta[0];
        int y = point.y + delta[1];
        if (x >= 0 && y >= 0 && x < 8 && y < 8 && board[y][x] == old) {
            board[y][x] = colour;
            queue.add(new Point(x, y));
        }
    }
}

final int WIDTH = 520, HEIGHT = 420;

int[][] board;
int moves;
Color[] colours = {new Color(239, 97, 97), new Color(65, 164, 222), new Color(250, 194, 63), new Color(109, 195, 121)};
void restart() {
    board = new int[8][8];
    moves = 0;
    var random = new Random(5421);
    for (var row : board) {
        for (int x = 0; x < 8; x++) {
            row[x] = random.nextInt(4);
        }
    }
}

void fill(int colour) {
    if (colour < 0 || colour >= 4 || colour == board[0][0] || won()) {
        return;
    }
    int old = board[0][0];
    var queue = new ArrayList<Point>();
    queue.add(new Point(0, 0));
    board[0][0] = colour;
    spread(queue, old, colour);
    moves++;
}

boolean won() {
    for (var row : board) {
        for (int c : row) {
            if (c != board[0][0]) {
                return false;
            }
        }
    }
    return true;
}

void draw(Graphics2D g) {
    for (int y = 0; y < 8; y++) {
        for (int x = 0; x < 8; x++) {
            g.setColor(colours[board[y][x]]);
            g.fillRect(25 + x * 40, 25 + y * 40, 39, 39);
        }
    }
    for (int c = 0; c < 4; c++) {
        g.setColor(colours[c]);
        g.fillRect(380, 50 + c * 65, 100, 45);
        g.setColor(Color.BLACK);
        g.drawString("" + (c + 1), 425, 78 + c * 65);
    }
    g.setColor(Color.BLACK);
    g.drawString((won() ? "Filled! " : "") + "Moves: " + moves, 25, 380);
    g.drawString("Seed 5421 - compare your next attempt", 25, 403);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code >= KeyEvent.VK_1 && code <= KeyEvent.VK_4) {
        fill(code - KeyEvent.VK_1);
    }
}

void press(int x, int y, int button) {
    if (x >= 380 && x < 480 && y >= 50 && y < 310 && (y - 50) % 65 < 45) {
        fill((y - 50) / 65);
    }
}

void showWindow() {
    var frame = new JFrame("Flood");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click a colour or press 1-4   R: same board</div></html>", SwingConstants.CENTER);
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
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `public` — Zpřístupňuje tuto declaration (deklaraci) dalšímu kódu.
/// - `protected` — Visibility keyword (klíčové slovo přístupnosti) používané připraveným library callback
///   (knihovním zpětným voláním), například paintComponent.
/// - `super` — V super.paintComponent(g) požádá připravenou parent implementation (nadřazenou
///   implementaci), aby nejprve vykreslila běžné pozadí.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
