void main() {
    int food = Integer.parseInt(IO.readln("Food for Dot: ").strip());
    PetBot first = new PetBot("Dot", 2);
    PetBot second = new PetBot("Dash", 6);
    first.feed(food);
    IO.println(first.name + ": " + first.energy);
    IO.println(second.name + ": " + second.energy);
}

class PetBot {
    String name;
    int energy;

    PetBot(String name, int energy) {
        this.name = "Unknown";
        this.energy = 0;
    }

    void feed(int amount) {
        energy = amount;
    }
}
