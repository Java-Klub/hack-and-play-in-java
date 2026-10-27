// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L11.09 · PixelSortingGlitch
///
/// Vytáhni z obrázku pruhy řazením vybraných úseků bodů. Práh mění místa, kde se glitch zachytí a kde
/// původní obraz přežije.
///
/// **Tvoje mise:** Upravený obrázek se uloží jako lost-pixels.png místo zvoleného názvu. Oprav cíl
/// uložení a neměň vstupní obrázek.
///
/// **Dnes trénuješ:** Path (cesta) je součást export contract (smlouvy ukládání), včetně názvu. Úprava
/// obrázku a cíl jsou dvě oddělené volby; zápis má respektovat obě a nevymýšlet sousední soubor.
///
/// Po Run File stiskni Enter pro úpravu připraveného obrázku do exports/pixel-sorted.png a potom otevři
/// právě tento cíl.
/// Po opravě: Save → Test File → zahraj si znovu.
void saveSorted(Path path, BufferedImage input, int threshold) throws IOException {
    Files.createDirectories(path.toAbsolutePath().getParent());
    if (!ImageIO.write(sort(input, threshold), "png", path.resolveSibling("lost-pixels.png").toFile())) {
        throw new IOException("PNG writer missing.");
    }
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `Files.createDirectories` — Připraví cílové adresáře a funguje i tehdy, když už existují.
/// - `sort(input, threshold)` — Vytvoří upravený obrázek z předaného input (vstupu) a prahu.
/// - `path.resolveSibling("lost-pixels.png")` — Vybere jiný název ve stejném nadřazeném adresáři a
///   ignoruje požadovaný název.
/// - `!ImageIO.write` — Výsledek false od writer (zapisovače) se změní ve výslovnou chybu uložení.
///
/// ### Co už obstarává připravená část
///
/// Připravený sort kopíruje input (vstup) a řadí omezené úseky podle jasu. Main může použít vestavěný
/// obrázek, zeptá se na cíl a volá saveSorted; ImageIO dodá PNG encoding (kódování).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. ResolveSibling mění zadaný název.
/// 2. Sort už tvoří nezávislý výstupní obraz.
/// 3. Encoder (zapisovač obrazu) má použít původní Path; vytvoření adresářů zachovej.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
BufferedImage demo() {
    var image = new BufferedImage(240, 160, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < 160; y++) {
        for (int x = 0; x < 240; x++) {
            image.setRGB(x, y, Color.HSBtoRGB(x / 240.0F, 0.7F, 0.2F + y / 200.0F));
        }
    }
    return image;
}

int light(int rgb) {
    return (((rgb >> 16) & 255) + ((rgb >> 8) & 255) + (rgb & 255)) / 3;
}

BufferedImage sort(BufferedImage input, int threshold) {
    if (threshold < 0 || threshold > 255) {
        throw new IllegalArgumentException("Threshold: 0..255.");
    }
    var output = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
    for (int y = 0; y < input.getHeight(); y++) {
        int[] row = input.getRGB(0, y, input.getWidth(), 1, null, 0, input.getWidth());
        int start = 0;
        while (start < row.length) {
            if (light(row[start]) < threshold) {
                start++;
                continue;
            }
            int end = start;
            while (end < row.length && light(row[end]) >= threshold) {
                end++;
            }
            Integer[] streak = new Integer[end - start];
            for (int i = 0; i < streak.length; i++) {
                streak[i] = row[start + i];
            }
            Arrays.sort(streak, Comparator.comparingInt(this::light));
            for (int i = 0; i < streak.length; i++) {
                row[start + i] = streak[i];
            }
            start = end;
        }
        output.setRGB(0, y, row.length, 1, row, 0, row.length);
    }
    return output;
}

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Sort zachová tmavé hranice i vstupní obraz; řazení úseků je připravené.
/// Main volá saveSorted pro skutečný zvolený výstup; ImageIO kóduje PNG přes zadaný Path.
void main(String[] args) throws Exception {
    if (args.length == 0) {
        String entered = IO.readln("Output path (blank exports/pixel-sorted.png, q quits): ");
        if (entered == null || entered.strip().equalsIgnoreCase("q")) return;
        String destination = entered.isBlank() ? "exports/pixel-sorted.png" : entered.strip();
        args = new String[]{"-", destination};
    }

    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        var input = args.length == 0 || args[0].equals("-") ? demo() : ImageIO.read(Path.of(args[0]).toFile());
        if (input == null) {
            throw new IllegalArgumentException("Input must be a readable image.");
        }
        Path output = Path.of(args.length > 1 ? args[1] : "pixel-sorted.png");
        int option = args.length > 2 ? Integer.parseInt(args[2]) : 110;
        saveSorted(output, input, option);
        IO.println("Saved: " + output.toAbsolutePath());
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
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
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
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
/// - `Path` — Umístění v souborech, nikoli obsah souboru.
/// - `Files` — JDK operace pro čtení a zápis místních souborů.
// </editor-fold>

