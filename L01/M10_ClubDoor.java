/// # L01.10 · ClubDoor
///
/// **Tvoje mise · Najdi schovanou propustku:** Otevři dveře dílny. Fungující program kontroluje
/// kartu, ale tvoji návštěvní kartu odmítá. Přečti malý source (zdroják), najdi přijímanou propustku
/// a dej ji na svou kartu. Běžné ověřování dveří zachovej: jiné karty musí dál odmítat. Spuštění
/// vypíše Badge a Door opened/locked.

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

String cardCode = "VISITOR";

boolean mayEnter(String code) {
    return workshopPass.equals(code);
}

final String workshopPass = "ORBIT";
// </editor-fold>
