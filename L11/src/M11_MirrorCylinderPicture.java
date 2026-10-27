// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L11.11 · MirrorCylinderPicture
///
/// Zkresli obrázek do zvláštního vějíře zahnutých pásů, jako přípravu pro válcové zrcadlo. I bez odrazu
/// je uložené zkreslení obrázkem, který můžeš prozkoumat.
///
/// **Tvoje mise:** Cílový soubor vznikne, ale zůstane prázdný, protože PNG bajty míří do nepoužité
/// paměti. Znovu propoj encoding (kódování) obrázku s otevřeným cílem.
///
/// **Dnes trénuješ:** Otevření output stream (výstupního proudu) a zápis do něj jsou různé akce. Správa
/// resource (prostředku) stream bezpečně zavře, ale bajty dorazí jen tehdy, když encoding (kódování)
/// opravdu dostane tento stream.
///
/// Po Run File stiskni Enter pro uložení exports/mirror.png a potom otevři toto PNG pro prozkoumání
/// zahnutého zkreslení.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F32 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
void save(Path path, long seed, double option) throws Exception {
    Files.createDirectories(path.toAbsolutePath().getParent());
    try (var output = Files.newOutputStream(path)) {
        if (!ImageIO.write(render(seed, option), "png", new ByteArrayOutputStream())) {
            throw new IOException("PNG writer missing.");
        }
    }
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
/// - `Files.newOutputStream(path)` — Otevře požadovaný cíl a vrátí stream (proud), který spravuje jeho
///   bajty.
/// - `new ByteArrayOutputStream()` — Vytvoří samostatný cíl v paměti, který nesouvisí s otevřeným
///   souborem.
/// - `ImageIO.write` — Pošle zakódované PNG bajty tomu output object (výstupnímu objektu), který dostane.
///
/// ### Co už obstarává připravená část
///
/// Render dodává omezené polární zkreslení a save připraví adresáře. Main se zeptá na cíl; po opravě má
/// stejné ImageIO encoding (kódování) naplnit tento soubor, aby jej běžný prohlížeč obrázků otevřel.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Otevřený file (soubor) nestačí, když encoder (zapisovač obrazu) píše jinam.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Proměnná z try-with-resources je skutečný file output (souborový výstup).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Do ImageIO.write předej output místo nového paměťového stream (proudu).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
double[] ground(double angle, double height) {
    double x = 60 * Math.cos(angle);
    double y = 60 * Math.sin(angle);
    double dx = x;
    double dy = y + 600;
    double dz = height - 300;
    double dot = dx * Math.cos(angle) + dy * Math.sin(angle);
    dx -= 2 * dot * Math.cos(angle);
    dy -= 2 * dot * Math.sin(angle);
    double distance = -height / dz;
    return new double[]{x + distance * dx, y + distance * dy};
}

/// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).
BufferedImage render(long seed, double option) {
    if (!Double.isFinite(option) || option < 30 || option > 140) {
        throw new IllegalArgumentException("Maximum mirror height: 30..140.");
    }
    var source = new BufferedImage(500, 160, BufferedImage.TYPE_INT_RGB);
    var ink = source.createGraphics();
    ink.setColor(Color.WHITE);
    ink.fillRect(0, 0, 500, 160);
    ink.setColor(Color.getHSBColor(new Random(seed).nextFloat(), 0.9F, 0.7F));
    ink.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 100));
    ink.drawString("JAVA", 65, 120);
    ink.dispose();
    var image = new BufferedImage(900, 800, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 900, 800);
        for (int v = 0; v < 160; v++) {
            for (int u = 0; u < 500; u++) {
                double angle = -Math.PI / 2 + (u / 499.0 - 0.5) * 1.4;
                var point = ground(angle, 5 + option * (1 - v / 159.0));
                g.setColor(new Color(source.getRGB(u, v)));
                g.fillRect(450 + (int)point[0], 700 + (int)point[1], 4, 4);
            }
        }
        g.setColor(Color.BLACK);
        g.drawOval(390, 640, 120, 120);
        g.drawString("Cylinder radius 60. View from (0,-600,300), relative to circle centre.", 15, 785);
    } finally {
        g.dispose();
    }
    return image;
}

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Ground odrazí připravený paprsek do roviny papíru; render vzorkuje původní kresbu.
/// Save spravuje skutečný PNG stream (proud); main vypíše zvolené místní umístění.
void main(String[] args) throws Exception {
    if (args.length == 0) {
        String entered = IO.readln("Output path (blank exports/mirror.png, q quits): ");
        if (entered == null || entered.strip().equalsIgnoreCase("q")) return;
        String destination = entered.isBlank() ? "exports/mirror.png" : entered.strip();
        args = new String[]{destination};
    }

    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "mirror-cylinder-picture.png");
        long seed = args.length > 1 ? Long.parseLong(args[1]) : 42;
        double option = args.length > 2 ? Double.parseDouble(args[2]) : 125;
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

