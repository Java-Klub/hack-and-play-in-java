// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L04.07 · KochSnowflakeCards
///
/// Vytvoř pohlednici se sněhovou vločkou, jejíž tři strany rostou do menších klikatých úseků. Připravené
/// stavění hrany přidá jemné detaily; tvoje část rozhoduje, zda se tvar uzavře po celém obvodu.
///
/// **Tvoje mise:** Uložené vločce chybí celá strana. Oprav smyčku stran ve snowflake tak, aby index
/// (pozice) rohů 0, 1 a 2 navštívila právě jednou; připravené stavění hrany zachovej.
///
/// **Dnes trénuješ:** Rozsah for nad pozicemi: tři uložené rohy mají index (pozice) 0, 1 a 2. Počáteční
/// hodnota a ukončovací porovnání společně rozhodují, které strany budou v obrázku existovat.
///
/// Použij Run File, obnov projekt v kartě NetBeans Files a otevři koch-snowflake-cards.png, abys prohlédl
/// chybějící stranu.
/// Po opravě: Save → Test File → zahraj si znovu.
int snowflake(Graphics2D g, int level) {
    int[] xs = {270, 70, 470};
    int[] ys = {45, 390, 390};
    int segments = 0;
    for (int side = 1; side < 3; side++) {
        int next = (side + 1) % 3;
        segments += edge(g, xs[side], ys[side], xs[next], ys[next], level);
    }
    return segments;
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `for (int side = 1; side < 3; side++)` — Počáteční side, podmínka pokračování a zvětšení o jeden
///   tvoří rozsah této smyčky.
/// - `int next = (side + 1) % 3;` — Připravený výraz vybere následující roh a z posledního se vrátí k
///   prvnímu.
/// - `xs[side]` — Vyhledej vodorovnou pozici tohoto rohu; hranaté závorky vybírají uloženou položku.
/// - `segments += edge(g, xs[side], ys[side], xs[next], ys[next], level);` — Požádej připravenou akci
///   edge o vykreslení této strany a připočti vrácený počet úseků.
/// - `return segments;` — Po dokončení všech vybraných stran vrať celkový počet vykreslených malých
///   úseků.
///
/// ### Co už obstarává připravená část
///
/// xs a ys obsahují pozice tří rohů. edge už rozděluje stranu na menší klikaté úseky, při vyšší úrovni
/// opakovaně; tuto recursion (rekurzi) měnit nemusíš. render připraví plátno a barvy, save a main zapíší
/// koch-snowflake-cards.png.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Začátek na jedné vynechá první stranu.
/// 2. Tři index (indexy) jsou 0,1,2 a vyloučená hranice je 3.
/// 3. Začni side = 0 a zachovej side < 3.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int edge(Graphics2D g, double x, double y, double nx, double ny, int level) {
    if (level == 0) {
        g.drawLine((int)x, (int)y, (int)nx, (int)ny);
        return 1;
    }
    double dx = (nx - x) / 3;
    double dy = (ny - y) / 3;
    double ax = x + dx;
    double ay = y + dy;
    double bx = ax + dx * 0.5 - dy * Math.sqrt(3) / 2;
    double by = ay + dx * Math.sqrt(3) / 2 + dy * 0.5;
    return edge(g, x, y, ax, ay, level - 1)
        + edge(g, ax, ay, bx, by, level - 1)
        + edge(g, bx, by, x + 2 * dx, y + 2 * dy, level - 1)
        + edge(g, x + 2 * dx, y + 2 * dy, nx, ny, level - 1);
}

BufferedImage render(long seed, double option) {
    int level = (int)option;
    if (!Double.isFinite(option) || level < 0 || level > 6) {
        throw new IllegalArgumentException("Level: 0..6.");
    }
    var image = new BufferedImage(540, 480, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 540, 480);
        g.setColor(Color.getHSBColor(new Random(seed).nextFloat(), 0.8F, 0.7F));
        snowflake(g, level);
    } finally {
        g.dispose();
    }
    return image;
}

void save(Path path, long seed, double option) throws Exception {
    Files.createDirectories(path.toAbsolutePath().getParent());
    if (!ImageIO.write(render(seed, option), "png", path.toFile())) {
        throw new IllegalStateException("PNG writer missing.");
    }
}

void main(String[] args) throws Exception {
    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "koch-snowflake-cards.png");
        long seed = args.length > 1 ? Long.parseLong(args[1]) : 42;
        double option = args.length > 2 ? Double.parseDouble(args[2]) : 4;
        save(path, seed, option);
        IO.println("Saved: " + path.toAbsolutePath());
    } catch (IllegalArgumentException invalid) {
        IO.println("Invalid options: " + invalid.getMessage());
    }
}

/// Připravené vstupní body: main, render, save.
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
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
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

