void main() {
    int[] route = {1, 2, 3};
    int[] shared = route;
    int[] saved = route.clone();
    shared[0] = 9;
    IO.println("Route: " + Arrays.toString(route));
    IO.println("Saved: " + Arrays.toString(saved));
}
