/// # Hotový příklad · 10.4 Předej pravidlo řazení
/// Lekce: 04-ordering.html

void main() {
    List<String> names = new ArrayList<>(List.of("owl", "a", "bear"));
    Comparator<String> byLength = (left, right) ->
        Integer.compare(left.length(), right.length());
    names.sort(byLength);
    IO.println(names);
}
