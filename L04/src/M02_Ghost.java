/// # L04.02 · Ghost
///
/// Společně stavte začátek slova a v každém tahu přidejte jedno písmeno. Hráč prohraje, pokud dokončí
/// slovníkové slovo alespoň o čtyřech písmenech nebo nezanechá žádné možné pokračování.
///
/// **Tvoje mise:** R je platný začátek pro ROBOT, ROBIN a ROCK, ale hra jej odmítá. Oprav possiblePrefix
/// tak, aby hledání mohlo dojít ke každému slovu ve slovníku a přitom skončilo, jakmile zná odpověď.
///
/// **Dnes trénuješ:** Hledání pomocí while: prohlédni vždy jednu možnost a po neshodě posuň index
/// (pozici). Existují dva běžné konce: return true při nalezení vhodného slova, nebo dosažení konce
/// slovníku a return false.
///
/// Použij Run File, přečti vypsaný slovník a napiš R; starter (výchozí program) chybně oznámí slepý
/// začátek navzdory uvedeným slovům na R.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F10 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
boolean possiblePrefix(String candidate) {
    int index = 0;
    while (index < words.length && index < 1) {
        if (words[index].startsWith(candidate)) {
            return true;
        }
        index++;
    }
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F10">
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
/// Předpověz vypsaná čísla a konečnou uloženou hodnotu. Output (výstup) je `5`, `3`, `1`; potom je
/// energy `-1`, takže další condition neplatí. Podmínka se neověřuje uprostřed těla. `energy -= 2`
/// odečte dvojku a uloží výsledek; bez postupu se loop opakuje donekonečna. `return` ukončí method
/// (metodu); `break` ukončí loop (smyčku). Sleduj condition a měnící se hodnoty odděleně, zejména
/// pokud prohledávaná data mohou přibývat. Samotná přítomnost while nezaručuje, že program někdy
/// skončí; důvod ukončení musí vyplývat z prováděných kroků.
///
/// Nyní předpověz trace (stopu průběhu), pokud energy začíná na čtyřech.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int index = 0;` — Začni u první položky slovníku. Toto nastavení proběhne za celé hledání jen
///   jednou.
/// - `while (index < words.length && index < 1)` — Obě porovnání musí být true (pravda). První hlídá
///   hranici slovníku; přidané porovnání nyní hledání dál omezuje.
/// - `words[index].startsWith(candidate)` — Ověř, zda slovo na této pozici začíná celým přijatým
///   fragmentem.
/// - `return true;` — Nalezené vhodné slovo stačí k okamžitému ukončení method (metody); další slova už
///   nejsou potřeba.
/// - `index++;` — Po slově, které nesplnilo kontrolu začátku, přejdi k další položce slovníku.
/// - `return false;` — K této odpovědi se dojde, když smyčka skončí bez nalezení pokračování.
///
/// ### Co už obstarává připravená část
///
/// words je připravený veřejný slovník, který se před hrou vypíše. command přidá písmeno, zavolá
/// possiblePrefix, ověří dokončení slova a vystřídá hráče. display a main zajišťují textové prostředí,
/// restart i ukončení.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// index < 1 skončí po jediné položce slovníku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Hranice má odpovídat words.length a po každém neúspěchu musí přijít index++.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Odstraň nadbytečné omezení index < 1; kontrolu délky zachovej.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String[] words = {"GHOST", "GHOUL", "GAME", "GAMER", "GARDEN", "ROBOT", "ROBIN", "ROCK", "STONE", "STORY", "START", "STARTER"};
String fragment;
String outcome;
int player;

void reset() {
    fragment = "";
    outcome = "";
    player = 1;
}

void command(String input) {
    require(outcome.isEmpty());
    String letter = input.toUpperCase(Locale.ROOT);
    require(letter.matches("[A-Z]"));
    fragment += letter;
    boolean possible = possiblePrefix(fragment);
    if (!possible || fragment.length() >= 4 && List.of(words).contains(fragment)) {
        outcome = "Player " + (3 - player) + " wins; player " + player + " completed a word or dead prefix.";
    } else {
        player = 3 - player;
    }
}

void display() {
    IO.println("Public dictionary: " + String.join(" ", words));
    IO.println("Player " + player + ", fragment: " + fragment);
}

void main() {
    reset();
    IO.println("Ghost");
    IO.println("Append one letter; completing a word of 4+ letters or a dead prefix loses. /r restarts; /q quits.");
    IO.println("The dictionary is deliberately small. /words lists its words without using a turn.");
    boolean showState = true;
    while (true) {
        if (showState) {
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
            showState = false;
        }
        String input = IO.readln(outcome.isEmpty() ? "> " : "Enter or /r: new round; /q: quit: ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r") || (!outcome.isEmpty() && input.isBlank())) {
            reset();
            showState = true;
            continue;
        }
        if (input.strip().equalsIgnoreCase("/words")) {
            IO.println("This small game uses only these words: " + String.join(", ", words));
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            command(input.strip());
            showState = true;
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move or command. Append one letter; completing a word of 4+ letters or a dead prefix loses. /r restarts; /q quits.");
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
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
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

