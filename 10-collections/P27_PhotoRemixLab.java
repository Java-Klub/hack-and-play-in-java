// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # 10.27 · PhotoRemixLab
///
/// Obrovský měsíc přitahuje pozornost, zatímco malý klíč téměř mizí v plakátu. Staň se obrazovým
/// detektivem: pomocí četností barev zvýrazni malý předmět.
///
/// **Tvoje mise:** Funkční výchozí nástroj zvýrazňuje nejčastější původní barvu. Změň lupu na nejnižší
/// kladný počet; při shodě vybírej menší signed RGB key (číselný klíč barvy se znaménkem). Prázdná data
/// vracejí 0; zkoumání počty nemění. Zachovej propojení volby se skutečným azurovým zvýrazněním: musí
/// se objevit malý klíč, ne jen nový popisek.
///
/// **Dnes trénuješ:** Čti histogram (četnosti) jako důkaz a sleduj vybraný key (klíč) do druhého
/// průchodu obrázkem. Mění se vítězná data, nikoli stavba okna.
///
/// Spusť plakát a poznamenej, co svítí azurově. Up/Down mění práh plakátu, R obnoví scénu. Po změně
/// detektivní lupy svítí malý klíč vlevo nahoře, běžný měsíc už ne.

// <editor-fold defaultstate="expanded" desc="Program · prozkoumej pravidla">
void countColour(int rgb) {
    colourCounts.put(rgb, colourCounts.getOrDefault(rgb, 0) + 1);
}

final int WIDTH = 640, HEIGHT = 440;

BufferedImage source, result;
int threshold;
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
void restart() {
    source = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
    var picture = source.createGraphics();
    for (int stripe = 0; stripe < 8; stripe++) {
        picture.setColor(new Color(20 + stripe * 12, 35, 70));
        picture.fillRect(stripe * 50, 0, 50, 300);
    }
    picture.setColor(Color.WHITE);
    picture.fillOval(120, 70, 150, 150);
    picture.setColor(Color.YELLOW);
    picture.fillRect(310, 40, 10, 10);
    picture.fillRect(335, 65, 10, 10);
    picture.setColor(Color.MAGENTA);
    picture.fillRect(35, 40, 6, 6);
    picture.fillRect(40, 42, 18, 2);
    picture.fillRect(52, 44, 2, 5);
    picture.dispose();
    threshold = 128;
    remix();
}

int spotlightColour() {
    int bestColour = 0, bestCount = 0;
    for (var entry : colourCounts.entrySet()) {
        int colour = entry.getKey(), count = entry.getValue();
        if (count > 0 && (bestCount == 0 || count < bestCount || count == bestCount && colour < bestColour)) {
            bestColour = colour;
            bestCount = count;
        }
    }
    return bestColour;
}

void remix() {
    int width = source.getWidth(), height = source.getHeight();
    colourCounts.clear();
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) countColour(source.getRGB(x, y));
    }
    int selected = spotlightColour();
    result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            var colour = new Color(source.getRGB(x, y));
            int brightness = (colour.getRed() * 2126 + colour.getGreen() * 7152 + colour.getBlue() * 722) / 10000;
            int poster = (brightness < threshold ? new Color(25, 45, 80) : new Color(255, 205, 120)).getRGB();
            result.setRGB(x, y, source.getRGB(x, y) == selected ? Color.CYAN.getRGB() : poster);
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
    g.drawString("Spotlight | Threshold " + threshold, 12, 22);
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


// <editor-fold defaultstate="expanded" desc="Nová dovednost · F24">
/// ### Map (mapa) pamatuje hodnotu pro každý key (klíč)
///
/// List (seznam) drží pozice. Map drží vztah: každý key vede k jedné aktuální value (hodnotě). Zde
/// jméno barvy vede k počtu.
///
/// ```java
/// Map<String, Integer> marbles = new HashMap<>();
/// marbles.put("blue", 2);
/// int before = marbles.getOrDefault("blue", 0);
/// marbles.put("blue", before + 1);
/// IO.println(marbles.getOrDefault("gold", 0));
/// ```
///
/// Blue má nyní počet 3; pro gold se vypíše 0. `getOrDefault(key, fallback)` vrátí fallback (náhradní
/// hodnotu), když key chybí, ale nic nevloží. `put` nahradí dosavadní value daného key: samo nepřičítá.
/// Ostatní key zůstanou beze změny.
///
/// `containsKey` zjišťuje samotnou přítomnost. `entrySet()` umožní projít dvojice key/value: každý
/// entry (položka) poskytuje `getKey()` a `getValue()`. Map neslibuje, že první entry má nejvyšší
/// value; vítěze musíš vybrat porovnáváním. Předpověz blue po dalším stejném kroku. Potom předpověz
/// výsledek `getOrDefault("gold", 9)` a rozhodni, zda se tím gold uložilo.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ### Pozoruj a sleduj tok
///
/// Obrazový postup nejdřív shromáždí důkazy, potom vybere barvu a pak přebarví každý odpovídající
/// původní pixel. Histogram (četnosti) přiřazuje colour key (klíči barvy) počet. Po shromáždění můžeš
/// dvojice dočasně vypsat a porovnat hypotézu „největší vyhraje“ s „nejmenší vyhraje“. Až bude pokus
/// jasný, diagnostické výpisy smaž. Nepřebarvuj zdrojová data, abys výsledek předstíral.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pokus a opakování">
/// Před úpravou zapiš „měsíc“ jako současný výsledek. Prohlédni nebo dočasně vypiš počty barev, zvol
/// hypotézu vzácnosti a změň výběr. Ověř, že klíč zůstává i při změně prahu a R zopakuje stejné důkazy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Který předmět zabírá nejvíce pixelů a který jen malou skvrnu? Předpověz jejich pořadí podle
/// četností.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Najdi výběr mezi průchodem počítání a přebarvení. Porovnávej entry value (hodnoty položek), neměň
/// coordinates (souřadnice) kresby.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Najdi spotlightColour a jeho entrySet loop (průchod položkami). Sleduj nejmenší kladný počet, řeš
/// prázdnou Map (mapu) i shodu RGB a Map neměň. remix už vrácenou barvu používá.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta · dobrovolně">
/// Skryj vlastní malý jednobarevný znak ve scéně s většími barevnými plochami. Dej spoluhráči běžnou
/// lupu nejčastější barvy a nech jej znak odhalit.
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
