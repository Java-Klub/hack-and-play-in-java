void main() {
    Map<String, Integer> counts = new HashMap<>();
    String[] signals = {"Moon", "Sun", "Moon"};
    for (String signal : signals) {
        int previous = counts.getOrDefault(signal, 0);
        counts.put(signal, previous + 1);
    }
    IO.println("Moon: " + counts.getOrDefault("Moon", 0));
    IO.println("Sun: " + counts.getOrDefault("Sun", 0));
    IO.println("Star: " + counts.getOrDefault("Star", 0));
    IO.println("Names: " + counts.size());
}
