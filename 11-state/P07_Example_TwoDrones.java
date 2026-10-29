/// # Hotový příklad · 11.2 Každý robot má vlastní baterii
/// Lekce: 02-instances.html

void main() {
    Drone first = new Drone("Dot", 5);
    Drone second = new Drone("Dash", 8);
    first.move();
    IO.println(first.name + ": " + first.charge);
    IO.println(second.name + ": " + second.charge);
}

class Drone {
    String name;
    int charge;

    Drone(String name, int charge) {
        this.name = name;
        this.charge = charge;
    }

    void move() {
        charge = charge - 1;
    }
}
