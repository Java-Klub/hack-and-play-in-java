// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L11.04 · MandelbrotPostcards
///
/// Pohlednice dorazila bez barevného receptu. Krajinu umíš znovu nakreslit; malý barevný proužek je
/// stopa k chybějícímu seed.
///
/// **Tvoje mise:** Tajemná paleta pochází z některého seed mezi 0 a 255. Vytvoř hledač všech seedů,
/// jejichž paleta začíná předaným vzorkem barev, ve vzestupném pořadí. Nemožný vzorek nemá kandidáty.
/// Běžné kreslení pohlednice zachovej; seed pro obrázek vyber svým nástrojem místo hádání.
///
/// **Zkoumání:** Vyřeš deterministickou záhadu vlastním nástrojem pro omezené hledání.
///
/// Přečti tajemný vzorek i zprávu o kandidátech, potom vyber seed palety a span (šířku záběru).
/// Obrázek ukáže referenční barevný proužek. Enter nechá běžné výchozí hodnoty; /q skončí.

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Uspořádej výstavu dvou pohlednic: zvol palette seed (počáteční stav barev) a span (šířku záběru)
/// pro celou množinu, potom jiný záběr a střed u okraje. Zapiš všechna čtyři čísla, ne jen seed.
/// Paleta mění barvu; span a střed mění viditelnou krajinu.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F27">
/// ### Seeded randomness (náhoda se seedem): opakuj pokus
///
/// Pseudorandom generator (generátor pseudonáhodných hodnot) vytváří posloupnost. Seed (počáteční
/// hodnota) určuje výchozí stav; stejné seedy a stejná volání zopakují stejné výsledky.
///
/// ```java
/// var first = new Random(9);
/// var second = new Random(9);
/// int a = first.nextInt(4);
/// int b = second.nextInt(4);
/// IO.println(a == b);
/// ```
///
/// Výstup je `true`. Každé nextInt posune příslušný generátor. `nextInt(4)` vybere celé číslo od 0 do
/// 3; horní mez nepatří do výběru. V celém generovaném obrázku používej jeden generátor, aby další
/// volby četly další místa posloupnosti. Opakované vytváření se stejným seedem uvnitř loop (cyklu)
/// stále vrací první volbu. Různé seedy mohou náhodou dát stejný jednotlivý výsledek.
///
/// Předpověz: která čísla smí vrátit `nextInt(3)` a může vrátit 3?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Deník průzkumníka · vyložený příklad">
/// ### Projdi malý prostor; stopa není odpověď
///
/// Seeded generation (generování s počátečním stavem) se opakuje při stejném pořadí volání. Hledání
/// může kandidáty znovu vygenerovat a porovnat jejich výsledky; nemusí obracet vnitřní matematiku
/// Random.
///
/// ```java
/// var matches = new ArrayList<Integer>();
/// for (int n = 0; n <= 20; n++) {
///     if (n * n % 10 == 6) matches.add(n);
/// }
/// ```
///
/// Neutrální hledání najde 4, 6, 14 a 16. Předpověz výsledek pro horní mez 10. Vracej všechny shody:
/// krátká stopa může odpovídat více možnostem. Barevný sample (vzorek) je prefix (začátek), nemusí
/// tvořit celou paletu. Jeho položky porovnávej se začátkem vygenerovaného pole; při první neshodě
/// další srovnávání tohoto kandidáta ukonči. Stejná jednotlivá barva neprokazuje stejnou paletu;
/// důkazem je posloupnost.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Najdi operaci převádějící seed na celou posloupnost barev. Ověř, kudy tajemný vzorek přichází do
/// zprávy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Nedokončený nástroj matchingSeeds je vedle main. Může znovu použít palette bez změny escape nebo
/// kreslení.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Zkus seed 0..255, porovnej každou položku vzorku s prefixem palety a shodující long seed přidej do
/// výsledku. Připravenou ochranu délky vzorku zachovej.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program k prozkoumání">
int[] palette(long seed) {
    var random = new Random(seed);
    int[] colours = new int[16];
    for (int i = 0; i < colours.length; i++) {
        float hue = random.nextInt(360) / 360.0F;
        colours[i] = Color.HSBtoRGB(hue, 0.8F, 1);
    }
    return colours;
}

