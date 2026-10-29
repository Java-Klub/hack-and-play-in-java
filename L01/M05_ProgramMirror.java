import module java.base;
class M05_ProgramMirror {
    /// # L01.05 · ProgramMirror
    ///
    /// Malé programové zrcadlo ukáže Java instrukci jako text a potom zprávu, kterou by tato instrukce
    /// vypsala. Porovnej instrukci s výsledkem a sleduj, jak může program popisovat jiný program.
    ///
    /// **Tvoje mise:** Odražená instrukce ztratila uvozovky kolem HELLO. Oprav templateLine tak, aby je
    /// vypsaná instrukce obsahovala a další output (výstup) zůstal HELLO.
    ///
    /// **Dnes trénuješ:** Uvozovaný text a escaping (zápis speciálních znaků): text připomínající code
    /// (program) uvnitř String (řetězce) je stále jen data. Uvozovka patřící dovnitř potřebuje zvláštní
    /// zápis, aby ji Java nepovažovala za konec řetězce.
    ///
    /// Z hlavní složky obsahující README.md:
    /// - Hraní: `java L01/M05_ProgramMirror.java`
    /// - Kontrola této mise: `java L01/Check.java M05_ProgramMirror.java`
    ///
    /// Spusť command (příkaz) pro hraní uvedený výše a porovnej dva řádky v výstup v terminálu: první
    /// má popisovat instrukci a druhý má být její zprávou. Po opravě: ulož soubor, spusť uvedenou
    /// kontrolu a zahraj si znovu.
    ///
    /// Potřebuješ výklad? Připomenutí · F02 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
    /// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
    String templateLine() {
        return "IO.println(HELLO);";
    }

    // <editor-fold defaultstate="collapsed" desc="Připomenutí · F02">
    /// String literal (doslovně zapsaný řetězec) je text mezi dvojitými uvozovkami. Vnější uvozovky
    /// říkají Javě, kde text začíná a končí; nevypisují se. I číslice v uvozovkách jsou text. Expression
    /// (výraz) naopak může před vypsáním vypočítat hodnotu.
    ///
    /// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
    ///
    /// ```java
    /// IO.println("Shelf 4");
    /// IO.println(4 + 2);
    /// IO.println("Shelf \"B\"");
    /// ```
    ///
    /// Předpověz tři řádky. Jsou to `Shelf 4`, `6` a `Shelf "B"`. V posledním literal představuje každé
    /// `\"` jednu uvozovku patřící do textu. Zpětné lomítko je zápis, nikoli další vypsaný znak.
    /// `IO.println` vypíše svůj argument (předanou hodnotu) a ukončí řádek. Mezery uvnitř literal
    /// zůstávají skutečnými mezerami; změna comment (komentářů) vypsanou zprávu nezmění. Rozlišuj tedy
    /// znaky zapsané ve source (zdrojovém textu) a znaky, které se opravdu objeví v output (výstupu).
    ///
    /// Nyní předpověz `IO.println(4 + 5);` a `IO.println("4 + 5");`.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
    /// ## Jak číst právě tyto řádky
    ///
    /// - `String` — Vrácená hodnota je text, i když vypadá jako Java instrukce.
    /// - `templateLine()` — Tato akce bez input (vstupu) poskytuje zapsanou podobu instrukce.
    /// - `return` — Předej tento text do main, která jej vypíše.
    /// - `"IO.println(HELLO);"` — Vnější uvozovky uzavírají celou textovou hodnotu; znaky uvnitř se zde
    ///   neprovádějí.
    /// - `IO.println(HELLO);` — Uvnitř String (řetězce) jsou tečka, závorky a středník tisknutelné znaky
    ///   popisující jinou instrukci.
    /// - `;` — Středník za zavírací uvozovkou ukončuje skutečný return statement (příkaz vrácení); liší se od
    ///   tištěného středníku uvnitř textu.
    ///
    /// ### Co už obstarává připravená část
    ///
    /// main vypíše vrácenou šablonu a potom samostatnou zprávu HELLO. Úloha zrcadlí jednu instrukci;
    /// nevypisuje svůj celý source (zdrojový text). Obě skutečné tiskové akce jsou připravené.
    // </editor-fold>

    // <editor-fold defaultstate="expanded" desc="Pro tuto hru">
    /// Odděl tři vrstvy: Java source (zdrojový text), který upravuješ, text vrácený templateLine a dva
    /// řádky vypsané v výstup v terminálu. První vypsaný řádek musí obsahovat uvozovky kolem HELLO;
    /// druhý je samostatná zpráva. Znaky prvního řádku jsou data, nikoli další prováděný příkaz.
    /// Neutrální příklad: source text `"A \"B\" C"` představuje hodnotu `A "B" C`: každé `\"` přidá
    /// jednu uvozovku bez ukončení vnějšího literal (doslovného řetězce). Než změníš vlastní literal,
    /// předpověz tuto hodnotu. Rozlišuj poslední středník skutečného return statement (příkazu vrácení)
    /// a středník vypsaný uvnitř textu.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
    /// Řádek v uvozovkách je data; nespouští se jako Java code (kód).
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
    /// Dvojitou uvozovku uvnitř String literal (řetězcového literálu) zapiš zpětným lomítkem a uvozovkou.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
    /// Použij String literal (řetězcový literál) "IO.println(\"HELLO\");".
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
    void main() {
        IO.println(templateLine());
        IO.println("HELLO");
    }

    /// Připravené vstupní body: main.
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
    /// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
    /// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
    ///   textu nepatří.
    /// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
    /// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek.
    // </editor-fold>

}
