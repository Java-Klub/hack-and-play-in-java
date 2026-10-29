/// # Úkol · ClubDoor
/// Lekce: 4.1 Polož otázku kódu — 01-questions.html
///
/// **Najdi propustku do dílny.** Spuštění vypíše Badge: VISITOR a Door locked. Dveře
/// návštěvnickou kartu odmítají.
///
/// **Úkol:** V malém programu najdi text, který dveře přijímají. Tímto textem nahraď VISITOR na
/// své kartě. Samotné pravidlo přijímání karet zachovej: jiné karty mají dál zůstat zamčené.
///
/// **Výsledek:** Spuštění vypíše tvou změněnou kartu a Door opened. Propustka je přímo ve
/// zdrojovém kódu; nemusíš ji hádat ani zadávat za běhu.

// <editor-fold defaultstate="expanded" desc="Malý program · prozkoumej">
void main() {
    IO.println("CLUB DOOR");
    IO.println("Badge: " + cardCode);
    IO.println(mayEnter(cardCode) ? "Door opened. Welcome to the workshop!" : "Door locked. Find the workshop pass in this source.");
}

String cardCode = "VISITOR";

boolean mayEnter(String code) {
    return workshopPass.equals(code);
}

final String workshopPass = "ORBIT";
// </editor-fold>
