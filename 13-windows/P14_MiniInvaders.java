// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # 13.14 · MiniInvaders
///
/// Loď už bojuje poctivě: jedna střela zničí jednoho mimozemšťana. Uprav zbraň na řízený průrazný výboj
/// pro dva cíle; nesmí se změnit v nekonečný vrtací laser.
///
/// **Tvoje mise:** Každý nový výstřel musí přežít první odlišný zásah a skončit po druhém. Každý
/// mimozemšťan boduje jednou; spotřebovaná střela neboduje a nový výstřel má čerstvou zásobu dvou
/// zásahů. Zachovej pohyb, míření, konec a restart. Nejdřív sleduj běžnou jednorázovou životnost a
/// potom najdi vznik i konec střely.
///
/// **Dnes trénuješ:** Nahraď jeden invariant (trvale platné pravidlo) jiným: výboj má omezenou zásobu
/// zásahů a její spotřeba musí odpovídat životnosti střely.
///
/// Řiď Left/Right a střílej Space; R začíná znovu. Miř do svislého sloupce. Před úpravou výstřel skončí
/// na prvním mimozemšťanovi; potom sleduj, jak stejná střela odstraní druhého a zmizí.

// <editor-fold defaultstate="expanded" desc="Program · prozkoumej pravidla">
void step() {
    if (!state.equals("PLAY")) {
        return;
    }
    shipX = Math.max(16, Math.min(WIDTH - 16, shipX + (right ? 5 : 0) - (left ? 5 : 0)));
    if (fire && !bulletActive) {
        bulletActive = true;
        hitsLeft = 2;
        bulletX = shipX;
        bulletY = SHIP_Y - 14;
    }
    if (bulletActive) {
        bulletY -= 7;
        if (bulletY < -10) {
            bulletActive = false;
        }
    }
    int minColumn = 5, maxColumn = -1;
    for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 5; column++) {
            if (!aliens[row][column]) {
                continue;
            }
            minColumn = Math.min(minColumn, column);
            maxColumn = Math.max(maxColumn, column);
        }
    }
    if (maxColumn == -1) {
        state = "YOU WIN";
        return;
    }
    formationX += speedX;
    double minimum = -minColumn * 60, maximum = WIDTH - maxColumn * 60 - 28;
    if (formationX < minimum || formationX > maximum) {
        formationX = Math.max(minimum, Math.min(maximum, formationX));
        speedX = -speedX;
        formationY += 18;
    }
    hit : for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 5; column++) {
            double x = formationX + column * 60, y = formationY + row * 34;
            if (aliens[row][column] && bulletActive && bulletX >= x && bulletX <= x + 28
            && bulletY <= y + 18 && bulletY + 10 >= y) {
                hitAlien(row, column);
                break hit;
            }
        }
    }
    if (countAliens() == 0) {
        state = "YOU WIN";
    }
    for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 5; column++) {
            if (aliens[row][column] && formationY + row * 34 + 18 >= SHIP_Y - 12) {
                state = "INVASION";
            }
        }
    }
}

void hitAlien(int row, int column) {
    if (!bulletActive || hitsLeft <= 0 || !aliens[row][column]) return;
    aliens[row][column] = false;
    score++;
    hitsLeft--;
    if (hitsLeft == 0) bulletActive = false;
}

final int WIDTH = 600, HEIGHT = 420, SHIP_Y = 388;
final boolean[][] aliens = new boolean[3][5];
double shipX, formationX, formationY, speedX, bulletX, bulletY;
boolean left, right, fire, bulletActive;
int score;
int hitsLeft;
String state;

void clearKeys() {
    left = right = fire = false;
}

/// restart(): Obnovuje počáteční model Mini Invaders pro další pokus.
void restart() {
    for (var row : aliens) {
        java.util.Arrays.fill(row, true);
    }
    shipX = WIDTH / 2.0;
    formationX = 60;
    formationY = 45;
    speedX = 1.2;
    bulletActive = false;
    score = 0;
    hitsLeft = 0;
    state = "PLAY";
    clearKeys();
}

