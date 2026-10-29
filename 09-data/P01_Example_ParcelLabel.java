/// # Hotový příklad · 9.1 Dej údajům společný tvar
/// Lekce: 01-records.html

void main() {
    Parcel parcel = new Parcel("Moon rock", 4);
    IO.println("Item: " + parcel.name());
    IO.println("Weight: " + parcel.weight());
}

record Parcel(String name, int weight) {
}
