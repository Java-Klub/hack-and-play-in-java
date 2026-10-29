/// # L02.07 · Codebreaker
///
/// Prolom čtyřmístný kód pomocí stop, které odlišují správnou číslici na správném místě od číslice
/// umístěné jinam. Po známém cvičném kódu spusť nové kolo s ukrytým náhodným kódem.
///
/// **Tvoje mise:** Zadání cvičného kódu dá čtyři přesné číslice, ale žádnou výhru. Oprav matchesCode tak,
/// aby přijímalo stejný text i ze samostatných String value (řetězcových hodnot).
///
/// **Dnes trénuješ:** Boolean decision (logické rozhodnutí) a rovnost textu: boolean znamená true nebo
/// false. U textu se porovnání znaků liší od otázky, zda dvě jména ukazují na tentýž uložený String
/// object (objekt řetězce).
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L02/M07_Codebreaker.java`
/// - Kontrola této mise: `java L02/Check.java M07_Codebreaker.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše a zadej 1234 pro prozkoumání chybné cvičné výhry;
/// po opravě zadej r a vyzkoušej nově ukrytý kód. Po opravě: ulož soubor, spusť uvedenou kontrolu a
/// zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F06 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
boolean matchesCode(String guess) {
    return secret.equals(guess);
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F06">
/// Boolean expression (logický výraz) dává `true` nebo `false`. `&&` vyžaduje obě condition
/// (podmínky), `||` alespoň jednu a `!` obrátí logický výsledek. Comparison (porovnání) jako `>=`
/// zahrnuje rovnost; `>` ji nezahrnuje.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// int age = 14;
/// boolean hasTicket = false;
/// boolean enter = age >= 12 && hasTicket;
/// boolean recognised = "open".equals("OPEN");
/// ```
///
/// Předpověz oba výsledky: každý je `false`. Věk stačí, ale podmínka vstupenky ne. `equals` porovnává
/// obsah String (řetězců) a rozlišuje velikost písmen; `equalsIgnoreCase` ji záměrně ignoruje. U
/// object (objektů) se `==` ptá, zda dva reference (odkazy) označují tentýž objekt, nikoli zda mají
/// stejný text. Porovnání obsahu a rozsahu odpovídají na různé otázky. Než condition uvěříš, ověř
/// hodnotu přesně na hranici a těsně za ní.
///
/// Nyní předpověz enter pro věk dvanáct s platnou vstupenkou.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `boolean` — Výsledek je rozhodnutí ano/ne: true pro shodu a jinak false.
/// - `matchesCode(String guess)` — Připravená volající část se tohoto pravidla ptá na celý hádaný text.
/// - `secret` — Uložený cílový kód tohoto kola.
/// - `guess` — Text přijatý pro tento pokus, ne nutně tentýž uložený object (objekt) jako secret.
/// - `==` — U String object (objektů řetězců) se ptá na identity (totožnost), nikoli stejné znaky; liší
///   se to od číselné rovnosti.
/// - `return` — Předej rozhodnutí zpět do submit, kde určuje výhru kola.
///
/// ### Co už obstarává připravená část
///
/// submit ověří čtyři číslice, spočítá přesné a jinak umístěné shody a požádá matchesCode o rozhodnutí
/// výhry. První secret je 1234 pro zkoumání; reset pro další kola vytváří náhodné kódy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Dvě samostatné String value (řetězcové hodnoty) mohou obsahovat stejný text.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Použij operaci porovnání obsahu String (řetězců).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Vrať secret.equals(guess).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String secret = "1234";
int attempts;
boolean won;
int[] feedback = {0, 0};

String newCode() {
    Random random = new Random();
    String code = "";
    for (int i = 0; i < 4; i++) {
        code += random.nextInt(1, 7);
    }
    return code;
}

void reset() {
    secret = newCode();
    attempts = 0;
    won = false;
    feedback = new int[] {0, 0};
}

boolean over() {
    return won || attempts >= 10;
}

int[] score(String guess) {
    boolean[] usedSecret = new boolean[4];
    boolean[] usedGuess = new boolean[4];
    int exact = 0;
    int elsewhere = 0;
    for (int i = 0; i < 4; i++) {
        if (secret.charAt(i) == guess.charAt(i)) {
            exact++;
            usedSecret[i] = true;
            usedGuess[i] = true;
        }
    }
    for (int i = 0; i < 4; i++) {
        if (usedGuess[i]) {
            continue;
        }
        for (int j = 0; j < 4; j++) {
            if (!usedSecret[j] && guess.charAt(i) == secret.charAt(j)) {
                elsewhere++;
                usedSecret[j] = true;
                break;
            }
        }
    }
    return new int[] {exact, elsewhere};
}

boolean submit(String guess) {
    if (over() || !guess.matches("[1-6]{4}")) {
        return false;
    }
    feedback = score(guess);
    attempts++;
    won = matchesCode(guess);
    return true;
}

void main() {
    IO.println("Training opening: code 1234. Restart deals a random code.");
    IO.println("CODEBREAKER: guess four digits 1-6 in ten attempts. Repeats are allowed.");
    IO.println("Exact = right digit and position; elsewhere = right digit, different position.");
    boolean showState = true;
    while (true) {
        if (showState) {
            if (over()) {
                IO.println((won ? "You win!" : "Out of attempts!") + " Code: " + secret);
            }
            showState = false;
        }
        String input = IO.readln(over() ? "Enter or /r: new round; /q: quit: " : "Attempt " + (attempts + 1) + "/10, r restart, q quit: ");
        if (input == null || (input.trim().equalsIgnoreCase("q") || input.trim().equalsIgnoreCase("/q"))) {
            return;
        }
        input = input.trim();
        if ((input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r") || (over() && input.isBlank()))) {
            reset();
            showState = true;
            continue;
        }
        if (over()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        if (submit(input)) {
            showState = true;
            IO.println(input + " -> exact " + feedback[0] + ", elsewhere " + feedback[1]);
        } else {
            IO.println("Enter exactly four digits 1-6 during an unfinished round.");
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
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
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
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>

