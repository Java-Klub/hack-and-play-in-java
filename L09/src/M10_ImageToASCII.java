// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L09.10 · ImageToASCII
///
/// Proměň obrázek ve stěnu znaků. Měň šířku textu a zjisti, kolik detailů přežije, když se obrazové body
/// změní v tečky, dvojtečky a další značky.
///
/// **Tvoje mise:** Překlep v šířce smrskne výsledek na čtyři znaky místo slíbené šířky 80. Oprav náhradní
/// hodnotu a nepřijímej čísla mimo 4–160.
///
/// **Dnes trénuješ:** Úzká parsing boundary (hranice rozboru vstupu) odděluje nerozpoznaný text od
/// platného integer (celého čísla) s chybnou hodnotou. Náhradní šířku má vybrat jen očekávaná conversion
/// exception (výjimka převodu).
///
/// Po Run File zadej šířku obrázku od 4 do 160 nebo zkus oops a potom prozkoumej znakový obrázek v Output
/// i portrait.txt.
/// Po opravě: Save → Test File → zahraj si znovu.
int readWidth(String text) {
    try {
        int value = Integer.parseInt(text.strip());
        if (value < 4 || value > 160) throw new IllegalArgumentException("width: 4..160.");
        return value;
    } catch (NumberFormatException invalid) {
        return 4;
    }
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `Integer.parseInt(text.strip())` — Převede text celého čísla po odstranění okolních mezer.
/// - `value < 4 || value > 160` — Znak || znamená, že k odmítnutí stačí překročení kterékoli meze.
/// - `catch (NumberFormatException invalid)` — Spustí se pro text jako oops; číselná chyba rozmezí se zde
///   nezachytí.
/// - `return 4;` — Tato náhradní hodnota určuje šířku po nerozpoznaném vstupu.
///
/// ### Co už obstarává připravená část
///
/// Main se zeptá na šířku a vypíše vybranou hodnotu. Připravený převod čte jas, vybírá znak z palety a
/// uloží stejný text do portrait.txt.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Před parsing (rozborem) odstraň okolní mezery.
/// 2. Zachyť NumberFormatException, nikoli každou chybu programu.
/// 3. Výchozí hodnota je 80; guard (podmínky) rozsahu zůstanou oddělené.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
BufferedImage demo() {
    var image = new BufferedImage(240, 160, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < 160; y++) {
        for (int x = 0; x < 240; x++) {
            image.setRGB(x, y, Color.HSBtoRGB(x / 240.0F, 0.7F, 0.2F + y / 200.0F));
        }
    }
    return image;
}

String convert(BufferedImage image, int width) {
    if (width < 4 || width > 160) {
        throw new IllegalArgumentException("Width: 4..160.");
    }
    int rows = Math.max(1, width * image.getHeight() / image.getWidth() / 2);
    String ramp = " .:-=+*#%@";
    var result = new StringBuilder();
    for (int row = 0; row < rows; row++) {
        for (int col = 0; col < width; col++) {
            int rgb = image.getRGB(col * image.getWidth() / width, row * image.getHeight() / rows);
            int light = (((rgb >> 16) & 255) * 299 + ((rgb >> 8) & 255) * 587 + (rgb & 255) * 114) / 1000;
            result.append(ramp.charAt(light * (ramp.length() - 1) / 255));
        }
        result.append('\n');
    }
    return result.toString();
}

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Convert čte jas obrazu přes připravený ASCII ramp (stupnici znaků).
/// Main čte tuto volbu; save/export a rendering (vykreslení) jsou připravené.
void main(String[] args) throws Exception {
    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    String enteredOption = "";
    if (args.length <= 2) {
        enteredOption = IO.readln("width 4..160 (blank/default 80, q quits): ");
        if (enteredOption == null || enteredOption.strip().equalsIgnoreCase("q")) return;
    }
    try {
        var input = args.length == 0 || args[0].equals("-") ? demo() : ImageIO.read(Path.of(args[0]).toFile());
        if (input == null) {
            throw new IllegalArgumentException("Input must be a readable image.");
        }
        Path output = Path.of(args.length > 1 ? args[1] : "portrait.txt");
        int option = readWidth(args.length > 2 ? args[2] : enteredOption);
        IO.println("width: " + option);
        Files.createDirectories(output.toAbsolutePath().getParent());
        String text = convert(input, option);
        Files.writeString(output, text);
        IO.print(text);
        IO.println("Saved: " + output.toAbsolutePath());
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
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `'` — Jednoduché uvozovky ohraničují jeden character (znak), například značku na desce.
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `++` — Zvětší číselnou hodnotu o jedna.
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
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
/// - `Path` — Umístění v souborech, nikoli obsah souboru.
/// - `Files` — JDK operace pro čtení a zápis místních souborů.
// </editor-fold>

