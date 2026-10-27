// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L12.09 · CrystalGrowth
///
/// Vypěstuj krystal z bloudících částic, které přilnou k existujícímu shluku. Povolení dotyku po
/// úhlopříčce má změnit charakter růstu oproti dotyku jen hranou.
///
/// **Tvoje mise:** Nový diagonal mode (režim úhlopříček) se chová stejně jako původní čtyřsousední.
/// Rozšiř oddělené pravidlo dotyku a zachovej původní volbu i bezpečné meze.
///
/// **Dnes trénuješ:** Transfer (přenos znalosti) znovu použije známé pravidlo sousedství pro novou volbu.
/// Pojmenovaná operace dotyku má vyjádřit rozdíl čtyř a osmi sousedů a zachovat postup růstu.
///
/// Po Run File stiskni Enter pro růst po úhlopříčce a crystal-new.png, potom spusť n pro porovnání s
/// crystal-old.png.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F36 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
boolean touching(boolean[][] grid, int x, int y, boolean diagonals) {
    for (int dy = -1; dy <= 1; dy++) {
        for (int dx = -1; dx <= 1; dx++) {
            if (dx == 0 && dy == 0) continue;
            if (dx != 0 && dy != 0) continue;
            int nx = x + dx, ny = y + dy;
            if (ny >= 0 && ny < grid.length && nx >= 0 && nx < grid[ny].length && grid[ny][nx]) return true;
        }
    }
    return false;
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F36">
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
/// - `dx == 0 && dy == 0` — Rozpozná středovou cell (buňku), která není vlastním sousedem.
/// - `dx != 0 && dy != 0` — Rozpozná úhlopříčný posun, protože se mění obě osy.
/// - `boolean diagonals` — Volba caller (volající části) zde existuje, ale pravidlo ji nyní nezohledňuje.
/// - `ny < grid.length && nx >= 0 && nx < grid[ny].length` — Ověří meze řádku i sloupce před čtením
///   kandidátní cell (buňky).
///
/// ### Co už obstarává připravená část
///
/// Main se zeptá na druh dotyku. Připravená omezená random walk (náhodná procházka) volá touching před
/// přichycením částice; kreslení i PNG export jsou připravené. Oddělenou odpovědnost vysvětli sám: check
/// (kontroly) dokládají chování, ne čitelnost.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Staré pravidlo odmítá diagonální dvojice dx/dy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Odmítej je jen tehdy, když diagonals je false.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Zachovej vynechání středu a hranice řádků; vysvětli, proč helper (pomocná funkce) vlastní sousedy
/// a grow náhodnou chůzi.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
boolean[][] grow(long seed, int particles) {
    if (particles < 1 || particles > 1200) {
        throw new IllegalArgumentException("Particles: 1..1200.");
    }
    boolean[][] stuck = new boolean[161][161];
    stuck[80][80] = true;
    var random = new Random(seed);
    double radius = 2;
    int[] dx = {1, -1, 0, 0};
    int[] dy = {0, 0, 1, -1};
    for (int particle = 0; particle < particles && radius < 65; particle++) {
        double angle = random.nextDouble() * Math.PI * 2;
        double launch = radius + 6;
        int x = 80 + (int)(Math.cos(angle) * launch);
        int y = 80 + (int)(Math.sin(angle) * launch);
        for (int walk = 0; walk < 12000; walk++) {
            if (x < 1 || x > 159 || y < 1 || y > 159 || Math.hypot(x - 80, y - 80) > launch + 15) {
                break;
            }
            if (!stuck[y][x] && touching(stuck, x, y, diagonalGrowth)) {
                stuck[y][x] = true;
                radius = Math.max(radius, Math.hypot(x - 80, y - 80));
                break;
            }
            int direction = random.nextInt(4);
            x += dx[direction];
            y += dy[direction];
        }
    }
    return stuck;
}

/// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).
BufferedImage render(long seed, double option) {
    if (!Double.isFinite(option)) {
        throw new IllegalArgumentException("Finite particle count required.");
    }
    var stuck = grow(seed, (int)option);
    var image = new BufferedImage(483, 483, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        for (int y = 0; y < 161; y++) {
            for (int x = 0; x < 161; x++) {
                if (stuck[y][x]) {
                    g.setColor(Color.getHSBColor((float)Math.hypot(x - 80, y - 80) / 90, 0.6F, 1));
                    g.fillRect(x * 3, y * 3, 3, 3);
                }
            }
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

boolean diagonalGrowth = true;

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Touching vlastní pravidlo čtyř/osmi sousedů; grow omezenou náhodnou chůzi a přichycení.
/// Render obarví vzniklý shluk; save/main zachovávají místní PNG postup.
void main(String[] args) throws Exception {
    if (args.length == 0) {
        String entered = IO.readln("Allow diagonal growth? y/n (blank y, q quits): ");
        if (entered == null || entered.strip().equalsIgnoreCase("q")) return;
        diagonalGrowth = !entered.strip().equalsIgnoreCase("n");
        args = new String[]{"crystal-" + (diagonalGrowth ? "new" : "old") + ".png"};
    }

    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "crystal-growth.png");
        long seed = args.length > 1 ? Long.parseLong(args[1]) : 42;
        double option = args.length > 2 ? Double.parseDouble(args[2]) : 600;
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
/// - `!=` — Ověří, že se dvě hodnoty liší.
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

