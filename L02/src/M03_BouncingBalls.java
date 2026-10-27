// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;
// </editor-fold>

/// # L02.03 · BouncingBalls
///
/// Vhoď do místnosti několik barevných míčků a sleduj, jak jejich pohyb mění gravitace a odrazy od stěn.
/// Porovnávej jemné a prudké hody; míčky navzájem procházejí.
///
/// **Tvoje mise:** Míčky u boční stěny ztratí vodorovnou rychlost místo odrazu. Oprav bounceSpeed tak,
/// aby stěna zachovala tři čtvrtiny velikosti příchozí rychlosti.
///
/// **Dnes trénuješ:** Desetinná arithmetic (aritmetika): seskupení může provést celočíselné dělení před
/// násobením. Porovnej celočíselný a desetinný poměr a potom předpověz zachovanou rychlost míčku
/// přilétajícího rychlostí 4.
///
/// Použij Run File, tažením do strany hoď míček na stěnu a sleduj odraz; Space místnost pozastaví a R ji
/// vyprázdní.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F04 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
double bounceSpeed(double speed) {
    return Math.abs(speed) * 3.0 / 4;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F04">
/// Arithmetic (aritmetika) používá operand type (typy operandů) v okamžiku provedení operace. Jsou-li
/// oba operand u `/` celočíselné, Java zahodí desetinnou část. Pozdější uložení výsledku do `double`
/// ji nevrátí.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// int pages = 7;
/// double first = pages / 2;
/// double second = pages / 2.0;
/// double third = (double) pages / 2;
/// ```
///
/// Předpověz tři hodnoty: `3.0`, `3.5`, `3.5`. Cast (převod) `(double)` převede toto použití `pages`
/// před dělením; nezmění deklarovaný type (typ) proměnné. Závorky seskupují výpočty, `*` násobí a `/`
/// dělí. Kromě kladného input (vstupu) ověř i záporný a nulu. Pojmenuj jednotky: vzdálenost a
/// rychlost jsou různé veličiny, i když obě používají `double`. O výsledku rozhoduje celý expression
/// (výraz), nikoli jen type napsaný před jménem method (metody).
///
/// Nyní předpověz všechny tři výsledky, pokud pages obsahuje devítku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `double bounceSpeed(double speed)` — Výpočet přijímající rychlost se znaménkem a vracející
///   nezápornou velikost po odrazu.
/// - `Math.abs(speed)` — Připravený výpočet absolutní hodnoty: například -4 změní na 4. Tečka vybírá abs
///   a závorky jí předávají speed.
/// - `*` — Vynásob velikost zlomkem zachovaným stěnou.
/// - `(3 / 4)` — Závorky nejdřív vyhodnotí celočíselné 3 / 4, které zahodí desetinnou část.
/// - `return` — Vrať velikost; nový kladný či záporný směr už vybírá volající část.
///
/// ### Co už obstarává připravená část
///
/// add vytvoří a omezí hod. step volá tvoje pravidlo u bočních a horní stěny a dodává gravitaci i
/// samostatné chování podlahy. draw kreslí uložené míčky a myš vytváří hody.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// 3 / 4 s celočíselnými operand (operandy) je nula.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Math.abs už odstraňuje znaménko původního směru.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Násob hodnotou 3.0 / 4 nebo 0.75.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 440;

record Ball(double x, double y, double vx, double vy, Color colour) {
}

ArrayList<Ball> balls = new ArrayList<>();
Random random = new Random(9);
int startX, startY, aimX, aimY;
boolean aiming, paused;
void restart() {
    balls.clear();
    aiming = false;
    paused = true;
}

void add(double x, double y, double vx, double vy) {
    if (balls.size() >= 60) {
        return;
    }
    double speed = Math.hypot(vx, vy);
    if (speed > 12) {
        vx *= 12 / speed;
        vy *= 12 / speed;
    }
    balls.add(new Ball(Math.max(10, Math.min(WIDTH - 10, x)), Math.max(10, Math.min(HEIGHT - 10, y)), vx, vy, Color.getHSBColor(random.nextFloat(), .7f, .85f)));
    paused = false;
}

void step() {
    if (paused) {
        return;
    }
    for (int i = 0; i < balls.size(); i++) {
        var b = balls.get(i);
        double vx = b.vx * .998, vy = b.vy + .15, x = b.x + vx, y = b.y + vy;
        if (x < 10) {
            x = 10;
            vx = bounceSpeed(vx);
        }
        if (x > WIDTH - 10) {
            x = WIDTH - 10;
            vx = -bounceSpeed(vx);
        }
        if (y < 10) {
            y = 10;
            vy = bounceSpeed(vy);
        }
        if (y > HEIGHT - 10) {
            y = HEIGHT - 10;
            vy = -Math.abs(vy) * .72;
            vx *= .97;
            if (Math.abs(vy)<.6) {
                vy = 0;
            }
        }
        balls.set(i, new Ball(x, y, vx, vy, b.colour));
    }
}

void draw(Graphics2D g) {
    g.setColor(new Color(240, 243, 250));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    for (var b : balls) {
        g.setColor(b.colour);
        g.fillOval((int)b.x - 10, (int)b.y - 10, 20, 20);
    }
    if (aiming) {
        g.setColor(Color.GRAY);
        g.drawLine(startX, startY, aimX, aimY);
    }
    g.setColor(Color.BLACK);
    g.drawString("Balls: " + balls.size() + " / 60   " + (paused ? "Paused" : "Gravity on") + "   No ball-to-ball collisions", 12, 23);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code == KeyEvent.VK_SPACE) {
        paused = !paused;
    }
}

void press(int x, int y, int button) {
    startX = aimX = x;
    startY = aimY = y;
    aiming = true;
}

void drag(int x, int y) {
    aimX = x;
    aimY = y;
}

void release(int x, int y) {
    if (aiming) {
        add(startX, startY, (x - startX) * .08, (y - startY) * .08);
        aiming = false;
    }
}

void showWindow() {
    var frame = new JFrame("Bouncing Balls");
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
        @Override public void mouseDragged(MouseEvent e) {
            drag(e.getX(), e.getY());
            panel.repaint();
        }
        @Override public void mouseReleased(MouseEvent e) {
            release(e.getX(), e.getY());
            panel.repaint();
        }
    };
    panel.addMouseListener(mouse);
    panel.addMouseMotionListener(mouse);
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
    var help = new JLabel("<html><div style=\"text-align:center\">Drag: add and throw a ball   Space: pause   R: clear room</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

void main() {
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
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
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
/// - `public` — Zpřístupňuje tuto declaration (deklaraci) dalšímu kódu.
/// - `protected` — Visibility keyword (klíčové slovo přístupnosti) používané připraveným library callback
///   (knihovním zpětným voláním), například paintComponent.
/// - `super` — V super.paintComponent(g) požádá připravenou parent implementation (nadřazenou
///   implementaci), aby nejprve vykreslila běžné pozadí.
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

