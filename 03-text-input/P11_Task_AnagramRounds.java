/// # Úkol · AnagramRounds
/// Lekce: 3.3 Nech hráče měnit příběh — 03-stories.html
///
/// Rozlušti pět přesmyček a zjisti, kolik jich vyřešíš na nejvýše tři odhady každé. Jedno kolo může
/// přijímat více skutečných přesmyček, takže správné může být i nečekané slovo.
///
/// **Tvoje mise · Zpřesni rozhodčího:** Platná odpověď jako silent je odmítnuta kvůli jiné velikosti
/// písmen než ve slovníku.
/// Oprav normalize a zachovej současné odstranění okrajových mezer.
///
/// Spusť program a v prvním kole napiš silent; po opravě vyzkoušej i směs velkých a malých
/// písmen a sleduj, zda je posouzena stejně.

String normalize(String input) {
    return input.strip();
}

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// Chain (řetězení volání) používá vrácenou hodnotu každé operace jako receiver (příjemce) další
/// operace. Například `"  Hi  ".strip().length()` nejprve vytvoří `Hi`, potom jeho délku 2. Nyní
/// předpověz stejný chain pro `"  Hello  "`. Tvůj výpočet má stále vracet text, nikoli jeho délku.
/// Dostupná operace převodu na velká písmena je `toUpperCase(Locale.ROOT)`; `Locale.ROOT` je
/// připravená constant (konstanta) předávaná jako argument (argument), nikoli další call (volání
/// metody). Udrží porovnání se slovníkem stejné při různých místních nastaveních. Při přidávání
/// převodu velikosti písmen zachovej současné čištění okrajů.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Nahraď jednu existující skupinu slov novou skupinou skutečných přesmyček pro kamaráda. Zachovej
/// strukturu array (pole) a velká písmena. Vyhni se neznámým slovům; ověř, že zamíchané zadání a
/// přijímané odpovědi mají stejné znaky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String[][] answers = {{"LISTEN", "SILENT", "ENLIST", "TINSEL"}, {"STONE", "TONES", "NOTES"}, {"EARTH", "HEART", "HATER"}, {"ANGEL", "GLEAN", "ANGLE"}, {"NIGHT", "THING"}};
int round;
int tries;
int score;
String scrambled;
String outcome;
String message;
String[] clues = {"Use your ears", "A small rock", "Our planet", "A winged figure in stories", "The time after sunset"};

void scramble() {
    var letters = new ArrayList<Character>();
    for (char c : answers[round][0].toCharArray()) {
        letters.add(c);
    }
    Collections.shuffle(letters);
    scrambled = "";
    for (char c : letters) {
        scrambled += c;
    }
}

void reset() {
    round = tries = score = 0;
    outcome = "";
    message = "Rearrange every shown letter into an English word. Example: TAC becomes CAT.";
    scramble();
}

void advance() {
    round++;
    tries = 0;
    if (round == 5) {
        outcome = "Finished: " + score + "/5";
    } else {
        scramble();
    }
}

void command(String input) {
    require(outcome.isEmpty());
    String word = normalize(input);
    if (word.equals("SKIP")) {
        message = "Skipped. One accepted answer was " + answers[round][0] + ".";
        advance();
        return;
    }
    if (!word.matches("[A-Z]+")) {
        throw new IllegalArgumentException("Use letters A-Z after cleanup. If a lowercase English answer is rejected, that is the normalize mission's fault; uppercase input still lets you play.");
    }
    if (List.of(answers[round]).contains(word)) {
        message = "Accepted: " + word + ". One point!";
        score++;
        advance();
    } else {
        tries++;
        message = word.length() != answers[round][0].length()
            ? "Use all " + answers[round][0].length() + " letters exactly once; " + word + " has " + word.length() + "."
            : "That is not an accepted rearrangement. The word list is small; /words shows the accepted options.";
        message += " Guess used: " + tries + "/3.";
        if (tries == 3) {
            message += " One accepted answer was " + answers[round][0] + ".";
            advance();
        }
    }
}

void display() {
    IO.println(message);
    if (round < 5) {
        IO.println("Round " + (round + 1) + "/5: " + scrambled + "; guesses used " + tries + "/3; score " + score);
        IO.println("Clue for one possible answer: " + clues[round] + ". Other accepted anagrams can count too.");
    }
}

void main() {
    reset();
    IO.println("Anagram Rounds");
    IO.println("Use every shown letter once to make one English word. Example: TAC becomes CAT. Three guesses per word, five rounds. SKIP reveals an answer; /words lists accepted words without a guess; /r restarts; /q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
            showState = false;
        }
        String input = IO.readln(outcome.isEmpty() ? "Your word, SKIP or /words: " : "Enter or /r: new round; /q: quit: ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r") || (!outcome.isEmpty() && input.isBlank())) {
            reset();
            showState = true;
            continue;
        }
        if (input.strip().equalsIgnoreCase("/words") && outcome.isEmpty()) {
            IO.println("Accepted words: " + String.join(", ", answers[round]));
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        if (input.isBlank()) {
            IO.println("Type a word using the shown letters, SKIP for the next puzzle, or /words for choices. No guess spent.");
            continue;
        }
        try {
            command(input.strip());
            showState = true;
        } catch (IllegalArgumentException invalid) {
            IO.println(invalid.getMessage());
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

// </editor-fold>
