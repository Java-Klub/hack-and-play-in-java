// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L11.10 · SecretPNGPostcard
///
/// Pošli tajnou pohlednici, jejíž viditelný obrázek mnoho neprozradí. Program skryje tvůj text v datech
/// PNG bodů a pokusí se jej přečíst z uložené kartičky.
///
/// **Tvoje mise:** Opětovné čtení pohlednice v adresáři selže, protože načítání adresář zahodí. Oprav
/// otevření zvoleného souboru a zachovej výslovné chyby nečitelného obrázku.
///
/// **Dnes trénuješ:** Path (cesta) určuje skutečný resource (prostředek) k otevření, ne pouhý název.
/// Try-with-resources (automatická správa prostředků) vlastní input stream (vstupní proud); dekódování
/// obrázku a skryté zprávy jsou další kroky.
///
/// Program přečte data/postcard.png přes readMessage a zobrazí získanou zprávu s pohlednicí.
/// Ukázková pohlednice vznikne pouze při neexistenci souboru.
String readMessage(Path path) throws IOException {
    try (var input = Files.newInputStream(Path.of(path.getFileName().toString()))) {
        var image = ImageIO.read(input);
        if (image == null) throw new IOException("Input is not a readable image.");
        return decode(image);
    }
}

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

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `path.getFileName()` — Zahodí všechny adresáře ze zvolené cesty k pohlednici.
/// - `Files.newInputStream` — Otevře bajty z předané cesty; zahozený adresář nedokáže odhadnout.
/// - `image == null` — ImageIO takto může oznámit nerozpoznaný obrázek místo vrácení bodů.
/// - `decode(image)` — Vybere skrytý text až po načtení správného obrázku.
///
/// ### Co už obstarává připravená část
///
/// Ukázkový data/postcard.png vznikne pouze při neexistenci. main zavolá readMessage s úplným Path
/// (cestou) a zobrazí skutečnou pohlednici s přečtenou zprávou; existující obsah zachová.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Celý Path obsahuje umístění zvolené volající částí.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Samotné getFileName vnořenou pohlednici nenajde.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// ImageIO čte vlastní stream (proud); zachovej kontrolu null obrazu i existující bit decoder (čtečku
/// bitů).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String message = "The moon has excellent Wi-Fi.";

BufferedImage encode(BufferedImage original, String text) {
    byte[] payload = text.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    int capacity = original.getWidth() * original.getHeight() * 3 / 8 - 4;
    if (payload.length > capacity) {
        throw new IllegalArgumentException("Message exceeds image capacity.");
    }
    byte[] data = new byte[payload.length + 4];
    java.nio.ByteBuffer.wrap(data).putInt(payload.length).put(payload);
    var image = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    g.drawImage(original, 0, 0, null);
    g.dispose();
    for (int bit = 0; bit < data.length * 8; bit++) {
        int pixel = bit / 3;
        int x = pixel % image.getWidth();
        int y = pixel / image.getWidth();
        int shift = 16 - bit % 3 * 8;
        int rgb = image.getRGB(x, y);
        int value = (data[bit / 8] >> (7 - bit % 8)) & 1;
        image.setRGB(x, y, (rgb & ~(1 << shift)) | (value << shift));
    }
    return image;
}

int readByte(BufferedImage image, int index) {
    int result = 0;
    for (int offset = 0; offset < 8; offset++) {
        int bit = index * 8 + offset;
        int pixel = bit / 3;
        int rgb = image.getRGB(pixel % image.getWidth(), pixel / image.getWidth());
        result = result * 2 + ((rgb >> (16 - bit % 3 * 8)) & 1);
    }
    return result;
}

String decode(BufferedImage image) {
    int capacity = image.getWidth() * image.getHeight() * 3 / 8;
    if (capacity < 4) {
        throw new IllegalArgumentException("Image is too small.");
    }
    int length = 0;
    for (int i = 0; i < 4; i++) {
        length = (length << 8) | readByte(image, i);
    }
    if (length < 0 || length > capacity - 4) {
        throw new IllegalArgumentException("Invalid message header.");
    }
    byte[] data = new byte[length];
    for (int i = 0; i < length; i++) {
        data[i] = (byte)readByte(image, i + 4);
    }
    return new String(data, java.nio.charset.StandardCharsets.UTF_8);
}

/// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).
BufferedImage render(long seed, double option) {
    var image = new BufferedImage(400, 240, BufferedImage.TYPE_INT_RGB);
    var random = new Random(seed);
    for (int y = 0; y < 240; y++) {
        for (int x = 0; x < 400; x++) {
            image.setRGB(x, y, Color.HSBtoRGB(x / 400.0F, 0.5F, 0.6F + random.nextFloat() * 0.4F));
        }
    }
    return encode(image, message);
}

/// Připravená export boundary (hranice ukládání): používá místní cestu; encoding (kódování) zajišťuje JDK.
void save(Path path, long seed, double option) throws Exception {
    Files.createDirectories(path.toAbsolutePath().getParent());
    if (!ImageIO.write(render(seed, option), "png", path.toFile())) {
        throw new IllegalStateException("PNG writer missing.");
    }
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
    Path input = Path.of("data", "postcard.png");
    try {
        if (!Files.exists(input)) save(input, 42, 1);
        String recovered = readMessage(input);
        var image = ImageIO.read(input.toFile());
        if (image == null) throw new IOException("The postcard is not a readable PNG.");
        IO.println("Recovered from " + input + ": " + recovered);
        showImages("Secret PNG Postcard", new String[]{recovered}, new java.awt.image.BufferedImage[]{image});
    } catch (Exception failure) {
        IO.println("Cannot read requested postcard " + input + ": " + failure.getMessage() + ". Repair readMessage; Save the source, run its check command, then play again.");
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
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `%` — Remainder (zbytek po dělení); hodí se pro opakující se pozice nebo tahy.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
/// - `/` — Dělí čísla. Dělení dvou int hodnot zahodí desetinnou část.
/// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
///   List<String> lomené závorky popisují element type (typ prvku).
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `null` — Žádný object (objekt) tu není.
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
