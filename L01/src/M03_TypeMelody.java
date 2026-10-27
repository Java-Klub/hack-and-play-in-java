// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
// </editor-fold>

/// # L01.03 · TypeMelody
///
/// Proměň krátkou napsanou zprávu v malou melodii. Každé písmeno dostane tón, zatímco jiné znaky nechají
/// tichý úsek, takže různé zprávy vytvářejí různé rytmy.
///
/// **Tvoje mise:** První WAV obsahuje čtyři tiché úseky. Změň zprávu poskytovanou defaultMessage tak, aby
/// úvodní melodie vyjadřovala JAVA.
///
/// **Dnes trénuješ:** Text literal (doslovný text): dvojité uvozovky označí svůj obsah jako textová data.
/// Číslice v uvozovkách například zůstane znakem zprávy; nestane se výpočtem.
///
/// Použij Run File, v kartě Files projektu najdi melody.wav a otevři uložený soubor ve zvukovém
/// přehrávači, abys zprávu slyšel.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F02 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
String defaultMessage() {
    return "1234";
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F02">
/// String literal (doslovně zapsaný řetězec) je text mezi dvojitými uvozovkami. Vnější uvozovky
/// říkají Javě, kde text začíná a končí; nevypisují se. I číslice v uvozovkách jsou text. Expression
/// (výraz) naopak může před vypsáním vypočítat hodnotu.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// IO.println("Shelf 4");
/// IO.println(4 + 2);
/// IO.println("Shelf \"B\"");
/// ```
///
/// Předpověz tři řádky. Jsou to `Shelf 4`, `6` a `Shelf "B"`. V posledním literal představuje každé
/// `\"` jednu uvozovku patřící do textu. Zpětné lomítko je zápis, nikoli další vypsaný znak.
/// `IO.println` vypíše svůj argument (předanou hodnotu) a ukončí řádek. Mezery uvnitř literal
/// zůstávají skutečnými mezerami; změna comment (komentářů) vypsanou zprávu nezmění. Rozlišuj tedy
/// znaky zapsané ve source (zdrojovém textu) a znaky, které se opravdu objeví v output (výstupu).
///
/// Nyní předpověz `IO.println(4 + 5);` a `IO.println("4 + 5");`.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `String` — Výsledkem je String (řetězec): posloupnost textových znaků.
/// - `defaultMessage()` — Jméno s prázdnými závorkami je připravená otázka bez input (vstupu): jaká
///   zpráva se má použít?
/// - `{` — Složené závorky uzavírají statement (příkazy), které na otázku odpovídají.
/// - `return` — Předej následující hodnotu zpět části, která požádala o zprávu.
/// - `"1234"` — Uvozovky patří k zápisu Javy; čtyři znaky uvnitř se stanou skutečnou zprávou.
/// - `;` — Zde ukonči tento return statement (příkaz vrácení hodnoty).
///
/// ### Co už obstarává připravená část
///
/// main zavolá defaultMessage, aby získala text, a zapíše melody.wav. synthesize převede písmena na
/// zvukové vzorky a write uloží přehratelný WAV. Tato zvuková část je připravená; přehrávání obstará
/// samostatný přehrávač.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// Tento program exportuje zvuk místo otevření herního okna. Po Run File přečti umístění za `Saved:`
/// v Output. Přepni na **Window → Files**, rozbal L01 a podle potřeby obnov seznam souborů; najdi
/// `melody.wav`. Uložený soubor otevři systémovým zvukovým přehrávačem. Pokud jej NetBeans
/// nepřehraje, najdi vypsanou cestu v systémovém správci souborů a otevři jej tam. Čtyři tiché úseky
/// výchozí verze jsou záměrné pozorování, nikoli selhání zvukové karty. Po úpravě source (zdrojového
/// textu) znovu použij Run File k nahrazení WAV a znovu jej otevři; Test File kontroluje chování, ale
/// tento poslech neprovádí.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Zprávou je pouze text mezi uvozovkami.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Připravený synthesizer (syntetizér) přiřazuje číslicím tiché úseky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Použij return "JAVA"; potom znovu vytvoř a otevři melody.wav.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int SAMPLE_RATE = 22050;
final int NOTE_SAMPLES = 2205;

byte[] synthesize(String text, long seed) {
    if (text.isEmpty() || text.length() > 120) {
        throw new IllegalArgumentException("Use 1..120 characters.");
    }
    int[] scale = {0, 2, 4, 5, 7, 9, 11};
    int root = 60 + new Random(seed).nextInt(12);
    byte[] pcm = new byte[text.length() * NOTE_SAMPLES * 2];
    for (int letter = 0; letter < text.length(); letter++) {
        char character = text.charAt(letter);
        if (character >= 'A' && character <= 'Z') character = (char) (character + 'a' - 'A');
        if (character < 'a' || character > 'z') continue;
        int index = character - 'a';
        int note = root + scale[index % scale.length] + 12 * (index / 7 % 2);
        double frequency = 440 * Math.pow(2, (note - 69) / 12.0);
        for (int sample = 0; sample < NOTE_SAMPLES; sample++) {
            double fade = Math.min(1.0, Math.min(sample, NOTE_SAMPLES - 1 - sample) / 220.0);
            int value = (int) (6000 * fade * Math.sin(2 * Math.PI * frequency * sample / SAMPLE_RATE));
            int offset = (letter * NOTE_SAMPLES + sample) * 2;
            pcm[offset] = (byte) value;
            pcm[offset + 1] = (byte) (value >> 8);
        }
    }
    return pcm;
}

void write(String text, long seed, Path path) throws Exception {
    byte[] pcm = synthesize(text, seed);
    var format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
    Files.createDirectories(path.toAbsolutePath().getParent());
    try (var stream = new AudioInputStream(new ByteArrayInputStream(pcm), format, pcm.length / 2)) {
        AudioSystem.write(stream, AudioFileFormat.Type.WAVE, path.toFile());
    }
}

void main() throws Exception {
    Path path = Path.of("melody.wav");
    write(defaultMessage(), 42, path);
    IO.println("Saved: " + path.toAbsolutePath());
    IO.println("Open the WAV to hear the message; digits are rests.");
}

/// Připravené vstupní body: main, write.
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
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
///   se ale dál měnit může.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `%` — Remainder (zbytek po dělení); hodí se pro opakující se pozice nebo tahy.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
/// - `/` — Dělí čísla. Dělení dvou int hodnot zahodí desetinnou část.
/// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
///   List<String> lomené závorky popisují element type (typ prvku).
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
/// - `Path` — Umístění v souborech, nikoli obsah souboru.
/// - `Files` — JDK operace pro čtení a zápis místních souborů.
// </editor-fold>

