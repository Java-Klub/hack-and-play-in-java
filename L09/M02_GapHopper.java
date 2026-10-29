// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L09.02 · GapHopper
///
/// Udrž malého letce mezi pohybujícími se trubkami. Překážky mají tvořit nekonečnou trať, ne postupně
/// zmizet za zády.
///
/// **Tvoje mise:** Obnovená trubka se vloží na chybný konec a postupně rozbije trať. Obnov pořadí
/// překážek beze změny pravidel letu.
///
/// **Dnes trénuješ:** List (seznam) může vyjadřovat pořadí zleva doprava, nejen hromadu objektů. Odebrání
/// první zmizelé trubky i vložení náhrady musí tento význam zachovat.
///
/// Po spuštění programu udržuj letce mezi trubkami pomocí Space nebo klikání a po nárazu začni
/// znovu klávesou R.
void recyclePipes() {
    if (!pipes.isEmpty() && pipes.getFirst().x + PIPE_WIDTH < 0) {
        double furthest = pipes.getLast().x;
        pipes.removeFirst();
        pipes.add(new Pipe(furthest + 260, newGap()));
    }
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F22">
/// ### List (seznam): pořadí, které můžeš měnit
///
/// `List<String>` uchovává texty v pořadí. `new ArrayList<>()` vytvoří mutable list (měnitelný
/// seznam): obsah lze měnit bez nahrazení samotného seznamu.
///
/// ```java
/// List<String> snacks = new ArrayList<>();
/// snacks.add("apple");
/// snacks.add("nuts");
/// IO.println(snacks.get(0));
/// IO.println(snacks.size());
/// ```
///
/// Výstup je `apple`, potom `2`. `add(value)` připojí jeden prvek na konec; `get(index)` přečte prvek
/// bez odebrání. Indexes (indexy) začínají nulou. `isEmpty()` zjišťuje, zda seznam nemá prvky.
/// `removeLast()` odebere a vrátí poslední prvek; volej jej jen u neprázdného seznamu. Seznam přímo z
/// `List.of(...)` měnit nelze, ale `new ArrayList<>(List.of(...))` měnit lze.
///
/// Předpověz: po `snacks.removeLast()` jaká bude velikost a první prvek?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `!pipes.isEmpty()` — Znak ! obrátí odpověď: pokračuj jen tehdy, když první trubka existuje.
/// - `pipes.getLast().x` — Přečte pozici poslední trubky, aniž by ji odstranil.
/// - `pipes.removeFirst()` — Odstraní první zmizelou trubku a posune zbývající pořadí.
/// - `pipes.addFirst` — Vloží na index (pořadové číslo) nula; porovnej tento konec s novou pozicí úplně
///   vpravo.
///
/// ### Co už obstarává připravená část
///
/// Každý step posune trubky a ověří body i srážky před voláním recyclePipes. NewGap vybere omezený otvor;
/// připravené okno obslouží skok, restart a úklid timer (časovače).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Nejstarší překážka má index (pořadové číslo) nula.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Před odstraněním prvního element (prvku) přečti pozici posledního.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Remove a add zachovají pořadí dvou překážek; List neměň během dřívějšího for-each.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
static class Pipe {
    double x;
    int gapY;
    boolean scored;
    Pipe(double x, int gapY) {
        this.x = x;
        this.gapY = gapY;
    }
}

final int WIDTH = 500, HEIGHT = 400, BIRD_X = 110, RADIUS = 10, PIPE_WIDTH = 55, GAP = 130;
final Random random = new Random();
final java.util.List<Pipe> pipes = new ArrayList<>(java.util.List.of(new Pipe(340, 200), new Pipe(600, 200)));
double birdY, velocityY;
int score;
boolean running, lost, spaceDown;

int newGap() {
    return 100 + random.nextInt(201);
}

/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
void restart() {
    birdY = 180;
    velocityY = 0;
    score = 0;
    running = lost = spaceDown = false;
    for (int index = 0; index < pipes.size(); index++) {
        pipes.get(index).x = 340 + index * 260;
        pipes.get(index).gapY = newGap();
        pipes.get(index).scored = false;
    }
}

void flap() {
    if (lost) return;
    running = true;
    velocityY = -5.5;
}

/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
void step() {
    if (!running || lost) return;
    velocityY += 0.28;
    birdY += velocityY;
    if (birdY - RADIUS < 0 || birdY + RADIUS > HEIGHT) lost = true;
    for (Pipe pipe : pipes) pipe.x -= 2.5;
    for (Pipe pipe : pipes) {
        boolean overlap = BIRD_X + RADIUS > pipe.x && BIRD_X - RADIUS < pipe.x + PIPE_WIDTH;
        if (overlap && (birdY - RADIUS < pipe.gapY - GAP / 2
                || birdY + RADIUS > pipe.gapY + GAP / 2)) lost = true;
        if (!pipe.scored && pipe.x + PIPE_WIDTH < BIRD_X - RADIUS) {
            pipe.scored = true;
            score++;
        }

    }
    recyclePipes();
}

/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
void draw(Graphics g) {
    g.setColor(new Color(31, 51, 82));
    g.fillRect(0, 0, WIDTH, HEIGHT + 44);
    for (Pipe pipe : pipes) {
        g.setColor(new Color(74, 197, 158));
        g.fillRect((int) pipe.x, 0, PIPE_WIDTH, pipe.gapY - GAP / 2);
        g.fillRect((int) pipe.x, pipe.gapY + GAP / 2, PIPE_WIDTH, HEIGHT - pipe.gapY - GAP / 2);
        g.setColor(new Color(134, 239, 176));
        g.fillRect((int) pipe.x, pipe.gapY - GAP / 2 - 10, PIPE_WIDTH, 10);
        g.fillRect((int) pipe.x, pipe.gapY + GAP / 2, PIPE_WIDTH, 10);
    }
    g.setColor(new Color(255, 218, 87));
    g.fillOval(BIRD_X - RADIUS, (int) birdY - RADIUS, RADIUS * 2, RADIUS * 2);
    g.setColor(Color.BLACK);
    g.fillOval(BIRD_X + 3, (int) birdY - 5, 4, 4);
    g.setColor(Color.WHITE);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
    String status = lost ? "CRASH! Press R" : running ? "Score: " + score : "SPACE OR CLICK TO START";
    g.drawString(status, 16, HEIGHT + 22);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Space / click: flap     R: restart", 16, HEIGHT + 40);
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Gap Hopper");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT + 44));
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_R) restart();
            if (event.getKeyCode() == KeyEvent.VK_SPACE && !spaceDown) {
                spaceDown = true;
                flap();
            }
        }
        @Override public void keyReleased(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_SPACE) spaceDown = false;
        }
    });
    panel.addMouseListener(new MouseAdapter() {
        @Override public void mousePressed(MouseEvent event) { flap(); }
    });
    var timer = new Timer(20, event -> {
/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
        step();
        panel.repaint();
    });
    frame.addWindowFocusListener(new WindowAdapter() {
        @Override public void windowLostFocus(WindowEvent event) { spaceDown = false; }
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
/// - `double` — Numeric type (číselný typ) pro přibližná desetinná čísla. Java používá desetinnou tečku.
/// - `boolean` — Hodnota true nebo false: ano nebo ne.
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
///   se ale dál měnit může.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
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
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `class` — Definuje pojmenovaný obal pro uložené hodnoty a method (metody). Zde Pipe sdružuje stav
///   jedné překážky.
/// - `static` — Patří ke class (třídě), nikoli k jednomu konkrétnímu objektu hry. Často je v připraveném
///   test main.
/// - `public` — Zpřístupňuje tuto declaration (deklaraci) dalšímu kódu.
/// - `protected` — Visibility keyword (klíčové slovo přístupnosti) používané připraveným library callback
///   (knihovním zpětným voláním), například paintComponent.
/// - `super` — V super.paintComponent(g) požádá připravenou parent implementation (nadřazenou
///   implementaci), aby nejprve vykreslila běžné pozadí.
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