/// countAliens(): Počítá právě živé buňky nezávisle na počítadle skóre.
int countAliens() {
    int count = 0;
    for (var row : aliens) {
        for (boolean alive : row) {
            if (alive) {
                count++;
            }
        }
    }
    return count;
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(Graphics g) {
    g.setColor(new Color(12, 20, 34));
    g.fillRect(0, 0, WIDTH, HEIGHT + 42);
    g.setColor(new Color(39, 51, 72));
    for (int x = 15; x < WIDTH; x += 47) {
        for (int y = 15; y < HEIGHT; y += 61) {
            g.fillRect(x, y, 2, 2);
        }
    }
    g.setColor(new Color(130, 237, 151));
    for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 5; column++) {
            if (!aliens[row][column]) {
                continue;
            }
            int x = (int) formationX + column * 60, y = (int) formationY + row * 34;
            g.fillRect(x, y, 28, 18);
            g.clearRect(x + 6, y + 5, 4, 4);
            g.clearRect(x + 18, y + 5, 4, 4);
        }
    }
    g.setColor(new Color(82, 201, 250));
    g.fillPolygon(new int[]{(int) shipX - 15, (int) shipX, (int) shipX + 15},
    new int[]{SHIP_Y + 12, SHIP_Y - 12, SHIP_Y + 12}, 3);
    if (bulletActive) {
        g.setColor(new Color(255, 222, 93));
        g.fillRect((int) bulletX - 2, (int) bulletY, 4, 10);
    }
    g.setColor(Color.WHITE);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
    g.drawString("Hits: " + score + "/15    " + state, 14, HEIGHT + 18);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Left / Right: move     Space: fire     R: restart", 14, HEIGHT + 36);
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; časovače končí při
/// zavření okna.
void showWindow() {
    var frame = new JFrame("Mini Invaders - Last Row");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT + 42));
    var status = new JLabel("Left / Right / Space: start   Clear all aliens before they descend   R: restart");
    frame.add(status, BorderLayout.NORTH);
    panel.setFocusable(true);
    boolean[] started = {false};
    panel.addKeyListener(new KeyAdapter() {
        void change(KeyEvent event, boolean pressed) {
            if (pressed && (event.getKeyCode() == KeyEvent.VK_LEFT
                    || event.getKeyCode() == KeyEvent.VK_RIGHT || event.getKeyCode() == KeyEvent.VK_SPACE)) {
                started[0] = true;
            }
            switch (event.getKeyCode()) {
                case KeyEvent.VK_LEFT -> left = pressed;
                case KeyEvent.VK_RIGHT -> right = pressed;
                case KeyEvent.VK_SPACE -> fire = pressed;
                case KeyEvent.VK_R -> {
                    if (pressed) {
                        restart();
                        started[0] = false;
                    }
                }
            }
        }
        @Override public void keyPressed(KeyEvent event) {
            change(event, true);
        }
        @Override public void keyReleased(KeyEvent event) {
            change(event, false);
        }
    });
    var timer = new Timer(20, event -> {
        if (started[0]) {
            step();
        }
        panel.repaint();
    });
    frame.addWindowFocusListener(new WindowAdapter() {
        @Override public void windowLostFocus(WindowEvent event) {
            clearKeys();
        }
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


// <editor-fold defaultstate="collapsed" desc="Připomenutí · F25">
/// ### Invariant (trvale platné pravidlo) přežije akci
///
/// Invariant je slib o platném state (stavu). Platí před tahem i po něm, ne až na konci hry. Představ
/// si přesun svítících kamínků mezi nádobami:
///
/// ```java
/// int violet = 6;
/// int gold = 2;
/// int amount = 3;
/// if (amount >= 0 && amount <= violet) {
///     violet -= amount;
///     gold += amount;
/// }
/// IO.println(violet + gold);
/// ```
///
/// Trace (průchod): z `(6, 2)` vznikne `(3, 5)`, součet ve výpisu zůstane 8. Oba assignment
/// (přiřazovací příkazy) patří k jedné přijaté akci. Změna jediné nádoby by kamínky tiše vytvářela nebo
/// mazala. Požadavek 9 neprojde guard (vstupní podmínkou) a nezmění ani jednu nádobu.
///
/// Nejdřív napiš slib: součet zůstává stejný, zásoba neklesá pod nulu nebo tvar nezůstane ve zdi. Potom
/// vyjmenuj všechny field (sdílené proměnné), které musí akce změnit společně. Předpověz obě nádoby pro
/// amount 0 a 7. Ve vlastní variantě smíš vymyslet jiná pravidla; musíš ale říct, jaký invariant pro ně
/// platí.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ### Pozoruj a sleduj tok
///
/// Ber projectile (střelu) jako krátký příběh: vznik, pohyb, přijatý dotyk a konec. Score (body) vzniká
/// ze skutečného dotyku, nikoli z tick (kroku časovače). Odstraněný cíl už znovu zasáhnout nejde.
/// Nejdřív najdi tyto události podle účinku a rozhodni, který state (údaj o stavu) má patřit aktuální
/// střele. Counter (počítadlo) vyjádří omezenou zásobu bez změny geometrie formace.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pokus a opakování">
/// Pro srovnání před a po použij stejný sloupec. Zapiš, zda střela přežije jeden zásah, dva zásahy a
/// minutí. Ověř jeden bod za odstraněný cíl a to, že restart nepřenese napůl spotřebovanou střelu do
/// další bitvy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Jediný nesmrtelný boolean (údaj ano/ne) nevyjádří právě dva dotyky. Popiš zásobu před každým
/// přijatým zásahem i po něm.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Najdi vznik střely ze vstupu a akci při dotyku mimozemšťana. Čerstvá zásoba patří ke vzniku,
/// spotřeba k dotyku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Střela vzniká ve step a dotyk zpracovává hitAlien. Přidej counter (počítadlo) pro aktuální střelu,
/// při výstřelu jej obnov, ubírej jen za živý cíl a při nule střelu ukonči. Restart tento state (stav)
/// vynuluje.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta · dobrovolně">
/// Navrhni třízásahový náboj vykoupený dlouhou pauzou před dalším výstřelem. Rozhodni, jak hráč uvidí
/// cenu této výhody místo pouhého zesílení zbraně.
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
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
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
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
