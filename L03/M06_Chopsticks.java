/// # L03.06 · Chopsticks
///
/// Ťukni jednou ze svých živých rukou do soupeřovy ruky a přičti jí své prsty. Pět a více prstů ruku
/// vyřadí; vyřazením obou soupeřových rukou vyhraješ.
///
/// **Tvoje mise · Zavři podvodnou mezeru:** Rozhodčí nyní povolí každý úder. Sestav handsAlive: oba index
/// (indexy) musí být 0
/// nebo 1 a obě vybrané ruce musí mít více než nula prstů. Odmítni tah před neplatným lookup
/// (čtením). Method (metoda) pouze povoluje; nesmí měnit prsty ani hráče.
///
/// **Dnes trénuješ:** Několik guard (ochran) v pořadí: nejdřív ověř, že místo existuje, a potom zkoumej,
/// zda je použitelné. Odmítnutý zásah nesmí změnit prsty ani předat tah.
///
/// Spusť program a třikrát zadej 1 1 pro vyřazení ruky, potom zkus čtvrté 1 1 a sleduj, zda
/// neplatný zásah změní hráče.
boolean handsAlive(int own, int opposing) {
    if (own < 0 || own >= 2 || opposing < 0 || opposing >= 2) {
        return false;
    }
    if (hands[player][own] == 0 || hands[1 - player][opposing] == 0) {
        return false;
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F08">
/// Guard (ochranná podmínka) odmítne požadavek před chráněnou prací. Nejdřív přečti odmítané případy
/// a potom se ptej, co zůstává povolené. Short-circuit (zkrácené vyhodnocení) je důležité: u `A || B`
/// Java přeskočí B, pokud A už platí.
///
/// ```java
/// boolean useSlot(int slot) {
///     if (slot < 0 || slot >= 4) {
///         return false;
///     }
///     IO.println("Using slot " + slot);
///     return true;
/// }
/// ```
///
/// Předpověz výpis a vrácenou hodnotu pro `-1`, `3` a `4`. Jen trojka dojde k výpisu. Odmítnutí musí
/// nastat před jakoukoli state change (změnou stavu), která se nemá provést. Přístup na desku sám
/// může potřebovat kontrolu rozsahu, než jej lze bezpečně vyhodnotit. `return false` ukončí toto call
/// (volání metody); automaticky nevrací zpět práci již provedenou předtím.
///
/// `useSlot(3)` předá trojku do `slot`; `boolean` před názvem říká, že výsledkem je true/false. Nyní
/// předpověz slot nula.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Existence a potom život
///
/// Připravená tabulka hands patří současnému a soupeřícímu hráči. player je 0 nebo 1; 1 - player
/// vybere druhého. První index vybírá hráče, druhý jeho ruku. Potřebuješ jen dva vybrané počty. Guard
/// (ochranná podmínka) vrátí výsledek včas, aby se pozdější čtení a skutečný úder pro odmítnutou
/// volbu neprovedly.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Průchod hodnotami · zkus předpovědět">
/// Použitelná ruka musí existovat a mít prsty. own a opposing jsou vnitřní index (indexy) 0 nebo 1.
/// Neplatné indexy odmítni před čtením rukou. Potom počet nula znamená vyřazení. Ověřovací method
/// (metoda) nesmí přepnout hráče; command provede úspěšný úder později.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Připrav kamarádovi krátkou výzvu: posloupnost vyřazující ruku a potom pokus o neplatný úder.
/// Vystřídejte se jako hráč a rozhodčí. Později jako samostatný domácí experiment změň význam součtu
/// pěti a vysvětli změnu hry.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Neplatné číslo ruky a vyřazená ruka jsou dva různé důvody odmítnutí.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Oba indexy ověř před čtením hands[player][own] a hands[1 - player][opposing].
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Pro indexy mimo 0..1 vrať false; potom vrať false, je-li některý vybraný počet <= 0; jinak vrať
/// true.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[][] hands;
int player;
Set<String> seen;
String outcome;

void reset() {
    hands = new int[][]{{1, 1}, {1, 1}};
    player = 0;
    seen = new HashSet<>();
    outcome = "";
    seen.add(key());
}

String key() {
    return Arrays.deepToString(hands) + player;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 2);
    int a = Integer.parseInt(p[0]) - 1;
    int b = Integer.parseInt(p[1]) - 1;
    require(handsAlive(a, b));
    hands[1 - player][b] += hands[player][a];
    if (hands[1 - player][b] >= 5) {
        hands[1 - player][b] = 0;
    }
    if (hands[1 - player][0] + hands[1 - player][1] == 0) {
        outcome = "Player " + (player + 1) + " wins";
    }
    player = 1 - player;
    if (outcome.isEmpty() && !seen.add(key())) {
        outcome = "Draw: repeated state";
    }
}

void display() {
    IO.println("Player " + (player + 1) + "; P1 " + Arrays.toString(hands[0]) + " P2 " + Arrays.toString(hands[1]));
}

void main() {
    reset();
    IO.println("Chopsticks");
    IO.println("Enter own hand 1-2 and opposing hand 1-2. Five or more kills a hand; each turn is one attack. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter own hand 1-2 and opposing hand 1-2. Five or more kills a hand; each turn is one attack. /r restarts; /q quits.");
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
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
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
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Set` — Uchovává různé hodnoty; stejná hodnota je přítomná nejvýše jednou.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