BufferedImage render(long seed, double span) {
    if (!Double.isFinite(span) || span <= 1.0E-5 || span > 6 || !Double.isFinite(centerX + centerY)) {
        throw new IllegalArgumentException("Span must be 0.00001..6.");
    }
    var image = new BufferedImage(480, 360, BufferedImage.TYPE_INT_RGB);
    int[] colours = palette(seed);
    for (int y = 0; y < 360; y++) {
        for (int x = 0; x < 480; x++) {
            int n = escape(centerX + (x / 479.0 - 0.5) * span, centerY + (y / 359.0 - 0.5) * span * 0.75);
            image.setRGB(x, y, n == 160 ? 0 : colours[n % colours.length]);
        }
    }
    return image;
}

int escape(double x, double y) {
    double a = 0;
    double b = 0;
    int steps = 0;
    while (a * a + b * b <= 4 && steps < 160) {
        double next = a * a - b * b + x;
        b = 2 * a * b + y;
        a = next;
        steps++;
    }
    return steps;
}
double centerX = -0.6;
double centerY = 0;


void showImages(String title, String[] captions, java.awt.image.BufferedImage[] images) {
    if (java.awt.GraphicsEnvironment.isHeadless()) return;
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame(title);
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        var pictures = new javax.swing.JPanel(new java.awt.GridLayout(1, images.length, 8, 8));
        for (int i = 0; i < images.length; i++) {
            var picture = new javax.swing.JPanel(new java.awt.BorderLayout());
            picture.add(new javax.swing.JLabel(captions[i], javax.swing.SwingConstants.CENTER), java.awt.BorderLayout.NORTH);
            double scale = Math.min(1, Math.min(900.0 / images.length / images[i].getWidth(), 550.0 / images[i].getHeight()));
            var icon = new javax.swing.ImageIcon(images[i].getScaledInstance(
                    Math.max(1, (int)(images[i].getWidth() * scale)),
                    Math.max(1, (int)(images[i].getHeight() * scale)), java.awt.Image.SCALE_FAST));
            picture.add(new javax.swing.JLabel(icon), java.awt.BorderLayout.CENTER);
            pictures.add(picture);
        }
        frame.add(pictures);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}


java.util.List<Long> matchingSeeds(int[] sample) {
    if (sample.length < 1 || sample.length > 16) {
        throw new IllegalArgumentException("Supply 1..16 palette colours.");
    }
    var matches = new java.util.ArrayList<Long>();
    for (long seed = 0; seed <= 255; seed++) {
        int[] colours = palette(seed);
        boolean match = true;
        for (int i = 0; i < sample.length; i++) {
            if (sample[i] != colours[i]) {
                match = false;
                break;
            }
        }
        if (match) matches.add(seed);
    }
    return matches;
}

BufferedImage paletteStrip(int[] colours) {
    var image = new BufferedImage(colours.length * 30, 40, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) image.setRGB(x, y, colours[x / 30]);
    }
    return image;
}

void main() {
    int[] mystery = {-13420801, -13369474, -52415, -13369522, -13369416, -27085, -13369386, -6684877, -5112013, -52256, -13369355, -35789, -52405, -6684877, -13369372, -13409281};
    try {
        IO.println("Mystery palette: " + java.util.Arrays.toString(mystery));
        IO.println("Matching seeds 0..255: " + matchingSeeds(mystery));
        String seedText = IO.readln("Palette seed [42], /q to quit: ");
        if (seedText == null || seedText.strip().equalsIgnoreCase("/q")) return;
        String spanText = IO.readln("Horizontal span [3.2], /q to quit: ");
        if (spanText == null || spanText.strip().equalsIgnoreCase("/q")) return;
        long seed = seedText.isBlank() ? 42 : Long.parseLong(seedText.strip());
        double size = spanText.isBlank() ? 3.2 : Double.parseDouble(spanText.strip());
        showImages("Mandelbrot Mystery", new String[]{"Mystery palette", "Chosen seed " + seed},
                   new BufferedImage[]{paletteStrip(mystery), render(seed, size)});
    } catch (Exception failure) {
        IO.println("Cannot draw this experiment: " + failure.getMessage());
    }
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
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
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
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
