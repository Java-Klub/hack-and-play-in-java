/// # Solved task · Hangman
/// Lesson: 10.2 Remember a first visit — 02-sets.html
///
/// Original assignment (this version is complete):
///
/// Reveal a hidden word one letter at a time before six wrong guesses end the round. Repeated letters can
/// uncover several places at once, so each clue can change your next guess.
///
/// **Your mission:** Make /hint suggest the first still-hidden vowel in the word, reading left to
/// right. If no hidden vowel remains, suggest the first other hidden letter; if nothing is hidden, say
/// so. Asking must not reveal a letter, spend a guess, or change the round. The ordinary game already
/// works.
///
/// Try /hint before any guess, then A, then /hint twice. Compare the suggestion with Tried and Misses
/// rather than treating advice as a played guess.

String hintLetter() {
    String fallback = "";
    int index = 0;
    while (index < word.length()) {
        String letter = word.substring(index, index + 1);
        if (!tried.contains(letter)) {
            if ("AEIOU".contains(letter)) {
                return letter;
            }
            if (fallback.isEmpty()) {
                fallback = letter;
            }
        }
        index++;
    }
    return fallback;
}

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
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
