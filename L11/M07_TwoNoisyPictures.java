// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
// </editor-fold>

/// # L11.07 · TwoNoisyPictures
///
/// Rozděl tajemství do dvou zašuměných černobílých obrázků. Každý díl má sám vypadat bezradně, zatímco
/// jejich překrytí odhalí skrytý tvar.
///
/// **Tvoje mise:** Každý malý blok znovu použije stejné promíchání, protože se generátor restartuje
/// uvnitř pixel loop (cyklu obrazových bodů). Obnov různorodou, opakovatelnou sequence (posloupnost) pro
/// celý obrázek.
///
/// **Dnes trénuješ:** Random se seed (počáteční hodnotou) je průběžný zdroj voleb. Nové vytvoření se
/// stejným seed zopakuje první volbu; sdílená instance dá dalším blokům další volby.
///
/// Po spuštění programu napiš seed (počáteční číslo) nebo stiskni Enter pro 42; prohlédni Java
/// obrázek, potom jej zavři a zopakuj stejný seed nebo porovnej 43.
BufferedImage[] shares(BufferedImage secret, long seed) {
    var images = new BufferedImage[3];
    for (int i = 0; i < 3; i++) {
        images[i] = new BufferedImage(secret.getWidth() * 2, secret.getHeight() * 2, BufferedImage.TYPE_INT_RGB);
    }
    var random = new Random(seed);
    for (int y = 0; y < secret.getHeight(); y++) {
        for (int x = 0; x < secret.getWidth(); x++) {
            var order = new ArrayList<>(List.of(0, 1, 2, 3));
            Collections.shuffle(order, new Random(seed));
            boolean ink = (secret.getRGB(x, y) & 255) > 127;
            for (int i = 0; i < 4; i++) {
                int dx = order.get(i) % 2;
                int dy = order.get(i) / 2;
                boolean first = i < 2;
                boolean second = ink ? !first : first;
                images[0].setRGB(x * 2 + dx, y * 2 + dy, first ? 0 : 0xFFFFFF);
                images[1].setRGB(x * 2 + dx, y * 2 + dy, second ? 0 : 0xFFFFFF);
                images[2].setRGB(x * 2 + dx, y * 2 + dy, first || second ? 0 : 0xFFFFFF);
            }
        }
    }
    return images;
}

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
/// - `var random = new Random(seed)` — Vytvoří jeden generátor pro celou operaci, který může zachovat
///   pozici v sequence (posloupnosti).
/// - `Collections.shuffle(order, new Random(seed))` — Nyní pro každý blok restartuje náhodu místo
///   pokračování existujícího generátoru.
/// - `x * 2 + dx` — Umístí dílčí bod do bloku dva krát dva pro tento zdrojový pixel (obrazový bod).
/// - `first || second` — Překrytí je černé, pokud kterákoli část označí tento dílčí bod černě.
///
/// ### Co už obstarává připravená část
///
/// shares vytvoří dva šumové obrázky z message mask (masky zprávy). render je spojí; main zobrazí
/// překrytí i oba podíly společně pro porovnání zprávy s jejími částmi.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// New Random(seed) začíná sequence (posloupnost) pokaždé od začátku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Method (metoda) už jeden Random tvoří před pixel loop (cykly bodů obrazu).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Existující object (objekt) předej do každého Collections.shuffle.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String message = "JAVA";

BufferedImage mask() {
    if (message.isBlank() || message.length() > 7) {
        throw new IllegalArgumentException("Message: 1..7 characters.");
    }
    var image = new BufferedImage(180, 60, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 38));
        g.drawString(message, 5, 45);
    } finally {
        g.dispose();
    }
    return image;
}

/// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).
BufferedImage render(long seed, double option) {
    return shares(mask(), seed)[2];
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
        var pair = shares(mask(), seed);
        showImages("Two Noisy Pictures", new String[]{"Overlay", "Share 1", "Share 2"},
                new java.awt.image.BufferedImage[]{render(seed, 1), pair[0], pair[1]});
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
/// - `boolean` — Hodnota true nebo false: ano nebo ne.
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
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
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
