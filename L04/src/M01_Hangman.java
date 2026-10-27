/// # L04.01 · Hangman
///
/// Odhaluj skryté slovo po písmenech, než šest chybných tipů ukončí kolo. Opakované písmeno může odkrýt
/// několik míst zároveň, takže každá nápověda může změnit tvůj další tip.
///
/// **Tvoje mise:** Zobrazení ztrácí poslední znak každého slova. Oprav hranici while v mask tak, aby
/// každý znak vytvořil právě jedno písmeno nebo podtržítko; postup index dál zachovej.
///
/// **Dnes trénuješ:** While loop (smyčka s podmínkou): před každým průchodem ověř podmínku, udělej část
/// práce a posuň se ke konci. U šestipísmenného slova jsou platné index (pozice) 0 až 5; následující
/// index už musí smyčku zastavit.
///
/// Použij Run File a napiš A pro cvičné slovo BANANA; porovnej zobrazené _A_A_ se šesti místy, která
/// slovo potřebuje.
/// Po opravě: Save → Test File → zahraj si znovu.
String mask() {
    String result = "";
    int index = 0;
    while (index < word.length() - 1) {
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

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `String result = "";` — Před sbíráním znaků slova jednou založ prázdný String (řetězec) result.
/// - `int index = 0;` — První znak má index (pozici) nula; index pojmenovává právě zpracovávanou pozici.
/// - `while (index < word.length() - 1)` — Opakuj, dokud je toto porovnání true (pravda). length() dává
///   počet znaků; rozliš počet znaků a poslední platný index (pozici).
/// - `word.substring(index, index + 1)` — Přečti jeden znak jako text: první pozici zahrň a před druhou
///   skonči.
/// - `result += "_";` — Připoj jedno podtržítko k již nasbíranému textu místo nahrazení celého výsledku.
/// - `index++;` — Po každém průchodu zvětši pozici o jedna. Bez postupu by se mohl stále dokola
///   zpracovávat tentýž znak.
/// - `return result;` — Po skončení smyčky vrať volající části dokončený text k zobrazení.
///
/// ### Co už obstarává připravená část
///
/// Připravená větev na jedné pozici vybere odkryté písmeno nebo podtržítko. guess kontroluje tipy a
/// počítá chyby; main vypisuje mask, řeší konec po šesti chybách a přijímá /r nebo /q. Samotná mask se
/// hráče na input (vstup) neptá.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Slovo délky šest má index (indexy) 0 až 5.
/// 2. Vyhodnoť současnou condition (podmínku), když index je pět.
/// 3. Pokračuj při index < word.length(); index++ zajišťuje postup.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
    IO.println("/r chooses a new word; /q quits.");
    while (true) {
        IO.println(mask() + "    Tried: " + tried + "    Misses: " + misses + "/6");
        if (over()) {
            IO.println((won() ? "You win!" : "Out of guesses!") + " Word: " + word);
        }
        String input = IO.readln(over() ? "/r replay, /q quit: " : "Guess A-Z, /r restart, /q quit: ");
        if (input == null || input.trim().equalsIgnoreCase("/q")) {
            return;
        }
        input = input.trim();
        if (input.equalsIgnoreCase("/r")) {
            reset();
            continue;
        }
        if (!guess(input)) {
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
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
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
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

