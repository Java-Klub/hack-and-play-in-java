// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # 09.17 · MiniPacman
///
/// Posbírej všechny tečky, zatímco tě v bludišti honí duch. Poslední tečku i střet s duchem můžeš
/// stihnout ve stejném kroku.
///
/// **Tvoje mise:** Vytvoř afterContact(): dopadení vrací CAUGHT i při poslední tečce; jinak nula teček
/// vrací WON a všechny ostatní situace PLAY. Nedokončený transition (přechod) nyní nechává každé kolo
/// běžet.
///
/// **Dnes trénuješ:** Popiš jeden state transition (přechod stavu) s výslovnou prioritou souběžných
/// condition (podmínek).
///
/// Spusť program a šipkou se rozjeď bludištěm, potom miř k tečkám a vyhýbej se duchovi.
enum RoundState {
    PLAY, CAUGHT, WON
}

RoundState afterContact(boolean caught, int dotsLeft) {
    if (caught) return RoundState.CAUGHT;
    if (dotsLeft == 0) return RoundState.WON;
    return RoundState.PLAY;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F21">
/// ### State (stav) dostává jméno
///
/// Enum (výčtový typ) vyjmenuje uzavřenou sadu jmen. Variable (proměnná) tohoto typu drží vždy jedno
/// jméno: nemůže současně obsahovat OPEN i LOCKED.
///
/// ```java
/// enum Door { LOCKED, OPEN }
/// Door afterKey(Door before, boolean hasKey) {
///     if (before == Door.LOCKED && hasKey) return Door.OPEN;
///     return before;
/// }
/// ```
///
/// Trace (průchod) pro `afterKey(Door.LOCKED, true)`: obě části `&&` jsou true, proto se vrátí OPEN.
/// Bez klíče se vrátí původní LOCKED. Samotné volání ještě výsledek neukládá. Caller (volající) může
/// při změně použít `door = afterKey(door, hasKey)`.
///
/// Nakresli dva kruhy LOCKED → OPEN a ke šipce napiš „key“. Rozhodni, co se má stát s již otevřenými
/// dveřmi. Předpověz výsledek pro OPEN s klíčem a ukaž, ke kterému `return` se dojde. Herní priority
/// musí určit pravidla hry; enum je sám nevymyslí. Named state (pojmenovaný stav) jen pomůže odlišit
/// možnosti bez tajných čísel.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst tuto část
///
/// RoundState uvádí tři vzájemně výlučné výsledky. `caught` a `dotsLeft` jsou fakta o stejném kroku,
/// nikoli o různých kolech. Nejdřív vyhodnoť prioritu. Caller (volající) uloží vrácený state (stav) a
/// připravený timer (časovač) dokončené kolo dál neposouvá.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Sepiš výsledky pro caught/not caught v kombinaci s nulou a nenulovým počtem teček.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Případ dopadení současně s poslední tečkou určuje pořadí condition (podmínek).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Nejdřív vracej CAUGHT pro dopadení, pak WON bez teček, jinak PLAY.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta · dobrovolně">
/// Vytvoř jasně označenou variantu, ve které poslední tečka vyhrává remízu. Porovnej oba konce na
/// stejném dopadení a řekni, který ti připadá spravedlivější.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
record Cell(int x, int y) {
}

final String[] map = {
"#############", "#...........#", "#.###...###.#",
"#...#...#...#", "#...#...#...#", "#...........#",
"#.###...###.#", "#...........#", "#############"
};
final int TILE = 30;
final boolean[][] pellets = new boolean[map.length][map[0].length()];
final Random random = new Random();
int playerX, playerY, ghostX, ghostY, dx, dy, wantedX, wantedY, remaining, ticks;
boolean started;

RoundState state;

boolean open(int x, int y) {
    return y >= 0 && y < map.length && x >= 0 && x < map[y].length() && map[y].charAt(x) != '#';
}

/// restart(): Obnovuje počáteční model Mini Pacman pro další pokus.
void restart() {
    playerX = playerY = 1;
    ghostX = 11;
    ghostY = 7;
    dx = dy = wantedX = wantedY = remaining = ticks = 0;
    started = false;
    state = RoundState.PLAY;
    for (int y = 0; y < map.length; y++) {
        for (int x = 0; x < map[y].length(); x++) {
            pellets[y][x] = open(x, y) && (x != playerX || y != playerY);
            if (pellets[y][x]) {
                remaining++;
            }
        }
    }
}

void direction(int x, int y) {
    if (Math.abs(x) + Math.abs(y) != 1) {
        return;
    }
    wantedX = x;
    wantedY = y;
    started = true;
}

/// moveGhost(): Dodané hladové hledání legálního souseda pronásleduje hráče bez průchodu zdí.
void moveGhost() {
    var choices = new ArrayList<Cell>();
    int best = Integer.MAX_VALUE;
    for (var delta : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
        int x = ghostX + delta[0], y = ghostY + delta[1];
        if (!open(x, y)) {
            continue;
        }
        int distance = Math.abs(x - playerX) + Math.abs(y - playerY);
        if (distance < best) {
            choices.clear();
            best = distance;
        }
        if (distance == best) {
            choices.add(new Cell(x, y));
        }
    }
    if (!choices.isEmpty()) {
        var next = choices.get(random.nextInt(choices.size()));
        ghostX = next.x();
        ghostY = next.y();
    }
}

void step() {
    if (!started || state != RoundState.PLAY) {
        return;
    }
    if (open(playerX + wantedX, playerY + wantedY)) {
        dx = wantedX;
        dy = wantedY;
    }
    if (open(playerX + dx, playerY + dy)) {
        playerX += dx;
        playerY += dy;
    }
    if (pellets[playerY][playerX]) {
        pellets[playerY][playerX] = false;
        remaining--;
    }
    boolean caught = playerX == ghostX && playerY == ghostY;
    if (++ticks % 3 == 0 && !caught) {
        moveGhost();
    }
    caught|=playerX == ghostX && playerY == ghostY;
    state = afterContact(caught, remaining);
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(Graphics g) {
    int width = map[0].length() * TILE, height = map.length * TILE;
    g.setColor(new Color(16, 18, 38));
    g.fillRect(0, 0, width, height + 54);
    for (int y = 0; y < map.length; y++) {
        for (int x = 0; x < map[y].length(); x++) {
            if (!open(x, y)) {
                g.setColor(new Color(52, 78, 153));
                g.fillRoundRect(x * TILE + 2, y * TILE + 2, TILE - 4, TILE - 4, 8, 8);
            } else {
                if (pellets[y][x]) {
                    g.setColor(new Color(255, 219, 167));
                    g.fillOval(x * TILE + 12, y * TILE + 12, 6, 6);
                }
            }
        }
    }
    g.setColor(new Color(255, 222, 71));
    g.fillOval(playerX * TILE + 4, playerY * TILE + 4, TILE - 8, TILE - 8);
    g.setColor(new Color(255, 117, 173));
    g.fillRoundRect(ghostX * TILE + 4, ghostY * TILE + 4, TILE - 8, TILE - 8, 12, 12);
    g.setColor(Color.WHITE);
    g.fillOval(ghostX * TILE + 8, ghostY * TILE + 10, 5, 7);
    g.fillOval(ghostX * TILE + 17, ghostY * TILE + 10, 5, 7);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
    g.drawString("Dots left: " + remaining + "   " + (started ? state : "ARROWS TO START"), 10, height + 23);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Arrow keys: steer     R: restart", 10, height + 44);
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; časovače končí při
/// zavření okna.
void showWindow() {
    var frame = new JFrame("Mini Pacman - Maze Muncher");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(map[0].length() * TILE, map.length * TILE + 54));
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent event) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_LEFT -> direction(-1, 0);
                case KeyEvent.VK_RIGHT -> direction(1, 0);
                case KeyEvent.VK_UP -> direction(0, -1);
                case KeyEvent.VK_DOWN -> direction(0, 1);
                case KeyEvent.VK_R -> restart();
            }
        }
    });
    var timer = new Timer(130, event -> {
        step();
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent event) {
            timer.stop();
        }
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
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `'` — Jednoduché uvozovky ohraničují jeden character (znak), například značku na desce.
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
/// - `enum` — Type (typ) s pevnou sadou pojmenovaných hodnot, například READY, PLAYING a FINISHED.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
