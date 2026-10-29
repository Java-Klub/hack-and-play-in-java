/// # 04.15 · Nim
///
/// **Tvoje mise · Napiš nástroj k vítězství:** Pozoruj fungujícího bota, odvoď, jakou hromádku chce
/// zanechat, a proměň tuto myšlenku v rádce člověku. Otazník má doporučit 3 ze sedmi a 1 z osmi.
/// Check se rádce ptá také na prázdnou hromádku: vrať 0, protože tam žádný tah neexistuje. Dotaz jen
/// radí: nesmí odebrat kameny ani předat tah. Bota zachovej.
///
/// Výchozí program funguje podle původních pravidel. Kontrola už očekává tvůj nový cíl.
///
/// Hraj pomocí 1–3, otazník žádá radu, r restartuje a q ukončí hru. Výchozí program už dovoluje hrát
/// proti botovi; nedokončený rádce říká 0.

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

// <editor-fold defaultstate="expanded" desc="Průzkum a pokus">
/// Před čtením strategie pozoruj dvě různé volby bota. Potom najdi výpočet výběru tahu a odliš výběr
/// od provedení. Rádce dostává zkoumanou hromádku jako parameter (parametr), proto smí poradit i k
/// jiné pozici bez změny hry. Neutrální příklad: pomocník předpovídá pro dodanou teplotu bez změny
/// uložené dnešní teploty. Použij myšlenku, nikoli čtení nesouvisející globální hodnoty.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · oblast hledání">
/// Najdi vstupní větev pro ?. Jakou vrácenou hodnotu vypisuje? Porovnej pomocníka s rozhodováním
/// bota.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · konkrétní cesta">
/// advice(pile) má zkoumat pile; botMove čte stones. Stejnou myšlenku větvení lze použít bez pohybu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná úprava · prozrazuje postup">
/// Použij pile % 4: pro nulu vrať 0, pro kladný násobek čtyř 1 a jinak zbytek.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Dej kamarádovi záměrně slabého rádce a zachovej silného bota. Vysvětli hromádku, kde slabá rada
/// promarní výhodu. Vrať silného rádce a porovnej stejnou pozici.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Program · prozkoumej podle potřeby">
int advice(int pile) {
    if (pile == 0) return 0;
    int remainder = pile % 4;
    if (remainder == 0) return 1;
    return remainder;
}

int botMove() {
    if (stones == 0) {
        return 0;
    }
    int remainder = stones % 4;
    if (remainder == 0) {
        return 1;
    } else {
        return remainder;
    }
}


int stones = 21;
boolean humanTurn = true;
int winner;

void reset() {
    stones = 21;
    humanTurn = true;
    winner = 0;
}

boolean take(int amount) {
    if (winner != 0 || amount < 1 || amount > 3 || amount > stones) {
        return false;
    }
    stones -= amount;
    if (stones == 0) {
        winner = humanTurn ? 1 : 2;
    } else {
        humanTurn = !humanTurn;
    }
    return true;
}

void main() {
    IO.println("NIM: take 1-3 stones. Taking the last stone wins.");
    IO.println("You play first against the bot. ?: coach; r restarts; q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            if (winner == 0 && !humanTurn) {
                int amount = botMove();
                IO.println("Bot takes " + amount + ".");
                take(amount);
            }
            IO.println("Stones: " + stones + " " + "|".repeat(stones));
            if (winner != 0) {
                IO.println(winner == 1 ? "You win!" : "Bot wins!");
            }
            showState = false;
        }
        String input = IO.readln(winner == 0 ? "Take 1-3, r, q: " : "Enter or /r: new round; /q: quit: ");
        if (input == null || (input.trim().equalsIgnoreCase("q") || input.trim().equalsIgnoreCase("/q"))) {
            return;
        }
        input = input.trim();
        if ((input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r") || (winner != 0 && input.isBlank()))) {
            reset();
            showState = true;
            continue;
        }
        if (winner != 0) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        if (input.equals("?")) {
            IO.println("Coach suggests: " + advice(stones));
            continue;
        }
        try {
            if (take(Integer.parseInt(input))) {
                showState = true;
            } else {
                IO.println("Illegal move. Take 1-3 available stones during your turn.");
            }
        } catch (NumberFormatException exception) {
            IO.println("Enter a number, r or q.");
        }
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
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `%` — Remainder (zbytek po dělení); hodí se pro opakující se pozice nebo tahy.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
///   List<String> lomené závorky popisují element type (typ prvku).
/// - `?` — V condition ? first : second vybere první hodnotu, když condition (podmínka) platí, jinak
///   druhou.
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
