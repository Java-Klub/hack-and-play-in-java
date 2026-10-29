// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L01.08 · SunflowerSpiral
///
/// **Vypěstuj spirálu ze dvou paprsků.** Program sází barevná semínka jedno po druhém. Teď mezi
/// nimi otočí směr vždy o půl otáčky, takže leží ve dvou protilehlých řadách.
///
/// **Úkol:** Najdi nastavení úhlu mezi sousedními semínky a změň hodnotu použitou při běžném
/// spuštění na `137.5` stupně. Použij desetinnou tečku. Připravený výpočet souřadnic zachovej.
///
/// **Výsledek:** Nově spuštěný obrázek rozprostře semínka do spirálovitého květu. Když nastavení
/// najdeš, sleduj také, jak se jeho hodnota dostane ke kreslení jednoho semínka.

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F03">
/// Variable (proměnná) pojmenuje uloženou hodnotu. Type (typ) popisuje přípustné hodnoty: `int`
/// uchovává celá čísla a `double` může uchovat desetinnou část. Initialization (počáteční přiřazení)
/// dodá první hodnotu; pozdější assignment (přiřazení) ji nahradí.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// int charge = 4;
/// charge = charge + 3;
/// IO.println(charge);
/// ```
///
/// Předpověz output (výstup). Nejprve `charge` uchovává čtyřku. Na druhém řádku Java přečte starou
/// hodnotu pro výpočet pravého expression (výrazu) a potom pod stejným jménem uloží sedmičku. `=` je
/// assignment, nikoli matematické tvrzení, že obě strany mají navždy zůstat stejné. Další statement
/// (příkazy) čtoucí toto jméno nyní dostanou sedmičku. Jméno se neaktualizuje samo: hodnotu musí
/// změnit provedené assignment. Variable označenou `final` nelze znovu přiřadit.
///
/// Nyní předpověz konečné charge, pokud počáteční přiřazení použije šest místo čtyř.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Průzkum a pokus">
/// Sleduj hodnoty místo učení vzorce nazpaměť. Začni u main a sleduj call (volání), které žádá
/// obrázek. Jméno může předat zvolené číslo několika method (metodám). Neutrální příklad:
/// `show(size)` dodá současnou hodnotu size, nikoli slovo "size". Fractional literal (desetinný
/// literál) používá tečku. Před úpravou předpověz, zda se úhel volí jednou při spuštění, nebo zvlášť
/// zapisuje pro každý bod.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · oblast hledání">
/// Najdi žádost o obrázek, nikoli výpočty sinu a kosinu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · konkrétní cesta">
/// showPicture předává pojmenovaný úhel do render. Sleduj toto jméno k jeho initialization
/// (počátečnímu přiřazení).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná úprava · prozrazuje postup">
/// Field (pole objektu) defaultAngle začíná na 180. Toto jediné přiřazení změň na 137.5.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Zkus tři blízké hodnoty a kamarádovi dej číselný recept bez obrázku. Nech jej předpovědět paprsky
/// nebo spirálu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Program · prozkoumej podle potřeby">
double defaultAngle = 180;


double[] position(int index, double degrees) {
    double angle = Math.toRadians(degrees) * index;
    double radius = 7 * Math.sqrt(index);
    return new double[]{250 + radius * Math.cos(angle), 250 + radius * Math.sin(angle)};
}

BufferedImage render(long seed, double degrees) {
    if (!Double.isFinite(degrees) || degrees < 1 || degrees > 360) {
        throw new IllegalArgumentException("Angle: 1..360 degrees.");
    }
    var image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        float hue = new Random(seed).nextFloat();
        for (int i = 0; i < 1000; i++) {
            var point = position(i, degrees);
            g.setColor(Color.getHSBColor(hue + i / 1000.0F, 0.65F, 1));
            g.fillOval((int)point[0] - 3, (int)point[1] - 3, 6, 6);
        }
    } finally {
        g.dispose();
    }
    return image;
}

// Prepared display of the picture calculated above.
void showPicture() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("Image preview needs a graphical display.");
        return;
    }
    var image = render(42, defaultAngle);
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame("SunflowerSpiral");
        var picture = new javax.swing.JLabel(new javax.swing.ImageIcon(image));
        picture.setOpaque(true);
        picture.setBackground(new java.awt.Color(35, 39, 48));
        frame.add(picture);
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}

void main() {
    showPicture();
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
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
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
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek.
// </editor-fold>
