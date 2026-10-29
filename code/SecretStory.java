void main() {
    String name = IO.readln("Name: ");
    String item = IO.readln("Item: ");
    String story = "NAME steals ITEM. NAME escapes.";
    story = story.replace("NAME", name);
    story = story.replace("ITEM", item);
    IO.println(story);
}
