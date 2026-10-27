// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.List;
// </editor-fold>

/// # L11.07 · TwoNoisyPictures
///
/// Rozděl tajemství do dvou zašuměných černobílých obrázků. Každý díl má sám vypadat bezradně, zatímco
/// jejich překrytí odhalí skrytý tvar.
///
/// **Tvoje mise:** Každý malý blok znovu použije stejné promíchání, protože se generátor restartuje
/// uvnitř pixel loop (cyklu obrazových bodů). Obnov různorodou, opakovatelnou sequence (posloupnost) pro
/// celý obrázek.
///
/// **Dnes trénuješ:** Random se seed (počáteční hodnotou) je průběžný zdroj voleb. Nové vytvoření se
/// stejným seed zopakuje první volbu; sdílená instance dá dalším blokům další volby.
///
/// Po Run File stiskni Enter pro seed (počáteční hodnotu) 42 a otevři two-noisy-pictures-42.png spolu s
/// obrázky .share1.png a .share2.png.
/// Po opravě: Save → Test File → zahraj si znovu.
BufferedImage[] shares(BufferedImage secret, long seed) {
    var images = new BufferedImage[3];
    for (int i = 0; i < 3; i++) {
        images[i] = new BufferedImage(secret.getWidth() * 2, secret.getHeight() * 2, BufferedImage.TYPE_INT_RGB);
    }
    var random = new Random(seed);
    for (int y = 0; y < secret.getHeight(); y++) {
        for (int x = 0; x < secret.getWidth(); x++) {
            var order = new ArrayList<>(List.of(0, 1, 2, 3));
            Collections.shuffle(order, random);
            boolean ink = (secret.getRGB(x, y) & 255) > 127;
            for (int i = 0; i < 4; i++) {
                int dx = order.get(i) % 2;
                int dy = order.get(i) / 2;
                boolean first = i < 2;
                boolean second = ink ? !first : first;
                images[0].setRGB(x * 2 + dx, y * 2 + dy, first ? 0 : 0xFFFFFF);
                images[1].setRGB(x * 2 + dx, y * 2 + dy, second ? 0 : 0xFFFFFF);
                images[2].setRGB(x * 2 + dx, y * 2 + dy, first || second ? 0 : 0xFFFFFF);
            }
        }
    }
    return images;
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `var random = new Random(seed)` — Vytvoří jeden generátor pro celou operaci, který může zachovat
///   pozici v sequence (posloupnosti).
/// - `Collections.shuffle(order, new Random(seed))` — Nyní pro každý blok restartuje náhodu místo
///   pokračování existujícího generátoru.
/// - `x * 2 + dx` — Umístí dílčí bod do bloku dva krát dva pro tento zdrojový pixel (obrazový bod).
/// - `first || second` — Překrytí je černé, pokud kterákoli část označí tento dílčí bod černě.
///
/// ### Co už obstarává připravená část
///
/// Připravené černobílé pravidlo tvoří doplňkové či shodné vzory dva krát dva podle tajné masky. Main
/// uloží oba díly a potom save přes render uloží jejich překrytí, takže můžeš prozkoumat, co každý
/// obrázek ukazuje.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. New Random(seed) začíná sequence (posloupnost) pokaždé od začátku.
/// 2. Method (metoda) už jeden Random tvoří před pixel loop (cykly bodů obrazu).
/// 3. Existující object (objekt) předej do každého Collections.shuffle.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String message = "JAVA";

BufferedImage mask() {
    if (message.isBlank() || message.length() > 7) {
        throw new IllegalArgumentException("Message: 1..7 characters.");
    }
    var image = new BufferedImage(180, 60, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 38));
        g.drawString(message, 5, 45);
    } finally {
        g.dispose();
    }
    return image;
}

/// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).
BufferedImage render(long seed, double option) {
    return shares(mask(), seed)[2];
}

/// Připravená export boundary (hranice ukládání): používá místní cestu; encoding (kódování) zajišťuje JDK.
void save(Path path, long seed, double option) throws Exception {
    Files.createDirectories(path.toAbsolutePath().getParent());
    if (!ImageIO.write(render(seed, option), "png", path.toFile())) {
        throw new IllegalStateException("PNG writer missing.");
    }
}

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Shares vytvoří dvě masky po dvou černých tečkách a jejich překrytí.
/// Připravené doplňování skrývá bílé/černé pixel (body obrazu) zprávy; main uloží obě části i překrytí.
void main(String[] args) throws Exception {
    if (args.length == 0) {
        String entered = IO.readln("Seed (blank 42, q quits): ");
        if (entered == null || entered.strip().equalsIgnoreCase("q")) return;
        String seedText = entered.isBlank() ? "42" : entered.strip();
        args = new String[]{"two-noisy-pictures-" + seedText + ".png", seedText};
    }

    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "two-noisy-pictures.png");
        long seed = args.length > 1 ? Long.parseLong(args[1]) : 42;
        double option = 1;
        if (args.length > 2) {
            message = args[2];
        }
        Files.createDirectories(path.toAbsolutePath().getParent());
        var images = shares(mask(), seed);
        ImageIO.write(images[0], "png", Path.of(path + ".share1.png").toFile());
        ImageIO.write(images[1], "png", Path.of(path + ".share2.png").toFile());
/// Připravená export boundary (hranice ukládání): používá místní cestu; encoding (kódování) zajišťuje JDK.
        save(path, seed, option);
        IO.println("Saved: " + path.toAbsolutePath());
    } catch (IllegalArgumentException invalid) {
        IO.println("Invalid options: " + invalid.getMessage());
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
/// - `boolean` — Hodnota true nebo false: ano nebo ne.
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
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
/// - `Path` — Umístění v souborech, nikoli obsah souboru.
/// - `Files` — JDK operace pro čtení a zápis místních souborů.
// </editor-fold>

