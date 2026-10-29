/// # Úkol · 10.3 sčítej týmová skóre
/// Lekce: 03-maps.html

void main() {
    Map<String, Integer> scores = new HashMap<>();
    while (true) {
        String name = IO.readln("Team (blank ends): ").strip();
        if (name.isEmpty()) {
            break;
        }
        int points = Integer.parseInt(IO.readln("Points: ").strip());
        award(scores, name, points);
    }
    String wanted = IO.readln("Find team: ").strip();
    IO.println("Score: " + scores.getOrDefault(wanted, 0));
    IO.println("Teams: " + scores.size());
}

void award(Map<String, Integer> scores, String name, int points) {
    scores.put(name, points);
}
