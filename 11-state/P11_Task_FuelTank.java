/// # Vyřešený úkol · 11.3 ochraň oba konce palivoměru
/// Lekce: 03-invariants.html

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
        if (amount <= 0 || amount > 10 - fuel) {
            return false;
        }
        fuel = fuel + amount;
        return true;
    }

    boolean travel(int cost) {
        if (cost <= 0 || cost > fuel) {
            return false;
        }
        fuel = fuel - cost;
        return true;
    }
}
