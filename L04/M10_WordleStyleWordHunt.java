/// # L04.10 · WordleStyleWordHunt
///
/// Hádej pětipísmenné slovo pomocí nápověd pro přesné pozice, přesunutá písmena a chybějící písmena.
/// Opakovaná písmena jsou zvlášť zajímavá: jediný dostupný výskyt nemůže vysvětlit dva různé tipované
/// znaky.
///
/// **Tvoje mise:** Cvičné slovo LEVEL obsahuje dvě L, ale pro ALLEY jedna žlutá shoda zmizí. Oprav
/// leftoverLetters tak, aby každé dosud nespárované tajné písmeno přispělo do svého připraveného counter
/// (počítadla) bez smazání dřívějších výskytů.
///
/// **Dnes trénuješ:** Více accumulator (průběžných součtů): připravené counts má jedno counter
/// (počítadlo) pro každé písmeno. Dva objevy nespárovaného L musejí oba přispět do stejného počítadla;
/// shody na přesné pozici jsou již oddělené.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L04/M10_WordleStyleWordHunt.java`
/// - Kontrola této mise: `java L04/Check.java M10_WordleStyleWordHunt.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše a napiš ALLEY proti cvičnému slovu LEVEL; porovnej
/// vypsanou nápovědu se dvěma L, která se obě mohou shodovat na jiném místě. Po opravě: ulož
/// soubor, spusť uvedenou kontrolu a zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F12 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
int[] leftoverLetters(String guess, char[] result) {
    int[] counts = new int[26];
    for (int i = 0; i < 5; i++) {
        if (secret.charAt(i) == guess.charAt(i)) {
            result[i] = 'G';
        } else {
            int letter = secret.charAt(i) - 'A';
            counts[letter] = 1;
        }
    }
    return counts;
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
/// - `int[] counts = new int[26];` — Připravené úložiště založí dvacet šest counter (počítadel) s nulou,
///   jedno pro každé velké písmeno.
/// - `for (int i = 0; i < 5; i++)` — Prohlédni každou z pěti pozic jednou, s index (pozicemi) nula až
///   čtyři.
/// - `result[i] = 'G';` — Připravená větev označí přesnou shodu jediným znakem G.
/// - `int letter = secret.charAt(i) - 'A';` — Tento připravený převod vybere counter (počítadlo) písmene:
///   A má nulu, B jedničku a tak dále.
/// - `counts[letter] = 1;` — Hranaté závorky vyberou jediné counter (počítadlo); toto assignment
///   (přiřazení) nyní nahrazuje jeho předchozí počet.
/// - `return counts;` — Předej všechny zbývající počty písmen druhému průchodu pro sestavení nápovědy.
///
/// ### Co už obstarává připravená část
///
/// grade volá leftoverLetters a potom zbývající počty spotřebovává na žluté shody, aby žádné tajné
/// písmeno nepoužila dvakrát. command kontroluje vypsaný seznam povolených slov a uchovává nejvýše šest
/// tipů; display vysvětluje G, Y a tečku. První kolo používá LEVEL, /r vybere náhodné slovo.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// U LEVEL proti ALLEY zůstávají na začátku obě L bez přesné shody.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// counts[letter] je jediný připravený counter (čítač) pro dané písmeno.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Použij counts[letter] += 1 místo = 1.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String[] words = {"LEVEL", "LEVER", "RIVER", "ROBOT", "APPLE", "ALLEY", "EAGER", "EARTH", "STONE", "MONEY"};
String secret;
String outcome;
List<String> history;

void reset() {
    secret = words[new Random().nextInt(words.length)];
    history = new ArrayList<>();
    outcome = "";
}

String grade(String guess) {
    char[] result = ".....".toCharArray();
    int[] counts = leftoverLetters(guess, result);
    for (int i = 0; i < 5; i++) {
        if (result[i] != 'G' && counts[guess.charAt(i) - 'A'] > 0) {
            result[i] = 'Y';
            counts[guess.charAt(i) - 'A']--;
        }
    }
    return new String(result);
}

void command(String input) {
    require(outcome.isEmpty());
    String word = input.toUpperCase(Locale.ROOT);
    require(List.of(words).contains(word));
    history.add(word + " " + grade(word));
    if (word.equals(secret)) {
        outcome = "You win!";
    } else {
        if (history.size() == 6) {
            outcome = "Out of tries. Word: " + secret;
        }
    }
}

void display() {
    IO.println("Allowed: " + String.join(" ", words));
    IO.println("G exact, Y elsewhere, . absent; attempts " + history.size() + "/6");
    for (String row : history) {
        IO.println(row);
    }
}

void main() {
    reset();
    secret = "LEVEL";
    IO.println("Training word: LEVEL. /r chooses a random word.");
    IO.println("Wordle-style Word Hunt");
    IO.println("Guess one allowed five-letter word in six tries. /r restarts; /q quits.");
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
        if (!outcome.isEmpty()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            command(input.strip());
            showState = true;
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move or command. Guess one allowed five-letter word in six tries. /r restarts; /q quits.");
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
/// - `'` — Jednoduché uvozovky ohraničují jeden character (znak), například značku na desce.
/// - `char` — Type (typ) pro jednu UTF-16 jednotku, často jednoduchý znak desky jako X. Pro text slouží
///   String.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
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
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>

