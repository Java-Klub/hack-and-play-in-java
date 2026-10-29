// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # 14.23 · MountainPostcards
///
/// Vytvoř horskou siluetu a porovnej ji se zrcadleným dvojčetem. Zrcadlení má změnit levé a pravé
/// vrcholy, ne vymyslet jinou horu.
///
/// **Tvoje mise:** Proveď refactoring (změnu stavby) fungujícího obrázku. render nyní obrací hřeben
/// přímo uvnitř; tuto odpovědnost vyjmi do reverseRidge a v render nahraď blok voláním své metody.
/// Zachovej původní neobrácenou variantu, neměň vstup a zachovej chování obrázku.
///
/// **Dnes trénuješ:** Vyjmeš operaci z existujícího fungujícího kódu, dáš jí contract (dohodu) a
/// původního volajícího na ni napojíš. Chování zachováš, odpovědnosti změníš.
///
/// Nejdřív prohlédni New rule i Original rule. Po vyjmutí pomocné metody zopakuj stejný seed: oba
/// pohledy mají vypadat stejně jako před změnou. Kontrola helper začíná RED, přestože inline obrázek
/// už funguje.
double[] reverseRidge(double[] heights) {
    double[] reversed = new double[heights.length];
    for (int i = 0; i < heights.length; i++) reversed[i] = heights[heights.length - 1 - i];
    return reversed;
}

BufferedImage render(long seed, double roughness) {
    if (!Double.isFinite(roughness) || roughness < 0.2 || roughness > 0.9) {
        throw new IllegalArgumentException("Roughness: 0.2..0.9.");
    }
    var image = new BufferedImage(640, 400, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    var random = new Random(seed);
    try {
        for (int y = 0; y < 400; y++) {
            g.setColor(new Color(255 - y / 4, 120 - y / 6, 120 + y / 4));
            g.drawLine(0, y, 640, y);
        }
        for (int layer = 0; layer < 5; layer++) {
            var heights = ridge(random, roughness);
            if (mirrored) heights = reverseRidge(heights);
            var polygon = new Polygon();
            polygon.addPoint(0, 400);
            for (int x = 0; x < 257; x++) {
                polygon.addPoint(x * 640 / 256, Math.max(20, Math.min(390, 170 + layer * 38 + (int)heights[x])));
            }
            polygon.addPoint(640, 400);
            g.setColor(new Color(100 - layer * 18, 95 - layer * 15, 135 - layer * 20));
            g.fillPolygon(polygon);
        }
    } finally {
        g.dispose();
    }
    return image;
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Navrhni horské logo spojením původního hřebene a jeho obrácené kopie v jednom obrázku, každého
/// jinou barvou. Rozhodni, zda se potkají uprostřed, nebo překryjí. Generování stále musí dostávat
/// nezměněná původní data hřebene.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F36">
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

// <editor-fold defaultstate="expanded" desc="Jak číst tento kód">
/// ### Refactoring mění odpovědnosti, výsledky zachovává
///
/// Výchozí obrázek už zrcadlí správně. Jeho inline reversal (obracení přímo v místě) je užitečné
/// chování, ne chyba ke smazání. Dej operaci reusable contract (znovu použitelnou dohodu): vrať nové
/// obrácené pole a původní nech beze změny. render potom o operaci požádá jménem místo opakování
/// implementace. Po tomto refactoring se obrázek nemá náhle změnit.
///
/// Neutrální příklad: [5, 9, 1, 7] se změní na [7, 1, 9, 5]. Index i výsledku čte index length - 1 -
/// i originálu. Předpověz výsledek pro [8] a prázdné pole. Prázdný cyklus nic nečte. Clone vytvoří
/// nezávislou kopii, ale pořadí zachová.
///
/// Kontroly ověřují výsledek a nezávislost helper (pomocné metody) i obě varianty skutečného
/// renderer. Nečtou zdroj pro vynucení konkrétního volání. Při self-review (vlastní kontrole) ukaž
/// jediné místo implementující obrácení i jeho volání z render: dvě správné kopie mohou splnit
/// chování, ale minou cíl refactoring.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Při tvorbě pomocné metody si nech fungující inline reversal jako referenci.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Vrácené pole musí být nové. Přesuň obracecí cyklus do reverseRidge a vrať jeho výsledek.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Celý inline blok při mirrored nahraď voláním reverseRidge. Porovnej oba obrázky před změnou a po
/// ní.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
double[] ridge(Random random, double roughness) {
    double[] heights = new double[257];
    heights[0] = random.nextDouble() * 40;
    heights[256] = random.nextDouble() * 40;
    double amplitude = 90;
    for (int gap = 256; gap > 1; gap /= 2) {
        for (int left = 0; left < 256; left += gap) {
            heights[left + gap / 2] = (heights[left] + heights[left + gap]) / 2 + (random.nextDouble() * 2 - 1) * amplitude;
        }
        amplitude *= roughness;
    }
    return heights;
}

/// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).



boolean mirrored = true;

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
    mirrored = true;
    var changed = render(42, 0.65);
    mirrored = false;
    var original = render(42, 0.65);
    mirrored = true;
    showImages("Mountain Postcards", new String[]{"New rule", "Original rule"}, new java.awt.image.BufferedImage[]{changed, original});
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
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
// </editor-fold>
