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
/// Použij Run File a porovnej dva řádky v Output: první má popisovat instrukci a druhý má být její
/// zprávou.
/// Po opravě: Save → Test File → zahraj si znovu.
String templateLine() {
    return "IO.println(HELLO);";
}

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

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Řádek v uvozovkách je data; nespouští se jako Java code (kód).
/// 2. Dvojitou uvozovku uvnitř String literal (řetězcového literálu) zapiš zpětným lomítkem a uvozovkou.
/// 3. Použij String literal (řetězcový literál) "IO.println(\"HELLO\");".
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
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

