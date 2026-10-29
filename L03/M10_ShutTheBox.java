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
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L03/M10_ShutTheBox.java`
/// - Kontrola této mise: `java L03/Check.java M10_ShutTheBox.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše, porovnej dvě vypsané hodnoty Dice s cílem rolled
/// a potom napiš různá otevřená čísla, která mají tento součet vytvořit. Po opravě: ulož soubor,
/// spusť uvedenou kontrolu a zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F13 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
int diceTotal(int first, int second) {
    return first;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F13">
/// Method (metoda) zabalí operaci pod jediné jméno. Parameter (parametry) jsou místní jména informací
/// přijatých při každém call (volání); argument (argumenty) jsou skutečně předané hodnoty. `return`
/// předá jediný výsledek zpět volající části.
///
/// ```java
/// int remaining(int capacity, int used) {
///     return capacity - used;
/// }
///
/// void main() {
///     IO.println(remaining(12, 5));
///     IO.println(remaining(9, 4));
/// }
/// ```
///
/// Předpověz obě vypsané hodnoty: sedmičku a pětku. Čárka odděluje parameter či argument a jejich
/// pořadí rozhoduje, kterou hodnotu jméno dostane. Jména popisují role; informace si sama
/// nezískávají. Tato method počítá bez vypisování a změny připraveného state (stavu programu). Při
/// opravě výpočtu vyzkoušej druhou dvojici argument, aby se za jediný případ nemohla schovat náhodná
/// pevná odpověď. Method se provede až při skutečném call, nikoli samotným zapsáním definice.
///
/// Nyní předpověz remaining(10, 6).
// </editor-fold>

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

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Použij různé hodnoty, například 2 a 5, aby byl ignorovaný parametr vidět.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Oba parameter (parametry) patří tomuto konkrétnímu volání.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Vrať first + second.
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
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
