void main() {
    String name = IO.readln("Cargo: ");
    int count = Integer.parseInt(IO.readln("Count: ").strip());
    int unitWeight = Integer.parseInt(IO.readln("Unit weight: ").strip());
    Cargo cargo = new Cargo(name, count, unitWeight);
    IO.println(cargo.name() + ": " + cargo.count() + " pieces");
    IO.println("Total weight: " + cargo.count() * cargo.unitWeight());
}

record Cargo(String name, int count, int unitWeight) {
}
