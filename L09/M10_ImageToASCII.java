// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import module java.base;
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

class M10_ImageToASCII {
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
    /// Z hlavní složky obsahující README.md:
    /// - Hraní: `java L09/M10_ImageToASCII.java`
    /// - Kontrola této mise: `java L09/Check.java M10_ImageToASCII.java`
    ///
    /// Po spuštění programu napiš šířku od 4 do 160 nebo zkus oops; prohlédni označený ASCII obrázek
    /// přímo v výstup v terminálu a dalším command pro hraní porovnej jinou šířku. Po opravě: ulož
    /// soubor, spusť uvedenou kontrolu a zahraj si znovu.
    ///
    /// Potřebuješ výklad? Připomenutí · F31 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
    /// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
    int readWidth(String text) {
        try {
            int value = Integer.parseInt(text.strip());
            if (value < 4 || value > 160) throw new IllegalArgumentException("width: 4..160.");
            return value;
        } catch (NumberFormatException invalid) {
            return 80;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Připomenutí · F31">
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
    /// - `Integer.parseInt(text.strip())` — Převede text celého čísla po odstranění okolních mezer.
    /// - `value < 4 || value > 160` — Znak || znamená, že k odmítnutí stačí překročení kterékoli meze.
    /// - `catch (NumberFormatException invalid)` — Spustí se pro text jako oops; číselná chyba rozmezí se zde
    ///   nezachytí.
    /// - `return 4;` — Tato náhradní hodnota určuje šířku po nerozpoznaném vstupu.
    ///
    /// ### Co už obstarává připravená část
    ///
    /// main předá zadanou šířku přes readWidth a convert převede pevnou ukázku pomocí character ramp
    /// (stupnice znaků). Výsledek vznikne pouze v výstup v terminálu; další command pro hraní umožní
    /// jinou šířku.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
    /// ### Neúspěšný převod není totéž co chybná číselná šířka
    ///
    /// Trasuj `" 12 "`: strip odstraní krajní mezery, parseInt vytvoří 12, guard (kontrola) projde a
    /// return vrátí 12. `"twelve"` selže uvnitř parseInt, takže konkrétní catch vybere náhradní hodnotu.
    /// `"200"` se převede, ale výslovná kontrola rozsahu vyvolá IllegalArgumentException; catch pro
    /// NumberFormatException tuto jinou chybu nezachytí. Předpověz cesty pro `"4"`, `"160"` a `"3"`.
    /// Zachovej přijetí obou krajních hodnot a porovnej cestu překlepu se šířkou slíbenou v prompt (výzvě
    /// ke vstupu).
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
    /// Před parsing (rozborem) odstraň okolní mezery.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
    /// Zachyť NumberFormatException, nikoli každou chybu programu.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
    /// Výchozí hodnota je 80; guard (podmínky) rozsahu zůstanou oddělené.
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

    /// Program spusť uvedeným command (příkazem) pro hraní. Kontrola hry ověří pravidlo bez otevření
    /// okna. Připravená část zajišťuje pouze uvedenou hlavní hru nebo pokus.
    void main() {
        String input = IO.readln("Width 4..160 (blank/default 80, q quits): ");
        if (input == null || input.strip().equalsIgnoreCase("q")) return;
        try {
            int width = readWidth(input);
            IO.println("ASCII picture, width " + width + ":");
            IO.print(convert(demo(), width));
        } catch (IllegalArgumentException failure) {
            IO.println("Invalid width: " + failure.getMessage());
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
    /// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
    /// - `++` — Zvětší číselnou hodnotu o jedna.
    /// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
    /// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
    /// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
    ///   (balíčku).
    /// - `/` — Dělí čísla. Dělení dvou int hodnot zahodí desetinnou část.
    /// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
    ///   List<String> lomené závorky popisují element type (typ prvku).
    /// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
    /// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
    /// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
    /// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
    /// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
    /// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
    /// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
    /// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
    /// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. print vypíše text bez
    ///   přidání nového řádku. readln čeká na celý řádek vstupu.
    // </editor-fold>

}
