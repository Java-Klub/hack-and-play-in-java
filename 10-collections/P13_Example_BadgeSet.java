/// # Hotový příklad · 10.2 Pamatuj si první návštěvu
/// Lekce: 02-sets.html

void main() {
    Set<String> badges = new HashSet<>();
    IO.println("First Moon: " + badges.add("Moon"));
    IO.println("Second Moon: " + badges.add("Moon"));
    badges.add("Sun");
    IO.println("Has Sun: " + badges.contains("Sun"));
    IO.println("Unique badges: " + badges.size());
}
