/// # L04.12 · WordChain
///
/// Střídejte se v prodlužování řetězu slov: každé nové slovo musí začínat posledním písmenem předchozího
/// a žádné se nesmí opakovat. Vyber pokračování, které soupeři nechá málo možností.
///
/// **Tvoje mise:** Úvodní nabídka si nechává jediné slovo, i když jich je platných mnoho. Oprav choices
/// tak, aby uchovala každé přijatelné slovo nalezené ve smyčce místo ztrácení dřívějších nálezů.
///
/// **Dnes trénuješ:** Accumulation (postupné sbírání) do seznamu: průběžným výsledkem může být seznam
/// místo čísla. Prázdný seznam vytvoř jednou a při objevování dalších platných možností uchovej dřívější
/// položky.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L04/M12_WordChain.java`
/// - Kontrola této mise: `java L04/Check.java M12_WordChain.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše a porovnej celý Dictionary s jedinou další
/// nabídkou; po opravě napiš APPLE a prohlédni nabídnutá pokračování na E. Po opravě: ulož soubor,
/// spusť uvedenou kontrolu a zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F12 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
List<String> choices() {
    var list = new ArrayList<String>();
    for (String w : words) {
        if (!used.contains(w) && (last.isEmpty() || w.charAt(0) == last.charAt(last.length() - 1))) {
            list.add(w);
        }
    }
    return list;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F12">
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
/// - `var list = new ArrayList<String>();` — Připravený řádek vytvoří prázdný seznam textových položek;
///   var dovolí compiler (překladači) odvodit jeho pevný typ z této počáteční hodnoty.
/// - `for (String w : words)` — Prober každé slovo připraveného slovníku jednou a aktuální slovo pojmenuj
///   w.
/// - `!used.contains(w)` — Tento připravený filtr požaduje slovo, které se dosud nehrálo.
/// - `list.clear();` — Smaž všechny dosud nasbírané položky; zvaž důsledek po několika úspěšných
///   průchodech.
/// - `list.add(w);` — Připoj aktuální přijaté slovo k průběžnému výsledku.
/// - `return list;` — Vrať nasbírané platné možnosti společně pro zobrazení i ověřování tahů.
///
/// ### Co už obstarává připravená část
///
/// Zbytek připraveného filtru již kontroluje požadované počáteční písmeno; první slovo takové omezení
/// nemá. command používá choices k ověření tahu a zaznamenává použitá slova; display používá tentýž
/// výsledek pro nabídku, takže oprava ovlivní skutečně povolenou hru.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// list.clear smaže dříve přijatá slova.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Prázdný List (seznam) už vzniká před začátkem cyklu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Odstraň list.clear(); zachovej list.add(w).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String[] words = {"APPLE", "EARTH", "HOUSE", "EAGLE", "ELEPHANT", "TIGER", "ROBOT", "TREE", "ECHO", "ORBIT", "TRAIN", "NIGHT", "TABLE", "ENGINE", "RAIN", "NORTH", "HERO", "OCEAN", "NEST", "TOWER"};
Set<String> used;
String last;
String outcome;
int player;

void reset() {
    used = new LinkedHashSet<>();
    last = "";
    outcome = "";
    player = 1;
}

void command(String input) {
    require(outcome.isEmpty());
    String word = input.toUpperCase(Locale.ROOT);
    require(choices().contains(word));
    used.add(word);
    last = word;
    int previous = player;
    player = 3 - player;
    if (choices().isEmpty()) {
        outcome = "Player " + previous + " wins: no unused continuation.";
    }
}

void display() {
    IO.println("Dictionary: " + String.join(" ", words));
    IO.println("Player " + player + ", used: " + used + "; next choices: " + choices());
}

void main() {
    reset();
    IO.println("Word Chain");
    IO.println("Enter an unused listed word beginning with the last letter. A player with no continuation loses. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter an unused listed word beginning with the last letter. A player with no continuation loses. /r restarts; /q quits.");
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
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
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
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `Set` — Uchovává různé hodnoty; stejná hodnota je přítomná nejvýše jednou.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>

