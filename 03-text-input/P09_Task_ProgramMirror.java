/// # Úkol · ProgramMirror
/// Lekce: 3.3 Nech hráče měnit příběh — 03-stories.html
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
/// program nespouští. Uvozovku uvnitř textu vysvětluje příklad v 03-stories.html.

String templateLine() {
    return "IO.println(HELLO);";
}

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Vymysli krátký nový pozdrav. Změň zobrazený příkaz i samostatně vypsanou zprávu tak, aby si
/// odpovídaly. Zkus pozdrav obsahující uvozovku a vysvětli, která zpětná lomítka patří pouze do Java
/// source (zdrojového kódu).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
void main() {
    IO.println(templateLine());
    IO.println("HELLO");
}

// </editor-fold>
