// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
// </editor-fold>

/// # L01.02 · MissingTriangle
///
/// Tři vykrojené černé kruhy mohou vyvolat dojem bílého trojúhelníku, který nikdo nenakreslil. Otoč jeden
/// kruh a sleduj, jak zdánlivý trojúhelník zmizí.
///
/// **Tvoje mise:** Kruhy nyní vypadají plné, protože jedna kreslicí akce zakrývá jinou. Oprav paintDisc
/// tak, aby každému kruhu zůstal bílý výkroj.
///
/// **Dnes trénuješ:** Sequence (pořadí) a vrstvy: když se zavolá paintDisc, obě vnitřní call (volání)
/// běží v zapsaném pořadí. Předpověz, která barva zůstane nahoře. Samotná definice čeká, až ji připravená
/// kreslicí část zavolá.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L01/M02_MissingTriangle.java`
/// - Kontrola této mise: `java L01/Check.java M02_MissingTriangle.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše, kliknutím otoč kruh a klávesou O zobraz obrysy,
/// které program skutečně kreslí. Po opravě: ulož soubor, spusť uvedenou kontrolu a zahraj si
/// znovu.
///
/// Řádky s `///` jsou průvodce pro tebe; při spuštění se neprovádějí.
///
/// Potřebuješ výklad? Připomenutí · F01 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
void paintDisc(Graphics2D g, int i) {
    whiteBite(g, i);
    darkDisc(g, i);
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F01">
/// Method (metoda) je pojmenovaná akce. Její definice akci zpřístupní; call (volání) ji požádá o
/// provedení. Java spustí tento compact program (stručný program) v `main`, i kdyby jiná method byla
/// zapsaná první.
///
/// ```java
/// void announce() {
///     IO.println("Before");
/// }
///
/// void main() {
///     announce();
///     IO.println("After");
/// }
/// ```
///
/// Před dalším čtením předpověz dva řádky output (výstupu). Call `announce();` vstoupí do této
/// method, vypíše `Before` a potom se vrátí k dalšímu statement (příkazu) v `main`, který vypíše
/// `After`. Uvnitř zavolané method běží tyto statement v pořadí. `void` znamená, že se nevrací
/// výsledná hodnota; `()` zde nepředává žádné argument (argumenty). Složené závorky ohraničují tělo a
/// středník ukončuje každé call. Comment (komentáře) program vysvětlují, ale neprovádějí se.
///
/// Nyní předpověz, co nastane, pokud main před posledním výpisem zavolá announce dvakrát.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `void paintDisc` — Tímto pojmenuješ kreslicí akci bez vráceného výsledku; jejím účinkem je kresba.
/// - `(Graphics2D g, int i)` — Pero g a číslo kruhu i přicházejí z připravené části. Čárka odděluje tyto
///   dva input (vstupy).
/// - `{` — Vše až po odpovídající } patří do této akce.
/// - `whiteBite(g, i);` — Nakresli bílý výkroj kruhu i perem g. Oba input (vstupy) se předávají společně.
/// - `darkDisc(g, i);` — Nakresli plný černý kruh; ten může překrýt dříve nakreslený výkroj.
/// - `;` — Středník ukončuje každé call (volání) akce. Při přesunu celého volání přesuneš i jeho
///   středník.
///
/// ### Co už obstarává připravená část
///
/// Řádky /// vysvětlují úlohu a neprovádějí se. main níže spustí program a otevře okno; okno požádá draw
/// o kresbu a draw zavolá tvoji akci pro všechny tři kruhy. darkDisc a whiteBite obsahují tvary, rotate a
/// zbývající část řeší otáčení a klikání.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Najdi dvě připravené kreslicí akce.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Bílý tvar musí být poslední vrstva.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Zavolej darkDisc(g, i) před whiteBite(g, i).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
void darkDisc(Graphics2D g, int i) {
    g.setColor(Color.BLACK);
    g.fillOval(cx[i] - 65, cy[i] - 65, 130, 130);
}

void whiteBite(Graphics2D g, int i) {
    g.setColor(Color.WHITE);
    g.fillArc(cx[i] - 66, cy[i] - 66, 132, 132, base[i] + rotation[i], 60);
}

final int WIDTH = 600, HEIGHT = 450;

int[] rotation = new int[3];
int[] cx = {300, 160, 440}, cy = {80, 320, 320}, base = {-120, 0, 120};
boolean outlines;
void restart() {
    rotation = new int[3];
    outlines = false;
}

void rotate(int index) {
    if (index >= 0 && index < 3) {
        rotation[index] = (rotation[index] + 30) % 360;
    }
}

void draw(Graphics2D g) {
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    for (int i = 0; i < 3; i++) {
        paintDisc(g, i);
        if (outlines) {
            g.setColor(Color.RED);
            g.drawOval(cx[i] - 65, cy[i] - 65, 130, 130);
            for (int side = 0; side < 2; side++) {
                double a = Math.toRadians(base[i] + rotation[i] + side * 60);
                g.drawLine(cx[i], cy[i], cx[i] + (int)(65 * Math.cos(a)), cy[i] - (int)(65 * Math.sin(a)));
            }
        }
    }
    g.setColor(Color.DARK_GRAY);
    g.drawString("Is there a white triangle? No triangle is filled by the program.", 15, 420);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code >= KeyEvent.VK_1 && code <= KeyEvent.VK_3) {
        rotate(code - KeyEvent.VK_1);
    }
    if (pressed && code == KeyEvent.VK_O) {
        outlines = !outlines;
    }
}

void press(int x, int y, int button) {
    for (int i = 0; i < 3; i++) {
        if (Math.hypot(x - cx[i], y - cy[i])<65) {
            rotate(i);
        }
    }
}

void showWindow() {
    var frame = new JFrame("Missing Triangle");
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
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Click a disc or press 1-3: rotate   O: actual outlines   R: align</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

void main() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("This game needs a graphical display; model checks still work headlessly.");
        return;
    }
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
/// - `%` — Remainder (zbytek po dělení); hodí se pro opakující se pozice nebo tahy.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
/// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
///   List<String> lomené závorky popisují element type (typ prvku).
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
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

