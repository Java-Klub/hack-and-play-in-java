/// # Solved task · 5.4 the med kit did not heal anything
/// Lesson: 04-local-values.html

void main() {
    int health = Integer.parseInt(IO.readln("Health: ").strip());
    health = heal(health);
    IO.println("Health after kit: " + health);
}

int heal(int health) {
    health = health + 3;
    if (health > 10) {
        health = 10;
    }
    return health;
}
