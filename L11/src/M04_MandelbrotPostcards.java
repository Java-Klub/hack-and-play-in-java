// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L11.04 · MandelbrotPostcards
///
/// Pošli pohlednici z okraje Mandelbrotovy množiny, kde změna měřítka odhaluje nové detaily. Seed
/// (počáteční hodnoty) mají dát stejné krajině různé, opakovatelné barevné nálady.
///
/// **Tvoje mise:** Každý seed (počáteční hodnota) vytvoří stejnou paletu, protože náhoda barev vždy
/// začíná nulou. Obnov požadovaný seed beze změny výpočtu fraktálu.
///
/// **Dnes trénuješ:** Random se seed (počáteční hodnotou) dává opakovatelnou sequence (posloupnost), ne
/// pokaždé novou. Jeden vybraný hue (barevný tón) zde řídí paletu: stejné seed mají souhlasit a různé se
/// mají umět lišit.
///
/// Po Run File zadej seed (počáteční hodnotu) 42 a otevři mandelbrot-postcards-42.png, potom spuštění
/// zopakuj s 43 pro porovnání palet.
/// Po opravě: Save → Test File → zahraj si znovu.
BufferedImage render(long seed, double span) {
    if (!Double.isFinite(span) || span <= 1.0E-5 || span > 6 || !Double.isFinite(centerX + centerY)) {
        throw new IllegalArgumentException("Span must be 0.00001..6.");
    }
    var image = new BufferedImage(480, 360, BufferedImage.TYPE_INT_RGB);
    float hue = new Random(seed).nextFloat();
    for (int y = 0; y < 360; y++) {
        for (int x = 0; x < 480; x++) {
            int n = escape(centerX + (x / 479.0 - 0.5) * span, centerY + (y / 359.0 - 0.5) * span * 0.75);
            image.setRGB(x, y, n == 160 ? 0 : Color.HSBtoRGB(hue + n / 55.0F, 0.8F, 1));
        }
    }
    return image;
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `long seed` — Požadovaný počátek od caller (volající části) pro opakovatelné barevné volby.
/// - `new Random(0)` — Pokaždé vytvoří stejnou náhodnou sequence (posloupnost) a ignoruje seed parameter
///   (parametr).
/// - `nextFloat()` — Vybere jeden posun barevného tónu ze zvoleného generátoru.
/// - `n == 160 ? 0 :` — Nechá vnitřní body černé a obarví body, které uniknou.
///
/// ### Co už obstarává připravená část
///
/// Main se zeptá na seed (počáteční hodnotu) a save volá render. Připravený escape provádí omezené
/// opakování s komplexními čísly; pixel loop (cyklus obrazových bodů) spojí počet opakování s barvou a
/// uloží PNG.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Method (metoda) už dostává seed (počáteční hodnotu).
/// 2. Pevný seed udělá všechny palety stejné.
/// 3. Random palety vytvoř z parameter (parametru), jednou na render.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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

/// Připravená export boundary (hranice ukládání): používá místní cestu; encoding (kódování) zajišťuje JDK.
void save(Path path, long seed, double option) throws Exception {
    Files.createDirectories(path.toAbsolutePath().getParent());
    if (!ImageIO.write(render(seed, option), "png", path.toFile())) {
        throw new IllegalStateException("PNG writer missing.");
    }
}

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Escape je připravený omezený výpočet Mandelbrota.
/// Render vybere jeden odstín ze seed (počáteční hodnoty) a obarví počty kroků; save/main uloží místní pohlednici.
void main(String[] args) throws Exception {
    if (args.length == 0) {
        String entered = IO.readln("Seed (blank 42, q quits): ");
        if (entered == null || entered.strip().equalsIgnoreCase("q")) return;
        String seedText = entered.isBlank() ? "42" : entered.strip();
        args = new String[]{"mandelbrot-postcards-" + seedText + ".png", seedText};
    }

    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "mandelbrot-postcards.png");
        long seed = args.length > 1 ? Long.parseLong(args[1]) : 42;
        double option = args.length > 2 ? Double.parseDouble(args[2]) : 3.2;
        if (args.length > 3) {
            centerX = Double.parseDouble(args[3]);
        }
        if (args.length > 4) {
            centerY = Double.parseDouble(args[4]);
        }
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
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
/// - `Path` — Umístění v souborech, nikoli obsah souboru.
/// - `Files` — JDK operace pro čtení a zápis místních souborů.
// </editor-fold>

