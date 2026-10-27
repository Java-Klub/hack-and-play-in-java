/// # L04.13 · YatzyScorecard
///
/// Sestavuj skóre z kostek rozhodováním, které přehodit a kterou kategorii využít. Každou kategorii lze
/// vyplnit jen jednou, takže body přijaté nyní mohou znamenat vzdání se lepší budoucí příležitosti.
///
/// **Tvoje mise:** Po zapsání několika kategorií zobrazený součet zapomíná dřívější body. Oprav hlavní
/// sum v total tak, aby přispěly všechny vyplněné kategorie, nevyplněné položky se přeskočily a
/// připravený bonus horní části zůstal zachovaný.
///
/// **Dnes trénuješ:** Průběžný součet: zachovej předchozí celkovou hodnotu a zahrň každý přijatý bodový
/// zisk. Zde jsou dva samostatné accumulator (průběžné součty), protože sum počítá všechny kategorie,
/// zatímco upper jen prvních šest pro pozdější rozhodnutí o bonusu.
///
/// Použij Run File, napiš bank 13, potom zapiš jinou volnou kategorii ukazující nenulové body a porovnej
/// vypsaný součet s oběma vyplněnými výsledky.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F12 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
int total() {
    int sum = 0;
    int upper = 0;
    for (int i = 0; i < 13; i++) {
        if (scores[i] >= 0) {
            sum += scores[i];
            if (i < 6) {
                upper += scores[i];
            }
        }
    }
    return sum + (upper >= 63 ? 35 : 0);
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
/// - `int sum = 0;` — Před procházením tabulky jednou založ celkový součet s nulou.
/// - `int upper = 0;` — Uchovávej samostatný průběžný součet pro šest horních kategorií; nezaměňuj jej s
///   celkovým součtem.
/// - `for (int i = 0; i < 13; i++)` — Navštiv všech třináct pozic kategorií jednou, od nuly do dvanácti.
/// - `scores[i] >= 0` — Nezáporná hodnota je vyplněná kategorie, včetně záměrné nuly; -1 znamená dosud
///   volnou kategorii.
/// - `sum = scores[i];` — Současné assignment (přiřazení) přepisuje průběžný součet body jediné
///   kategorie.
/// - `upper += scores[i];` — Tato připravená accumulation (postupné sčítání) uchovává dřívější body
///   horních kategorií před zahrnutím aktuálního zisku.
/// - `upper >= 63 ? 35 : 0` — Připravená krátká volba dává bonus 35, když upper dosáhne 63, jinak nulu;
///   bonus se přičítá jednou po smyčce.
///
/// ### Co už obstarává připravená část
///
/// value už počítá body třinácti kategorií z pěti kostek. command řeší přehazování a vyplní zvolenou
/// kategorii pouze jednou; display ukazuje dostupné body a volá total. Připravená pravidla obsahují horní
/// bonus, ale žádný Joker ani bonus opakovaného Yatzy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// sum = scores[i] v každé vyplněné kategorii nahradí součet.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// sum musí uchovat dřívější body; upper už sčítá správně.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Použij sum += scores[i].
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String[] names = {"Ones", "Twos", "Threes", "Fours", "Fives", "Sixes", "Three-kind", "Four-kind", "Full-house", "Small-straight", "Large-straight", "Yatzy", "Chance"};
int[] dice;
int[] scores;
int rolls;
int turn;
String outcome;
Random random = new Random();

void deal() {
    dice = new int[5];
    for (int i = 0; i < 5; i++) {
        dice[i] = random.nextInt(1, 7);
    }
    rolls = 1;
}

void reset() {
    scores = new int[13];
    Arrays.fill(scores, -1);
    turn = 0;
    outcome = "";
    deal();
}

int value(int category) {
    int[] n = new int[7];
    int sum = 0;
    for (int d : dice) {
        n[d]++;
        sum += d;
    }
    if (category < 6) {
        return n[category + 1] * (category + 1);
    }
    int max = Arrays.stream(n).max().orElse(0);
    boolean two = false;
    boolean three = false;
    for (int v : n) {
        two |= v == 2;
        three |= v == 3;
    }
    int run = 0;
    int longest = 0;
    for (int i = 1; i <= 6; i++) {
        run = n[i] > 0 ? run + 1 : 0;
        longest = Math.max(longest, run);
    }
    return switch (category) {
    case 6 -> max >= 3 ? sum : 0;
    case 7 -> max >= 4 ? sum : 0;
    case 8 -> two && three ? 25 : 0;
    case 9 -> longest >= 4 ? 30 : 0;
    case 10 -> longest == 5 ? 40 : 0;
    case 11 -> max == 5 ? 50 : 0;
    default -> sum;
    };
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length >= 2);
    if (p[0].equalsIgnoreCase("bank")) {
        require(p.length == 2);
        int c = Integer.parseInt(p[1]) - 1;
        require(c >= 0 && c < 13 && scores[c] < 0);
        scores[c] = value(c);
        turn++;
        if (turn == 13) {
            outcome = "Scorecard complete. Total including upper bonus: " + total();
        } else {
            deal();
        }
    } else {
        require(p[0].equalsIgnoreCase("roll") && rolls < 3);
        var ids = new HashSet<Integer>();
        for (int i = 1; i < p.length; i++) {
            int n = Integer.parseInt(p[i]) - 1;
            require(n >= 0 && n < 5 && ids.add(n));
        }
        for (int n : ids) {
            dice[n] = random.nextInt(1, 7);
        }
        rolls++;
    }
}

void display() {
    IO.println("Dice " + Arrays.toString(dice) + "; roll " + rolls + "/3; total " + total());
    for (int i = 0; i < 13; i++) {
        IO.println((i + 1) + " " + names[i] + ": " + (scores[i] >= 0 ? scores[i] : "open; now " + value(i)));
    }
    IO.println("Upper sum63 earns35. FullHouse25, SmallStraight30, Large40, Yatzy50. No Joker or repeat bonuses.");
}

void main() {
    reset();
    IO.println("Yatzy Scorecard");
    IO.println("roll positions(1-5), at most two rerolls; bank category(1-13) fills it once. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. roll positions(1-5), at most two rerolls; bank category(1-13) fills it once. /r restarts; /q quits.");
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
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
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
/// - `switch` — Vybere case (větev) z několika možností. Může také vrátit výsledek.
/// - `case` — Jedna větev switch; šipka ukazuje, co větev provede nebo vrátí.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

