/// # L04.09 · EvilHangman
///
/// Hraj hangman proti počítači, který si ještě nezvolil jediné tajné slovo. Po každém tipu si nechává
/// největší skupinu slov slučitelných s další nápovědou, takže jej každé odkryté písmeno stojí možnosti.
///
/// **Tvoje mise:** Počítač počítá každou neprázdnou skupinu jako jediné slovo a zbytečně zahazuje
/// užitečné možnosti. Oprav familySize tak, aby total pamatovalo každé slovo započítané v předchozích
/// průchodech.
///
/// **Dnes trénuješ:** Accumulation (postupné sčítání): průběžný součet jednou nastav před smyčkou a potom
/// jej aktualizuj s využitím předchozí hodnoty. Skupina se třemi slovy potřebuje tři příspěvky, nikoli
/// trojí nahrazení stejným číslem.
///
/// Použij Run File a tipni E, abys viděl vybranou nápovědu; potom pomocí Test File porovnej skupinu tří
/// slov se skupinou jediného slova.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F12 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
int familySize(List<String> family) {
    int total = 0;
    for (int i = 0; i < family.size(); i++) {
        total = 1;
    }
    return total;
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F12">
/// Accumulator (průběžný výsledek) pamatuje rostoucí výsledek mezi průchody. Jeho počáteční hodnota
/// patří před loop (smyčku); uvnitř by výsledek začínal znovu pro každou položku. Uloženým výsledkem
/// může být text, nejen číslo.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// String receipt = "Items:";
/// for (String item : List.of("tea", "cake")) {
///     receipt += " " + item;
/// }
/// ```
///
/// Předpověz receipt po každém průchodu: `Items: tea`, potom `Items: tea cake`. `+=` spojí starou
/// hodnotu s novým příspěvkem a výsledek uloží. Samotné `=` uloženou hodnotu nahradí. `List.of(...)`
/// dodá pevnou sequence (posloupnost) uvedených hodnot; dvojtečka navštíví jednu položku za průchod.
/// Sleduj aktuální položku a accumulator ve dvou sloupcích, abys zjistil, zda dřívější práce přežije.
/// Počáteční hodnota má vzniknout jednou za celý výpočet, nikoli jednou za každou objevenou položku.
///
/// Nyní předpověz receipt, pokud připravený seznam obsahuje jen `"rice"`.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int total = 0;` — Před prvním průchodem začni s nulovým počtem slov.
/// - `for (int i = 0; i < family.size(); i++)` — Navštiv každé slovo skupiny jednou; size() je počet slov
///   v připravené skupině.
/// - `total = 1;` — Assignment (přiřazení) nahradí staré total jedničkou, takže předchozí průchody nyní
///   nezanechají trvalý příspěvek.
/// - `return total;` — Po všech průchodech vrať nasčítanou velikost skupiny pro porovnání s ostatními
///   skupinami.
///
/// ### Co už obstarává připravená část
///
/// command rozdělí slučitelná slova podle nápovědy, kterou by odhalila, a volá familySize k porovnání
/// skupin. Připravené pravidlo rozhoduje remízy podle abecedního pořadí nápověd, uchovává dřívější
/// nápovědy a počítá chyby. display a main řeší tipy, restart i ukončení.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// total = 1 nahrazuje předchozí počet.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Accumulator (průběžný výsledek) musí pamatovat dřívější členy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Použij total += 1 a total = 0 nech před cyklem.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String[] dictionary = {"ALLY", "BETA", "COOL", "DEAL", "ELSE", "FLEW", "GOOD", "HOPE", "IBEX", "JAZZ", "KEPT", "LEND"};
List<String> pool;
String mask;
String tried;
String outcome;
int misses;

void reset() {
    pool = new ArrayList<>(List.of(dictionary));
    mask = "____";
    tried = "";
    misses = 0;
    outcome = "";
}

String pattern(String word, char letter) {
    String s = "";
    for (int i = 0; i < 4; i++) {
        s += word.charAt(i) == letter ? letter : mask.charAt(i);
    }
    return s;
}

void command(String input) {
    require(outcome.isEmpty());
    String letter = input.toUpperCase(Locale.ROOT);
    require(letter.matches("[A-Z]") && !tried.contains(letter));
    var families = new TreeMap<String, List<String>>();
    for (String word : pool) {
        families.computeIfAbsent(pattern(word, letter.charAt(0)), (k)->new ArrayList<>()).add(word);
    }
    String best = "";
    int count = -1;
    for (var entry : families.entrySet()) {
        if (familySize(entry.getValue()) > count) {
            best = entry.getKey();
            count = familySize(entry.getValue());
        }
    }
    pool = new ArrayList<>(families.get(best));
    if (best.equals(mask)) {
        misses++;
    }
    mask = best;
    tried += letter;
    if (!mask.contains("_")) {
        outcome = "You win: " + mask;
    } else {
        if (misses == 6) {
            outcome = "You lose. A consistent word: " + pool.getFirst();
        }
    }
}

void display() {
    IO.println(mask + "  Tried: " + tried + "  Misses: " + misses + "/6");
    IO.println("Largest family stays; ties use alphabetical mask order.");
}

void main() {
    reset();
    IO.println("Evil Hangman");
    IO.println("Guess one letter. The computer keeps a consistent largest word family. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Guess one letter. The computer keeps a consistent largest word family. /r restarts; /q quits.");
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
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
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
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

