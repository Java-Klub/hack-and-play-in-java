// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L12.01 · ReactionDiffusionSkins
///
/// Simulace kůže už běží. Staň se návrhářem pokusu: měň jediný místní recept a ověř, zda vysvětlení
/// odpovídá vzoru, který skutečně vznikne.
///
/// **Tvoje mise:** Sestav férový pokus se třemi kůžemi: použij stejný seed i počet kroků pro reakce
/// a×b², poloviční reakci a nulovou reakci. Měň jen vybranou chemistry role (chemickou roli); znovu
/// použij stejnou difuzi i kreslení. Vrať tři obrazy v tomto pořadí a nakonec obnov původní roli
/// volajícího, i když pokus selže.
///
/// **Zkoumání:** Prozkoumej composition výměnou jediné role za stejných podmínek.
///
/// Okno porovnává Standard, Half reaction a No reaction ze stejného počátku. Ve starter (výchozí
/// verzi) se opakuje jeden recept. Po zapojení tvého experimentálního nástroje porovnej všechny tři
/// bez změny seed.

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// Po třech řízených srovnáních vymysli čtvrtý recept. Změň jen místní reakci a pozorovaný vzor
/// pojmenuj podle vlastního tvora. Zachovej seed i délku pokusu, aby jej někdo jiný zopakoval.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F34">
/// ### Interface (rozhraní): požádej vybranou roli o práci
///
/// Interface pojmenovává operaci, kterou dodají jeho implementations (implementace). Volající používá
/// tuto roli bez výběru jejího výpočtu.
///
/// ```java
/// interface Points {
///     int award(int n);
/// }
/// record DoublePoints() implements Points {
///     public int award(int n) {
///         return n * 2;
///     }
/// }
/// ```
///
/// Při `Points rule = new DoublePoints();` vrátí `rule.award(3)` hodnotu 6. `implements Points`
/// slibuje danou operaci; implementace musí dodat její public method (veřejnou metodu). Proměnná rule
/// označuje vybraný objekt, proto volej jeho operaci se současným vstupem místo opakování pevného
/// výpočtu ve volajícím. Jiná implementace může dodat jiná pravidla přes stejné rozhraní.
///
/// Předpověz: co tato implementace vrátí pro `rule.award(5)` a kde by se měnil kód pro trojnásobek
/// vstupu?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Deník průzkumníka · vyložený příklad">
/// ### Změň jedinou příčinu; vypůjčené nastavení vrať
///
/// Interface field (uložená role) vybírá výpočet. Výměna implementace se liší od přepsání volajícího
/// i od změny počátečních dat. Controlled comparison (řízené porovnání) potřebuje stejné vstupy a
/// jasné pořadí výsledků.
///
/// ```java
/// var before = rule;
/// try {
///     rule = alternative;
///     runExperiment();
/// } finally {
///     rule = before;
/// }
/// ```
///
/// Tento nezávislý vzor obnoví rule při dokončení i výjimce z runExperiment. Předpověz, kterou roli
/// použije pozdější volání, když pokus selže před return. Finally je slib úklidu, ne catch skrývající
/// chybu. Hotový obraz zachyť před dalším během. V závěru pojmenuj pozorovaný tvar místo slibu, že
/// každé nastavení vytvoří určitou zvířecí kůži.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Sleduj zobrazený obraz zpět k vybranému výpočtu a najdi místo obnovující vstupní mřížky při novém
/// kreslení.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// compareSkins nyní opakuje render bez změny chemistry. Interface Chemistry dodává jen reakci;
/// render obnoví seed a posune difuzi.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Uchovej původní Chemistry, postupně zapoj tři receptové lambda a vykresli stejné vstupy. Původní
/// roli obnov ve finally. Step ani jeho mřížky neměň.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program k prozkoumání">
Chemistry makeChemistry() {
    return (a, b) -> {
        double activated = a * b;
        return activated * b;
    };
}

Chemistry chemistry = makeChemistry();

double reactionAt(int x, int y) {
    return chemistry.reaction(a[y][x], b[y][x]);
}

final int SIZE = 64;
double[][] a;
double[][] b;

/// Připravený restart: vytvoří počáteční model (model pravidel); neotevírá okno.
void reset(long seed) {
    var random = new Random(seed);
    a = new double[SIZE][SIZE];
    b = new double[SIZE][SIZE];
    for (int y = 0; y < SIZE; y++) {
        for (int x = 0; x < SIZE; x++) {
            a[y][x] = 1;
            if (x > 23 && x < 40 && y > 23 && y < 40) {
                a[y][x] = 0.5;
                b[y][x] = 0.8 + random.nextDouble() * 0.2;
            }
        }
    }
}

double laplace(double[][] field, int x, int y) {
    double sum = -field[y][x];
    for (int dy = -1; dy <= 1; dy++) {
        for (int dx = -1; dx <= 1; dx++) {
            if (dx == 0 && dy == 0) {
                continue;
            }
            double weight = dx == 0 || dy == 0 ? 0.2 : 0.05;
            sum += field[(y + dy + SIZE) % SIZE][(x + dx + SIZE) % SIZE] * weight;
        }
    }
    return sum;
}

/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
void step() {
    double[][] nextA = new double[SIZE][SIZE];
    double[][] nextB = new double[SIZE][SIZE];
    for (int y = 0; y < SIZE; y++) {
        for (int x = 0; x < SIZE; x++) {
            double reaction = reactionAt(x, y);
            nextA[y][x] = Math.clamp(a[y][x] + laplace(a, x, y) - reaction + 0.055 * (1 - a[y][x]), 0, 1);
            nextB[y][x] = Math.clamp(b[y][x] + 0.5 * laplace(b, x, y) + reaction - 0.117 * b[y][x], 0, 1);
        }
    }
    a = nextA;
    b = nextB;
}

/// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).
BufferedImage render(long seed, double option) {
    int count = (int)option;
    if (!Double.isFinite(option) || count < 1 || count > 3000) {
        throw new IllegalArgumentException("Steps: 1..3000.");
    }
/// Připravený restart: vytvoří počáteční model (model pravidel); neotevírá okno.
    reset(seed);
    for (int i = 0; i < count; i++) {
/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
        step();
    }
    var image = new BufferedImage(SIZE * 6, SIZE * 6, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) {
            float value = (float)Math.clamp((a[y / 6][x / 6] - b[y / 6][x / 6]), 0, 1);
            image.setRGB(x, y, Color.HSBtoRGB(0.6F + value * 0.3F, 0.65F, value));
        }
    }
    return image;
}


interface Chemistry { double reaction(double a, double b); }


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


BufferedImage[] compareSkins(long seed, int steps) {
    return new BufferedImage[]{render(seed, steps), render(seed, steps), render(seed, steps)};
}

void main() {
    try {
        showImages("Skin Experiments", new String[]{"Standard", "Half reaction", "No reaction"}, compareSkins(42, 1200));
    } catch (Exception failure) {
        IO.println("Cannot run the experiment: " + failure.getMessage());
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
/// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
///   se ale dál měnit může.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
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
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `interface` — Popisuje role (roli), kterou mohou plnit různé implementation (implementace). Volající
///   používá roli bez znalosti jejích vnitřností.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek.
// </editor-fold>
