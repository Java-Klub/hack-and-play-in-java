// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
// </editor-fold>

/// # L12.07 · RetroSoundForge
///
/// Vyrob krátký retro zvuk: klesající laser, stoupající skok či kolísavý tón. Drobné vzorce promění
/// typované karty hodnot v úplně jiné WAV efekty.
///
/// **Tvoje mise:** Wobble se změnil v rovný tón, protože jeho depth component (složka hloubky) se
/// nepoužije. Obnov modulaci pro požadovaný čas a zachovej ostatní vzorce i časové meze.
///
/// **Dnes trénuješ:** Switch s record pattern (vzory record) může společně vybrat vzorec a rozbalit jeho
/// parameter (parametry). Různé Wobble musí používat vlastní střed i odchylku a čas musí dál ovlivňovat
/// výsledek.
///
/// Po Run File stiskni Enter pro wobble a otevři retro-wobble.wav v přehrávači; zvuk se ukládá místo
/// automatického přehrání.
/// Po opravě: Save → Test File → zahraj si znovu.
double frequency(Sound sound, double t) {
    if (!Double.isFinite(t) || t < 0 || t > 1) throw new IllegalArgumentException("Time: 0..1.");
    return switch (sound) {
        case Laser(var start, var ratio) -> start * Math.pow(ratio, t);
        case Jump(var base, var rise) -> base + rise * t;
        case Wobble(var base, var depth) -> base;
    };
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `case Wobble(var base, var depth)` — Pojmenuje dvě hodnoty přenášené touto zvukovou alternativou.
/// - `-> base` — Nyní vrací pouze střední frekvenci, nezávisle na depth i čase.
/// - `t < 0 || t > 1` — Odmítne časy mimo normalised interval (normalizované rozmezí) před výběrem
///   vzorce.
/// - `Math.pow(ratio, t)` — Připravený vzorec Laser používá exponenciální poměr místo přímého růstu.
///
/// ### Co už obstarává připravená část
///
/// ChooseSound vytváří hodnoty Laser, Jump či Wobble. Signal se opakovaně ptá frequency na výšku sample
/// (vzorku), skládá fázi a zeslabí konec; write uloží konečný WAV bez automatického přehrávání.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Base je střední frekvence; depth je odchylka.
/// 2. Připravený sinus má v t=1/32 hodnotu jedna.
/// 3. Použij obě rozpoznané component (složky): base + depth krát připravený sinus.
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

/// Připravená syntéza: omezené sample loop (cykly vzorků) tvoří zvuk; automatické přehrávání zde není.
double[] signal(String kind, double seconds) {
    if (!Double.isFinite(seconds) || seconds < 0.1 || seconds > 3) {
        throw new IllegalArgumentException("Duration: 0.1..3 seconds.");
    }
    Sound sound = chooseSound(kind);
    double[] signal = new double[(int)(RATE * seconds)];
    double phase = 0;
    for (int i = 0; i < signal.length; i++) {
        double t = i / (double)signal.length;
        phase += 2 * Math.PI * frequency(sound, t) / RATE;
        double fade = Math.min(1, Math.min(i, signal.length - 1 - i) / 150.0);
        signal[i] = 0.25 * Math.exp(-3 * t) * fade * Math.sin(phase);
    }
    return signal;
}

sealed interface Sound permits Laser, Jump, Wobble {}
record Laser(double start, double ratio) implements Sound {}
record Jump(double base, double rise) implements Sound {}
record Wobble(double base, double depth) implements Sound {}
Sound chooseSound(String kind) {
    return switch (kind) {
        case "laser" -> new Laser(1600, 0.08);
        case "jump" -> new Jump(180, 1100);
        case "wobble" -> new Wobble(400, 180);
        default -> throw new IllegalArgumentException("Sound: laser, jump or wobble.");
    };
}

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// ChooseSound vytvoří typované hodnoty; frequency vybere jejich skutečné vzorce.
/// Signal tvoří omezené PCM sample (vzorky); write/main uloží konečný WAV bez automatického přehrávání.
void main(String[] args) throws Exception {
    if (args.length == 0) {
        String entered = IO.readln("Sound laser/jump/wobble (blank wobble, q quits): ");
        if (entered == null || entered.strip().equalsIgnoreCase("q")) return;
        String sound = entered.isBlank() ? "wobble" : entered.strip();
        args = new String[]{"retro-" + sound + ".wav", sound};
    }

    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "retro-sound.wav");
        String kind = args.length > 1 ? args[1] : "laser";
        double seconds = args.length > 2 ? Double.parseDouble(args[2]) : 0.7;
/// Připravený WAV encoding (kódování): omezené PCM sample (vzorky) a try-with-resources spravují stream (proud).
        write(path, signal(kind, seconds));
        IO.println("Saved: " + path.toAbsolutePath());
    } catch (IllegalArgumentException invalid) {
        IO.println(invalid.getMessage());
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
/// - `switch` — Vybere case (větev) z několika možností. Může také vrátit výsledek.
/// - `case` — Jedna větev switch; šipka ukazuje, co větev provede nebo vrátí.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `implements` — Říká, že record nebo class (třída) plní roli uvedeného interface (rozhraní).
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `interface` — Popisuje role (roli), kterou mohou plnit různé implementation (implementace). Volající
///   používá roli bez znalosti jejích vnitřností.
/// - `sealed` — Omezuje data type (datový typ) na výslovně uvedené alternativy.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
/// - `Path` — Umístění v souborech, nikoli obsah souboru.
/// - `Files` — JDK operace pro čtení a zápis místních souborů.
// </editor-fold>

