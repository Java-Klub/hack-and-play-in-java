/// # Úkol · MadLibsStoryMachine
/// Lekce: 3.3 Nech hráče měnit příběh — 03-stories.html
///
/// Dej stroji jméno, tvora, místo a předmět a přečti si absurdní příběh, který z nich sestaví. Víceslovné
/// odpovědi mohou vytvořit mnohem zvláštnější výsledek než jednotlivá slova.
///
/// **Tvoje mise · Napiš titulek příběhu:** V cleanWord odstraň okrajové mezery a zachovej víceslovné
/// fráze. Potom doplň
/// headline: pro jméno "  Red Fox  " musí vrátit přesně "Report from Red Fox:". Program tvůj titulek
/// vypíše před příběhem; jiné jméno musí dát jiný titulek.
///
/// Spusť program a na otázku na jméno napiš Red Fox s mezerami kolem, potom doplň další tři
/// fráze a prohlédni hotový příběh.

String cleanWord(String word) {
    return word;
}

String headline(String name) {
    return "Report:";
}

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Přepiš tři připravené věty příběhu tak, aby všechny čtyři zadané fráze měly význam. Vytvoř zprávu,
/// detektivní závěr nebo absurdní reklamu. Kamaráda požádej o čtyři vstupy, aniž mu předem prozradíš
/// novou šablonu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String story(String name, String creature, String place, String object) {
    name = cleanWord(name);
    creature = cleanWord(creature);
    place = cleanWord(place);
    object = cleanWord(object);
    for (String word : List.of(name, creature, place, object)) {
        if (word.isBlank() || word.length() > 40) {
            throw new IllegalArgumentException("Words need 1..40 characters.");
        }
    }
    return name + " hired a " + creature + " to guard " + place + ".\n"
        + "Unfortunately, the only security equipment was a " + object + ".\n"
        + "By midnight, the " + creature + " had promoted the " + object + " to manager.\n";
}

void main() {
    String[] labels = {"A name", "A creature", "A place", "An object"};
    IO.println("Build a strange story. Each answer may be a complete phrase; /q quits.");
    while (true) {
        String[] words = new String[4];
        for (int i = 0; i < words.length;) {
            String input = IO.readln(labels[i] + " (/q to quit): ");
            if (input == null || input.equalsIgnoreCase("/q")) return;
            String cleaned = cleanWord(input);
            if (cleaned.isBlank() || cleaned.length() > 40) {
                IO.println("Use a nonblank phrase of at most 40 characters. This answer has not been accepted.");
                continue;
            }
            words[i++] = input;
        }
        IO.println(headline(words[0]));
        IO.print(story(words[0], words[1], words[2], words[3]));
        while (true) {
            String input = IO.readln("Enter or /r: new story; /q: quit: ");
            if (input == null || input.strip().equalsIgnoreCase("/q")) return;
            if (input.isBlank() || input.strip().equalsIgnoreCase("/r")) break;
            IO.println("The story is finished. Press Enter or /r for a new story; /q quits.");
        }
    }
}

// </editor-fold>
