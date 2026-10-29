// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import module java.base;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.Timer;
// </editor-fold>

class M01_AsteroidDrift {
    /// # L10.01 · AsteroidDrift
    ///
    /// Řiď malou loď mezi letícími asteroidy a střelbou si uvolňuj cestu. Ovládání palby ztichlo, přestože
    /// ostatní let stále funguje.
    ///
    /// **Tvoje mise:** Držení Space nevytváří střely, protože uložená akce palby nic nedělá. Připoj odloženou
    /// akci, ale nestřílej už při jejím vytváření.
    ///
    /// **Dnes trénuješ:** Callback (předaná akce) je kód připravený pro pozdější event (událost).
    /// FiringAction vrací tento balíček; herní loop (cyklus) rozhoduje, kdy jej při držení Space spustí.
    ///
    /// Z hlavní složky obsahující README.md:
    /// - Hraní: `java L10/M01_AsteroidDrift.java`
    /// - Kontrola této mise: `java L10/Check.java M01_AsteroidDrift.java`
    ///
    /// Po spuštění programu řiď a zrychluj šipkami, drž Space pro střelbu a klávesou R začni let znovu.
    /// Po opravě: ulož soubor, spusť uvedenou kontrolu a zahraj si znovu.
    ///
    /// Potřebuješ výklad? Nová dovednost · F28 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
    /// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
    Runnable firingAction() {
        return this::fire;
    }

    // <editor-fold defaultstate="expanded" desc="Nová dovednost · F28">
    /// ### Callback (zpětné volání): akce na později
    ///
    /// Callback je akce, kterou jiná část programu zavolá později, třeba po kliknutí. `Runnable` je akce
    /// bez předávaných argumentů a bez vraceného výsledku.
    ///
    /// ```java
    /// Runnable cheer = () -> IO.println("Hooray!");
    /// IO.println("Ready");
    /// cheer.run();
    /// ```
    ///
    /// Výstup je `Ready`, potom `Hooray!`. Vytvoření lambda (anonymní funkce) zápisem `() -> ...` její
    /// tělo nespustí. To udělá volání `run()`. Složené závorky dovolují uvnitř callbacku několik příkazů.
    /// Method reference (odkaz na metodu) jako `this::wave` obdobně předá metodu `wave` tohoto objektu
    /// pro pozdější použití; `wave()` ji zavolá hned. Připravené UI rozhoduje, kdy callback vyvolat.
    ///
    /// Předpověz: co se vypíše, když poslední řádek nahradí dvě po sobě jdoucí volání `cheer.run()`?
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
    /// ## Jak číst právě tyto řádky
    ///
    /// - `Runnable` — JDK role pro akci bez argument (argumentů) a bez vráceného výsledku.
    /// - `firingAction()` — Vytváří akci; název neznamená, že má střela vzniknout okamžitě.
    /// - `() -> {}` — Lambda (anonymní funkce) bez parameter (parametrů) a s prázdným tělem: nyní nic
    ///   nezmění.
    ///
    /// ### Co už obstarává připravená část
    ///
    /// Připravená fire method (metoda) řeší prodlevu i vznik střely. Update (aktualizace) při držení klávesy
    /// spouští fireCallback a potom posouvá střely i asteroidy; Swing okno obslouží klávesy a při zavření
    /// zastaví timer (časovač).
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
    /// Runnable představuje akci, nikoli její výsledek.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
    /// Vrať method reference (odkaz na metodu) nebo lambda, která fire zavolá později.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
    /// Zachovej guard (podmínky) cooldown/lost v fire; nekopíruj je do callback (předané akce).
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
    final int WIDTH = 600, HEIGHT = 420;

    record Rock(double x, double y, double vx, double vy, double radius) {
    }

    record Shot(double x, double y, double vx, double vy, int life) {
    }

    ArrayList<Rock> rocks = new ArrayList<>();
    ArrayList<Shot> shots = new ArrayList<>();
    double x, y, vx, vy, angle;
    int cooldown;
    boolean started, lost;
    /// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
    void restart() {
        x = WIDTH / 2;
        y = HEIGHT / 2;
        vx = vy = 0;
        angle = -Math.PI / 2;
        cooldown = 0;
        started = lost = false;
        rocks.clear();
        shots.clear();
        for (int i = 0; i < 5; i++) {
            rocks.add(new Rock(40 + i * 115, 40, .6 + i * .1, .45, 19));
        }
    }

    double wrap(double value, double size) {
        return (value % size + size) % size;
    }

