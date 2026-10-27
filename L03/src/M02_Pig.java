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
/// Použij Run File a zadávej d pro hod nebo b pro uložení tahu; sleduj, co se stane při jedničce a
/// šestce.
/// Po opravě: Save → Test File → zahraj si znovu.
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

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Projdi současnou condition (podmínku) pro die = 1.
/// 2. Obě těla branch (větví) jsou připravená; chybná je výběrová condition (podmínka).
/// 3. If comparison (porovnání) musí být die == 1.
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
    Random random = new Random();
    IO.println("PIG: two players race to 50 banked points.");
    IO.println("Roll 1: lose this turn's points. Bank: keep them and pass the die.");
    while (true) {
        IO.println("Scores: P1 " + scores[0] + ", P2 " + scores[1] + "; turn total " + turnTotal);
        if (winner != -1) {
            IO.println("Player " + (winner + 1) + " wins!");
        }
        String input = IO.readln(winner == -1 ? "Player " + (player + 1) + ": d roll, b bank, r restart, q quit: " : "r replay, q quit: ");
        if (input == null || input.trim().equalsIgnoreCase("q")) {
            return;
        }
        input = input.trim().toLowerCase(Locale.ROOT);
        if (input.equals("r")) {
            reset();
        } else if (input.equals("d") && winner == -1) {
            int die = random.nextInt(1, 7);
            roll(die);
            IO.println(die == 1 ? "Rolled 1. Turn lost!" : "Rolled " + die + ".");
        } else if (input.equals("b") && bank()) {
            IO.println("Points banked.");
        } else {
            IO.println("Use d to roll, b to bank a positive turn total, r or q.");
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
/// - `'` — Jednoduché uvozovky ohraničují jeden character (znak), například značku na desce.
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
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

