// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # 13.11 · Snake
///
/// Veď rostoucího hada k jídlu, aniž by se zakousl do sebe. Rychlé stisky kláves mají žádat rozumné
/// zatáčky, ne přepisovat pravidla mezi dvěma posuny.
///
/// **Tvoje mise:** Zatáčka okamžitě změní potvrzený směr místo čekání na další tick (časový krok). Oprav
/// čekající záměr a zachovej jednu platnou zatáčku na posun.
///
/// **Dnes trénuješ:** Input (vstup) a čas simulace jsou oddělené. Event (událost) klávesy navrhne směr;
/// další tick (časový krok) jej použije jednou, takže zákaz otočení stále vychází z posledního skutečného
/// pohybu.
///
/// Po spuštění programu šipka spustí hada; dalšími šipkami jej veď k jídlu. Náraz nebo zaplněná
/// plocha ukončí kolo; R obnoví začátek a čeká na další šipku.
void turn(int x, int y) {
    if (turnQueued || lost || won || Math.abs(x) + Math.abs(y) != 1) return;
    if (x == -dx && y == -dy) return;
    plannedX = x;
    plannedY = y;
    turnQueued = true;
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Navrhni mírnějšího hada: hlava se přes okraj přenese na protější stranu. Nejdřív rozhodni o nárazu
/// do těla v cíli, potom změň jen pravidlo stěny. Rychlé protichůdné klávesy stále musí dodržet
/// jedinou zatáčku za tick (krok).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F30">
/// ### Input (vstup) požádá; tick (krok) posune čas
///
/// Event (událost) může zaznamenat záměr bez okamžité změny celé simulace. Tick provede jednu
/// omezenou aktualizaci. Kreslení drž oddělené od obou činností.
///
/// ```java
/// int height = 0;
/// int pending = 0;
/// void press() {
///     pending = 2;
/// }
/// void tick() {
///     height += pending;
///     pending = 0;
/// }
/// ```
///
/// Po `press()` je height stále 0. Jeden `tick()` jej změní na 2 a požadavek spotřebuje; další tick
/// jej ponechá na 2. Opakovaný vstup před tímto krokem stále zaznamená jeden požadavek místo dvou
/// aktualizací. Připravený timer (časovač) může tick volat, ale pozastavená nebo dokončená hra nemá
/// pokračovat. Restart musí obnovit počáteční stav.
///
/// Předpověz: co od počátečního stavu udělá pořadí press, tick, press, tick?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `turnQueued || lost || won` — Kterýkoli z těchto state (stavů) stačí k odmítnutí nové zatáčky.
/// - `Math.abs(x) + Math.abs(y) != 1` — Povolí jen jednu jednotku v jedné ose, bez úhlopříček a nulového
///   pohybu.
/// - `x == -dx && y == -dy` — Rozpozná požadavek přímo opačný k potvrzenému směru.
/// - `dx = x;` — Nyní mění potvrzený vodorovný směr už během obsluhy vstupu.
///
/// ### Co už obstarává připravená část
///
/// Připravený step potvrdí field (položky objektu) dalšího směru, posune hada a ověří jídlo i srážky.
/// Okno převádí šipky na volání turn; restart vymaže čekající zatáčku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Turn ukládá záměr; step jej provede.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Uvnitř turn nepřiřazuj dx/dy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Zachovej turnQueued, aby druhá klávesa před tick (časovým krokem) nezvrátila první zatočení.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
record Cell(int x, int y) { }
final int COLUMNS = 20, ROWS = 15, TILE = 24;
final Random random = new Random();
final List<Cell> body = new ArrayList<>();
Cell food;
int dx, dy, plannedX, plannedY, score;
boolean turnQueued, lost, won;

/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
void restart() {
    body.clear();
    body.add(new Cell(5, 7));
    body.add(new Cell(4, 7));
    body.add(new Cell(3, 7));
    dx = plannedX = 1;
    dy = plannedY = score = 0;
    turnQueued = lost = won = false;
    placeFood();
}

void placeFood() {
    var free = new ArrayList<Cell>();
    for (int y = 0; y < ROWS; y++) {
        for (int x = 0; x < COLUMNS; x++) {
            var cell = new Cell(x, y);
            if (!body.contains(cell)) free.add(cell);
        }
    }
    won = free.isEmpty();
    food = won ? null : free.get(random.nextInt(free.size()));
}

/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
void step() {
    if (lost || won) return;
    dx = plannedX;
    dy = plannedY;
    turnQueued = false;
    var head = body.getFirst();
    var next = new Cell(head.x() + dx, head.y() + dy);
    boolean growing = next.equals(food);
    int occupied = body.size() - (growing ? 0 : 1);
    if (next.x() < 0 || next.x() >= COLUMNS || next.y() < 0 || next.y() >= ROWS
            || body.subList(0, occupied).contains(next)) {
        lost = true;
        return;
    }
    body.addFirst(next);
    if (growing) {
        score++;
        placeFood();
    } else {
        body.removeLast();
    }
}

/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
void draw(Graphics g) {
    g.setColor(new Color(14, 27, 30));
    g.fillRect(0, 0, COLUMNS * TILE, ROWS * TILE + 50);
    g.setColor(new Color(25, 44, 46));
    for (int x = 0; x <= COLUMNS; x++) g.drawLine(x * TILE, 0, x * TILE, ROWS * TILE);
    for (int y = 0; y <= ROWS; y++) g.drawLine(0, y * TILE, COLUMNS * TILE, y * TILE);
    if (food != null) {
        g.setColor(new Color(255, 116, 114));
        g.fillOval(food.x() * TILE + 4, food.y() * TILE + 4, TILE - 8, TILE - 8);
    }
    for (int index = 0; index < body.size(); index++) {
        Cell cell = body.get(index);
        g.setColor(index == 0 ? Color.WHITE : new Color(89, 222, 154));
        g.fillRoundRect(cell.x() * TILE + 2, cell.y() * TILE + 2, TILE - 4, TILE - 4, 6, 6);
    }
    g.setColor(Color.WHITE);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
    String status = lost ? "CRASH!" : won ? "FULL BOARD - YOU WIN!" : "SNACK SNAKE";
    g.drawString(status + "   Score: " + score, 12, ROWS * TILE + 21);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Arrow keys: turn     R: restart", 12, ROWS * TILE + 41);
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Snake - Snack Snake");
    boolean[] launched = {false};
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
            draw(g);
            if (!launched[0]) {
                g.setColor(Color.WHITE);
                g.drawString("Arrow key to start", 20, 65);
            }
        }
    };
    panel.setPreferredSize(new Dimension(COLUMNS * TILE, ROWS * TILE + 50));
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() >= KeyEvent.VK_LEFT && event.getKeyCode() <= KeyEvent.VK_DOWN) launched[0] = true;
            switch (event.getKeyCode()) {
                case KeyEvent.VK_LEFT -> turn(-1, 0);
                case KeyEvent.VK_RIGHT -> turn(1, 0);
                case KeyEvent.VK_UP -> turn(0, -1);
                case KeyEvent.VK_DOWN -> turn(0, 1);
                case KeyEvent.VK_R -> { restart(); launched[0] = false; }
            }
        }
    });
    var timer = new Timer(125, event -> {
/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
        if (launched[0]) step();
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent event) { timer.stop(); }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

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
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
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
/// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
///   List<String> lomené závorky popisují element type (typ prvku).
/// - `?` — V condition ? first : second vybere první hodnotu, když condition (podmínka) platí, jinak
///   druhou.
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
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
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `null` — Žádný object (objekt) tu není.
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
