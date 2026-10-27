// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L01.09 · PixelMonsters
///
/// Vytvoř malou souměrnou příšerku z obrazových bodů a porovnej různé počáteční hodnoty, abys našel
/// několik oblíbených. Na průhledném pozadí vynikne její hranatý tvar.
///
/// **Tvoje mise:** Příšerka přišla o světlé oči. Oprav jedinou sdílenou eyeColour tak, aby oba oční bloky
/// zbělely bez samostatné úpravy každého oka.
///
/// **Dnes trénuješ:** Jedna named value (pojmenovaná hodnota), několik čtenářů: obě oči dostávají stejnou
/// uloženou barvu. Jediná vědomá změna proto má ovlivnit dvě místa obrázku.
///
/// Použij Run File, přijmi monster.png a zadej seed (počáteční hodnotu) 42, potom obnov Files a otevři
/// monster.png; pro porovnání opravených očí použij stejný seed.
/// Po opravě: Save → Test File → zahraj si znovu.
int eyeColour = Color.WHITE.getRGB();

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int eyeColour` — Pojmenovaná celočíselná hodnota barvy. Připravené volání palety vytvoří její zápis
///   pro obrazový bod; číslo nemusíš rozebírat.
/// - `=` — Ulož barvu vrácenou expression (výrazem) na pravé straně.
/// - `Color.BLACK` — Pojmenovaná černá barva z připravené palety Javy.
/// - `.` — Tečka vybírá pojmenovanou část nebo akci: nejdřív barvu z Color, potom akci této barvy.
/// - `getRGB()` — Požádej vybranou barvu o číselnou podobu potřebnou pro obrázek; prázdné závorky
///   znamenají, že nepředáváš další input (vstup).
/// - `;` — Dokonči uložení jediné sdílené barvy před sestavením pozdějšího obrázku.
///
/// ### Co už obstarává připravená část
///
/// render používá stejnou eyeColour pro levé i pravé oko, zatímco připravená náhodná část vytváří
/// souměrné tělo. write uloží průhledný PNG; main se zeptá na jméno souboru a seed (počáteční hodnotu).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Obě oční místa už čtou eyeColour.
/// 2. Color.WHITE.getRGB() je připravený způsob získání hodnoty bílé barvy.
/// 3. V initialization (inicializaci) nahraď Color.BLACK hodnotou Color.WHITE.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
BufferedImage render(long seed) {
    var random = new Random(seed);
    int[][] cells = new int[16][16];
    int body = Color.HSBtoRGB(random.nextFloat(), 0.7f, 0.9f);
    for (int row = 2; row < 14; row++) {
        int width = 3 + random.nextInt(5);
        for (int col = 8 - width; col < 8; col++) {
            if (random.nextInt(5) != 0) {
                cells[row][col] = body;
                cells[row][15 - col] = body;
            }
        }
    }
    cells[5][5] = eyeColour;
    cells[5][10] = eyeColour;
    cells[6][5] = 0xFF111111;
    cells[6][10] = 0xFF111111;
    var image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
    var drawing = image.createGraphics();
    try {
        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 16; col++) {
                drawing.setColor(new Color(cells[row][col], true));
                drawing.fillRect(col * 16, row * 16, 16, 16);
            }
        }
    } finally {
        drawing.dispose();
    }
    return image;
}

void write(long seed, Path path) throws Exception {
    Files.createDirectories(path.toAbsolutePath().getParent());
    ImageIO.write(render(seed), "png", path.toFile());
}

void main(String[] args) throws Exception {
    String target = args.length > 0 ? args[0] : IO.readln("Output PNG path (Enter for monster.png, q to quit): ");
    if (target == null || target.equalsIgnoreCase("q")) return;
    if (target.isBlank()) target = "monster.png";
    String seedText = args.length > 1 ? args[1] : IO.readln("Integer seed (q to quit): ");
    if (seedText == null || seedText.equalsIgnoreCase("q")) return;
    try {
        long seed = Long.parseLong(seedText.strip());
        Path path = Path.of(target);
        write(seed, path);
        IO.println("Saved: " + path.toAbsolutePath());
        IO.println("Seed: " + seed + ". The image has a transparent background.");
    } catch (NumberFormatException invalidSeed) {
        IO.println("Use an integer seed.");
    }
}

/// Připravené vstupní body: main, render, write.
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
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `!=` — Ověří, že se dvě hodnoty liší.
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
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
/// - `Path` — Umístění v souborech, nikoli obsah souboru.
/// - `Files` — JDK operace pro čtení a zápis místních souborů.
// </editor-fold>

