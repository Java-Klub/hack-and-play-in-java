void main() {
    List<String> names = new ArrayList<>(List.of("owl", "a", "bear"));
    Comparator<String> byLength = (left, right) ->
        Integer.compare(left.length(), right.length());
    names.sort(byLength);
    IO.println(names);
}
