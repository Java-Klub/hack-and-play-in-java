// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
// </editor-fold>

/// # L12.03 · PhasingRhythms
///
/// Polož přes sebe dva krátké rytmy s trochu odlišnými rozestupy. Jejich měnící se souběh tvoří nové
/// důrazy, přestože se jednotlivé vzory nemění.
///
/// **Tvoje mise:** Do směsi dorazí jen první hlas. Obnov výsledek obou hlasů ve stejném požadovaném čase
/// a nepřepisuj zvuk jednotlivých hlasů.
///
/// **Dnes trénuješ:** Composition (skládání) spojuje malé role přes společné interface (rozhraní). Obě
/// Voice odpovídají na převod času na sample (vzorek); mix spojuje odpovědi a nemusí znát vznik
/// jednotlivých rytmů.
///
/// Pomocí Run File ulož phasing.wav, potom jej otevři v přehrávači a poslouchej měnící se důrazy mezi
/// oběma rytmy.
/// Po opravě: Save → Test File → zahraj si znovu.
double mix(double time) {
    return 0.17 * (first.sample(time) + second.sample(time));
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `mix(double time)` — Caller (volající část) žádá jeden společný sample (vzorek) právě v tomto
///   okamžiku.
/// - `first.sample(time)` — Zavolá připravený první hlas s časem od caller (volající části).
/// - `0.17 *` — Použije zesílení výstupu; zvaž, na které přispívající hlasy má působit.
///
/// ### Co už obstarává připravená část
///
/// Signal žádá mix o každý WAV sample (vzorek). Připravené Voice field (položky objektu) volají tap se
/// dvěma různými rozestupy; tap tvoří konečné pulzy a write uloží omezený výsledek bez automatického
/// přehrávání.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Voice popisuje roli převodu času na sample (vzorek).
/// 2. Nevolej jen první field (položku objektu) a nepoužívej dva různé časy.
/// 3. Zesílení nech vně součtu, aby se použilo stejně pro oba vyměnitelné hlasy.
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
boolean[] rhythm = {true, false, true, false, true, true, false, true, false, true, false, true};

double tap(double time, double beat) {
    int index = (int)(time / beat);
    double within = time - index * beat;
    if (!rhythm[index % rhythm.length] || within > 0.035) {
        return 0;
    }
    return Math.sin(2 * Math.PI * 1200 * within) * Math.exp(-within * 120);
}

/// Připravená syntéza: omezené sample loop (cykly vzorků) tvoří zvuk; automatické přehrávání zde není.
double[] signal(double seconds) {
    if (!Double.isFinite(seconds) || seconds < 1 || seconds > 40) {
        throw new IllegalArgumentException("Duration: 1..40 seconds.");
    }
    double[] signal = new double[(int)(RATE * seconds)];
    for (int i = 0; i < signal.length; i++) {
        double time = i / (double)RATE;
        signal[i] = mix(time);
    }
    return signal;
}

interface Voice { double sample(double time); }
Voice first = time -> tap(time, 0.12);
Voice second = time -> tap(time, 0.13);

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Mix spojuje dvě skutečné Voice field (položky objektu); signal jej volá pro každý WAV sample (vzorek).
/// Tap definuje připravené vzory 0.12/0.13 sekundy; write/main uloží konečný fázující zvuk.
void main(String[] args) throws Exception {
    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "phasing.wav");
        double seconds = args.length > 1 ? Double.parseDouble(args[1]) : 18.72;
/// Připravený WAV encoding (kódování): omezené PCM sample (vzorky) a try-with-resources spravují stream (proud).
        write(path, signal(seconds));
        IO.println("Saved: " + path.toAbsolutePath());
        IO.println("Identical twelve-beat patterns use 0.12 and 0.13 second beats; both align again at 18.72 seconds.");
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
/// - `boolean` — Hodnota true nebo false: ano nebo ne.
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
///   se ale dál měnit může.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
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
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `interface` — Popisuje role (roli), kterou mohou plnit různé implementation (implementace). Volající
///   používá roli bez znalosti jejích vnitřností.
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

