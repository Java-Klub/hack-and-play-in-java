// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # 12.20 · MagicEyeStrip
///
/// Místo anonymního čísla si veď deník tečkovaných pokusů. Komentáře vysvětlí volby a poslední
/// uložený pokus znovu vytvoří obraz.
///
/// **Tvoje mise:** Proměň seed.txt v deník pokusů: po odstranění okrajových mezer přeskoč prázdné
/// řádky i řádky začínající # a jako zvolený seed použij poslední zbývající celé číslo. Chybný
/// číselný řádek i deník bez seed mají dál ohlásit chybu. Běžný soubor s jediným číslem musí stále
/// fungovat.
///
/// **Zkoumání:** Odvoď a implementuj malý formát uchovaného deníku pokusů.
///
/// Spusť jednou pro vytvoření chybějícího seed.txt. Na samostatné řádky přidej # komentář, 42 a
/// pozdější -7 a spusť znovu. Terminál oznámí vybraný seed a Java okno jeho tečkované pole.

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Připrav výzvu opakovatelnosti: kamarádovi dej jen soubor se seed (počátečním stavem) a požádej ho
/// o stejnou tečkovanou plochu. Potom změň skrytý hloubkový tvar a soubor nech stejný. Vysvětli, co
/// reprodukuje uložené číslo a co patří programu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F32">
/// ### Files (soubory): cíl a životnost prostředku
///
/// `Path` popisuje umístění; jeho vytvoření nevytvoří soubor. Uvnitř metody s deklarací `throws
/// IOException` tento kód zapíše poznámku:
///
/// ```java
/// Path file = Path.of("exports", "note.txt");
/// Files.createDirectories(file.getParent());
/// try (var writer = Files.newBufferedWriter(file)) {
///     writer.write("Hi");
/// }
/// ```
///
/// Výsledek je exports/note.txt obsahující `Hi`. `getParent()` vybere exports; `getFileName()` vybere
/// jen note.txt a zahodí složku. Try-with-resources (automatické uzavření prostředku) uzavře writer
/// na konci bloku i při selhání zápisu. `throws` nechá I/O failure (chybu vstupu/výstupu) řešit
/// volajícího; tady ji neřeší. Také obrazové a zvukové encodery (kodéry) potřebují skutečný cíl,
/// nikoli nesouvisející paměťový buffer (vyrovnávací paměť).
///
/// Předpověz: pokud file změníš na `Path.of("cards", "one.txt")`, kam se poznámka zapíše?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Deník průzkumníka · vyložený příklad">
/// ### File format (formát souboru) je sada dohod
///
/// Files.readAllLines(path) vrací List<String>, jednu položku pro každý řádek bez jeho zakončení.
/// startsWith("#") se ptá, zda řetězec začíná #. Formát určuje význam řádků; název souboru není jeho
/// obsah.
///
/// ```java
/// String last = null;
/// for (String line : List.of("  red ", "", "blue")) {
///     String text = line.strip();
///     if (text.isEmpty()) continue;
///     last = text;
/// }
/// ```
///
/// Tento neutrální průchod nechá last jako blue. Předpověz výsledek po nahrazení blue mezerami.
/// Sentinel (rozlišovací hodnota), například null, odliší žádnou přijatou hodnotu od přijaté nuly. Na
/// číslo převáděj každý datový řádek, aby pozdější platné číslo nezakrylo překlep. Chyba čtení a
/// chybný obsah jsou různé chyby; ani jedna neopravňuje soubor přepsat náhradní hodnotou.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Najdi čtení dodávající seed vypisovaný v main. Před deníkem vyzkoušej jedno číslo.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// readSeed nyní převádí celý soubor najednou. Čti řádky, každý očisti a před převodem rozliš
/// komentář, prázdný řádek a data.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Pamatuj si poslední úspěšně převedený long, ale převáděj každý datový řádek. Nakonec odmítni
/// chybějící hodnotu; vybranou vrať bez zápisu do souboru.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program k prozkoumání">
long readSeed(Path path) throws IOException {
    long selected = 0;
    boolean found = false;
    for (String line : Files.readAllLines(path)) {
        String text = line.strip();
        if (text.isEmpty() || text.startsWith("#")) continue;
        selected = Long.parseLong(text);
        found = true;
    }
    if (!found) throw new NumberFormatException("The notebook contains no seed.");
    return selected;
}

BufferedImage render(long seed, double option) {
    int depth = (int)option;
    if (!Double.isFinite(option) || depth < 1 || depth > 24) {
        throw new IllegalArgumentException("Disparity: 1..24 pixels.");
    }
    var image = new BufferedImage(600, 240, BufferedImage.TYPE_INT_RGB);
    var random = new Random(seed);
    for (int y = 0; y < 240; y++) {
        for (int x = 0; x < 600; x++) {
            int shift = x > 230 && x < 420 && y > 60 && y < 180 ? 80 - depth : 80;
            int colour = x < 80 ? random.nextBoolean() ? 0xFFFFFF : 0 : image.getRGB(x - shift, y);
            image.setRGB(x, y, colour);
        }
    }
    return image;
}


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


void main() {
    Path input = Path.of("seed.txt");
    try {
        if (!Files.exists(input)) Files.writeString(input, "42");
        long seed = readSeed(input);
        IO.println("Loaded seed from seed.txt: " + seed);
        showImages("Magic Eye Strip", new String[]{"Seed " + seed}, new java.awt.image.BufferedImage[]{render(seed, 16)});
    } catch (IOException | IllegalArgumentException failure) {
        IO.println("Cannot load seed.txt: " + failure.getMessage() + ". Check the notebook data lines.");
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
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
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
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek.
/// - `Path` — Umístění v souborech, nikoli obsah souboru.
/// - `Files` — JDK operace pro čtení a zápis místních souborů.
// </editor-fold>
