/// # Vyřešený úkol · 6.4 první měsíční okno
/// Lekce: 04-search.html

void main() {
    int start = Integer.parseInt(IO.readln("Start: ").strip());
    int end = Integer.parseInt(IO.readln("End: ").strip());
    int found = -1;
    for (int moment = start; moment <= end; moment++) {
        if (moment % 3 == 0 && moment % 2 != 0) {
            found = moment;
            break;
        }
    }
    if (found == -1) {
        IO.println("None");
    } else {
        IO.println("Window: " + found);
    }
}
