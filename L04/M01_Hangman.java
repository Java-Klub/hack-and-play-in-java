/// # L04.01 · Hangman
///
/// Odhaluj skryté slovo po písmenech, než šest chybných tipů ukončí kolo. Opakované písmeno může odkrýt
/// několik míst zároveň, takže každá nápověda může změnit tvůj další tip.
///
///
/// **Tvoje mise:** Uprav /hint na chytřejšího coach (poradce): doporučí první dosud skrytou samohlásku
/// ve slově zleva. Když žádná nezbývá, doporučí první jiné skryté písmeno; když je vše odhaleno, nic
/// nedoporučí. Dotaz nesmí odhalit písmeno, spotřebovat tip ani změnit kolo. Běžná hra už funguje.
///
/// **Dnes se učíš:** While loop (smyčka while) při hledání: pokračuj, dokud se může objevit lepší
/// kandidát, a pamatuj si náhradní možnost.
///
/// Zkus /hint před prvním tipem, potom A a dvakrát /hint. Porovnej doporučení s Tried a Misses; rada
/// není odehraný tip.
String hintLetter() {
    int index = 0;
    while (index < word.length()) {
        String letter = word.substring(index, index + 1);
        if (!tried.contains(letter)) {
            return letter;
        }
        index++;
    }
    return "";
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F10">
/// `while` ověřuje condition (podmínku) před každým průchodem včetně prvního. Je-li hned false
/// (nepravda), tělo proběhne nula krát. Něco musí postupovat k ukončení nebo záměrně čekat na nový
/// input (vstup).
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// int energy = 5;
/// while (energy > 0) {
///     IO.println(energy);
///     energy -= 2;
/// }
/// ```
///
/// Předpověz vypsaná čísla a konečnou uloženou hodnotu. výstup v terminálu (výstup) je `5`, `3`,
/// `1`; potom je energy `-1`, takže další condition neplatí. Podmínka se neověřuje uprostřed těla.
/// `energy -= 2` odečte dvojku a uloží výsledek; bez postupu se loop opakuje donekonečna. `return`
/// ukončí method (metodu); `break` ukončí loop (smyčku). Sleduj condition a měnící se hodnoty
/// odděleně, zejména pokud prohledávaná data mohou přibývat. Samotná přítomnost while nezaručuje,
/// že program někdy skončí; důvod ukončení musí vyplývat z prováděných kroků.
///
/// Nyní předpověz trace (stopu průběhu), pokud energy začíná na čtyřech.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Jak číst tento kód">
/// Query (dotaz) může číst tajná data bez odehrání tahu. Odděl tři otázky: co je skryto, kterému
/// skrytému písmenu dáš přednost a co se změní po skutečném tipu.
///
/// Pro jiné slovo METER s tried E zůstávají skryté M, T, R. Skrytá samohláska už není, náhradní možnost
/// je tedy M. Předpověz radu před tipem E. `substring(i, i + 1)` čte jeden znak jako String (řetězec);
/// `contains` se ptá na jeho přítomnost. Prázdný řetězec znamená žádné doporučení.
///
/// Najdi cestu od příkazu /hint k vypsanému textu. Zpracování skutečného tipu a funkční zobrazení slova
/// zachovej.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Která část pouze odpovídá a která mění Tried nebo Misses?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Sleduj /hint do hintLetter. Současný průchod končí u prvního skrytého písmene, takže neobjeví
/// pozdější samohlásku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Zapamatuj první skryté písmeno jako náhradu, pokračuj v hledání AEIOU a náhradu vrať až po
/// neúspěšném průchodu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta">
/// Dej spolužákovi dva poradce s jinými oblíbenými skupinami písmen. Ze tří dotazů má odhalit jejich
/// preference bez čtení kódu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String mask() {
    String result = "";
    int index = 0;
    while (index < word.length()) {
        String letter = word.substring(index, index + 1);
        if (tried.contains(letter)) {
            result += letter;
        } else {
            result += "_";
        }
        index++;
    }
    return result;
}

String[] words = {"LEVEL", "ROBOT", "GALAXY", "JAVA", "PIXEL", "PUZZLE", "PLANET", "BANANA"};
String word = "BANANA";
String tried = "";
int misses;

void reset() {
    word = words[new Random().nextInt(words.length)];
    tried = "";
    misses = 0;
}

boolean won() {
    return mask().equals(word);
}

boolean over() {
    return won() || misses >= 6;
}

boolean guess(String input) {
    String letter = input.toUpperCase(Locale.ROOT);
    if (over() || !letter.matches("[A-Z]") || tried.contains(letter)) {
        return false;
    }
    tried += letter;
    if (!word.contains(letter)) {
        misses++;
    }
    return true;
}

void main() {
    IO.println("Training word: BANANA. /r chooses a random word.");
    IO.println("HANGMAN: reveal the word before six wrong letters.");
    IO.println("Repeated letters cost nothing. Every letter accepts either case.");
    IO.println("/hint suggests a letter without playing it; /r chooses a new word; /q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            IO.println(mask() + "    Tried: " + tried + "    Misses: " + misses + "/6");
            if (over()) {
                IO.println((won() ? "You win!" : "Out of guesses!") + " Word: " + word);
            }
            showState = false;
        }
        String input = IO.readln(over() ? "Enter or /r: new round; /q: quit: " : "Guess A-Z, /hint advice, /r restart, /q quit: ");
        if (input == null || input.trim().equalsIgnoreCase("/q")) {
            return;
        }
        input = input.trim();
        if ((input.equalsIgnoreCase("/r") || (over() && input.isBlank()))) {
            reset();
            showState = true;
            continue;
        }
        if (input.equalsIgnoreCase("/hint")) {
            String suggestion = hintLetter();
            IO.println(suggestion.isEmpty() ? "No hidden letters remain." : "Coach suggests " + suggestion);
            continue;
        }
        if (over()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        if (guess(input)) {
            showState = true;
        } else {
            IO.println("Enter one new letter A-Z during an unfinished round.");
        }
    }
}

/// Připravené vstupní body: main, reset.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Malá mapa Javy · volitelné">
/// ## Neznámý zápis?
///
/// Tato malá mapa je dobrovolná. Najdi jen znak nebo slovo, kterému nerozumíš. Není třeba se ji učit
/// dopředu.
///
/// - `///` — Slova pro čtenáře. Java tyto comment (komentářové) řádky při spuštění přeskočí.
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
/// - `boolean` — Hodnota true nebo false: ano nebo ne.
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
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
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
