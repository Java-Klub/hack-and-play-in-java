// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
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
/// Po Run File klávesou M měň způsob úpravy měsíčního obrázku, šipkami jej upravuj a nad obrazem čti
/// počítadlo bodů.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F24 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
void countColour(int rgb) {
    colourCounts.put(rgb, 1);
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
/// Remix loop (cyklus úpravy) volá countColour pro původní zdrojové pixel (obrazové body). CountedPixels
/// sečte hodnoty Map pro viditelný popisek; připravený obrázek má opakované barvy, aby byla chyba patrná.
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
boolean pixelated, original;
int threshold, block;
String message;
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
    block = 12;
    pixelated = original = false;
    message = "O: load a personal image";
    remix();
}

void remix() {
    int w = source.getWidth(), h = source.getHeight();
    colourCounts.clear();
    for (int yy = 0; yy < h; yy++) {
        for (int xx = 0; xx < w; xx++) countColour(source.getRGB(xx, yy));
    }
    result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    if (!pixelated) {
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                var c = new Color(source.getRGB(x, y));
                int light = (c.getRed() * 2126 + c.getGreen() * 7152 + c.getBlue() * 722) / 10000;
                result.setRGB(x, y, (light < threshold ? new Color(25, 45, 80) : new Color(255, 205, 120)).getRGB());
            }
        }
    }
    else {
        for (int y = 0; y < h; y += block) {
            for (int x = 0; x < w; x += block) {
                long r = 0, g = 0, b = 0, count = 0;
                for (int yy = y; yy < Math.min(h, y + block); yy++) {
                    for (int xx = x; xx < Math.min(w, x + block); xx++) {
                        var c = new Color(source.getRGB(xx, yy));
                        r += c.getRed();
                        g += c.getGreen();
                        b += c.getBlue();
                        count++;
                    }
                }
                int rgb = new Color((int)(r / count), (int)(g / count), (int)(b / count)).getRGB();
                for (int yy = y; yy < Math.min(h, y + block); yy++) {
                    for (int xx = x; xx < Math.min(w, x + block); xx++) {
                        result.setRGB(xx, yy, rgb);
                    }
                }
            }
        }
    }
}

void adjust(int t, int size) {
    threshold = Math.max(16, Math.min(240, threshold + t));
    block = Math.max(2, Math.min(40, block + size));
    remix();
}

void load(File file) throws IOException {
    try(var input = ImageIO.createImageInputStream(file)) {
        if (input == null) {
            throw new IOException("Cannot read this file");
        }
        var readers = ImageIO.getImageReaders(input);
        if (!readers.hasNext()) {
            throw new IOException("Unsupported image");
        }
        var reader = readers.next();
        try {
            reader.setInput(input);
            int w = reader.getWidth(0), h = reader.getHeight(0);
            if ((long)w * h > 20000000) {
                throw new IOException("Image is too large (maximum 20 million pixels)");
            }
            var loaded = reader.read(0);
            double scale = Math.min(1, Math.min(600.0 / w, 350.0 / h));
            source = new BufferedImage(Math.max(1, (int)(w * scale)), Math.max(1, (int)(h * scale)), BufferedImage.TYPE_INT_RGB);
            var g = source.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, source.getWidth(), source.getHeight());
            g.drawImage(loaded, 0, 0, source.getWidth(), source.getHeight(), null);
            g.dispose();
            remix();
        } finally {
            reader.dispose();
        }
    }
}

void choose() {
    var chooser = new JFileChooser();
    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        try {
            load(chooser.getSelectedFile());
            message = "Loaded " + chooser.getSelectedFile().getName();
        } catch (IOException e) {
            message = e.getMessage();
        }
    }
}

/// Připravená export boundary (hranice ukládání): používá místní cestu; encoding (kódování) zajišťuje JDK.
void save(File file) throws IOException {
    ImageIO.write(result, "png", file);
}

/// Připravená export boundary (hranice ukládání): používá místní cestu; encoding (kódování) zajišťuje JDK.
void save() {
    try {
/// Připravená export boundary (hranice ukládání): používá místní cestu; encoding (kódování) zajišťuje JDK.
        save(new File("photo-remix.png"));
        message = "Saved photo-remix.png";
    } catch (IOException e) {
        message = "Save failed: " + e.getMessage();
    }
}

/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
void draw(Graphics2D g) {
    var image = original ? source : result;
    g.drawImage(image, (WIDTH - image.getWidth()) / 2, 45, null);
    g.setColor(Color.BLACK);
    g.drawString((original ? "Original" : pixelated ? "Pixel portrait" : "Two-colour poster") + "   Threshold " + threshold + "   Block " + block, 12, 22);
    g.drawString(message + " | Pixels counted: " + countedPixels() + " / " + source.getWidth() * source.getHeight(), 12, 420);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_O -> choose();
            case KeyEvent.VK_M -> {
                pixelated = !pixelated;
                remix();
            }
            case KeyEvent.VK_SPACE -> original = !original;
            case KeyEvent.VK_UP -> adjust(8, 0);
            case KeyEvent.VK_DOWN -> adjust(-8, 0);
            case KeyEvent.VK_LEFT -> adjust(0, -2);
            case KeyEvent.VK_RIGHT -> adjust(0, 2);
            case KeyEvent.VK_S -> save();
        }
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
    var help = new JLabel("<html><div style=\"text-align:center\">O: open image   M: poster/pixels   Arrows: threshold/block   Space: original   S: save   R:<br>reset</div></html>", SwingConstants.CENTER);
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

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Remix smaže colourCounts, spočítá původní pixel (body obrazu) a vytvoří změněný obraz.
/// Draw ukazuje skutečný přehled; load/save a Swing input (vstup) jsou připravené hranice.
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
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
///   se ale dál měnit může.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
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
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `Map` — Přiřazuje key (klíčům) hodnoty. Pro chybějící key je třeba rozhodnout výchozí chování.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

