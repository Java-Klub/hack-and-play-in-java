// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.Color;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L01.09 · PixelMonsters
///
/// **Rozsviť příšeře oči.** Program už vytváří barevnou příšerku, ale obě její oči jsou černé.
///
/// **Úkol:** Na řádku s `eyeColour` níže vyber bílou barvu místo černé. Zachovej zbytek řádku
/// včetně `.getRGB();`; nekresli každé oko zvlášť. Dostupná jména barev jsou `Color.BLACK`,
/// `Color.WHITE`, `Color.RED` a `Color.YELLOW`.
///
/// **Výsledek:** V novém okně budou obě oči bílé a tělo zůstane stejné. Jedna změněná hodnota se
/// totiž používá pro obě oči.

int eyeColour = Color.WHITE.getRGB();

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
/// souměrné tělo. main vykreslí seed (počáteční hodnotu) 42 a ukáže obrázek v připraveném okně.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// Obrázek používá stále stejný seed (počáteční hodnotu) 42, aby šly oči před úpravou a po ní přímo
/// porovnat. Color poskytuje pojmenované barvy jako BLACK, WHITE, RED a YELLOW; getRGB() vrací
/// jejich číselný zápis. Upravuješ jedinou sdílenou barvu, nikoli dvě pozice očí.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Průchod hodnotami · zkus předpovědět">
/// Jméno lze číst opakovaně. Představ si `int badge = 7;` použité na dvou dveřích: jediná změna badge
/// změní číslo, které přečtou oboje dveře. Zde obě oční pozice čtou eyeColour; změna jediného oka
/// jinde by společné pravidlo obešla. getRGB dodává číselný zápis barvy, nemusíš si jej pamatovat.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Změnou eyeColour na dvě dostupná jména Color dej stejné příšeře přátelský a výhružný výraz. Ponech
/// seed 42, aby tělo zůstalo srovnatelné. Než odhalíš změněnou barvu, nech kamaráda vybrat výraz.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Obě oční místa už čtou eyeColour.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Color.WHITE.getRGB() je připravený způsob získání hodnoty bílé barvy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// V initialization (inicializaci) nahraď Color.BLACK hodnotou Color.WHITE.
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

// Prepared display of the picture calculated above.
void showPicture() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("Image preview needs a graphical display.");
        return;
    }
    var image = render(42);
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame("PixelMonsters");
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

/// Připravené vstupní body: main, render.
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
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
/// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
///   List<String> lomené závorky popisují element type (typ prvku).
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek.
// </editor-fold>
