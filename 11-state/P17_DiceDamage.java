void main() {
    Random random = new Random();
    int die = roll(random);
    IO.println("Roll: " + die);
    IO.println("Damage: " + damage(die));
}

int roll(Random random) {
    return random.nextInt(1, 7);
}

int damage(int die) {
    if (die <= 2) {
        return 0;
    }
    return die - 2;
}
