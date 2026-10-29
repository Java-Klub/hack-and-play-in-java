// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L09.11 · MazePostcards
///
/// Vytvoř pohlednici s bludištěm, jehož rozložení mění seed (počáteční hodnota). Šířka určuje, kolik
/// klikatých chodeb se na stránku vejde.
///
/// **Tvoje mise:** Překlep vytvoří maličké bludiště se dvěma sloupci místo slíbených 20. Oprav zpracování
/// chybného textu a zachovej číselný limit 2–50.
///
/// **Dnes trénuješ:** Obnova po chybném input (vstupu) má přesný contract (smlouvu): nerozpoznaný text
/// dostane záměrnou výchozí hodnotu, převedené číslo musí splnit meze. Konkrétní catch tyto případy
/// odděluje.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L09/M11_MazePostcards.java`
/// - Kontrola této mise: `java L09/Check.java M11_MazePostcards.java`
///
/// Po spuštění programu napiš počet sloupců 2–50 nebo zkus oops; přečti Chosen value a prohlédni
/// Java obrázek. Pro další vstup zavři okno a znovu spusť command pro hraní. Po opravě: ulož
/// soubor, spusť uvedenou kontrolu a zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F31 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
int readColumns(String text) {
    try {
        int columns = Integer.parseInt(text.strip());
        if (columns < 2 || columns > 50) throw new IllegalArgumentException("Columns: 2..50.");
        return columns;
    } catch (NumberFormatException invalid) {
        return 2;
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
/// - `int columns` — Ukládá celý počet sloupců bludiště; desetinný text se tímto převodem nestane integer
///   (celým číslem).
/// - `columns < 2 || columns > 50` — Kterékoli porovnání může odmítnout převedenou, ale nepoužitelnou
///   velikost bludiště.
/// - `catch (NumberFormatException invalid)` — Vybere náhradní cestu pro chybně zapsané číslo.
/// - `return 2;` — Tato hodnota nyní určí šířku bludiště po překlepu.
///
/// ### Co už obstarává připravená část
///
/// main předá zadaný počet sloupců do readColumns. Připravený generator (generátor) se stack
/// (zásobníkem) vytvoří omezené průchody a render přímo zobrazí jejich stěny v Javě.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Číslo se může převést a přesto být odmítnuto
///
/// V readColumns se `" 8 "` převede na 8 a běžně vrátí. `"eight"` selže při převodu a přejde do catch
/// pro NumberFormatException. `"1"` se převede na 1, potom výslovná kontrola mezí vyvolá
/// IllegalArgumentException, kterou tento konkrétní catch neřeší. Trasuj `"2"` a `"50"` a ověř, že
/// oba krajní body patří do rozsahu. Potom předpověz, zda `"51"` a `"fifty"` projdou stejnou cestou.
/// Generátor bludiště má dostat buď přijatelné číslo, nebo dokumentovanou náhradu chybného textu,
/// nikoli libovolnou malou šířku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Výchozí hodnota řeší chybu převodu, nikoli každou neplatnou hodnotu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Guard (podmínku) zahrnutého rozsahu nech mimo případ NumberFormatException.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Main předá výsledek přes render do skutečného generate.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
boolean[][] generate(long seed, int columns, int rows) {
    if (columns < 2 || columns > 50 || rows < 2 || rows > 40) {
        throw new IllegalArgumentException("Maze dimensions: 2..50 by 2..40.");
    }
    boolean[][] open = new boolean[rows * 2 + 1][columns * 2 + 1];
    var random = new Random(seed);
    var stack = new ArrayDeque<Point>();
    stack.push(new Point(1, 1));
    open[1][1] = true;
    int[][] offsets = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}};
    while (!stack.isEmpty()) {
        Point cell = stack.peek();
        var choices = new ArrayList<Point>();
        for (var offset : offsets) {
            int x = cell.x + offset[0];
            int y = cell.y + offset[1];
            if (x > 0 && x < columns * 2 && y > 0 && y < rows * 2 && !open[y][x]) {
                choices.add(new Point(x, y));
            }
        }
        if (choices.isEmpty()) {
            stack.pop();
        } else {
            Point next = choices.get(random.nextInt(choices.size()));
            open[(cell.y + next.y) / 2][(cell.x + next.x) / 2] = true;
            open[next.y][next.x] = true;
            stack.push(next);
        }
    }
    open[1][0] = true;
    open[rows * 2 - 1][columns * 2] = true;
    return open;
}

/// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).
BufferedImage render(long seed, double option) {
    if (!Double.isFinite(option)) {
        throw new IllegalArgumentException("Finite dimensions required.");
    }
    var open = generate(seed, (int)option, 15);
    var image = new BufferedImage(open[0].length * 12, open.length * 12, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(Color.WHITE);
        for (int y = 0; y < open.length; y++) {
            for (int x = 0; x < open[y].length; x++) {
                if (open[y][x]) {
                    g.fillRect(x * 12, y * 12, 12, 12);
                }
            }
        }
    } finally {
        g.dispose();
    }
    return image;
}


/// Program spusť uvedeným command (příkazem) pro hraní. Kontrola hry ověří pravidlo bez otevření
/// okna. Připravená část zajišťuje pouze uvedenou hlavní hru nebo pokus.
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
    String input = IO.readln("Columns 2..50 (blank/default 20, q quits): ");
    if (input == null || input.strip().equalsIgnoreCase("q")) return;
    try {
        var option = readColumns(input);
        IO.println("Chosen value: " + option);
        showImages("Maze Postcards", new String[]{"Value " + option}, new java.awt.image.BufferedImage[]{render(42, option)});
    } catch (IllegalArgumentException failure) {
        IO.println("Invalid input: " + failure.getMessage());
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
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
