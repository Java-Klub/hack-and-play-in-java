// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
// </editor-fold>

/// # L12.10 · PictureMusic
///
/// Poslechni si malý obrázek, jako by jeho řádky byly tóny a sloupce čas. Transpose (transpozice) má
/// posunout melodii výš či níž a zachovat rytmus i obrys.
///
/// **Tvoje mise:** Transpose input (vstup transpozice) se přijme, ale neovlivní výšku tónu. Rozšiř
/// oddělené pravidlo frekvence a zachovej původní zvuk pro shift nula.
///
/// **Dnes trénuješ:** Malá oddělená operace může přidat schopnost beze změny práce caller (volající
/// části). PitchFrequency spojí známé pravidlo řádku a tónu s půltónovým posunem; nula zůstane případem
/// zachování původního chování.
///
/// Po Run File zadej 0 a potom spusť znovu s 12; v přehrávači otevři picture-music-0.wav a
/// picture-music-12.wav pro porovnání výšky.
/// Po opravě: Save → Test File → zahraj si znovu.
double pitchFrequency(int row, int shift) {
    if (row < 0 || row >= 12 || shift < -12 || shift > 12) throw new IllegalArgumentException("Row 0..11; transpose -12..12.");
    return 220 * Math.pow(2, (11 - row) / 12.0);
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int row, int shift` — Operace dostane pozici v obrázku i požadovaný posun.
/// - `shift < -12 || shift > 12` — Udrží transpose (transpozici) v rozmezí jedné oktávy oběma směry.
/// - `(11 - row) / 12.0` — Převede vzdálenost řádku na část oktávy; desetinný dělitel zachová dílčí
///   kroky.
/// - `220 * Math.pow(2,` — Násobí základní frekvenci poměrem oktáv, ne přičtením pevného počtu hertzů.
///
/// ### Co už obstarává připravená část
///
/// Připravený převod obrázku na zvuk mění jas v sílu tónu a volá pitchFrequency pro výšky řádků. Main se
/// zeptá na transpose (transpozici) a write uloží WAV; vysvětli, proč nové pravidlo patří do malé
/// operace, ne do každého caller (volající části).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Změna výšky tónu patří do exponentu, nikoli hlasitosti.
/// 2. Shift přičti k 11-row před dělením 12.0.
/// 3. Zachovej guard (podmínky) row/shift a vysvětli, proč sampling (vzorkování) obrazu zůstává mimo
///   helper.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int RATE = 22050;

/// Připravený WAV encoding (kódování): omezené PCM sample (vzorky) a try-with-resources spravují stream (proud).
void write(Path path, double[] signal) throws Exception {
    byte[] pcm = new byte[signal.length * 2];
    for (int i = 0; i < signal.length; i++) {
        int value = (int)(Math.max(-0.8, Math.min(0.8, signal[i])) * 32767);
        pcm[i * 2] = (byte)value;
        pcm[i * 2 + 1] = (byte)(value >> 8);
    }
    Files.createDirectories(path.toAbsolutePath().getParent());
    var format = new AudioFormat(RATE, 16, 1, true, false);
    try (var stream = new AudioInputStream(new ByteArrayInputStream(pcm), format, signal.length)) {
        AudioSystem.write(stream, AudioFileFormat.Type.WAVE, path.toFile());
    }
}

java.awt.image.BufferedImage demo() {
    var image = new java.awt.image.BufferedImage(32, 12, java.awt.image.BufferedImage.TYPE_INT_RGB);
    for (int x = 0; x < 32; x++) {
        image.setRGB(x, 5 + (int)(4 * Math.sin(x * 0.6)), 0xFFFFFF);
    }
    return image;
}

/// Připravená syntéza: omezené sample loop (cykly vzorků) tvoří zvuk; automatické přehrávání zde není.
double[] signal(java.awt.image.BufferedImage image) {
    int step = RATE / 10;
    double[] signal = new double[32 * step];
    for (int col = 0; col < 32; col++) {
        for (int row = 0; row < 12; row++) {
            int x = Math.min(image.getWidth() - 1, (int)((col + 0.5) * image.getWidth() / 32));
            int y = Math.min(image.getHeight() - 1, (int)((row + 0.5) * image.getHeight() / 12));
            int rgb = image.getRGB(x, y);
            double brightness = (((rgb >> 16) & 255) + ((rgb >> 8) & 255) + (rgb & 255)) / 765.0;
            double frequency = pitchFrequency(row, transpose);
            for (int i = 0; i < step; i++) {
                double fade = Math.min(1, Math.min(i, step - 1 - i) / 100.0);
                signal[col * step + i] += brightness / 40 * fade * Math.sin(2 * Math.PI * frequency * i / RATE);
            }
        }
    }
    return signal;
}

int transpose = 0;

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// PitchFrequency vlastní matematiku řádku/posunu; signal jas a sampling (vzorkování) časových sloupců.
/// Demo dodá vlastní obraz; main může použít místní obraz a volitelný transpose argument (argument posunu); write uloží WAV.
void main(String[] args) throws Exception {
    if (args.length == 0) {
        String entered = IO.readln("Transpose -12..12 (blank 12, q quits): ");
        if (entered == null || entered.strip().equalsIgnoreCase("q")) return;
        String shift = entered.isBlank() ? "12" : entered.strip();
        args = new String[]{"picture-music-" + shift + ".wav", "-", shift};
    }

    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    Path path = Path.of(args.length > 0 ? args[0] : "picture-music.wav");
    var image = args.length > 1 && !args[1].equals("-") ? javax.imageio.ImageIO.read(Path.of(args[1]).toFile()) : demo();
    if (image == null) {
        IO.println("Use a readable local image.");
        return;
    }
    if (args.length > 2) transpose = Integer.parseInt(args[2]);
/// Připravený WAV encoding (kódování): omezené PCM sample (vzorky) a try-with-resources spravují stream (proud).
    write(path, signal(image));
    IO.println("Saved sonification: " + path.toAbsolutePath());
    IO.println("32 columns become time; 12 rows become pitches; brightness sets volume. This is not recorded space sound.");
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
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
/// - `Path` — Umístění v souborech, nikoli obsah souboru.
/// - `Files` — JDK operace pro čtení a zápis místních souborů.
// </editor-fold>

