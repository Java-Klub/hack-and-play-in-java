/// # L02.08 · PokerDice
///
/// Hoď pěti kostkami, ponech slibné hodnoty a před uložením bodů přehoď vybrané pozice. Pět kol umožní
/// porovnat malé jisté odměny se snahou získat silnější kombinace.
///
/// **Tvoje mise:** Cvičná kombinace 2,2,2,3,4 je chybně odměněna jako full house. Oprav fullHouse tak,
/// aby byla vyžadována současně trojice i dvojice.
///
/// **Dnes trénuješ:** Skládání boolean condition (logických podmínek): OR přijme kteroukoli část, zatímco
/// AND vyžaduje obě. U trojice bez dvojice nejdřív urči obě pravdivostní hodnoty zvlášť a teprve potom je
/// spoj.
///
/// Použij Run File pro prohlédnutí cvičné kombinace a bodů, potom například příkazem roll 4 5 přehoď
/// pouze tyto dvě kostky a příkazem bank ulož body.
/// Po opravě: Save → Test File → zahraj si znovu.
boolean fullHouse(int largestGroup, int pairs) {
    return largestGroup == 3 || pairs == 1;
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `boolean fullHouse(int largestGroup, int pairs)` — Pravidlo přijme dva spočítané údaje a vrátí
///   jediné rozhodnutí o zařazení.
/// - `largestGroup == 3` — Ověř, zda největší skupina stejných kostek obsahuje tři kostky; == porovnává
///   tato čísla.
/// - `pairs == 1` — Ověř, zda byla nalezena právě jedna dvojice.
/// - `||` — Logical OR (logické nebo): true, když platí alespoň jedna strana, včetně případů, kdy druhá
///   neplatí.
/// - `return` — Vrať spojený výsledek true/false do výpočtu bodů.
///
/// ### Co už obstarává připravená část
///
/// points už počítá skupiny kostek a ptá se fullHouse na tuto kategorii. reset dodává známou cvičnou
/// kombinaci; pozdější volání deal losují náhodné kombinace a command řeší přehazování i ukládání bodů.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Napiš, zda je každá polovina true u trojice bez dvojice.
/// 2. OR přijme kteroukoli polovinu; toto pravidlo potřebuje obě.
/// 3. Spoj obě comparison (porovnání) pomocí &&.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[] dice;
int rolls;
int round;
int total;
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
    round = total = 0;
    outcome = "";
    deal();
    dice = new int[]{2, 2, 2, 3, 4};
}

int points() {
    int[] n = new int[7];
    for (int d : dice) {
        n[d]++;
    }
    int max = Arrays.stream(n).max().orElse(0);
    int pairs = 0;
    for (int v : n) {
        if (v == 2) {
            pairs++;
        }
    }
    boolean straight = true;
    for (int i = 2; i <= 5; i++) {
        straight &= n[i] == 1;
    }
    straight &= n[1] == 1 || n[6] == 1;
    return max == 5 ? 50 : max == 4 ? 40 : fullHouse(max, pairs) ? 35 : straight ? 30 : max == 3 ? 20 : pairs == 2 ? 10 : pairs == 1 ? 5 : 0;
}

void reroll(String[] p) {
    require(rolls < 3 && p.length > 1);
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

void command(String input) {
    require(outcome.isEmpty());
    if (input.equalsIgnoreCase("bank")) {
        total += points();
        round++;
        if (round == 5) {
            outcome = "Five rounds finished. Total: " + total;
        } else {
            deal();
        }
    } else {
        String[] p = input.split("\\s+");
        require(p[0].equalsIgnoreCase("roll"));
        reroll(p);
    }
}

void display() {
    IO.println("Round " + (round + 1) + "/5; dice " + Arrays.toString(dice) + "; roll " + rolls + "/3; current points " + points() + "; total " + total);
    IO.println("Five50 Four40 FullHouse35 Straight30 Three20 TwoPairs10 Pair5 High0. Equal categories tie.");
}

void main() {
    reset();
    IO.println("Poker Dice");
    IO.println("roll positions(1-5) rerolls those dice, at most twice; bank scores the hand. Five rounds. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. roll positions(1-5) rerolls those dice, at most twice; bank scores the hand. Five rounds. /r restarts; /q quits.");
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
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