    void fire() {
        if (cooldown != 0 || lost || rocks.isEmpty()) {
            return;
        }
        started = true;
        shots.add(new Shot(x + Math.cos(angle) * 15, y + Math.sin(angle) * 15,
        vx + Math.cos(angle) * 6, vy + Math.sin(angle) * 6, 70));
        cooldown = 10;
    }

    /// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
    void step() {
        if (!started || lost || rocks.isEmpty()) {
            return;
        }
        if (held[KeyEvent.VK_LEFT]) {
            angle -= .07;
        }
        if (held[KeyEvent.VK_RIGHT]) {
            angle += .07;
        }
        if (held[KeyEvent.VK_UP]) {
            vx += Math.cos(angle) * .09;
            vy += Math.sin(angle) * .09;
        }
        double speed = Math.hypot(vx, vy);
        if (speed > 5) {
            vx *= 5 / speed;
            vy *= 5 / speed;
        }
        x = wrap(x + vx, WIDTH);
        y = wrap(y + vy, HEIGHT);
        if (cooldown > 0) {
            cooldown--;
        }
        if (held[KeyEvent.VK_SPACE]) {
            fireCallback.run();
        }
        for (int i = 0; i < rocks.size(); i++) {
            var r = rocks.get(i);
            rocks.set(i, new Rock(wrap(r.x + r.vx, WIDTH), wrap(r.y + r.vy, HEIGHT), r.vx, r.vy, r.radius));
        }
        for (int i = shots.size() - 1; i >= 0; i--) {
            var s = shots.get(i);
            s = new Shot(wrap(s.x + s.vx, WIDTH), wrap(s.y + s.vy, HEIGHT), s.vx, s.vy, s.life - 1);
            boolean hit = false;
            for (int j = rocks.size() - 1; j >= 0; j--) {
                var r = rocks.get(j);
                if (Math.hypot(s.x - r.x, s.y - r.y) < r.radius + 2) {
                    rocks.remove(j);
                    hit = true;
                    break;
                }
            }
            if (hit || s.life <= 0) {
                shots.remove(i);
            }
            else {
                shots.set(i, s);
            }
        }
        for (var r : rocks) {
            if (Math.hypot(x - r.x, y - r.y) < r.radius + 8) {
                lost = true;
            }
        }
    }

    /// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
    void draw(Graphics2D g) {
        g.setColor(new Color(16, 22, 38));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.LIGHT_GRAY);
        for (var r : rocks) {
            g.drawOval((int)(r.x - r.radius), (int)(r.y - r.radius), (int)(2 * r.radius), (int)(2 * r.radius));
        }
        g.setColor(Color.ORANGE);
        for (var s : shots) {
            g.fillOval((int)s.x - 2, (int)s.y - 2, 4, 4);
        }
        var ship = new Path2D.Double();
        for (int i = 0; i < 3; i++) {
            double a = angle + (i == 0 ? 0 : i == 1 ? 2.5 : -2.5);
            double px = x + Math.cos(a) * 13, py = y + Math.sin(a) * 13;
            if (i == 0) {
                ship.moveTo(px, py);
            }
            else {
                ship.lineTo(px, py);
            }
        }
        ship.closePath();
        g.setColor(lost ? Color.RED : Color.CYAN);
        g.draw(ship);
        g.setColor(Color.WHITE);
        g.drawString(!started ? "Press an arrow or Space to launch" : lost ? "Ship lost! R: retry" : rocks.isEmpty() ? "All five cleared! R: replay" : "Rocks: " + rocks.size(), 15, 22);
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
    /// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
            restart();
            clearKeys();
        }
        if (pressed && (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_UP || code == KeyEvent.VK_SPACE)) {
            started = true;
        }
    }

    /// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
    void showWindow() {
        var frame = new JFrame("Asteroid Drift");
        var panel = new JPanel() {
            @Override protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                var g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    /// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
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
    /// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
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
        var help = new JLabel("<html><div style=\"text-align:center\">Arrows: turn / thrust   Space: fire   R: restart</div></html>", SwingConstants.CENTER);
        help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        frame.add(help, BorderLayout.SOUTH);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        panel.requestFocusInWindow();
        timer.start();
    }

    Runnable fireCallback = firingAction();

    /// Program spusť uvedeným command (příkazem) pro hraní. Kontrola hry ověří pravidlo bez otevření
    /// okna. Připravená část zajišťuje pouze uvedenou hlavní hru nebo pokus.
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
    /// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
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
    /// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
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
    /// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
    ///   složky a závorky ().
    /// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
    /// - `JFrame` — Okno dodané JDK knihovnou Swing.
    /// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
    /// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
    /// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
    ///   se provede později.
    // </editor-fold>

}
