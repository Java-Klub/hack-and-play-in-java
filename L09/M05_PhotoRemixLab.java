// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L09.05 · PhotoRemixLab
///
/// Promíchej připravený obrázek pomocí obrazových efektů a prozkoumej jeho barvy. Počítadlo má zahrnout
/// každý bod, i velkou plochu stejné barvy.
///
/// **Tvoje mise:** Opakované barvy si drží počet jedna, takže zobrazený součet bodů je příliš malý. Oprav
/// počítání beze změny obrazových efektů.
///
/// **Dnes trénuješ:** Map (mapa) spojuje každý colour key (klíč barvy) s aktuálním počtem. Opakovaný key
/// potřebuje nový součet; put starou hodnotu nahrazuje, samo k ní nepřičítá.
///
/// Po spuštění programu porovnej Pixels counted s celkovým počtem bodů; Up/Down mění práh
/// plakátu a R obnoví ukázku.
void countColour(int rgb) {
    colourCounts.put(rgb, colourCounts.getOrDefault(rgb, 0) + 1);
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F24">
/// ### Map (mapa): hodnota podle klíče
///
/// `Map<String, Integer>` přiřazuje textovým keys (klíčům) celočíselné values (hodnoty). Jeden klíč
/// má nejvýše jednu uloženou hodnotu.
///
/// ```java
/// Map<String, Integer> badges = new HashMap<>();
/// badges.put("moon", 4);
/// IO.println(badges.getOrDefault("moon", 0));
/// IO.println(badges.getOrDefault("sun", 0));
/// ```
///
/// Výstup je `4`, potom `0`. `getOrDefault(key, fallback)` přečte existující hodnotu; při chybějícím
/// klíči vrátí fallback (náhradní hodnotu). Toto čtení nic nevkládá. `put(key, value)` hodnotu uloží
/// a případnou předchozí hodnotu téhož klíče přepíše; čísla automaticky nesčítá. `containsKey(key)`
/// použij, když potřebuješ výslovně rozlišit chybějící klíč. V těchto příkladech neukládáme `null`.
///
/// Předpověz: po `badges.put("moon", 9)` co vrátí stejná dvě čtení?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int rgb` — Jeden integer (celé číslo) nese zabalenou barvu použitou jako Map key (klíč mapy).
/// - `colourCounts.put` — Uloží hodnotu pod rgb a nahradí případnou dřívější hodnotu.
/// - `rgb, 1` — Tato dvojice nyní přiřadí jedna, i když se rgb už vyskytlo.
///
/// ### Co už obstarává připravená část
///
/// remix volá countColour pro každý bod ukázky a vytvoří jediný dvoubarevný plakát. draw čte
/// skutečný součet ze stejné Map (mapy); Up/Down mění práh a R obnoví ukázku.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// ### Před přepsáním nejdřív přečti
///
/// Potřebné čtení je `map.getOrDefault(key, fallback)`. U tohoto `Map<Integer, Integer>` je key
/// (klíč) celé číslo představující barvu a výsledek je její počet. Jestliže klíč 17 uchovává 6,
/// `map.getOrDefault(17, 0)` vrátí 6; pro chybějící klíč 23 vrátí 0 a klíč nevloží. `map.put(key,
/// value)` potom uloží přesně zadanou hodnotu a případný předchozí počet přepíše.
///
/// Předpověz potřebné počty po třech pixelech stejné barvy a jednom odlišném. Vysvětli, proč pomůže
/// vyčistit mapu jednou před novým remixem, zatímco čištění uvnitř countColour by zapomnělo dřívější
/// pixely.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Put nahrazuje původní hodnotu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Přečti existující počet; pro chybějící key (klíč) použij nulu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Ke starému počtu přičti jednu a ulož jej pod stejným key (klíčem).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 440;

BufferedImage source, result;
int threshold;
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
void restart() {
    source = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < 300; y++) {
        for (int x = 0; x < 400; x++) {
            source.setRGB(x, y, new Color(x * 255 / 399, y * 255 / 299, (x + y) * 255 / 698).getRGB());
        }
    }
    var moon = source.createGraphics();
    moon.setColor(Color.WHITE);
    moon.fillOval(120, 70, 150, 150);
    moon.dispose();
    threshold = 128;
    remix();
}

void remix() {
    int width = source.getWidth(), height = source.getHeight();
    colourCounts.clear();
    result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            countColour(source.getRGB(x, y));
            var colour = new Color(source.getRGB(x, y));
            int brightness = (colour.getRed() * 2126 + colour.getGreen() * 7152 + colour.getBlue() * 722) / 10000;
            result.setRGB(x, y, (brightness < threshold ? new Color(25, 45, 80) : new Color(255, 205, 120)).getRGB());
        }
    }
}

void adjust(int amount) {
    threshold = Math.max(16, Math.min(240, threshold + amount));
    remix();
}


/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
void draw(Graphics2D g) {
    g.drawImage(result, (WIDTH - result.getWidth()) / 2, 45, null);
    g.setColor(Color.BLACK);
    g.drawString("Two-colour poster | Threshold " + threshold, 12, 22);
    g.drawString("Pixels counted: " + countedPixels() + " / " + source.getWidth() * source.getHeight(), 12, 420);
}

void key(int code, boolean pressed) {
    if (!pressed) return;
    switch (code) {
        case KeyEvent.VK_R -> restart();
        case KeyEvent.VK_UP -> adjust(8);
        case KeyEvent.VK_DOWN -> adjust(-8);
    }
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Photo Remix Lab");
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
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Up/Down: poster threshold   R: restart</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

Map<Integer, Integer> colourCounts = new HashMap<>();

int countedPixels() {
    int total = 0;
    for (int count : colourCounts.values()) total += count;
    return total;
}

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
/// - `boolean` — Hodnota true nebo false: ano nebo ne.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
/// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
///   se ale dál měnit může.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
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
/// - `null` — Žádný object (objekt) tu není.
/// - `Map` — Přiřazuje key (klíčům) hodnoty. Pro chybějící key je třeba rozhodnout výchozí chování.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
