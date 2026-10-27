// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L07.10 · SoftLanding
///
/// Veď přistávací modul k plošině a před dotykem zabrzdi. Omezená nádrž dělá z měkkého přistání plánovací
/// výzvu.
///
/// **Tvoje mise:** Prázdná nádrž stále pohání motory a palivo klesá pod nulu. Oprav podmínku tahu v
/// step() a zachovej běžnou gravitaci i kontrolu přistání.
///
/// **Dnes trénuješ:** Invariants (vždy platné podmínky): palivo neklesá pod nulu a prázdná nádrž
/// neposkytuje tah. Několik současně držených směrových kláves stále patří jednomu kroku simulace.
///
/// Spusť Run File, šipkou nahoru začni brzdit pád a tahem vlevo či vpravo se srovnej nad přistávací
/// plošinu.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F25 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
void step() {
    if (!state.equals("FLYING")) {
        return;
    }
    velocityY += 0.04;
    if (fuel > 0 && (left || right || up)) {
        if (left) {
            velocityX -= 0.06;
        }
        if (right) {
            velocityX += 0.06;
        }
        if (up) {
            velocityY -= 0.09;
        }
        fuel--;
    }
    x += velocityX;
    y += velocityY;
    if (x < HALF || x > WIDTH - HALF || y < HALF) {
        state = "CRASH";
        return;
    }
    if (y + HALF >= GROUND) {
        y = GROUND - HALF;
        boolean onPad = x - HALF >= PAD_X && x + HALF <= PAD_X + PAD_WIDTH;
        boolean gentle = Math.abs(velocityX) <= 1.2 && velocityY >= 0 && velocityY <= 1.5;
        state = onPad && gentle ? "LANDED!" : "CRASH";
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
/// - `left || right || up` — Libovolný požadovaný motor vstoupí do této společné větve tahu.
/// - `velocityY += 0.04` — Gravitace dál působí na modul i bez paliva.
/// - `fuel--` — Spotřebuje jednu jednotku za celou aktualizaci s tahem, nikoli za každou klávesu.
/// - `Math.abs(velocityX)` — Kontrolu přistání zajímá velikost vodorovné rychlosti v obou směrech.
///
/// ### Co už obstarává připravená část
///
/// Zbytek step() mění polohu a kontroluje, zda celý modul jemně dosedl uvnitř plošiny. clearKeys() uvolní
/// klávesy při restartu či ztrátě zaměření; časování a kreslení je připravené.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Gravitace působí i bez paliva.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Existující jediné snížení už správně řeší kombinované klávesy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Obal celý blok tahu podmínkou fuel>0; nestačí dodatečně omezit číslo.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 600, HEIGHT = 420, GROUND = 385, PAD_X = 380, PAD_WIDTH = 140, HALF = 12;
double x, y, velocityX, velocityY;
int fuel;
boolean left, right, up;
String state;

/// clearKeys(): Uvolňuje držený tah při restartu a ztrátě focus, aby vstup nezůstal zaseknutý.
void clearKeys() {
    left = right = up = false;
}

/// restart(): Obnovuje počáteční model Soft Landing pro další pokus.
void restart() {
    x = 150;
    y = 60;
    velocityX = 1.2;
    velocityY = 0;
    fuel = 240;
    state = "READY";
    clearKeys();
}

void begin() {
    if (state.equals("READY")) {
        state = "FLYING";
    }
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(Graphics g) {
    g.setColor(new Color(14, 20, 39));
    g.fillRect(0, 0, WIDTH, HEIGHT + 60);
    g.setColor(new Color(74, 79, 101));
    g.fillRect(0, GROUND, WIDTH, HEIGHT - GROUND);
    g.setColor(new Color(87, 226, 166));
    g.fillRect(PAD_X, GROUND, PAD_WIDTH, 8);
    g.setColor(Color.WHITE);
    g.drawString("LAND HERE", PAD_X + 34, GROUND + 25);
    if (state.equals("FLYING") && fuel > 0) {
        g.setColor(new Color(255, 164, 70));
        if (up) {
            g.fillPolygon(new int[]{(int) x - 7, (int) x, (int) x + 7},
            new int[]{(int) y + HALF, (int) y + 30, (int) y + HALF}, 3);
        }
        if (left) {
            g.fillRect((int) x + HALF, (int) y - 3, 12, 6);
        }
        if (right) {
            g.fillRect((int) x - HALF - 12, (int) y - 3, 12, 6);
        }
    }
    g.setColor(state.equals("CRASH") ? new Color(255, 115, 120) : new Color(126, 209, 255));
    g.fillRoundRect((int) x - HALF, (int) y - HALF, HALF * 2, HALF * 2, 6, 6);
    g.setColor(Color.WHITE);
    g.fillOval((int) x - 5, (int) y - 6, 10, 9);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
    g.drawString(state + "    Fuel: " + fuel, 14, HEIGHT + 22);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Speed X: %.2f   Down: %.2f   Safe: |X| <= 1.2, Down <= 1.5".formatted(velocityX, velocityY), 14, HEIGHT + 41);
    g.drawString("Arrows: thrust and start     Up: brake fall     R: restart", 14, HEIGHT + 57);
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; časovače končí při
/// zavření okna.
void showWindow() {
    var frame = new JFrame("Soft Landing");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT + 60));
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        void change(KeyEvent event, boolean pressed) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    left = pressed;
                    if (pressed) {
                        begin();
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    right = pressed;
                    if (pressed) {
                        begin();
                    }
                }
                case KeyEvent.VK_UP -> {
                    up = pressed;
                    if (pressed) {
                        begin();
                    }
                }
                case KeyEvent.VK_R -> {
                    if (pressed) {
                        restart();
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
        step();
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
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
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

