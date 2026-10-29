/// # Worked example · 9.1 Give related data a shape
/// Lesson: 01-records.html

void main() {
    Parcel parcel = new Parcel("Moon rock", 4);
    IO.println("Item: " + parcel.name());
    IO.println("Weight: " + parcel.weight());
}

record Parcel(String name, int weight) {
}
