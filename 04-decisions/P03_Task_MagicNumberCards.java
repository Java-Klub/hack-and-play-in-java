/// # Úkol · MagicNumberCards
/// Lekce: 4.1 Polož otázku kódu — 01-questions.html
///
/// Mysli si číslo od 0 do 63 a řekni programu, na kterých ze šesti číselných karet se vyskytuje. Stroj
/// spojí tvoje odpovědi ano/ne a odhalí zvolené číslo.
///
/// **Tvoje mise · Oprav čtení myšlenek:** Kouzlo považuje napsané odpovědi ano za ne. Oprav yesAnswer
/// tak, aby y i Y poznalo
/// podle textu a n zůstalo false.
///
/// Spusť program, mysli si 5 a na šest karet odpověz y, n, y, n, n, n, abys porovnal oznámené
/// číslo před opravou a po ní.

boolean yesAnswer(String input) {
    return input == "y";
}

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Předveď trik kamarádovi, který si číslo zvolí tajně. Po úspěchu záměrně zalži právě u jedné karty
/// a porovnej chybu s jejím prvním číslem. Dokáže kamarád změnu vysvětlit pomocí karty místo kouzel?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
List<Integer> card(int bit) {
    if (bit < 0 || bit > 5) {
        throw new IllegalArgumentException("Card index: 0..5.");
    }
    var values = new ArrayList<Integer>();
    for (int value = 0; value < 64; value++) {
        if ((value & (1 << bit)) != 0) {
            values.add(value);
        }
    }
    return values;
}

int reveal(boolean[] answers) {
    if (answers.length != 6) {
        throw new IllegalArgumentException("Six answers required.");
    }
    int result = 0;
    for (int bit = 0; bit < 6; bit++) {
        if (answers[bit]) {
            result += 1 << bit;
        }
    }
    return result;
}

void main() {
    IO.println("Think of one number from 0 to 63. Keep that same number for all six cards.");
    IO.println("For each card, type y if it contains your number, otherwise n. Enter sends an answer; /r starts over; /q quits.");
    boolean[] answers = new boolean[6];
    int bit = 0;
    boolean showCard = true;
    while (true) {
        if (showCard) {
            IO.println("Card " + (bit + 1) + "/6:");
            IO.println(card(bit));
            showCard = false;
        }
        String input = IO.readln(bit < 6 ? "Is your number on this card? y/n, /r, /q: " : "Enter or /r: new number; /q: quit: ");
        if (input == null) return;
        input = input.strip();
        if (input.equalsIgnoreCase("/q") || input.equalsIgnoreCase("q")) return;
        if (input.equalsIgnoreCase("/r") || input.equalsIgnoreCase("r") || (bit == 6 && input.isEmpty())) {
            Arrays.fill(answers, false); bit = 0; showCard = true;
            IO.println("Choose a new number from 0 to 63 and keep it for all six cards.");
            continue;
        }
        if (bit == 6) {
            IO.println("The number was revealed. Press Enter or /r for another number; /q quits.");
            continue;
        }
        if (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n")) {
            IO.println("Please type y for present or n for absent, then press Enter. The card above is still current.");
            continue;
        }
        answers[bit] = yesAnswer(input);
        bit++;
        if (bit == 6) IO.println("Your number is " + reveal(answers) + ".");
        else showCard = true;
    }
}

// </editor-fold>
