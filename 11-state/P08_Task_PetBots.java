/// # Vyřešený úkol · 11.2 nakrm jen vybraného robota
/// Lekce: 02-instances.html

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
        this.name = name;
        this.energy = energy;
    }

    void feed(int amount) {
        energy = energy + amount;
    }
}
