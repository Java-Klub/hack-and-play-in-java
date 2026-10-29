/// # 04.04 · ClubDoor
///
/// **Najdi propustku do dílny.** Spuštění vypíše Badge: VISITOR a Door locked. Dveře
/// návštěvnickou kartu odmítají.
///
/// **Úkol:** V malém programu najdi text, který dveře přijímají. Tímto textem nahraď VISITOR na
/// své kartě. Samotné pravidlo přijímání karet zachovej: jiné karty mají dál zůstat zamčené.
///
/// **Výsledek:** Spuštění vypíše tvou změněnou kartu a Door opened. Propustka je přímo ve
/// zdrojovém kódu; nemusíš ji hádat ani zadávat za běhu.

// <editor-fold defaultstate="expanded" desc="Výklad · F03">
/// Sleduj jméno zpět k uložené hodnotě a potom dopředu k jeho použitím. main zahájí příběh; method
/// call (volání metody) položí pojmenovanou otázku. Připravené equals porovná texty a ? : vybere
/// jednu ze dvou zpráv; rozhodování prostuduješ v dalším levelu. Dnes vybíráš data a otázku
/// nepřepisuješ. String drží text v uvozovkách. `String colour = "blue";` například uloží blue pod
/// colour; jméno samo není text. Před změnou předpověz řádek karty.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · oblast hledání">
/// Zobrazená karta a přijímaná propustka jsou dvě různé datové položky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · konkrétní cesta">
/// Sleduj argument (vstup) mayEnter a potom text, s nímž se porovnává.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná úprava · prozrazuje postup">
/// workshopPass ponech ORBIT a cardCode nastav na stejný text.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Malý program · prozkoumej">
void main() {
    IO.println("CLUB DOOR");
    IO.println("Badge: " + cardCode);
    IO.println(mayEnter(cardCode) ? "Door opened. Welcome to the workshop!" : "Door locked. Find the workshop pass in this source.");
}

String cardCode = "ORBIT";

boolean mayEnter(String code) {
    return workshopPass.equals(code);
}

final String workshopPass = "ORBIT";
// </editor-fold>
