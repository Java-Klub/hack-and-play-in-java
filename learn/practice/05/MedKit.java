void main() {
    int health = Integer.parseInt(IO.readln("Health: ").strip());
    heal(health);
    IO.println("Health after kit: " + health);
}

int heal(int health) {
    health = health + 3;
    return health;
}
