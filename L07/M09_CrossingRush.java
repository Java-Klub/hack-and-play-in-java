// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.Timer;
// </editor-fold>

/// # L07.09 · CrossingRush
///
/// Přeskakuj mezi jízdními pruhy a dosáhni zeleného břehu. Únik bokem z okna nemá být tajnou bezpečnou
/// cestou.
///
/// **Tvoje mise:** Opakované skoky do strany vynesou hráče mimo viditelnou plochu. Oprav hop(), aby
/// uvnitř bočních hranic zůstal celý hráč.
///
/// **Dnes trénuješ:** Invariants (vždy platné podmínky): každá přijatá poloha má držet celý tvar hráče
/// uvnitř plochy. Souřadnice středu nepředstavuje celý kolizní tvar.
///
/// Spusť program a šipkami přeskakuj mezi projíždějícími auty až k zelenému břehu nahoře.
void hop(int dx, int dy) {
    if (lost || won) {
        return;
    }
    started = true;
    x = Math.max(12, Math.min(WIDTH - 12, x + dx));
    y = Math.min(370, y + dy);
    collide();
    if (!lost && y < 45) {
        won = true;
    }
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F25">
/// ### Invariant: slib po každé akci
///
/// Invariant (trvale platné pravidlo) je vztah, který má přežít přijaté akce. Některá akce množství
/// pouze přesune mezi místy, nevytváří další. Před změnami ověř celý požadavek a potom jej proveď
/// jednou. Odmítnutí má zachovat původní state (stav).
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
/// Nádoby mají tři a pět; součet zůstane osm, protože přesun obě strany vyrovná. Potom zvaž devět
/// jednotek: co musí zůstat beze změny? Vysvětli, proč změna jediné strany poruší součet, přestože
/// jednotlivé hodnoty mohou působit platně.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `if (lost || won)` — Dokončená kola odmítají další pohyb.
/// - `x = x + dx` — Zapisuje vodorovnou polohu; navržený výsledek musí dodržovat pravidlo hranic.
/// - `Math.min(370, y + dy)` — Existující mez omezuje pohyb na spodním okraji.
/// - `collide()` — Kontroluje dopravu až po výběru nové polohy hráče.
///
/// ### Co už obstarává připravená část
///
/// collide() porovnává hráče se stejnými obdélníky aut, které používá kreslení. Připravený timer
/// (časovač) posouvá dopravu; hop() řeší vstup hráče a zastavení po výhře či prohře.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Okraj podle poloměru hráče je 12 pixelů.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Po každém aktivním skoku má platit 12 <= x <= WIDTH-12.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Omez x+dx mezi tyto hranice; ponech kolize i kontrolu konce kola.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 600, HEIGHT = 420;

record Car(double x, int lane, double speed) {
}

ArrayList<Car> cars = new ArrayList<>();
int x, y;
boolean started, lost, won;
/// restart(): Obnovuje počáteční model Crossing Rush pro další pokus.
void restart() {
    x = 300;
    y = 370;
    started = lost = won = false;
    cars.clear();
    for (int lane = 0; lane < 5; lane++) {
        for (int n = 0; n < 3; n++) {
            cars.add(new Car(n * 230 + lane * 47, lane, (lane % 2 == 0 ? 1 : -1) * (1.3 + lane * .28)));
        }
    }
}

/// collide(): Porovnává box hráče se stejnými obdélníky aut jako při kreslení.
void collide() {
    for (var car : cars) {
        if (new Rectangle2D.Double(car.x, 65 + car.lane * 50, 65, 32).intersects(x - 11, y - 11, 22, 22)) {
            lost = true;
        }
    }
}

void step() {
    if (!started || lost || won) {
        return;
    }
    for (int i = 0; i < cars.size(); i++) {
        var c = cars.get(i);
        double xx = c.x + c.speed;
        if (xx > WIDTH + 10) {
            xx = -75;
        }
        if (xx<-75) {
            xx = WIDTH + 10;
        }
        cars.set(i, new Car(xx, c.lane, c.speed));
    }
    collide();
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(Graphics2D g) {
    g.setColor(new Color(100, 180, 120));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    for (int lane = 0; lane < 5; lane++) {
        g.setColor(new Color(65, 70, 80));
        g.fillRect(0, 60 + lane * 50, WIDTH, 44);
    }
    for (var c : cars) {
        g.setColor(c.lane % 2 == 0 ? Color.ORANGE : Color.CYAN);
        g.fillRect((int)c.x, 65 + c.lane * 50, 65, 32);
    }
    g.setColor(lost ? Color.RED : Color.YELLOW);
    g.fillOval(x - 11, y - 11, 22, 22);
    g.setColor(Color.BLACK);
    g.drawString(won ? "Across safely! R: another run" : lost ? "Traffic got you! R: retry" : !started ? "Arrow keys: hop to the green goal" : "Five lanes; choose your moment", 12, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_LEFT -> hop(-30, 0);
            case KeyEvent.VK_RIGHT -> hop(30, 0);
            case KeyEvent.VK_UP -> hop(0, -50);
            case KeyEvent.VK_DOWN -> hop(0, 50);
        }
    }
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; časovače končí při
/// zavření okna.
void showWindow() {
    var frame = new JFrame("Crossing Rush");
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
    var timer = new Timer(20, e -> {
        step();
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent e) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Arrows: hop   Reach the top green bank   R: restart</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
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
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
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
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
