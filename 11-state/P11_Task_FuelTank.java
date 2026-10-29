/// # Task · 11.3 protect both ends of the fuel gauge
/// Lesson: 03-invariants.html

void main() {
    int added = Integer.parseInt(IO.readln("Refill: ").strip());
    int cost = Integer.parseInt(IO.readln("Travel cost: ").strip());
    Tank tank = new Tank();
    IO.println("Refilled: " + tank.refill(added));
    IO.println("Fuel: " + tank.fuel());
    IO.println("Travelled: " + tank.travel(cost));
    IO.println("Fuel: " + tank.fuel());
}

class Tank {
    private int fuel = 5;

    int fuel() {
        return fuel;
    }

    boolean refill(int amount) {
        fuel = fuel + amount;
        return true;
    }

    boolean travel(int cost) {
        fuel = fuel - cost;
        return true;
    }
}
