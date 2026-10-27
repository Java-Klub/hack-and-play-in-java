/// # L02.10 · MagicNumberCards
///
/// Mysli si číslo od 0 do 63 a řekni programu, na kterých ze šesti číselných karet se vyskytuje. Stroj
/// spojí tvoje odpovědi ano/ne a odhalí zvolené číslo.
///
/// **Tvoje mise:** Kouzlo považuje napsané odpovědi ano za ne. Oprav yesAnswer tak, aby y i Y poznalo
/// podle textu a n zůstalo false.
///
/// **Dnes trénuješ:** Boolean text comparison (logické porovnání textu): rozhodnutí může vzniknout z
/// textu místo arithmetic (aritmetiky). Stejně vypadající text může být uložen samostatně a velikost
/// písmen je další vlastnost, kterou musí porovnání řešit.
///
/// Použij Run File, mysli si 5 a na šest karet odpověz y, n, y, n, n, n, abys porovnal oznámené číslo
/// před opravou a po ní.
/// Po opravě: Save → Test File → zahraj si znovu.
boolean yesAnswer(String input) {
    return input == "y";
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `boolean` — Odpověď true/false na otázku, zda tento řádek znamená ano.
/// - `yesAnswer(String input)` — Pojmenované rozhodnutí o textu, které přijímá celou odpověď jako input
///   (vstup).
/// - `input` — Přijatý řádek hráče; není to doslovný text v uvozovkách v tvém source (zdrojovém textu).
/// - `==` — U String object (objektů řetězců) porovnává identity (totožnost) místo textového obsahu.
/// - `"y"` — Jednoznakový String literal (doslovný řetězec); dvojité uvozovky označují text, nikoli
///   variable (proměnnou) pojmenovanou y.
/// - `return` — Pošli rozhodnutí do připraveného loop (cyklu) pro čtení karet.
///
/// ### Co už obstarává připravená část
///
/// card sestaví šest vypsaných karet a reveal spojí jejich připravené číselné hodnoty. main ověří y/n a
/// zeptá se yesAnswer, kterou odpověď uložit. Pro tuto opravu nemusíš rozebírat matematiku binárních
/// karet.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Zadaný text nemusí být tentýž String object (objekt řetězce) jako doslovný text v uvozovkách.
/// 2. Porovnání bez rozlišení velikosti písmen zpracuje y i Y.
/// 3. Vrať input.equalsIgnoreCase("y").
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
List<Integer> card(int bit) {
    if (bit < 0 || bit > 5) {
        throw new IllegalArgumentException("Card index: 0..5.");
    }
    var values = new ArrayList<Integer>();
    for (int value = 0; value < 64; value++) {
        if ((value & (1 << bit)) != 0) {
            values.add(value);
        }
    }
    return values;
}

int reveal(boolean[] answers) {
    if (answers.length != 6) {
        throw new IllegalArgumentException("Six answers required.");
    }
    int result = 0;
    for (int bit = 0; bit < 6; bit++) {
        if (answers[bit]) {
            result += 1 << bit;
        }
    }
    return result;
}

void main() {
    IO.println("Think of a number from 0 to 63. Is it on each card?");
    boolean[] answers = new boolean[6];
    for (int bit = 0; bit < 6; ) {
        IO.println(card(bit));
        String input = IO.readln("On this card? y/n (q to quit): ");
        if (input == null || input.equalsIgnoreCase("q")) {
            return;
        }
        if (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n")) {
            IO.println("Please enter y or n.");
            continue;
        }
        answers[bit] = yesAnswer(input);
        bit++;
    }
    IO.println("Your number is " + reveal(answers) + ".");
}

/// Připravené vstupní body: main.
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
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
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
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

