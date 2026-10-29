// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import javax.swing.Timer;
// </editor-fold>

/// # L06.06 · PegDrop
///
/// Pusť kotouček mezi kolíky a miř na cenné přihrádky. Číslo pod přihrádkou má odpovídat získaným bodům.
///
/// **Tvoje mise:** Každá přihrádka vyplácí hodnotu první. Oprav scoreAt(int index), aby dotaz na platnou
/// přihrádku vrátil její vlastní cenu a sám body nepřiděloval.
///
/// **Dnes trénuješ:** Pure contracts (smlouvy čistých dotazů): přečtení ceny a přidělení bodů jsou různé
/// odpovědnosti. scoreAt() vrací požadovanou hodnotu bez změny součtu nebo pohybu.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L06/M06_PegDrop.java`
/// - Kontrola této mise: `java L06/Check.java M06_PegDrop.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše a kliknutím nad přihrádkou pusť kotouček, potom
/// porovnej číslo přihrádky dopadu se získanými body. Po opravě: ulož soubor, spusť uvedenou
/// kontrolu a zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F15 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
int scoreAt(int index) {
    // Dodaná ochrana hlásí neplatný index; exception handling přijde v L09.
    if (index < 0 || index >= values.length) {
        throw new IllegalArgumentException("Unknown bin");
    }
    return values[0];
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F15">
/// ### Contracts: ptej se bez změny světa
///
/// Contract (smlouva) popisuje přijímaný input (vstup) a význam výsledku. Pure calculation (čistý
/// výpočet) používá své vstupy bez změny dalšího state (stavu). Query (dotaz) může číst aktuální
/// model (model pravidel), ale má jej zachovat. Získání odpovědi a potvrzení akce jsou různé
/// odpovědnosti.
///
/// ```java
/// int contrast(int light, int dark) {
///     return Math.abs(light - dark);
/// }
/// ```
///
/// `Math.abs` dává nezápornou velikost, odstraní tedy případné záporné znaménko. Tuto method (metodu)
/// můžeš volat opakovaně, aniž by něco spotřebovala. Argument (argumenty) `(9, 4)` dávají pět: rozdíl
/// je pět. Předpověz `(4, 9)`. Které uložené hodnoty porovnáš před dotazem a po něm, abys doložil
/// nepřítomnost side effect (vedlejšího účinku)? Stejnou ochranu potřebuje odmítnutý kandidát.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int scoreAt(int index)` — Dotaz požaduje celočíselný výsledek pro jeden index (pozici) přihrádky.
/// - `index < 0 || index >= values.length` — Připravený guard (ochranná kontrola) pozná pozici mimo
///   dostupné přihrádky.
/// - `values.length` — Počet uložených cen přihrádek; sám už není platným indexem (pozicí).
/// - `return values[0]` — Přečte jeden prvek array (pole) a vrátí jej; nepřidává tím body do součtu.
///
/// ### Co už obstarává připravená část
///
/// step() už řeší srážky s kolíky a dopad, potom volá scoreAt() pro cílovou přihrádku. Dodaný guard
/// (ochrannou kontrolu indexu) zachovej; exceptions (výjimky) a jejich zpracování přijdou v L09.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Array values je bodovací tabulka vykreslená pod deskou.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Volající už rozhoduje, kdy disk dopadl.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Dodaná ochrana už odmítá neplatné indexy. Vrať values[index]; udělení bodů zůstává v step().
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 600, HEIGHT = 440;

ArrayList<Point> pegs = new ArrayList<>();
double x, y, vx, vy;
boolean active, done;
int score, bin;
int[] values = {10, 30, 50, 100, 100, 50, 30, 10};
/// restart(): Obnovuje počáteční model Peg Drop pro další pokus.
void restart() {
    pegs.clear();
    for (int row = 0; row < 7; row++) {
        for (int column = 0; column < 8; column++) {
            int xx = 35 + column * 75 + (row % 2) * 37;
            if (xx < WIDTH - 15) {
                pegs.add(new Point(xx, 75 + row * 40));
            }
        }
    }
    x = WIDTH / 2;
    y = 25;
    vx = vy = 0;
    active = done = false;
    score = 0;
    bin = -1;
}

void drop(int xx) {
    if (active || done) {
        return;
    }
    x = Math.max(20, Math.min(WIDTH - 20, xx));
    y = 25;
    vx = vy = 0;
    active = true;
}

/// step(): Používá omezené podkroky, normály kolíků a oddělení; potom udělí body cílové přihrádky.
void step() {
    if (!active) {
        return;
    }
    for (int sub = 0; sub < 4 && active; sub++) {
        vy = Math.min(7, vy + .04);
        x += vx / 4;
        y += vy / 4;
        for (var p : pegs) {
            double dx = x - p.x, dy = y - p.y, distance = Math.hypot(dx, dy);
            if (distance < 14) {
                double nx = distance<.001 ? 1 : dx / distance, ny = distance<.001 ? 0 : dy / distance;
                x = p.x + nx * 14.01;
                y = p.y + ny * 14.01;
                double normal = vx * nx + vy * ny;
                if (normal < 0) {
                    vx -= 1.65 * normal * nx;
                    vy -= 1.65 * normal * ny;
                }
                if (Math.abs(nx)<.1 && Math.abs(vx)<.1) {
                    vx += p.x % 2 == 0 ? .12 : -.12;
                }
            }
        }
        if (x < 9) {
            x = 9;
            vx = Math.abs(vx) * .7;
        }
        if (x > WIDTH - 9) {
            x = WIDTH - 9;
            vx = -Math.abs(vx) * .7;
        }
        vx = Math.max(-6, Math.min(6, vx));
        if (y >= HEIGHT - 12) {
            y = HEIGHT - 12;
            active = false;
            done = true;
            bin = Math.min(7, (int)x / 75);
            score = scoreAt(bin);
        }
    }
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(Graphics2D g) {
    g.setColor(new Color(235, 242, 250));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(new Color(80, 100, 130));
    for (var p : pegs) {
        g.fillOval(p.x - 5, p.y - 5, 10, 10);
    }
    g.setColor(Color.GRAY);
    for (int i = 0; i <= 8; i++) {
        g.drawLine(i * 75, 370, i * 75, HEIGHT);
    }
    for (int i = 0; i < 8; i++) {
        g.drawString("" + values[i], i * 75 + 25, HEIGHT - 20);
    }
    g.setColor(Color.ORANGE);
    g.fillOval((int)x - 9, (int)y - 9, 18, 18);
    g.setColor(Color.BLACK);
    g.drawString(done ? "Bin " + (bin + 1) + " - " + score + " points! R: another disc" : active ? "Bouncing..." : "Click across the top to choose and drop a disc", 12, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code == KeyEvent.VK_SPACE) {
        drop(WIDTH / 2);
    }
}

void press(int x, int y, int button) {
    drop(x);
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; časovače končí při
/// zavření okna.
void showWindow() {
    var frame = new JFrame("Peg Drop");
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
    };
    panel.addMouseListener(mouse);
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click: choose a drop position   Space: centre drop   R: next disc</div></html>", SwingConstants.CENTER);
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
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

