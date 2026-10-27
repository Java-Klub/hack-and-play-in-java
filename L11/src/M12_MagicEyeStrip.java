// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L11.12 · MagicEyeStrip
///
/// Vytvoř opakovaný tečkovaný pás, který může skrývat prostorový tvar při spojení sousedních vzorů očima.
/// Seed file (soubor počáteční hodnoty) dovolí uložit a zopakovat konkrétní tečky.
///
/// **Tvoje mise:** Program převádí název souboru na číslo místo čtení čísla uvnitř. Oprav načítání seed
/// (počáteční hodnoty) včetně mezer, chybného obsahu a chybějícího souboru.
///
/// **Dnes trénuješ:** Path (cesta) pojmenovává umístění; jeho text není obsahem tohoto místa. Před
/// převodem čísla přečti file (soubor), aby chybějící a chybně zapsaná data zůstala různými chybami.
///
/// Po Run File stiskni Enter pro čtení seed.txt, vytvořeného s 42 jen pokud chybí, a potom otevři
/// magic-eye-strip.png.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F32 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
long readSeed(Path path) throws IOException {
    return Long.parseLong(Files.readString(path).strip());
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
/// - `Path path` — Vybrané umístění textového file (souboru), nikoli samotný číselný seed (počáteční
///   hodnota).
/// - `path.toString()` — Vytvoří cestu jako seed.txt, ne text uložený uvnitř seed.txt.
/// - `Long.parseLong` — Převede číselný String (text) na long a odmítne chybný obsah.
/// - `throws IOException` — Umožní skutečné chybě čtení file (souboru) dojít k obsluze chyby u caller
///   (volající části).
///
/// ### Co už obstarává připravená část
///
/// Main běžně používá seed.txt a vytvoří jej s 42 jen tehdy, když chybí. Připravený render tvoří
/// opakovatelné tečky a kopíruje je v rozestupech podle hloubky; save zapíše magic-eye-strip.png.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Path je umístění, nikoli uložený obsah.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Před převodem čísla použij Files.readString.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Mezery jsou povolené; chybný obsah a chybějící file (soubor) zůstanou různé chyby.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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

/// Připravená export boundary (hranice ukládání): používá místní cestu; encoding (kódování) zajišťuje JDK.
void save(Path path, long seed, double option) throws Exception {
    Files.createDirectories(path.toAbsolutePath().getParent());
    if (!ImageIO.write(render(seed, option), "png", path.toFile())) {
        throw new IllegalStateException("PNG writer missing.");
    }
}

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// ReadSeed načítá volitelný textový input (vstup); render jím nastaví šum teček.
/// Připravené pravidlo periody/hloubky tvoří středový tvar; save/main uloží PNG bez síťové závislosti.
void main(String[] args) throws Exception {
    if (args.length == 0) {
        String entered = IO.readln("Seed text path (blank seed.txt, created with 42 if absent; q quits): ");
        if (entered == null || entered.strip().equalsIgnoreCase("q")) return;
        Path seedPath = Path.of(entered.isBlank() ? "seed.txt" : entered.strip());
        try {
            if (entered.isBlank() && !Files.exists(seedPath)) Files.writeString(seedPath, "42");
            long seed = readSeed(seedPath);
/// Připravená export boundary (hranice ukládání): používá místní cestu; encoding (kódování) zajišťuje JDK.
            save(Path.of("magic-eye-strip.png"), seed, 16);
            IO.println("Loaded seed: " + seed + "; saved magic-eye-strip.png");
        } catch (IOException | IllegalArgumentException failure) {
            IO.println("Seed input failed: " + failure.getMessage());
        }
        return;
    }

    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "magic-eye-strip.png");
        long seed = args.length > 1 ? Long.parseLong(args[1]) : 42;
        double option = args.length > 2 ? Double.parseDouble(args[2]) : 16;
        if (args.length > 3) seed = readSeed(Path.of(args[3]));
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
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
/// - `Path` — Umístění v souborech, nikoli obsah souboru.
/// - `Files` — JDK operace pro čtení a zápis místních souborů.
// </editor-fold>

