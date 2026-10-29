/// # Úkol · 3.3 příběh zapomněl jméno i lup
/// Lekce: 03-stories.html

void main() {
    String name = IO.readln("Name: ");
    String item = IO.readln("Item: ");
    String story = "NAME steals ITEM. NAME escapes.";
    story.replace("NAME", name);
    IO.println(story);
}
