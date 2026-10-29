// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Path2D;
import java.awt.geom.Area;
// </editor-fold>

/// # L10.03 · ShadowPlayground
///
/// Pohybuj lampou kolem překážek a sleduj prodlužující se stíny. Zkus přiblížením světla udělat z malé
/// překážky obrovskou siluetu.
///
/// **Tvoje mise:** Lampa ignoruje kliknutí i tažení, protože její odložená akce je prázdná. Obnov spojení
/// od každé vstupní pozice ke skutečnému světlu.
///
/// **Dnes trénuješ:** Callback (předaná akce) odděluje přijetí event (události) od volby jejího účinku.
/// Akce se dvěma souřadnicemi má předat hodnoty události, ne uložit pevnou pozici či jednat už při
/// přípravě.
///
/// Po spuštění programu klikáním či tažením kolem překážek posouvej lampu a pomocí R obnov její
/// počáteční pozici.
java.util.function.BiConsumer<Integer, Integer> lightAction() {
    return this::moveLight;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F28">
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
/// - `BiConsumer<Integer, Integer>` — Popisuje akci, která přijme x a y, ale nevrací hodnotu.
/// - `lightAction()` — Dodává opakovaně použitelnou akci, kterou input handler (obsluha vstupu) zavolá
///   později.
/// - `(x, y) -> {}` — Pojmenuje budoucí vstupy, ale prázdné tělo je nyní zahodí.
///
/// ### Co už obstarává připravená část
///
/// Press i drag volají stejný light callback (předanou akci světla). Připravený moveLight ověří povolenou
/// pozici; geometrie stínů a kreslení čtou výsledné souřadnice lampy a restart obnoví scénu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// MoveLight už omezuje x a y na plátno.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Callback (předaná akce) má volat tuto method (metodu), nikoli ji kopírovat či obejít.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Method reference (odkaz na metodu) oddělí vytvoření od spuštění.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 440;

Rectangle[] obstacles = {new Rectangle(190, 130, 70, 100), new Rectangle(380, 220, 100, 60)};
double lightX, lightY;
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
void restart() {
    lightX = 100;
    lightY = 80;
}

void moveLight(int x, int y) {
    lightX = Math.max(0, Math.min(WIDTH, x));
    lightY = Math.max(0, Math.min(HEIGHT, y));
}

Area shadow(Rectangle obstacle) {
    if (obstacle.contains(lightX, lightY)) {
        return new Area(new Rectangle(0, 0, WIDTH, HEIGHT));
    }
    Point[] corners = {new Point(obstacle.x, obstacle.y), new Point(obstacle.x + obstacle.width, obstacle.y), new Point(obstacle.x + obstacle.width, obstacle.y + obstacle.height), new Point(obstacle.x, obstacle.y + obstacle.height)};
    var area = new Area(obstacle);
    for (int i = 0; i < 4; i++) {
        var a = corners[i];
        var b = corners[(i + 1) % 4];
        double da = Math.max(.001, a.distance(lightX, lightY)), db = Math.max(.001, b.distance(lightX, lightY));
        var path = new Path2D.Double();
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.lineTo(b.x + (b.x - lightX) / db * 1500, b.y + (b.y - lightY) / db * 1500);
        path.lineTo(a.x + (a.x - lightX) / da * 1500, a.y + (a.y - lightY) / da * 1500);
        path.closePath();
        area.add(new Area(path));
    }
    return area;
}

/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
void draw(Graphics2D g) {
    g.setColor(new Color(255, 246, 205));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    var combined = new Area();
    boolean inside = false;
    for (var obstacle : obstacles) {
        combined.add(shadow(obstacle));
        if (obstacle.contains(lightX, lightY)) {
            inside = true;
        }
    }
    g.setColor(new Color(65, 75, 100, 160));
    g.fill(combined);
    g.setColor(new Color(45, 50, 65));
    for (var obstacle : obstacles) {
        g.fill(obstacle);
    }
    g.setColor(Color.ORANGE);
    g.fillOval((int)lightX - 10, (int)lightY - 10, 20, 20);
    g.setColor(Color.BLACK);
    g.drawString(inside ? "Light inside an obstacle - drag it outside" : "Drag the light around two opaque rectangles", 12, 23);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
        restart();
    }
}

void press(int x, int y, int button) {
    lightCallback.accept(x, y);
}

void drag(int x, int y) {
    lightCallback.accept(x, y);
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Shadow Playground");
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
        @Override public void mouseDragged(MouseEvent e) {
            drag(e.getX(), e.getY());
            panel.repaint();
        }
    };
    panel.addMouseListener(mouse);
    panel.addMouseMotionListener(mouse);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Click / drag: move the light   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

java.util.function.BiConsumer<Integer, Integer> lightCallback = lightAction();

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
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `++` — Zvětší číselnou hodnotu o jedna.
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
