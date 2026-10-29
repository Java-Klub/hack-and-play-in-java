/// # Úkol · 5.3 spočítej lup a přiděl hodnost
/// Lekce: 03-results.html

void main() {
    int coins = Integer.parseInt(IO.readln("Coins: ").strip());
    int gems = Integer.parseInt(IO.readln("Gems: ").strip());
    int points = score(coins, gems);
    IO.println("Score: " + points);
    IO.println("Rank: " + rank(points));
}

int score(int coins, int gems) {
    return 0;
}

String rank(int points) {
    return "Rookie";
}
