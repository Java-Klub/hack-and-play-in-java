// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.List;
// </editor-fold>

/// # L09.08 · DungeonBlueprints
///
/// Vytvoř plán podzemí a objev, které místnosti se vejdou před propojením chodbami. Legenda má říct,
/// kolik malých a velkých místností se skutečně podařilo umístit.
///
/// **Tvoje mise:** Každá přijatá místnost přepíše předchozí počet své kategorie na jedna. Oprav součty
/// legendy a nezapočítej odmítnuté pokusy o umístění.
///
/// **Dnes trénuješ:** Map (mapa) seskupuje jednotlivé místnosti pod společné category key (klíče
/// kategorií). Klasifikace vybere key; další počítání mění jeho součet bez vymazání dřívějších místností.
///
/// Pomocí Run File ulož dungeon-blueprints.png, potom jej otevři a porovnej místnosti s legendou SMALL a
/// LARGE.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F24 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
void countRoom(Room room) {
    String kind = room.width() * room.height() <= 42 ? "SMALL" : "LARGE";
    roomCounts.put(kind, 1);
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F24">
/// ### Map (mapa): hodnota podle klíče
///
/// `Map<String, Integer>` přiřazuje textovým keys (klíčům) celočíselné values (hodnoty). Jeden klíč
/// má nejvýše jednu uloženou hodnotu.
///
/// ```java
/// Map<String, Integer> badges = new HashMap<>();
/// badges.put("moon", 4);
/// IO.println(badges.getOrDefault("moon", 0));
/// IO.println(badges.getOrDefault("sun", 0));
/// ```
///
/// Výstup je `4`, potom `0`. `getOrDefault(key, fallback)` přečte existující hodnotu; při chybějícím
/// klíči vrátí fallback (náhradní hodnotu). Toto čtení nic nevkládá. `put(key, value)` hodnotu uloží
/// a případnou předchozí hodnotu téhož klíče přepíše; čísla automaticky nesčítá. `containsKey(key)`
/// použij, když potřebuješ výslovně rozlišit chybějící klíč. V těchto příkladech neukládáme `null`.
///
/// Předpověz: po `badges.put("moon", 9)` co vrátí stejná dvě čtení?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `room.width() * room.height()` — Čte record component (složky record) a násobením získá plochu
///   místnosti.
/// - `<= 42 ? "SMALL" : "LARGE"` — Vybere jeden název kategorie; plocha 42 patří do SMALL.
/// - `roomCounts.put(kind, 1)` — Uloží jedna pod zvolený key (klíč), i když jej sdílí několik místností.
///
/// ### Co už obstarává připravená část
///
/// Umísťování ověří překryv a volá countRoom jen pro přijaté místnosti. Chodby, kreslení i uložení jsou
/// připravené; popisek čte výsledné součty kategorií.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Hranice plochy zahrnuje přesně 42.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Různé Room object (objekty) mohou sdílet stejný druh jako key (klíč).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Použij starý počet nebo nulu a přičti jednu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
record Room(int x, int y, int width, int height) {
    boolean overlaps(Room other) {
        return x - 1 < other.x + other.width && x + width + 1 > other.x && y - 1 < other.y + other.height && y + height + 1 > other.y;
    }
}

boolean[][] generate(long seed, int requested) {
    if (requested < 2 || requested > 18) {
        throw new IllegalArgumentException("Rooms: 2..18.");
    }
    roomCounts.clear();
    var random = new Random(seed);
    var rooms = new ArrayList<Room>();
    for (int attempt = 0; attempt < 500 && rooms.size() < requested; attempt++) {
        var room = new Room(1 + random.nextInt(37), 1 + random.nextInt(22), 4 + random.nextInt(7), 4 + random.nextInt(7));
        boolean fits = true;
        for (var other : rooms) {
            fits &= !room.overlaps(other);
        }
        if (fits) {
            rooms.add(room);
            countRoom(room);
        }
    }
    boolean[][] floor = new boolean[34][50];
    Room previous = null;
    for (var room : rooms) {
        for (int y = room.y; y < room.y + room.height; y++) {
            for (int x = room.x; x < room.x + room.width; x++) {
                floor[y][x] = true;
            }
        }
        if (previous != null) {
            int x = previous.x + previous.width / 2;
            int y = previous.y + previous.height / 2;
            int targetX = room.x + room.width / 2;
            int targetY = room.y + room.height / 2;
            while (x != targetX) {
                floor[y][x] = true;
                x += Integer.signum(targetX - x);
            }
            while (y != targetY) {
                floor[y][x] = true;
                y += Integer.signum(targetY - y);
            }
            floor[y][x] = true;
        }
        previous = room;
    }
    return floor;
}

/// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).
BufferedImage render(long seed, double option) {
    if (!Double.isFinite(option)) {
        throw new IllegalArgumentException("Finite room count required.");
    }
    var floor = generate(seed, (int)option);
    var image = new BufferedImage(600, 408, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        for (int y = 0; y < 34; y++) {
            for (int x = 0; x < 50; x++) {
                g.setColor(floor[y][x] ? new Color(235, 220, 180) : new Color(30, 35, 40));
                g.fillRect(x * 12, y * 12, 11, 11);
            }
        }
        g.setColor(Color.WHITE);
        g.drawString("SMALL " + roomCounts.getOrDefault("SMALL", 0) + " / LARGE " + roomCounts.getOrDefault("LARGE", 0), 8, 14);
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

Map<String, Integer> roomCounts = new HashMap<>();

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Generate uchovává List (seznam) místností a spojuje sousední středy chodbou ve tvaru L.
/// CountRoom řídí přehled ve skutečném obrazu; render/save ponechávají připravené kreslení a PNG hranice.
void main(String[] args) throws Exception {
    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "dungeon-blueprints.png");
        long seed = args.length > 1 ? Long.parseLong(args[1]) : 42;
        double option = args.length > 2 ? Double.parseDouble(args[2]) : 8;
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
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `Map` — Přiřazuje key (klíčům) hodnoty. Pro chybějící key je třeba rozhodnout výchozí chování.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
/// - `Path` — Umístění v souborech, nikoli obsah souboru.
/// - `Files` — JDK operace pro čtení a zápis místních souborů.
// </editor-fold>

