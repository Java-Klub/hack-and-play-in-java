// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L11.10 · SecretPNGPostcard
///
/// Nosič zpráv už funguje. Dej mu skutečný obrázek: použij vlastní PNG místo kresby znovu vytvářené
/// uvnitř programu.
///
/// **Tvoje mise:** Použij data/cover.png jako skutečný obrázkový vstup nové tajné pohlednice. Načti
/// jej, schovej zadanou zprávu a zapiš zvlášť data/postcard.png bez změny obálky. Pozdější Enter má
/// otevřít uloženou kartu beze změny. Výměna obálky ovlivní další nově napsanou kartu, ne automaticky
/// starou.
///
/// **Zkoumání:** Prozkoumej uchovaná vstupní data a vytvoř nástroj obrázek → pohlednice.
///
/// První běh vytvoří ukázkovou obálku. Na vypsanou cestu obálky vlož jiné PNG, napiš novou zprávu a
/// porovnej kartu. Enter otevírá existující kartu; /q skončí.

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// Do obálky navrhni obrazovou hádanku a do dvou karet vlož dvě různé zprávy. Před dekódováním
/// porovnej běžný obraz, potom zjisti, které spodní bity pixelů se změnily. Oba původní obrázky
/// zachovej.
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
/// ### Sleduj bajty přes dvě různé souborové role
///
/// Input path (vstupní cesta) označuje čtená data, output path (výstupní cesta) vytvářená data. Změna
/// objektu v paměti nepřepíše vstupní soubor, dokud do něj výslovně nezapíšeš. Vstup ověř před
/// otevřením cíle, aby nečitelný vstup nesmazal užitečný výstup.
///
/// ```java
/// String original = Files.readString(Path.of("draft.txt"));
/// String edited = original.toUpperCase(Locale.ROOT);
/// Files.writeString(Path.of("poster.txt"), edited);
/// ```
///
/// Obsahuje-li draft.txt Nova, dostane poster.txt NOVA a draft.txt zůstane Nova. Předpověz další běh
/// po změně vstupu na Orbit. Obrázky mají obdobnou hranici: ImageIO.read dekóduje obraz, transformace
/// vytvoří výsledek a ImageIO.write jej zakóduje. PNG encoder (zapisovač) zachová spodní bity
/// používané zdejším formátem zprávy. Otevření existující karty je čtení; vytvoření nové je
/// samostatná volba.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Najdi místo setkání nové zprávy s pozadím. Porovnej tuto cestu s otevřením už uložené karty.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// main vede nové karty přes postcardFromCover. Jeho současné tělo stále volá starý zápis
/// generovaného pozadí; encode už umí schovat text do předaného obrázku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Načti a ověř cover přes ImageIO, zakóduj obrázek i text před otevřením výstupu a zapiš PNG do
/// jiného cíle. Cover zachovej a odmítni jeho použití jako vlastního cíle.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program k prozkoumání">
void save(Path path, long seed, double option) throws Exception {
    Files.createDirectories(path.toAbsolutePath().getParent());
    try (var output = Files.newOutputStream(path)) {
        if (!ImageIO.write(render(seed, option), "png", output)) {
            throw new IOException("PNG writer missing.");
        }
    }
}

String readMessage(Path path) throws IOException {
    try (var input = Files.newInputStream(path)) {
        var image = ImageIO.read(input);
        if (image == null) throw new IOException("Input is not a readable image.");
        return decode(image);
    }
}

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
BufferedImage background(long seed) {
    var image = new BufferedImage(400, 240, BufferedImage.TYPE_INT_RGB);
    var random = new Random(seed);
    for (int y = 0; y < 240; y++) {
        for (int x = 0; x < 400; x++) {
            image.setRGB(x, y, Color.HSBtoRGB(x / 400.0F, 0.5F, 0.6F + random.nextFloat() * 0.4F));
        }
    }
    return image;
}

BufferedImage render(long seed, double option) {
    return encode(background(seed), message);
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


void postcardFromCover(Path cover, Path destination, String text) throws Exception {
    if (Files.exists(destination) && Files.isSameFile(cover, destination)) {
        throw new IllegalArgumentException("Cover and postcard must be different files.");
    }
    message = text;
    save(destination, 42, 1);
}

void main() {
    Path cover = Path.of("data", "cover.png");
    Path card = Path.of("data", "postcard.png");
    try {
        if (!Files.exists(cover)) {
            Files.createDirectories(cover.toAbsolutePath().getParent());
            if (!ImageIO.write(background(42), "png", cover.toFile())) throw new IOException("PNG writer missing.");
        }
        IO.println("Artwork input: " + cover.toAbsolutePath());
        String text = IO.readln("New secret message, Enter opens the saved postcard, /q to quit: ");
        if (text == null || text.strip().equalsIgnoreCase("/q")) return;
        if (!text.isEmpty() || !Files.exists(card)) {
            String chosen = text.isEmpty() ? "Pixel moon" : text;
            postcardFromCover(cover, card, chosen);
        }
        String recovered = readMessage(card);
        if (!text.isEmpty() && !recovered.equals(text)) throw new IOException("The saved message differs from the new text.");
        var image = ImageIO.read(card.toFile());
        if (image == null) throw new IOException("The postcard is not a readable PNG.");
        IO.println("Recovered from " + card.toAbsolutePath() + ": " + recovered);
        showImages("Secret PNG Postcard", new String[]{recovered}, new BufferedImage[]{image});
    } catch (Exception failure) {
        IO.println("Cannot open the postcard: " + failure.getMessage());
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
