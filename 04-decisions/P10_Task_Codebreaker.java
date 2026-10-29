/// # Solved task · Codebreaker
/// Lesson: 4.3 Combine rules — 03-logic.html
///
/// Original assignment (this version is complete):
///
/// **Your mission · Install a cheat code:** Add a universal service code: 1111 wins against every
/// hidden code. Ordinary correct guesses must still win and other wrong guesses must still fail. Do
/// not replace the hidden code, bypass four-digit validation or change the clues. The cheat must
/// survive restart.
///
/// The starter works by its original rules. The check already expects your new goal.
///
/// The known opening is 1234. Enter r for a new hidden code, then try 1111. This must be a real
/// accepted winning guess, not just a printed victory message.

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Invent another service code and explain why it still has to obey the accepted input alphabet. Give
/// a friend only the ordinary game rules and let them inspect your modified source for the backdoor.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Program · explore as needed">

String secret = "1234";
int attempts;
boolean won;
int[] feedback = {0, 0};

String newCode() {
    Random random = new Random();
    String code = "";
    for (int i = 0; i < 4; i++) {
        code += random.nextInt(1, 7);
    }
    return code;
}

void reset() {
    secret = newCode();
    attempts = 0;
    won = false;
    feedback = new int[] {0, 0};
}

boolean over() {
    return won || attempts >= 10;
}

int[] score(String guess) {
    boolean[] usedSecret = new boolean[4];
    boolean[] usedGuess = new boolean[4];
    int exact = 0;
    int elsewhere = 0;
    for (int i = 0; i < 4; i++) {
        if (secret.charAt(i) == guess.charAt(i)) {
            exact++;
            usedSecret[i] = true;
            usedGuess[i] = true;
        }
    }
    for (int i = 0; i < 4; i++) {
        if (usedGuess[i]) {
            continue;
        }
        for (int j = 0; j < 4; j++) {
            if (!usedSecret[j] && guess.charAt(i) == secret.charAt(j)) {
                elsewhere++;
                usedSecret[j] = true;
                break;
            }
        }
    }
    return new int[] {exact, elsewhere};
}

boolean matchesCode(String guess) {
    return secret.equals(guess) || "1111".equals(guess);
}

boolean submit(String guess) {
    if (over() || !guess.matches("[1-6]{4}")) {
        return false;
    }
    feedback = score(guess);
    attempts++;
    won = matchesCode(guess);
    return true;
}

void main() {
    IO.println("Training opening: code 1234. Restart deals a random code.");
    IO.println("CODEBREAKER: guess four digits 1-6 in ten attempts. Repeats are allowed.");
    IO.println("Exact = right digit and position; elsewhere = right digit, different position.");
    boolean showState = true;
    while (true) {
        if (showState) {
            if (over()) {
                IO.println((won ? "You win!" : "Out of attempts!") + " Code: " + secret);
            }
            showState = false;
        }
        String input = IO.readln(over() ? "Enter or /r: new round; /q: quit: " : "Attempt " + (attempts + 1) + "/10, r restart, q quit: ");
        if (input == null || (input.trim().equalsIgnoreCase("q") || input.trim().equalsIgnoreCase("/q"))) {
            return;
        }
        input = input.trim();
        if ((input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r") || (over() && input.isBlank()))) {
            reset();
            showState = true;
            continue;
        }
        if (over()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        if (submit(input)) {
            showState = true;
            IO.println(input + " -> exact " + feedback[0] + ", elsewhere " + feedback[1]);
        } else {
            IO.println("Enter exactly four digits 1-6 during an unfinished round.");
        }
    }
}
// </editor-fold>
