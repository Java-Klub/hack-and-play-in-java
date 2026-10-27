/// # L03.10 · ShutTheBox
///
/// Hoď dvěma kostkami a zavři otevřená číslovaná políčka se součtem odpovídajícím hodu. Snaž se ponechat
/// užitečný výběr pro další hody a zavřít všechna políčka, než nezbude platná možnost.
///
/// **Tvoje mise:** Cíl zahrnuje pouze první kostku, ačkoli se vypisují obě strany. Oprav diceTotal tak,
/// aby přispívaly obě přijaté hodnoty kostek.
///
/// **Dnes trénuješ:** Opakovaně použitelný výpočet: dva parameter (parametry) dovolí jediné method
/// (metodě) pracovat s libovolnou dvojicí kostek. Volání s 1 a 1 nebo se 6 a 6 má například sledovat
/// stejné pravidlo bez kopírování těla.
///
/// Použij Run File, porovnej dvě vypsané hodnoty Dice s cílem rolled a potom napiš různá otevřená čísla,
/// která mají tento součet vytvořit.
/// Po opravě: Save → Test File → zahraj si znovu.
int diceTotal(int first, int second) {
    return first + second;
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int diceTotal(int first, int second)` — Method (metoda) vrací celé číslo a přijímá dvě samostatně
///   pojmenovaná celá čísla.
/// - `first` — Hodnota první kostky dodaná při tomto call (volání).
/// - `second` — Hodnota druhé kostky, dostupná i přesto, že ji nehotové tělo ignoruje.
/// - `,` — Odděl dvě declaration (zavedení) parametrů; jejich hodnoty nespojuje.
/// - `return first;` — Toto nyní vrací pouze jediný input (vstup) místo výsledku používajícího oba.
///
/// ### Co už obstarává připravená část
///
/// roll vylosuje a vypíše dvě skutečné kostky a potom volá diceTotal. possible kontroluje, zda otevřená
/// políčka umožní splnit cíl, command zavře vybraná políčka a display ukáže zbývající volby.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Použij různé hodnoty, například 2 a 5, aby byl ignorovaný parametr vidět.
/// 2. Oba parameter (parametry) patří tomuto konkrétnímu volání.
/// 3. Vrať first + second.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
boolean[] open;
int target;
String outcome;
Random random = new Random();

void reset() {
    open = new boolean[10];
    Arrays.fill(open, true);
    open[0] = false;
    outcome = "";
    roll();
}

int sum() {
    int n = 0;
    for (int i = 1; i <= 9; i++) {
        if (open[i]) {
            n += i;
        }
    }
    return n;
}

boolean possible() {
    for (int bits = 1; bits < 512; bits++) {
        int total = 0;
        boolean legal = true;
        for (int i = 1; i <= 9; i++) {
            if ((bits & (1 << (i - 1))) != 0) {
                total += i;
                legal &= open[i];
            }
        }
        if (legal && total == target) {
            return true;
        }
    }
    return false;
}

void roll() {
    firstDie = random.nextInt(1, 7);
    secondDie = random.nextInt(1, 7);
    target = diceTotal(firstDie, secondDie);
    if (!possible()) {
        outcome = "No legal closure. Final open sum: " + sum();
    }
}

void command(String input) {
    require(outcome.isEmpty());
    var chosen = new HashSet<Integer>();
    int total = 0;
    for (String p : input.split("\\s+")) {
        int n = Integer.parseInt(p);
        require(n >= 1 && n <= 9 && open[n] && chosen.add(n));
        total += n;
    }
    require(total == target);
    for (int n : chosen) {
        open[n] = false;
    }
    if (sum() == 0) {
        outcome = "Box shut: you win!";
    } else {
        roll();
    }
}

void display() {
    String s = "Open: ";
    for (int i = 1; i <= 9; i++) {
        if (open[i]) {
            s += i + " ";
        }
    }
    IO.println("Dice: " + firstDie + " + " + secondDie);
    IO.println(s + "; rolled " + target);
}

void main() {
    reset();
    IO.println("Shut the Box");
    IO.println("Enter distinct open numbers whose sum matches two dice. No legal subset ends the round. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter distinct open numbers whose sum matches two dice. No legal subset ends the round. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

int firstDie;
int secondDie;

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
/// - `!=` — Ověří, že se dvě hodnoty liší.
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

