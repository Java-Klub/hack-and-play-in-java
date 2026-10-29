/// # Vyřešený úkol · 3.3 příběh zapomněl jméno i lup
/// Lekce: 03-stories.html

void main() {
    String name = IO.readln("Name: ");
    String item = IO.readln("Item: ");
    String story = "NAME steals ITEM. NAME escapes.";
    story = story.replace("NAME", name);
    story = story.replace("ITEM", item);
    IO.println(story);
}
