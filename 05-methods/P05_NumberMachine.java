/// # 05.05 · NumberMachine
///
/// **Tvoje mise · Napodob černou skříňku:** Malý stroj a tvoje kopie dostávají stejné číslo. Z
/// vypsaných dvojic odvoď pravidlo, přidej alespoň jedno vypovídající volání compare a uprav kopii,
/// aby souhlasila pro všechna celá čísla -20 až 20. Referenční stroj zachovej. Source (zdroják) je
/// dostupný: před čtením referenčního vzorce předpověz nový případ.

// <editor-fold defaultstate="expanded" desc="Výklad · F04">
/// Několik příkladů může odpovídat různým pravidlům. Vyber nový input (vstup), který tvé domněnky
/// rozliší. Záporná čísla a nula jsou užitečné důkazy. Každé volání compare předá jeden argument
/// dvěma method (metodám) a vypíše obě return values (vrácené hodnoty). Parameter number je místní
/// jméno tohoto volání. Násobení používá *, nikoli matematický horní index; aritmetika proběhne před
/// vrácením výsledku. Neutrální `return amount * 3 - 1;` dává pro amount 2 výsledek 5. Tento ukázkový
/// vzorec do stroje neopisuj.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · oblast hledání">
/// Porovnej výsledky pro 2 a -2. Sedí na oba pouhé zdvojnásobení vstupu?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · konkrétní cesta">
/// imitation musí počítat ze svého parameter (parametru). Pevný seznam vypsaných odpovědí neobslouží
/// další volání.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná úprava · prozrazuje postup">
/// Reference vstup umocní na druhou a přičte jedna. Pravidlo použij v imitation a ověř další vstup.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Malý program · prozkoumej">
void main() {
    IO.println("NUMBER MACHINE: input -> machine / your copy");
    compare(0);
    compare(1);
    compare(2);
    compare(-2);
    compare(5);
}

void compare(int number) {
    IO.println(number + " -> " + machine(number) + " / " + imitation(number));
}

int imitation(int number) {
    return number * number + 1;
}


// <editor-fold defaultstate="collapsed" desc="Referenční stroj · až po předpovědi">
int machine(int number) {
    return number * number + 1;
}
// </editor-fold>
// </editor-fold>
