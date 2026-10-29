/// # Úkol · Hangman
/// Lekce: 10.2 Pamatuj si první návštěvu — 02-sets.html
///
/// Odhaluj skryté slovo po písmenech, než šest chybných tipů ukončí kolo. Opakované písmeno může odkrýt
/// několik míst zároveň, takže každá nápověda může změnit tvůj další tip.
///
/// **Tvoje mise:** Uprav /hint na chytřejšího coach (poradce): doporučí první dosud skrytou samohlásku
/// ve slově zleva. Když žádná nezbývá, doporučí první jiné skryté písmeno; když je vše odhaleno, nic
/// nedoporučí. Dotaz nesmí odhalit písmeno, spotřebovat tip ani změnit kolo. Běžná hra už funguje.
///
/// Zkus /hint před prvním tipem, potom A a dvakrát /hint. Porovnej doporučení s Tried a Misses; rada
/// není odehraný tip.

String hintLetter() {
    int index = 0;
    while (index < word.length()) {
        String letter = word.substring(index, index + 1);
        if (!tried.contains(letter)) {
            return letter;
        }
        index++;
    }
    return "";
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String mask() {
    String result = "";
    int index = 0;
    while (index < word.length()) {
        String letter = word.substring(index, index + 1);
        if (tried.contains(letter)) {
            result += letter;
        } else {
            result += "_";
        }
        index++;
    }
    return result;
}

String[] words = {"LEVEL", "ROBOT", "GALAXY", "JAVA", "PIXEL", "PUZZLE", "PLANET", "BANANA"};
String word = "BANANA";
String tried = "";
int misses;

void reset() {
    word = words[new Random().nextInt(words.length)];
    tried = "";
    misses = 0;
}

boolean won() {
    return mask().equals(word);
}

boolean over() {
    return won() || misses >= 6;
}

boolean guess(String input) {
    String letter = input.toUpperCase(Locale.ROOT);
    if (over() || !letter.matches("[A-Z]") || tried.contains(letter)) {
        return false;
    }
    tried += letter;
    if (!word.contains(letter)) {
        misses++;
    }
    return true;
}

void main() {
    IO.println("Training word: BANANA. /r chooses a random word.");
    IO.println("HANGMAN: reveal the word before six wrong letters.");
    IO.println("Repeated letters cost nothing. Every letter accepts either case.");
    IO.println("/hint suggests a letter without playing it; /r chooses a new word; /q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            IO.println(mask() + "    Tried: " + tried + "    Misses: " + misses + "/6");
            if (over()) {
                IO.println((won() ? "You win!" : "Out of guesses!") + " Word: " + word);
            }
            showState = false;
        }
        String input = IO.readln(over() ? "Enter or /r: new round; /q: quit: " : "Guess A-Z, /hint advice, /r restart, /q quit: ");
        if (input == null || input.trim().equalsIgnoreCase("/q")) {
            return;
        }
        input = input.trim();
        if ((input.equalsIgnoreCase("/r") || (over() && input.isBlank()))) {
            reset();
            showState = true;
            continue;
        }
        if (input.equalsIgnoreCase("/hint")) {
            String suggestion = hintLetter();
            IO.println(suggestion.isEmpty() ? "No hidden letters remain." : "Coach suggests " + suggestion);
            continue;
        }
        if (over()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        if (guess(input)) {
            showState = true;
        } else {
            IO.println("Enter one new letter A-Z during an unfinished round.");
        }
    }
}

// </editor-fold>
