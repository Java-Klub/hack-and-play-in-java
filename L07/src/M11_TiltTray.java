// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
import javax.swing.Timer;
// </editor-fold>

/// # L07.11 · TiltTray
///
/// Nakláněj podnos a doveď kuličku kolem dřevěných bloků do zeleného cíle. Přijet pomalu je těžší než se
/// cíle jen dotknout.
///
/// **Tvoje mise:** Boční srážka otočí rychlost, ale nechá kuličku uvnitř překážky, kde drnčí. Oprav
/// vodorovné oddělení v step().
///
/// **Dnes trénuješ:** Invariants (vždy platné podmínky): po vyřešení srážky se kulička na řešené ose už
/// nesmí překrývat s blokem. Samotná změna rychlosti neplatnou polohu neopraví.
///
/// Spusť Run File a šipkami nakláněj kuličku kolem bloků, přičemž před zeleným cílem přibrzdi.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F25 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
void step() {
    if (!started || won) {
        return;
    }
    vx += (held[KeyEvent.VK_RIGHT] ? .13 : 0) - (held[KeyEvent.VK_LEFT] ? .13 : 0);
    vy += (held[KeyEvent.VK_DOWN] ? .13 : 0) - (held[KeyEvent.VK_UP] ? .13 : 0);
    vx = Math.max(-3.5, Math.min(3.5, vx * .985));
    vy = Math.max(-3.5, Math.min(3.5, vy * .985));
    x += vx;
    for (var p : obstacles) {
        if (hit(p)) {
            x = x;
            vx *= -.3;
        }
    }
    y += vy;
    for (var p : obstacles) {
        if (hit(p)) {
            y = vy > 0 ? p.y - 10 : p.y + p.height + 10;
            vy *= -.3;
        }
    }
    if (x < 20) {
        x = 20;
        vx = Math.abs(vx) * .3;
    }
    if (x > 580) {
        x = 580;
        vx = -Math.abs(vx) * .3;
    }
    if (y < 20) {
        y = 20;
        vy = Math.abs(vy) * .3;
    }
    if (y > 400) {
        y = 400;
        vy = -Math.abs(vy) * .3;
    }
    if (Math.hypot(x - 530, y - 345)<19 && Math.hypot(vx, vy)<1.5) {
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
/// - `x += vx` — Posune vodorovnou polohu před testováním překážek.
/// - `if (hit(p))` — Ptá se připraveného geometrického dotazu, zda se tento blok překrývá s kuličkou.
/// - `x = x` — Přiřadí polohu zpět sobě samé, takže překryv zůstane nevyřešený.
/// - `vx *= -.3` — Otočí a zmenší vodorovnou rychlost, čímž napodobí nepružný odraz.
///
/// ### Co už obstarává připravená část
///
/// hit() už pomocí nejbližšího bodu testuje kruh proti obdélníku. step() řeší nejprve vodorovný a potom
/// svislý pohyb; zvlášť obsluhuje stěny podnosu a pomalý příjezd do cíle.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Samotné obrácení vx ponechá střed v již pronikající pozici.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Směr srážky určuje stranu, odkud kulička přijela.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Při pohybu doprava nastav x=p.x-10, doleva x=p.x+p.width+10; potom tlum a obrať vx.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 600, HEIGHT = 420;

Rectangle[] obstacles = {new Rectangle(180, 70, 40, 230), new Rectangle(350, 200, 130, 35)};
double x, y, vx, vy;
boolean started, won;
/// restart(): Obnovuje počáteční model Tilt Tray pro další pokus.
void restart() {
    x = 65;
    y = 65;
    vx = vy = 0;
    started = won = false;
}

/// hit(): Dodaná geometrie nejbližšího bodu ověřuje kruh proti obdélníkové překážce.
boolean hit(Rectangle p) {
    return Math.hypot(x - Math.max(p.x, Math.min(p.x + p.width, x)), y - Math.max(p.y, Math.min(p.y + p.height, y)))<10;
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(Graphics2D g) {
    g.setColor(new Color(235, 222, 190));
    g.fillRect(10, 10, 580, 400);
    g.setColor(new Color(110, 85, 60));
    for (var p : obstacles) {
        g.fill(p);
    }
    g.setColor(new Color(80, 180, 120));
    g.fillOval(502, 317, 56, 56);
    g.setColor(new Color(65, 100, 180));
    g.fillOval((int)x - 10, (int)y - 10, 20, 20);
    g.setColor(Color.BLACK);
    g.drawString(won ? "Balanced into the target! R: replay" : !started ? "Arrow keys tilt the tray" : "Brake with the opposite arrow; enter green slowly", 20, 35);
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
    if (pressed && (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN)) {
        started = true;
    }
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; časovače končí při
/// zavření okna.
void showWindow() {
    var frame = new JFrame("Tilt Tray");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Arrows: tilt / brake   Guide the ball slowly into green   R: restart</div></html>", SwingConstants.CENTER);
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

