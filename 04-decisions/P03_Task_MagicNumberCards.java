/// # Task · MagicNumberCards
/// Lesson: 4.1 Ask a question in code — 01-questions.html
///
/// Think of a number between 0 and 63 and tell the program which of six number cards contains it. The
/// machine combines your yes/no answers to reveal your chosen number.
///
/// **Your mission · Repair a mind-reading trick:** The trick treats typed yes answers as no. Repair
/// yesAnswer so both y and Y are
/// recognized by their text, while n stays false.
///
/// Run the program, think of 5 and answer the six cards with y, n, y, n, n, n to compare the
/// reported number before and after your repair.

boolean yesAnswer(String input) {
    return input == "y";
}

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Perform the trick for a friend who chooses the number privately. After it works, deliberately lie
/// on exactly one card and compare the error with that card’s first number. Can your friend explain
/// the changed result from the card rather than guessing magic?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
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
