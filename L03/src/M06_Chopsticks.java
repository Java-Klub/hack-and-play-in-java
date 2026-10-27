/// # L03.06 · Chopsticks
///
/// Ťukni jednou ze svých živých rukou do soupeřovy ruky a přičti jí své prsty. Pět a více prstů ruku
/// vyřadí; vyřazením obou soupeřových rukou vyhraješ.
///
/// **Tvoje mise:** Vyřazená ruka se stále může účastnit zásahu. Oprav handsAlive tak, aby neplatná čísla
/// rukou a kteroukoli ruku s nulou prstů odmítlo před změnou hry.
///
/// **Dnes trénuješ:** Několik guard (ochran) v pořadí: nejdřív ověř, že místo existuje, a potom zkoumej,
/// zda je použitelné. Odmítnutý zásah nesmí změnit prsty ani předat tah.
///
/// Použij Run File a třikrát zadej 1 1 pro vyřazení ruky, potom zkus čtvrté 1 1 a sleduj, zda neplatný
/// zásah změní hráče.
/// Po opravě: Save → Test File → zahraj si znovu.
boolean handsAlive(int own, int opposing) {
    if (own < 0 || own >= 2 || opposing < 0 || opposing >= 2) {
        return false;
    }
    if (hands[player][own] == 0 || hands[1 - player][opposing] == 0) {
        return false;
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `own` — Vybraný index (pořadí) útočící ruky, už převedený na nulu nebo jedničku.
/// - `opposing` — Vybraný index (pořadí) bránící ruky druhého hráče.
/// - `own < 0 || own >= 2` — Tyto alternativy odmítají číslo mimo dvě dostupná místa rukou.
/// - `hands[player][own]` — Přečti prsty z připravené dvouúrovňové tabulky: nejdřív vyber hráče a potom
///   jeho ruku.
/// - `< 0` — Toto se ptá, zda je hodnota záporná; vyřazená ruka má ve skutečnosti nula prstů.
/// - `return false;` — Ukonči kontrolu jako odmítnutou, jakmile guard (ochrana) najde problém.
/// - `return true;` — Konečného přijetí dosáhne pouze call (volání), které projde oběma ochranami.
///
/// ### Co už obstarává připravená část
///
/// command převede napsaná čísla rukou a ptá se handsAlive před zásahem. Připravené tělo přičítá prsty,
/// vyřazuje ruce, střídá hráče a odhaluje opakované pozice; main zobrazuje ruce obou hráčů.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Mrtvou ruku představuje nula, nikoli záporné číslo.
/// 2. Ochranu index (indexů) zachovej před přečtením obou rukou.
/// 3. Odmítni tah, když je kterákoli uložená hodnota ruky == 0.
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
    IO.println("Enter own hand 1-2 and opposing hand 1-2. Five or more kills a hand; no splitting. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter own hand 1-2 and opposing hand 1-2. Five or more kills a hand; no splitting. /r restarts; /q quits.");
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
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Set` — Uchovává různé hodnoty; stejná hodnota je přítomná nejvýše jednou.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

