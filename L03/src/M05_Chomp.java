/// # L03.05 · Chomp
///
/// Dva hráči okusují jednu tabulku čokolády. Každé sousto odebere obdélník směrem vpravo dolů, ale
/// snědení jedovatého levého horního dílku prohrává hru.
///
/// **Tvoje mise:** Viditelný čtvrtý řádek nelze ukousnout. Oprav range guard (ochranu rozsahu) v bite a
/// dál odmítej čokoládu mimo tabulku i už snědené dílky.
///
/// **Dnes trénuješ:** Hranice před čtením: guard (ochrana) musí přijmout hranici patřící do hry a
/// odmítnout následující hodnotu. Zároveň chrání pozdější čtení řádku před použitím neexistujícího řádku.
///
/// Použij Run File a zadáním 4 5 ukousni viditelný pravý dolní dílek, potom zkus stejné souřadnice znovu,
/// když už tam není.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F08 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
boolean bite(int row, int column) {
    if (winner != 0 || row < 1 || row >= 4 || column < 1 || column > widths[row - 1]) {
        return false;
    }
    for (int i = row - 1; i < widths.length; i++) {
        widths[i] = Math.min(widths[i], column - 1);
    }
    if (row == 1 && column == 1) {
        winner = 3 - player;
    } else {
        player = 3 - player;
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F08">
/// Guard (ochranná podmínka) odmítne požadavek před chráněnou prací. Nejdřív přečti odmítané případy
/// a potom se ptej, co zůstává povolené. Short-circuit (zkrácené vyhodnocení) je důležité: u `A || B`
/// Java přeskočí B, pokud A už platí.
///
/// ```java
/// boolean useSlot(int slot) {
///     if (slot < 0 || slot >= 4) {
///         return false;
///     }
///     IO.println("Using slot " + slot);
///     return true;
/// }
/// ```
///
/// Předpověz výpis a vrácenou hodnotu pro `-1`, `3` a `4`. Jen trojka dojde k výpisu. Odmítnutí musí
/// nastat před jakoukoli state change (změnou stavu), která se nemá provést. Přístup na desku sám
/// může potřebovat kontrolu rozsahu, než jej lze bezpečně vyhodnotit. `return false` ukončí toto call
/// (volání metody); automaticky nevrací zpět práci již provedenou předtím.
///
/// `useSlot(3)` předá trojku do `slot`; `boolean` před názvem říká, že výsledkem je true/false. Nyní
/// předpověz slot nula.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `row >= 4` — Současná horní kontrola řádků zahrnuje do odmítnutí i poslední skutečný řádek.
/// - `||` — Kterákoli špatná condition (podmínka) odmítne sousto; dřívější hranice mohou zastavit
///   vyhodnocení před čtením řádku.
/// - `widths[row - 1]` — Připravená tabulka šířek říká, kolik dílků v řádku zbývá; odečtení jedničky
///   převádí lidské číslo řádku.
/// - `column > widths[row - 1]` — Odmítni sloupec za zbývající čokoládou tohoto konkrétního řádku.
/// - `return false;` — Odejdi před odebráním čokolády nebo změnou hráče.
/// - `return true;` — Oznam přijaté sousto po provedení připraveného odebírání a pravidla jedu.
///
/// ### Co už obstarává připravená část
///
/// Připravený loop (cyklus) zkrátí všechny dotčené řádky po průchodu ochranou. Zbývající branch (větev)
/// řeší jed a střídání, zatímco main kreslí nerovnou tabulku a čte dvojice řádek/sloupec. Opravuješ pouze
/// vstupní hranici.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Řádek čtyři do tabulky patří.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Bounds guard (ochranu hranic) nemaž: chrání i widths[row - 1].
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Odmítnutí řádku je row < 1 || row > 4.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[] widths = {5, 5, 5, 5};
int player = 1;
int winner;

void reset() {
    Arrays.fill(widths, 5);
    player = 1;
    winner = 0;
}

void main() {
    IO.println("CHOMP: two players share a chocolate bar.");
    IO.println("A bite removes its cell and the rectangle below and right.");
    IO.println("P at row 1, column 1 is poisoned. Eating P loses.");
    boolean showState = true;
    while (true) {
        if (showState) {
            IO.println("    1 2 3 4 5");
            for (int row = 0; row < 4; row++) {
                String line = (row + 1) + " : ";
                for (int column = 0; column < 5; column++) {
                    line += column >= widths[row] ? ". " : row == 0 && column == 0 ? "P " : "# ";
                }
                IO.println(line);
            }
            if (winner != 0) {
                IO.println("Player " + winner + " wins!");
            }
            showState = false;
        }
        String input = IO.readln(winner == 0 ? "Player " + player + ": row column, r restart, q quit: " : "Enter or /r: new round; /q: quit: ");
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
            String[] parts = input.split("\\s+");
            if (parts.length == 2 && bite(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]))) {
                showState = true;
            } else {
                IO.println("Illegal bite. Enter row and column of remaining chocolate.");
            }
        } catch (NumberFormatException exception) {
            IO.println("Enter two numbers, r or q.");
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
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
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
/// - `?` — V condition ? first : second vybere první hodnotu, když condition (podmínka) platí, jinak
///   druhou.
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>

