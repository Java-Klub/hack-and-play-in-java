// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L12.12 · MountainPostcards
///
/// Vytvoř horskou siluetu a porovnej ji se zrcadleným dvojčetem. Zrcadlení má změnit levé a pravé
/// vrcholy, ne vymyslet jinou horu.
///
/// **Tvoje mise:** Volba zrcadlení vytvoří nezměněnou kopii hřebene. Sestav obrácený výsledek a zachovej
/// původní height array (pole výšek) i starý nezrcadlený režim.
///
/// **Dnes trénuješ:** Pure transformation (čistá transformace) vytvoří nové uspořádání bez změny input
/// (vstupu). Oddělené obrácení hřebene dává nové volbě jednu odpovědnost a zachovává nezávislost tvorby
/// na zobrazení.
///
/// Po Run File zadej n pro mountain-old.png, potom spusť znovu a stiskni Enter pro zrcadlený
/// mountain-new.png.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F36 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
double[] reverseRidge(double[] heights) {
    double[] reversed = new double[heights.length];
    for (int i = 0; i < heights.length; i++) reversed[i] = heights[heights.length - 1 - i];
    return reversed;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F36">
/// ### Transfer (přenos): zachovej slib a přidej volbu
///
/// Extract helper (vyčlenění pomocné metody) pojmenuje jednu odpovědnost. Vstupy a výsledek tvoří
/// contract (smlouvu), kterou můžeš využít jinde.
///
/// ```java
/// String surround(String text, boolean stars) {
///     String edge = stars ? "*" : "#";
///     return edge + text + edge;
/// }
/// ```
///
/// `surround("hi", false)` vrátí `"#hi#"`; původní styl s mřížkami dál funguje. Volající může požádat
/// o nový styl bez opakování celé operace. Ověř obě volby, prázdný vstup i jiné slovo. Tests (testy)
/// ověří výsledky; vysvětlení, kdo vybírá styl a kdo sestavuje popisek, ověří rozdělení odpovědností.
/// Úspěšný nový režim nemá potichu měnit původní.
///
/// Předpověz: co vytvoří `surround("", true)` a která část by se měnila při změně obklopujícího
/// znaku?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `double[] heights` — Uspořádané array (pole) výšek hřebene od jedné strany obrázku ke druhé.
/// - `heights.clone()` — Vytvoří nezávislé array (pole), ale nechá každou hodnotu na původním index
///   (pořadovém čísle).
/// - `return` — Předá výsledné array (pole) kreslení; nemusí jít o původní objekt.
///
/// ### Co už obstarává připravená část
///
/// Připravený midpoint generator (generátor půlením úseku) vytvoří hřeben jednou podle seed (počáteční
/// hodnoty). Render vybere původní hřeben nebo výsledek reverseRidge a vykreslí jej; main se zeptá na
/// zrcadlení a uloží odlišně pojmenované PNG k porovnání.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Poslední původní hodnota patří na index (pořadové číslo) nula výsledku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Cílové i přiřaď ke zdroji length-1-i.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Vysvětli, proč převrácení patří do samostatné transformace hodnot a ne do náhodné tvorby hřebene.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
double[] ridge(Random random, double roughness) {
    double[] heights = new double[257];
    heights[0] = random.nextDouble() * 40;
    heights[256] = random.nextDouble() * 40;
    double amplitude = 90;
    for (int gap = 256; gap > 1; gap /= 2) {
        for (int left = 0; left < 256; left += gap) {
            heights[left + gap / 2] = (heights[left] + heights[left + gap]) / 2 + (random.nextDouble() * 2 - 1) * amplitude;
        }
        amplitude *= roughness;
    }
    return heights;
}

/// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).
BufferedImage render(long seed, double roughness) {
    if (!Double.isFinite(roughness) || roughness < 0.2 || roughness > 0.9) {
        throw new IllegalArgumentException("Roughness: 0.2..0.9.");
    }
    var image = new BufferedImage(640, 400, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    var random = new Random(seed);
    try {
        for (int y = 0; y < 400; y++) {
            g.setColor(new Color(255 - y / 4, 120 - y / 6, 120 + y / 4));
            g.drawLine(0, y, 640, y);
        }
        for (int layer = 0; layer < 5; layer++) {
            var heights = ridge(random, roughness);
            if (mirrored) heights = reverseRidge(heights);
            var polygon = new Polygon();
            polygon.addPoint(0, 400);
            for (int x = 0; x < 257; x++) {
                polygon.addPoint(x * 640 / 256, Math.max(20, Math.min(390, 170 + layer * 38 + (int)heights[x])));
            }
            polygon.addPoint(640, 400);
            g.setColor(new Color(100 - layer * 18, 95 - layer * 15, 135 - layer * 20));
            g.fillPolygon(polygon);
        }
    } finally {
        g.dispose();
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

boolean mirrored = true;

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Ridge tvoří výšky ze seed (počáteční hodnoty); reverseRidge vlastní jen nové zrcadlení.
/// Render skládá pět vrstev a volá transformaci; save/main zachovávají místní PNG export.
void main(String[] args) throws Exception {
    if (args.length == 0) {
        String entered = IO.readln("Mirror ridge? y/n (blank y, q quits): ");
        if (entered == null || entered.strip().equalsIgnoreCase("q")) return;
        mirrored = !entered.strip().equalsIgnoreCase("n");
        args = new String[]{"mountain-" + (mirrored ? "new" : "old") + ".png"};
    }

    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "mountain-postcards.png");
        long seed = args.length > 1 ? Long.parseLong(args[1]) : 42;
        double option = args.length > 2 ? Double.parseDouble(args[2]) : 0.65;
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

