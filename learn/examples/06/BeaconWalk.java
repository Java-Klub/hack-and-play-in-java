void main() {
    int count = Integer.parseInt(IO.readln("Count: ").strip());
    for (int index = 0; index < count; index++) {
        IO.println("Beacon " + index);
    }
    IO.println("Done");
}
