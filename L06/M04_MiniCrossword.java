import module java.base;
class M04_MiniCrossword {
    /// # L06.04 · MiniCrossword
    ///
    /// Vyřeš malou křížovku postupně po nápovědách a klidně odpověď smaž, když ji chceš přehodnotit. Oslava
    /// má počkat na všechny odpovědi.
    ///
    /// **Tvoje mise:** Počítadlo přičítá staré výsledky k novým a může slavit už po třech odpovědích. Oprav
    /// nové spočítání uvnitř command().
    ///
    /// **Dnes trénuješ:** Scope and state lifetime (platnost jména a životnost stavu): součet z jednoho
    /// průchodu potřebuje pro každý průchod nový začátek. Rozliš dočasný součet od vyřešených nápověd
    /// uložených mezi příkazy.
    ///
    /// Z hlavní složky obsahující README.md:
    /// - Hraní: `java L06/M04_MiniCrossword.java`
    /// - Kontrola této mise: `java L06/Check.java M04_MiniCrossword.java`
    ///
    /// Spusť command (příkaz) pro hraní uvedený výše, jako první odpověď napiš 1 CAT a potvrď Enter,
    /// potom si přečti zbývající očíslované nápovědy. Po opravě: ulož soubor, spusť uvedenou kontrolu a
    /// zahraj si znovu.
    ///
    /// Potřebuješ výklad? Připomenutí · F14 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
    /// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
    void command(String input) {
        require(outcome.isEmpty());
        String[] p = input.toUpperCase(Locale.ROOT).split("\\s+");
        require(p.length == 2);
        boolean erase = p[0].equals("ERASE");
        int n = Integer.parseInt(erase ? p[1] : p[0]) - 1;
        require(n >= 0 && n < 6);
        if (erase) {
            solved[n] = false;
        } else {
            require(p[1].equals(answers[n]));
            solved[n] = true;
        }
        for (boolean value : solved) {
            if (value) {
                count++;
            }
        }
        if (count == 6) {
            outcome = "All clues solved!";
        }
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
    /// - `boolean erase` — Dočasná hodnota ano/ne, která popisuje jen tento příkaz.
    /// - `solved[n] = true` — Uloží stav vyřešené nápovědy, aby jej viděly i pozdější příkazy.
    /// - `for (boolean value : solved)` — Prochází právě uložené stavy místo historie příkazů.
    /// - `count++` — Připočítá jednu vyřešenou nápovědu do součtu tohoto průchodu.
    ///
    /// ### Co už obstarává připravená část
    ///
    /// Začátek command() už rozpozná odpověď nebo mazání a aktualizuje solved. grid() promítá uložené
    /// odpovědi do zobrazené křížovky; tvůj součet rozhoduje o závěrečném oznámení outcome.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
    /// solved[] je trvalý stav; count je jen dočasný souhrn.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
    /// Po jedné, dvou a třech odpovědích dosáhne starý akumulátor 1+2+3.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
    /// Uvnitř command() těsně před průchodem deklaruj int count = 0 a odstraň field.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
    int count;

    String[] answers = {"CAT", "ARE", "TEN", "CAT", "ARE", "TEN"};
    String[] clues = {"Across row1: pet that meows", "Across row2: plural of is", "Across row3: number after nine", "Down col1: pet that meows", "Down col2: plural of is", "Down col3: number after nine"};
    boolean[] solved;
    String outcome;

    /// reset(): Obnovuje počáteční kolo Mini Crossword a maže stav patřící starému kolu.
    void reset() {
        solved = new boolean[6];
        outcome = "";
    }

    /// grid(): Promítá vyřešená vodorovná/svislá slova do nové zobrazovací mřížky tři krát tři.
    char[][] grid() {
        char[][] b = new char[3][3];
        for (char[] row : b) {
            Arrays.fill(row, '.');
        }
        for (int n = 0; n < 6; n++) {
            if (solved[n]) {
                for (int i = 0; i < 3; i++) {
                    b[n < 3 ? n : i][n < 3 ? i : n - 3] = answers[n].charAt(i);
                }
            }
        }
        return b;
    }

    /// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
    void display() {
        for (char[] row : grid()) {
            IO.println(new String(row));
        }
        for (int i = 0; i < 6; i++) {
            IO.println((i + 1) + " " + clues[i] + (solved[i] ? " [done]" : ""));
        }
    }

    /// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
    /// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
    /// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
    void main() {
        reset();
        IO.println("Mini Crossword");
        IO.println("Solve six clues crossing in a tiny three-by-three crossword. A correct answer reveals its shared letters.");
        IO.println("Enter clueNumber answer, or erase clueNumber. A tiny authored 3x3 crossword. /r restarts; /q quits.");
        IO.println("Example: 1 CAT answers clue 1. erase 1 clears that answer so you can inspect the crossing again.");
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
                IO.println("Invalid move. " + "Enter clueNumber answer, or erase clueNumber. A tiny authored 3x3 crossword. /r restarts; /q quits.");
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
    /// - `'` — Jednoduché uvozovky ohraničují jeden character (znak), například značku na desce.
    /// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
    /// - `char` — Type (typ) pro jednu UTF-16 jednotku, často jednoduchý znak desky jako X. Pro text slouží
    ///   String.
    /// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
    /// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
    /// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
    /// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
    /// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
    /// - `++` — Zvětší číselnou hodnotu o jedna.
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
    /// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
    /// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
    /// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
    /// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
    /// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
    /// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
    /// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
    ///   řádek vstupu.
    // </editor-fold>

}
