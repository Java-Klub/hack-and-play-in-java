/// # 03.09 · ProgramMirror
///
/// **Vypiš kód jako text.** Tohle je obtížnější odbočka s uvozovkami. Program vypíše ukázku Java
/// kódu a pod ní pozdrav.
///
/// **Úkol:** Uprav text vracený z `templateLine` níže tak, aby první vypsaný řádek obsahoval
/// uvozovky kolem HELLO. Výstup má vypadat přesně takto:
///
/// ```text
/// IO.println("HELLO");
/// HELLO
/// ```
///
/// Druhý řádek už funguje; zachovej ho. První řádek se pouze vypisuje jako text, jeho obsah tento
/// program nespouští. Nový zápis pro uvozovku uvnitř textu vysvětluje sekce pod tvým kódem.

String templateLine() {
    return "IO.println(HELLO);";
}

// <editor-fold defaultstate="expanded" desc="Uvozovka uvnitř textu · F02">
/// Vnější `"` označuje konec textu, proto uvozovka patřící dovnitř potřebuje zápis `\"`. Java z
/// těchto dvou zapsaných znaků vytvoří jednu textovou uvozovku.
///
/// ```java
/// return "The \"Owl\" club";
/// ```
///
/// Tento příklad vrací text `The "Owl" club`. Vnější uvozovky ani zpětná lomítka do výsledku
/// nepatří. V této úloze potřebuješ stejným způsobem dostat do textu dvě uvozovky kolem slova
/// HELLO.
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

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Vymysli krátký nový pozdrav. Změň zobrazený příkaz i samostatně vypsanou zprávu tak, aby si
/// odpovídaly. Zkus pozdrav obsahující uvozovku a vysvětli, která zpětná lomítka patří pouze do Java
/// source (zdrojového kódu).
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
