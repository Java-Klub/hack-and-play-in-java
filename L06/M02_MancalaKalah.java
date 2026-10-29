/// # L06.02 · MancalaKalah
///
/// Rozsévej kameny po jamkách, získej další tah a nasbírej víc než soupeř. Potom si v novém kole vyměňte
/// taktiku.
///
/// **Tvoje mise:** Restart může ponechat na tahu předchozího hráče. Oprav reset(), aby nové jamky i
/// aktivní hráč patřili stejnému čerstvému kolu.
///
/// **Dnes trénuješ:** Scope and state lifetime (platnost jména a životnost stavu): lokální player po
/// návratu z reset() zanikne, ale field (proměnná programu) player zůstává. Obnov stav, který skutečně
/// čtou příkazy.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L06/M02_MancalaKalah.java`
/// - Kontrola této mise: `java L06/Check.java M02_MancalaKalah.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše a zadej číslo jedné své jamky od 1 do 6, aby se
/// její kameny rozesely po desce. Po opravě: ulož soubor, spusť uvedenou kontrolu a zahraj si
/// znovu.
///
/// Potřebuješ výklad? Připomenutí · F14 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
void reset() {
    pits = new int[14];
    Arrays.fill(pits, 4);
    pits[6] = pits[13] = 0;
    int player = 0;
    outcome = "";
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F14">
/// ### Scope: které úložiště jméno označuje?
///
/// Field (položka objektu) patří aktuálnímu objektu a může přežít více volání. Local variable (místní
/// proměnná) patří jedinému call (volání) či bloku. Declaration (deklarace) obsahuje type (typ);
/// assignment (přiřazení) do existujícího jména nevytváří další proměnnou. Místní jméno může zastínit
/// stejně napsaný field.
///
/// ```java
/// int volume = 7;
///
/// void whisper() {
///     int volume = 2;
///     IO.println(volume);
/// }
/// ```
///
/// `whisper()` vypíše dvě, zatímco field zůstane sedm: local declaration (místní deklarace) vytvořila
/// jiné úložiště. Předpověz pozdější čtení field; `this.volume` podle potřeby výslovně označí field.
/// Která hodnota má přežít několik volání a která patří pouze dočasnému výpočtu?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `new int[14]` — Vytvoří čtrnáct nulových pozic pro jamky a pokladnice.
/// - `Arrays.fill(pits, 4)` — Vloží do každé pozice čtyři kameny před vyprázdněním pokladnic.
/// - `pits[6] = pits[13] = 0` — Stejná nula se přiřadí oběma pozicím pokladnic.
/// - `int player = 0` — Tato deklarace zavádí local variable (lokální proměnnou) player místo automatické
///   změny stejnojmenného pole programu.
///
/// ### Co už obstarává připravená část
///
/// command() už rozsévá kameny, přeskakuje soupeřovu pokladnici, řeší braní i konečné sebrání. side()
/// sčítá běžné jamky hráče; pro spravedlivý restart tyto mechanismy neměň.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Field přežívá mezi voláními command().
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Lokální int player zanikne při návratu z reset().
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Přiřaď existujícímu player; inicializaci jamek ponech.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[] pits;
int player;
String outcome;

/// side(): Sčítá šest obyčejných jamek jednoho hráče.
int side(int who) {
    int n = 0;
    for (int i = who * 7; i < who * 7 + 6; i++) {
        n += pits[i];
    }
    return n;
}

/// command(): Rozsévá kameny, přeskakuje cizí zásobník, bere protější kameny a vyhodnocuje prázdnou
/// stranu.
void command(String input) {
    require(outcome.isEmpty());
    int chosen = Integer.parseInt(input) - 1;
    require(chosen >= 0 && chosen < 6);
    int at = player * 7 + chosen;
    require(pits[at] > 0);
    int stones = pits[at];
    pits[at] = 0;
    while (stones > 0) {
        at = (at + 1) % 14;
        if (at == (player == 0 ? 13 : 6)) {
            continue;
        }
        pits[at]++;
        stones--;
    }
    int store = player == 0 ? 6 : 13;
    if (at >= player * 7 && at < player * 7 + 6 && pits[at] == 1 && pits[12 - at] > 0) {
        pits[store] += pits[12 - at] + 1;
        pits[12 - at] = pits[at] = 0;
    }
    if (side(0) == 0 || side(1) == 0) {
        for (int who = 0; who < 2; who++) {
            for (int i = who * 7; i < who * 7 + 6; i++) {
                pits[who == 0 ? 6 : 13] += pits[i];
                pits[i] = 0;
            }
        }
        outcome = pits[6] == pits[13] ? "Draw" : "Player " + (pits[6] > pits[13] ? 1 : 2) + " wins";
    } else {
        if (at != store) {
            player = 1 - player;
        }
    }
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    IO.println("Player " + (player + 1) + " chooses own pit 1-6 (left-to-right in its list).");
    IO.println("P1 " + Arrays.toString(Arrays.copyOfRange(pits, 0, 6)) + " store " + pits[6]);
    IO.println("P2 " + Arrays.toString(Arrays.copyOfRange(pits, 7, 13)) + " store " + pits[13]);
    IO.println("Opposite pairs: P1 pit1/P2 pit6 ... P1 pit6/P2 pit1.");
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    IO.println("Mancala / Kalah");
    IO.println("Collect more stones in your store. Sow one by one, skip the enemy store, and play again when you finish in your store.");
    IO.println("Kalah: enter own pit 1-6. Sow forward; skip enemy store; finish in own store plays again. /r restarts; /q quits.");
    IO.println("Example: 3 selects your third pit. The two printed pit lists each run from their own 1 to 6.");
    display();
    while (true) {
        String input = IO.readln(outcome.isEmpty() ? "> " : "Enter or /r: new round; /q: quit: ");
        if (input == null) {
            return;
        }
        input = input.strip();
        if (input.equalsIgnoreCase("/q") || input.equalsIgnoreCase("q")) {
            return;
        }
        if (input.equalsIgnoreCase("/r") || input.equalsIgnoreCase("r")
                || (!outcome.isEmpty() && input.isEmpty())) {
            reset();
            display();
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("That round is over. Press Enter to play again, or /q to quit.");
            continue;
        }
        try {
            command(input);
            display();
            if (!outcome.isEmpty()) {
                IO.println(outcome);
            }
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move. " + "Kalah: enter own pit 1-6. Sow forward; skip enemy store; finish in own store plays again. /r restarts; /q quits.");
        }
    }
}

/// require(): Odmítá neplatný příkaz výjimkou zachycenou dodanou vstupní smyčkou.
void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
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
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
/// - `%` — Remainder (zbytek po dělení); hodí se pro opakující se pozice nebo tahy.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
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
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>

