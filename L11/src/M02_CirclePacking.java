// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.List;
// </editor-fold>

/// # L11.02 · CirclePacking
///
/// Nech kruhy vtěsnat se do mezer mezi dřívějšími kruhy. Rostoucí shluk má používat nové středy místo
/// opakovaných pokusů na stejném místě.
///
/// **Tvoje mise:** Nové středy se odmítají, protože se method (metoda) ptá, zda už byly viděny. Oprav
/// přijetí středu tak, aby uspěl právě první pokus.
///
/// **Dnes trénuješ:** Členství v Set (množině) a vložení jsou různé operace. Boolean result (logický
/// výsledek) vložení může oznámit nový střed a současně jej zaznamenat pro další pokusy.
///
/// Pomocí Run File vytvoř circle-packing.png a po dokončení omezeného skládání otevři cestu vypsanou za
/// Saved.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F23 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
boolean claimCentre(Set<Centre> seen, int x, int y) {
    return seen.add(new Centre(x, y));
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F23">
/// ### Set (množina): členství bez duplikátů
///
/// `Set` odpovídá, zda hodnota patří do kolekce. Každému vložení nepřiděluje indexovanou pozici.
///
/// ```java
/// Set<String> stickers = new HashSet<>();
/// IO.println(stickers.add("moon"));
/// IO.println(stickers.add("moon"));
/// IO.println(stickers.size());
/// ```
///
/// Výstup je `true`, `false`, potom `1`. První add změní membership (členství); druhý najde stejnou
/// hodnotu již uloženou. `contains(value)` se ptá bez změny množiny. `remove(value)` hodnotu odebere
/// a oznámí, zda byla přítomna. Record (datový záznam) má automatické porovnání podle hodnot svých
/// složek, takže dva samostatně vytvořené stejné záznamy představují stejného člena množiny. Na
/// pořadí průchodu přes HashSet nespoléhej.
///
/// Předpověz: po odebrání moon a přidání sun jaká bude velikost a výsledek `contains("moon")`?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `Set<Centre>` — Ukládá odlišné hodnoty Centre; stejné coordinate (souřadnice) představují stejný
///   střed.
/// - `seen.contains` — Jen ověří členství; nově nalezený střed nezaznamená.
/// - `new Centre(x, y)` — Vytvoří value record (hodnotový record), jehož rovnost závisí na x a y.
///
/// ### Co už obstarává připravená část
///
/// Omezený pack loop (cyklus skládání) volá claimCentre před zkoumáním dostupného poloměru. Vzdálenosti
/// pro srážky, růst poloměru až do dotyku, barvy i závěrečný circle-packing.png jsou připravené.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Contains se jen ptá; nový střed si nezapamatuje.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Record se stejným x/y je roven starší hodnotě.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Set.add současně uloží hodnotu a oznámí, zda byla nová.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
record Bubble(double x, double y, double radius) {}

List<Bubble> pack(long seed, int count) {
    if (count < 1 || count > 200) {
        throw new IllegalArgumentException("Bubbles: 1..200.");
    }
    var random = new Random(seed);
    var bubbles = new ArrayList<Bubble>();
    Set<Centre> seen = new HashSet<>();
    for (int attempt = 0; attempt < 10000 && bubbles.size() < count; attempt++) {
        var candidate = new Bubble(2 + random.nextInt(396), 2 + random.nextInt(396), 1);
        if (!claimCentre(seen, (int)candidate.x(), (int)candidate.y())) continue;
        boolean free = true;
        for (var other : bubbles) {
            free &= Math.hypot(candidate.x - other.x, candidate.y - other.y) >= 3;
        }
        if (free) {
            bubbles.add(candidate);
        }
    }
    boolean[] stopped = new boolean[bubbles.size()];
    for (int step = 0; step < 400; step++) {
        for (int i = 0; i < bubbles.size(); i++) {
            if (stopped[i]) {
                continue;
            }
            var b = bubbles.get(i);
            double radius = b.radius + 0.5;
            boolean free = b.x >= radius && b.y >= radius && b.x + radius < 400 && b.y + radius < 400;
            for (int j = 0; j < bubbles.size() && free; j++) {
                var other = bubbles.get(j);
                if (i != j && Math.hypot(b.x - other.x, b.y - other.y) < radius + other.radius) {
                    free = false;
                }
            }
            if (free) {
                bubbles.set(i, new Bubble(b.x, b.y, radius));
            } else {
                stopped[i] = true;
            }
        }
    }
    return bubbles;
}

/// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).
BufferedImage render(long seed, double option) {
    if (!Double.isFinite(option)) {
        throw new IllegalArgumentException("Finite bubble count required.");
    }
    var bubbles = pack(seed, (int)option);
    var image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        for (int i = 0; i < bubbles.size(); i++) {
            var b = bubbles.get(i);
            g.setColor(Color.getHSBColor(i * 0.618F, 0.6F, 0.9F));
            g.fillOval((int)(b.x - b.radius), (int)(b.y - b.radius), (int)(b.radius * 2), (int)(b.radius * 2));
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

record Centre(int x, int y) {}

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// ClaimCentre uchovává stabilní record key (klíče) místo mutable object (měnitelných objektů) souřadnic.
/// Pack provede nejvýše 10 000 návrhů a nechá růst nepřekrývající se bubliny; render/save výsledek ukážou.
void main(String[] args) throws Exception {
    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "circle-packing.png");
        long seed = args.length > 1 ? Long.parseLong(args[1]) : 42;
        double option = args.length > 2 ? Double.parseDouble(args[2]) : 80;
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
/// - `!=` — Ověří, že se dvě hodnoty liší.
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
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `Set` — Uchovává různé hodnoty; stejná hodnota je přítomná nejvýše jednou.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
/// - `Path` — Umístění v souborech, nikoli obsah souboru.
/// - `Files` — JDK operace pro čtení a zápis místních souborů.
// </editor-fold>

