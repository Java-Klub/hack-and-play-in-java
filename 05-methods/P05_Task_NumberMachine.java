/// # Solved task · NumberMachine
/// Lesson: 5.2 Send values to a method — 02-parameters.html
///
/// Original assignment (this version is complete):
///
/// **Your mission · Reconstruct a black box:** A small machine and your copy receive the same number.
/// Infer a rule from the printed pairs, add at least one revealing compare call, and make your copy
/// agree for every integer from -20 to 20. Keep the reference machine unchanged. The source is
/// available: predict a new case before reading its reference formula.

// <editor-fold defaultstate="expanded" desc="Small program · explore">
void main() {
    IO.println("NUMBER MACHINE: input -> machine / your copy");
    compare(0);
    compare(1);
    compare(2);
    compare(-2);
    compare(5);
}

void compare(int number) {
    IO.println(number + " -> " + machine(number) + " / " + imitation(number));
}

int imitation(int number) {
    return number * number + 1;
}

// <editor-fold defaultstate="collapsed" desc="Reference machine · after predicting">
int machine(int number) {
    return number * number + 1;
}
// </editor-fold>
// </editor-fold>
