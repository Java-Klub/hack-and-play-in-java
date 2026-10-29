/// # Vyřešený úkol · NumberMachine
/// Lekce: 5.2 Pošli metodě hodnoty — 02-parameters.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// **Tvoje mise · Napodob černou skříňku:** Malý stroj a tvoje kopie dostávají stejné číslo. Z
/// vypsaných dvojic odvoď pravidlo, přidej alespoň jedno vypovídající volání compare a uprav kopii,
/// aby souhlasila pro všechna celá čísla -20 až 20. Referenční stroj zachovej. Source (zdroják) je
/// dostupný: před čtením referenčního vzorce předpověz nový případ.

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
