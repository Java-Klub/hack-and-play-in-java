// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.Timer;
// </editor-fold>

/// # L04.06 · VirtualScribbler
///
/// Nech neposedné pero rozvíjet kresbu po plátně a potom řiď jeho tah a roztěkanost. Zastav zajímavý vzor
/// nebo ulož obrázek před vyčištěním plátna pro další pokus.
///
/// **Tvoje mise:** Každá požadovaná dávka vykreslí o krok méně. Oprav burst tak, aby count vyžádalo právě
/// tolik call (volání) step, včetně nuly a jedné; zachovej hranice i pravidla pozastavení.
///
/// **Dnes trénuješ:** Počítané opakování: vyber rozsah for podle počtu průchodů. Pro count rovné jedné
/// musí tělo běžet jednou; pro nulu se nesmí provést vůbec.
///
/// Použij Run File, klávesou Space spusť pero a šipkami měň kresbu; sleduj Steps a potom pomocí Test File
/// ověř přesné dávky nuly, jednoho a čtyř kroků.
/// Po opravě: Save → Test File → zahraj si znovu.
void burst(int count) {
    if (count < 0 || count > 60) {
        return;
    }
    for (int i = 1; i < count; i++) {
        step();
    }
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `void burst(int count)` — Definuj akci přijímající požadovaný počet kroků; kreslí místo vracení
///   hodnoty.
/// - `if (count < 0 || count > 60)` — Připravená kontrola odmítá hodnoty pod nulou nebo nad šedesáti; ||
///   znamená, že stačí kterákoli podmínka.
/// - `return;` — V akci void toto okamžitě odejde bez vrácené hodnoty.
/// - `for (int i = 1; i < count; i++)` — Nastav counter (počítadlo), ověř hranici a po každém průchodu
///   jej zvětši; sleduj skutečné hodnoty místo odhadování počtu průchodů.
/// - `step();` — Jediné call (volání) požádá připravené pero o jeden pohyb. Prázdné závorky znamenají, že
///   akce nepotřebuje input (vstup).
///
/// ### Co už obstarává připravená část
///
/// step zajišťuje neklidný pohyb pera, odrazy od okrajů, kreslení i kontrolu pozastavení. Timer (časovač)
/// pravidelně žádá burst o čtyři kroky; draw ukazuje plátno a skutečný counter (počítadlo) Steps bez
/// pohybu pera. Připravené ovládání řeší tah, roztěkanost, obnovení i export scribble.png.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Vyzkoušej count = 1 se současnou počáteční hodnotou i.
/// 2. Počítání od nuly při i < count dá count průchodů.
/// 3. Začni int i = 0; zachovej i < count a i++.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 450;

BufferedImage canvas;
Random random;
double x, y, angle;
int drift, jitter, steps;
boolean paused;
String message;
void clear() {
    var g = canvas.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.dispose();
}

void restart() {
    canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    clear();
    random = new Random(71);
    x = WIDTH / 2;
    y = HEIGHT / 2;
    angle = 0;
    drift = 3;
    jitter = 4;
    steps = 0;
    paused = true;
    message = "Space starts the pen";
}

void adjust(int d, int j) {
    drift = Math.max(-10, Math.min(10, drift + d));
    jitter = Math.max(0, Math.min(10, jitter + j));
}

void step() {
    if (paused) {
        return;
    }
    angle += drift * .008 + random.nextGaussian() * jitter * .04;
    double nx = x + Math.cos(angle) * 2.2, ny = y + Math.sin(angle) * 2.2;
    if (nx < 2 || nx > WIDTH - 3) {
        angle = Math.PI - angle;
        nx = Math.max(2, Math.min(WIDTH - 3, nx));
    }
    if (ny < 2 || ny > HEIGHT - 3) {
        angle = -angle;
        ny = Math.max(2, Math.min(HEIGHT - 3, ny));
    }
    var g = canvas.createGraphics();
    g.setColor(Color.getHSBColor((steps++ % 1500) / 1500f, .75f, .7f));
    g.setStroke(new BasicStroke(1.5f));
    g.draw(new Line2D.Double(x, y, nx, ny));
    g.dispose();
    x = nx;
    y = ny;
}

void save(File file) throws IOException {
    ImageIO.write(canvas, "png", file);
}

void save() {
    try {
        save(new File("scribble.png"));
        message = "Saved scribble.png";
    } catch (IOException e) {
        message = "Save failed: " + e.getMessage();
    }
}

void draw(Graphics2D g) {
    g.drawImage(canvas, 0, 0, null);
    g.setColor(Color.RED);
    g.drawOval((int)x - 4, (int)y - 4, 8, 8);
    g.setColor(new Color(245, 245, 245));
    g.fillRect(0, 0, WIDTH, 28);
    g.setColor(Color.BLACK);
    g.drawString("Steps: " + steps + "  Drift: " + drift + "  Jitter: " + jitter + "  " + (paused ? "Paused" : "Drawing") + "  " + message, 10, 20);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_SPACE -> paused = !paused;
            case KeyEvent.VK_LEFT -> adjust(-1, 0);
            case KeyEvent.VK_RIGHT -> adjust(1, 0);
            case KeyEvent.VK_UP -> adjust(0, 1);
            case KeyEvent.VK_DOWN -> adjust(0, -1);
            case KeyEvent.VK_C -> clear();
            case KeyEvent.VK_S -> save();
        }
    }
}

void showWindow() {
    var frame = new JFrame("Virtual Scribbler");
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
        burst(4);
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent e) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Space: play / pause   Arrows: drift / jitter   C: clear   S: save PNG   R: reset</div></html>", SwingConstants.CENTER);
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

/// Připravené vstupní body: main, restart, draw, save, showWindow.
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
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

