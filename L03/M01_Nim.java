/// # L03.01 · Nim
///
/// Ber ze společné hromádky jeden, dva nebo tři kameny a snaž se vzít poslední dřív než bot. Sleduj, jak
/// ponechaná velikost hromádky změní další volbu bota.
///
/// **Tvoje mise:** Bot bere jeden kámen i tehdy, když je užitečná jiná platná volba. Oprav dvě cesty v
/// botMove a zachovej bezpečné chování prázdné hromádky.
///
/// **Dnes trénuješ:** If/else branching (větvení): jediná condition (podmínka) vybere vrácený výsledek.
/// Porovnej hromádky sedmi a osmi kamenů: jejich zbytky se liší, proto má záležet na vybrané branch
/// (větvi).
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L03/M01_Nim.java`
/// - Kontrola této mise: `java L03/Check.java M01_Nim.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše a vezmi 2 kameny; porovnej další tah bota z 19
/// kamenů a pomocí r zkus stejný začátek znovu. Po opravě: ulož soubor, spusť uvedenou kontrolu a
/// zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F07 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
int botMove() {
    if (stones == 0) {
        return 0;
    }
    int remainder = stones % 4;
    if (remainder == 0) {
        return 1;
    } else {
        return 1;
    }
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F07">
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
/// - `int botMove()` — Výpočet bez input (vstupu), který vrací jediný celočíselný tah; čte připravenou
///   hodnotu stones.
/// - `if (stones == 0)` — Vyhodnoť číselnou rovnost v závorkách a tuto branch (větev) vyber jen pro
///   prázdnou hromádku.
/// - `return 0;` — Okamžitě dokonči výpočet bez tahu; pozdější statement (příkazy) tohoto call (volání)
///   se neprovedou.
/// - `stones % 4` — Remainder (zbytek) po dělení velikosti hromádky čtyřmi; například sedm zanechá zbytek
///   tři.
/// - `if (remainder == 0)` — Vyber náhradní branch (větev) pro násobek čtyř.
/// - `else` — Alternativní tělo se provede, když if condition (podmínka) neplatí; složené závorky toto
///   tělo sdružují.
///
/// ### Co už obstarává připravená část
///
/// take ověřuje a provádí tahy, střídá hráče a zapisuje vítěze. main volá botMove při tahu bota a dodává
/// výpis hromádky, zadání tahů, restart a konec. Výpočet bota má tah vrátit, nikoli sám odebrat kameny.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// Poslední kámen vyhrává; v tahu se berou jeden, dva nebo tři. Uvažuj odzadu: hráč před čtyřmi
/// kameny nemůže zabránit soupeři, aby v dalším tahu vzal zbytek. Myšlenka se opakuje ve skupinách po
/// čtyřech: na soupeřův odběr lze odpovědět tak, aby oba tahy společně odebraly čtyři. Z počtu
/// nedělitelného čtyřmi se snaž soupeři nechat násobek čtyř. Z přesného kladného násobku žádný tah
/// nezaručí výhru proti dokonalým odpovědím; tento bot používá platnou náhradní volbu jednoho kamene.
/// Prázdná hromádka nemá tah. Přelož pravidlo do dvou připravených branch (větví), aniž by botMove
/// samo kameny odstraňovalo.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Projdi sedm kamenů: remainder (zbytek) je tři.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Else branch (větev jinak) se má lišit od větve s nulovým zbytkem.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// V else vrať remainder místo 1.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
    IO.println("You play first against the bot. r restarts; q quits.");
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
