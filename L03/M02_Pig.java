/// # L03.02 · Pig
///
/// Dva hráči závodí o uložení 50 bodů. Každý další hod může zvýšit odměnu současného tahu, ale jednička
/// ztratí neuložené body a předá kostku.
///
/// **Tvoje mise:** Výchozí verze zaměňuje účinky jedničky a šestky. Oprav výběr branch (větve) v roll
/// beze změny připravených účinků obou větví.
///
/// **Dnes trénuješ:** Účinky branch (větví): if/else může vybírat mezi změnou několika hodnot a změnou
/// jediné. Projdi zvlášť průběžné body tahu a hráče při ztrátovém i bodujícím hodu.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L03/M02_Pig.java`
/// - Kontrola této mise: `java L03/Check.java M02_Pig.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše a zadávej d pro hod nebo b pro uložení tahu;
/// sleduj, co se stane při jedničce a šestce. Po opravě: ulož soubor, spusť uvedenou kontrolu a
/// zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F07 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
boolean roll(int die) {
    if (winner != -1 || die < 1 || die > 6) {
        return false;
    }
    if (die == 1) {
        turnTotal = 0;
        player = 1 - player;
    } else {
        turnTotal += die;
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F07">
/// `if` položí boolean question (logickou otázku). Jeho tělo běží jen při pravdivé odpovědi. `else
/// if` položí další otázku teprve po neúspěchu předchozích; poslední `else` pokrývá všechny zbývající
/// možnosti.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// int temperature = 10;
/// if (temperature < 0) {
///     IO.println("Frozen");
/// } else if (temperature < 20) {
///     IO.println("Cool");
/// } else {
///     IO.println("Warm");
/// }
/// ```
///
/// S desítkou se vypíše `Cool`: první comparison (porovnání) neplatí a druhé ano. Předpověz zprávy
/// pro `-3` a `20`. Každé spuštění vypíše právě jednu zprávu. Na pořadí záleží: příliš široká
/// dřívější branch (větev) může zabránit dosažení další. Uvnitř výpočtu může branch místo výpisu
/// použít `return` výsledku. Ten okamžitě dokončí toto call (volání metody); neopustí pouze větev.
/// Složené závorky ukazují, které statement (příkazy) patří k jednotlivým možnostem.
///
/// Nyní předpověz zprávu při přesně nula stupních.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `boolean roll(int die)` — Přijmi hodnotu jediné kostky a vrať, zda byl hod přijat.
/// - `winner != -1 || die < 1 || die > 6` — Připravený guard (ochrana) odmítá ukončené kolo nebo nemožnou
///   kostku; != znamená nerovnost a || spojuje alternativy.
/// - `if (die == 6)` — Toto nyní rozhoduje, která platná strana kostky vybere branch (větev) ztráty tahu.
/// - `turnTotal = 0;` — Nahraď neuložený součet nulou.
/// - `player = 1 - player;` — Přepni mezi index (indexy) hráčů nula a jedna.
/// - `turnTotal += die;` — Přičti kostku k předchozímu součtu místo nahrazení součtu.
/// - `return true;` — Oznam, že platný hod byl zpracován.
///
/// ### Co už obstarává připravená část
///
/// bank uloží body současného tahu a ověří závod k 50. main vylosuje kostku, zavolá roll, potom vypíše
/// stav a zpracuje další příkaz. Body neukončeného tahu zůstávají oddělené od uloženého skóre.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Projdi současnou condition (podmínku) pro die = 1.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Obě těla branch (větví) jsou připravená; chybná je výběrová condition (podmínka).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// If comparison (porovnání) musí být die == 1.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[] scores = {0, 0};
int player;
int turnTotal;
int winner = -1;

void reset() {
    scores[0] = 0;
    scores[1] = 0;
    player = 0;
    turnTotal = 0;
    winner = -1;
}

boolean bank() {
    if (winner != -1 || turnTotal == 0) {
        return false;
    }
    scores[player] += turnTotal;
    turnTotal = 0;
    if (scores[player] >= 50) {
        winner = player;
    } else {
        player = 1 - player;
    }
    return true;
}

void main() {
    reset();
    var random = new Random();
    IO.println("PIG: two players race to 50 banked points.");
    IO.println("d rolls, b banks. A 1 should lose this turn's points; banking keeps them and passes play. /r restarts; /q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            IO.println("Scores: P1 " + scores[0] + ", P2 " + scores[1] + "; turn total " + turnTotal);
            if (winner != -1) IO.println("Player " + (winner + 1) + " wins!");
            showState = false;
        }
        String input = IO.readln(winner == -1 ? "Player " + (player + 1) + ": d roll, b bank, /r, /q: " : "Enter or /r: new round; /q: quit: ");
        if (input == null) return;
        input = input.strip().toLowerCase(Locale.ROOT);
        if (input.equals("q") || input.equals("/q")) return;
        if (input.equals("r") || input.equals("/r") || (winner != -1 && input.isEmpty())) {
            reset(); showState = true; continue;
        }
        if (winner != -1) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        if (input.equals("d")) {
            int before = player;
            int die = random.nextInt(1, 7);
            roll(die);
            IO.println("Player " + (before + 1) + " rolled " + die + ". " + (player != before ? "Turn passed; the turn total is now " + turnTotal + "." : "The same player continues."));
            showState = true;
        } else if (input.equals("b") && bank()) {
            IO.println("Turn points banked.");
            showState = true;
        } else {
            IO.println("Use d to roll, b to bank a positive turn total, /r or /q.");
        }
    }
}

/// Připravené vstupní body: main, reset.
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
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
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
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
