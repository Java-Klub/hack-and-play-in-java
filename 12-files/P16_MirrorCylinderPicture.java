// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # 12.16 · MirrorCylinderPicture
///
/// Proměň slovo ze svého textového souboru ve vějíř zahnutých pásů jako přípravu pro válcové zrcadlo.
/// Zkreslení prozkoumej přímo v obrázku v Javě a při dalším spuštění zkus jiné slovo.
///
/// **Tvoje mise:** Vytvoř readCaption: otevři vybraný UTF-8 text pomocí newBufferedReader, přečti
/// první řádek, odstraň okolní whitespace (bílé znaky) a přijmi 1..8 znaků. Prázdný, bílý nebo příliš
/// dlouhý první řádek ohlas jako IOException. Kreslené slovo musí pocházet ze souboru.
///
/// **Dnes trénuješ:** Implementuješ skutečnou file boundary (souborovou hranici): otevři vybraný
/// prostředek, předej skutečný obsah a spolehlivě jej zavři. Výsledek má přetrvat jedno volání
/// metody.
///
/// První spuštění vytvoří data/mirror-word.txt s JAVA. Změň první řádek na MOON a spusť znovu:
/// obrázek v Javě se musí změnit. Použij nejvýše osm znaků, aby se slovo vešlo do obrázku.
String readCaption(Path path) throws IOException {
    try (var reader = Files.newBufferedReader(path)) {
        String line = reader.readLine();
        if (line == null) throw new IOException("The first line is empty.");
        String text = line.strip();
        if (text.isEmpty() || text.length() > 8) {
            throw new IOException("Use 1..8 characters on the first line.");
        }
        return text;
    }
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Vytvoř sbírku slovních obrazů úpravou prvního řádku data/mirror-word.txt mezi spuštěními. Zkus
/// MOON, krátké jméno a interpunkční znak. Pro každý návrh si nech textový soubor; porovnej deformaci
/// úzkého a širokého slova kolem stejného kruhu.
// </editor-fold>

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

// <editor-fold defaultstate="expanded" desc="Jak číst tento kód">
/// ### Textový soubor se stává vstupem obrázku
///
/// Files.newBufferedReader(path) otevře UTF-8 character reader (čtečku znaků). reader.readLine()
/// vrátí další řádek bez konce řádku, nebo null, když žádný řádek není. Prázdný řádek je String "",
/// což není null. Null zkontroluj před voláním metod String. Neutrální soubor s "  NOVA  " na prvním
/// řádku dá po strip hodnotu "NOVA"; druhý řádek je mimo tento contract (dohodu). Předpověz výsledek
/// prázdného prvního řádku a NOVA na druhém.
///
/// Try-with-resources zavře reader při chybě, validaci i return. IOException zachová chybějící soubor
/// a špatný obsah pro zprávu v main. main vytvoří data/mirror-word.txt s JAVA jen při neexistenci,
/// načte jej do caption a přímo jej kreslí. Mezi spuštěními uprav tento malý text, a vytvoř jiný
/// obraz.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Před strip prvního řádku vyřeš možnost null.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Obal newBufferedReader(path) pomocí try-with-resources; název souboru není obsah.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Přečti řádek, odmítni null, použij strip, odmítni délku mimo 1..8 a vrať přijatý text.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String caption = "JAVA";

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
    ink.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.min(100, 440 / Math.max(1, caption.length()))));
    ink.drawString(caption, 30, 120);
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
    Path input = Path.of("data", "mirror-word.txt");
    try {
        if (!Files.exists(input)) {
            Files.createDirectories(input.toAbsolutePath().getParent());
            Files.writeString(input, "JAVA\n");
        }
        caption = readCaption(input);
        IO.println("Read " + caption + " from " + input.toAbsolutePath());
        IO.println("Edit the first line of that text file and run again to author another picture.");
        showImages("Mirror Cylinder Picture", new String[]{caption}, new BufferedImage[]{render(42, 125)});
    } catch (Exception failure) {
        IO.println("Cannot read the word artwork: " + failure.getMessage());
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
/// - `null` — Žádný object (objekt) tu není.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek.
/// - `Path` — Umístění v souborech, nikoli obsah souboru.
/// - `Files` — JDK operace pro čtení a zápis místních souborů.
// </editor-fold>
