// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L11.06 · ColourFilterSecrets
///
/// Ukryj jasný nápis do barevného šumu a potom jej odhal pohledem na červenou složku. Změna seed
/// (počáteční hodnoty) má změnit maskování a zachovat čitelnou zprávu.
///
/// **Tvoje mise:** Šum zůstává stejný při každém požadovaném seed (počáteční hodnotě). Oprav zdroj náhody
/// a zachovej složku, která nese ukrytou masku.
///
/// **Dnes trénuješ:** Jeden Random se seed (počáteční hodnotou) má dodávat šum pro celý obrázek.
/// Opakovatelnost pomáhá s porovnáním, zatímco jiný seed má změnit rušivé barevné složky.
///
/// Po spuštění programu napiš seed (počáteční číslo) nebo stiskni Enter pro 42; prohlédni Java
/// obrázek, potom jej zavři a zopakuj stejný seed nebo porovnej 43.
BufferedImage hide(BufferedImage mask, long seed) {
    var random = new Random(seed);
    var image = new BufferedImage(mask.getWidth(), mask.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) {
            int red = (mask.getRGB(x, y) & 255) > 127 ? 240 : 15;
            image.setRGB(x, y, (red << 16) | (random.nextInt(256) << 8) | random.nextInt(256));
        }
    }
    return image;
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Schovej vlastní malý znak změnou kresby mask (masky). Potom přesuň zprávu do green channel
/// (zeleného kanálu) a odpovídajícím způsobem změň odhalení. Kamarádovi nejdřív ukaž jen zašuměný
/// obrázek, potom správný filtr.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F27">
/// ### Seeded randomness (náhoda se seedem): opakuj pokus
///
/// Pseudorandom generator (generátor pseudonáhodných hodnot) vytváří posloupnost. Seed (počáteční
/// hodnota) určuje výchozí stav; stejné seedy a stejná volání zopakují stejné výsledky.
///
/// ```java
/// var first = new Random(9);
/// var second = new Random(9);
/// int a = first.nextInt(4);
/// int b = second.nextInt(4);
/// IO.println(a == b);
/// ```
///
/// Výstup je `true`. Každé nextInt posune příslušný generátor. `nextInt(4)` vybere celé číslo od 0 do
/// 3; horní mez nepatří do výběru. V celém generovaném obrázku používej jeden generátor, aby další
/// volby četly další místa posloupnosti. Opakované vytváření se stejným seedem uvnitř loop (cyklu)
/// stále vrací první volbu. Různé seedy mohou náhodou dát stejný jednotlivý výsledek.
///
/// Předpověz: která čísla smí vrátit `nextInt(3)` a může vrátit 3?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `new Random(0)` — Pevně nastaví sequence (posloupnost) šumu na nulu místo požadovaného seed
///   (počáteční hodnoty).
/// - `red << 16` — Vloží červenou hodnotu nesoucí tajemství do její části zabaleného RGB integer (celého
///   čísla).
/// - `random.nextInt(256)` — Vybere intenzitu složky od nuly do 255.
/// - `|` — Spojí bit field (bitové oblasti) červené, zelené a modré do jedné barvy.
///
/// ### Co už obstarává připravená část
///
/// hide použije zvolený seed (počáteční číslo) pro barevný šum nad message mask (maskou zprávy).
/// render vloží skrytý pohled nad odhalenou verzi s červeným filtrem do jednoho Java obrázku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Mask určuje červený channel (kanál); náhodnost mění zelený/modrý.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Neměň channel encoding (kódování kanálů) ani zprávu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Nahraď pevný seed (počáteční hodnotu) předaným parameter (parametrem); Random používej v celém
/// pixel loop (cyklu bodů obrazu).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String message = "SECRET";

BufferedImage mask(String text) {
    if (text.isBlank() || text.length() > 16) {
        throw new IllegalArgumentException("Message: 1..16 characters.");
    }
    var image = new BufferedImage(600, 120, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 48));
        g.drawString(text, 12, 80);
    } finally {
        g.dispose();
    }
    return image;
}

BufferedImage reveal(BufferedImage image) {
    var result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) {
            int red = (image.getRGB(x, y) >> 16) & 255;
            result.setRGB(x, y, red * 0x010101);
        }
    }
    return result;
}

/// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).
BufferedImage render(long seed, double option) {
    var hidden = hide(mask(message), seed);
    var clear = reveal(hidden);
    var image = new BufferedImage(600, 240, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.drawImage(hidden, 0, 0, null);
        g.drawImage(clear, 0, 120, null);
    } finally {
        g.dispose();
    }
    return image;
}


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
    String input = IO.readln("Seed (blank 42, q quits): ");
    if (input == null || input.strip().equalsIgnoreCase("q")) return;
    try {
        long seed = input.isBlank() ? 42 : Long.parseLong(input.strip());
        showImages("Colour Filter Secrets", new String[]{"Seed " + seed}, new java.awt.image.BufferedImage[]{render(seed, 1)});
    } catch (IllegalArgumentException failure) {
        IO.println("Use a whole-number seed: " + failure.getMessage());
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
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
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
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
