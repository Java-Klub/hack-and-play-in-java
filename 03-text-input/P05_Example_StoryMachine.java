/// # Worked example · 3.3 Let the player shape the story
/// Lesson: 03-stories.html

void main() {
    String pilot = IO.readln("Pilot: ");
    String place = IO.readln("Place: ");
    String story = "PLAYER shouts \"Go!\" in PLACE.\nPLAYER runs.";
    story = story.replace("PLAYER", pilot);
    story = story.replace("PLACE", place);
    IO.println(story);
}
