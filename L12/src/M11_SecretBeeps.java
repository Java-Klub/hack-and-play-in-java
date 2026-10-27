// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
// </editor-fold>

/// # L12.11 · SecretBeeps
///
/// Proměň krátkou zprávu v tečky, čárky a pípání. Zkus delší mezeru mezi slovy a poslechni si, jak
/// samotné časování usnadní či ztíží rozlišení stejných značek.
///
/// **Tvoje mise:** Požadovaná mezera slov se ověří, ale vždy ji nahradí sedm jednotek. Rozšiř převod
/// časování a zachovej původní sedmijednotkový režim i ostatní mezery.
///
/// **Dnes trénuješ:** Přenes existující encoding rule (pravidlo kódování) do parameterised operation
/// (operace s parametrem). Zachovej časování písmen a značek, zatímco nový parameter (parametr) řídí
/// oddělení slov; původní chování ověř, ale nekopíruj kvůli němu celý postup.
///
/// Po Run File stiskni Enter pro E E, vyber mezeru slov 7 či 10 a otevři odpovídající
/// secret-beeps-gap7.wav nebo secret-beeps-gap10.wav.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F36 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
List<Integer> units(String text, int wordGap) {
    if (wordGap < 3 || wordGap > 12) throw new IllegalArgumentException("Word gap: 3..12 units.");
    var result = new ArrayList<Integer>();
    String[] words = code(text).split(" / ");
    for (int w = 0; w < words.length; w++) {
        if (w > 0) {
            result.add(-7);
        }
        String[] letters = words[w].split(" ");
        for (int l = 0; l < letters.length; l++) {
            if (l > 0) {
                result.add(-3);
            }
            for (int m = 0; m < letters[l].length(); m++) {
                if (m > 0) {
                    result.add(-1);
                }
                result.add(letters[l].charAt(m) == '.' ? 1 : 3);
            }
        }
    }
    return result;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F36">
/// ### Transfer (přenos): zachovej slib a přidej volbu
///
/// Extract helper (vyčlenění pomocné metody) pojmenuje jednu odpovědnost. Vstupy a výsledek tvoří
/// contract (smlouvu), kterou můžeš využít jinde.
///
/// ```java
/// String surround(String text, boolean stars) {
///     String edge = stars ? "*" : "#";
///     return edge + text + edge;
/// }
/// ```
///
/// `surround("hi", false)` vrátí `"#hi#"`; původní styl s mřížkami dál funguje. Volající může požádat
/// o nový styl bez opakování celé operace. Ověř obě volby, prázdný vstup i jiné slovo. Tests (testy)
/// ověří výsledky; vysvětlení, kdo vybírá styl a kdo sestavuje popisek, ověří rozdělení odpovědností.
/// Úspěšný nový režim nemá potichu měnit původní.
///
/// Předpověz: co vytvoří `surround("", true)` a která část by se měnila při změně obklopujícího
/// znaku?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `wordGap < 3 || wordGap > 12` — Ověří požadovanou mezeru před sestavením časových jednotek.
/// - `result.add(-7)` — Záporné jednotky znamenají ticho; tato konstanta nyní určuje každou mezeru slov.
/// - `result.add(-3)` — Zachovává samostatnou tříjednotkovou pause (pauzu) mezi písmeny.
/// - `charAt(m) == '.' ? 1 : 3` — Tečka je jedna znějící jednotka a čárka tři.
///
/// ### Co už obstarává připravená část
///
/// Code dodává Morseovy značky a oddělovače. Units je mění v kladné a záporné délky zvuku a ticha, signal
/// tvoří sample (vzorky) a write uloží WAV; původní units s jedním argument (argumentem) zachovává
/// výchozích sedm jednotek.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Záporné unit (jednotky) představují délku ticha.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Slova odděluje jen branch (větev) w>0.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Tam použij -wordGap, dvě ostatní mezery zachovej a vysvětli oddělení kódování textu od časování.
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
String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
String[] morse = {
    ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..",
    ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.",
    "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", "-----",
    ".----", "..---", "...--", "....-", ".....", "-....", "--...", "---..", "----."
};

String code(String text) {
    if (text.isBlank() || text.length() > 40 || !text.matches("[A-Za-z0-9 ]+")) {
        throw new IllegalArgumentException("Use 1..40 ASCII letters, digits and spaces.");
    }
    var words = new StringJoiner(" / ");
    for (String word : text.strip().toUpperCase(Locale.ROOT).split(" +")) {
        var letters = new StringJoiner(" ");
        for (char c : word.toCharArray()) {
            letters.add(morse[alphabet.indexOf(c)]);
        }
        words.add(letters.toString());
    }
    return words.toString();
}

/// Připravená syntéza: omezené sample loop (cykly vzorků) tvoří zvuk; automatické přehrávání zde není.
double[] signal(String text, int milliseconds) {
    if (milliseconds < 20 || milliseconds > 100) {
        throw new IllegalArgumentException("Unit duration: 20..100 ms.");
    }
    var units = units(text, wordGap);
    int frames = RATE * milliseconds / 1000;
    double[] signal = new double[units.stream().mapToInt(Math::abs).sum() * frames];
    int offset = 0;
    for (int unit : units) {
        int length = Math.abs(unit) * frames;
        if (unit > 0) {
            for (int i = 0; i < length; i++) {
                double fade = Math.min(1, Math.min(i, length - 1 - i) / 40.0);
                signal[offset + i] = 0.2 * fade * Math.sin(2 * Math.PI * 650 * i / RATE);
            }
        }
        offset += length;
    }
    return signal;
}

int wordGap = 7;
List<Integer> units(String text) {
    return units(text, 7);
}

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Code mapuje ASCII písmena/číslice na Morse; units vlastní podepsané délky tónu/ticha.
/// Signal rozvine délky na sample (vzorky) pomocí wordGap; main případně volí nový čtvrtý argument (argument) a write uloží WAV.
void main(String[] args) throws Exception {
    if (args.length == 0) {
        String entered = IO.readln("Message (blank E E, q quits): ");
        if (entered == null || entered.strip().equalsIgnoreCase("q")) return;
        String message = entered.isBlank() ? "E E" : entered;
        String gap = IO.readln("Word gap 3..12 (blank 10, q quits): ");
        if (gap == null || gap.strip().equalsIgnoreCase("q")) return;
        gap = gap.isBlank() ? "10" : gap.strip();
        args = new String[]{"secret-beeps-gap" + gap + ".wav", message, "60", gap};
    }

    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
    try {
        Path path = Path.of(args.length > 0 ? args[0] : "secret-beeps.wav");
        String text = args.length > 1 ? args[1] : "SOS JAVA";
        int unit = args.length > 2 ? Integer.parseInt(args[2]) : 60;
        if (args.length > 3) wordGap = Integer.parseInt(args[3]);
        IO.println(code(text));
/// Připravený WAV encoding (kódování): omezené PCM sample (vzorky) a try-with-resources spravují stream (proud).
        write(path, signal(text, unit));
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
/// - `'` — Jednoduché uvozovky ohraničují jeden character (znak), například značku na desce.
/// - `char` — Type (typ) pro jednu UTF-16 jednotku, často jednoduchý znak desky jako X. Pro text slouží
///   String.
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
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
/// - `Path` — Umístění v souborech, nikoli obsah souboru.
/// - `Files` — JDK operace pro čtení a zápis místních souborů.
// </editor-fold>

