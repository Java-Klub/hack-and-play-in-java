// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
// </editor-fold>

/// # L09.04 · VoronoiStainedGlass
///
/// Rozhoď barevná semínka a nech každé místo obrázku připojit k nejbližšímu. Různá semínka promění
/// stejnou plochu v nové vitráže.
///
/// **Tvoje mise:** Vytvoření prvního semínka selže, protože zvolená kolekce nemůže růst. Připrav List
/// vhodný pro postupné sestavení požadovaných semínek.
///
/// **Dnes trénuješ:** List (seznam) popisuje přístup v pořadí, ale ne každý List dovoluje mutation
/// (změnu). Tato method (metoda) sestavuje výsledek postupně, proto její implementace musí přijímat add.
///
/// Program sestaví obrázek. Pokud seedList odmítne přidat první barevný bod, čti výstup v
/// terminálu. Po opravě program otevře barevný obrázek.
List<Seed> seedList(Random random, int count) {
    if (count < 0 || count > 100) throw new IllegalArgumentException("Seeds: 0..100.");
    List<Seed> seeds = new ArrayList<>();
    for (int i = 0; i < count; i++) {
        seeds.add(new Seed(random.nextInt(400), random.nextInt(300), Color.HSBtoRGB(random.nextFloat(), 0.65F, 1)));
    }
    return seeds;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F22">
/// ### List (seznam): pořadí, které můžeš měnit
///
/// `List<String>` uchovává texty v pořadí. `new ArrayList<>()` vytvoří mutable list (měnitelný
/// seznam): obsah lze měnit bez nahrazení samotného seznamu.
///
/// ```java
/// List<String> snacks = new ArrayList<>();
/// snacks.add("apple");
/// snacks.add("nuts");
/// IO.println(snacks.get(0));
/// IO.println(snacks.size());
/// ```
///
/// Výstup je `apple`, potom `2`. `add(value)` připojí jeden prvek na konec; `get(index)` přečte prvek
/// bez odebrání. Indexes (indexy) začínají nulou. `isEmpty()` zjišťuje, zda seznam nemá prvky.
/// `removeLast()` odebere a vrátí poslední prvek; volej jej jen u neprázdného seznamu. Seznam přímo z
/// `List.of(...)` měnit nelze, ale `new ArrayList<>(List.of(...))` měnit lze.
///
/// Předpověz: po `snacks.removeLast()` jaká bude velikost a první prvek?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `List<Seed>` — Element type (typ prvku) určuje, že každá uložená hodnota je Seed.
/// - `List.of()` — Prázdný immutable List (neměnný seznam): prázdnost je velikost, neměnnost zákaz změny.
/// - `seeds.add` — Přidá jednu vytvořenou hodnotu do sestavované kolekce.
/// - `random.nextInt(400)` — Vybere platnou x coordinate (souřadnici) od nuly do 399.
///
/// ### Co už obstarává připravená část
///
/// render požádá seedList o středy a přiřadí každý bod nejbližšímu středu; showImages zobrazí
/// obrázek z paměti přímo v Javě.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// List.of vytvoří výsledek, který nelze měnit.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Zvol mutable implementation (měnitelnou implementaci) při zachování role List<Seed>.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Přidávej uvnitř counted loop (cyklu s počítadlem); nula dá prázdný mutable List (měnitelný
/// seznam).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
record Seed(int x, int y, int colour) {}

int nearest(List<Seed> seeds, int x, int y) {
    int winner = 0;
    long distance = Long.MAX_VALUE;
    for (int i = 0; i < seeds.size(); i++) {
        long dx = x - seeds.get(i).x;
        long dy = y - seeds.get(i).y;
        long candidate = dx * dx + dy * dy;
        if (candidate < distance) {
            distance = candidate;
            winner = i;
        }
    }
    return winner;
}

/// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).
BufferedImage render(long seed, double option) {
    int count = (int)option;
    if (!Double.isFinite(option) || count < 2 || count > 100) {
        throw new IllegalArgumentException("Seeds: 2..100.");
    }
    var random = new Random(seed);
    var seeds = seedList(random, count);
    var image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
    int[][] cells = new int[300][400];
    for (int y = 0; y < 300; y++) {
        for (int x = 0; x < 400; x++) {
            cells[y][x] = nearest(seeds, x, y);
        }
    }
    for (int y = 0; y < 300; y++) {
        for (int x = 0; x < 400; x++) {
            boolean border = x > 0 && cells[y][x] != cells[y][x - 1] || y > 0 && cells[y][x] != cells[y - 1][x];
            image.setRGB(x, y, border ? 0 : seeds.get(cells[y][x]).colour);
        }
    }
    return image;
}


void showImages(String title, String[] captions, java.awt.image.BufferedImage[] images) {
    if (java.awt.GraphicsEnvironment.isHeadless()) return;
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame(title);
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        var pictures = new javax.swing.JPanel(new java.awt.GridLayout(1, images.length, 8, 8));
        for (int i = 0; i < images.length; i++) {
            var picture = new javax.swing.JPanel(new java.awt.BorderLayout());
            picture.add(new javax.swing.JLabel(captions[i], javax.swing.SwingConstants.CENTER), java.awt.BorderLayout.NORTH);
            double scale = Math.min(1, Math.min(900.0 / images.length / images[i].getWidth(), 550.0 / images[i].getHeight()));
            var icon = new javax.swing.ImageIcon(images[i].getScaledInstance(
                    Math.max(1, (int)(images[i].getWidth() * scale)),
                    Math.max(1, (int)(images[i].getHeight() * scale)), java.awt.Image.SCALE_FAST));
            picture.add(new javax.swing.JLabel(icon), java.awt.BorderLayout.CENTER);
            pictures.add(picture);
        }
        frame.add(pictures);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}


void main() {
    try {
        showImages("Voronoi Stained Glass", new String[]{"Result"}, new java.awt.image.BufferedImage[]{render(42, 20)});
    } catch (Exception failure) {
        IO.println("Cannot build the picture. Check seedList; Save the source, run its check command, then play again.");
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
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek.
// </editor-fold>
