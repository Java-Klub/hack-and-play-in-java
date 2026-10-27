// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
// </editor-fold>

/// # L11.08 · EndlessToneStaircase
///
/// Vytvoř zvukové schodiště, které díky vrstvám oktáv zdánlivě stále stoupá. Porovnej konečný WAV s
/// obyčejně stoupajícím tónem a poslouchej, kde začne působit iluze.
///
/// **Tvoje mise:** WAV se zapíše do pracovního adresáře, i když požádáš o vnořený cíl. Zachovej úplný
/// zvolený Path (cestu) až do konečného zápisu.
///
/// **Dnes trénuješ:** Path (cesta) nese adresáře i název souboru. Ponechání jen posledního názvu změní
/// cíl; příprava nadřazených adresářů neopraví pozdější zápis jinam.
///
/// Po Run File stiskni Enter pro uložení exports/shepard.wav a otevři jej v přehrávači spolu se
/// srovnávacím .plain.wav.
/// Po opravě: Save → Test File → zahraj si znovu.
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

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `path.toAbsolutePath().getParent()` — Najde nadřazený adresář úplného cíle, aby mohl vzniknout.
/// - `path.getFileName()` — Vybere jen poslední název a zahodí požadovaný adresář.
/// - `try (var stream =` — Po skončení bloku zavře vlastní audio stream (zvukový proud), i když zápis
///   selže.
/// - `AudioSystem.write` — Zakóduje připravené sample (vzorky) do WAV v předaném cíli.
///
/// ### Co už obstarává připravená část
///
/// Připravená syntéza míchá tóny po oktávách s měnícími se vahami, samostatný plain signal dává srovnání.
/// Write převádí sample (vzorky) na PCM a JDK zapíše WAV hlavičku; main vypíše, kde oba soubory otevřít.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. GetFileName záměrně zahazuje nadřazenou cestu.
/// 2. Původní Path už ukazuje na zvolený cíl.
/// 3. Použij jej v AudioSystem.write; zachovej createDirectories i try-with-resources.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int RATE = 22050;

/// Připravená syntéza: omezené sample loop (cykly vzorků) tvoří zvuk; automatické přehrávání zde není.
double[] signal(int layers, boolean plain) {
    if (layers < 2 || layers > 8) {
        throw new IllegalArgumentException("Octave layers: 2..8.");
    }
    int noteLength = RATE / 4;
    double[] signal = new double[noteLength * 12 * 3];
    for (int i = 0; i < signal.length; i++) {
        int step = i / noteLength % 12;
        int within = i % noteLength;
        double fade = Math.min(1, Math.min(within, noteLength - 1 - within) / 200.0);
        double sum = 0;
        double weightSum = 0;
        for (int octave = 0; octave < (plain ? 1 : layers); octave++) {
            double pitch = octave + step / 12.0;
            double weight = plain ? 1 : Math.exp(-Math.pow((pitch - layers / 2.0) / 1.3, 2));
            double frequency = (plain ? 220 : 27.5) * Math.pow(2, pitch);
            sum += weight * Math.sin(2 * Math.PI * frequency * within / RATE);
            weightSum += weight;
        }
        signal[i] = 0.18 * fade * sum / weightSum;
    }
    return signal;
}

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Signal tvoří tři konečné melodické loop (cykly); nepokouší se o automatické přehrání.
/// Write kóduje zvolené sample (vzorky); main uloží obě WAV verze a vypíše umístění.
void main(String[] args) throws Exception {
    if (args.length == 0) {
        String entered = IO.readln("Output path (blank exports/shepard.wav, q quits): ");
        if (entered == null || entered.strip().equalsIgnoreCase("q")) return;
        String destination = entered.isBlank() ? "exports/shepard.wav" : entered.strip();
        args = new String[]{destination};
    }

    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "shepard.wav");
        int layers = args.length > 1 ? Integer.parseInt(args[1]) : 6;
        write(path, signal(layers, false));
        write(Path.of(path + ".plain.wav"), signal(layers, true));
        IO.println("Saved layered and plain comparison: " + path.toAbsolutePath());
        IO.println("Three finite twelve-step loops. Use your player's Stop control.");
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
/// - `'` — Jednoduché uvozovky ohraničují jeden character (znak), například značku na desce.
/// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
///   se ale dál měnit může.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
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

