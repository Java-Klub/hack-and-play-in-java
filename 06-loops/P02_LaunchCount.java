void main() {
    int remaining = Integer.parseInt(IO.readln("Start: ").strip());
    while (remaining > 0) {
        IO.println(remaining);
        remaining = remaining - 1;
    }
    IO.println("Launch");
}
