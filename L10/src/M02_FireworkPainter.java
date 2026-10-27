// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.Timer;
// </editor-fold>

/// # L10.02 · FireworkPainter
///
/// Maluj noční oblohu ohňostroji, které rozkvetou tam, kam klikneš. Každý výbuch má respektovat vybrané
/// místo, ne pevný bod plátna.
///
/// **Tvoje mise:** Kliknutí nevytvoří výbuch, protože vrácená akce ignoruje obě souřadnice. Ať odložená
/// akce změní skutečný model (model pravidel) ohňostroje.
///
/// **Dnes trénuješ:** Callback (předaná akce) se dvěma argument (argumenty) přenese souřadnice kliknutí
/// do pozdější akce. Vytvoření callback nesmí změnit částice; spuštění má použít právě dané kliknutí
/// jednou.
///
/// Po Run File klikej na různá místa plátna pro ohňostroje, pomocí Space zastav pohyb a klávesou C vymaž
/// oblohu.
/// Po opravě: Save → Test File → zahraj si znovu.
java.util.function.BiConsumer<Integer, Integer> burstAction() {
    return (x, y) -> {};
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `BiConsumer<Integer, Integer>` — JDK role akce, která přijímá dva integer (celá čísla) a nevrací
///   hodnotu.
/// - `(x, y)` — Tyto lambda parameter (parametry anonymní funkce) dostanou souřadnice při spuštění akce.
/// - `-> {}` — Šipka uvádí tělo akce, které je nyní prázdné.
///
/// ### Co už obstarává připravená část
///
/// Press předá kliknutí uloženému burst callback (předané akci výbuchu). Připravený burst vytvoří
/// částice, step posune jejich omezené životy a draw je vykreslí; pause, reset i uložení jsou připravené.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. BiConsumer přijme dvě hodnoty později.
/// 2. Předej je v pořadí x/y; při tvorbě callback (předané akce) burst nevolej.
/// 3. Připravená burst už přidá šedesát částic a spustí pohyb.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 440;

record Particle(double x, double y, double vx, double vy, int age, Color colour) {
}

BufferedImage canvas;
ArrayList<Particle> particles = new ArrayList<>();
Random random = new Random(41);
boolean paused;
String message;
void clear() {
    var g = canvas.createGraphics();
    g.setColor(new Color(8, 12, 28));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.dispose();
    particles.clear();
}

/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
void restart() {
    canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    clear();
    paused = true;
    message = "Click to burst";
}

void burst(int x, int y) {
    if (particles.size()>1140) {
        return;
    }
    Color colour = Color.getHSBColor(random.nextFloat(), .75f, 1);
    for (int i = 0; i < 60; i++) {
        double a = 2 * Math.PI * i / 60, velocity = 1 + random.nextDouble() * 3;
        particles.add(new Particle(x, y, Math.cos(a) * velocity, Math.sin(a) * velocity, 0, colour));
    }
    paused = false;
}

/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
void step() {
    if (paused) {
        return;
    }
    var g = canvas.createGraphics();
    for (int i = particles.size() - 1; i >= 0; i--) {
        var p = particles.get(i);
        double x = p.x + p.vx, y = p.y + p.vy;
        g.setColor(new Color(p.colour.getRed(), p.colour.getGreen(), p.colour.getBlue(), Math.max(0, 150 - p.age * 2)));
        g.draw(new Line2D.Double(p.x, p.y, x, y));
        if (p.age >= 65) {
            particles.remove(i);
        }
        else {
            particles.set(i, new Particle(x, y, p.vx * .992, p.vy + .035, p.age + 1, p.colour));
        }
    }
    g.dispose();
}

/// Připravená export boundary (hranice ukládání): používá místní cestu; encoding (kódování) zajišťuje JDK.
void save(File file) throws IOException {
    ImageIO.write(canvas, "png", file);
}

/// Připravená export boundary (hranice ukládání): používá místní cestu; encoding (kódování) zajišťuje JDK.
void save() {
    try {
/// Připravená export boundary (hranice ukládání): používá místní cestu; encoding (kódování) zajišťuje JDK.
        save(new File("fireworks.png"));
        message = "Saved fireworks.png";
    } catch (IOException e) {
        message = "Save failed: " + e.getMessage();
    }
}

/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
void draw(Graphics2D g) {
    g.drawImage(canvas, 0, 0, null);
    g.setColor(Color.WHITE);
    g.drawString(message + "   Particles: " + particles.size() + "   " + (paused ? "Paused" : ""), 12, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_SPACE -> paused = !paused;
            case KeyEvent.VK_C -> clear();
            case KeyEvent.VK_S -> save();
        }
    }
}

void press(int x, int y, int button) {
    burstCallback.accept(x, y);
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Firework Painter");
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
/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click: burst   Space: pause   C: clear   S: save PNG   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

java.util.function.BiConsumer<Integer, Integer> burstCallback = burstAction();

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Press spustí burstCallback; burst vytvoří částice přesně v těchto coordinate (souřadnicích).
/// Step nechá částice stárnout a kreslí stopu; draw jen zobrazí plátno; export i úklid timer (časovače) jsou připravené.
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
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
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

