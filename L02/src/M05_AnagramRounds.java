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
///
/// Potřebuješ výklad? Nová dovednost · F05 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
String normalize(String input) {
    return input.strip().toUpperCase(Locale.ROOT);
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F05">
/// `IO.readln` přijímá celý řádek včetně vnitřních mezer. String operation (operace nad řetězcem)
/// vrací text, nemění původní String (řetězec). Potřebuješ-li výsledek, musíš jej uchovat.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// String raw = "  Quiet Room  ";
/// String cleaned = raw.strip();
/// String loud = cleaned.toUpperCase(Locale.ROOT);
/// ```
///
/// Předpověz všechny tři hodnoty. `raw` stále obsahuje okrajové mezery, `cleaned` je `Quiet Room` a
/// `loud` je `QUIET ROOM`. `strip()` odstraní okrajové whitespace (bílé znaky), nikoli mezeru mezi
/// slovy. Tečka vybere operaci hodnoty vlevo; závorky předávají její argument (argumenty).
/// `Locale.ROOT` žádá převod velikosti písmen nezávislý na jazyce místo místního nastavení počítače.
/// Připravený input code (vstupní část) řeší `null` při konci vstupu; prázdný řádek je jiná hodnota.
/// Samotné zavolání operace ještě nenahradí text uchovaný pod původním jménem.
///
/// Nyní předpověz cleaned a loud pro raw text `"  Soft Sky  "`.
// </editor-fold>

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

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// Chain (řetězení volání) používá vrácenou hodnotu každé operace jako receiver (příjemce) další
/// operace. Například `"  Hi  ".strip().length()` nejprve vytvoří `Hi`, potom jeho délku 2. Nyní
/// předpověz stejný chain pro `"  Hello  "`. Tvůj výpočet má stále vracet text, nikoli jeho délku.
/// Dostupná operace převodu na velká písmena je `toUpperCase(Locale.ROOT)`; `Locale.ROOT` je
/// připravená constant (konstanta) předávaná jako argument (argument), nikoli další call (volání
/// metody). Udrží porovnání se slovníkem stejné při různých místních nastaveních. Při přidávání
/// převodu velikosti písmen zachovej současné čištění okrajů.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// strip už odstraňuje okrajové mezery.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Operace se String (řetězcem) vrací nový String; výsledek musíš použít.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Vrať input.strip().toUpperCase(Locale.ROOT).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String[][] answers = {{"LISTEN", "SILENT", "ENLIST", "TINSEL"}, {"STONE", "TONES", "NOTES"}, {"EARTH", "HEART", "HATER"}, {"ANGEL", "GLEAN", "ANGLE"}, {"NIGHT", "THING"}};
int round;
int tries;
int score;
String scrambled;
String outcome;
String message;
String[] clues = {"Use your ears", "A small rock", "Our planet", "A winged figure in stories", "The time after sunset"};

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
    message = "Rearrange every shown letter into an English word. Example: TAC becomes CAT.";
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
        message = "Skipped. One accepted answer was " + answers[round][0] + ".";
        advance();
        return;
    }
    if (!word.matches("[A-Z]+")) {
        throw new IllegalArgumentException("Use letters A-Z after cleanup. If a lowercase English answer is rejected, that is the normalize mission's fault; uppercase input still lets you play.");
    }
    if (List.of(answers[round]).contains(word)) {
        message = "Accepted: " + word + ". One point!";
        score++;
        advance();
    } else {
        tries++;
        message = word.length() != answers[round][0].length()
            ? "Use all " + answers[round][0].length() + " letters exactly once; " + word + " has " + word.length() + "."
            : "That is not an accepted rearrangement. The word list is small; /words shows the accepted options.";
        message += " Guess used: " + tries + "/3.";
        if (tries == 3) {
            message += " One accepted answer was " + answers[round][0] + ".";
            advance();
        }
    }
}

void display() {
    IO.println(message);
    if (round < 5) {
        IO.println("Round " + (round + 1) + "/5: " + scrambled + "; guesses used " + tries + "/3; score " + score);
        IO.println("Clue for one possible answer: " + clues[round] + ". Other accepted anagrams can count too.");
    }
}

void main() {
    reset();
    IO.println("Anagram Rounds");
    IO.println("Use every shown letter once to make one English word. Example: TAC becomes CAT. Three guesses per word, five rounds. SKIP reveals an answer; /words lists accepted words without a guess; /r restarts; /q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
            showState = false;
        }
        String input = IO.readln(outcome.isEmpty() ? "Your word, SKIP or /words: " : "Enter or /r: new round; /q: quit: ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r") || (!outcome.isEmpty() && input.isBlank())) {
            reset();
            showState = true;
            continue;
        }
        if (input.strip().equalsIgnoreCase("/words") && outcome.isEmpty()) {
            IO.println("Accepted words: " + String.join(", ", answers[round]));
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        if (input.isBlank()) {
            IO.println("Type a word using the shown letters, SKIP for the next puzzle, or /words for choices. No guess spent.");
            continue;
        }
        try {
            command(input.strip());
            showState = true;
        } catch (IllegalArgumentException invalid) {
            IO.println(invalid.getMessage());
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
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>

