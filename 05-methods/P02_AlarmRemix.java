void main() {
    alarm();
    IO.println("Door A locked");
    alarm();
    IO.println("Door B locked");
}

void alarm() {
    IO.println("Intruder!");
    IO.println("Lights red");
}
