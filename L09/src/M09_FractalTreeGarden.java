// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L09.09 · FractalTreeGarden
///
/// Vypěstuj větvící se strom změnou jediného úhlu. Malé pootočení promění úzkou větvičku v rozložitou
/// korunu, kterou stojí za to uložit jako pohlednici.
///
/// **Tvoje mise:** Překlep v úhlu potichu vybere 10 místo slíbené výchozí hodnoty 28. Oprav náhradní
/// hodnotu a dál odmítej čísla mimo povolené rozmezí.
///
/// **Dnes trénuješ:** Parsing (rozbor vstupu) mění text v číslo a může selhat ještě před kontrolou
/// rozmezí. Zachyť konkrétní chybu převodu textu; číslo mimo rozmezí je jiný problém.
///
/// Po Run File zadej úhel větvení od 10 do 70 nebo zkus oops a potom otevři PNG na cestě vypsané za
/// Saved.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F31 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
double readAngle(String text) {
    try {
        double value = Double.parseDouble(text.strip());
        if (!Double.isFinite(value) || value < 10 || value > 70) throw new IllegalArgumentException("angle: 10..70.");
        return value;
    } catch (NumberFormatException invalid) {
        return 10;
    }
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F31">
/// ### Parse (převod), validation (ověření), potom uložení
///
/// Parsing převádí text; validation ověřuje, zda je výsledné číslo povolené. Každý krok může selhat
/// jinak.
///
/// ```java
/// int readCopies(String text) {
///     try {
///         int n = Integer.parseInt(text.strip());
///         if (n < 1 || n > 5) {
///             throw new IllegalArgumentException("Copies: 1..5");
///         }
///         return n;
///     } catch (NumberFormatException invalid) {
///         return 3;
///     }
/// }
/// ```
///
/// `readCopies(" 2 ")` vrátí `2`: převod i ověření uspějí. `"oops"` selže při převodu a přes `catch`
/// vrátí `3`. Číselný text `"8"` se převede, ale následně vyvolá jinou exception (výjimku); tento
/// konkrétní catch ji nezachytí. `return` ukončí metodu; `throw` opustí běžnou cestu. Uložený stav
/// hry měň až po úspěšném ověření.
///
/// Předpověz cestu pro `"five"`, `"5"` a `"0"`.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `Double.parseDouble(text.strip())` — Odstraní okolní mezery a pokusí se o převod na desetinné číslo.
/// - `!Double.isFinite(value)` — Odmítne NaN a nekonečna, která nejsou použitelnými úhly větvení.
/// - `catch (NumberFormatException invalid)` — Zachytí chybný číselný text, nikoli každou možnou
///   programátorskou chybu.
/// - `return 10;` — Současná náhradní hodnota určuje viditelný strom po překlepu.
///
/// ### Co už obstarává připravená část
///
/// Main se zeptá na úhel a před kreslením volá readAngle. Připravená recursive routine (rekurzivní část)
/// zkracuje větve a ukládací část zapíše výsledné PNG místně.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Trasuj tři různé cesty
///
/// V readAngle se `" 24 "` očistí, převede a vrátí jako 24. `"oops"` převést nejde a vstoupí do catch
/// pro NumberFormatException; porovnej současný výsledek s ohlášenou výchozí hodnotou. `"90"` se
/// převede úspěšně, ale neprojde range guard (kontrolou mezí). Jeho IllegalArgumentException není
/// NumberFormatException zachytávaná touto větví, proto pokračuje k volajícímu. NaN a nekonečno jsou
/// také zvláštní číselné hodnoty, které odmítá isFinite. Předpověz, které řádky proběhnou pro `"70"`
/// a `"9"`; každé selhání není důvod nahradit vstup výchozí hodnotou.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Před parsing (rozborem) odstraň okolní mezery.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Zachyť NumberFormatException, nikoli každou chybu programu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Výchozí hodnota je 28; guard (podmínky) rozsahu zůstanou oddělené.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
double shortening = 0.7;

int branches(Graphics2D g, double x, double y, double length, double heading, double fork, int depth) {
    double nx = x + Math.cos(heading) * length;
    double ny = y + Math.sin(heading) * length;
    g.drawLine((int)x, (int)y, (int)nx, (int)ny);
    if (depth == 0) {
        return 1;
    }
    return 1
        + branches(g, nx, ny, length * shortening, heading - fork, fork, depth - 1)
        + branches(g, nx, ny, length * shortening, heading + fork, fork, depth - 1);
}

/// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).
BufferedImage render(long seed, double angle) {
    if (!Double.isFinite(angle) || angle < 10 || angle > 70 || shortening < 0.4 || shortening > 0.78) {
        throw new IllegalArgumentException("Angle 10..70; optional shortening 0.4..0.78.");
    }
    var image = new BufferedImage(600, 480, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(new Color(15, 24, 32));
        g.fillRect(0, 0, 600, 480);
        g.setColor(Color.getHSBColor(new Random(seed).nextFloat(), 0.6F, 1));
        branches(g, 300, 470, 120, -Math.PI / 2, Math.toRadians(angle), 8);
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

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Branches rekurzivně kreslí omezený strom; úhel a zkrácení jsou vysvětlené ovládání.
/// Main čte tuto volbu; save/export a rendering (vykreslení) jsou připravené.
void main(String[] args) throws Exception {
    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    String enteredOption = "";
    if (args.length <= 2) {
        enteredOption = IO.readln("angle 10..70 (blank/default 28, q quits): ");
        if (enteredOption == null || enteredOption.strip().equalsIgnoreCase("q")) return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "fractal-tree-garden.png");
        long seed = args.length > 1 ? Long.parseLong(args[1]) : 42;
        double option = readAngle(args.length > 2 ? args[2] : enteredOption);
        IO.println("angle: " + option);
        if (args.length > 3) {
            shortening = Double.parseDouble(args[3]);
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
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
// </editor-fold>

