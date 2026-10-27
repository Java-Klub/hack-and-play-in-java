/// # L02.05 · AnagramRounds
///
/// Rozlušti pět přesmyček a zjisti, kolik jich vyřešíš na nejvýše tři odhady každé. Jedno kolo může
/// přijímat více skutečných přesmyček, takže správné může být i nečekané slovo.
///
/// **Tvoje mise:** Platná odpověď jako silent je odmítnuta kvůli jiné velikosti písmen než ve slovníku.
/// Oprav normalize a zachovej současné odstranění okrajových mezer.
///
/// **Dnes trénuješ:** String operation (operace s řetězcem): textová operace vytvoří výsledek, který
/// musíš použít. Řádek lze očistit na okrajích a potom upravit na velikost písmen očekávanou další částí
/// programu.
///
/// Použij Run File a v prvním kole napiš silent; po opravě vyzkoušej i směs velkých a malých písmen a
/// sleduj, zda je posouzena stejně.
/// Po opravě: Save → Test File → zahraj si znovu.
String normalize(String input) {
    return input.strip().toUpperCase(Locale.ROOT);
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `String normalize(String input)` — Přijmi celý input line (vstupní řádek) a vrať jeho sjednocený
///   text.
/// - `input` — Text přijatý při tomto call (volání), včetně napsaných mezer a různé velikosti písmen.
/// - `.strip()` — Požádej text o kopii bez whitespace (bílých znaků) na začátku a konci; prázdné závorky
///   nepředávají další input (vstup).
/// - `return input.strip();` — Vrať výsledek operace místo pouhého zavolání a zahození nového textu.
///
/// ### Co už obstarává připravená část
///
/// command používá normalize před porovnáním s připravenými odpověďmi. scramble zamíchá vypsaná písmena a
/// advance řídí pět kol a limit odhadů. Input loop (vstupní cyklus) už čte celé řádky a řeší opakování.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. strip už odstraňuje okrajové mezery.
/// 2. Operace se String (řetězcem) vrací nový String; výsledek musíš použít.
/// 3. Vrať input.strip().toUpperCase(Locale.ROOT).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String[][] answers = {{"LISTEN", "SILENT", "ENLIST", "TINSEL"}, {"STONE", "TONES", "NOTES"}, {"EARTH", "HEART", "HATER"}, {"ANGEL", "GLEAN", "ANGLE"}, {"NIGHT", "THING"}};
int round;
int tries;
int score;
String scrambled;
String outcome;

void scramble() {
    var letters = new ArrayList<Character>();
    for (char c : answers[round][0].toCharArray()) {
        letters.add(c);
    }
    Collections.shuffle(letters);
    scrambled = "";
    for (char c : letters) {
        scrambled += c;
    }
}

void reset() {
    round = tries = score = 0;
    outcome = "";
    scramble();
}

void advance() {
    round++;
    tries = 0;
    if (round == 5) {
        outcome = "Finished: " + score + "/5";
    } else {
        scramble();
    }
}

void command(String input) {
    require(outcome.isEmpty());
    String word = normalize(input);
    if (word.equals("SKIP")) {
        advance();
        return;
    }
    require(word.matches("[A-Z]+"));
    if (List.of(answers[round]).contains(word)) {
        score++;
        advance();
    } else {
        if (++tries == 3) {
            advance();
        }
    }
}

void display() {
    if (round < 5) {
        IO.println("Round " + (round + 1) + "/5: " + scrambled + "; tries " + tries + "/3. Score " + score);
    }
}

void main() {
    reset();
    IO.println("Anagram Rounds");
    IO.println("Unscramble five words, three guesses each; skip moves on. Accepted alternatives are curated. /r restarts; /q quits.");
    while (true) {
        display();
        if (!outcome.isEmpty()) {
            IO.println(outcome);
        }
        String input = IO.readln("> ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r")) {
            reset();
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. /r restarts; /q quits.");
            continue;
        }
        try {
            command(input.strip());
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move or command. Unscramble five words, three guesses each; skip moves on. Accepted alternatives are curated. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

/// Připravené vstupní body: main, reset, command.
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
/// - `char` — Type (typ) pro jednu UTF-16 jednotku, často jednoduchý znak desky jako X. Pro text slouží
///   String.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
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
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

