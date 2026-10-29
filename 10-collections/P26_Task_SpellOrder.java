/// # Vyřešený úkol · 10.4 rozhodni i při stejné délce
/// Lekce: 04-ordering.html

void main() {
    List<String> spells = new ArrayList<>(List.of("owl", "ant", "eagle", "bee", "a"));
    spells.sort((left, right) -> compareSpells(left, right));
    IO.println(spells);
    IO.println("Tie: " + (compareSpells("bee", "ant") > 0));
    IO.println("Same: " + compareSpells("owl", "owl"));
}

int compareSpells(String left, String right) {
    int byLength = Integer.compare(left.length(), right.length());
    if (byLength != 0) {
        return byLength;
    }
    return left.compareTo(right);
}
