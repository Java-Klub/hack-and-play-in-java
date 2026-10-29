void main() {
    int count = Integer.parseInt(IO.readln("Potions: ").strip());
    int tokens = price(count);
    IO.println("Tokens needed: " + tokens);
    IO.println("One extra potion: " + price(count + 1));
}

int price(int count) {
    return count * 3;
}
