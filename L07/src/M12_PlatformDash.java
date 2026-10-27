// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
import javax.swing.Timer;
// </editor-fold>

/// # L07.12 · PlatformDash
///
/// Běhej a skákej přes několik plošin k vlajce. Průskok plošinou směrem nahoru a pozdější dopad na ni má
/// působit přirozeně.
///
/// **Tvoje mise:** Plošiny přitahují hráče na svůj vršek i v nesprávné části skoku. Oprav dopadovou větev
/// step(), aby přijímala jen skutečné překročení horní hrany.
///
/// **Dnes trénuješ:** Invariants (vždy platné podmínky): dopad musí souhlasit se směrem i předchozí
/// polohou, nestačí překryv v novém snímku. Zachovej rozdíl mezi stoupáním, pádem a stáním.
///
/// Spusť Run File, běhej šipkami vlevo a vpravo a mezerníkem skákej směrem k plošinám a vlajce.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F25 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
void step() {
    if (!started || won) {
        return;
    }
    x += (held[KeyEvent.VK_RIGHT] ? 3.4 : 0) - (held[KeyEvent.VK_LEFT] ? 3.4 : 0);
    x = Math.max(0, Math.min(WIDTH - 20, x));
    double oldBottom = y + 28;
    vy += .25;
    y += vy;
    grounded = false;
    if (vy >= 0) {
        for (var p : platforms) {
            if (x + 20 > p.x && x < p.x + p.width && oldBottom <= p.y && y + 28 >= p.y) {
                y = p.y - 28;
                vy = 0;
                grounded = true;
                break;
            }
        }
    }
    if (grounded && x > 495 && y < 200) {
        won = true;
    }
    if (y > HEIGHT) {
        falls++;
        attempt();
        clearKeys();
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
/// - `double oldBottom = y + 28` — Pamatuje polohu chodidel před svislým pohybem této aktualizace.
/// - `vy += .25` — Přidá gravitaci ke svislé rychlosti před změnou polohy.
/// - `x + 20 > p.x && x < p.x + p.width` — Testuje vodorovný překryv hráče s jednou plošinou.
/// - `grounded = true` — Označuje skutečný dopad, který dovolí další skok.
///
/// ### Co už obstarává připravená část
///
/// jump() poskytuje rychlost vzhůru jen při stání. Zbytek step() zpracuje vstup a gravitaci, zkontroluje
/// vlajku a obnoví pokus po pádu; kreslení používá stejné obdélníky plošin.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Landing (přistání) je překročení, ne pouhý překryv
///
/// Screen y (svislá souřadnice obrazovky) roste dolů, takže kladná svislá rychlost znamená pád.
/// Porovnej původní dolní hranu, novou dolní hranu a vršek plošiny. V jiném příkladu hrana
/// přecházející z 30 na 38 překročí povrch v 35, ale hrana původně pod 35 jej v tomto kroku
/// nepřekročí. Předpověz skok vzhůru skrz plošinu. Vodorovný překryv je nutný, ale neurčí stranu, ze
/// které hráč přišel.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Současný překryv neříká, ze které strany hráč přišel.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Porovnej oldBottom s horní hranou plošiny před pohybem.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Vyžaduj vy>=0 a oldBottom<=p.y spolu s novým spodkem překračujícím p.y.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 600, HEIGHT = 420;

Rectangle[] platforms = {new Rectangle(30, 360, 150, 14), new Rectangle(220, 280, 130, 14), new Rectangle(400, 200, 150, 14)};
double x, y, vy;
boolean started, grounded, won;
int falls;
void attempt() {
    x = 60;
    y = 332;
    vy = 0;
    grounded = true;
    started = false;
}

/// restart(): Obnovuje počáteční model Platform Dash pro další pokus.
void restart() {
    falls = 0;
    won = false;
    attempt();
}

/// jump(): Nastaví vzestupnou rychlost jen na podložce; ve vzduchu znovu skákat nelze.
void jump() {
    if (!won && grounded) {
        started = true;
        grounded = false;
        vy = -8.8;
    }
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(Graphics2D g) {
    g.setColor(new Color(225, 240, 255));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(new Color(55, 100, 85));
    for (var p : platforms) {
        g.fill(p);
    }
    g.setColor(Color.ORANGE);
    g.fillRect(520, 160, 8, 40);
    g.fillPolygon(new int[]{528, 555, 528}, new int[]{160, 170, 180}, 3);
    g.setColor(new Color(60, 80, 200));
    g.fillRect((int)x, (int)y, 20, 28);
    g.setColor(Color.BLACK);
    g.drawString(won ? "Goal reached! R: replay" : "Falls: " + falls + (!started ? " - press an arrow or Space" : ""), 15, 25);
}

boolean[] held = new boolean[512];
void clearKeys() {
    Arrays.fill(held, false);
}

void key(int code, boolean pressed) {
    if (code >= 0 && code < held.length) {
        held[code] = pressed;
    }
    if (pressed && code == KeyEvent.VK_R) {
        restart();
        clearKeys();
    }
    if (pressed && (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT)) {
        started = true;
    }
    if (pressed && code == KeyEvent.VK_SPACE) {
        jump();
    }
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; časovače končí při
/// zavření okna.
void showWindow() {
    var frame = new JFrame("Platform Dash");
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
        @Override public void keyReleased(KeyEvent e) {
            key(e.getKeyCode(), false);
        }
    });
    var timer = new Timer(20, e -> {
        step();
        panel.repaint();
    });
    frame.addWindowFocusListener(new WindowAdapter() {
        @Override public void windowLostFocus(WindowEvent e) {
            clearKeys();
        }
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent e) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Left / Right: run   Space: jump   Reach the flag   R: restart</div></html>", SwingConstants.CENTER);
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
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
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

