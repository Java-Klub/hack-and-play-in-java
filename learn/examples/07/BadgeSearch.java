void main() {
    String[] badges = {"OWL", "FOX", "OWL"};
    String wanted = IO.readln("Badge: ");
    int found = -1;
    for (int index = 0; index < badges.length; index++) {
        if (badges[index].equals(wanted)) {
            found = index;
            break;
        }
    }
    if (found == -1) {
        IO.println("Missing");
    } else {
        IO.println("Index: " + found);
    }
}
