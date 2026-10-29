/// # Worked example · 6.4 Stop at the first match
/// Lesson: 04-search.html

void main() {
    int limit = Integer.parseInt(IO.readln("Limit: ").strip());
    int found = -1;
    for (int moment = 1; moment <= limit; moment++) {
        if (moment % 4 == 0) {
            found = moment;
            break;
        }
    }
    if (found == -1) {
        IO.println("None");
    } else {
        IO.println("Signal: " + found);
    }
}
