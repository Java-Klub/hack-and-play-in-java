/// # Úkol · Jotto
/// Lekce: 7.2 Projdi hodnoty bez ručního výběru — 02-traversal.html
///
/// Najdi tajné pětipísmenné slovo podle nápověd, které prozradí jen počet společných písmen. Pozice se
/// neodhalují, takže další tip může zkoušet jinou kombinaci místo pouhého přeskupení stejných písmen.
///
/// **Tvoje mise:** Nápověda nikdy nepřesáhne jedničku, i když se shoduje několik písmen. Oprav matches
/// tak, aby průběžně počítala všechna společná písmena místo zapomínání dřívějších shod.
///
/// Napiš STONE proti cvičnému slovu PLANT; T a N jsou společná, ale starter
/// (výchozí program) hlásí jen jedno.

int matches(String word) {
    int n = 0;
    for (char c : word.toCharArray()) {
        if (secret.indexOf(c) >= 0) {
            n = 1;
        }
    }
    return n;
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String[] words = {"PLANT", "TIGER", "BRICK", "GHOST", "MONEY", "WATER", "CHAIR", "CLOUD", "SPEAR", "STONE"};
String secret;
String outcome;
int attempts;
List<String> history;

void reset() {
    secret = words[new Random().nextInt(words.length)];
    attempts = 0;
    history = new ArrayList<>();
    outcome = "";
}

void command(String input) {
    require(outcome.isEmpty());
    String word = input.toUpperCase(Locale.ROOT);
    require(List.of(words).contains(word));
    attempts++;
    history.add(word + " -> " + matches(word) + " shared letters");
    if (word.equals(secret)) {
        outcome = "You win";
    } else {
        if (attempts == 10) {
            outcome = "Out of tries. Word: " + secret;
        }
    }
}

void display() {
    IO.println("Allowed words: " + String.join(" ", words));
    IO.println("Attempts " + attempts + "/10. No positions are revealed.");
    for (String row : history) {
        IO.println(row);
    }
}

void main() {
    reset();
    secret = "PLANT";
    IO.println("Training word: PLANT. /r chooses a random word.");
    IO.println("Jotto");
    IO.println("Guess a listed five-letter word with distinct letters. Ten turns. /r restarts; /q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
            showState = false;
        }
        String input = IO.readln(outcome.isEmpty() ? "> " : "Enter or /r: new round; /q: quit: ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r") || (!outcome.isEmpty() && input.isBlank())) {
            reset();
            showState = true;
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            command(input.strip());
            showState = true;
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move or command. Guess a listed five-letter word with distinct letters. Ten turns. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

// </editor-fold>
