/// # Solved task · 5.3 count the loot and award a rank
/// Lesson: 03-results.html

void main() {
    int coins = Integer.parseInt(IO.readln("Coins: ").strip());
    int gems = Integer.parseInt(IO.readln("Gems: ").strip());
    int points = score(coins, gems);
    IO.println("Score: " + points);
    IO.println("Rank: " + rank(points));
}

int score(int coins, int gems) {
    return coins + gems * 5;
}

String rank(int points) {
    if (points >= 20) {
        return "Master";
    }
    return "Rookie";
}
