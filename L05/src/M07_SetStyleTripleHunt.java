/// # L05.07 · SetStyleTripleHunt
///
/// Předběhni kamaráda při hledání trojice, jejíž vlastnosti jsou vždy všechny stejné nebo všechny různé.
/// Vítězná trojice může využívat poslední kartu na stole.
///
/// **Tvoje mise:** Platná trojice s poslední kartou se přehlíží. Oprav hasSet(), aby se posoudila každá
/// kombinace tří různých karet.
///
/// **Dnes trénuješ:** Traversal and search (průchod a hledání): uspořádané indexy (pozice) vybírají
/// kombinace bez opakování karty. Meze průchodů jsou stejně důležité jako pravidlo shody.
///
/// Spusť Run File, prohlédni si zobrazené vlastnosti karet a zadej čísla tří karet, které podle tebe
/// tvoří platnou trojici.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F17 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
boolean hasSet() {
    for (int a = 0; a < table.size(); a++) {
        for (int b = a + 1; b < table.size(); b++) {
            for (int c = b + 1; c < table.size(); c++) {
                if (triple(table.get(a), table.get(b), table.get(c))) {
                    return true;
                }
            }
        }
    }
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F17">
/// ### Traversal: vždy jedna otázka
///
/// Traversal (průchod) navštěvuje hodnoty; search (hledání) může skončit první shodou, count
/// (počítání) musí prohlédnout všechny relevantní hodnoty. Ve for-each loop (cyklu pro každý prvek)
/// dostane jméno před dvojtečkou jednu hodnotu ze sequence (posloupnosti) za ní. Průběžný počet založ
/// mimo opakované tělo, aby dřívější shody přežily.
///
/// ```java
/// int[] pulses = {2, 0, 4};
/// int strong = 0;
/// for (int pulse : pulses) {
///     if (pulse > 1) {
///         strong++;
///     }
/// }
/// IO.println(strong);
/// ```
///
/// Výsledek je dvě, protože pulzy dva a čtyři splňují condition (podmínku). Kdybys potřeboval pouze
/// vědět, zda existuje nějaký silný pulz, kdy už můžeš odpovědět? Jaký počet má mít prázdné array
/// (pole)?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int b = a + 1` — Začne druhou kartu až za první, aby se jedna karta nepoužila dvakrát.
/// - `int c = b + 1` — Začne třetí kartu až za druhou, takže má každá kombinace jediné pořadí.
/// - `table.size()` — Počet právě dostupných karet, nikoli poslední platný index (pozice).
/// - `table.get(c)` — Přečte kartu na kandidátním indexu (pozici) c pro připravený dotaz triple().
///
/// ### Co už obstarává připravená část
///
/// triple() už porovnává zakódované vlastnosti karet. hasSet() mu vybírá kandidáty; připravená hra
/// výsledek využívá, když hráč tvrdí, že žádná trojice nezbývá.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Reading API (rozhraní čtení) stolu
///
/// `table` je připravený List (seznam): `size()` dává aktuální počet karet a `get(index)` jednu
/// přečte bez odebrání. Pozice začínají nulou jako u array (pole). Tři loop index (pozice cyklů)
/// záměrně rostou: a předchází b a b předchází c, takže stejnou kartu nevybereš dvakrát. Předpověz,
/// jak se poslední pozice dostane do trojice; připravený query (dotaz) `triple` dál posuzuje
/// vlastnosti.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Použij a < b < c, aby se karta neopakovala.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Největší dovolené c je table.size()-1.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Podmínka c loop porovnává c s table.size(), nikoli table.size()-1.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
ArrayDeque<Integer> deck;
List<Integer> table;
int score;
String outcome;

/// reset(): Obnovuje počáteční kolo Set-style Triple Hunt a maže stav patřící starému kolu.
void reset() {
    var cards = new ArrayList<Integer>();
    for (int i = 0; i < 81; i++) {
        cards.add(i);
    }
    Collections.shuffle(cards);
    deck = new ArrayDeque<>(cards);
    table = new ArrayList<>();
    score = 0;
    outcome = "";
    deal(12);
}

void deal(int count) {
    while (count-- > 0 && !deck.isEmpty()) {
        table.add(deck.remove());
    }
}

/// triple(): Dodané číslice v základu tři ověřují, že je každá vlastnost všechna stejná nebo
/// všechna různá.
boolean triple(int a, int b, int c) {
    for (int i = 0; i < 4; i++) {
        if ((a % 3 + b % 3 + c % 3) % 3 != 0) {
            return false;
        }
        a /= 3;
        b /= 3;
        c /= 3;
    }
    return true;
}

/// command(): Ověřuje jeden skutečný příkaz hráče před jeho provedením v této hře.
void command(String input) {
    require(outcome.isEmpty());
    if (input.equalsIgnoreCase("more")) {
        require(!hasSet() && !deck.isEmpty());
        deal(3);
    } else {
        String[] p = input.split("\\s+");
        require(p.length == 3);
        int[] ids = new int[3];
        for (int i = 0; i < 3; i++) {
            ids[i] = Integer.parseInt(p[i]) - 1;
            require(ids[i] >= 0 && ids[i] < table.size());
        }
        Arrays.sort(ids);
        require(ids[0] != ids[1] && ids[1] != ids[2] && triple(table.get(ids[0]), table.get(ids[1]), table.get(ids[2])));
        for (int i = 2; i >= 0; i--) {
            table.remove(ids[i]);
        }
        score++;
        deal(Math.max(0, 12 - table.size()));
    }
    if (deck.isEmpty() && !hasSet()) {
        outcome = "Finished: " + score + " triples found.";
    }
}

String code(int card) {
    String s = "";
    for (int i = 0; i < 4; i++) {
        s = (card % 3) + s;
        card /= 3;
    }
    return s;
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    IO.println("Four attributes, values 0/1/2: each attribute must be all equal or all different.");
    for (int i = 0; i < table.size(); i++) {
        IO.println((i + 1) + ": " + code(table.get(i)));
    }
    IO.println("Sets " + score + "; deck " + deck.size() + "; " + (hasSet() ? "A set exists" : "No set: use more if deck remains"));
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    IO.println("Set-style Triple Hunt");
    IO.println("Find three cards whose four attributes are each all equal or all different.");
    IO.println("Enter three card numbers, or more when no set exists. /r restarts; /q quits.");
    IO.println("Example format: 1 2 3 selects three displayed card numbers. Use more only when no triple exists.");
    display();
    while (true) {
        String input = IO.readln(outcome.isEmpty() ? "> " : "Enter or /r: new round; /q: quit: ");
        if (input == null) {
            return;
        }
        input = input.strip();
        if (input.equalsIgnoreCase("/q") || input.equalsIgnoreCase("q")) {
            return;
        }
        if (input.equalsIgnoreCase("/r") || input.equalsIgnoreCase("r")
                || (!outcome.isEmpty() && input.isEmpty())) {
            reset();
            display();
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("That round is over. Press Enter to play again, or /q to quit.");
            continue;
        }
        try {
            command(input);
            display();
            if (!outcome.isEmpty()) {
                IO.println(outcome);
            }
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move. " + "Enter three card numbers, or more when no set exists. /r restarts; /q quits.");
        }
    }
}

/// require(): Odmítá neplatný příkaz výjimkou zachycenou dodanou vstupní smyčkou.
void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
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
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `%` — Remainder (zbytek po dělení); hodí se pro opakující se pozice nebo tahy.
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
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>

