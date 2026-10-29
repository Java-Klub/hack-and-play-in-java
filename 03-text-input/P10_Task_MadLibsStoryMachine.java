/// # Solved task · MadLibsStoryMachine
/// Lesson: 3.3 Let the player shape the story — 03-stories.html
///
/// Original assignment (this version is complete):
///
/// Give the machine a name, a creature, a place and an object, then read the absurd story it builds from
/// them. Multiword answers can make the result much stranger than single words.
///
/// **Your mission · Write a story title:** Clean outside spaces in cleanWord without damaging phrases.
/// Then implement
/// headline: for name "  Red Fox  " it must return exactly "Report from Red Fox:". The program will
/// print your title before its story; another name must produce another title.
///
/// Run the program and answer the name prompt with spaces around Red Fox, then supply the other
/// three phrases and inspect the finished story.

String cleanWord(String word) {
    return word.strip();
}

String headline(String name) {
    String cleaned = cleanWord(name);
    return "Report from " + cleaned + ":";
}

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Rewrite the three prepared story sentences while keeping all four supplied phrases useful. Make a
/// news report, a detective ending or a ridiculous advert. Ask a friend for the four inputs without
/// revealing your new template first.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
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
