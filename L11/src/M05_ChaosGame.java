// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L11.05 · ChaosGame
///
/// Opakovaně poskoč o polovinu vzdálenosti k náhodně vybranému rohu. Z velice malého náhodného pravidla
/// může vyrůst překvapivě uspořádaný fraktál.
///
/// **Tvoje mise:** Poslední platný vrchol se nikdy nevybere. Oprav rozmezí volby včetně jediného
/// dostupného vrcholu a chyby při nulovém počtu.
///
/// **Dnes trénuješ:** Meze Random mají exclusive upper end (nezahrnutý horní konec). Převeď počet
/// platných kandidátů na správné rozmezí volby a nevyřaď omylem posledního kandidáta.
///
/// Po Run File stiskni Enter pro seed (počáteční hodnotu) 42 a otevři chaos-game-42.png pro prozkoumání
/// vzoru opakovaných skoků na polovinu.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F27 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
int chooseVertex(Random random, int count) {
    if (count < 1) throw new IllegalArgumentException("No legal vertices.");
    return random.nextInt(count);
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F27">
/// ### Seeded randomness (náhoda se seedem): opakuj pokus
///
/// Pseudorandom generator (generátor pseudonáhodných hodnot) vytváří posloupnost. Seed (počáteční
/// hodnota) určuje výchozí stav; stejné seedy a stejná volání zopakují stejné výsledky.
///
/// ```java
/// var first = new Random(9);
/// var second = new Random(9);
/// int a = first.nextInt(4);
/// int b = second.nextInt(4);
/// IO.println(a == b);
/// ```
///
/// Výstup je `true`. Každé nextInt posune příslušný generátor. `nextInt(4)` vybere celé číslo od 0 do
/// 3; horní mez nepatří do výběru. V celém generovaném obrázku používej jeden generátor, aby další
/// volby četly další místa posloupnosti. Opakované vytváření se stejným seedem uvnitř loop (cyklu)
/// stále vrací první volbu. Různé seedy mohou náhodou dát stejný jednotlivý výsledek.
///
/// Předpověz: která čísla smí vrátit `nextInt(3)` a může vrátit 3?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `count < 1` — Při nulovém či záporném počtu kandidátů neexistuje platná volba.
/// - `random.nextInt` — Vrátí integer (celé číslo) od nuly přísně pod předanou mezí.
/// - `Math.max(1, count - 1)` — Udržuje kladnou mez, ale nyní zmenšuje platné rozmezí.
///
/// ### Co už obstarává připravená část
///
/// Připravený render vybere vrcholy mnohoúhelníku a opakovaně posouvá bod napůl k výsledku chooseVertex.
/// Omezený loop (cyklus) maluje návštěvy; main přijme seed (počáteční hodnotu) a uloží pohlednici.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Random.nextInt(bound) hranici bound nezahrnuje.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Odečtení jedné od count odstraní poslední platný vrchol.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Prázdný rozsah ověř před omezeným náhodným voláním.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
double[] jump(double x, double y, double targetX, double targetY) {
    return new double[]{(x + targetX) / 2, (y + targetY) / 2};
}

/// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).
BufferedImage render(long seed, double option) {
    int count = (int)option;
    if (!Double.isFinite(option) || count < 100 || count > 300000) {
        throw new IllegalArgumentException("Points: 100..300000.");
    }
    var image = new BufferedImage(500, 420, BufferedImage.TYPE_INT_RGB);
    var random = new Random(seed);
    double[][] vertices = {{250, 15}, {15, 405}, {485, 405}};
    double[] point = {250, 210};
    for (int i = 0; i < count + 20; i++) {
        int corner = chooseVertex(random, 3);
        point = jump(point[0], point[1], vertices[corner][0], vertices[corner][1]);
        if (i >= 20) {
            image.setRGB((int)point[0], (int)point[1], Color.HSBtoRGB(corner / 3.0F, 0.6F, 1));
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
/// ChooseVertex vybírá další připravený roh; jump půlí vektor k němu.
/// Render zahodí krátký začátek a kreslí omezený počet bodů; save/main uloží PNG.
void main(String[] args) throws Exception {
    if (args.length == 0) {
        String entered = IO.readln("Seed (blank 42, q quits): ");
        if (entered == null || entered.strip().equalsIgnoreCase("q")) return;
        String seedText = entered.isBlank() ? "42" : entered.strip();
        args = new String[]{"chaos-game-" + seedText + ".png", seedText};
    }

    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "chaos-game.png");
        long seed = args.length > 1 ? Long.parseLong(args[1]) : 42;
        double option = args.length > 2 ? Double.parseDouble(args[2]) : 50000;
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

